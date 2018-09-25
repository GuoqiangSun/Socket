package cn.com.startai.socket.sign.scm.impl;

import android.app.Application;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.IRegDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.scm.AbsSocketScm;
import cn.com.startai.socket.sign.scm.bean.CostRate;
import cn.com.startai.socket.sign.scm.bean.CountdownData;
import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.bean.sensor.SensorData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Humidity;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.TempHumidityData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Temperature;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.receivetask.ProtocolTaskImpl;
import cn.com.startai.socket.sign.scm.util.MySocketDataCache;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.util.Bit;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.ProtocolProcessor;
import cn.com.swain.support.protocolEngine.ProtocolProcessorFactory;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SocketScmManager extends AbsSocketScm implements IService, IDataProtocolOutput, OnTaskCallBack, IRegDebugerProtocolStream, ScmDevice.OnHeartbeatCallBack {

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
        if (mResponse != null) {

            if (mResponseData.getRepeatMsgModel().isNeedRepeatSend()) {

                long timeout = 1000 * 3;

                mScmDeviceUtils.getScmDevice(mResponseData.toID).recordSendMsg(mResponseData, timeout);
            }

            if (Tlog.isDebug()) {
                Tlog.w(TAG, " sendData  mac:" + mResponseData.toID + mResponseData.getRepeatMsgModel().toString());
            }

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


    private final Object syncObj = new byte[1];
    private ProtocolProcessor pm;

    private ScmDeviceUtils mScmDeviceUtils;


    @Override
    public void onSCreate() {
        Tlog.v(TAG, " SocketProtocolWrapper onSCreate()");

        MySocketDataCache.BuildParams mParams = new MySocketDataCache.BuildParams();
        mParams.setCustom(CustomManager.getInstance().getCustom());
        mParams.setProduct(CustomManager.getInstance().getProduct());
        mParams.setProtocolVersion(CustomManager.getInstance().getProtocolVersion());

        MySocketDataCache.getMInstance().init(mParams);
        MySocketDataCache.getMInstance().onSCreate();

        mScmDeviceUtils = new ScmDeviceUtils(this, this);

        int version = CustomManager.getInstance().getProtocolVersion();

        pm = ProtocolProcessorFactory.newSingleThreadAnalysisMutilTask(
                LooperManager.getInstance().getProtocolLooper(),
                new ProtocolTaskImpl(this, this),
                version);
    }

    @Override
    public void onSResume() {
        Tlog.v(TAG, " SocketProtocolWrapper onSResume()");
        MySocketDataCache.getMInstance().onSResume();
    }

    @Override
    public void onSPause() {
        Tlog.v(TAG, " SocketProtocolWrapper onSPause()");
        MySocketDataCache.getMInstance().onSPause();
    }

    @Override
    public void onSDestroy() {
        Tlog.v(TAG, " SocketProtocolWrapper onSDestroy()");

        MySocketDataCache.getMInstance().onSDestroy();

        if (pm != null) {
            pm.release();
            pm = null;
        }

        mScmDeviceUtils.cleanMap();
    }

    @Override
    public void onSFinish() {
        Tlog.v(TAG, " SocketProtocolWrapper onSFinish()");
        MySocketDataCache.getMInstance().onSFinish();
    }

    @Override
    public void onInputServerData(ReceivesData mReceivesData) {
        if (pm != null) {
            pm.onInReceiveData(mReceivesData);
        }
    }

    @Override
    public void onStartSendHeartbeat(ScmDevice scmDevice) {

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, scmDevice.getAddress() + " onStartSendHeartbeat :" + Integer.toHexString(scmDevice.getToken()));
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
        mScmDeviceUtils.showConnectDevice();

        queryScmTime(address);

        if (productDetectionManager != null) {
            productDetectionManager.connected(address);
        }

    }

    @Override
    public void onDisconnected(String address) {
        Tlog.v(TAG, " onDisconnected : " + address);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(address);
        scmDevice.disconnected();
        if (CustomManager.getInstance().isBleSocket()) {
            scmDevice.clearSensor();
        }

        if (productDetectionManager != null) {
            productDetectionManager.disconnected(address);
        }

    }

    /************/

    @Override
    public void quickControlRelay(String mac, boolean on) {
        Tlog.v(TAG, " quickControlRelay status : " + on);
        ResponseData mResponseData = MySocketDataCache.getQuickSetRelaySwitch(mac, on);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " quickControlRelay " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void quickQueryRelay(String mac) {
        Tlog.v(TAG, " quickQueryRelay  ");

        ResponseData mResponseData = MySocketDataCache.getQuickQueryRelayStatus(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " quickQueryRelay " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void queryHistoryCount(QueryHistoryCount mQueryCount) {
        Tlog.v(TAG, " queryHistoryCount  ");

        byte[] params = new byte[6];

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        if (mQueryCount.startTime != null) {
            try {
                Date date = mFormat.parse(mQueryCount.startTime);
                params[0] = (byte) (date.getYear() + 1900 - 2000);
                params[1] = (byte) (date.getMonth() + 1);
                params[2] = (byte) date.getDate();
            } catch (ParseException e) {
                e.printStackTrace();
                Tlog.e(TAG, " queryHistoryCount parse startTime", e);
            }
        }

        if (mQueryCount.endTime != null) {
            try {
                Date date = mFormat.parse(mQueryCount.endTime);
                params[3] = (byte) (date.getYear() + 1900 - 2000);
                params[4] = (byte) (date.getMonth() + 1);
                params[5] = (byte) date.getDate();
            } catch (ParseException e) {
                e.printStackTrace();
                Tlog.e(TAG, " queryHistoryCount parse endTime", e);
            }
        }

        ResponseData mResponseData = MySocketDataCache.getQueryHistoryCount(mQueryCount.mac, params);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryHistoryCount " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryCostRate(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryConstRate(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCostRate " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryCumuParam(String mac) {
        ResponseData mResponseData = MySocketDataCache.getQueryCumuParam(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " queryCumuParam " + mResponseData.toString());
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
        Tlog.v(TAG, " SwitchRelay status : " + status);

        ResponseData mResponseData = MySocketDataCache.getSetRelaySwitch(mac, status);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " SwitchRelay " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void queryRelayState(String mac) {
        Tlog.v(TAG, " QueryRelayState  ");
        ResponseData mResponseData = MySocketDataCache.getQueryRelayStatus(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setPowerCountdown(PowerCountdown powerCountdown) {
        Tlog.v(TAG, " setPowerCountdown  ");
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getSetCountdown(powerCountdown.getMac(), powerCountdown.getStatus(),
                    powerCountdown.getSwitchGear(), powerCountdown.getHour(), powerCountdown.getMinute());
        }

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " PowerCountdown data:" + mResponseData.toString());
        }

        onOutputDataToServer(mResponseData);

    }

    @Override
    public void setCommonTiming(TimingCommonData mTimingCommonData) {
        Tlog.v(TAG, " setCommonTiming  ");
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getSetCommonTiming(mTimingCommonData.getMac(),
                    mTimingCommonData.getId(), mTimingCommonData.getState(), mTimingCommonData.isOn(),
                    mTimingCommonData.getWeek(), mTimingCommonData.getHour(), mTimingCommonData.getMinute(),
                    mTimingCommonData.getStartup());
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " Timing Common data: " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void setAdvanceTiming(TimingAdvanceData mTimingAdvanceData) {
        Tlog.v(TAG, " setAdvanceTiming  ");
        ResponseData mResponseData;
        synchronized (syncObj) {
//            (byte id, byte startHour, byte startMinute, byte stopHour, byte stopMinute, boolean on, byte onIntervalHour,
// byte onIntervalMinute, byte offIntervalHour, byte offIntervalMinute, byte startup) {
            mResponseData = MySocketDataCache.getSetAdvanceTiming(mTimingAdvanceData.mac, mTimingAdvanceData.id,
                    (byte) mTimingAdvanceData.startHour, (byte) mTimingAdvanceData.startMinute,
                    (byte) mTimingAdvanceData.endHour, (byte) mTimingAdvanceData.endMinute, mTimingAdvanceData.on,
                    (byte) mTimingAdvanceData.onIntervalHour, (byte) mTimingAdvanceData.onIntervalMinute,
                    (byte) mTimingAdvanceData.offIntervalHour, (byte) mTimingAdvanceData.offIntervalMinute,
                    mTimingAdvanceData.startup);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " Timing Advance data: " + mResponseData.toString());
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
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " bindDevice data: " + mResponseData.toString());
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
            Tlog.v(TAG, " unbindDevice data: " + mResponseData.toString());
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
            Tlog.v(TAG, " requestToken data: " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void controlDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " controlDevice  " + userID + " token:" + Integer.toHexString(token));

        mScmDeviceUtils.getScmDevice(mac).setToken(token);

        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = MySocketDataCache.getControlDevice(mac, bytes, token);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void appSleep(String mac, String userID, int token) {
        Tlog.v(TAG, " appSleep " + userID + " token:" + token);
        ResponseData mResponseData = MySocketDataCache.getAppSleep(mac, userID.getBytes(), token);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void disconnectDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " disconnectDevice " + userID + " token:" + token);
        ResponseData mResponseData = MySocketDataCache.getDisconnectDevice(mac, userID.getBytes(), token);
        onOutputDataToServer(mResponseData);
    }


    @Override
    public void setTempHumidityAlarm(TempHumidityAlarmData mAlarm) {
        Tlog.v(TAG, " setTempHumidityAlarm  ");
        ResponseData mResponseData;
        synchronized (syncObj) {
            int model;
            if (mAlarm.isTemperatureType()) {
                model = SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE;
            } else {
                model = SocketSecureKey.Model.ALARM_MODEL_HUMIDITY;
            }
            mResponseData = MySocketDataCache.getSetTempHumidityAlarm(mAlarm.getMac(), mAlarm.isStartup(),
                    model, mAlarm.getAlarmValue(), mAlarm.getAlarmValueDeci(), mAlarm.isLimitUp());

            if (model == SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE) {
                ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mAlarm.getMac());

                Tlog.v(TAG, " setTempAlarmData limit: " + mAlarm.getLimit());

                if (mAlarm.isLimitUp()) {
                    scmDevice.setTempHotAlarmData(mAlarm.getOriginalAlarmValue());

                } else if (mAlarm.isLimitDown()) {

                    scmDevice.setTempCodeAlarmData(mAlarm.getOriginalAlarmValue());
                }

            }
        }


        if (Debuger.isLogDebug) {

            Tlog.v(TAG, " setTempHumidityAlarm  float:" + mAlarm.getOriginalAlarmValue() + " int:" + mAlarm.getAlarmValue() + " deci:" + mAlarm.getAlarmValueDeci());

            Tlog.v(TAG, " setTempHumidityAlarm  data: " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTempHumidityData(String mac) {
        Tlog.v(TAG, " queryTempHumidityData  ");
        ResponseData mResponseTempData = MySocketDataCache.getQueryTemperatureLimitUp(mac);
        onOutputDataToServer(mResponseTempData);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseTempData2 = MySocketDataCache.getQueryTemperatureLimitDown(mac);
        onOutputDataToServer(mResponseTempData2);

//        Tlog.v(TAG, " queryHumidity ");
//        ResponseData mResponseHumidityData = new SocketResponseDataUtil().newResponseDataCalCrc(mac, MySocketDataCache.getQueryHumidity());
//        if (mResponse != null) {
//            mResponse.onOutputDataToServer(mResponseHumidityData);
//        }
    }

    @Override
    public void queryCountdownData(String mac) {
        Tlog.v(TAG, " queryCountdownData  ");
        ResponseData mResponseData = MySocketDataCache.getQueryCountdown(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTimingData(String mac) {
        Tlog.v(TAG, " queryTimingData  ");
        ResponseData mResponseData = MySocketDataCache.getQueryCommonTimingList(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryScmTime(String mac) {
        Tlog.v(TAG, " queryScmTime  ");
        ResponseData mResponseData = MySocketDataCache.getQueryTime(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setScmTime(String mac) {
        Tlog.v(TAG, " setScmTime  ");
        ResponseData mResponseData;
        synchronized (syncObj) {
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
            mResponseData = MySocketDataCache.getSetTime(mac, year, month, day, hours, minutes, seconds, week);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setScmTime data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);

    }


    @Override
    public void setSetVoltageAlarmValue(String mac, int value) {
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getVoltageAlarmValue(mac, value);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetVoltageAlarmValue  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetCurrentAlarmValue(String mac, int value) {
        ResponseData mResponseData;
        synchronized (syncObj) {

//            mResponseData = MySocketDataCache.getCurrentAlarmValue(mac, (byte)value);


            mResponseData = MySocketDataCache.getCurrentAlarmValue2(mac, value * 100);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetCurrentAlarmValue  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetPowerAlarmValue(String mac, int value) {
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getPowerAlarmValue(mac, value);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetPowerAlarmValue  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetTemperatureUnit(String mac, int value) {
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getTempUnit(mac, (byte) value);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetTemperatureUnit  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetMonetaryUnit(String mac, int unit) {
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getMonetaryUnit(mac, (byte) unit);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetMonetaryUnit  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetElectricityPrice(String mac, int value) {
        ResponseData mResponseData;
        synchronized (syncObj) {
            mResponseData = MySocketDataCache.getElectricityPrice(mac, value);
        }
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetElectricityPrice  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryVoltageAlarmValue(String mac) {
        Tlog.v(TAG, " queryVoltageAlarmValue  ");
        ResponseData mResponseData = MySocketDataCache.getQueryVoltageAlarmValue(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryCurrentAlarmValue(String mac) {
        Tlog.v(TAG, " queryCurrentAlarmValue  ");
        ResponseData mResponseData = MySocketDataCache.getQueryCurrentAlarmValue(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryPowerAlarmValue(String mac) {
        Tlog.v(TAG, " queryPowerAlarmValue  ");
        ResponseData mResponseData = MySocketDataCache.getQueryPowerAlarmValue(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryTemperatureUnit(String mac) {
        Tlog.v(TAG, " queryTemperatureUnit  ");
        ResponseData mResponseData = MySocketDataCache.getQueryTemperatureUnit(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryMonetaryUnit(String mac) {
        Tlog.v(TAG, " queryMonetaryUnit  ");
        ResponseData mResponseData = MySocketDataCache.getQueryMonetaryUnit(mac);
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void queryElectricityPrice(String mac) {
        Tlog.v(TAG, " queryElectricityPrice  ");
        ResponseData mResponseData = MySocketDataCache.getQueryElectricityPrices(mac);
        onOutputDataToServer(mResponseData);
    }


    @Override
    public void rename(RenameBean obj) {
        Tlog.v(TAG, " rename  " + obj.name);

        ResponseData mResponseData;
        synchronized (syncObj) {
            String mac = obj.address;
            String name = obj.name;
            byte[] bytes = name.getBytes();
            mResponseData = MySocketDataCache.getRename(mac, bytes);
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void setSetRecoveryScm(String mac) {
        ResponseData mResponseData = MySocketDataCache.getRecovery(mac);
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " setSetRecoveryScm  data:" + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    @Override
    public void querySpendingElectricity(String mac) {
        Tlog.v(TAG, " querySpendingElectricity  ");

        ResponseData mResponseSpendingData = MySocketDataCache.getQuerySpendingElectricityE(mac);
        onOutputDataToServer(mResponseSpendingData);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseData mResponseCountdownData = MySocketDataCache.getQuerySpendingElectricityS(mac);
        onOutputDataToServer(mResponseCountdownData);
    }

    @Override
    public void setSpendingCountdown(SpendingElectricityData obj) {
        Tlog.v(TAG, " setSpendingCountdown  ");

        ResponseData responseData = MySocketDataCache.getSetSpendingCountdown(obj.mac, obj.alarmSwitch,
                (byte) obj.model, (byte) obj.year, (byte) obj.month, (byte) obj.day, obj.alarmValue);
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
    public void onTempHumiResult(String mac, float temp, float humi) {

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);

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

            Tlog.e(TAG, " onTempHumiResult:" + mTempHumi.toJsonStr());

            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);
        }

    }

    private static final int DIFF = 1000 * 32;

    @Override
    public void onScmTimeResult(String mac, boolean result, long millis) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultScmTime(mac, result, millis);
        }

        if (result) {
            //  单片机时间不对，自动帮他校准时间
            if (Math.abs(System.currentTimeMillis() - millis) > DIFF) {
                // update scm time
                ScmDevice scmData = mScmDeviceUtils.containScmDevice(mac);

                if (scmData != null && !scmData.isUpdateScmTime()) {
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

        switch (model) {

            case SocketSecureKey.Model.ALARM_MODEL_HUMIDITY:

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onResultSetHumidityAlarm(mac, result, startup, limit);
                }

                break;

            case SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE:

                if (result) {

                    final TempHumidityData mTempHumi = mScmDeviceUtils.getScmDevice(mac).getTempHumidityData();

//                    Tlog.v(TAG, " onSetTempHumiAlarmResult useCacheData update limit: " +limit);

                    if (mTempHumi.mTemperature.typeIsHot((byte) limit)) {
                        mTempHumi.mTemperature.hotAlarmSwitch = startup;
                        mTempHumi.useSetTempHotAlarmData();
                    } else if (mTempHumi.mTemperature.typeIsCode((byte) limit)) {
                        mTempHumi.mTemperature.codeAlarmSwitch = startup;
                        mTempHumi.useSetTempCodeAlarmData();
                    }
                }

                if (mScmResultCallBack != null) {
                    mScmResultCallBack.onResultSetTemperatureAlarm(mac, result, startup, limit);
                }
                break;

        }

    }


    @Override
    public void onQueryTemperatureResult(String mac, boolean result, Temperature mTemperature) {

        if (mScmResultCallBack != null) {

//            Tlog.e(TAG," onQueryTemperatureResult:" + mTemperature.toString());

            final TempHumidityData mTempHumi = mScmDeviceUtils.getScmDevice(mac).getTempHumidityData();
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

            Tlog.e(TAG, " onQueryTemperatureResult:" + mTempHumi.toJsonStr());

            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);
        }

    }

    @Override
    public void onQueryHumidityResult(String mac, boolean result, Humidity mHumidity) {

        final TempHumidityData mTempHumi = mScmDeviceUtils.getScmDevice(mac).getTempHumidityData();
        if (result) {
            mTempHumi.mHumidity.alarmSwitch = mHumidity.alarmSwitch;
            mTempHumi.mHumidity.alarmValue = mHumidity.alarmValue;
            mTempHumi.mHumidity.currentValue = mHumidity.currentValue;
        }
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultQueryTemperatureHumidityData(mac, mTempHumi);
        }
    }


    @Override
    public void onQueryTimingResult(String mac, boolean result, TimingListData mData) {

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

    @Override
    public void onSetTimingResult(String mac, boolean result) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onResultSetTiming(mac, result);
        }
    }

    @Override
    public void onTimingCommonExecuteResult(String mac, TimingCommonData mData) {
        // 定时功能生效，回调js继电器的状态
        if (mData != null) {
            onRelayResult(mac, mData.isOn());
        }
    }


    @Override
    public void onTimingAdvanceExecuteResult(String mac, TimingAdvanceData mAdvanceData) {
        if (mAdvanceData != null) {
            onRelayResult(mac, mAdvanceData.on);
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
    public void onQueryHistoryCountResult(boolean result, QueryHistoryCount mCount) {
        if (mScmResultCallBack != null) {
            mScmResultCallBack.onQueryHistoryCountResult(result, mCount);
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
            mScmResultCallBack.onResultSettingMonetaryUnit(mac, result);
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

        String mac = mWiFiDevice.getMac();

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        scmDevice.putIp(mWiFiDevice.getIp());

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
                    Toast.makeText(app, Integer.toHexString(what) + mFailTask.description, Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    @Override
    public void onSuccess(String mac, byte type, byte cmd, int seq) {
        //...

        final int what = (type & 0xFF) << 8 | ((cmd - 1) & 0xFF);

        // 上报数据不参与重发机制
        if (type == SocketSecureKey.Type.TYPE_REPORT || type == SocketSecureKey.Type.TYPE_ERROR) {
            if (Debuger.isDebug) {
                Tlog.e(TAG, " receive report Data mac:" + mac + " what:" + Integer.toHexString(what) + " seq:" + seq);
            }
            return;
        }

        // 发现数据是用FF的mac发送的，回来的数据是设备的mac;
        if ((type == SocketSecureKey.Type.TYPE_SYSTEM && cmd == SocketSecureKey.Cmd.CMD_DISCOVERY_DEVICE_RESPONSE)) {
            if (Debuger.isDebug) {
                Tlog.e(TAG, " receive discovery Data mac:" + mac + " what:" + Integer.toHexString(what) + " seq:" + seq);
            }
            return;
        }

        if (Debuger.isDebug) {
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