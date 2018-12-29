package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class Main extends AbsHandlerJsInterface {


    public interface IJSMainCallBack {

        /**
         * 控制继电器开关
         *
         * @param mac
         * @param status
         */
        void onJSMSwitchRelay(String mac, boolean status);

        /**
         * @param mac
         */
        void onJSMQueryRelayStatus(String mac);

        /**
         * @param mac
         */
        void onJSMSystemSetup(String mac);

    }

    public static final class Method {

        private static final String METHOD_POWER_SWITCH_RESPONSE = "javascript:powerSwitchResponse('$mac',$result)";

        /**
         * 继电器开关回调js的函数
         *
         * @return
         */
        public static final String callJsSwitchPower(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_POWER_SWITCH_RESPONSE.replace("$mac", mac).replace("$result", String.valueOf(result));
        }


    }


    public static final String NAME_JSI = "Main";

    private String TAG = H5Config.TAG;

    private final IJSMainCallBack mCallBack;

    public Main(Looper mLooper, IJSMainCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void powerSwitchRequest(String mac, boolean status) {
        Tlog.v(TAG, " powerSwitchRequest mac: " + mac + ",status:" + status);

        if (status) {
            getHandler().obtainMessage(MSG_TURN_ON_RELAY, mac).sendToTarget();
        } else {
            getHandler().obtainMessage(MSG_TURN_OFF_RELAY, mac).sendToTarget();
        }

    }

    @JavascriptInterface
    public void powerSwitchStatusRequest(String mac) {
        Tlog.v(TAG, " powerSwitchStatusRequest mac: " + mac);

        getHandler().obtainMessage(MSG_QUERY_RELAY_STATUS, mac).sendToTarget();

    }

    @JavascriptInterface
    public void systemSetupRequest(String mac) {
        Tlog.v(TAG, " systemSetupRequest mac: " + mac);
        if (mCallBack != null) {
            mCallBack.onJSMSystemSetup(mac);
        }
    }

    private static final int MSG_TURN_ON_RELAY = 0x0A;

    private static final int MSG_TURN_OFF_RELAY = 0x0B;

    private static final int MSG_QUERY_RELAY_STATUS = 0x0C;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_TURN_ON_RELAY) {
            if (mCallBack != null) {
                mCallBack.onJSMSwitchRelay((String) msg.obj, true);
            }

        } else if (msg.what == MSG_TURN_OFF_RELAY) {

            if (mCallBack != null) {
                mCallBack.onJSMSwitchRelay((String) msg.obj, false);
            }

        } else if (msg.what == MSG_QUERY_RELAY_STATUS) {

            if (mCallBack != null) {
                mCallBack.onJSMQueryRelayStatus((String) msg.obj);
            }
        }

    }

}
