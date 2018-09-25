package cn.com.startai.socket.sign.js.jsInterface;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/3 0003
 * desc :
 */

public class Device extends AbsJsInterface {

    public interface IJSDeviceCallBack {

        /**
         * 添加设备
         */
        void onJSDAddDevices();

        /**
         * 停止扫描
         */
        void onJSDStopScan();

        /**
         * 打开ble
         */
        void onJSDTurnOnBle();

        /**
         * 连接断开ble
         *
         * @param mac
         * @param con true con false discon
         */
        void onJSDSwitchBle(String mac, boolean con);

        /**
         * 下发数据给js
         *
         * @param mac
         * @param publish
         */
        void onJSDPublishSensorData(String mac, boolean publish);

        /**
         * 请求ble状态
         */
        void onJSDRequestBleState();

        void onJSDRequestIsFirstBinding();

        void onJSDRequestReconnectDevice(String mac);
    }

    public static final class Method {

        private static final String METHOD_BLE_NOT_ENABLE = "javascript:addDeviceResponse(1,'')";

        /**
         * ble没有激活回调js的函数
         *
         * @return
         */
        public static final String callJsHWNotEnable() {

            return METHOD_BLE_NOT_ENABLE;
        }

        private static final String METHOD_SCAN_BLE = "javascript:addDeviceResponse(2,'')";

        /**
         * 跳转到ble扫描界面
         *
         * @return
         */
        public static final String callJsScanHW() {

            return METHOD_SCAN_BLE;
        }

        private static final String METHOD_BLE_NOT_SCANNED = "javascript:addDeviceResponse(3,'')";

        /**
         * 没有扫描到Ble
         *
         * @return
         */
        public static final String callJsHWNotScanned() {

            return METHOD_BLE_NOT_SCANNED;
        }


        private static final String METHOD_DISPLAY_BLE = "javascript:addDeviceResponse(4,'$data')";


        /**
         * 显示扫描到的ble设备
         *
         * @param data "{
         *             name: '卧室空调插座1',
         *             mac： '00-23-45-65-95-56',
         *             state: 0
         *             }"
         * @return
         */
        public static final String callJsDisplayHW(String data) {

            return METHOD_DISPLAY_BLE.replace("$data", String.valueOf(data));
        }

        private static final String METHOD_SWITCH_BLE = "javascript:equipmentSwitchResponse('$mac',$state,$result)";

        /**
         * @param mac
         * @param con true 连接，false 断开
         * @return
         */
        public static final String callJsSwitchHW(String mac, boolean con, boolean result) {

            return callJsSwitchHW(mac, con ? 1 : 0, result);
        }

        /**
         * @param mac
         * @param state  0  断开， 1 连接
         * @param result
         * @return
         */
        private static final String callJsSwitchHW(String mac, int state, boolean result) {

            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_SWITCH_BLE.replace("$mac", mac).replace("$state", String.valueOf(state)).replace("$result", String.valueOf(result));
        }

        private static final String METHOD_TURN_OFF_BLE = "javascript:blueToothStateResponse($result)";

        /**
         * 用户主动关闭蓝牙回调给js
         *
         * @param result true : ble on  ; false : ble off
         * @return
         */
        public static final String callJsHWTurn(boolean result) {
            return METHOD_TURN_OFF_BLE.replace("$result", String.valueOf(result));
        }

        private static final String METHOD_RESPONSE_SOCKET_DATA = "javascript:socketStatusResponse('$mac','$data')";

        /**
         * 数据展示
         *
         * @param mac
         * @param data
         * @return
         */
        public static final String callJsDisplaySensorData(String mac, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_RESPONSE_SOCKET_DATA.replace("$mac", mac).replace("$data", data);
        }

        private static final String METHOD_RESPONSE_BLE_STATE = "javascript:isOpenBluetoothResponse($result)";


        public static String callJsBleState(boolean turnOn) {
            return METHOD_RESPONSE_BLE_STATE.replace("$result", String.valueOf(turnOn));
        }

        private static final String METHOD_FIRST_BINDING = "javascript:isFirstBindingResponse($result,'$data')";

        public static String callJSFirstBinding(boolean first, String data) {
            return METHOD_FIRST_BINDING.replace("$result", String.valueOf(first)).replace("$data", data == null ? "" : data);
        }

    }

    public static final String NAME_JSI = "Device";

    private String TAG = H5Config.TAG;

    private final IJSDeviceCallBack mCallBack;

    public Device(IJSDeviceCallBack mCallBack) {
        super(NAME_JSI);
        this.mCallBack = mCallBack;
    }


    @JavascriptInterface
    public void addDeviceRequest() {
        Tlog.v(TAG, " addDeviceRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDAddDevices();
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    @JavascriptInterface
    public void closeSearchRequest() {
        Tlog.v(TAG, " closeSearchRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDStopScan();
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    @JavascriptInterface
    public void turnOnBlueToothRequest() {
        Tlog.v(TAG, " turnOnBlueToothRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDTurnOnBle();
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    /**
     * h5请求断开ble
     */
    private static final int REQUEST_STATE_DISCON = 0x00;

    /**
     * h5请求连接ble
     */
    private static final int REQUEST_STATE_CON = 0x01;

    @JavascriptInterface
    public void equipmentSwitchRequest(String mac, int state) {
        Tlog.v(TAG, " equipmentSwitchRequest mac: " + mac + " state: " + state);

        if (mCallBack != null) {
            mCallBack.onJSDSwitchBle(mac, state == REQUEST_STATE_CON);
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    @JavascriptInterface
    public void socketStatusRequest(String mac, boolean receive) {
        Tlog.v(TAG, " socketStatusRequest mac: " + mac + ",receive:" + receive);
        if (mCallBack != null) {
            mCallBack.onJSDPublishSensorData(mac, receive);
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    @JavascriptInterface
    public void isOpenBluetoothRequest() {
        Tlog.v(TAG, " isOpenBluetoothRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDRequestBleState();
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }

    }

    @JavascriptInterface
    public void isFirstBindingRequest() {
        Tlog.v(TAG, " isFirstBindingRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDRequestIsFirstBinding();
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

    @JavascriptInterface
    public void deviceReconnectRequest(String mac) {
        Tlog.v(TAG, " deviceReconnectRequest ");
        if (mCallBack != null) {
            mCallBack.onJSDRequestReconnectDevice(mac);
        } else {
            Tlog.e(TAG, " IJSDeviceCallBack==null");
        }
    }

}
