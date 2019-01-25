package cn.com.startai.socket.sign.scm.impl;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.startai.socket.R;
import cn.com.startai.socket.db.gen.CountElectricityDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.IRegDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.CountElectricity;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.AbsSocketScm;
import cn.com.startai.socket.sign.scm.bean.CostRate;
import cn.com.startai.socket.sign.scm.bean.CountdownData;
import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.PointReport;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.startai.socket.sign.scm.bean.sensor.SensorData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Humidity;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.TempHumidityData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Temperature;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.receivetask.ProtocolTaskImpl;
import cn.com.startai.socket.sign.scm.util.MySocketDataCache;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.util.Bit;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.ProtocolProcessorFactory;
import cn.com.swain.support.protocolEngine.pack.ComModel;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.resolve.AbsProtocolProcessor;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SocketScmManager extends AbsSocketScm
        implements IService, IDataProtocolOutput,
        OnTaskCallBack, IRegDebugerProtocolStream,
        ScmDevice.OnScmCallBack {

    public static final String TAG = "SocketScmManager";

    private Application app;

    public SocketScmManager(Application app) {
        this.app = app;
    }

    private IScmResultCallBack mScmResultCallBack;

    @Override
    public void regIScmResultCallBack(IScmResultCallBack mScmResultCallBack) {
        this.mScmResultCallBack = mScmResultCallBack;
    }

    @Override
    public void onNetworkChange() {
        if (mScmDeviceUtils != null) {
            mScmDeviceUtils.onNetworkChange();
        }
    }

    private IDataProtocolOutput mResponse;

    @Override
    public void regIProtocolOutput(IDataProtocolOutput mResponse) {
        this.mResponse = mResponse;
    }

    @Override
    public void onOutputDataToServer(ResponseData mResponseData) {

        if (mResponseData.getRepeatMsgModel().isNeedRepeatSend()) {
            mScmDeviceUtils.getScmDevice(mResponseData.toID).recordSendMsg(mResponseData, 1000 * 3L);
        }

        if (Debuger.isLogDebug) {
            Tlog.w(TAG, " onOutputDataToServer :" + String.valueOf(mResponseData));
        }

        if (mResponse != null) {
            mResponse.onOutputDataToServer(mResponseData);
        }
    }

    @Override
    public void onBroadcastDataToServer(ResponseData mResponseData) {
        if (mResponse != null) {
            mResponse.onBroadcastDataToServer(mResponseData);
        }
    }

    /**************/

    private AbsProtocolProcessor pm;

    private ScmDeviceUtils mScmDeviceUtils;

    @Override
    public void onSCreate() {
        Tlog.v(TAG, " SocketScmManager onSCreate()");

        MySocketDataCache.BuildParams mParams = new MySocketDataCache.BuildParams();
        mParams.setCustom(CustomManager.getInstance().getCustom());
        mParams.setProduct(CustomManager.getInstance().getProduct());
        mParams.setProtocolVersion(CustomManager.getInstance().getProtocolVersion());
        mParams.setVirtualScm(this);

        MySocketDataCache.getInstance().init(mParams);
        MySocketDataCache.getInstance().onSCreate();

        mScmDeviceUtils = new ScmDeviceUtils(this, this);


        pm = ProtocolProcessorFactory.newMultiChannelSingleTask(LooperManager.getInstance().getProtocolLooper(),
                new ProtocolTaskImpl(this, this, app),
                CustomManager.getInstance().getProtocolVersion(), true);

    }

    @Override
    public void onSResume() {
        Tlog.v(TAG, " SocketScmManager onSResume()");
        MySocketDataCache.getInstance().onSResume();
    }

    @Override
    public void onSPause() {
        Tlog.v(TAG, " SocketScmManager onSPause()");
        MySocketDataCache.getInstance().onSPause();
    }

    @Override
    public void onSDestroy() {
        Tlog.v(TAG, " SocketScmManager onSDestroy()");

        MySocketDataCache.getInstance().onSDestroy();

        if (pm != null) {
            pm.release();
            pm = null;
        }

        mScmDeviceUtils.cleanMap();
    }

    @Override
    public void onSFinish() {
        Tlog.v(TAG, " SocketScmManager onSFinish()");
        MySocketDataCache.getInstance().onSFinish();
    }

    @Override
    public void onInputServerData(ReceivesData mReceivesData) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " SocketScmManager onInputServerData() " + String.valueOf(mReceivesData));
        }

        if (pm != null) {
            pm.onInputServerData(mReceivesData);
        } else {
            Tlog.e(TAG, " SocketScmManager onInputServerData() pm=null ");
        }

    }

    @Override
    public void onStartSendHeartbeat(ScmDevice scmDevice) {

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, scmDevice.getAddress() + " onStartSendHeartbeat :"
                    + Integer.toHexString(scmDevice.getToken()));
        }

    }

    @Override
    public void onHeartbeatLose(String mac, int diff) {

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " onHeartbeatLose " + mac + " diff:" + diff);
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultHeartbeatLose(mac, diff);
        }
    }


    @Override
    public void onHeartbeatResult(String mac, boolean result) {
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, mac + " onHeartbeatReceive ... ");
        }
        mScmDeviceUtils.getScmDevice(mac).onReceiveHeartbeat(result);

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultHeartbeatReceive(mac, result);
        }
    }


    /************/

    @Override
    public void onConnected(String address) {
        Tlog.v(TAG, " onConnected : " + address);

        mScmDeviceUtils.getScmDevice(address).connected();
        if (Debuger.isLogDebug) {
            mScmDeviceUtils.showConnectDevice();
        }

        queryScmTime(address);
        setScmTimezone(address);

        if (productDetectionManager != null) {
            productDetectionManager.connected(address);
        }

    }

    @Override
    public void onDisconnected(String address) {
        Tlog.v(TAG, " onDisconnected : " + address);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(address);
        scmDevice.disconnected();

        // 蓝牙连接断开后sensor可能改变，所以清空数据
        if (CustomManager.getInstance().isTriggerBle()) {
            scmDevice.clearSensor();
        }

        if (productDetectionManager != null) {
            productDetectionManager.disconnected(address);
        }

    }

    /************/


    @Override
    public void queryUSBState(String mac) {

        ResponseData mResponseData = MySocketDataCache.getQueryUSBState(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryUSBState " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setUSBState(String mac, boolean state) {

        ResponseData mResponseData = MySocketDataCache.getSwitchUSB(mac, state);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setUSBState " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public int getScmToken(String mac) {
        int token = mScmDeviceUtils.getScmDevice(mac).getToken();
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " getScmToken mac:" + mac + " " + token);
        }
        if (token == -1 || token == 0) {
            if (mScmResultCallBack != null) {
                token = mScmResultCallBack.getTokenFromDB(mac);
                if (Debuger.isLogDebug) {
                    Tlog.d(TAG, " getScmToken From DB. mac:" + mac + " " + token);
                }
            }
        }

        return token;
    }

    @Override
    public void setTemperatureTimingAlarm(TimingTempHumiData mTimingAdvanceData) {

//            (byte id, byte startHour, byte startMinute, byte stopHour, byte stopMinute, boolean startup, byte onIntervalHour,
// byte onIntervalMinute, byte offIntervalHour, byte offIntervalMinute, byte startup) {
        ResponseData mResponseData = MySocketDataCache.getSetTimingTemp(mTimingAdvanceData.mac, mTimingAdvanceData.id,
                mTimingAdvanceData.state,
                (byte) mTimingAdvanceData.startHour, (byte) mTimingAdvanceData.startMinute,
                (byte) mTimingAdvanceData.endHour, (byte) mTimingAdvanceData.endMinute, mTimingAdvanceData.on,
                (byte) mTimingAdvanceData.onIntervalHour, (byte) mTimingAdvanceData.onIntervalMinute,
                (byte) mTimingAdvanceData.offIntervalHour, (byte) mTimingAdvanceData.offIntervalMinute,
                mTimingAdvanceData.startup, (byte) mTimingAdvanceData.week,
                (byte) mTimingAdvanceData.alarmValue, mTimingAdvanceData.model);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setTemperatureTimingAlarm data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void queryTemperatureTimingAlarm(String mac, int model) {

        ResponseData mResponseData = MySocketDataCache.getQueryTimingTemp(mac, model);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTemperatureTimingAlarm data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void queryYellowLightRGB(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryYellowLight(mac, 1);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryYellowLightRGB : " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryColourLampRGB(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryColorLamp(mac, 1);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryColourLampRGB : " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void quickControlRelay(String mac, boolean on) {
        ResponseData mResponseData = MySocketDataCache.getQuickSetRelaySwitch(mac, on);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " quickControlRelay status : " + on + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void quickQueryRelay(String mac) {

        ResponseData mResponseData = MySocketDataCache.getQuickQueryRelayStatus(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " quickQueryRelay " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public synchronized void queryHistoryCount(QueryHistoryCount mQueryCount) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mQueryCount.mac);
        scmDevice.removeQueryHistory();

        QueryHistoryUtil.queryHistoryCount(mQueryCount, scmDevice);
        if (Debuger.isDebug) {
            testReport(mQueryCount.mac);
        }
    }


    @Override
    public void onDelaySend(int what, Object obj) {
        if (what == 0) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onQueryHistoryCountResult(true, (QueryHistoryCount) obj);
            }
        } else if (what == 1) {
            queryHistoryCount((QueryHistoryCount) obj);
        } else if (what == 2) {
            onOutputDataToServer((ResponseData) obj);
        }

    }

    @Override
    public void onQueryHistoryCountResult(boolean result, QueryHistoryCount mCount) {

        if (!result) {

            Tlog.e(TAG, " onQueryHistoryCountResult fail..");
            return;
        }

        if (mCount == null) {

            Tlog.e(TAG, " onQueryHistoryCountResult QueryHistoryCount=null..");
            return;
        }

        QueryHistoryUtil.updateHistory(mCount);

        QueryHistoryUtil.deleteOldHistory(mCount.mac);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mCount.mac);
        QueryHistoryCount queryCount = scmDevice.getQueryCount();
        if (queryCount != null && queryCount.msgSeq == mCount.msgSeq
//                &&SocketSecureKey.Util.isIntervalMinute((byte) queryCount.interval)
                ) {

            Tlog.e(TAG, " queryHistoryCount again:");
            queryCount.needQueryFromServer = false;
            scmDevice.removeQueryHistory();
            scmDevice.removeQueryHistoryCountResult();
            scmDevice.sendQueryHistory(1500, queryCount);

        }

    }


    private boolean testReportRun;
    private String testMac;
    private Handler mRepeatHandler;

    private void testReport(String mac) {
        testMac = mac;

        if (Debuger.isTest && !testReportRun) {
            testReportRun = true;

            if (mRepeatHandler == null) {
                mRepeatHandler = new Handler(LooperManager.getInstance().getWorkLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        repeatSend();
                    }
                };
            }

            repeatSend();

        }
    }

    private void repeatSend() {
        PointReport mPointReport = new PointReport();
        mPointReport.mac = testMac;
        mPointReport.ts = System.currentTimeMillis();
        mPointReport.electricity = (float) ((Math.random() * 9 + 1) * 1000);

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onElectricityReportResult(true, mPointReport);
        }
        if (mRepeatHandler != null) {
            mRepeatHandler.sendEmptyMessageDelayed(0, 1000 * 60 * 5);
        }
    }


    @Override
    public void onElectricityReportResult(boolean result, PointReport mElectricity) {

        if (!result) {
            Tlog.e(TAG, " onElectricityReportResult fail ");
            return;
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onElectricityReportResult(result, mElectricity);
        }

        CountElectricityDao countElectricityDao =
                DBManager.getInstance().getDaoSession().getCountElectricityDao();

        long startTimeOriginal = mElectricity.ts;
        long startTime = DateUtils.fastFormatTsToDayTs(startTimeOriginal); // startTime  yyyy/mm//dd

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

        Tlog.e(TAG, " onElectricityReportResult startTime:"
                + startTime
                + " " + mFormat.format(new Date(startTime))
                + " " + mElectricity.ts
                + " " + mFormat.format(new Date(mElectricity.ts)));

        List<CountElectricity> list = countElectricityDao.queryBuilder()
                .where(CountElectricityDao.Properties.Mac.eq(mElectricity.mac),
                        CountElectricityDao.Properties.Timestamp.eq(startTime)).list();

        CountElectricity countElectricity = null;
        if (list.size() > 0) {
            countElectricity = list.get(0);
        }

        if (countElectricity == null) {
            countElectricity = new CountElectricity();
        }
        countElectricity.setMac(mElectricity.mac);
        byte[] electricity = countElectricity.getElectricity();
        int oneDayBytes = CountElectricity.ONE_DAY_BYTES; // 一天数据长度
        if (electricity == null) {
            electricity = new byte[oneDayBytes];
        } else {
            if (electricity.length < oneDayBytes) {
                byte[] cache = new byte[oneDayBytes];
                System.arraycopy(electricity, 0, cache, 0, electricity.length);
                electricity = cache;
            }
        }

        countElectricity.setElectricity(electricity);
        countElectricity.setTimestamp(startTime);

        int minuteOfDay = DateUtils.getMinuteOfDay(startTimeOriginal, 5);
        int index = (minuteOfDay / 5 - 1);
        Tlog.e(TAG, " point report insert buf index:" + index);

        if (index < 0) {

            List<CountElectricity> listL = countElectricityDao.queryBuilder()
                    .where(CountElectricityDao.Properties.Mac.eq(mElectricity.mac),
                            CountElectricityDao.Properties.Timestamp.eq(startTime - DateUtils.ONE_DAY)).list();

            if (listL.size() > 0) {
                CountElectricity countElectricity1 = listL.get(0);

                byte[] electricityLast = countElectricity1.getElectricity();

                if (electricityLast != null) {

                    int point = (60 * 24 / 5 - 1) * 8;

                    if (electricityLast.length >= (point + 4)) {
                        electricityLast[point] = mElectricity.data[0];
                        electricityLast[point + 1] = mElectricity.data[1];
                        electricityLast[point + 2] = mElectricity.data[2];
                        electricityLast[point + 3] = mElectricity.data[3];

                        countElectricity1.setElectricity(electricityLast);
                        countElectricityDao.update(countElectricity1);

                    }

                }
            }

        } else {
            int point = index * 8;
            if (point <= electricity.length && mElectricity.data != null) {
                electricity[point] = mElectricity.data[0];
                electricity[point + 1] = mElectricity.data[1];
                electricity[point + 2] = mElectricity.data[2];
                electricity[point + 3] = mElectricity.data[3];
            }

            if (countElectricity.getId() == null) {
                long insertIndex = countElectricityDao.insert(countElectricity);
                Tlog.v(TAG, " PointCount insert:" + insertIndex);
            } else {
                countElectricityDao.update(countElectricity);
                Tlog.v(TAG, " update insert:" + countElectricity.getId());
            }

        }

    }


    @Override
    public void queryCostRate(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryConstRate(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCostRate " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryCumuParam(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryCumuParam(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCumuParam " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryVersion(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryVersion(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryVersion " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void querySSID(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQuerySSID(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " querySSID " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void update(String mac) {
        ResponseData mResponseData = MySocketDataCache.getUpdate(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " update " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setColorLamp(String mac, int i, int r, int g, int b) {
        ResponseData mResponseData = MySocketDataCache.getSetColorLamp(mac, i, r, g, b);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setColorLamp " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setLightRGB(ColorLampRGB obj) {

        ResponseData mResponseData = MySocketDataCache.getSetLightColor(obj.mac, obj.seq, obj.r, obj.g, obj.b, obj.model);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setLightRGB " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }


    @Override
    public void lanComModel(boolean result, String mac) {
        Tlog.v(TAG, " lanComModel()  : " + result + " mac:" + mac);

        ScmDevice scmData = mScmDeviceUtils.containScmDevice(mac);

        if (scmData == null) {
            Tlog.e(TAG, " lanComMode()  this.address == null ");
            return;
        }

        if (result) {
            scmData.starHeartbeat();
        } else {
            scmData.stopHeartbeat();
        }

    }

    @Override
    public void publishSensorData(String mac, boolean publish) {
        Tlog.v(TAG, " publishSensorData publish : " + publish);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

        scmDevice.setIsPublish(publish);

        // H5 请求数据，把上次的数据先发送一份给H5;
        if (publish) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultPublishSensorData(mac, scmDevice.getSensorData());
            }
        }

    }

    @Override
    public void switchRelay(String mac, boolean status) {

        ResponseData mResponseData = MySocketDataCache.getSetRelaySwitch(mac, status);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " SwitchRelay " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void switchFlash(String mac, boolean status) {

        ResponseData mResponseData = MySocketDataCache.getSwitchFlash(mac, status);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " switchFlash " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void queryRelayState(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryRelayStatus(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " QueryRelayState " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryFlashState(String mac) {

        ResponseData mResponseData = MySocketDataCache.getQueryFlashState(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryFlashState " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setPowerCountdown(PowerCountdown powerCountdown) {
        ResponseData mResponseData = MySocketDataCache.getSetCountdown(powerCountdown.getMac(), powerCountdown.getStatus(),
                powerCountdown.getSwitchGear(), powerCountdown.getHour(), powerCountdown.getMinute());
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setPowerCountdown data:" + String.valueOf(mResponseData));
        }

        onOutputDataToServer(mResponseData);

    }

    @Override
    public void setCommonTiming(TimingCommonData mTimingCommonData) {
        ResponseData mResponseData = MySocketDataCache.getSetCommonTiming(mTimingCommonData.getMac(),
                mTimingCommonData.getId(), mTimingCommonData.getState(), mTimingCommonData.isOn(),
                mTimingCommonData.getWeek(), mTimingCommonData.getHour(), mTimingCommonData.getMinute(),
                mTimingCommonData.getStartup());
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, "setCommonTiming data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void setAdvanceTiming(TimingAdvanceData mTimingAdvanceData) {
        ResponseData mResponseData = MySocketDataCache.getSetAdvanceTiming(mTimingAdvanceData.mac, mTimingAdvanceData.id,
                mTimingAdvanceData.state,
                (byte) mTimingAdvanceData.startHour, (byte) mTimingAdvanceData.startMinute,
                (byte) mTimingAdvanceData.endHour, (byte) mTimingAdvanceData.endMinute, mTimingAdvanceData.on,
                (byte) mTimingAdvanceData.onIntervalHour, (byte) mTimingAdvanceData.onIntervalMinute,
                (byte) mTimingAdvanceData.offIntervalHour, (byte) mTimingAdvanceData.offIntervalMinute,
                mTimingAdvanceData.startup, (byte) mTimingAdvanceData.week);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, "setAdvanceTiming:" + String.valueOf(mTimingAdvanceData));
            Tlog.v(TAG, " setAdvanceTiming data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void bindDevice(LanBindInfo mLanBindInfo, String userID) {
        Tlog.v(TAG, " bindDevice  " + mLanBindInfo.mac + " userID:" + userID);
        byte[] bytes = userID != null ? userID.getBytes() : null;
        // bit0=1 绑定
        // bit1=1 真实userID
        int info = new Bit().add(0).reserve(1, (bytes != null && bytes.length > 0)).getDevice();
        byte[] pwdBuf = mLanBindInfo.pwd != null ? mLanBindInfo.pwd.getBytes() : null;
        ResponseData mResponseData = MySocketDataCache.getBindDevice(mLanBindInfo.mac, bytes, (byte) info, pwdBuf);
        mResponseData.getRepeatMsgModel().setMaxRepeatTimes(3);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " bindDevice data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void unbindDevice(String mac, String userID) {
        Tlog.v(TAG, " unbindDevice  " + mac + " userID:" + userID);
        byte[] bytes = userID != null ? userID.getBytes() : null;
        // bit0=0 解除绑定
        // bit1=1 真实userID
        int info = new Bit().remove(0).reserve(1, (bytes != null && bytes.length > 0)).getDevice();
        ResponseData mResponseData = MySocketDataCache.getBindDevice(mac, bytes, (byte) info, null);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " unbindDevice data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void requestToken(String mac, String userID) {
        int random = (int) ((Math.random() * 9 + 1) * 100000);
        Tlog.v(TAG, " requestToken  " + userID + " random:" + random);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        scmDevice.putRequestTokenRandom(random);

        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = MySocketDataCache.getRequestToken(mac, bytes, random);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " requestToken data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void controlDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " controlDevice  " + userID + " token:" + Integer.toHexString(token));

        mScmDeviceUtils.getScmDevice(mac).setToken(token);
        MySocketDataCache.putToken(mac, token);

        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = MySocketDataCache.getControlDevice(mac, bytes, token);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " controlDevice data: " + String.valueOf(mResponseData));
        }

        onOutputDataToServer(mResponseData);
    }

    @Override
    public void appSleep(String mac, String userID, int token) {
        Tlog.v(TAG, " appSleep userID:" + userID + " token:" + token);

        if (userID != null) {
            ResponseData mResponseData = MySocketDataCache.getAppSleep(mac, userID.getBytes(), token);

            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " appSleep data: " + String.valueOf(mResponseData));
            }

            onOutputDataToServer(mResponseData);
        } else {
            Tlog.e(TAG, " appSleep userID=null ");
        }
    }

    @Override
    public void disconnectDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " disconnectDevice userID:" + userID + " token:" + token);
        if (userID != null) {
            ResponseData mResponseData = MySocketDataCache.getDisconnectDevice(mac, userID.getBytes(), token);
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " disconnectDevice data: " + String.valueOf(mResponseData));
            }
            onOutputDataToServer(mResponseData);
        } else {
            Tlog.e(TAG, " disconnectDevice userID=null ");
        }
    }


    @Override
    public void setTempHumidityAlarm(TempHumidityAlarmData mAlarm) {
        ResponseData mResponseData;
        int model;
        if (mAlarm.isTemperatureType()) {
            model = SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE;
        } else {
            model = SocketSecureKey.Model.ALARM_MODEL_HUMIDITY;
        }
        mResponseData = MySocketDataCache.getSetTempHumidityAlarm(mAlarm.getMac(), mAlarm.isStartup(),
                model, mAlarm.getAlarmValue(), mAlarm.getAlarmValueDeci(), mAlarm.isLimitUp());

        if (mAlarm.isTemperatureType()) {
            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mAlarm.getMac());


            if (mAlarm.isLimitUp()) {
                scmDevice.setTempHotAlarmData(mAlarm.getOriginalAlarmValue());

            } else if (mAlarm.isLimitDown()) {

                scmDevice.setTempCodeAlarmData(mAlarm.getOriginalAlarmValue());
            }

        } else if (mAlarm.isHumidityType()) {
            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mAlarm.getMac());


            if (mAlarm.isLimitUp()) {
                scmDevice.setHumiHotAlarmData(mAlarm.getOriginalAlarmValue());

            } else if (mAlarm.isLimitDown()) {

                scmDevice.setHumiCodeAlarmData(mAlarm.getOriginalAlarmValue());
            }
        }


        if (Debuger.isLogDebug) {

            Tlog.v(TAG, " setTempHumidityAlarm  alarm:" + String.valueOf(mAlarm));

            Tlog.v(TAG, " setTempHumidityAlarm  data: " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTempHumidityData(String mac) {
        ResponseData mResponseTempDataUp = MySocketDataCache.getQueryTemperatureLimitUp(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTempDataUp: " + String.valueOf(mResponseTempDataUp));
        }
        onOutputDataToServer(mResponseTempDataUp);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseTempDataDown = MySocketDataCache.getQueryTemperatureLimitDown(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTempDataDown: " + String.valueOf(mResponseTempDataDown));
        }

        onOutputDataToServer(mResponseTempDataDown);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseHumiDataUp = MySocketDataCache.getQueryHumidityLimitUp(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryHumiDataUp: " + String.valueOf(mResponseHumiDataUp));
        }
        onOutputDataToServer(mResponseHumiDataUp);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseHumiDataDown = MySocketDataCache.getQueryHumidityLimitDown(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryHumiDataDown: " + String.valueOf(mResponseHumiDataDown));
        }
        onOutputDataToServer(mResponseHumiDataDown);
    }

    @Override
    public void queryCountdownData(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryCountdown(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCountdownData  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTimingData(String mac) {
        queryComTimingListData(mac);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        queryAdvTimingListData(mac);

    }

    @Override
    public void queryComTimingListData(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryCommonTimingList(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCommonTimingData  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryAdvTimingListData(String mac) {

        ResponseData mResponseData = MySocketDataCache.getQueryAdvanceTimingList(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryAdvanceTimingData  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setNightLightTiming(NightLightTiming nightLightTiming) {

        if (nightLightTiming != null) {
            ResponseData mResponseData = MySocketDataCache.getSetTimingNightLight(nightLightTiming.mac,
                    nightLightTiming.startup,
                    (byte) nightLightTiming.getStartHour(), (byte) nightLightTiming.getStartMinute(),
                    (byte) nightLightTiming.getStopHour(), (byte) nightLightTiming.getStopMinute());
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " setNightLightTiming  data:" + String.valueOf(mResponseData));
            }
            onOutputDataToServer(mResponseData);
        }
    }

    @Override
    public void setNightLightWisdom(NightLightTiming nightLightTiming) {
        if (nightLightTiming != null) {
            ResponseData mResponseData = MySocketDataCache.getSetWisdomNightLight(nightLightTiming.mac,
                    nightLightTiming.startup,
                    (byte) nightLightTiming.getStartHour(), (byte) nightLightTiming.getStartMinute(),
                    (byte) nightLightTiming.getStopHour(), (byte) nightLightTiming.getStopMinute());
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " setNightLightWisdom  data:" + String.valueOf(mResponseData));
            }
            onOutputDataToServer(mResponseData);
        }
    }

    @Override
    public void openWisdomNightLight(String mac) {
        ResponseData mResponseData = MySocketDataCache.getSetWisdomNightLight(mac, true);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " openWisdomNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void closeWisdomNightLight(String mac) {
        ResponseData mResponseData = MySocketDataCache.getSetWisdomNightLight(mac, false);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " closeWisdomNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryNightLight(String mac) {
//        ResponseData mResponseData = MySocketDataCache.getQueryNightLight(mac);

        ResponseData mResponseData = MySocketDataCache.getQueryYellowLight(mac, 0);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void switchNightLight(String mac, boolean b) {

        ResponseData mResponseData = MySocketDataCache.getSwitchNightLight(mac, b);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " switchNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }


    @Override
    public void setNightLightColor(ColorLampRGB obj) {
        ResponseData mResponseData = MySocketDataCache.getSeYellowLight(obj.mac, obj.seq, obj.r, obj.g, obj.b);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setNightLightColor  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryIndicatorState(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryAllIndicatorState(mac);

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryIndicatorState  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void controlIndicatorState(String mac, boolean b) {
        ResponseData mResponseData = MySocketDataCache.getTrunAllIndicatorState(mac, b);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " controlIndicatorState  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTemperatureSensor(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryTempSensorStatus(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTemperatureSensor  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryRunningNightLight(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryRunningNightLight(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryRunningNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

//        queryTimingNightLight(mac);
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        queryWisdomNightLight(mac);
    }

    @Override
    public void queryTimingNightLight(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryTimingNightLight(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTimingNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryWisdomNightLight(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryWisdomNightLight(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryWisdomNightLight  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryScmTimezone(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryTimezone(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryScmTimezone  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setScmTimezone(String mac) {

        byte zone = getTimezone();

        Tlog.v(TAG, " setScmTimezone  zoneByte:" + zone + " zoneInt:" + (zone & 0xFF));

        ResponseData mResponseData = MySocketDataCache.getSetTimezone(mac, zone);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setScmTimezone  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }


    public static byte getTimezone() {

        java.util.TimeZone aDefault = java.util.TimeZone.getDefault();

        String timezone = aDefault.getDisplayName(false, java.util.TimeZone.SHORT);

        int i = timezone.indexOf("+");

        boolean isA = i > 0;

        if (!isA) {
            i = timezone.indexOf("-");
        }

        int i1 = timezone.indexOf(":");

        String substring = timezone.substring(i + 1, i1);

        int i2 = Integer.parseInt(substring);

        int timezoneInt = isA ? i2 : -i2;

        return (byte) (timezoneInt & 0xFF);


    }

    @Override
    public void queryScmTime(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryTime(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryScmTime  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setScmTime(String mac) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        Date date = instance.getTime();
        byte year = (byte) (date.getYear() + 1900 - 2000);
        byte month = (byte) (date.getMonth() + 1);
        byte day = (byte) date.getDate();
        byte hours = (byte) date.getHours();
        byte minutes = (byte) date.getMinutes();
        byte seconds = (byte) date.getSeconds();
        byte week = (byte) date.getDay();
        ResponseData mResponseData = MySocketDataCache.getSetTime(mac, year, month, day, hours, minutes, seconds, week);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setScmTime data[" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);

    }


    @Override
    public void setSetVoltageAlarmValue(String mac, int value) {
        ResponseData mResponseData = MySocketDataCache.getVoltageAlarmValue(mac, value);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetVoltageAlarmValue  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetCurrentAlarmValue(String mac, int value) {
        ResponseData mResponseData = MySocketDataCache.getCurrentAlarmValue(mac, value * 100);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetCurrentAlarmValue  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetPowerAlarmValue(String mac, int value) {
        ResponseData mResponseData = MySocketDataCache.getPowerAlarmValue(mac, value);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetPowerAlarmValue  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetTemperatureUnit(String mac, int value) {
        ResponseData mResponseData;

        if (CustomManager.getInstance().isTriggerBle()) {
            mResponseData = MySocketDataCache.getTempUnitBle(mac, (byte) value);
        } else {

            mResponseData = MySocketDataCache.getTempUnit(mac, (byte) value);
        }

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetTemperatureUnit  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetMonetaryUnit(String mac, int unit) {
        ResponseData mResponseData = MySocketDataCache.getMonetaryUnit(mac, (byte) unit);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetMonetaryUnit  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetElectricityPrice(String mac, int value) {
        ResponseData mResponseData = MySocketDataCache.getElectricityPrice(mac, value);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetElectricityPrice  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryVoltageAlarmValue(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryVoltageAlarmValue(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryVoltageAlarmValue  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryCurrentAlarmValue(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryCurrentAlarmValue(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCurrentAlarmValue  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryPowerAlarmValue(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryPowerAlarmValue(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryPowerAlarmValue  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTemperatureUnit(String mac) {
        ResponseData mResponseData;
        if (CustomManager.getInstance().isTriggerBle()) {
            mResponseData = MySocketDataCache.getQueryTemperatureUnitBle(mac);
        } else {
            mResponseData = MySocketDataCache.getQueryTemperatureUnit(mac);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryTemperatureUnit  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryMonetaryUnit(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryMonetaryUnit(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryMonetaryUnit  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryElectricityPrice(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryElectricityPrices(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryElectricityPrice  data:" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    public void testProtocolAnalysis(String mac, String content, int model) {
        ResponseData responseTestData = MySocketDataCache.getResponseTestData(mac, content);
        ReceivesData mReceiveData = new ReceivesData(mac, responseTestData.data);
        if (ComModel.MODEL_LAN == model) {
            mReceiveData.getReceiveModel().setModelOnlyLan();
        } else if (ComModel.MODEL_WAN == model) {
            mReceiveData.getReceiveModel().setModelOnlyWan();
        } else if (ComModel.MODEL_CASUAL == model) {
            mReceiveData.getReceiveModel().setModelCasual();
        } else {
            mReceiveData.getReceiveModel().fillEmpty();
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " testProtocolAnalysis  " + String.valueOf(mReceiveData));
        }
        onInputServerData(mReceiveData);
    }

    @Override
    public void rename(RenameBean obj) {
        String mac = obj.address;
        String name = obj.name;
        byte[] bytes = name != null ? name.getBytes() : mac.getBytes();
        ResponseData mResponseData = MySocketDataCache.getRename(mac, bytes);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " rename  " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryDeviceName(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryDeviceName(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryDeviceName " + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetRecoveryScm(String mac) {
        ResponseData mResponseData = MySocketDataCache.getRecovery(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetRecoveryScm :" + String.valueOf(mResponseData));
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void querySpendingElectricity(String mac) {

        ResponseData mResponseSpendingData = MySocketDataCache.getQuerySpendingElectricityE(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " querySpendingElectricityE :" + String.valueOf(mResponseSpendingData));
        }
        onOutputDataToServer(mResponseSpendingData);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseCountdownData = MySocketDataCache.getQuerySpendingElectricityS(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " querySpendingElectricityS :" + String.valueOf(mResponseCountdownData));
        }
        onOutputDataToServer(mResponseCountdownData);
    }

    @Override
    public void setSpendingCountdown(SpendingElectricityData obj) {

        ResponseData responseData = MySocketDataCache.getSetSpendingCountdown(obj.mac, obj.alarmSwitch,
                (byte) obj.model, (byte) obj.year, (byte) obj.month, (byte) obj.day, obj.alarmValue);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSpendingCountdown :" + String.valueOf(responseData));
        }
        onOutputDataToServer(responseData);
    }

    /************/

    @Override
    public void onElectricResult(String mac, int power, int avePower, int maxPower, float freq, float voltage, float current) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

        SensorData mSensorData = scmDevice.getSensorData();
        mSensorData.mPower.value = power;
        mSensorData.mPower.averageValue = avePower;
        mSensorData.mPower.maximumValue = maxPower;
        mSensorData.frequency = freq;
        mSensorData.voltage = voltage;
        mSensorData.electricity = current;

        if (scmDevice.isPublish()) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultPublishSensorData(mac, mSensorData);
            }
        }

    }


    @Override
    public void onNewElectricResult(String id, int relpower, int avepower, int maxpower, float freq,
                                    float voltage, float current, float maxCurrent, float powerFactory) {
        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(id);

        SensorData mSensorData = scmDevice.getSensorData();
        mSensorData.mPower.value = relpower;
        mSensorData.mPower.averageValue = avepower;
        mSensorData.mPower.maximumValue = maxpower;
        mSensorData.powerFactor = powerFactory;
        mSensorData.frequency = freq;
        mSensorData.voltage = voltage;
        mSensorData.electricity = current;

        if (scmDevice.isPublish()) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultPublishSensorData(id, mSensorData);
            }
        }
    }

    @Override
    public void onQueryCostRateResult(boolean result, CostRate mCostRate) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryCostRate(result, mCostRate);
        }
    }

    @Override
    public void onQueryCumuParamsResult(boolean result, CumuParams cumuParams) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryCumuParams(result, cumuParams);
        }
    }

    @Override
    public void onUpdateVersionResult(boolean result, UpdateVersion mVersion) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultUpdateVersion(result, mVersion);
        }
    }

    @Override
    public void onUSBResult(String id, boolean on) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultUSBState(id, on);
        }
    }

    @Override
    public void onNightLightResult(String id, boolean on) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(id);
        scmDevice.putNightLightState(on);

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onNightLightResult(id, on);
        }
    }

    @Override
    public void onTempHumiResult(String mac, float temp, float humi) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

        Tlog.d(TAG, " onTempHumiResult:" + mac + " temp:" + temp + " humi:" + humi);

        final SensorData mSensorData = scmDevice.getSensorData();
        mSensorData.mTemperature.value = temp;
        mSensorData.mHumidity.value = humi;

        if (scmDevice.isPublish()) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultPublishSensorData(mac, mSensorData);
            }
        }

        if (mScmResultCallBack != null) {
            final TempHumidityData mTempHumi = scmDevice.getTempHumidityData();
            mTempHumi.mTemperature.currentValue = temp;
            mTempHumi.mHumidity.currentValue = humi;

            Tlog.e(TAG, " onTempHumiResult:" + mTempHumi.toJsonStr());

            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);
        }


        if (CustomManager.getInstance().isAirtempNBProjectTest()) {
            // 温度=100 发送设备离线通知;
            if (temp == 100F) {
                notifyDeviceOffline(mac);
            }

        }

    }


    private void notifyDeviceOffline(String mac) {

        if (app == null) {
            return;
        }

        /**
         *  创建通知栏管理工具
         */

        Context applicationContext = app.getApplicationContext();

        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            //发送通知请求
            return;
        }

        Resources resources = app.getResources();

        String offline = resources.getString(R.string.offline);
        String ticker = String.valueOf(mac) + offline;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道

            int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(ticker, ticker, importance);

//            String description = "渠道描述1";
//            mChannel.setDescription(description);//渠道描述
//            mChannel.enableLights(true);//是否显示通知指示灯
//            mChannel.enableVibration(true);//是否振动

            //创建通知渠道
            notificationManager.createNotificationChannel(mChannel);
        }

        /**
         *  实例化通知栏构造器
         */

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(app, ticker);

        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle(resources.getString(R.string.device_offline))
                //设置内容
                .setContentText(String.valueOf(mac) + offline)
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker(ticker)
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);
        int id = mac.hashCode();
        notificationManager.notify(id, mBuilder.build());
    }

    private static final long DIFF = 1000 * 32; //32s
    private static final long HOUR = 1000 * 60 * 60;//1hour

    @Override
    public void onScmTimeResult(String mac, boolean result, long millis) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultScmTime(mac, result, millis);
        }

        if (result) {
            //  单片机时间不对，自动帮他校准时间
            long diff = Math.abs(System.currentTimeMillis() - millis);
            if (diff > DIFF) {
                // update scm time
                ScmDevice scmData = mScmDeviceUtils.containScmDevice(mac);

                if (scmData != null &&
                        (!scmData.isUpdateScmTime()
                                || diff >= HOUR)
                        ) {
                    scmData.setIsUpdateTime(true);
                    setScmTime(mac);
                }
            }
        }

    }

    @Override
    public void onRelayResult(String mac, boolean status) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSwitchRelay(mac, status);
        }
    }

    @Override
    public void onSetCountdownResult(String mac, boolean result, boolean startup, boolean on) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSetCountdown(mac, result, startup);
        }
    }

    @Override
    public void onQueryCountdownResult(String mac, boolean result, CountdownData mCountdownData) {

        if (result) {
            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultQueryCountdown(mac, mCountdownData);
            }

            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

            final SensorData mSensorData = scmDevice.getSensorData();
            mSensorData.on = mCountdownData.Switchgear;
            mSensorData.time = mCountdownData.hour * 60 * 60 * 1000 + mCountdownData.minute * 60 * 1000;

            if (scmDevice.isPublish()) {
                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onResultPublishSensorData(mac, mSensorData);
                }
            }
        }

    }

    @Override
    public void onCountdownReportResult(String mac, CountdownData mCountdownData) {
        onQueryCountdownResult(mac, true, mCountdownData);
    }

    @Override
    public void onSetTempHumiAlarmResult(String mac, boolean result, boolean startup, int model, int limit) {

        final ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        final SensorData sensorData = scmDevice.getSensorData();

        switch (model) {

            case SocketSecureKey.Model.ALARM_MODEL_HUMIDITY:

                if (result) {

                    final TempHumidityData mTempHumi = scmDevice.getTempHumidityData();

                    if (mTempHumi.mHumidity.typeIsHot((byte) limit)) {
                        mTempHumi.mHumidity.hotAlarmSwitch = startup;
                        mTempHumi.useSetHumiHotAlarmData();

                        if (startup) {
                            sensorData.mHumidity.alarmValue = mTempHumi.mHumidity.hotAlarmValue;
                        }

                    } else if (mTempHumi.mHumidity.typeIsCode((byte) limit)) {
                        mTempHumi.mHumidity.codeAlarmSwitch = startup;
                        mTempHumi.useSetHumiCodeAlarmData();

                        if (startup) {
                            sensorData.mHumidity.alarmValue = mTempHumi.mHumidity.codeAlarmValue;
                        }

                    }
                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onResultSetHumidityAlarm(mac, result, startup, limit);
                }

                break;

            case SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE:

                if (result) {

                    final TempHumidityData mTempHumi = scmDevice.getTempHumidityData();

                    if (mTempHumi.mTemperature.typeIsHot((byte) limit)) {
                        mTempHumi.mTemperature.hotAlarmSwitch = startup;
                        mTempHumi.useSetTempHotAlarmData();

                        if (startup) {
                            sensorData.mTemperature.alarmValue = mTempHumi.mTemperature.hotAlarmValue;
                        }

                    } else if (mTempHumi.mTemperature.typeIsCode((byte) limit)) {
                        mTempHumi.mTemperature.codeAlarmSwitch = startup;
                        mTempHumi.useSetTempCodeAlarmData();

                        if (startup) {
                            sensorData.mTemperature.alarmValue = mTempHumi.mTemperature.codeAlarmValue;
                        }

                    }
                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onResultSetTemperatureAlarm(mac, result, startup, limit);
                }
                break;

        }

        if (mScmResultCallBack != null) {
            if (scmDevice.isPublish()) {
                mScmResultCallBack.onResultPublishSensorData(mac, sensorData);
            }
        }

    }


    @Override
    public void onQueryTemperatureResult(String mac, boolean result, Temperature mTemperature) {

        final ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

        final TempHumidityData mTempHumi = scmDevice.getTempHumidityData();
        if (result) {
            if (mTemperature.typeIsHot()) {
                mTempHumi.mTemperature.hotAlarmSwitch = mTemperature.hotAlarmSwitch;
                mTempHumi.mTemperature.hotAlarmValue = mTemperature.hotAlarmValue;
            } else if (mTemperature.typeIsCode()) {
                mTempHumi.mTemperature.codeAlarmSwitch = mTemperature.codeAlarmSwitch;
                mTempHumi.mTemperature.codeAlarmValue = mTemperature.codeAlarmValue;
            }

            mTempHumi.mTemperature.currentValue = mTemperature.currentValue;
        }

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, " onQueryTemperatureResult:" + mTempHumi.toJsonStr());
        }

        final SensorData sensorData = scmDevice.getSensorData();
        if (result) {
            if (mTemperature.typeIsHot() && mTemperature.hotAlarmSwitch) {
                sensorData.mTemperature.alarmValue = mTemperature.hotAlarmValue;
            } else if (mTemperature.typeIsCode() && mTemperature.codeAlarmSwitch) {
                sensorData.mTemperature.alarmValue = mTemperature.codeAlarmValue;
            }
            sensorData.mTemperature.value = mTemperature.currentValue;
        }

        if (mScmResultCallBack != null) {

            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);

            if (scmDevice.isPublish()) {
                mScmResultCallBack.onResultPublishSensorData(mac, sensorData);
            }
        }

    }

    @Override
    public void onQueryHumidityResult(String mac, boolean result, Humidity mHumidity) {

        final ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

        final TempHumidityData mTempHumi = scmDevice.getTempHumidityData();
        if (result) {
            if (mHumidity.typeIsHot()) {
                mTempHumi.mHumidity.hotAlarmSwitch = mHumidity.hotAlarmSwitch;
                mTempHumi.mHumidity.hotAlarmValue = mHumidity.hotAlarmValue;
            } else if (mHumidity.typeIsCode()) {
                mTempHumi.mHumidity.codeAlarmSwitch = mHumidity.codeAlarmSwitch;
                mTempHumi.mHumidity.codeAlarmValue = mHumidity.codeAlarmValue;
            }

            mTempHumi.mHumidity.currentValue = mHumidity.currentValue;
        }

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, " onQueryHumidityResult:" + mTempHumi.toJsonStr());
        }

        final SensorData sensorData = scmDevice.getSensorData();
        if (result) {
            if (mHumidity.typeIsHot() && mHumidity.hotAlarmSwitch) {
                sensorData.mHumidity.alarmValue = mHumidity.hotAlarmValue;
            } else if (mHumidity.typeIsCode() && mHumidity.codeAlarmSwitch) {
                sensorData.mHumidity.alarmValue = mHumidity.codeAlarmValue;
            }
            sensorData.mHumidity.value = mHumidity.currentValue;
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);
            if (scmDevice.isPublish()) {
                mScmResultCallBack.onResultPublishSensorData(mac, sensorData);
            }
        }

        // debug
//        onQueryTempSensorResult(true, mac, false);

    }


    @Override
    public void onQueryTimingResult(String mac, boolean result, TimingListData mData) {

        if (result) {
            final TimingListData mTimingListData = mScmDeviceUtils.getScmDevice(mac).getTimingListData();

            if (mData.isAdvanceModel()) {
                mTimingListData.advanceDataArrayCopy(mData.getAdvanceDataArray());
            } else if (mData.isCommonModel()) {
                mTimingListData.commonDataArrayCopy(mData.getCommonDataArray());
            }

            if (mScmResultCallBack != null) {
                mScmResultCallBack.onResultQueryTiming(mac, mTimingListData);
            }
        }

    }

    @Override
    public void onSetTimingResult(String mac, TimingSetResult mResult) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSetTiming(mac, mResult);
        }
    }

    @Override
    public void onTimingCommonExecuteResult(String mac, TimingCommonData mData) {
        // 定时功能生效，回调js继电器的状态
        if (mData != null) {
            onRelayResult(mac, mData.isOn());

            TimingSetResult mResult = new TimingSetResult();
            mResult.result = true;
            mResult.mac = mData.getMac();
            mResult.model = SocketSecureKey.Util.getAdvanceTiming();
            mResult.id = mData.getId();
            mResult.startup = mData.getStartup();
            mResult.state = mData.getState();
            mResult.week = mData.getWeek();
            onSetTimingResult(mac, mResult);

        }
    }


    @Override
    public void onTimingAdvanceExecuteResult(String mac, TimingAdvanceData mAdvanceData) {
        if (mAdvanceData != null) {
            onRelayResult(mac, mAdvanceData.on);

            TimingSetResult mResult = new TimingSetResult();
            mResult.result = true;
            mResult.mac = mAdvanceData.mac;
            mResult.model = SocketSecureKey.Util.getAdvanceTiming();
            mResult.id = mAdvanceData.id;
            mResult.startup = mAdvanceData.startup;
            mResult.state = mAdvanceData.state;
            mResult.week = (byte) mAdvanceData.week;
            onSetTimingResult(mac, mResult);

//            final TimingListData mTimingListData = mScmDeviceUtils.getScmDevice(mac).getTimingListData();
//
//            ArrayList<TimingAdvanceData> advanceDataArray = mTimingListData.getAdvanceDataArray();
//
//            if (advanceDataArray != null && advanceDataArray.size() > 0) {
//                try {
//                    for (int i = 0; i < advanceDataArray.size(); i++) {
//                        TimingAdvanceData tmpTimingAdvanceData = advanceDataArray.get(i);
//                        if (tmpTimingAdvanceData.id == mAdvanceData.id) {
//                            tmpTimingAdvanceData.startup = mAdvanceData.startup;
//                            tmpTimingAdvanceData.state = mAdvanceData.state;
//                            tmpTimingAdvanceData.week = mAdvanceData.week;
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            if (mScmResultCallBack != null) {
//                mScmResultCallBack.onResultQueryTiming(mac, mTimingListData);
//            }


        }
    }

    @Override
    public void onLanBindResult(boolean result, LanBindingDevice mLanBindingDevice) {

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultDeviceLanBind(result, mLanBindingDevice);
        }
    }

    @Override
    public void onTokenInvalid(String mac) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onTokenInvalid(mac);
        }
    }

    @Override
    public void onLanUnBindResult(boolean result, LanBindingDevice mLanBindingDevice) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onLanUnBindResult(result, mLanBindingDevice);
        }
    }

    @Override
    public void onRequestTokenResult(boolean result, String mac, int random, int token) {
        if (mScmResultCallBack != null) {

            if (result) {
                ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
                int requestRandom = scmDevice.getRequestRandom();
                Tlog.e(TAG, " requestRandom:" + requestRandom + " " + random);
                if (requestRandom == random - 1) {

                    scmDevice.setToken(token);
                    MySocketDataCache.putToken(mac, token);

                    mScmResultCallBack.onResultRequestToken(mac, token);

                }
            }
        }
    }

    @Override
    public void onConnectResult(boolean result, String id) {
        if (mScmResultCallBack != null) {
            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(id);
            scmDevice.setConResult(result);
            mScmResultCallBack.onResultConnect(result, id);
        }
    }

    @Override
    public void onSleepResult(boolean result, String id) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSleep(result, id);
        }
    }

    @Override
    public void onDisconnectResult(boolean result, String id) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultDisconnect(result, id);
        }
    }

    @Override
    public void onCostRateSetResult(boolean result, byte model) {

    }


    @Override
    public void onSettingVoltageResult(String mac, boolean result, int mAlarmVoltageValue) {

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingVoltage(mac, result);
        }
    }

    @Override
    public void onSettingCurrentResult(String mac, boolean result, float mAlarmCurrentValue) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingCurrent(mac, result);
        }
    }

    @Override
    public void onSettingPowerResult(String mac, boolean result, int mAlarmPowerValue) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingPower(mac, result);
        }
    }

    @Override
    public void onSettingTemperatureUnitResult(String mac, boolean result, int mTemperatureUnit) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingTemperatureUnit(mac, result);
        }
    }

    @Override
    public void onSettingMonetaryUnitResult(String mac, boolean result, int mMonetaryUnit) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingMonetaryUnit(mac, result, mMonetaryUnit);
        }
    }

    @Override
    public void onSettingElectricityPriceResult(String mac, boolean result, int mElectricityPrice) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingElectricityPrice(mac, result);
        }
    }

    @Override
    public void onSettingRecoveryResult(String mac, boolean result) {

        if (result) {

            ScmDevice scmData = mScmDeviceUtils.containScmDevice(mac);
            if (scmData != null) {
                scmData.clearSensor();
            }
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSettingRecovery(mac, result);
        }

    }

    @Override
    public void onQueryCurrentResult(String id, boolean result, float value) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryCurrentAlarmValue(id, result, value);
        }
    }

    @Override
    public void onQueryElectricityPriceResult(String id, boolean result, int mElectricityPrices) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryElectricityPrice(id, result, mElectricityPrices);
        }
    }

    @Override
    public void onQueryMonetaryUnitResult(String id, boolean result, int value) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryMonetaryUnit(id, result, value);
        }
    }

    @Override
    public void onQueryPowerResult(String id, boolean result, int mAlarmPowerValue) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryPowerAlarmValue(id, result, mAlarmPowerValue);
        }
    }

    @Override
    public void onQueryTemperatureUnitResult(String id, boolean result, int value) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryTemperatureUnit(id, result, value);
        }
    }

    @Override
    public void onQueryVoltageResult(String id, boolean result, int mAlarmVoltageValue) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryVoltageAlarmValue(id, result, mAlarmVoltageValue);
        }
    }

    @Override
    public void onDeviceDiscoveryResult(String id, boolean result, LanDeviceInfo mWiFiDevice) {

        if (mWiFiDevice != null) {
            String mac = mWiFiDevice.getMac();
            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
            scmDevice.putIp(mWiFiDevice.getIp());
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultDeviceDiscovery(id, result, mWiFiDevice);
        }
    }

    @Override
    public void onDeviceRenameResult(String id, boolean result, String name) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultRename(id, result, name);
        }
    }

    @Override
    public void onQueryDeviceNameResult(String id, boolean result, String name) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryRename(id, result, name);
        }
    }

    @Override
    public void onQueryDeviceSSIDResult(String id, boolean result, int rssi, String ssid) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryDeviceSSID(id, result, rssi, ssid);
        }
    }


    @Override
    public void onRGBSetResult(boolean result, ColorLampRGB mColorLamp) {

        if (result) {

//            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mColorLamp.mac);

            if (mColorLamp.model == SocketSecureKey.Model.MODEL_COLOR_LAMP) {
//                scmDevice.setColorLamp(mColorLamp);
//
//                ColorLampRGB yellowLightRGB = scmDevice.getYellowLightRGB();
//
//                if (mColorLamp.r == 0 && mColorLamp.g == 0 && mColorLamp.b == 0
//                        && yellowLightRGB != null
//                        && yellowLightRGB.r == 0 && yellowLightRGB.g == 0 && yellowLightRGB.b == 0
//                        ) {
//                    // 彩灯关,小夜灯没关,不通知UI.
//                    return;
//
//                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onRGBSetResult(true, mColorLamp);
                }


            } else if (mColorLamp.model == SocketSecureKey.Model.MODEL_YELLOW_LIGHT) {
//                scmDevice.setYellowLight(mColorLamp);
//
//                ColorLampRGB mColorLampRGB = scmDevice.getColorLampRGB();
//
//                if (mColorLamp.r == 0 && mColorLamp.g == 0 && mColorLamp.b == 0
//                        && mColorLampRGB != null
//                        && mColorLampRGB.r == 0 && mColorLampRGB.g == 0 && mColorLampRGB.b == 0
//                        ) {
//                    // 小夜灯关,彩灯没关,不通知UI.
//                    return;
//
//                }
                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onRGBYellowSetResult(true, mColorLamp);
                }
            }

        }
    }

    @Override
    public void onRGBQueryResult(boolean result, ColorLampRGB mColorLampRGB) {

        if (result) {

//            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mColorLampRGB.mac);

            if (mColorLampRGB.model == SocketSecureKey.Model.MODEL_COLOR_LAMP) {
//                scmDevice.setColorLamp(mColorLampRGB);
//
//                ColorLampRGB yellowLightRGB = scmDevice.getYellowLightRGB();
//                if (mColorLampRGB.r == 0 && mColorLampRGB.g == 0 && mColorLampRGB.b == 0) {
//                    // 如果
//                    if (yellowLightRGB != null) {
//                        if (yellowLightRGB.r == 0 && yellowLightRGB.g == 0 && yellowLightRGB.b == 0) {
//
//                        } else {
//                            // 彩灯关,小夜灯没关,不通知UI.
//                            return;
//                        }
//                    }
//
//                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onRGBQueryResult(true, mColorLampRGB);
                }

            } else if (mColorLampRGB.model == SocketSecureKey.Model.MODEL_YELLOW_LIGHT) {
//                scmDevice.setYellowLight(mColorLamp);
//
//                ColorLampRGB mColorLampRGB = scmDevice.getColorLampRGB();
//
//                if (mColorLamp.r == 0 && mColorLamp.g == 0 && mColorLamp.b == 0
//                        && mColorLampRGB != null
//                        && mColorLampRGB.r == 0 && mColorLampRGB.g == 0 && mColorLampRGB.b == 0
//                        ) {
//                    // 小夜灯关,彩灯没关,不通知UI.
//                    return;
//
//                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onRGBYellowSetResult(true, mColorLampRGB);
                }
            }

        }


    }


    @Override
    public void onQuerySpendingElectricityResult(String id, boolean result, SpendingElectricityData mSpendingElectricityData) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(id);
        SpendingElectricityData mSpendingData = scmDevice.getSpendingData();
        SpendingElectricityData mElectricityData = scmDevice.getElectricityData();

        if (result) {
            if (SocketSecureKey.Util.isElectricity((byte) mSpendingElectricityData.model)) {
                mElectricityData.memor(mSpendingElectricityData);
            } else {
                mSpendingData.memor(mSpendingElectricityData);
            }
        }

        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQuerySpendingElectricity(id, result, mElectricityData, mSpendingData);
        }
    }

    @Override
    public void onSetSpendingElectricityResult(String id, boolean result, SpendingElectricityData mSpendingElectricityData) {
        if (mScmResultCallBack != null) {
            int model = 0;
            boolean alarmSwitch = false;
            if (mSpendingElectricityData != null) {
                model = mSpendingElectricityData.model;
                alarmSwitch = mSpendingElectricityData.alarmSwitch;
            }
            mScmResultCallBack.onResultSetSpendingElectricity(id, model, alarmSwitch, result);
        }
    }


    @Override
    public void onSetTimingTempHumiResult(boolean result, String id, TimingTempHumiData mAdvanceData) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSetTimingTempHumi(result, id, mAdvanceData);
        }
    }

    @Override
    public void onQueryTimingTempHumiResult(boolean result, String id, ArrayList<TimingTempHumiData> mDataLst) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryTimingTempHumi(result, id, mDataLst);
        }
    }

    @Override
    public void onSetNightLightResult(boolean result, NightLightTiming mNightLightTiming) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSetNightLight(result, mNightLightTiming);
        }
    }

    @Override
    public void onQueryNightLightResult(boolean result, NightLightTiming mNightLightTiming) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryNightLight(result, mNightLightTiming);
        }
    }

    @Override
    public void onColorLamResult(String id, boolean b) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultColorLam(id, b);
        }
    }

    @Override
    public void onIndicatorStatusResult(String mac, boolean result, byte seq, boolean on) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultIndicatorStatus(mac, result, seq, on);
        }
    }

    @Override
    public void onQueryTempSensorResult(boolean result, String mac, boolean status) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryTempSensor(result, mac, status);
        }
    }

    @Override
    public void onReportTempSensorResult(String mac, boolean status) {

        if (!status) {
            notifyDeviceSensorError(mac);
        }
    }


    private void notifyDeviceSensorError(String mac) {

        if (app == null) {
            return;
        }
        if (mac == null || H5Config.DEFAULT_MAC.equalsIgnoreCase(mac)) {
            Tlog.e(TAG, " notifyDeviceSensorError mac == null");
            return;
        }

        /**
         *  创建通知栏管理工具
         */

        Context applicationContext = app.getApplicationContext();

        NotificationManager notificationManager = (NotificationManager)
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            //发送通知请求
            Tlog.e(TAG, " notifyDeviceSensorError notificationManager == null");
            return;

        }

        String ticker = String.valueOf(mac) + " sensor error";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道

            int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(ticker, ticker, importance);

//            String description = "渠道描述1";
//            mChannel.setDescription(description);//渠道描述
//            mChannel.enableLights(true);//是否显示通知指示灯
//            mChannel.enableVibration(true);//是否振动

            //创建通知渠道
            notificationManager.createNotificationChannel(mChannel);
        }

        /**
         *  实例化通知栏构造器
         */

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(app, ticker);

        Resources resources = app.getResources();
        String title = resources.getString(R.string.temp_sensor_error_title);
        String error = resources.getString(R.string.temp_sensor_error);

        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle(String.valueOf(mac) + title)
                //设置内容
                .setContentText(error)
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.warning))
                //设置小图标
                .setSmallIcon(R.mipmap.warning)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker(String.valueOf(mac) + title)
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);
        int id = mac.hashCode();
        notificationManager.notify(id, mBuilder.build());
    }


    @Override
    public IDebugerProtocolStream getIDebugerStream() {
        return productDetectionManager;
    }

    @Override
    public void onFail(FailTaskResult mFailTask) {
//...

        if (productDetectionManager != null) {
            productDetectionManager.receiveFail(mFailTask);
        }

        if (Debuger.isToastDebug) {
            LooperManager.getInstance().getWorkHandler().post(new Runnable() {
                @Override
                public void run() {

                    final int what = (mFailTask.type & 0xFF) << 8 | ((mFailTask.cmd - 1) & 0xFF);
                    Toast.makeText(app, "cmd:" + Integer.toHexString(what) + " " + mFailTask.description, Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    @Override
    public void onTestResult(byte[] protocolParams) {

        if (productDetectionManager != null) {
            productDetectionManager.receiveProtocolAnalysisResult(protocolParams);
        }

    }

    @Override
    public void onSetTimezoneResult(boolean result, String id, byte zone) {

    }

    @Override
    public void onQueryTimezoneResult(boolean result, String id, byte zone) {

        if (zone != getTimezone()) {
            setScmTimezone(id);
        }

    }

    @Override
    public void onRemoveCmd(String id, byte paramType, byte paramCmd, int seq) {
        if (Debuger.isLogDebug) {
            Tlog.e(TAG, " onRemoveCmd Data mac:" + id
                    + " what:" + Integer.toHexString(paramType)
                    + "-" + Integer.toHexString(paramCmd)
                    + " seq:" + seq);
        }
        final int what = (paramType & 0xFF) << 8 | ((paramCmd - 1) & 0xFF);

        mScmDeviceUtils.getScmDevice(id).receiveOnePkg(what, seq);
    }


    @Override
    public void onSuccess(String mac, byte type, byte cmd, int seq) {
        //...

        final int what = (type & 0xFF) << 8 | ((cmd - 1) & 0xFF);

        // 上报数据不参与重发机制
        if (type == SocketSecureKey.Type.TYPE_REPORT || type == SocketSecureKey.Type.TYPE_ERROR) {
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " receive report Data mac:" + mac + " what:" + Integer.toHexString(what) + " seq:" + seq);
            }
            return;
        }

        // 发现数据是用FF的mac发送的，回来的数据是设备的mac;
        if ((type == SocketSecureKey.Type.TYPE_SYSTEM && cmd == SocketSecureKey.Cmd.CMD_DISCOVERY_DEVICE_RESPONSE)) {
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " receive discovery Data mac:" + mac + " what:" + Integer.toHexString(what) + " seq:" + seq);
            }
            return;
        }

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, " receiveData mac:" + mac + " what:" + Integer.toHexString(what) + " seq:" + seq);
        }

        mScmDeviceUtils.getScmDevice(mac).receiveOnePkg(what, seq);

    }


    private IDebugerProtocolStream productDetectionManager = null;

    @Override
    public void regIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager) {
        Tlog.v(ProductDetectionManager.TAG, "SocketScmManager regIDebugerProtocolStream");
        this.productDetectionManager = productDetectionManager;
    }

    @Override
    public void unregIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager) {
        Tlog.v(ProductDetectionManager.TAG, "SocketScmManager unregIDebugerProtocolStream");
        this.productDetectionManager = null;
    }

}
