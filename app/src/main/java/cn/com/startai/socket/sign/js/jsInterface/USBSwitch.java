package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class USBSwitch extends AbsHandlerJsInterface {


    public interface IJSUSBSwitchCallBack {

        void onJSQueryUSBState(String mac);

        void onJSSetUSBState(String mac, boolean state);
    }


    public static final class Method {
        private static final String METHOD_USB_STATE
                = "javascript:USBSwitchResponse('$mac',$state)";

        public static String callJsUSBState(String mac, boolean on) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_USB_STATE.replace("$mac", mac).replace("$state", String.valueOf(on));
        }

    }

    public static final String NAME_JSI = "USBSwitch";

    private final IJSUSBSwitchCallBack mCallBack;


    private String TAG = H5Config.TAG;

    public USBSwitch(Looper mLooper, IJSUSBSwitchCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    private static final int MSG_QUERY_USB = 0x2D;
    private static final int MSG_SET_USB = 0x2E;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_USB) {

            String mac = (String) msg.obj;

            mCallBack.onJSQueryUSBState(mac);

        } else if (msg.what == MSG_SET_USB) {

            String mac = (String) msg.obj;
            boolean state = msg.arg1 != 0;
            mCallBack.onJSSetUSBState(mac, state);

        }

    }

    @JavascriptInterface
    public void USBSwitchStateRequest(String mac) {
        Tlog.v(TAG, " USBSwitchStateRequest ");
        getHandler().obtainMessage(MSG_QUERY_USB, mac).sendToTarget();
    }

    @JavascriptInterface
    public void USBSwitchRequest(String mac, boolean state) {
        Tlog.v(TAG, " USBSwitchRequest " + mac + " " + state);
        getHandler().obtainMessage(MSG_SET_USB, state ? 1 : 0, state ? 1 : 0, mac).sendToTarget();
    }

}
