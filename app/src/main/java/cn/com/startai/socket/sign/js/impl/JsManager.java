package cn.com.startai.socket.sign.js.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.com.startai.socket.app.view.CrossWebView;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.js.AbsJsManager;
import cn.com.startai.socket.sign.js.jsInterface.Add;
import cn.com.startai.socket.sign.js.jsInterface.Countdown;
import cn.com.startai.socket.sign.js.jsInterface.Device;
import cn.com.startai.socket.sign.js.jsInterface.DeviceList;
import cn.com.startai.socket.sign.js.jsInterface.Error;
import cn.com.startai.socket.sign.js.jsInterface.Language;
import cn.com.startai.socket.sign.js.jsInterface.Login;
import cn.com.startai.socket.sign.js.jsInterface.Main;
import cn.com.startai.socket.sign.js.jsInterface.Network;
import cn.com.startai.socket.sign.js.jsInterface.ReName;
import cn.com.startai.socket.sign.js.jsInterface.Router;
import cn.com.startai.socket.sign.js.jsInterface.Setting;
import cn.com.startai.socket.sign.js.jsInterface.SpendingCountdown;
import cn.com.startai.socket.sign.js.jsInterface.State;
import cn.com.startai.socket.sign.js.jsInterface.StatusBar;
import cn.com.startai.socket.sign.js.jsInterface.Store;
import cn.com.startai.socket.sign.js.jsInterface.TemperatureAndHumidity;
import cn.com.startai.socket.sign.js.jsInterface.Timing;
import cn.com.startai.socket.sign.js.jsInterface.User;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.jsInterface.AbsJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/11 0011
 * desc :
 */

public class JsManager extends AbsJsManager implements IService {

    public JsManager() {

    }

    private IJSManagerCallback mJSManagerCallBack;

    @Override
    public void regJsManagerCallBack(IJSManagerCallback mJSManagerCallBack) {
        this.mJSManagerCallBack = mJSManagerCallBack;
    }


    private JmHandler mHandler;

    private ArrayList<AbsJsInterface> mJSInterfaces = new ArrayList<>();

    @Override
    public void onSCreate() {

        Tlog.v(H5Config.TAG, "JsManager onSCreate()");

        Looper workLooper = LooperManager.getInstance().getWorkLooper();
        this.mHandler = new JmHandler(this, workLooper);

        mJSInterfaces.clear();
        //
        mJSInterfaces.add(0, new Setting(this));
        mJSInterfaces.add(1, new Device(this));
        mJSInterfaces.add(2, new Router(this));
        mJSInterfaces.add(3, new Main(workLooper, this));
        mJSInterfaces.add(4, new Countdown(workLooper, this));
        mJSInterfaces.add(5, new TemperatureAndHumidity(workLooper, this));
        mJSInterfaces.add(6, new Timing(workLooper, this));
        mJSInterfaces.add(7, new SpendingCountdown(workLooper, this));
        //
        mJSInterfaces.add(8, new Add(workLooper, this));
        mJSInterfaces.add(9, new Language(workLooper, this));
        mJSInterfaces.add(10, new Login(workLooper, this));
        mJSInterfaces.add(11, new DeviceList(workLooper, this));
        mJSInterfaces.add(12, new ReName(workLooper, this));
        mJSInterfaces.add(13, new Error(workLooper, this));
        mJSInterfaces.add(14, new Network());
        mJSInterfaces.add(15, new User(workLooper, this));
        mJSInterfaces.add(16, new Store(workLooper, this));
        mJSInterfaces.add(17, new State(workLooper, this));
        mJSInterfaces.add(18, new StatusBar(workLooper, this));

    }

    @Override
    public void onSResume() {
        Tlog.v(H5Config.TAG, "JsManager onSResume()");
    }

    @Override
    public void onSPause() {
        Tlog.v(H5Config.TAG, "JsManager onSPause()");
    }

//    private Data mData= new Data();

    public void regJsInterface(CrossWebView mWebView) {
//        mWebView.addJavascriptInterface(mData, Data.NAME_JSI);

        if (mJSInterfaces != null) {
            for (int i = 0; i < mJSInterfaces.size(); i++) {
                AbsJsInterface mJsInterface = mJSInterfaces.get(i);
                if (mJsInterface != null && mJsInterface.getName() != null && mWebView != null) {
                    mWebView.addJavascriptInterface(mJsInterface.getJsInterface(), mJsInterface.getName());
                }
            }
        }

    }

    @Override
    public void onSDestroy() {
        Tlog.v(H5Config.TAG, "JsManager onSDestroy()");
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
        }

        if (mJSInterfaces != null) {
            for (int i = 0; i < mJSInterfaces.size(); i++) {
                AbsJsInterface mJsInterface = mJSInterfaces.get(i);
                if (mJsInterface != null) {
                    mJsInterface.release();
                }
            }
        }
    }

    @Override
    public void onSFinish() {
        Tlog.v(H5Config.TAG, "JsManager onSFinish()");
    }

    @Override
    public void onJSDAddDevices() {
        mHandler.sendEmptyMessage(MSG_WHAT_ADD_DEVICES);
    }

    @Override
    public void onJSDStopScan() {
        mHandler.sendEmptyMessage(MSG_WHAT_STOP_SCAN);
    }

    @Override
    public void onJSDTurnOnBle() {
        mHandler.sendEmptyMessage(MSG_WHAT_TURN_ON_BLE);
    }

    @Override
    public void onJSDSwitchBle(String mac, boolean con) {
        if (con) {
            mHandler.obtainMessage(MSG_WHAT_CON_HW, mac).sendToTarget();
        } else {
            mHandler.obtainMessage(MSG_WHAT_DISCON_BLE, mac).sendToTarget();
        }
    }

    @Override
    public void onJSDPublishSensorData(String mac, boolean publish) {

        if (publish) {
            mHandler.obtainMessage(MSG_PUBLISH_SENSOR_DATA, mac).sendToTarget();
        } else {
            mHandler.obtainMessage(MSG_NOT_PUBLISH_SENSOR_DATA, mac).sendToTarget();
        }

    }

    @Override
    public void onJSDRequestBleState() {
        mHandler.sendEmptyMessage(MSG_REQUEST_BLE_STATE);
    }

    @Override
    public void onJSDRequestIsFirstBinding() {
        mHandler.sendEmptyMessage(MSG_IS_FIRST_BINDING);
    }

    @Override
    public void onJSDRequestReconnectDevice(String mac) {
        mHandler.obtainMessage(MSG_RECONNECT_DEVICE, mac).sendToTarget();
    }

    @Override
    public void onJSMSwitchRelay(String mac, boolean status) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSwitchRelay(mac, status);
        }

    }

    @Override
    public void onJSMQueryRelayStatus(String mac) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryRelayStatus(mac);
        }

    }

    private long mFirstSetupTs;
    private long mLastSetupTs;
    private int mStepUpPressTimes = 0;
    private static final int SETUP_CLICK_DELAY = 1000 * 6;
    private static final int MAX_SETUP_PRESS_COUNT = 7;
    private static final int NOTICE_PRESS_SETUP = 4;

    @Override
    public void onJSMSystemSetup(String mac) {

        long allDiff = System.currentTimeMillis() - mFirstSetupTs;

        if (allDiff > 0 && allDiff <= SETUP_CLICK_DELAY && ++mStepUpPressTimes >= MAX_SETUP_PRESS_COUNT) {
            // finish
            mStepUpPressTimes = 0;
            Message message = mHandler.obtainMessage(MSG_SKIP_PRODUCT_DETECTION, mac);
            mHandler.sendMessageDelayed(message, 100);

        } else if (Math.abs(allDiff) > SETUP_CLICK_DELAY) {
            // 间隔超过6s
            mStepUpPressTimes = 1;

        } else {
            // 间隔小于6s,并且没结束
            long mLastDiff = System.currentTimeMillis() - mLastSetupTs;
            if (Math.abs(mLastDiff) > 1000 * 2) {
                // 最近两次间隔大于2s
                mStepUpPressTimes = 1;
            }
        }

        int productDetectionNearStep = (MAX_SETUP_PRESS_COUNT - mStepUpPressTimes);
        if (productDetectionNearStep <= NOTICE_PRESS_SETUP && productDetectionNearStep > 0) {
            // before skip
            mHandler.obtainMessage(MSG_PRODUCT_DETECTION_NEAR_STEP, productDetectionNearStep, productDetectionNearStep, mac).sendToTarget();
        }

        if (mStepUpPressTimes == 1) {
            mFirstSetupTs = System.currentTimeMillis();
        }
        mLastSetupTs = System.currentTimeMillis();
    }

    private long mLastFinishTs;
    private int mFinishTimes = 0;
    private static final int FINISH_DELAY = 1000 * 2;
    private static final int MAX_FINISH_PRESS_COUNT = 2;

    @Override
    public void onJSRFinish() {

        long diff = System.currentTimeMillis() - mLastFinishTs;
        if (diff > 0 && diff <= FINISH_DELAY && ++mFinishTimes >= MAX_FINISH_PRESS_COUNT) {
            // finish
            mFinishTimes = 0;
            mHandler.sendEmptyMessage(MSG_WHAT_FINISH);
        } else if (Math.abs(diff) > FINISH_DELAY) {
            mFinishTimes = 1;
        }

        if ((MAX_FINISH_PRESS_COUNT - mFinishTimes) == 1) {
            mHandler.sendEmptyMessage(MSG_WHAT_FINISH_BEFORE);
        }

        mLastFinishTs = System.currentTimeMillis();
    }

    @Override
    public void onJSRDisableGoBack(boolean status) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSDisableGoBack(status);
        }
    }


    @Override
    public void onJSCQueryCountdownData(String mac) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryCountdownData(mac);
        }

    }

    @Override
    public void onJSCPowerCountdown(PowerCountdown mPowerCountDown) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSPowerCountdown(mPowerCountDown);
        }

    }

    @Override
    public void onJSTHQueryTempHumidity(String mac) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryTempHumidityData(mac);
        }

    }

    @Override
    public void onJSTHSetTemperatureAlarmValue(TempHumidityAlarmData obj) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetTempHumidityAlarm(obj);
        }
    }

    @Override
    public void onJSTHSetHumidityAlarmValue(TempHumidityAlarmData obj) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetTempHumidityAlarm(obj);
        }
    }


    @Override
    public void onJSTSetCommonTiming(TimingCommonData mTimingCommonData) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetCommonTiming(mTimingCommonData);
        }

    }

    @Override
    public void onJSTSetPatternTiming(TimingAdvanceData mTimingAdvanceData) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetAdvanceTiming(mTimingAdvanceData);
        }
    }

    @Override
    public void onJSTQueryTimingListData(String mac) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryTimingData(mac);
        }

    }

    public void onJSQueryScmTime(String mac) {
        mHandler.obtainMessage(MSG_QUERY_SCM_TIME, mac).sendToTarget();
    }

    @Override
    public void onJSSetVoltageAlarm(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_VOLTAGE_ALARM_VALUE, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetCurrentAlarm(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_CURRENT_ALARM_VALUE, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetPowerAlarm(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_POWER_ALARM_VALUE, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetTemperatureUnit(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_UNIT_TEMPERATURE, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetMonetaryUnit(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_UNIT_MONETARY, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetLocalElectricity(String mac, int value) {
        mHandler.obtainMessage(MSG_SET_PRICES_ELECTRICITY, value, value, mac).sendToTarget();
    }

    @Override
    public void onJSSetRecovery(String mac) {
        mHandler.obtainMessage(MSG_SET_RECOVERY_SCM, mac).sendToTarget();
    }

    @Override
    public void onJSQueryVoltageAlarmValue(String mac) {
        mHandler.obtainMessage(MSG_QUERY_VOLTAGE_ALARM_VALUE, mac).sendToTarget();
    }

    @Override
    public void onJSQueryCurrentAlarmValue(String mac) {
        mHandler.obtainMessage(MSG_QUERY_CURRENT_ALARM_VALUE, mac).sendToTarget();
    }

    @Override
    public void onJSQueryPowerAlarmValue(String mac) {
        mHandler.obtainMessage(MSG_QUERY_POWER_ALARM_VALUE, mac).sendToTarget();
    }

    @Override
    public void onJSQueryTemperatureUnit(String mac) {
        mHandler.obtainMessage(MSG_QUERY_TEMPERATURE_UNIT, mac).sendToTarget();
    }

    @Override
    public void onJSQueryMonetaryUnit(String mac) {
        mHandler.obtainMessage(MSG_QUERY_MONETARY_UNIT, mac).sendToTarget();
    }

    @Override
    public void onJSQueryElectricityPrices(String mac) {
        mHandler.obtainMessage(MSG_QUERY_ELECTRICITY_PRICE, mac).sendToTarget();
    }

    @Override
    public void onJSQueryBackupTimeDirectory(String mac) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryBackupTimeDirectory(mac);
        }

    }

    @Override
    public void onJSSaveBackupData(String mac, String jsonData) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSaveBackupData(mac, jsonData);
        }
    }

    @Override
    public void onJSRecoveryData(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRecoveryData(mac);
        }
    }

    @Override
    public void onJSLSetLanguage(String type) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetLanguage(type);
        }
    }

    @Override
    public void onJSLRequestSystemLanguage() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRequestLanguage();
        }
    }

    @Override
    public void onJSAIsWiFiCon() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSIsWiFiCon();
        }
    }

    @Override
    public void onJSAReqConWiFiSsid() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSReqConWiFiSSID();
        }
    }

    @Override
    public void onJSAConfigureWiFi(WiFiConfig mConfig) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSConfigureWiFi(mConfig);
        }
    }

    @Override
    public void onJSAStopConfigureWiFi() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSStopConfigureWiFi();
        }
    }

    @Override
    public void onJSADiscoveryLanDevice() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSDiscoveryLanDevice();
        }
    }

    @Override
    public void onJSACloseDiscoveryLanDevice() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSCloseDiscoveryLanDevice();
        }
    }

    @Override
    public void onJSABindLanDevice(LanBindInfo mLanBindInfo) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSBindLanDevice(mLanBindInfo);
        }
    }

    @Override
    public void onJSDeviceListRequest() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRequestDeviceList();
        }
    }

    @Override
    public void onJSControlDevice(LanDeviceInfo mWiFiDevice) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSControlWiFiDevice(mWiFiDevice);
        }
    }

    @Override
    public void onJSDisControlDevice(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSDisControlWiFiDevice(mac);
        }
    }


    @Override
    public void onJSUnbindingDevice(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSUnbindingDevice(mac);
        }
    }

    @Override
    public void onJSQuickControlRelay(String mac, boolean on) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQuickControlRelay(mac, on);
        }
    }

    @Override
    public void onJSQuickQueryRelay(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQuickQueryRelayStatus(mac);
        }
    }

    @Override
    public void onJSThirdLogin(String type) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSThirdLogin(type);
        }
    }

    @Override
    public void onJSMobileLogin(MobileLogin mLogin) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSMobileLogin(mLogin);
        }

    }

    @Override
    public void onJSEmailLogin(MobileLogin mLogin) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSEmailLogin(mLogin);
        }
    }

    @Override
    public void onJSGetMobileLoginCode(String phone) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSGetMobileLoginCode(phone);
        }
    }

    @Override
    public void onJSIsLogin() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSIsLogin();
        }
    }

    @Override
    public void onJSLoginOut() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSIsLoginOut();
        }
    }

    @Override
    public void onJSEmailRegister(UserRegister obj) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSEmailRegister(obj);
        }
    }

    @Override
    public void onJSEmailForgot(String email) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSEmailForgot(email);
        }
    }

    @Override
    public void onJSRename(RenameBean mDisplayDevice) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRename(mDisplayDevice);
        }
    }

    @Override
    public void onJSQuerySpendingCountdownData(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQuerySpendingElectricity(mac);
        }
    }

    @Override
    public void onJSSetSpendingCountdownAlarm(SpendingElectricityData mSpendingCountdownData) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetSpendingElectricity(mSpendingCountdownData);
        }
    }

    @Override
    public void onJSError(String msg) {

        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSH5Error(msg);
        }

    }


    @Override
    public void onJSUpdateUserPwd(UserUpdateInfo mPwd) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSUpdateUserPwd(mPwd);
        }
    }

    @Override
    public void onJSCheckIsLatestVersion() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSCheckIsLatestVersion();
        }
    }

    @Override
    public void onJSUpdateApp() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSUpdateApp();
        }
    }

    @Override
    public void onJSUpdateUserName(UserUpdateInfo obj) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSUpdateUserName(obj);
        }
    }

    @Override
    public void onJSRequestTakePhoto() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRequestTakePhoto();
        }
    }

    @Override
    public void onJSRequestLocalPhoto() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSRequestLocalPhoto();
        }
    }

    @Override
    public void onJSQueryUserInformation() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryUserInformation();
        }
    }

    @Override
    public void onJSQueryVersion() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryVersion();
        }
    }

    @Override
    public void onJSCancelUpdate() {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSCancelUpdate();
        }
    }

    @Override
    public void onJSGoToMall(String path) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSGoToMall(path);
        }
    }

    @Override
    public void onJSQueryHistoryCount(QueryHistoryCount mQueryCount) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryHistoryCount(mQueryCount);
        }
    }

    @Override
    public void onJSQueryCostRate(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryCostRate(mac);
        }
    }

    @Override
    public void onJSQueryCumuParam(String mac) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSQueryCumuParam(mac);
        }
    }

    @Override
    public void onJSSetStatusBar(StatusBarBean mStatusBar) {
        if (mJSManagerCallBack != null) {
            mJSManagerCallBack.onJSSetStatusBar(mStatusBar);
        }
    }


    private static class JmHandler extends Handler {
        private final WeakReference<JsManager> wr;

        JmHandler(JsManager jm, Looper mLooper) {
            super(mLooper);
            wr = new WeakReference<>(jm);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            JsManager jm;
            if (wr != null && (jm = wr.get()) != null) {
                jm.handleMessage(msg);
            } else {
                Tlog.e(H5Config.TAG, " JmHandler AndJsBridge==null");
            }

        }
    }


    //************************//

    private static final int MSG_WHAT_FINISH_BEFORE = 0x01;

    private static final int MSG_WHAT_FINISH = 0x02;

    private static final int MSG_WHAT_ADD_DEVICES = 0x03;

    private static final int MSG_WHAT_TURN_ON_BLE = 0x04;

    private static final int MSG_WHAT_STOP_SCAN = 0x05;

    private static final int MSG_WHAT_CON_HW = 0x06;

    private static final int MSG_WHAT_DISCON_BLE = 0x07;

    private static final int MSG_PUBLISH_SENSOR_DATA = 0x08;

    private static final int MSG_NOT_PUBLISH_SENSOR_DATA = 0x09;


    private static final int MSG_SKIP_PRODUCT_DETECTION = 0x13;

    private static final int MSG_PRODUCT_DETECTION_NEAR_STEP = 0x14;

    private static final int MSG_QUERY_SCM_TIME = 0x15;

    private static final int MSG_SET_VOLTAGE_ALARM_VALUE = 0x16;
    private static final int MSG_SET_CURRENT_ALARM_VALUE = 0x17;
    private static final int MSG_SET_POWER_ALARM_VALUE = 0x18;
    private static final int MSG_SET_UNIT_TEMPERATURE = 0x19;
    private static final int MSG_SET_UNIT_MONETARY = 0x1A;
    private static final int MSG_SET_PRICES_ELECTRICITY = 0x1B;
    private static final int MSG_SET_RECOVERY_SCM = 0x1C;

    private static final int MSG_QUERY_VOLTAGE_ALARM_VALUE = 0x1D;
    private static final int MSG_QUERY_CURRENT_ALARM_VALUE = 0x1E;
    private static final int MSG_QUERY_POWER_ALARM_VALUE = 0x1F;

    private static final int MSG_QUERY_TEMPERATURE_UNIT = 0x20;
    private static final int MSG_QUERY_MONETARY_UNIT = 0x21;
    private static final int MSG_QUERY_ELECTRICITY_PRICE = 0x22;


    private static final int MSG_REQUEST_BLE_STATE = 0x31;
    private static final int MSG_IS_FIRST_BINDING = 0x32;
    private static final int MSG_RECONNECT_DEVICE = 0x33;


    private void handleMessage(Message msg) {

        switch (msg.what) {

            case MSG_WHAT_FINISH:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSFinish();
                }
                break;

            case MSG_WHAT_FINISH_BEFORE:

                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSFinishBefore();
                }

                break;

            case MSG_WHAT_ADD_DEVICES:

                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSAddDevices();
                }

                break;
            case MSG_WHAT_TURN_ON_BLE:

                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSTurnOnDevice();
                }

                break;
            case MSG_WHAT_STOP_SCAN:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSStopScan();
                }
                break;
            case MSG_WHAT_CON_HW:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSConDevice((String) msg.obj);
                }
                break;
            case MSG_WHAT_DISCON_BLE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSDisconDevice((String) msg.obj);
                }
                break;

            case MSG_PUBLISH_SENSOR_DATA:

                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSPublishSensorData((String) msg.obj, true);
                }

                break;

            case MSG_NOT_PUBLISH_SENSOR_DATA:

                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSPublishSensorData((String) msg.obj, false);
                }

                break;


            case MSG_SKIP_PRODUCT_DETECTION:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSkipProductDetection((String) msg.obj);
                }
                break;

            case MSG_PRODUCT_DETECTION_NEAR_STEP:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSProductDetectionNearStep((String) msg.obj, msg.arg1);
                }
                break;

            case MSG_QUERY_SCM_TIME:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryTime((String) msg.obj);
                }
                break;

            case MSG_SET_VOLTAGE_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetVoltageAlarmValue((String) msg.obj, msg.arg1);
                }
                break;
            case MSG_SET_CURRENT_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetCurrentAlarmValue((String) msg.obj, msg.arg1);
                }
                break;
            case MSG_SET_POWER_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetPowerAlarmValue((String) msg.obj, msg.arg1);
                }
                break;
            case MSG_SET_UNIT_TEMPERATURE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetTemperatureUnit((String) msg.obj, msg.arg1);
                }
                break;

            case MSG_SET_UNIT_MONETARY:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetMonetaryUnit((String) msg.obj, msg.arg1);
                }
                break;

            case MSG_SET_PRICES_ELECTRICITY:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSSetElectricityPrice((String) msg.obj, msg.arg1);
                }
                break;

            case MSG_SET_RECOVERY_SCM:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSRecoveryScm((String) msg.obj);
                }
                break;

            case MSG_QUERY_VOLTAGE_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryVoltageAlarmValue((String) msg.obj);
                }
                break;
            case MSG_QUERY_CURRENT_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryCurrentAlarmValue((String) msg.obj);
                }
                break;
            case MSG_QUERY_POWER_ALARM_VALUE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryPowerAlarmValue((String) msg.obj);
                }
                break;
            case MSG_QUERY_TEMPERATURE_UNIT:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryTemperatureUnit((String) msg.obj);
                }
                break;
            case MSG_QUERY_MONETARY_UNIT:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryMonetaryUnit((String) msg.obj);
                }
                break;
            case MSG_QUERY_ELECTRICITY_PRICE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSQueryElectricityPrice((String) msg.obj);
                }
                break;


            case MSG_REQUEST_BLE_STATE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSRequestBleState();
                }
                break;

            case MSG_IS_FIRST_BINDING:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSRequestIsFirstBinding();
                }
                break;

            case MSG_RECONNECT_DEVICE:
                if (mJSManagerCallBack != null) {
                    mJSManagerCallBack.onJSReconDevice((String) msg.obj);
                }
                break;

        }

    }


}
