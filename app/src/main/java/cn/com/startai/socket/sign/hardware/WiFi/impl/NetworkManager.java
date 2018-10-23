package cn.com.startai.socket.sign.hardware.WiFi.impl;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.startai.esptouchsender.IEsptouchResult;
import cn.com.startai.esptouchsender.customer.EsptouchAsyncTask;
import cn.com.startai.esptouchsender.customer.MyEsptouchListener;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8001;
import cn.com.startai.mqttsdk.busi.entity.C_0x8002;
import cn.com.startai.mqttsdk.busi.entity.C_0x8003;
import cn.com.startai.mqttsdk.busi.entity.C_0x8004;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;
import cn.com.startai.mqttsdk.busi.entity.C_0x8016;
import cn.com.startai.mqttsdk.busi.entity.C_0x8017;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.busi.entity.C_0x8021;
import cn.com.startai.mqttsdk.busi.entity.C_0x8022;
import cn.com.startai.mqttsdk.busi.entity.C_0x8023;
import cn.com.startai.mqttsdk.busi.entity.C_0x8024;
import cn.com.startai.mqttsdk.busi.entity.C_0x8025;
import cn.com.startai.mqttsdk.busi.entity.C_0x8200;
import cn.com.startai.mqttsdk.event.AOnStartaiMessageArriveListener;
import cn.com.startai.mqttsdk.event.ICommonStateListener;
import cn.com.startai.mqttsdk.event.IConnectionStateListener;
import cn.com.startai.mqttsdk.event.PersistentEventDispatcher;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.MqttInitParam;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;
import cn.com.startai.socket.db.gen.WanBindingDeviceDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.DeveloperBuilder;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.DisplayDeviceList;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.hardware.AbsWiFi;
import cn.com.startai.socket.sign.hardware.WiFi.bean.WanBindingDevice;
import cn.com.startai.socket.sign.hardware.WiFi.util.BroadcastDiscoveryUtil;
import cn.com.startai.socket.sign.hardware.WiFi.util.ControlDevice;
import cn.com.startai.socket.sign.hardware.WiFi.util.ControlDeviceUtil;
import cn.com.startai.socket.sign.js.jsInterface.Add;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.util.MySocketSecureKey;
import cn.com.swain.baselib.util.IpUtil;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolInput;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.udp.ISocketResult;
import cn.com.swain.support.udp.UdpLanCom;
import cn.com.swain.support.udp.UdpResponseMsg;
import cn.com.swain169.log.Tlog;


/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class NetworkManager extends AbsWiFi implements ISocketResult {

    private final Application app;

    private final BroadcastReceiver mNetWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Tlog.v(TAG, " android.net.conn.CONNECTIVITY_CHANGE " + intent.getAction());

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr == null) {
                Tlog.e(TAG, "ConnectivityManager==null");
                return;
            }

            boolean wifiConnected = false;
            String type;
            NetworkInfo.State state;

            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiNetworkInfo.isConnected()) {

                Tlog.v(TAG, "WIFI isConnected ");
                wifiConnected = true;
                type = wifiNetworkInfo.getTypeName();
                state = wifiNetworkInfo.getState();

            } else if (wifiNetworkInfo.isConnectedOrConnecting()) {

                Tlog.v(TAG, "WIFI isConnecting");
                type = wifiNetworkInfo.getTypeName();
                state = wifiNetworkInfo.getState();

            } else {

                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (dataNetworkInfo.isConnected()) {

                    Tlog.v(TAG, "mobile isConnected ");
                    type = dataNetworkInfo.getTypeName();
                    state = dataNetworkInfo.getState();

                } else if (dataNetworkInfo.isConnectedOrConnecting()) {

                    Tlog.v(TAG, "mobile isConnecting");
                    type = dataNetworkInfo.getTypeName();
                    state = dataNetworkInfo.getState();

                } else {

                    Tlog.v(TAG, " unknown network ");
                    type = Add.NONE;
                    state = NetworkInfo.State.UNKNOWN;

                }
            }

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            //获取所有网络连接的信息
//            Network[] networks = connMgr.getAllNetworks();
//
//            //通过循环将网络信息逐个取出来
//            for (Network network : networks) {
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//                ｝
//        }

            if (!WifiManager.NETWORK_STATE_CHANGED_ACTION.equalsIgnoreCase(intent.getAction())) {
                mDeviceManager.onNetworkStateChange();
                mControlDeviceUtil.onNetworkStateChange();
            }

            if (wifiConnected) {
                discoveryLanDevice(6);
            }

            if (mResultCallBack != null) {
                mResultCallBack.onResultNetworkChange(type, Add.changeState(state));
            }
        }
    };

    private final UserManager mUserManager;
    private final DeviceManager mDeviceManager;

    public NetworkManager(Application app) {
        this.app = app;
        this.mUserManager = new UserManager(app);
        this.mDeviceManager = new DeviceManager();
    }

    public static final String TAG = "NetworkManager";
    private WifiManager mWiFiManager;
    private UdpLanCom mUdpCom;
    private BroadcastDiscoveryUtil mBroadcastUtil;

    @Override
    public void onSCreate() {

        Tlog.v(TAG, " NetworkManager onSCreate() ");

        // mqtt
        PersistentEventDispatcher.getInstance().registerOnTunnelStateListener(mConnectionStateListener);
        PersistentEventDispatcher.getInstance().registerOnPushListener(mComMessageListener);

        MqttInitParam mqttInitParam = null;

        if (CustomManager.getInstance().getCustom() == MySocketSecureKey.Custom.CUSTOM_WAN) {
            mqttInitParam = DeveloperBuilder.buildMqttInitParam(new DeveloperBuilder.WiFiSocketDeveloper());
        } else if (CustomManager.getInstance().getCustom() == MySocketSecureKey.Custom.CUSTOM_STARTAI) {
            mqttInitParam = DeveloperBuilder.buildMqttInitParam(new DeveloperBuilder.SuperSocketDeveloper());
        }

        if (mqttInitParam == null) {
            throw new NullPointerException(" no custom ");
        }

        StartAI.getInstance().initialization(app, mqttInitParam);

        mUserManager.onSCreate();
        mDeviceManager.onSCreate();

        mWiFiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
//        WifiManager.calculateSignalLevel()

        Looper workLooper = LooperManager.getInstance().getWorkLooper();
        mUdpCom = new UdpLanCom(workLooper, this);
        mUdpCom.init();

        mBroadcastUtil = new BroadcastDiscoveryUtil(workLooper, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        app.registerReceiver(mNetWorkStateReceiver, filter);

    }


    @Override
    public void onSResume() {
        Tlog.v(TAG, " NetworkManager onSResume() ");
        mUserManager.onSResume();
        mDeviceManager.onSResume();
    }

    @Override
    public void onSPause() {
        Tlog.v(TAG, " NetworkManager onSPause() ");
        mUserManager.onSPause();
        mDeviceManager.onSPause();
    }

    @Override
    public void onSDestroy() {
        Tlog.v(TAG, " NetworkManager onSDestroy() ");
        mUserManager.onSDestroy();
        mDeviceManager.onSDestroy();

        if (mUdpCom != null) {
            mUdpCom.release();
        }

        if (mBroadcastUtil != null) {
            mBroadcastUtil.stopDiscovery();
        }

        if (service != null) {
            service.shutdownNow();
            service = null;
        }

        app.unregisterReceiver(mNetWorkStateReceiver);
    }

    @Override
    public void onSFinish() {
        Tlog.v(TAG, " NetworkManager onSFinish() ");
    }

    @Override
    public void queryWiFiConnectState() {

        if (mResultCallBack != null) {
            mResultCallBack.onResultWiFiConState(isWiFiConnected());
        }

    }

    private boolean isWiFiConnected() {
        if (!mWiFiManager.isWifiEnabled()) {
            Tlog.e(TAG, "isWiFiConnected() wifi not enabled");
            return false;
        }

        WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
        String ssid = connectionInfo != null ? connectionInfo.getSSID() : null;
        int conNetworkID = connectionInfo != null ? connectionInfo.getNetworkId() : -1;
        Tlog.v(TAG, "isWiFiConnected() ssid: " + ssid + " NetworkId: " + conNetworkID);

        return (conNetworkID != -1);
    }

    @Override
    public void queryConWiFiSSID() {

        if (mResultCallBack != null) {
            mResultCallBack.onResultConWiFiSSID(getConnectedWiFiSSID());
        }
    }

    private String getConnectedWiFiSSID() {
        WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
        String ssid = connectionInfo != null ? connectionInfo.getSSID() : "unknown";
        int conNetworkID = connectionInfo != null ? connectionInfo.getNetworkId() : -1;
        String pwd = null;

        // 有些手机获取不到连接的ssid
        if (conNetworkID != -1) {
            List<WifiConfiguration> configuredNetworks = mWiFiManager.getConfiguredNetworks();
            if (configuredNetworks != null && configuredNetworks.size() > 0) {
                for (WifiConfiguration mWifiConfiguration : configuredNetworks) {
                    if (mWifiConfiguration.networkId == conNetworkID) {
                        pwd = mWifiConfiguration.preSharedKey;
                        ssid = mWifiConfiguration.SSID;
                        break;
                    }
                }
            }
        }

        Tlog.v(TAG, "connectionInfo ssid: " + ssid + " NetworkId: " + conNetworkID + " pwd: " + pwd);

        if (conNetworkID == -1) {
            return "unknown";
        }

        ssid = ssid.replaceAll("\"", "");
        return ssid;
    }

    private IWiFiResultCallBack mResultCallBack;

    @Override
    public void regWiFiResultCallBack(IWiFiResultCallBack mResultCallBack) {
        this.mResultCallBack = mResultCallBack;
        this.mUserManager.regWiFiResultCallBack(mResultCallBack);
        this.mDeviceManager.regWiFiResultCallBack(mResultCallBack);
    }

    private long startT;
    private ExecutorService service;
    private boolean hasConfigureWiFi = false;

   /* private final C2JavaExDevice.OnAirKissListener airKissLsn = new C2JavaExDevice.OnAirKissListener() {

        @Override
        public void onAirKissSuccess() {
            Tlog.v(TAG, "onAirKissSuccess use time:" + ((System.currentTimeMillis() - startT) / 1000) + "s");
            if (mResultCallBack != null) {
                mResultCallBack.onResultDeviceConfigureWiFi(true);
            }
        }

        @Override
        public void onAirKissFailed(int error) {
            Tlog.e(TAG, "onAirKissFailed errorCode = " + error);

            long t = ((System.currentTimeMillis() - startT) / 1000);

            if (t >= AIR_KISS_TIME_OUT / 3 * 2) {

                if (mResultCallBack != null) {
                    mResultCallBack.onResultDeviceConfigureWiFi(false);
                }

            }

        }

    };*/


    private final MyEsptouchListener mEspLsn = new MyEsptouchListener() {
        @Override
        public void onEspTouchResultFailed(String errorMsg, String errorCode) {
            Tlog.e(TAG, "配置失败 " + errorMsg + " errorCode = " + errorCode);

            if (mResultCallBack != null) {
                mResultCallBack.onResultDeviceConfigureWiFi(false);
            }

        }

        @Override
        public void onEsptouchResultAdded(IEsptouchResult iEsptouchResult) {
            Tlog.v(TAG, "配置成功 用时 " + ((System.currentTimeMillis() - startT) / 1000) + " s " + iEsptouchResult.getBssid() + "\n");
            if (mResultCallBack != null) {
                mResultCallBack.onResultDeviceConfigureWiFi(true);
            }
        }
    };

    private final AirkissHelper.AirkissHelperListener mAirKissHelperLsn = new AirkissHelper.AirkissHelperListener() {
        @Override
        public void onAirkissSuccess(InetAddress inetAddress) {
            String ip = inetAddress != null ? inetAddress.getHostAddress() : "0.0.0.0";
            Tlog.v(TAG, "配置成功 用时 " + ((System.currentTimeMillis() - startT) / 1000) + " s " + ip + "\n");
            if (mResultCallBack != null) {
                mResultCallBack.onResultDeviceConfigureWiFi(true);
            }
        }
    };

    private final long AIR_KISS_TIME_OUT = 1000 * 90;
    //    private final String ARI_KISS_ASE_KEY = "";
    private EsptouchAsyncTask mTask;

    @Override
    public void configureWiFi(WiFiConfig mConfig) {

        if (service == null) {
            synchronized (NetworkManager.this) {
                if (service == null) {
                    service = Executors.newFixedThreadPool(2);
                }
            }
        }

        if (mConfig == null) {
            Tlog.e(TAG, "WiFiConfig==null ");
            if (mResultCallBack != null) {
                mResultCallBack.onResultConfigureWiFi(false);
            }
            return;
        }

//        final int processPeroid = 0; // 发包流程间隔 最大5000ms
//        final int datePeroid = 5; // 发包间隔 最大80ms

        final String pwd = mConfig.getPwd();
        String ssid1 = mConfig.getSsid();
        if (ssid1 != null) {
            ssid1 = ssid1.replaceAll("\"", "");
        }
        final String ssid = ssid1;

        startT = System.currentTimeMillis();
        service.execute(() -> {

            final String conSSID = "\"" + ssid + "\"";
            String bssid = "";

            WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();

            if (connectionInfo.getNetworkId() == -1) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultConfigureWiFi(false);
                }
                return;
            }

            if (connectionInfo.getSSID().equalsIgnoreCase(conSSID)) {
                bssid = connectionInfo.getBSSID();
                Tlog.v(TAG, "connectionInfo bssid " + bssid);
            } else {
                List<WifiConfiguration> configuredNetworks = mWiFiManager.getConfiguredNetworks();

                if (configuredNetworks != null) {
                    for (WifiConfiguration mWiFiConfig : configuredNetworks) {
                        if (mWiFiConfig.SSID.equalsIgnoreCase(conSSID)) {
                            bssid = mWiFiConfig.BSSID;
                            Tlog.v(TAG, "WifiConfiguration bssid " + bssid);
                            break;
                        }
                    }
                }
            }

            if (!MacUtil.macMatches(bssid)) {
                Tlog.e(TAG, " bssid unInvalid " + bssid);
                if (mResultCallBack != null) {
                    mResultCallBack.onResultConfigureWiFi(false);
                }
                return;
            }

            if (!hasConfigureWiFi) {
                Tlog.v(TAG, "startAirKiss() " + ssid + " pwd:" + pwd);

                if (mTask != null) {
                    mTask.cancelEsptouch();
                }

                hasConfigureWiFi = true;
                final EsptouchAsyncTask mTmpTask = new EsptouchAsyncTask(app, bssid, ssid, pwd, 0, (int) AIR_KISS_TIME_OUT, mEspLsn);
                mTmpTask.execute();
                mTask = mTmpTask;

                AirkissHelper.getInstance().start(AIR_KISS_TIME_OUT, mAirKissHelperLsn);

//                C2JavaExDevice.getInstance().setAirKissListener(airKissLsn);
//                Java2CExDevice.startAirKissWithInter(pwd, ssid, ARI_KISS_ASE_KEY.getBytes(), AIR_KISS_TIME_OUT, processPeroid, datePeroid);
//                Java2CExDevice.startAirKiss(pwd, ssid, ARI_KISS_ASE_KEY.getBytes(), AIR_KISS_TIME_OUT);

            }

            if (mResultCallBack != null) {
                mResultCallBack.onResultConfigureWiFi(true);
            }

            Tlog.v(TAG, "startAirKissWithInter() finish ");
        });

    }

    @Override
    public void stopConfigureWiFi() {
        if (service != null) {
            service.execute(() -> {
                if (hasConfigureWiFi) {
                    hasConfigureWiFi = false;
                    Tlog.v(TAG, " stopConfigureWiFi() ");

                    if (mTask != null) {
                        mTask.cancelEsptouch();
                        mTask = null;
                    }

                    AirkissHelper.getInstance().stop();

//                    C2JavaExDevice.getInstance().setAirKissListener(null);
//                    Java2CExDevice.stopAirKiss();

                }
                Tlog.v(TAG, "stopConfigureWiFi() finish ");
            });
        }
    }

    /****** 以上是配网的****/

    @Override
    public String getLoginUserID() {
        return mUserManager.getLoginUserID();
    }

    @Override
    public void isLogin() {
        mUserManager.isLogin();
    }

    @Override
    public void getMobileLoginCode(String phone) {
        mUserManager.getMobileLoginCode(phone);
    }

    @Override
    public void loginMobile(MobileLogin mLogin) {
        mUserManager.loginMobile(mLogin);
    }

    @Override
    public void loginOut() {
        mUserManager.loginOut();
    }

    @Override
    public void emailLogin(MobileLogin mLogin) {
        mUserManager.emailLogin(mLogin);
    }

    @Override
    public void emailRegister(UserRegister obj) {
        mUserManager.emailRegister(obj);
    }

    @Override
    public void updateUserPwd(UserUpdateInfo mPwd) {
        mUserManager.updateUserPwd(mPwd);
    }

    @Override
    public void checkIsLatestVersion() {
        mUserManager.checkIsLatestVersion();
    }

    @Override
    public void updateApp() {
        mUserManager.updateApp();
    }

    @Override
    public void cancelUpdate() {
        mUserManager.cancelUpdate();
    }

    @Override
    public void updateUserName(UserUpdateInfo obj) {
        mUserManager.updateUserName(obj);
    }

    @Override
    public void takePhoto() {
        mUserManager.takePhoto();
    }

    @Override
    public void localPhoto() {
        mUserManager.localPhoto();
    }

    @Override
    public void queryUserInfo() {
        mUserManager.getUserInfo();
    }

    @Override
    public void emailForgot(String email) {
        mUserManager.emailForgot(email);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mUserManager.onActivityResult(requestCode, resultCode, data);
    }

    /****** 以上是用户的****/

    @Override
    public String getNameByMac(String mac) {
        LanDeviceInfo displayDeviceByMac = mDeviceManager.getDisplayDeviceByMac(mac);
        if (displayDeviceByMac != null) {
            return displayDeviceByMac.getName();
        }
        return "socket";
    }

    @Override
    public void queryBindDeviceList() {
        Tlog.v(TAG, " queryBindDeviceList() ");

        if (getLoginUserID() != null) {
            mDeviceManager.queryBindDeviceList(getLoginUserID());
            discoveryLanDevice(2);
        }

    }


    @Override
    public void discoveryLanDevice() {
        Tlog.v(TAG, " discoveryLanDevice ");
        lanDiscoveryDisplay = true;
        discoveryLanDevice(8);
    }

    private void discoveryLanDevice(int times) {
        String loginUserID = getLoginUserID();
        if (loginUserID != null && mBroadcastUtil != null) {
            mBroadcastUtil.startDiscovery(loginUserID, times);
        } else {
            Tlog.e(TAG, " discoveryLanDevice() loginUserID is null");
        }
    }

    private boolean lanDiscoveryDisplay = false;

    @Override
    public void closeDiscoveryLanDevice() {
        Tlog.v(TAG, " closeDiscoveryLanDevice ");
        lanDiscoveryDisplay = false;
        if (mBroadcastUtil != null) {
            mBroadcastUtil.stopDiscovery();
        }
    }

    @Override
    public void lanDeviceDiscovery(LanDeviceInfo mDevice) {

        mDeviceManager.lanDeviceDiscovery(mDevice);

        ControlDevice controlDevice = mControlDeviceUtil.get(mDevice.getMac());
        if (controlDevice != null) {
            final String loginUserID = getLoginUserID();
            final int token = mDeviceManager.getToken(mDevice.getMac(), loginUserID);
            controlDevice.lanDeviceDiscovery(token, loginUserID);
        }

        // 显示
        if (mResultCallBack != null && lanDiscoveryDisplay && mDevice.hasActivate && getLoginUserID() != null) {

            WanBindingDeviceDao wanBindingDeviceDao = DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
            List<WanBindingDevice> listWan = wanBindingDeviceDao.queryBuilder().where(WanBindingDeviceDao.Properties.Mac.eq(mDevice.mac),
                    WanBindingDeviceDao.Properties.Mid.eq(getLoginUserID())).list();

            if (listWan.size() <= 0) {
                Tlog.e(TAG, " update discovery device BindingState is false " + mDevice.getMac() + " " + mDevice.getName());
                mDevice.setIsWanBind(false);
                mDevice.setIsLanBind(false);
            } else {
                WanBindingDevice wanBindingDevice = listWan.get(0);
                mDevice.setIsWanBind(wanBindingDevice.getHasBindingByWan());
                mDevice.setIsLanBind(wanBindingDevice.getHasBindingByLan());

            }

            if (Debuger.isLogDebug) {
                Tlog.i(TAG, " lanDeviceDiscovery() " + mDevice.toString());
            }
            DisplayDeviceList mList = new DisplayDeviceList(mDevice);
            mResultCallBack.onResultLanDeviceListDisplay(true, mList);
        }

    }

    @Override
    public void bindingDevice(LanBindInfo mLanBindInfo) {
        Tlog.w(TAG, "bindingDevice() :" + mLanBindInfo.toString());
    }


    @Override
    public void unbindingDevice(String mac) {
        mDeviceManager.unbindingDevice(mac, getLoginUserID());
    }

    @Override
    public void onDeviceResponseLanBind(boolean result, LanBindingDevice mLanBindingDevice) {
        mDeviceManager.onDeviceResponseLanBind(result, mLanBindingDevice);

        if (mResultCallBack != null) {
            mResultCallBack.onResultNeedRequestToken(mLanBindingDevice.getOmac(), getLoginUserID());
        }

    }

    @Override
    public void onDeviceResponseLanUnBind(boolean result, LanBindingDevice mLanBindingDevice) {
        mDeviceManager.onDeviceResponseLanUnBind(result, mLanBindingDevice, getLoginUserID());
    }

    @Override
    public void onDeviceResponseToken(String mac, int token) {
        Tlog.e(TAG, " onDeviceResponseToken()  mac:" + mac + " token:" + Integer.toHexString(token));
        mDeviceManager.onDeviceResponseToken(mac, token, getLoginUserID());

        if (mControlDeviceUtil.containsKey(mac)) {
            ControlDevice controlDevice = mControlDeviceUtil.get(mac);
            if (controlDevice != null) {
                controlDevice.onResponseToken(getLoginUserID(), token);
            }
        }

    }


    @Override
    public void onTokenInvalid(String mac) {
        Tlog.e(TAG, " onTokenInvalid " + mac);

        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        if (controlDevice != null) {
            final String loginUserID = getLoginUserID();
            final int token = getToken(mac);
            controlDevice.onTokenInvalid(token, loginUserID);
        }

    }

    private final ControlDeviceUtil mControlDeviceUtil = new ControlDeviceUtil();

    @Override
    public void controlWiFiDevice(LanDeviceInfo obj) {

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, " controlWiFiDevice()  " + obj.getMac() + " " + obj.getIp());
        }

        LanDeviceInfo displayDeviceByMac = mDeviceManager.getDisplayDeviceByMac(obj.getMac());
        if (displayDeviceByMac != null) {
            obj = displayDeviceByMac;
        }

        ControlDevice mControlDevice = mControlDeviceUtil.get(obj.getMac());
        if (mControlDevice == null) {
            mControlDevice = new ControlDevice(obj, mResultCallBack);
            mControlDeviceUtil.put(obj.getMac(), mControlDevice);
        } else {
            mControlDevice.removeCallJsCon();
        }

        LanDeviceInfo mLanDiscoveryDevice = mDeviceManager.getDiscoveryDeviceByMac(obj.getMac());
        final String loginUserID = getLoginUserID();
        final int token = getToken(obj.getMac());
        mControlDevice.controlWiFiDevice(token, loginUserID, mLanDiscoveryDevice != null);

    }


    @Override
    public void onDeviceResponseConnect(boolean result, String mac) {

        Tlog.e(TAG, " onDeviceResponseConnect() result:" + result + " mac:" + mac);
        mDeviceManager.onDeviceResponseConnect(result, mac, getLoginUserID());

        ControlDevice mControlDevice = mControlDeviceUtil.get(mac);
        if (mControlDevice != null) {

            if (result) {
                LanDeviceInfo discoveryDeviceByMac = mDeviceManager.getDiscoveryDeviceByMac(mac);
                mControlDevice.responseConnected(discoveryDeviceByMac != null && IpUtil.ipMatches(discoveryDeviceByMac.ip));
            } else {
                mControlDevice.responseConnectedFail(getLoginUserID());
            }

        }
    }

    @Override
    public void disControlDevice(String mac) {
        Tlog.e(TAG, " disControlDevice " + mac);

        ControlDevice mControlDevice = mControlDeviceUtil.get(mac);
        if (mControlDevice != null) {
            mControlDevice.disControl();
        }
        mControlDeviceUtil.remove(mac);

        if (mResultCallBack != null) {
            int token = getToken(mac);
            Tlog.e(TAG, " appSleep " + mac + " token:" + Integer.toHexString(token));
            mResultCallBack.onResultAppSleep(mac, getLoginUserID(), token);

            mResultCallBack.onResultWiFiDeviceDisConnected(true, mac);

        }

    }

    @Override
    public void onDeviceResponseSleep(boolean result, String mac) {
        Tlog.e(TAG, " onDeviceResponseSleep() result:" + result + " mac:" + mac);
        mDeviceManager.onDeviceResponseSleep(result, mac, getLoginUserID());
        if (mResultCallBack != null) {

            if (!mControlDeviceUtil.containsKey(mac)) {
                mResultCallBack.onResultWiFiDeviceDisConnected(true, mac);
            } else {
                Tlog.e(TAG, " onDeviceResponseSleep mac" + mac + " under control ");
            }

        }
    }

    @Override
    public void onDeviceResponseDisconnect(boolean result, String mac) {
        Tlog.e(TAG, " onDeviceResponseDisconnect() result:" + result + " mac:" + mac);
        mDeviceManager.onDeviceResponseDisconnect(result, mac, getLoginUserID());
    }

    @Override
    public int getToken(String mac) {
        return mDeviceManager.getToken(mac, getLoginUserID());
    }


    @Override
    public void onDeviceResponseRename(String mac, String name) {
        mDeviceManager.onDeviceRename(mac, name);
    }


    @Override
    public void onDeviceResponseRelaySwitch(String mac, boolean status) {
        Tlog.e(TAG, " onDeviceResponseRelaySwitch() status:" + status + " mac:" + mac);
        mDeviceManager.relaySwitch(mac, status);
    }

    @Override
    public void receiveHeartbeat(String mac, boolean result) {
        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        if (controlDevice != null) {

            controlDevice.receiveHeartbeat(result);

            if (result && !controlDevice.canLanCom()) {
                final String loginUserID = getLoginUserID();
                final int token = getToken(mac);
                controlDevice.lanDeviceDiscovery(token, loginUserID);
            }
        }
    }


    @Override
    public void heartbeatLose(String mac, int loseTimes) {
        Tlog.e(TAG, " heartbeatLose startDiscovery mac :" + mac + " loseTimes:" + loseTimes);
        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        if (controlDevice != null) {
            if (controlDevice.canLanCom()) {
                discoveryLanDevice(6);
            } else {
                discoveryLanDevice(1);
            }
            controlDevice.heartbeatLose(loseTimes);
        }
    }

    private IDataProtocolInput mReceives;

    @Override
    public void regIProtocolInput(IDataProtocolInput mReceives) {
        this.mReceives = mReceives;
    }


    @Override
    public void onSocketInitResult(boolean result, String ip, int port) {
        Tlog.v(TAG, " onSocketInitResult result " + result + " ip->" + ip + " port->" + port);
        if (mResultCallBack != null) {
            mResultCallBack.onResultSocketInit(result);
        }
    }

    private final IOnCallListener mSendDataListener = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
//            Tlog.v(TAG, " wan passthrough onSuccess ");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {

            Tlog.e(TAG, " wan pass through send failed " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }

        }


        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    @Override
    public void onOutputDataToServer(ResponseData mResponseData) {

        if (mResponseData.getSendModel().isSendModelOnlyLan()) {

            if (Debuger.isLogDebug) {
                Tlog.i(TAG, "mResponseData.getSendModel().isSendModelOnlyLan()");
            }

            sendMsgByLan(mResponseData);

            return;
        }

        if (mResponseData.getSendModel().isSendModelOnlyWan()) {

            if (Debuger.isLogDebug) {
                Tlog.i(TAG, "mResponseData.getSendModel().isSendModelOnlyWan()");
            }

            sendMsgByWan(mResponseData);

            return;
        }

        ControlDevice controlDevice = mControlDeviceUtil.get(mResponseData.toID);

        if (controlDevice != null && controlDevice.canLanCom()) {
            if (!sendMsgByLan(mResponseData)) {
                sendMsgByWan(mResponseData);
            }
        } else {
            sendMsgByWan(mResponseData);
        }

    }

    private boolean sendMsgByLan(ResponseData mResponseData) {

        LanDeviceInfo mDiscoveryDeviceByMac = mDeviceManager.getDiscoveryDeviceByMac(mResponseData.toID);

        if (mDiscoveryDeviceByMac != null) {

//            mResponseData.obj = mDiscoveryDeviceByMac.ip;
//            mResponseData.arg = mDiscoveryDeviceByMac.port;
//            mUdpCom.write(mResponseData);

            UdpResponseMsg udpMsg = new UdpResponseMsg();
            udpMsg.ip = mDiscoveryDeviceByMac.ip;
            udpMsg.port = mDiscoveryDeviceByMac.port;
            udpMsg.data = mResponseData.data;
            mUdpCom.write(udpMsg);

            if (Debuger.isLogDebug) {
                Tlog.w(TAG, "onOutputDataToServerByLan() :" + mResponseData.toString());
            }

            return true;
        }

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, "onOutputDataToServerByLan() unknown IP;" + mResponseData.toString());
        }

        return false;
    }

    private void sendMsgByWan(ResponseData mResponseData) {

        LanDeviceInfo displayDeviceByMac = mDeviceManager.getDisplayDeviceByMac(mResponseData.toID);
        String deviceID = null;

        if (Debuger.isLogDebug) {
            Tlog.e(TAG, "onOutputDataToServerByWan() sendMsgByWan:" + String.valueOf(displayDeviceByMac));
        }

        if (displayDeviceByMac != null) {

            if (!displayDeviceByMac.getIsWanBind()) {
                if (Debuger.isLogDebug) {
                    Tlog.e(TAG, "onOutputDataToServerByWan() wanNotBind:" + mResponseData.toString());
                }
                deviceID = null;
            } else {
                deviceID = displayDeviceByMac.getDeviceID();
            }

        }

//        deviceID = mDeviceManager.getDisplayDeviceIDByMac(mResponseData.toID);


        if (deviceID != null) {

            mResponseData.obj = deviceID;
            mResponseData.getRepeatMsgModel().setNeedRepeatSend(false);

            if (Debuger.isLogDebug) {
                Tlog.w(TAG, "onOutputDataToServerByWan() :" + mResponseData.toString());
            }

            StartAI.getInstance().getBaseBusiManager().passthrough(deviceID, mResponseData.data, mSendDataListener);

        } else {

            if (Debuger.isLogDebug) {
                Tlog.e(TAG, "onOutputDataToServerByWan() unknown userID; " + mResponseData.toString());
            }

            if (mResponseData.toID != null && !mResponseData.toID.equalsIgnoreCase(H5Config.DEFAULT_MAC)) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(StartaiError.ERROR_SEND_NO_FID));
                }
            }

        }

    }

    private InetAddress getBroadcastAddress() {
        InetAddress address = IpUtil.getWiFiBroadcastAddress(app);
        if (address == null || address.getHostAddress().equalsIgnoreCase(IpUtil.BROAD_IP_BOUND)) {
            address = IpUtil.getLocalBroadcastAddress();
            if (address == null) {
                address = IpUtil.getBoundBroadcast();
            }
        }
        return address;
    }

    @Override
    public void onBroadcastDataToServer(ResponseData mResponseData) {

        if (getLoginUserID() == null) {
            Tlog.e(TAG, " onBroadcastDataToServer loginUserId is null ");
            return;
        }

        if (mUdpCom != null) {
            if (mResponseData.data != null) {

//                mResponseData.obj = getBroadcastAddress();
//                mResponseData.arg = 9222;
//                mUdpCom.broadcast(mResponseData);

                InetAddress broadcastAddress = getBroadcastAddress();
                if (broadcastAddress != null) {
                    UdpResponseMsg udpMsg = new UdpResponseMsg();
                    udpMsg.ip = broadcastAddress.getHostAddress();
                    udpMsg.port = 9222;
                    udpMsg.data = mResponseData.data;
                    mUdpCom.broadcast(udpMsg);
                }

                if (Debuger.isLogDebug) {
                    Tlog.w(TAG, "onBroadcastDataToServer() :" + mResponseData.toString());
                }

            }
        }
    }


    @Override
    public void onSocketReceiveData(String ip, int port, byte[] data) {

        if (mReceives != null) {

            String mac = mDeviceManager.getMacByIp(ip);

            if (mac == null) {
                mac = H5Config.DEFAULT_MAC;
            }

            ReceivesData mReceiveData = new ReceivesData(mac, data);
            mReceiveData.obj = ip;
            mReceiveData.arg = port;
            mReceives.onInputServerData(mReceiveData);

            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "onSocketReceiveData() :" + mReceiveData.toString());
            }

        } else {
            Tlog.e(TAG, " onSocketReceiveData  mReceives==null ");
        }

    }

    private void onWanSocketReceiveData(String fromId, byte[] data) {

        if (mReceives != null) {
            String mac = mDeviceManager.getDisplayDeviceMacByID(fromId);
            if (mac == null) {
                mac = H5Config.DEFAULT_MAC;
            }

            ReceivesData mReceiveData = new ReceivesData(mac, data);
            mReceiveData.obj = fromId;
            mReceives.onInputServerData(mReceiveData);

            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "onWanSocketReceiveData() :" + mReceiveData.toString());
            }

        } else {
            Tlog.e(TAG, " onWanSocketReceiveData  mReceives==null ");
        }
    }

    /**************************/

    private final IConnectionStateListener mConnectionStateListener = new ICommonStateListener() {
        @Override
        public void onTokenExpire(C_0x8018.Resp.ContentBean resp) {
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, "MQTT onConnectExpire " + String.valueOf(resp));
            }
        }

        @Override
        public void onConnectFail(int errorCode, String errorMsg) {
            Tlog.e(TAG, "MQTT onConnectFail " + errorMsg);
//            connectStatus = false;

            if (mResultCallBack != null) {
                mResultCallBack.onResultServerConnectState(false, String.valueOf(errorCode));
            }

        }

        @Override
        public void onConnected() {
            Tlog.e(TAG, "MQTT onConnected");
//            connectStatus = true;
            if (mResultCallBack != null) {
                mResultCallBack.onResultServerConnectState(true, "");
            }

        }

        @Override
        public void onDisconnect(int errorCode, String errorMsg) {
            Tlog.e(TAG, "MQTT onDisconnect " + errorCode + " er" + errorMsg);
//            connectStatus = true;
            if (mResultCallBack != null) {
                mResultCallBack.onResultServerConnectState(false, String.valueOf(errorCode));
            }
        }

        @Override
        public boolean needUISafety() {
            return false;
        }
    };


    private final AOnStartaiMessageArriveListener mComMessageListener = new AOnStartaiMessageArriveListener() {

//        private static final int FAIL = 0; //RespErr
//        private static final int SUCCESS = 1; // C_0x8022.Resp
//        private static final int STATE = -1; // RespErr

        /**
         * 所有的回调是否到ui线程
         */
        @Override
        public boolean needUISafety() {
            return false;
        }

        /**
         * 通用的消息接收方法,除基础业务以外的消息都回回调到此方法
         *
         * @param topic 接收消息的主题
         *              消息类型
         *              1成功 0失败
         * @param msg   消息内容
         */
        @Override
        public void onCommand(String topic, String msg) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " onCommand topic:" + topic + " msg:" + msg);
            }
        }

        @Override
        public void onPassthroughResult(C_0x8200.Resp resp, String dataString, byte[] dataByteArray) {
            super.onPassthroughResult(resp, dataString, dataByteArray);

            if (resp.getResult() == 1) {
                final String fromid = resp.getFromid();
                onWanSocketReceiveData(fromid, dataByteArray);
            } else {
                Tlog.e(TAG, " on pass through Result fail " + String.valueOf(resp));
            }

        }

        /**
         * 设备激活回调，如果激活成功只会回调一次
         */
        @Override
        public void onActiviteResult(C_0x8001.Resp resp) {
            super.onActiviteResult(resp);
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "onActiviteResult  " + String.valueOf(resp));
            }
        }

        @Override
        public void onUnActiviteResult(C_0x8003.Resp resp) {
            super.onUnActiviteResult(resp);
            mUserManager.onUnActivateResult(resp);
        }

        @Override
        public void onCheckIdetifyResult(C_0x8022.Resp resp) {
            super.onCheckIdetifyResult(resp);
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, " onCheckIdentifyResult " + String.valueOf(resp));
            }
        }

        @Override
        public void onDeviceConnectStatusChange(String userid, int status, String sn) {
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "onDeviceConnectStatusChange  sn " + sn + "  status:" + status + " userid:" + userid);
            }
            mDeviceManager.onDeviceConnectStatusChange(userid, status, sn);
            discoveryLanDevice(2);

        }

        @Override
        public void onBindResult(C_0x8002.Resp resp, String id, C_0x8002.Resp.ContentBean.BebindingBean bebinding) {
            super.onBindResult(resp, id, bebinding);
            mDeviceManager.onBindResult(resp, id, bebinding);
        }


        @Override
        public void onUnBindResult(C_0x8004.Resp resp, String id, String beUnbindid) {
            super.onUnBindResult(resp, id, beUnbindid);
            mDeviceManager.onUnBindResult(resp, id, beUnbindid);
        }

        @Override
        public void onGetBindListResult(C_0x8005.Response response) {
            super.onGetBindListResult(response);
            mDeviceManager.onGetBindListResult(response, getLoginUserID());
        }


        @Override
        public void onRegisterResult(C_0x8017.Resp resp) {
            super.onRegisterResult(resp);
            mUserManager.onRegisterResult(resp);
        }

        @Override
        public void onLogoutResult(int result, String errorCode, String errorMsg) {
            mUserManager.onLogoutResult(result, errorCode, errorMsg);
            mDeviceManager.onLogoutResult(result);
        }

        @Override
        public void onLoginResult(C_0x8018.Resp resp) {
            super.onLoginResult(resp);

            mDeviceManager.onLoginResult(resp.getResult());
            mUserManager.onLoginResult(resp);

            if (resp.getResult() == 1) {
                discoveryLanDevice(8);
            }

        }


        @Override
        public void onGetIdentifyCodeResult(C_0x8021.Resp resp) {
            super.onGetIdentifyCodeResult(resp);
            mUserManager.onGetIdentifyCodeResult(resp);
        }

        @Override
        public void onUpdateUserPwdResult(C_0x8025.Resp resp) {
            super.onUpdateUserPwdResult(resp);
            mUserManager.onUpdateUserPwdResult(resp);
        }

        @Override
        public void onUpdateUserInfoResult(C_0x8020.Resp resp) {
            super.onUpdateUserInfoResult(resp);
            mUserManager.onUpdateUserInfoResult(resp);
        }

        @Override
        public void onGetLatestVersionResult(C_0x8016.Resp resp) {
            super.onGetLatestVersionResult(resp);
            mUserManager.onGetLatestVersionResult(resp);
        }

        @Override
        public void onGetUserInfoResult(C_0x8024.Resp resp) {
            super.onGetUserInfoResult(resp);
            mUserManager.onGetUserInfoResult(resp);
        }

        @Override
        public void onSendEmailResult(C_0x8023.Resp resp) {
            super.onSendEmailResult(resp);
            mUserManager.onSendEmailResult(resp);
        }
    };

}
