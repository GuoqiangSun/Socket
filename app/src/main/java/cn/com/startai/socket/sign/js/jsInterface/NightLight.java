package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class NightLight extends AbsHandlerJsInterface {


    public interface IJSNightLightCallBack {


    }


    public static final class Method {

        private static final String NIGHT_LIGHT_PARAMS = "javascript:nightLightSettingResponse('$mac','$data')";

        public static String callQueryNigthLight(String mac, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return NIGHT_LIGHT_PARAMS.replace("$mac", mac).replace("$data", String.valueOf(data));
        }

    }

    public static final String NAME_JSI = "NightLight";

    private final IJSNightLightCallBack mCallBack;


    private String TAG = H5Config.TAG;

    public NightLight(Looper mLooper, IJSNightLightCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    private static final int MSG_QUERY_NIGHT_LIGHT = 0x2D;
    private static final int MSG_SET_NIGHT_LIGHT = 0x2E;
    private static final int MSG_SET_NIGHT_LIGHT_TIMING = 0x2F;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_NIGHT_LIGHT) {

            String mac = (String) msg.obj;

        } else if (msg.what == MSG_SET_NIGHT_LIGHT) {

            String mac = (String) msg.obj;
            boolean state = msg.arg1 != 0;


        } else if (msg.what == MSG_SET_NIGHT_LIGHT_TIMING) {
            NightLightTiming mNightLightTiming = (NightLightTiming) msg.obj;
        }

    }

    @JavascriptInterface
    public void nightLightSettingRequest(String mac) {
        Tlog.v(TAG, " nightLightSettingRequest ");
        getHandler().obtainMessage(MSG_QUERY_NIGHT_LIGHT, mac).sendToTarget();
    }


    @JavascriptInterface
    public void nightLightSwitchRequest(String mac, boolean state) {
        Tlog.v(TAG, " nightLightSwitchRequest " + mac + " " + state);
        getHandler().obtainMessage(MSG_SET_NIGHT_LIGHT, state ? 1 : 0, state ? 1 : 0, mac).sendToTarget();
    }

    @JavascriptInterface
    public void timingNightLightRequest(String mac, boolean state, String startTime, String endTime) {
        Tlog.v(TAG, " timingNightLightRequest " + mac + " " + state + " " + startTime + " " + endTime);
        NightLightTiming mNightLight = new NightLightTiming();
        mNightLight.mac = mac;
        mNightLight.state = state;
        mNightLight.startTime = startTime;
        mNightLight.endTime = endTime;
        getHandler().obtainMessage(MSG_SET_NIGHT_LIGHT_TIMING, mNightLight).sendToTarget();
    }


}
