package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class ColourLamp extends AbsHandlerJsInterface {


    public interface IJSColourLampCallBack {

        void onJSSetColourLampRGB(ColorLampRGB obj);

        void onJSQueryColourLampRGB(String mac);

        void onJSTurnColourLamp(String mac, boolean state);
    }


    public static final class Method {
        private static final String METHOD_COLOR_LAMP_RESPONSE = "javascript:colourLampControlResponse('$mac',$id,$r,$g,$b)";

        public static final String callJsColorLamp(String mac, int seq, int r, int g, int b) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_COLOR_LAMP_RESPONSE.replace("$mac", mac)
                    .replace("$id", String.valueOf(seq))
                    .replace("$r", String.valueOf(r))
                    .replace("$g", String.valueOf(g))
                    .replace("$b", String.valueOf(b))
                    ;
        }

        private static final String METHOD_COLOR_SWITCH_RESPONSE = "javascript:colourLampSwitchStateResponse('$mac',$state)";

        public static final String callJsColorLampSwitch(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_COLOR_SWITCH_RESPONSE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }


    }

    public static final String NAME_JSI = "ColourLamp";

    private final IJSColourLampCallBack mCallBack;


    private String TAG = H5Config.TAG;

    public ColourLamp(Looper mLooper, IJSColourLampCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    private static final int MSG_QUERY_COLOUR_LAMP = 0x2D;
    private static final int MSG_SET_COLOUR_LAMP = 0x2E;
    private static final int MSG_SET_COLOUR_LAMP_RGB = 0x2F;

    private static final int MSG_QUERY_COLOUR_LAMP_RGB = 0x30;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_COLOUR_LAMP) {

            String mac = (String) msg.obj;
            if (mCallBack != null) {
                mCallBack.onJSQueryColourLampRGB(mac);
            }

        } else if (msg.what == MSG_SET_COLOUR_LAMP) {

            String mac = (String) msg.obj;
            boolean state = msg.arg1 != 0;
            if (mCallBack != null) {
                mCallBack.onJSTurnColourLamp(mac, state);
            }

        } else if (msg.what == MSG_SET_COLOUR_LAMP_RGB) {
            if (mCallBack != null) {
                mCallBack.onJSSetColourLampRGB((ColorLampRGB) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_COLOUR_LAMP_RGB) {
            if (mCallBack != null) {
                mCallBack.onJSQueryColourLampRGB((String) msg.obj);
            }
        }

    }

    //    彩灯开关状态查询
    @JavascriptInterface
    public void colourLampSwitchStateRequest(String mac) {
        Tlog.v(TAG, " colourLampSwitchStateRequest ");
        getHandler().obtainMessage(MSG_QUERY_COLOUR_LAMP_RGB, mac).sendToTarget();
    }

    //    colourLampSwitchRequest
    @JavascriptInterface
    public void colourLampSwitchRequest(String mac, boolean state) {
        Tlog.v(TAG, " colourLampSwitchRequest " + mac + " " + state);

        ColorLampRGB mColorLampRGB = new ColorLampRGB();
        mColorLampRGB.mac = mac;
        mColorLampRGB.seq = 1;
        if (state) {
            mColorLampRGB.r = 93;
            mColorLampRGB.g = 138;
            mColorLampRGB.b = 168;
        } else {
            mColorLampRGB.r = 0;
            mColorLampRGB.g = 0;
            mColorLampRGB.b = 0;
        }
        mColorLampRGB.model = SocketSecureKey.Model.MODEL_COLOR_LAMP;
        getHandler().obtainMessage(MSG_SET_COLOUR_LAMP_RGB, mColorLampRGB).sendToTarget();

        getHandler().obtainMessage(MSG_SET_COLOUR_LAMP, state ? 1 : 0, state ? 1 : 0, mac).sendToTarget();

    }

    @JavascriptInterface
    public void colourLampControlRequest(String mac, int seq, int r, int g, int b) {
        Tlog.v(TAG, " colourLampControlRequest " + mac + " " + r + " " + g + " " + b);
        ColorLampRGB mColorLampRGB = new ColorLampRGB();
        mColorLampRGB.mac = mac;
        mColorLampRGB.seq = seq;
        mColorLampRGB.r = r;
        mColorLampRGB.g = g;
        mColorLampRGB.b = b;
        mColorLampRGB.model = SocketSecureKey.Model.MODEL_COLOR_LAMP;
        getHandler().obtainMessage(MSG_SET_COLOUR_LAMP_RGB, mColorLampRGB).sendToTarget();
    }

    @JavascriptInterface
    public void colourLampQueryRequest(String mac) {
        Tlog.v(TAG, " colourLampQueryRequest ");
        getHandler().obtainMessage(MSG_QUERY_COLOUR_LAMP_RGB, mac).sendToTarget();
    }

    @JavascriptInterface
    public void colourLampModeQueryRequest(String mac) {
        Tlog.v(TAG, " colourLampModeQueryRequest ");
    }

    @JavascriptInterface
    public void colourLampModeListQueryRequest(String mac) {
        Tlog.v(TAG, " colourLampModeListQueryRequest ");
    }

    @JavascriptInterface
    public void newColourLampModeRequest(String mac, String jsonData) {
        Tlog.v(TAG, " newColourLampModeRequest " + jsonData);
    }

    @JavascriptInterface
    public void deleteColourLampModeRequest(String mac, int modelID) {
        Tlog.v(TAG, " deleteColourLampModeRequest " + modelID);
    }


}
