package cn.com.startai.socket.mutual.js.impl;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.db.gen.DaoSession;
import cn.com.startai.socket.db.gen.PowerCountdownDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.mutual.js.AbsAndJsBridge;
import cn.com.startai.socket.mutual.js.IAndJSCallBack;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.DisplayBleDevice;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.mutual.js.bean.UpdateProgress;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.DisplayDeviceList;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.mutual.js.xml.LocalData;
import cn.com.startai.socket.sign.js.JsUserInfo;
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
import cn.com.startai.socket.sign.js.jsInterface.Setting;
import cn.com.startai.socket.sign.js.jsInterface.SpendingCountdown;
import cn.com.startai.socket.sign.js.jsInterface.State;
import cn.com.startai.socket.sign.js.jsInterface.TemperatureAndHumidity;
import cn.com.startai.socket.sign.js.jsInterface.Timing;
import cn.com.startai.socket.sign.js.jsInterface.USBSwitch;
import cn.com.startai.socket.sign.js.jsInterface.User;
import cn.com.startai.socket.sign.js.jsInterface.Version;
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
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.startai.socket.sign.scm.bean.sensor.SensorData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.TempHumidityData;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.file.FileUtil;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/4 0004
 * desc :
 */

public class AndJsBridge extends AbsAndJsBridge implements IService {

    private final IAndJSCallBack mCallBack;
    private final Application app;


    public AndJsBridge(Application app, IAndJSCallBack mCallBack) {
        this.app = app;
        this.mCallBack = mCallBack;
    }

    @Override
    public void onSCreate() {
        Tlog.v(TAG, " AndJsBridge onSCreate()");
    }

    @Override
    public void onSResume() {
        Tlog.v(TAG, " AndJsBridge onSResume()");
    }

    @Override
    public void onSPause() {
        Tlog.v(TAG, " AndJsBridge onSPause()");
    }

    @Override
    public void onSDestroy() {
        Tlog.v(TAG, " AndJsBridge onSDestroy()");

        if (finishToast != null) {
            finishToast.cancel();
        }
    }

    @Override
    public void onSFinish() {
        Tlog.v(TAG, " AndJsBridge onSFinish()");
    }

    @Override
    public void onJSFinish() {
        Tlog.v(TAG, " onJSFinish() ");
        if (mCallBack != null) {
            mCallBack.ajMainGoBack();
        }
    }

    private Toast finishToast;

    @Override
    public void onJSFinishBefore() {
        Tlog.v(TAG, " onJSFinishBefore() ");
        finishToast = Toast.makeText(app, R.string.goBack_again, Toast.LENGTH_SHORT);
        finishToast.show();
    }

    @Override
    public void onJSPowerCountdown(PowerCountdown powerCountdown) {
        Tlog.v(TAG, " onJSPowerCountdown() ");

        DaoSession daoSession = DBManager.getInstance().getDaoSession();
        PowerCountdownDao powerCountdownDao = daoSession.getPowerCountdownDao();
        QueryBuilder<PowerCountdown> where = powerCountdownDao.queryBuilder()
                .where(PowerCountdownDao.Properties.Mac.eq(powerCountdown.getMac()));

        List<PowerCountdown> list;
        if (where != null && (list = where.list()) != null && list.size() > 0) {
            Long id = list.get(0).getId();
            powerCountdown.setId(id);
            powerCountdownDao.update(powerCountdown);
            Tlog.v(TAG, "powerCountdownDao update : " + id);
        } else {
            long insert = powerCountdownDao.insert(powerCountdown);
            Tlog.v(TAG, "powerCountdownDao insert : " + insert);
        }

        if (mScmVirtual != null) {
            mScmVirtual.setPowerCountdown(powerCountdown);
        }

    }

    @Override
    public void onJSSetTempHumidityAlarm(TempHumidityAlarmData mAlarm) {
        Tlog.v(TAG, " onJSSetTempHumidityAlarm() ");
        if (mScmVirtual != null) {
            mScmVirtual.setTempHumidityAlarm(mAlarm);
        }
    }

    @Override
    public void onJSQueryTempHumidityData(String mac) {
        Tlog.v(TAG, " onJSQueryTempHumidityData() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryTempHumidityData(mac);
        }
    }

    @Override
    public void onJSQueryCountdownData(String mac) {
        Tlog.v(TAG, " onJSQueryCountdownData() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryCountdownData(mac);
        }
    }

    @Override
    public void onJSQueryTimingData(String mac) {
        Tlog.v(TAG, " onJSQueryTimingData() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryTimingData(mac);
        }
    }

    @Override
    public void onJSSetCommonTiming(TimingCommonData mTimingCommonData) {
        Tlog.v(TAG, " onJSSetCommonTiming() ");
        if (mScmVirtual != null) {
            mScmVirtual.setCommonTiming(mTimingCommonData);
        }
    }

    @Override
    public void onJSSetAdvanceTiming(TimingAdvanceData mTimingAdvanceData) {
        Tlog.v(TAG, " onJSSetAdvanceTiming() ");
        if (mScmVirtual != null) {
            mScmVirtual.setAdvanceTiming(mTimingAdvanceData);
        }
    }

    private Toast toast;

    @Override
    public void onJSProductDetectionNearStep(String mac, int step) {
        Tlog.v(TAG, " onJSProductDetectionNearStep() mac: " + mac + " step:" + step);
        if (app != null) {
            String mNearStep = app.getResources().getString(R.string.jump_product_detection_near_step);
            String msg = mNearStep.replace("$step", String.valueOf(step));

            if (toast == null) {
                toast = Toast.makeText(app, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    @Override
    public void onJSSkipProductDetection(String mac) {
        Tlog.v(TAG, " onJSSkipProductDetection() mac: " + mac);
        if (toast != null) {
            toast.cancel();
        }
        if (mCallBack != null) {
            mCallBack.skipProductDetection(mac);
        }
    }

    @Override
    public void onJSQueryTime(String mac) {
        Tlog.v(TAG, " onJSQueryTime() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryScmTime(mac);
        }
    }

    @Override
    public void onJSSetVoltageAlarmValue(String mac, int value) {
        Tlog.v(TAG, " onJSSetVoltageAlarmValue() " + value);
        if (mScmVirtual != null) {
            mScmVirtual.setSetVoltageAlarmValue(mac, value);
        }
    }

    @Override
    public void onJSSetCurrentAlarmValue(String mac, int value) {
        Tlog.v(TAG, " onJSSetCurrentAlarmValue() " + value);
        if (mScmVirtual != null) {
            mScmVirtual.setSetCurrentAlarmValue(mac, value);
        }
    }

    @Override
    public void onJSSetPowerAlarmValue(String mac, int value) {
        Tlog.v(TAG, " onJSSetPowerAlarmValue() " + value);
        if (mScmVirtual != null) {
            mScmVirtual.setSetPowerAlarmValue(mac, value);
        }
    }

    @Override
    public void onJSSetTemperatureUnit(String mac, int unit) {
        Tlog.v(TAG, " onJSSetTemperatureUnit() " + unit);
        if (mScmVirtual != null) {
            mScmVirtual.setSetTemperatureUnit(mac, unit);
        }
    }

    @Override
    public void onJSSetMonetaryUnit(String mac, int unit) {
        Tlog.v(TAG, " onJSSetMonetaryUnit() " + unit);
        if (mScmVirtual != null) {
            mScmVirtual.setSetMonetaryUnit(mac, unit);
        }
    }

    @Override
    public void onJSSetElectricityPrice(String mac, int value) {
        Tlog.v(TAG, " onJSSetElectricityPrice() " + value);
        if (mScmVirtual != null) {
            mScmVirtual.setSetElectricityPrice(mac, value);
        }
    }

    @Override
    public void onJSRecoveryScm(String mac) {
        Tlog.v(TAG, " onJSRecoveryScm() ");
        if (mScmVirtual != null) {
            mScmVirtual.setSetRecoveryScm(mac);
        }
    }

    @Override
    public void onJSQueryVoltageAlarmValue(String mac) {
        Tlog.v(TAG, " onJSQueryVoltageAlarmValue() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryVoltageAlarmValue(mac);
        }
    }

    @Override
    public void onJSQueryCurrentAlarmValue(String mac) {
        Tlog.v(TAG, " onJSQueryCurrentAlarmValue() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryCurrentAlarmValue(mac);
        }
    }

    @Override
    public void onJSQueryPowerAlarmValue(String mac) {
        Tlog.v(TAG, " onJSQueryPowerAlarmValue() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryPowerAlarmValue(mac);
        }
    }

    @Override
    public void onJSQueryTemperatureUnit(String mac) {
        Tlog.v(TAG, " onJSQueryTemperatureUnit() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryTemperatureUnit(mac);
        }
    }

    @Override
    public void onJSQueryMonetaryUnit(String mac) {
        Tlog.v(TAG, " onJSQueryMonetaryUnit() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryMonetaryUnit(mac);
        }
    }

    @Override
    public void onJSQueryElectricityPrice(String mac) {
        Tlog.v(TAG, " onJSQueryElectricityPrice() ");
        if (mScmVirtual != null) {
            mScmVirtual.queryElectricityPrice(mac);
        }
    }

    @Override
    public void onJSSetLanguage(String lan) {
        Tlog.v(TAG, " onJSSetLanguage() " + lan);

        LocalData localData = LocalData.getLocalData(app);
        localData.setLanguage(lan);

        Language.changeLanguage(app);

        String method = Language.Method.callJsSetSystemLanguage(true, lan);
        loadJs(method);

    }

    @Override
    public void onJSRequestLanguage() {
        Tlog.v(TAG, " onJSRequestLanguage() ");

        String type = Language.changeSystemLangToH5Lang(app);
        String method = Language.Method.callJsSystemLanguage(type);
        loadJs(method);

    }

    @Override
    public void onJSIsWiFiCon() {
        Tlog.v(TAG, " onJSIsWiFiCon() ");
        if (mNetworkManager != null) {
            mNetworkManager.queryWiFiConnectState();
        }
    }

    @Override
    public void onJSReqConWiFiSSID() {
        Tlog.v(TAG, " onJSReqConWiFiSSID() ");
        if (mNetworkManager != null) {
            mNetworkManager.queryConWiFiSSID();
        }
    }

    @Override
    public void onJSConfigureWiFi(WiFiConfig mConfig) {
        Tlog.v(TAG, " onJSConfigureWiFi() ");
        if (mNetworkManager != null) {
            mNetworkManager.configureWiFi(mConfig);
        }
    }

    @Override
    public void onJSStopConfigureWiFi() {
        Tlog.v(TAG, " onJSStopConfigureWiFi() ");
        if (mNetworkManager != null) {
            mNetworkManager.stopConfigureWiFi();
        }
    }

    @Override
    public void onJSRequestDeviceList() {
        Tlog.v(TAG, " onJSRequestDeviceList() ");

//        String userID = "";
//
//        if (mNetworkManager != null) {
//            userID = mNetworkManager.getUserID();
//        }
//
//        if (mScmVirtual != null) {
//            mScmVirtual.discoveryDevice(userID);
//        }

        if (mNetworkManager != null) {
            mNetworkManager.queryBindDeviceList();
        }

    }

    @Override
    public void onJSThirdLogin(String type) {
        Tlog.v(TAG, " onJSThirdLogin() " + type);
        if (mCallBack != null) {
            mCallBack.login(type);
        }
    }

    @Override
    public void onJSGetMobileLoginCode(String phone) {
        Tlog.v(TAG, " onJSGetMobileLoginCode() " + phone);

        int random = (int) ((Math.random() * 9 + 1) * 100000);
        Tlog.v(TAG, " random:" + random);

        if (mNetworkManager != null) {
            mNetworkManager.getMobileLoginCode(phone);
        }

    }

    @Override
    public void onJSMobileLogin(MobileLogin mLogin) {
        Tlog.v(TAG, " onJSMobileLogin() " + mLogin.toString());

        if (mNetworkManager != null) {
            mNetworkManager.loginMobile(mLogin);
        }

    }

    @Override
    public void onJSRename(RenameBean obj) {
        Tlog.v(TAG, " onJSRename() " + obj.name);
        if (mScmVirtual != null) {
            mScmVirtual.rename(obj);
        }
    }

    @Override
    public void onJSQuerySpendingElectricity(String mac) {
        Tlog.v(TAG, " onJSQuerySpendingElectricity() ");
        if (mScmVirtual != null) {
            mScmVirtual.querySpendingElectricity(mac);
        }
    }

    @Override
    public void onJSSetSpendingElectricity(SpendingElectricityData obj) {
        Tlog.v(TAG, " onJSSetSpendingElectricity() ");
        if (mScmVirtual != null) {
            mScmVirtual.setSpendingCountdown(obj);
        }
    }


    @Override
    public void onJSRequestBleState() {
        Tlog.v(TAG, " onJSRequestBleState() ");

        boolean hwEnabled = false;
        if (mBleManager != null) {
            hwEnabled = mBleManager.isHWEnabled();
        }

        String method = Device.Method.callJsBleState(hwEnabled);
        loadJs(method);

    }

    @Override
    public void onJSRequestIsFirstBinding() {
        Tlog.v(TAG, " onJSRequestIsFirstBinding() ");

        if (mBleManager != null) {
            mBleManager.requestIsFirstBinding();
        }

    }

    @Override
    public void onJSReconDevice(String mac) {
        Tlog.v(TAG, " onJSReconDevice() " + mac);
        if (mBleManager != null) {
            mBleManager.reconDevice(mac);
        }
    }

    @Override
    public void onJSH5Error(String msg) {
        Tlog.v(TAG, " onJSH5Error() ");
        SocketApplication.uncaughtH5Exception(msg);
    }

    @Override
    public void onJSQueryBackupTimeDirectory(String mac) {
        Tlog.v(TAG, " onJSQueryBackupTimeDirectory() ");

        File backupFile = getFileByMac(mac);
        Tlog.v(TAG, " backupFile : " + backupFile.getAbsolutePath());

        long timestamp = 0L;
        String dir = backupFile.getParent();

        if (backupFile.exists() && backupFile.length() > 0) {
            timestamp = backupFile.lastModified();
        }

        String method = Setting.Method.callJsQueryBackupdir(mac, timestamp, dir);
        loadJs(method);

    }

    @Override
    public void onJSSaveBackupData(String mac, String jsonData) {
        Tlog.v(TAG, " onJSSaveBackupData() ");

        boolean b = false;
        if (jsonData != null) {
            File backupFile = getFileByMac(mac);
            b = FileUtil.saveFileMsg(backupFile, jsonData, false);
            Tlog.v(TAG, " onJSSaveBackupData result " + b);
        }

        String method = Setting.Method.callJsSaveBackupdir(mac, b);
        loadJs(method);
    }

    @Override
    public void onJSRecoveryData(String mac) {
        Tlog.v(TAG, " onJSRecoveryData() ");

        File backupFile = getFileByMac(mac);
        Tlog.v(TAG, " backupFile : " + backupFile.getAbsolutePath());
        String fileContent = FileUtil.getFileContent(backupFile);
        String method = Setting.Method.callJsRecoveryData(mac, fileContent);
        loadJs(method);

    }

    private File getFileByMac(String mac) {
        File dbPath = FileManager.getInstance().getDBPath();
        String backupFileName = mac.replaceAll(":", "");
        backupFileName = backupFileName.trim() + ".json";
        return new File(dbPath, backupFileName);
    }

    @Override
    public void onJSDiscoveryLanDevice() {
        Tlog.v(TAG, " onJSDiscoveryLanDevice() ");

        if (mNetworkManager != null) {
            mNetworkManager.discoveryLanDevice();
        }

    }

    @Override
    public void onJSCloseDiscoveryLanDevice() {
        Tlog.v(TAG, " onJSCloseDiscoveryLanDevice() ");
        if (mNetworkManager != null) {
            mNetworkManager.closeDiscoveryLanDevice();
        }
    }

    @Override
    public void onJSBindLanDevice(LanBindInfo mLanBindInfo) {
        Tlog.v(TAG, " onJSBindLanDevice() ");

        String loginUserID = null;
        if (mNetworkManager != null) {
            loginUserID = mNetworkManager.getLoginUserID();
            mNetworkManager.bindingDevice(mLanBindInfo);

        }

        if (mScmVirtual != null) {
            mScmVirtual.bindDevice(mLanBindInfo, loginUserID);
        }

    }

    @Override
    public void onResultNeedReBind(String mac) {
        Tlog.v(TAG, " onResultNeedReBind() ");
        LanBindInfo mLanBindInfo = new LanBindInfo();
        mLanBindInfo.mac = mac;
        onJSBindLanDevice(mLanBindInfo);
    }


    @Override
    public void onJSIsLogin() {
        Tlog.v(TAG, " onJSIsLogin() ");

        if (mNetworkManager != null) {
            mNetworkManager.isLogin();
        }

    }


    @Override
    public void onJSControlWiFiDevice(LanDeviceInfo obj) {
        Tlog.v(TAG, " onJSControlWiFiDevice() ");

        if (mNetworkManager != null) {
            mNetworkManager.controlWiFiDevice(obj);
        }

    }


    @Override
    public void onResultNeedRequestToken(String mac, String userID) {
        Tlog.v(TAG, " onResultServerConnectState() mac" + mac + " userID:" + userID);

        if (mScmVirtual != null) {
            mScmVirtual.requestToken(mac, userID);
        }

    }

    @Override
    public void onResultCanControlDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " onResultCanControlDevice() mac" + mac + " userID:" + userID + " token:" + token);
        if (mScmVirtual != null) {
            mScmVirtual.controlDevice(mac, userID, token);
        }
    }

    @Override
    public void onResultAppSleep(String mac, String userID, int token) {
        Tlog.v(TAG, " onResultAppSleep() mac" + mac + " userID:" + userID + " token:" + token);
        if (mScmVirtual != null) {
            mScmVirtual.appSleep(mac, userID, token);
        }
    }

    @Override
    public void onResultStateQuickControlRelay(String mac, boolean status) {
        Tlog.v(TAG, " onResultStateQuickControlRelay() mac" + mac + " status:" + status);
        String method = DeviceList.Method.callJsStateQuicControlRelay(mac, status);
        loadJs(method);
    }

    @Override
    public void onResultUpdatePwd(boolean result, String errorCode) {
        Tlog.v(TAG, " onResultUpdatePwd() result" + result + " errorCode:" + errorCode);
        String method = User.Method.callJsChangePwdResult(result, errorCode);
        loadJs(method);
    }

    @Override
    public void onResultIsLatestVersion(boolean result, String errorCode, boolean isLatest) {
        Tlog.v(TAG, " onResultIsLatestVersion() result" + result + " errorCode:" + errorCode);
        String method = User.Method.callJsCheckIsLaterVersionResult(result, errorCode, isLatest);
        loadJs(method);
    }

    @Override
    public void onResultModifyUserInformation(boolean result, JsUserInfo mUserInfo) {
        Tlog.v(TAG, " onResultModifyUserInformation() result " + result);
        String data = "{}";
        if (result && mUserInfo != null) {
            data = mUserInfo.toJsonStr();
        }

        String method = User.Method.callJsModifyUserInfoResult(result, data);
        loadJs(method);
    }

    @Override
    public void onResultStartActivityForResult(Intent intent, int requestPhotoCode) {
        if (mCallBack != null) {
            mCallBack.onAjStartActivityForResult(intent, requestPhotoCode);
        }
    }

    @Override
    public void onResultGetUserInfo(boolean result, JsUserInfo mUserInfo) {
        Tlog.v(TAG, " onResultGetUserInfo() " + result);

        String data = "{}";
        if (result && mUserInfo != null) {
            data = mUserInfo.toJsonStr();
        }

        String method = User.Method.callJsUserInfoResult(result, data);
        loadJs(method);

    }

    @Override
    public void onResultUpdateProgress(boolean result, UpdateProgress mProgress) {
        Tlog.v(TAG, " onResultUpdateProgress() " + result);

        String data;
        if (mProgress != null) {
            data = mProgress.toJsonStr();
        } else {
            data = "{}";
        }
        String method = User.Method.callJsUpdateResult(result, data);
        loadJs(method);
    }

    @Override
    public void onResultModifyHeadLogo(boolean result) {
        Tlog.v(TAG, " onResultModifyHeadLogo() " + result);
        String method = User.Method.callJsModifyHeadLogoResult(result);
        loadJs(method);
    }

    @Override
    public void onResultEmailForgot(boolean result) {
        Tlog.v(TAG, " onResultEmailForgot() " + result);
        String method = Login.Method.callJsEmailForgot(result);
        loadJs(method);
    }


    @Override
    public void onJSDisControlWiFiDevice(String mac) {
        Tlog.v(TAG, " onJSDisControlWiFiDevice() " + mac);

        if (mNetworkManager != null) {
            mNetworkManager.disControlDevice(mac);
        }

    }

    @Override
    public void onJSUnbindingDevice(String mac) {
        Tlog.v(TAG, " onJSUnbindingDevice() " + mac);
        String loginUserID = null;
        if (mNetworkManager != null) {
            loginUserID = mNetworkManager.getLoginUserID();
            mNetworkManager.unbindingDevice(mac);
        }

        if (mScmVirtual != null) {
            mScmVirtual.unbindDevice(mac, loginUserID);
        }
    }

    @Override
    public void onJSIsLoginOut() {
        Tlog.v(TAG, " onJSIsLoginOut() ");
        if (mNetworkManager != null) {
            mNetworkManager.loginOut();
        }
    }

    @Override
    public void onJSEmailLogin(MobileLogin mLogin) {
        Tlog.v(TAG, " onJSEmailLogin() ");
        if (mNetworkManager != null) {
            mNetworkManager.emailLogin(mLogin);
        }
    }

    @Override
    public void onJSEmailRegister(UserRegister obj) {
        if (mNetworkManager != null) {
            mNetworkManager.emailRegister(obj);
        }
    }

    @Override
    public void onJSQuickControlRelay(String mac, boolean on) {
        if (mScmVirtual != null) {
            mScmVirtual.quickControlRelay(mac, on);
        }
    }

    @Override
    public void onJSQuickQueryRelayStatus(String mac) {

        if (mScmVirtual != null) {
            mScmVirtual.quickQueryRelay(mac);
        }

    }

    @Override
    public void onJSUpdateUserPwd(UserUpdateInfo mPwd) {
        if (mNetworkManager != null) {
            mNetworkManager.updateUserPwd(mPwd);
        }
    }

    @Override
    public void onJSCheckIsLatestVersion() {


        if (mNetworkManager != null) {
            mNetworkManager.checkIsLatestVersion();
        }
    }

    @Override
    public void onJSUpdateApp() {
        if (mNetworkManager != null) {
            mNetworkManager.updateApp();
        }
    }

    @Override
    public void onJSUpdateUserName(UserUpdateInfo obj) {
        if (mNetworkManager != null) {
            mNetworkManager.updateUserName(obj);
        }
    }

    @Override
    public void onJSRequestTakePhoto() {
        if (mNetworkManager != null) {
            mNetworkManager.takePhoto();
        }


    }

    @Override
    public void onJSRequestLocalPhoto() {
        if (mNetworkManager != null) {
            mNetworkManager.localPhoto();
        }

    }

    @Override
    public void onJSQueryUserInformation() {
        if (mNetworkManager != null) {
            mNetworkManager.queryUserInfo();
        }
    }

    @Override
    public void onJSQueryVersion() {
        Application application = app;
        String versionName;
        try {
            PackageInfo packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = "1.0.0";
        }

        String method = User.Method.callJsGetVersionResult(versionName);
        loadJs(method);

    }

    @Override
    public void onJSCancelUpdate() {
        if (mNetworkManager != null) {
            mNetworkManager.cancelUpdate();
        }
    }

    @Override
    public void onJSEmailForgot(String email) {
        if (mNetworkManager != null) {
            mNetworkManager.emailForgot(email);
        }
    }

    @Override
    public void onJSGoToMall(String path) {
        if (mCallBack != null) {
            mCallBack.ajSkipWebActivity(path);
        }
    }

    @Override
    public void onJSQueryHistoryCount(QueryHistoryCount mQueryCount) {
        if (mScmVirtual != null) {
            mScmVirtual.queryHistoryCount(mQueryCount);
        }
    }

    @Override
    public void onJSSetStatusBar(StatusBarBean mStatusBar) {
        if (mCallBack != null) {
            mCallBack.ajSetStatusBar(mStatusBar);
        }
    }

    @Override
    public void onJSQueryCostRate(String mac) {
        if (mScmVirtual != null) {
            mScmVirtual.queryCostRate(mac);
        }
    }

    @Override
    public void onJSQueryCumuParam(String mac) {
        if (mScmVirtual != null) {
            mScmVirtual.queryCumuParam(mac);
        }
    }

    @Override
    public void onJSQueryScmVersion(String mac) {
        if (mScmVirtual != null) {
            mScmVirtual.queryVersion(mac);
        }
    }

    @Override
    public void onJSUpdateScm(String mac) {
        if (mScmVirtual != null) {
            mScmVirtual.update(mac);
        }
    }

    @Override
    public void onJSWxLogin() {
        if (mNetworkManager != null) {
            mNetworkManager.wxLogin();
        }
    }

    @Override
    public void onJSSetColourLampRGB(ColorLampRGB obj) {
//        if (mScmVirtual != null) {
//            mScmVirtual.setLightRGB(obj.mac, obj.seq, obj.r, obj.g, obj.b);
//        }

        if (mScmVirtual != null) {
            mScmVirtual.setLightRGB(obj);
        }
    }

    @Override
    public void onJSQueryUSBState(String mac) {
        if (mScmVirtual != null) {
            mScmVirtual.queryUSBState(mac);
        }
    }

    @Override
    public void onJSSetUSBState(String mac, boolean state) {
        if (mScmVirtual != null) {
            mScmVirtual.setUSBState(mac, state);
        }
    }

    @Override
    public void onJSDisableGoBack(boolean status) {
        if (mCallBack != null) {
            mCallBack.ajDisableGoBack(status);
        }
    }

    @Override
    public void onJSSwitchRelay(String mac, boolean status) {
        Tlog.v(TAG, " onJSSwitchRelay() mac: " + mac + " status: " + status);
        if (mScmVirtual != null) {
            mScmVirtual.switchRelay(mac, status);
        }
    }

    @Override
    public void onJSQueryRelayStatus(String mac) {
        Tlog.v(TAG, " onJSQueryRelayStatus() mac: " + mac);
        if (mScmVirtual != null) {
            mScmVirtual.queryRelayState(mac);
        }
    }


    @Override
    public void onJSPublishSensorData(String mac, boolean publish) {
        Tlog.v(TAG, " onJSPublishSensorData(): " + mac + " publish:" + publish);
        if (mScmVirtual != null) {
            mScmVirtual.publishSensorData(mac, publish);
        }
    }


    /******************/

    @Override
    public void onJSAddDevices() {
        Tlog.v(TAG, " onJSAddDevices()");

        boolean enable = false;

        if (mBleManager != null) {
            enable = mBleManager.isHWEnabled();
        }

        Tlog.v(TAG, " isHWEnable(): " + enable);

        if (enable) {
            onDeviceEnabledSkipScanView();
        } else {
            onDeviceNotEnableSkipEnableView();
        }

    }

    @Override
    public void onJSTurnOnDevice() {
        Tlog.v(TAG, " onJSTurnOnDevice()");
        if (mBleManager != null) {


            mBleManager.enableHW();
        }
    }


    @Override
    public void onResultHWEnable() {
        onDeviceEnabledSkipScanView();
    }

    @Override
    public void onResultHWNotEnable() {
        Tlog.v(TAG, " onResultHWNotEnable()");
        Toast.makeText(app, R.string.ble_forbid_enable_skip_setting, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResultScanHWIsNull() {
        Tlog.v(TAG, " onResultScanHWIsNull() ");
        String methodStr = Device.Method.callJsHWNotScanned();
        loadJs(methodStr);
    }

    @Override
    public void onResultHWDisplay(DisplayBleDevice mDevice) {
        Tlog.v(TAG, " displayDevice(): " + mDevice.name + " ; " + mDevice.address);
        String bleDataJson = mDevice.toJsonStr();
        String methodStr = Device.Method.callJsDisplayHW(bleDataJson);
        loadJs(methodStr);
    }

    /**
     * ble激活，跳转到扫描界面
     */
    private void onDeviceEnabledSkipScanView() {
        Tlog.v(TAG, " skip scan view ... ");
        String methodStr = Device.Method.callJsScanHW();
        loadJs(methodStr);
        if (mBleManager != null) {
            mBleManager.scanningHW();
        }
    }

    @Override
    public void onJSStopScan() {
        Tlog.v(TAG, " onJSStopScan()");
        if (mBleManager != null) {
            mBleManager.stopScanningHW();
        }
    }


    /**
     * 蓝牙未激活，跳转到激活界面
     */
    private void onDeviceNotEnableSkipEnableView() {
        Tlog.v(TAG, " skip enable view ... ");
        String method = Device.Method.callJsHWNotEnable();
        loadJs(method);
    }

    @Override
    public void onJSConDevice(String mac) {

        Tlog.v(TAG, " onJSConDevice() mac: " + mac);

        if (mBleManager != null) {
            mBleManager.connectHW(mac);
        }
    }

    @Override
    public void onJSDisconDevice(String mac) {

        Tlog.v(TAG, " onJSDisconDevice() mac: " + mac);

        if (mBleManager != null) {
            mBleManager.disconnectHW(mac);
        }
    }

    /******************/

    @Override
    public void onResultHWConnection(String mac, boolean state, boolean result) {
        Tlog.v(TAG, " onResultHWConnection() , mac: " + mac + "  con :" + state + " ,result:" + result);

        if (state && result) {
            if (mScmVirtual != null) {
                mScmVirtual.onConnected(mac);
            }

        } else if (!state) {// && result
            if (mScmVirtual != null) {
                mScmVirtual.onDisconnected(mac);
            }

        }

        String method = Device.Method.callJsSwitchHW(mac, state, result);
        loadJs(method);
    }

    @Override
    public void onResultHWStateOff() {
        Tlog.v(TAG, "onResultHWStateOff() ,Ble turn off ");
        String method = Device.Method.callJsHWTurn(false);
        loadJs(method);
    }

    @Override
    public void onResultHWStateOn() {
        Tlog.v(TAG, "onResultHWStateOn() , turn on ");
        String method = Device.Method.callJsHWTurn(true);
        loadJs(method);
    }

    @Override
    public void onResultIsFirstBinding(boolean b, DisplayBleDevice displayDevice) {

        String data = "";
        if (displayDevice != null) {
            data = displayDevice.toJsonStr();
        }
        String method = Device.Method.callJSFirstBinding(b, data);
        loadJs(method);
    }

    /******************/

    @Override
    public void onResultPublishSensorData(String mac, SensorData mSensorData) {
        Tlog.v(TAG, " publishSensorDataResult()");
        String sensorDataJson = mSensorData.toJsonStr();
        String method = Device.Method.callJsDisplaySensorData(mac, sensorDataJson);
        loadJs(method);
    }

    /******************/

    @Override
    public void onResultSwitchRelay(String mac, boolean status) {
        Tlog.v(TAG, " onResultSwitchRelay() mac: " + mac + " status: " + status);
        String method = Main.Method.callJsSwitchPower(mac, status);
        loadJs(method);

        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseRelaySwitch(mac, status);
        }

    }

    @Override
    public void onResultSetCountdown(String mac, boolean result, boolean status) {
        Tlog.v(TAG, " onResultCountdown() mac: " + mac + " status: " + status);
        String method = Countdown.Method.callJsPowerCountdown(mac, result, status);
        loadJs(method);
    }

    @Override
    public void onResultQueryTemperatureHumidityData(String mac, TempHumidityData mTempHumidityData) {
        Tlog.v(TAG, " onResultTemperatureHumidityData() mac: " + mac);
        String mTempHumidityStr = mTempHumidityData.toJsonStr();
        String method = TemperatureAndHumidity.Method.callJsTemperatureHumidity(mac, mTempHumidityStr);
        loadJs(method);
    }

    @Override
    public void onResultSetTemperatureAlarm(String mac, boolean result, boolean startup, int limit) {
        Tlog.v(TAG, " onResultSetTemperatureAlarm() result:" + result + " startup:" + startup + " limit:" + limit);
        String method = TemperatureAndHumidity.Method.callJsTemperatureAlarmValue(mac, startup, result, limit);
        loadJs(method);
    }

    @Override
    public void onResultSetHumidityAlarm(String mac, boolean result, boolean startup, int limit) {
        Tlog.v(TAG, " onResultSetHumidityAlarm() result:" + result + " startup:" + startup + " limit:" + limit);
        String method = TemperatureAndHumidity.Method.callJsHumidityAlarmValue(mac, startup, result);
        loadJs(method);
    }

    @Override
    public void onResultQueryCountdown(String mac, CountdownData mCountdownData) {
        Tlog.v(TAG, " onResultQueryCountdown() on:" + mCountdownData.Switchgear + " startup:" + mCountdownData.countdownSwitch);
        checkCountdownData(mCountdownData);
        String s = mCountdownData.toJsonStr();
        String method = Countdown.Method.callJsPowerCountdownData(mac, s);
        loadJs(method);
    }

    private void checkCountdownData(CountdownData mCountdownData) {

        if (!mCountdownData.countdownSwitch) {
            if (mCountdownData.allTime == -1) {
                mCountdownData.allTime = 0;
            }
        }
        if (mCountdownData.allTime == -1 && mCountdownData.mac != null) {

            DaoSession daoSession = DBManager.getInstance().getDaoSession();
            PowerCountdownDao powerCountdownDao = daoSession.getPowerCountdownDao();
            QueryBuilder<PowerCountdown> where = powerCountdownDao.queryBuilder().where(PowerCountdownDao.Properties.Mac.eq(mCountdownData.mac));

            int time = mCountdownData.hour * 60 + mCountdownData.minute;

            List<PowerCountdown> list;
            if (where != null && (list = where.list()) != null && list.size() > 0) {
                PowerCountdown powerCountdown = list.get(0);

                int hour = powerCountdown.getHour();
                int minute = powerCountdown.getMinute();
                int tAllTime = hour * 60 + minute;

                if (tAllTime >= time) {
                    mCountdownData.allTime = tAllTime;
                } else {
                    mCountdownData.allTime = time;
                    powerCountdown.setHour(mCountdownData.hour);
                    powerCountdown.setMinute(mCountdownData.minute);
                    powerCountdownDao.update(powerCountdown);
                }
            } else {

                PowerCountdown mPowerCountdown = new PowerCountdown();
                mPowerCountdown.setHour(mCountdownData.hour);
                mPowerCountdown.setMinute(mCountdownData.minute);
                mPowerCountdown.setMac(mCountdownData.mac);
                mPowerCountdown.setStatus(mCountdownData.countdownSwitch);
                mPowerCountdown.setSwitchGear(mCountdownData.Switchgear);
                powerCountdownDao.insert(mPowerCountdown);
                mCountdownData.allTime = time;
            }
        }
    }


    @Override
    public void onResultQueryTiming(String mac, TimingListData mTimingListData) {
        Tlog.v(TAG, " onResultQueryTiming() ");
        String commonStr = mTimingListData.toCommonDataJsonStr();
        String advanceStr = mTimingListData.toAdvanceJsonStr();
        String method = Timing.Method.callJsTimingListData(mac, commonStr, advanceStr);
        loadJs(method);
    }

    @Override
    public void onResultSetTiming(String mac, TimingSetResult mResult) {
        Tlog.v(TAG, " onResultSetTiming() result:" + String.valueOf(mResult));

        if (mResult == null) {
            mResult = new TimingSetResult();
        }
        String method = Timing.Method.callJsTimingCommonSet(mac,
                mResult.result, mResult.on, mResult.id, mResult.model);
        loadJs(method);
    }

    @Override
    public void onResultScmTime(String mac, boolean result, long time) {
        Tlog.v(TAG, " onResultScmTime() time:" + time);

    }

    @Override
    public void onResultSettingVoltage(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingVoltage() result:" + result);
        String method = Setting.Method.callJsSetAlarmVoltageResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingCurrent(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingCurrent() result:" + result);
        String method = Setting.Method.callJsSetAlarmCurrentResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingPower(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingPower() result:" + result);
        String method = Setting.Method.callJsSetAlarmPowerResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingTemperatureUnit(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingTemperatureUnit() result:" + result);
        String method = Setting.Method.callJsSetTemperatureUnitResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingMonetaryUnit(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingMonetaryUnit() result:" + result);
        String method = Setting.Method.callJsSetMonetaryUnitResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingElectricityPrice(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingElectricityPrice() result:" + result);
        String method = Setting.Method.callJsSetElectricityResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultSettingRecovery(String mac, boolean result) {
        Tlog.v(TAG, " onResultSettingRecovery() result:" + result);
        String method = Setting.Method.callJsSetRecoveryResult(mac, result);
        loadJs(method);
    }

    @Override
    public void onResultQueryCurrentAlarmValue(String id, boolean result, float value) {
        Tlog.v(TAG, " onResultQueryCurrentAlarmValue() result:" + result);
        String method = Setting.Method.callJsQueryCurrentAlarmValue(id, result, value);
        loadJs(method);
    }

    @Override
    public void onResultQueryElectricityPrice(String id, boolean result, int mElectricityPrices) {
        Tlog.v(TAG, " onResultQueryElectricityPrice() result:" + result);
        String method = Setting.Method.callJsQueryElectricityPrices(id, result, mElectricityPrices);
        loadJs(method);
    }

    @Override
    public void onResultQueryMonetaryUnit(String id, boolean result, int value) {
        Tlog.v(TAG, " onResultQueryMonetaryUnit() result:" + result);
        String method = Setting.Method.callJsQueryMonetaryUnit(id, result, value);
        loadJs(method);
    }

    @Override
    public void onResultQueryPowerAlarmValue(String id, boolean result, int mAlarmPowerValue) {
        Tlog.v(TAG, " onResultQueryPowerAlarmValue() result:" + result);
        String method = Setting.Method.callJsQueryPowerAlarmValue(id, result, mAlarmPowerValue);
        loadJs(method);
    }

    @Override
    public void onResultQueryTemperatureUnit(String id, boolean result, int value) {
        Tlog.v(TAG, " onResultQueryTemperatureUnit() result:" + result);
        String method = Setting.Method.callJsQueryTemperatureUnit(id, result, value);
        loadJs(method);
    }

    @Override
    public void onResultQueryVoltageAlarmValue(String id, boolean result, int mAlarmVoltageValue) {
        Tlog.v(TAG, " onResultQueryVoltageAlarmValue() result:" + result);
        String method = Setting.Method.callJsQueryVoltageAlarmValue(id, result, mAlarmVoltageValue);
        loadJs(method);
    }

    @Override
    public void onResultWiFiDeviceListDisplay(DisplayDeviceList mList) {
        Tlog.v(TAG, " onResultWiFiDeviceListDisplay() ");

        String str = mList.toJsonStr();
        String method = DeviceList.Method.callJsDeviceList(str);
        loadJs(method);

    }

    @Override
    public void onResultLanDeviceListDisplay(boolean result, DisplayDeviceList mList) {
        Tlog.v(TAG, " onResultLanDeviceListDisplay() ");

        String str = mList.toJsonStr();
        String method = Add.Method.callJsLanDeviceList(result, str);
        loadJs(method);

    }

    @Override
    public void onResultWiFiDeviceConnected(boolean result, LanDeviceInfo mDevice) {
        Tlog.v(TAG, " onResultWiFiDeviceConnected() ");

        if (result && mDevice != null) {
            if (mScmVirtual != null) {
                mScmVirtual.onConnected(mDevice.mac);
            }
        }

        String mDeviceStr;
        if (mDevice != null) {
            mDeviceStr = mDevice.toJsonObj().toString();
        } else {
            mDeviceStr = "{}";
            result = false;
        }
        String method = DeviceList.Method.callJsDeviceControl(result, mDeviceStr);
        loadJs(method);

    }


    @Override
    public void onResultWiFiDeviceDisConnected(boolean result, String mac) {
        Tlog.v(TAG, " onResultWiFiDeviceDisConnected() ");
        if (result && mScmVirtual != null) {
            mScmVirtual.onDisconnected(mac);
        }
    }

    @Override
    public void onResultUnbind(boolean result, String mac) {
        Tlog.v(TAG, " onResultUnbind() " + result);
        String method = DeviceList.Method.callJsUnbindDevice(result, mac);
        loadJs(method);

    }

    @Override
    public void onResultLogout(boolean b) {
        Tlog.v(TAG, " onResultLogout() " + b);
        String method = Login.Method.callJsIsLoginOut(b);
        loadJs(method);
    }

    @Override
    public void onResultLanComModel(boolean result, String mac) {
        Tlog.v(TAG, " onResultLanComModel() " + result);

        if (mScmVirtual != null) {
            mScmVirtual.lanComModel(result, mac);
        }
    }

    @Override
    public void onResultMsgSendError(String errorCode) {
        Tlog.v(TAG, " onResultMsgSendError() " + errorCode);
        String method = Error.Method.callJsErrorResponse(errorCode);
        loadJs(method);

    }

    @Override
    public void onResultEmailRegister(boolean result, String errorCode) {
        Tlog.v(TAG, " onResultEmailRegister() " + errorCode);
        String method = Login.Method.callJsEmailRegister(result, errorCode);
        loadJs(method);
    }

    @Override
    public void onResultServerConnectState(boolean b, String errorCode) {
        Tlog.v(TAG, " onResultServerConnectState() " + b);
        String method = Network.Method.callJsWebServerStatusResponse(b, errorCode);
        loadJs(method);
    }


    @Override
    public void onResultMobileLogin(boolean result, String errorCode) {
        Tlog.v(TAG, " onResultMobileLogin() ");

        String method = Login.Method.callJsMobileLogin(result, errorCode);
        loadJs(method);

    }

    @Override
    public void onResultWxLogin(boolean result, String errcode) {
        Tlog.v(TAG, " onResultWxLogin() " + result);
        String method = Login.Method.callJsWXLogin(result);
        loadJs(method);
    }

    @Override
    public void onResultEmailLogin(boolean result, String errorCode) {
        Tlog.v(TAG, " onResultEmailLogin() ");

        String method = Login.Method.callJsEmailLogin(result, errorCode);
        loadJs(method);
    }

    @Override
    public void onResultBindDevice(boolean result) {
        Tlog.v(TAG, " onResultBindDevice() " + result);
        String method = Add.Method.callJsBindDevice(result);
        loadJs(method);
    }

    @Override
    public void onResultGetMobileLoginCode(boolean result) {
        Tlog.v(TAG, " onResultGetMobileLoginCode() " + result);
        String method = Login.Method.callJsGetMobileLoginCodeResult(result);
        loadJs(method);
    }

    @Override
    public void onResultNetworkChange(String type, int state) {
        Tlog.v(TAG, " onResultNetworkChange() " + type);

        String method = Add.Method.callJsNetworkChange(type, state);
        loadJs(method);

        if (mScmVirtual != null) {
            mScmVirtual.onNetworkChange();
        }

    }

    @Override
    public void onResultIsLogin(boolean result) {
        Tlog.v(TAG, " onResultIsLogin() " + result);
        String method = Login.Method.callJsIsLogin(result);
        loadJs(method);
    }


    @Override
    public void onResultDeviceDiscovery(String id, boolean result, LanDeviceInfo mDevice) {
        Tlog.v(TAG, " onResultDeviceDiscovery() " + id);

        if (result && mDevice != null) {

            if (mNetworkManager != null) {
                mNetworkManager.lanDeviceDiscovery(mDevice);
            }

        } else {
            Tlog.e(TAG, " DeviceDiscovery fail ");
        }

    }

    @Override
    public void onResultRename(String id, boolean result, String name) {
        Tlog.v(TAG, " onResultRename() result:" + result);
        String method = ReName.Method.callJsRename(id, result);
        loadJs(method);

        if (result) {
            if (mNetworkManager != null) {
                mNetworkManager.onDeviceResponseRename(id, name);
            }
        }

    }

    @Override
    public void onResultQuerySpendingElectricity(String id, boolean result, SpendingElectricityData mElectricityData, SpendingElectricityData mSpendingData) {
        Tlog.v(TAG, " onResultQuerySpendingElectricity() result:" + result);

        JSONObject jsonObject = null;
        if (mElectricityData != null) {
            jsonObject = mElectricityData.toJsonObj();
        }
        JSONObject jsonObject1 = null;
        if (mSpendingData != null) {
            jsonObject1 = mSpendingData.toJsonObj();
        }

        JSONObject obj = new JSONObject();
        if (jsonObject != null) {
            try {
                obj.put(SpendingElectricityData.MODEL_POWER, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject1 != null) {
            try {
                obj.put(SpendingElectricityData.MODEL_COST, jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String json = obj.toString();
        String method = SpendingCountdown.Method.callJsSpendingCountdownData(id, result, json);
        loadJs(method);

    }

    @Override
    public void onResultSetSpendingElectricity(String id, int model, boolean alarmSwitch, boolean result) {
        Tlog.v(TAG, " onResultSetSpendingElectricity() result:" + result);
        String method = SpendingCountdown.Method.callJsSpendingCountdownAlarm(id, model, alarmSwitch, result);
        loadJs(method);
    }

    @Override
    public void onResultDeviceLanBind(boolean result, LanBindingDevice mLanBindingDevice) {
        Tlog.v(TAG, " onResultDeviceLanBind() result:" + result);

        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseLanBind(result, mLanBindingDevice);
        }

    }

    @Override
    public void onResultHeartbeatLose(String mac, int loseTimes) {
        if (mNetworkManager != null) {
            mNetworkManager.heartbeatLose(mac, loseTimes);
        }
    }

    @Override
    public void onTokenInvalid(String mac) {
        Tlog.v(TAG, " onTokenInvalid() mac:" + mac);
        if (mNetworkManager != null) {
            mNetworkManager.onTokenInvalid(mac);
        }
    }

    @Override
    public void onLanUnBindResult(boolean result, LanBindingDevice mLanBindingDevice) {
        Tlog.v(TAG, " onLanUnBindResult() result:" + result);
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseLanUnBind(result, mLanBindingDevice);
        }
    }

    @Override
    public void onResultRequestToken(String mac, int token) {
        Tlog.v(TAG, " onResultRequestToken()  mac:" + mac + " token" + token);
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseToken(mac, token);
        }

    }

    @Override
    public void onResultConnect(boolean result, String id) {
        Tlog.v(TAG, " onResultConnect()  mac:" + id + " result" + result);
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseConnect(result, id);
        }
    }

    @Override
    public void onResultSleep(boolean result, String id) {
        Tlog.v(TAG, " onResultSleep()  mac:" + id + " result" + result);
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseSleep(result, id);
        }
    }

    @Override
    public void onResultDisconnect(boolean result, String id) {
        Tlog.v(TAG, " onResultDisconnect()  mac:" + id + " result" + result);
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseDisconnect(result, id);
        }
    }

    @Override
    public void onQueryHistoryCountResult(boolean result, QueryHistoryCount mCount) {
        Tlog.v(TAG, " onQueryHistoryCountResult()   result" + result);

        String method = State.Method.callJsHistoryData(mCount.mac, mCount.startTime,
                mCount.day, mCount.interval, mCount.toJsonArrayData());
        loadJs(method);

    }

    @Override
    public void onResultQueryCostRate(boolean result, CostRate mCostRate) {
        Tlog.v(TAG, " onResultQueryCostRate()   result" + result);
        String method = State.Method.callJsQueryCostRate(mCostRate.mac, mCostRate.hour1, mCostRate.minute1,
                mCostRate.price1, mCostRate.hour2, mCostRate.minute2, mCostRate.price2);
        loadJs(method);
    }

    @Override
    public void onResultQueryCumuParams(boolean result, CumuParams cumuParams) {
        Tlog.v(TAG, " onResultQueryCumuParams()   result" + result);
        String method = State.Method.callJsCumuParams(cumuParams.mac, cumuParams.time, cumuParams.GHG, cumuParams.electricity);
        loadJs(method);
    }

    @Override
    public void onResultHeartbeatReceive(String mac, boolean result) {
        if (mNetworkManager != null) {
            mNetworkManager.receiveHeartbeat(mac, result);
        }
    }

    @Override
    public void onResultUpdateVersion(boolean result, UpdateVersion mVersion) {

        if (mNetworkManager != null) {
            mNetworkManager.onDeviceUpdateResult(result,mVersion);
        }

        if (mVersion.isQueryVersionAction()) {
            String method = Version.Method.callJsScmVersion(mVersion.mac,
                    mVersion.newVersion > mVersion.curVersion, mVersion.newVersion, mVersion.curVersion);
            loadJs(method);
        } else if (mVersion.isUpdateVersionAction()) {

            String nameByMac;
            if (mNetworkManager != null) {
                nameByMac = mNetworkManager.getNameByMac(mVersion.mac);

            } else {
                nameByMac = mVersion.mac + "-" + String.valueOf(mVersion.curVersion);
            }

            String method = Version.Method.callJsScmUpdate(mVersion.mac, nameByMac
                    , result);
            loadJs(method);

        }


    }

    @Override
    public void onResultUSBState(String id, boolean on) {
        String method = USBSwitch.Method.callJsUSBState(id, on);
        loadJs(method);
    }

    @Override
    public void onElectricityReportResult(boolean result, PointReport mElectricity) {
        String method = State.Method.callJsElecPointReport(
                mElectricity.mac, mElectricity.ts, mElectricity.toJsonStr());
        loadJs(method);
    }

    @Override
    public int getTokenFromDB(String mac) {
        if (mNetworkManager != null) {
            return mNetworkManager.getToken(mac);
        }
        return -1;
    }

    @Override
    public void loadJs(String method) {
        if (mCallBack != null) {
            mCallBack.ajLoadJs(method);
        }
    }

    @Override
    public void onResultWiFiConState(boolean state) {
        Tlog.v(TAG, " onResultWiFiConState() state:" + state);
        String method = Add.Method.callJsWifiConState(state);
        loadJs(method);
    }

    @Override
    public void onResultConWiFiSSID(String ssid) {
        Tlog.v(TAG, " onResultConWiFiSSID() ssid:" + ssid);
        String method = Add.Method.callJsWifiSsid(ssid);
        loadJs(method);
    }

    @Override
    public void onResultConfigureWiFi(boolean result) {
        Tlog.v(TAG, " onResultConfigureWiFi() :" + result);
        String method = Add.Method.callJsConfigureWifi(result);
        loadJs(method);
    }

    @Override
    public void onResultDeviceConfigureWiFi(boolean result) {
        Tlog.v(TAG, " onResultDeviceConfigureWiFi() :" + result);
        String method = Add.Method.callJsDeviceConResult(result);
        loadJs(method);
    }

    @Override
    public void onResultSocketInit(boolean result) {
        Tlog.v(TAG, " onResultSocketInit() :" + result);

    }
}
