package cn.com.startai.socket.sign.js.jsInterface;


import android.net.NetworkInfo;
import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class Add extends AbsHandlerJsInterface {


    public interface IJSAddCallBack {

        void onJSAIsWiFiCon();

        void onJSAReqConWiFiSsid();

        void onJSAConfigureWiFi(WiFiConfig mConfig);

        void onJSAStopConfigureWiFi();

        void onJSADiscoveryLanDevice();

        void onJSACloseDiscoveryLanDevice();

        void onJSABindLanDevice(LanBindInfo obj);

        void onJSScanQRCode();
    }

    public static final class Method {


        private static final String SCAN_QR = "javascript:callThePhoneScanResponse($result,'$data')";

        public static String callJsScanQR(boolean result, String data) {
            return SCAN_QR.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }

        private static final String WIFI_IS_CON = "javascript:isConnectToWiFiResponse($result)";

        public static String callJsWifiConState(boolean result) {
            return WIFI_IS_CON.replace("$result", String.valueOf(result));
        }

        private static final String WIFI_SSID = "javascript:WiFiNameResponse('$ssid')";

        public static String callJsWifiSsid(String ssid) {
            return WIFI_SSID.replace("$ssid", String.valueOf(ssid));
        }

        private static final String CONFIGURE_WIFI = "javascript:WiFiConfigResponse($result)";

        public static String callJsConfigureWifi(boolean result) {
            return CONFIGURE_WIFI.replace("$result", String.valueOf(result));
        }

        private static final String DEVICE_CON_RESULT = "javascript:connectionNetworkResponse($result,'$mac')";

        public static String callJsDeviceConResult(boolean result, String mac) {
            return DEVICE_CON_RESULT.replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));
        }


        private static final String LAN_DEVICE_LIST = "javascript:deviceConnectedByRouterResponse($result,'$data')";

        public static String callJsLanDeviceList(boolean result, String data) {
            return LAN_DEVICE_LIST.replace("$result", String.valueOf(result)).replace("$data", data);
        }


        private static final String BIND_DEVICE = "javascript:addDeviceConnectedByRouterResponse($result)";

        public static String callJsBindDevice(boolean result) {
            return BIND_DEVICE.replace("$result", String.valueOf(result));
        }

        private static final String NETWORK_CHANGE = "javascript:switchNetworkResponse('$type',$state)";

        public static String callJsNetworkChange(String type, int state) {
            return NETWORK_CHANGE.replace("$type", String.valueOf(type)).replace("$state", String.valueOf(state));
        }

    }

    //    public static final String WIFI = "WiFi";
//    public static final String G4 = "MOBILE";
    public static final String NONE = "None";

    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTED = 3;
    public static final int DISCONNECTING = 4;
    public static final int UNKNOWN = 5;

    public static int changeState(NetworkInfo.State state) {

        int s;
        switch (state) {

            case CONNECTED:
                s = Add.CONNECTED;
                break;
            case CONNECTING:
                s = Add.CONNECTING;
                break;
            case DISCONNECTED:
                s = Add.DISCONNECTED;
                break;
            case DISCONNECTING:
                s = Add.DISCONNECTING;
                break;
            default:
                s = Add.UNKNOWN;
                break;
        }
        return s;
    }


    public static final String NAME_JSI = "Add";

    private String TAG = H5Config.TAG;

    private final IJSAddCallBack mCallBack;

    public Add(Looper mLooper, IJSAddCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void isConnectToWiFiRequest() {
        Tlog.v(TAG, " isConnectToWiFiRequest ");
        getHandler().sendEmptyMessage(MSG_IS_WIFI_CON);
    }


    @JavascriptInterface
    public void WiFiNameRequest() {
        Tlog.v(TAG, " WiFiNameRequest ");
        getHandler().sendEmptyMessage(MSG_REQ_CON_WIFI_SSID);
    }

    @JavascriptInterface
    public void WiFiConfigRequest(String ssid, String pwd) {
        Tlog.v(TAG, " WiFiConfigRequest pwd:" + pwd + " ssid:" + ssid);
        final WiFiConfig mConfig = new WiFiConfig();
        mConfig.setSsid(ssid);
        mConfig.setPwd(pwd);
        getHandler().obtainMessage(MSG_CONFIGURE_WIFI, mConfig).sendToTarget();

    }

    @JavascriptInterface
    public void stopConnectionNetworkRequest() {
        Tlog.v(TAG, " stopConnectionNetworkRequest ");
        getHandler().sendEmptyMessage(MSG_STOP_CONFIGURE_WIFI);
    }

    @JavascriptInterface
    public void openDeviceScanningRequest() {
        Tlog.v(TAG, " openDeviceScanningRequest ");

        getHandler().sendEmptyMessage(MSG_DISCOVERY_LAN_DEVICE);
    }

    @JavascriptInterface
    public void closeDeviceScanningRequest() {
        Tlog.v(TAG, " openDeviceScanningRequest ");

        getHandler().sendEmptyMessage(MSG_CLOSE_DISCOVERY_LAN_DEVICE);
    }

    @JavascriptInterface
    public void addDeviceConnectedByRouterRequest(String mac, String pwd) {
        Tlog.v(TAG, " addDeviceConnectedByRouterRequest ");

        LanBindInfo mInfo = new LanBindInfo();
        mInfo.mac = mac;
        mInfo.pwd = pwd;
        getHandler().obtainMessage(MSG_BIND_LAN_DEVICE, mInfo).sendToTarget();

    }

    @JavascriptInterface
    public void callThePhoneScanRequest() {
        Tlog.v(TAG, " callThePhoneScanRequest ");

        getHandler().sendEmptyMessage(MSG_SCAN_OR);
    }


    private static final int MSG_IS_WIFI_CON = 0x25;
    private static final int MSG_REQ_CON_WIFI_SSID = 0x26;
    private static final int MSG_CONFIGURE_WIFI = 0x27;
    private static final int MSG_STOP_CONFIGURE_WIFI = 0x28;
    private static final int MSG_DISCOVERY_LAN_DEVICE = 0x29;
    private static final int MSG_CLOSE_DISCOVERY_LAN_DEVICE = 0x2A;
    private static final int MSG_BIND_LAN_DEVICE = 0x2B;

    private static final int MSG_SCAN_OR = 0x2C;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_IS_WIFI_CON) {
            if (mCallBack != null) {
                mCallBack.onJSAIsWiFiCon();
            }
        } else if (msg.what == MSG_REQ_CON_WIFI_SSID) {

            if (mCallBack != null) {
                mCallBack.onJSAReqConWiFiSsid();
            }

        } else if (msg.what == MSG_CONFIGURE_WIFI) {

            if (mCallBack != null) {
                mCallBack.onJSAConfigureWiFi((WiFiConfig) msg.obj);
            }

        } else if (msg.what == MSG_STOP_CONFIGURE_WIFI) {

            if (mCallBack != null) {
                mCallBack.onJSAStopConfigureWiFi();
            }

        } else if (msg.what == MSG_DISCOVERY_LAN_DEVICE) {

            if (mCallBack != null) {
                mCallBack.onJSADiscoveryLanDevice();
            }

        } else if (msg.what == MSG_CLOSE_DISCOVERY_LAN_DEVICE) {

            if (mCallBack != null) {
                mCallBack.onJSACloseDiscoveryLanDevice();
            }

        } else if (msg.what == MSG_BIND_LAN_DEVICE) {
            if (mCallBack != null) {
                mCallBack.onJSABindLanDevice((LanBindInfo) msg.obj);
            }
        } else if (msg.what == MSG_SCAN_OR) {
            if (mCallBack != null) {
                mCallBack.onJSScanQRCode();
            }

        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }

    }


}
