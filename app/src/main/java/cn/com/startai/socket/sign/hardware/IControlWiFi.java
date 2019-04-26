package cn.com.startai.socket.sign.hardware;

import android.app.Activity;
import android.content.Intent;

import cn.com.startai.socket.mutual.js.bean.JsUserInfo;
import cn.com.startai.socket.mutual.js.bean.JsWeatherInfo;
import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.UpdateProgress;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.DisplayDeviceList;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public interface IControlWiFi {

    void queryWiFiConnectState();

    void queryConWiFiSSID();

    void regWiFiResultCallBack(IWiFiResultCallBack mResultCallBack);

    void configureWiFi(WiFiConfig mConfig);

    void stopConfigureWiFi();

    void lanDeviceDiscovery(LanDeviceInfo mDevice);

    void controlWiFiDevice(LanDeviceInfo obj);

    void queryBindDeviceList();

    void loginMobile(MobileLogin mLogin);

    void getMobileLoginCode(String phone, int type);

    String getLoginUserID();

    void discoveryLanDevice();

    void closeDiscoveryLanDevice();

    void onDeviceResponseLanBind(boolean result, LanBindingDevice mLanBindingDevice);

    void isLogin();

    void disControlDevice(String mac);

    void onDeviceResponseRename(String id, String name);

    void unbindingDevice(String mac);

    void heartbeatLose(String mac, int loseTimes);

    void receiveHeartbeat(String mac, boolean result);

    void onDeviceResponseRelaySwitch(String mac, boolean status);

    void loginOut();

    void onTokenInvalid(String mac);

    void emailLogin(MobileLogin mLogin);

    void emailRegister(UserRegister obj);

    void onDeviceResponseLanUnBind(boolean result, LanBindingDevice mLanBindingDevice);

    void onDeviceResponseToken(String mac, int token);

    void onDeviceResponseConnect(boolean result, String id);

    void onDeviceResponseSleep(boolean result, String id);

    void onDeviceResponseDisconnect(boolean result, String id);

    int getToken(String mac);

    void bindingDevice(LanBindInfo mLanBindInfo);

    void updateUserPwd(UserUpdateInfo mPwd);

    void checkIsLatestVersion();

    void updateApp();

    void cancelUpdate();

    void updateUserName(UserUpdateInfo obj);

    void takePhoto();

    void localPhoto();

    void queryUserInfo();

    void emailForgot(String email);

    String getNameByMac(String mac);

    void wxLogin();

    void aliLogin();

    void onDeviceUpdateResult(boolean result, UpdateVersion mVersion);

    void onDeviceResponseDeviceSSID(String id, int rssi, String ssid);

    void onDeviceResponseNightLightState(String id, boolean on);

    void setShakeNightLight(String mac, boolean b);

    void queryShakeNightLight(String mac);

    void updateNickName(String nickName);

    void bindWX();

    void bindAli();

    void bindPhone(MobileBind mMobileBind);

    void requestWeather();

    void queryLocationEnabled();

    void enableLocation();

    void unbindWX();

    void unbindAli();

    void queryWeatherByIp();

    void callPhone(String phone);

    void thirdLogin(Activity act, String type);

    void scanQRCode(Activity act);

    void resendEmail(String email);


    interface IWiFiResultCallBack {

        void onResultWiFiConState(boolean state);

        void onResultConWiFiSSID(String ssid);

        void onResultConfigureWiFi(boolean result);

        void onResultDeviceConfigureWiFi(boolean result, String mac);

        void onResultSocketInit(boolean result);

        void onResultWiFiDeviceListDisplay(DisplayDeviceList mList);

        void onResultLanDeviceListDisplay(boolean result, DisplayDeviceList mList);

        void onResultWiFiDeviceConnected(boolean result, LanDeviceInfo mDevice);

        void onResultMobileLogin(boolean result, String errorCode);

        void onResultEmailLogin(boolean result, String errorCode);

        void onResultBindDevice(boolean result, String mac);

        void onResultGetMobileLoginCode(boolean result, int type);

        void onResultNetworkChange(String type, int state);

        void onResultIsLogin(boolean result);

        void onResultWiFiDeviceDisConnected(boolean b, String mac);

        void onResultUnbind(boolean b, String mac);

        void onResultLogout(boolean b);

        void onResultLanComModel(boolean result, String mac);

        void onResultMsgSendError(String errorCode);

        void onResultEmailRegister(boolean result, String errorCode);

        void onResultServerConnectState(boolean b, String s);

        void onResultNeedRequestToken(String mac, String userID);

        void onResultCanControlDevice(String mac, String loginUserID, int token);

        void onResultAppSleep(String mac, String loginUserID, int token);

        void onResultStateQuickControlRelay(String mac, boolean status);

        void onResultUpdatePwd(boolean result, String errorCode);

        void onResultIsLatestVersion(boolean result, String errorCode, boolean isLatest);

        void onResultModifyUserInformation(boolean result, JsUserInfo mUserInfo);

        void onResultStartActivityForResult(Intent intent, int requestPhotoCode);

        void onResultGetUserInfo(boolean result, JsUserInfo mUserInfo);

        void onResultUpdateProgress(boolean result, UpdateProgress mProgress);

        void onResultModifyHeadLogo(boolean result);

        void onResultEmailForgot(boolean result);

        void onResultNeedReBind(String mac);

        void onResultWxLogin(boolean b, String errcode);

        void onResultShakeNightLight(String mac, boolean b);

        void onResultAliLogin(boolean b, String errcode);

        void onResultBindPhone(boolean b);

        void onResultWeatherInfo(JsWeatherInfo jsWeatherInfo);

        void onResultLocationEnabled(boolean b);

        void onResultUnbindWX(boolean b);

        void onResultUnbindAli(boolean b);

        void onResultCallPhone(boolean b);

        void onResultScanQRCode(boolean b, String scanResult);

        void onResultThirdLogin(boolean b, String errcode);

        void onResultResendEmail(boolean b);
    }

}
