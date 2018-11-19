package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class DeviceList extends AbsHandlerJsInterface {

    public interface IJSDeviceListCallBack {

        void onJSDeviceListRequest();

        void onJSControlDevice(LanDeviceInfo mWiFiDevice);

        void onJSDisControlDevice(String mac);

        void onJSUnbindingDevice(String obj);

        void onJSQuickControlRelay(String obj, boolean b);

        void onJSQuickQueryRelay(String obj);
    }


    public static final class Method {

        private static final String DEVICE_LIST = "javascript:deviceListResponse('$data')";

        public static String callJsDeviceList(String data) {
            return DEVICE_LIST.replace("$data", String.valueOf(data));
        }

        private static final String DEVICE_CONTROL = "javascript:controlDeviceResponse($result,'$data')";

        public static String callJsDeviceControl(boolean result, String data) {
            return DEVICE_CONTROL.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }

        private static final String DEVICE_UNBIND_RESULT = "javascript:unbundlingDeviceResponse($result,'$mac')";

        public static String callJsUnbindDevice(boolean result, String mac) {
            return DEVICE_UNBIND_RESULT.replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));
        }

        private static final String WIFI_POWER_SWITCH_RESULT = "javascript:wifiPowerSwitchResponse('$mac',$state)";

        public static String callJsStateQuicControlRelay(String mac, boolean state) {
            return WIFI_POWER_SWITCH_RESULT.replace("$state", String.valueOf(state))
                    .replace("$mac", String.valueOf(mac));
        }
    }

    public static final String NAME_JSI = "DeviceList";

    private String TAG = H5Config.TAG;

    private final IJSDeviceListCallBack mCallBack;

    public DeviceList(Looper mLooper, IJSDeviceListCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void deviceListRequest() {
        Tlog.v(TAG, " deviceListRequest ");
        getHandler().sendEmptyMessage(MSG_WIFI_DEVICE_LIST_REQUEST);
    }

    @JavascriptInterface
    public void controlDeviceRequest(String deviceJson) {
        Tlog.v(TAG, " controlDeviceRequest " + deviceJson);
        getHandler().obtainMessage(MSG_CONTROL_WIFI_DEVICE, deviceJson).sendToTarget();
    }


    @JavascriptInterface
    public void relieveControlDeviceRequest(String mac) {
        Tlog.v(TAG, " relieveControlDeviceRequest " + mac);
        getHandler().obtainMessage(MSG_DIS_CONTROL_WIFI_DEVICE, mac).sendToTarget();
    }

    @JavascriptInterface
    public void unbundlingDeviceRequest(String mac) {
        Tlog.v(TAG, " unbundlingDeviceRequest " + mac);
        getHandler().obtainMessage(MSG_UNBINDING_DEVICE, mac).sendToTarget();
    }

    @JavascriptInterface
    public void wifiPowerSwitchRequest(String mac, boolean status) {
        Tlog.v(TAG, " wifiPowerSwitchRequest " + mac + " status:" + status);
        if (status) {
            getHandler().obtainMessage(MSG_QUICK_CONTROL_RELAY_ON, mac).sendToTarget();
        } else {
            getHandler().obtainMessage(MSG_QUICK_CONTROL_RELAY_OFF, mac).sendToTarget();
        }
    }

    @JavascriptInterface
    public void wifiPowerSwitchStatusRequest(String mac) {
        Tlog.v(TAG, " wifiPowerSwitchStatusRequest " + mac);
        getHandler().obtainMessage(MSG_QUICK_QUERY_RELAY_STATUS, mac).sendToTarget();
    }


    private static final int MSG_WIFI_DEVICE_LIST_REQUEST = 0x29;
    private static final int MSG_CONTROL_WIFI_DEVICE = 0x2A;


    private static final int MSG_DIS_CONTROL_WIFI_DEVICE = 0x2D;

    private static final int MSG_UNBINDING_DEVICE = 0x2E;

    private static final int MSG_QUICK_CONTROL_RELAY_ON = 0x2F;
    private static final int MSG_QUICK_CONTROL_RELAY_OFF = 0x30;

    private static final int MSG_QUICK_QUERY_RELAY_STATUS = 0x31;

//

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_WIFI_DEVICE_LIST_REQUEST) {

            if (mCallBack != null) {
                mCallBack.onJSDeviceListRequest();
            }

        } else if (msg.what == MSG_CONTROL_WIFI_DEVICE) {

            if (mCallBack != null) {
                final LanDeviceInfo mWiFiDevice = LanDeviceInfo.fromJson((String) msg.obj);
                mCallBack.onJSControlDevice(mWiFiDevice);
            }

        } else if (msg.what == MSG_DIS_CONTROL_WIFI_DEVICE) {

            if (mCallBack != null) {
                mCallBack.onJSDisControlDevice((String) msg.obj);
            }

        } else if (msg.what == MSG_UNBINDING_DEVICE) {

            if (mCallBack != null) {
                mCallBack.onJSUnbindingDevice((String) msg.obj);
            }

        } else if (msg.what == MSG_QUICK_CONTROL_RELAY_ON) {

            if (mCallBack != null) {
                mCallBack.onJSQuickControlRelay((String) msg.obj, true);
            }

        } else if (msg.what == MSG_QUICK_CONTROL_RELAY_OFF) {

            if (mCallBack != null) {
                mCallBack.onJSQuickControlRelay((String) msg.obj, false);
            }

        } else if (msg.what == MSG_QUICK_QUERY_RELAY_STATUS) {

            if (mCallBack != null) {
                mCallBack.onJSQuickQueryRelay((String) msg.obj);
            }

        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }
}
