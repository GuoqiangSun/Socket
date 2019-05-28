package cn.com.startai.socket.sign.hardware.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.hardware.WiFi.impl.NetworkManager;
import cn.com.startai.socket.sign.hardware.ble.impl.BleManager;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolInput;
import cn.com.swain.support.protocolEngine.pack.ResponseData;

/**
 * author: Guoqiang_Sun
 * date: 2019/1/17 0017
 * Desc:
 */
public class HardwareManager extends AbsHardwareManager {

    private BleManager mBleManager;
    private NetworkManager mNetworkManager;

    public NetworkManager getNetworkManager() {
        return mNetworkManager;
    }

    public HardwareManager(Application app) {

        if (CustomManager.getInstance().isTriggerBle()) {
            // 协议输出，jsToBle,
            mBleManager = new BleManager(app);

        } else if (CustomManager.getInstance().isTriggerWiFi()) {
            mNetworkManager = new NetworkManager(app);

        } else if (CustomManager.getInstance().isGrowroomate()) {
            mNetworkManager = new NetworkManager(app);

        } else if (CustomManager.getInstance().isMUSIK()) {
            mNetworkManager = new NetworkManager(app);

        } else if (CustomManager.getInstance().isTestProject()) {
            mNetworkManager = new NetworkManager(app);
            mBleManager = new BleManager(app);
        } else if (CustomManager.getInstance().isAirtempNBProject()) {
            mNetworkManager = new NetworkManager(app);
        }

    }

    @Override
    public void onSCreate() {

        if (mBleManager != null) {
            mBleManager.onSCreate();
        }

        if (mNetworkManager != null) {
            mNetworkManager.onSCreate();
        }

    }

    @Override
    public void onSResume() {
        if (mBleManager != null) {
            mBleManager.onSResume();
        }
        if (mNetworkManager != null) {
            mNetworkManager.onSResume();
        }
    }

    @Override
    public void onSPause() {
        if (mBleManager != null) {
            mBleManager.onSPause();
        }
        if (mNetworkManager != null) {
            mNetworkManager.onSPause();
        }
    }

    @Override
    public void onSFinish() {
        if (mBleManager != null) {
            mBleManager.onSFinish();
        }
        if (mNetworkManager != null) {
            mNetworkManager.onSFinish();
        }
    }

    @Override
    public void onSDestroy() {
        if (mBleManager != null) {
            mBleManager.onSDestroy();
        }
        if (mNetworkManager != null) {
            mNetworkManager.onSDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mNetworkManager != null) {
            mNetworkManager.onActivityResult(requestCode, resultCode, data);
        }
        if (mBleManager != null) {
            mBleManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onWxLoginResult(BaseResp baseResp) {
        if (mNetworkManager != null) {
            mNetworkManager.onWxLoginResult(baseResp);
        }
    }

    @Override
    public void regIProtocolInput(IDataProtocolInput mReceives) {
        if (mBleManager != null) {
            mBleManager.regIProtocolInput(mReceives);
        }
        if (mNetworkManager != null) {
            mNetworkManager.regIProtocolInput(mReceives);
        }
    }

    @Override
    public void onOutputProtocolData(ResponseData mResponseData) {
        if (mBleManager != null) {
            mBleManager.onOutputProtocolData(mResponseData);
        }
        if (mNetworkManager != null) {
            mNetworkManager.onOutputProtocolData(mResponseData);
        }
    }

    @Override
    public void onBroadcastProtocolData(ResponseData mResponseData) {
        if (mBleManager != null) {
            mBleManager.onBroadcastProtocolData(mResponseData);
        }
        if (mNetworkManager != null) {
            mNetworkManager.onBroadcastProtocolData(mResponseData);
        }
    }

    @Override
    public boolean isBleEnabled() {
        if (mBleManager != null) {
            return mBleManager.isBleEnabled();
        }
        return false;
    }

    @Override
    public boolean enableBle() {
        if (mBleManager != null) {
            return mBleManager.enableBle();
        }
        return false;
    }

    @Override
    public void scanningBle() {
        if (mBleManager != null) {
            mBleManager.scanningBle();
        }
    }

    @Override
    public void stopScanningBle() {
        if (mBleManager != null) {
            mBleManager.stopScanningBle();
        }
    }

    @Override
    public void connectBle(String mac) {
        if (mBleManager != null) {
            mBleManager.connectBle(mac);
        }
    }

    @Override
    public void disconnectBle(String mac) {
        if (mBleManager != null) {
            mBleManager.disconnectBle(mac);
        }
    }

    @Override
    public void regIBleResultCallBack(IBleResultCallBack mHWCallBack) {
        if (mBleManager != null) {
            mBleManager.regIBleResultCallBack(mHWCallBack);
        }
    }

    @Override
    public void reconDevice(String mac) {
        if (mBleManager != null) {
            mBleManager.reconDevice(mac);
        }
    }

    @Override
    public void requestIsFirstBinding() {
        if (mBleManager != null) {
            mBleManager.requestIsFirstBinding();
        }
    }

    @Override
    public void queryWiFiConnectState() {
        if (mNetworkManager != null) {
            mNetworkManager.queryWiFiConnectState();
        }
    }

    @Override
    public void queryConWiFiSSID() {
        if (mNetworkManager != null) {
            mNetworkManager.queryConWiFiSSID();
        }
    }

    @Override
    public void regWiFiResultCallBack(IWiFiResultCallBack mResultCallBack) {
        if (mNetworkManager != null) {
            mNetworkManager.regWiFiResultCallBack(mResultCallBack);
        }
    }

    @Override
    public void configureWiFi(WiFiConfig mConfig) {
        if (mNetworkManager != null) {
            mNetworkManager.configureWiFi(mConfig);
        }
    }

    @Override
    public void stopConfigureWiFi() {
        if (mNetworkManager != null) {
            mNetworkManager.stopConfigureWiFi();
        }
    }

    @Override
    public void lanDeviceDiscovery(LanDeviceInfo mDevice) {
        if (mNetworkManager != null) {
            mNetworkManager.lanDeviceDiscovery(mDevice);
        }
    }

    @Override
    public void controlWiFiDevice(LanDeviceInfo obj) {
        if (mNetworkManager != null) {
            mNetworkManager.controlWiFiDevice(obj);
        }
    }

    @Override
    public void queryBindDeviceList() {
        if (mNetworkManager != null) {
            mNetworkManager.queryBindDeviceList();
        }
    }

    @Override
    public void loginMobile(MobileLogin mLogin) {
        if (mNetworkManager != null) {
            mNetworkManager.loginMobile(mLogin);
        }
    }

    @Override
    public void getMobileLoginCode(String phone, int type) {
        if (mNetworkManager != null) {
            mNetworkManager.getMobileLoginCode(phone, type);
        }
    }

    @Override
    public String getLoginUserID() {
        if (mNetworkManager != null) {
            return mNetworkManager.getLoginUserID();
        }
        return null;
    }

    @Override
    public void discoveryLanDevice() {
        if (mNetworkManager != null) {
            mNetworkManager.discoveryLanDevice();
        }
    }

    @Override
    public void closeDiscoveryLanDevice() {
        if (mNetworkManager != null) {
            mNetworkManager.closeDiscoveryLanDevice();
        }
    }

    @Override
    public void onDeviceResponseLanBind(boolean result, LanBindingDevice mLanBindingDevice) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseLanBind(result, mLanBindingDevice);
        }
    }

    @Override
    public void isLogin() {
        if (mNetworkManager != null) {
            mNetworkManager.isLogin();
        }
    }

    @Override
    public void disControlDevice(String mac) {
        if (mNetworkManager != null) {
            mNetworkManager.disControlDevice(mac);
        }
    }

    @Override
    public void onDeviceResponseRename(String id, String name) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseRename(id, name);
        }
    }

    @Override
    public void unbindingDevice(String mac) {
        if (mNetworkManager != null) {
            mNetworkManager.unbindingDevice(mac);
        }
    }

    @Override
    public void heartbeatLose(String mac, int loseTimes) {
        if (mNetworkManager != null) {
            mNetworkManager.heartbeatLose(mac, loseTimes);
        }
    }

    @Override
    public void receiveHeartbeat(String mac, boolean result) {
        if (mNetworkManager != null) {
            mNetworkManager.receiveHeartbeat(mac, result);
        }
    }

    @Override
    public void onDeviceResponseRelaySwitch(String mac, boolean status) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseRelaySwitch(mac, status);
        }
    }

    @Override
    public void loginOut() {
        if (mNetworkManager != null) {
            mNetworkManager.loginOut();
        }
    }

    @Override
    public void onTokenInvalid(String mac) {
        if (mNetworkManager != null) {
            mNetworkManager.onTokenInvalid(mac);
        }
    }

    @Override
    public void emailLogin(MobileLogin mLogin) {
        if (mNetworkManager != null) {
            mNetworkManager.emailLogin(mLogin);
        }
    }

    @Override
    public void emailRegister(UserRegister obj) {
        if (mNetworkManager != null) {
            mNetworkManager.emailRegister(obj);
        }
    }

    @Override
    public void onDeviceResponseLanUnBind(boolean result, LanBindingDevice mLanBindingDevice) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseLanUnBind(result, mLanBindingDevice);
        }
    }

    @Override
    public void onDeviceResponseToken(String mac, int token) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseToken(mac, token);
        }
    }

    @Override
    public void onDeviceResponseConnect(boolean result, String id) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseConnect(result, id);
        }
    }

    @Override
    public void onDeviceResponseSleep(boolean result, String id) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseSleep(result, id);
        }
    }

    @Override
    public void onDeviceResponseDisconnect(boolean result, String id) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseDisconnect(result, id);
        }
    }

    @Override
    public int getToken(String mac) {
        if (mNetworkManager != null) {
            mNetworkManager.getToken(mac);
        }
        return 0;
    }

    @Override
    public void bindingDevice(LanBindInfo mLanBindInfo) {
        if (mNetworkManager != null) {
            mNetworkManager.bindingDevice(mLanBindInfo);
        }
    }

    @Override
    public void updateUserPwd(UserUpdateInfo mPwd) {
        if (mNetworkManager != null) {
            mNetworkManager.updateUserPwd(mPwd);
        }
    }

    @Override
    public void checkIsLatestVersion() {
        if (mNetworkManager != null) {
            mNetworkManager.checkIsLatestVersion();
        }
    }

    @Override
    public void updateApp() {
        if (mNetworkManager != null) {
            mNetworkManager.updateApp();
        }
    }

    @Override
    public void cancelUpdate() {
        if (mNetworkManager != null) {
            mNetworkManager.cancelUpdate();
        }
    }

    @Override
    public void updateUserName(UserUpdateInfo obj) {
        if (mNetworkManager != null) {
            mNetworkManager.updateUserName(obj);
        }
    }

    @Override
    public void takePhoto() {
        if (mNetworkManager != null) {
            mNetworkManager.takePhoto();
        }
    }

    @Override
    public void localPhoto() {
        if (mNetworkManager != null) {
            mNetworkManager.localPhoto();
        }
    }

    @Override
    public void queryUserInfo() {
        if (mNetworkManager != null) {
            mNetworkManager.queryUserInfo();
        }
    }

    @Override
    public void emailForgot(String email) {
        if (mNetworkManager != null) {
            mNetworkManager.emailForgot(email);
        }
    }

    @Override
    public String getNameByMac(String mac) {
        if (mNetworkManager != null) {
            return mNetworkManager.getNameByMac(mac);
        }
        return null;
    }

    @Override
    public void wxLogin() {
        if (mNetworkManager != null) {
            mNetworkManager.wxLogin();
        }
    }

    @Override
    public void aliLogin() {
        if (mNetworkManager != null) {
            mNetworkManager.aliLogin();
        }
    }

    @Override
    public void onDeviceUpdateResult(boolean result, UpdateVersion mVersion) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceUpdateResult(result, mVersion);
        }
    }

    @Override
    public void onDeviceResponseDeviceSSID(String id, int rssi, String ssid) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseDeviceSSID(id, rssi, ssid);
        }
    }

    @Override
    public void onDeviceResponseNightLightState(String id, boolean on) {
        if (mNetworkManager != null) {
            mNetworkManager.onDeviceResponseNightLightState(id, on);
        }
    }

    @Override
    public void setShakeNightLight(String mac, boolean b) {
        if (mNetworkManager != null) {
            mNetworkManager.setShakeNightLight(mac, b);
        }
    }

    @Override
    public void queryShakeNightLight(String mac) {
        if (mNetworkManager != null) {
            mNetworkManager.queryShakeNightLight(mac);
        }
    }

    @Override
    public void updateNickName(String nickName) {
        if (mNetworkManager != null) {
            mNetworkManager.updateNickName(nickName);
        }
    }

    @Override
    public void bindWX() {
        if (mNetworkManager != null) {
            mNetworkManager.bindWX();
        }
    }

    @Override
    public void bindAli() {
        if (mNetworkManager != null) {
            mNetworkManager.bindAli();
        }
    }

    @Override
    public void bindPhone(MobileBind mMobileBind) {
        if (mNetworkManager != null) {
            mNetworkManager.bindPhone(mMobileBind);
        }
    }

    @Override
    public void requestWeather() {
        if (mNetworkManager != null) {
            mNetworkManager.requestWeather();
        }
    }

    @Override
    public void queryLocationEnabled() {
        if (mNetworkManager != null) {
            mNetworkManager.queryLocationEnabled();
        }
        if (mBleManager != null) {
            mBleManager.queryLocationEnabled();
        }
    }

    @Override
    public void enableLocation() {
        if (mNetworkManager != null) {
            mNetworkManager.enableLocation();
        }
        if (mBleManager != null) {
            mBleManager.enableLocation();
        }
    }

    @Override
    public void unbindWX() {
        if (mNetworkManager != null) {
            mNetworkManager.unbindWX();
        }
    }

    @Override
    public void unbindAli() {
        if (mNetworkManager != null) {
            mNetworkManager.unbindAli();
        }
    }

    @Override
    public void queryWeatherByIp() {
        if (mNetworkManager != null) {
            mNetworkManager.queryWeatherByIp();
        }
    }

    @Override
    public void callPhone(String phone) {
        if (mNetworkManager != null) {
            mNetworkManager.callPhone(phone);
        }
    }

    @Override
    public void thirdLogin(Activity act, String type) {
        if (mNetworkManager != null) {
            mNetworkManager.thirdLogin(act, type);
        }
    }

    @Override
    public void scanQRCode(Activity act) {
        if (mNetworkManager != null) {
            mNetworkManager.scanQRCode(act);
        }
    }

    @Override
    public void resendEmail(String email) {
        if (mNetworkManager != null) {
            mNetworkManager.resendEmail(email);
        }
    }

    @Override
    public void bindThird(String type, Activity activity) {
        if (mNetworkManager != null) {
            mNetworkManager.bindThird(type, activity);
        }
    }

    @Override
    public void skipWiFi(Activity act) {
        if (mNetworkManager != null) {
            mNetworkManager.skipWiFi(act);
        }
    }

}
