package cn.com.startai.socket.sign.js;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.js.jsInterface.Add;
import cn.com.startai.socket.sign.js.jsInterface.ColourLamp;
import cn.com.startai.socket.sign.js.jsInterface.Countdown;
import cn.com.startai.socket.sign.js.jsInterface.Device;
import cn.com.startai.socket.sign.js.jsInterface.DeviceList;
import cn.com.startai.socket.sign.js.jsInterface.Error;
import cn.com.startai.socket.sign.js.jsInterface.Language;
import cn.com.startai.socket.sign.js.jsInterface.Login;
import cn.com.startai.socket.sign.js.jsInterface.Main;
import cn.com.startai.socket.sign.js.jsInterface.NightLight;
import cn.com.startai.socket.sign.js.jsInterface.ReName;
import cn.com.startai.socket.sign.js.jsInterface.Router;
import cn.com.startai.socket.sign.js.jsInterface.Setting;
import cn.com.startai.socket.sign.js.jsInterface.SpendingCountdown;
import cn.com.startai.socket.sign.js.jsInterface.State;
import cn.com.startai.socket.sign.js.jsInterface.StatusBar;
import cn.com.startai.socket.sign.js.jsInterface.Store;
import cn.com.startai.socket.sign.js.jsInterface.TemperatureAndHumidity;
import cn.com.startai.socket.sign.js.jsInterface.Timing;
import cn.com.startai.socket.sign.js.jsInterface.USBSwitch;
import cn.com.startai.socket.sign.js.jsInterface.User;
import cn.com.startai.socket.sign.js.jsInterface.Version;
import cn.com.startai.socket.sign.js.jsInterface.Weather;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/16 0016
 * desc :
 */
public abstract class AbsJsManager implements Device.IJSDeviceCallBack,//
        Main.IJSMainCallBack, Router.IJSRouterCallBack, Countdown.IJSCountdownCallBack,//
        TemperatureAndHumidity.ITemperatureHumidityCallBack, Timing.IJSTimingCallBack,//
        Setting.IJSSettingCallBack, Language.IJSLanguageCallBack, Add.IJSAddCallBack,//
        DeviceList.IJSDeviceListCallBack, Login.IJSLoginCallBack,//
        ReName.IJSRenameCallBack, SpendingCountdown.IJSSpendingCallBack, Error.IJSErrorCallBack,//
        User.IJSUserCallBack, Store.IJSStoreCallBack, State.IJSStateCallBack,//
        StatusBar.IJSStatusBarCallBack, Version.IJSVersionCallBack,
        ColourLamp.IJSColourLampCallBack, NightLight.IJSNightLightCallBack,
        USBSwitch.IJSUSBSwitchCallBack, Weather.IJSWeatherCallBack {

    public abstract void regJsManagerCallBack(IJSManagerCallback mJSManagerCallBack);

    /**
     * author: Guoqiang_Sun
     * date : 2018/4/16 0016
     * desc :
     */
    public interface IJSManagerCallback {

        void onJSTurnOnDevice();

        void onJSStopScan();

        void onJSAddDevices();

        void onJSConDevice(String mac);

        void onJSDisconDevice(String mac);

        void onJSPublishSensorData(String mac, boolean publish);

        void onJSSwitchRelay(String mac, boolean status);

        void onJSQueryRelayStatus(String mac);

        void onJSFinish();

        void onJSFinishBefore();

        void onJSPowerCountdown(PowerCountdown powerCountdown);

        void onJSSetTempHumidityAlarm(TempHumidityAlarmData mAlarm);

        void onJSQueryTempHumidityData(String mac);

        void onJSQueryCountdownData(String mac);

        void onJSQueryTimingData(String mac);

        void onJSSetCommonTiming(TimingCommonData mTimingCommonData);

        void onJSSetAdvanceTiming(TimingAdvanceData mTimingAdvanceData);

        void onJSProductDetectionNearStep(String mac, int step);

        void onJSSkipProductDetection(String mac);

        void onJSQueryTime(String mac);

        void onJSSetVoltageAlarmValue(String mac, int value);

        void onJSSetCurrentAlarmValue(String mac, int value);

        void onJSSetPowerAlarmValue(String mac, int value);

        void onJSSetTemperatureUnit(String mac, int unit);

        void onJSSetMonetaryUnit(String mac, int unit);

        void onJSSetElectricityPrice(String mac, int value);

        void onJSRecoveryScm(String mac);

        void onJSQueryVoltageAlarmValue(String mac);

        void onJSQueryCurrentAlarmValue(String mac);

        void onJSQueryPowerAlarmValue(String mac);

        void onJSQueryTemperatureUnit(String mac);

        void onJSQueryMonetaryUnit(String mac);

        void onJSQueryElectricityPrice(String mac);

        void onJSSetLanguage(String lan);

        void onJSRequestLanguage();

        void onJSIsWiFiCon();

        void onJSReqConWiFiSSID();

        void onJSConfigureWiFi(WiFiConfig mConfig);

        void onJSStopConfigureWiFi();

        void onJSRequestDeviceList();

        void onJSThirdLogin(String type);

        void onJSGetMobileLoginCode(String phone, int type);

        void onJSMobileLogin(MobileLogin obj);

        void onJSRename(RenameBean obj);

        void onJSQuerySpendingElectricity(String mac);

        void onJSSetSpendingElectricity(SpendingElectricityData obj);

        void onJSControlWiFiDevice(LanDeviceInfo obj);

        void onJSRequestBleState();

        void onJSRequestIsFirstBinding();

        void onJSReconDevice(String mac);

        void onJSH5Error(String msg);


        void onJSQueryBackupTimeDirectory(String mac);

        void onJSSaveBackupData(String mac, String jsonData);

        void onJSRecoveryData(String mac);

        void onJSDiscoveryLanDevice();

        void onJSCloseDiscoveryLanDevice();

        void onJSBindLanDevice(LanBindInfo mLanBindInfo);

        void onJSIsLogin();


        void onJSDisControlWiFiDevice(String mac);

        void onJSUnbindingDevice(String mac);

        void onJSIsLoginOut();

        void onJSEmailLogin(MobileLogin mLogin);

        void onJSEmailRegister(UserRegister obj);

        void onJSQuickControlRelay(String mac, boolean on);

        void onJSDisableGoBack(boolean status);

        void onJSQuickQueryRelayStatus(String mac);

        void onJSUpdateUserPwd(UserUpdateInfo mPwd);

        void onJSCheckIsLatestVersion();

        void onJSUpdateApp();

        void onJSUpdateUserName(UserUpdateInfo obj);

        void onJSRequestTakePhoto();

        void onJSRequestLocalPhoto();

        void onJSQueryUserInformation();

        void onJSQueryVersion();

        void onJSCancelUpdate();

        void onJSEmailForgot(String email);

        void onJSGoToMall(String path);

        void onJSQueryHistoryCount(QueryHistoryCount mQueryCount);

        void onJSSetStatusBar(StatusBarBean mStatusBar);

        void onJSQueryCostRate(String mac);

        void onJSQueryCumuParam(String mac);

        void onJSQueryScmVersion(String mac);

        void onJSUpdateScm(String mac);

        void onJSWxLogin();

        void onJSSetColourLampRGB(ColorLampRGB obj);

        void onJSQueryUSBState(String mac);

        void onJSSetUSBState(String mac, boolean state);

        void onJSTHSetTemperatureTimingAlarm(TimingTempHumiData obj);

        void onJSTHQueryTemperatureTimingAlarm(String mac, int model);

        void onJSQueryColourLampRGB(String mac);

        void onJSTurnColourLamp(String mac, boolean state);

        void onJSTQueryComTimingListData(String mac);

        void onJSTQueryAdvTimingListData(String mac);

        void onJSSetNightLightTiming(NightLightTiming mNightLightTiming);

        void onJSSetNightLightWisdom(NightLightTiming mNightLightTiming);

        void onJSQueryNightLight(String mac);

        void onJSSetNightLight(String mac, boolean b);

        void onJSQueryRunningNightLight(String mac);

        void onJSShakeNightLight(String mac, boolean b);

        void onJSQueryShakeNightLight(String mac);

        void onJSUpdateNickName(String nickName);

        void onJSAliLogin();

        void onJSBindWX();

        void onJSBindAli();

        void onJSBindPhone(MobileBind mMobileBind);

        void onJSQueryWeather();

        void onJSQueryLocationEnabled();

        void onJSEnableLocation();

        void onJSUnBindWX();

        void onJSUnBindAli();

        void onJSSetNightLightColor(ColorLampRGB obj);

        void onJSQueryWeatherByIp();

        void onJSCallPhone(String phone);

        void onJSQueryIndicatorState(String mac);

        void onJSControlIndicatorState(String obj, boolean b);

        void queryTemperatureSensor(String mac);

        void onJSScanQRCode();

        void onJSQueryTotalElectric(SpendingElectricityData obj);

        void onJSResendEmail(String email);

        void onJSBindThird(String type);

        void onJSQueryMachineState(String mac);

        void onJSQueryElectricQuantity(String mac);

        void onJSQueryBleDevice(String mac);

        void onJSSkipWiFi();

    }

}
