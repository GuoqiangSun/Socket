package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class NightLight extends AbsHandlerJsInterface {

    public interface IJSNightLightCallBack {

        void onJSSetNightLightTiming(NightLightTiming mNightLightTiming);

        void onJSSetNightLightWisdom(NightLightTiming mNightLightTiming);

        void onJSQueryNightLight(String mac);

        void onJSSetNightLight(String obj, boolean b);

        void onJSQueryRunningNightLight(String mac);

        void onJSShakeNightLight(String mac, boolean b);

        void onJSQueryShakeNightLight(String mac);

        void onJSSetNightLightColor(ColorLampRGB obj);
    }


    public static final class Method {

        private static final String NIGHT_LIGHT_PARAMS = "javascript:nightLightSettingResponse('$mac','$data')";

        public static String callNightLightData(String mac, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return NIGHT_LIGHT_PARAMS.replace("$mac", mac).replace("$data", String.valueOf(data));
        }

        private static final String NIGHT_LIGHT_SWITCH = "javascript:ordinaryNightLightResponse('$mac',$state)";

        public static String callNightLightSwitch(String mac, boolean on) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return NIGHT_LIGHT_SWITCH.replace("$mac", mac).replace("$state", String.valueOf(on));
        }

        private static final String NIGHT_LIGHT_SHAKE = "javascript:shakeItNightLightResponse('$mac',$state)";

        public static String callNightLightShake(String mac, boolean on) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return NIGHT_LIGHT_SHAKE.replace("$mac", mac).replace("$state", String.valueOf(on));
        }

        private static final String METHOD_COLOR_LAMP_RESPONSE = "javascript:ordinaryNightLightResponse('$mac',$state,$id,$r,$g,$b)";

        public static final String callJsYellowLight(String mac,boolean state, int seq, int r, int g, int b) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_COLOR_LAMP_RESPONSE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state))
                    .replace("$id", String.valueOf(seq))
                    .replace("$r", String.valueOf(r))
                    .replace("$g", String.valueOf(g))
                    .replace("$b", String.valueOf(b))
                    ;
        }

    }

    public static final String NAME_JSI = "NightLight";

    private final IJSNightLightCallBack mCallBack;


    private String TAG = H5Config.TAG;

    public NightLight(Looper mLooper, IJSNightLightCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }


    private static final int MSG_QUERY_NIGHT_LIGHT_RUNNING = 0x2D;
    private static final int MSG_SET_NIGHT_LIGHT_WISDOM = 0x2E;
    private static final int MSG_SET_NIGHT_LIGHT_TIMING = 0x2F;
    private static final int MSG_SWITCH_NIGHT_LIGHT = 0x30;
    private static final int MSG_QUERY_NIGHT_LIGHT = 0x31;
    private static final int MSG_NIGHT_LIGHT_SHAKE = 0x32;
    private static final int MSG_QUERY_NIGHT_LIGHT_SHAKE = 0x33;
    private static final int MSG_SET_NIGHT_LIGHT_COLOR = 0x34;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_NIGHT_LIGHT_RUNNING) {

            String mac = (String) msg.obj;
            if (mCallBack != null) {
                mCallBack.onJSQueryRunningNightLight(mac);
            }


        } else if (msg.what == MSG_SET_NIGHT_LIGHT_WISDOM) {

            NightLightTiming mNightLightWisdom = (NightLightTiming) msg.obj;
            if (mCallBack != null) {
                mCallBack.onJSSetNightLightWisdom(mNightLightWisdom);
            }

        } else if (msg.what == MSG_SET_NIGHT_LIGHT_TIMING) {
            NightLightTiming mNightLightTiming = (NightLightTiming) msg.obj;
            if (mCallBack != null) {
                mCallBack.onJSSetNightLightTiming(mNightLightTiming);
            }
        } else if (msg.what == MSG_SWITCH_NIGHT_LIGHT) {
            if (mCallBack != null) {
                mCallBack.onJSSetNightLight((String) msg.obj, msg.arg1 == 1);
            }
        } else if (msg.what == MSG_QUERY_NIGHT_LIGHT) {
            if (mCallBack != null) {
                mCallBack.onJSQueryNightLight((String) msg.obj);
            }
        } else if (msg.what == MSG_NIGHT_LIGHT_SHAKE) {
            if (mCallBack != null) {
                mCallBack.onJSShakeNightLight((String) msg.obj, msg.arg1 == 1);
            }
        } else if (msg.what == MSG_QUERY_NIGHT_LIGHT_SHAKE) {
            if (mCallBack != null) {
                mCallBack.onJSQueryShakeNightLight((String) msg.obj);
            }
        } else if (msg.what == MSG_SET_NIGHT_LIGHT_COLOR) {
            if (mCallBack != null) {
                mCallBack.onJSSetNightLightColor((ColorLampRGB) msg.obj);
            }
        }

    }


    @JavascriptInterface
    public void shakeItNightLightDataRequest(String mac) {
        Tlog.v(TAG, " shakeItNightLightDataRequest ");
        getHandler().obtainMessage(MSG_QUERY_NIGHT_LIGHT_SHAKE, mac).sendToTarget();
    }

    @JavascriptInterface
    public void shakeItNightLightRequest(String mac, boolean state) {
        Tlog.v(TAG, " shakeItNightLightRequest " + state);
        int a = state ? 1 : 0;
        getHandler().obtainMessage(MSG_NIGHT_LIGHT_SHAKE, a, a, mac).sendToTarget();
    }

    @JavascriptInterface
    public void ordinaryNightLightDataRequest(String mac) {
        Tlog.v(TAG, " ordinaryNightLightDataRequest ");
        getHandler().obtainMessage(MSG_QUERY_NIGHT_LIGHT, mac).sendToTarget();
    }

    @JavascriptInterface
    public void ordinaryNightLightRequest(String mac, boolean on) {
        Tlog.v(TAG, " ordinaryNightLightRequest ");
        int i = on ? 1 : 0;
        getHandler().obtainMessage(MSG_SWITCH_NIGHT_LIGHT, i, i, mac).sendToTarget();
    }


    @JavascriptInterface
    public void ordinaryNightLightRequest(String mac, boolean on, int id, int r, int g, int b) {
        Tlog.v(TAG, " ordinaryNightLightRequest mac : " + mac + " id:" + id + " r:" + r + " g:" + g + " b:" + b);
        ColorLampRGB colorLampRGB
                = new ColorLampRGB();
        colorLampRGB.mac = mac;
        colorLampRGB.seq = id;
        colorLampRGB.model = SocketSecureKey.Model.MODEL_YELLOW_LIGHT;
        colorLampRGB.r = r;
        colorLampRGB.g = g;
        colorLampRGB.b = b;
        getHandler().obtainMessage(MSG_SET_NIGHT_LIGHT_COLOR, colorLampRGB).sendToTarget();

    }

    @JavascriptInterface
    public void nightLightSettingRequest(String mac) {
        Tlog.v(TAG, " nightLightSettingRequest ");
        getHandler().obtainMessage(MSG_QUERY_NIGHT_LIGHT_RUNNING, mac).sendToTarget();
    }

    @JavascriptInterface
    public void nightLightSwitchRequest(String mac, int id, boolean startup) {
        Tlog.v(TAG, " nightLightSwitchRequest " + mac + " startup:" + startup);

        NightLightTiming mNightLight = new NightLightTiming();
        mNightLight.mac = mac;
        mNightLight.id = id;
        mNightLight.startup = startup;
        mNightLight.startTime = "00:00";
        mNightLight.startHour = 0;
        mNightLight.startMinute = 0;
        mNightLight.endTime = "23:59";
        mNightLight.stopHour = 23;
        mNightLight.stopMinute = 59;
        getHandler().obtainMessage(MSG_SET_NIGHT_LIGHT_WISDOM, mNightLight).sendToTarget();

    }

    @JavascriptInterface
    public void timingNightLightRequest(String mac, int id, boolean state, String startTime, String endTime) {
        Tlog.v(TAG, " timingNightLightRequest " + mac + " startup:" + state
                + " startTime:" + startTime + " endTime:" + endTime);
        NightLightTiming mNightLight = new NightLightTiming();
        mNightLight.mac = mac;
        mNightLight.id = id;
        mNightLight.startup = state;
        mNightLight.startTime = startTime;

        String[] split = mNightLight.startTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                mNightLight.startHour = Integer.parseInt(s);
            } catch (Exception e) {
                mNightLight.startHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                mNightLight.startMinute = Integer.parseInt(s);
            } catch (Exception e) {
                mNightLight.startMinute = 0;
            }
        }

        mNightLight.endTime = endTime;

        split = mNightLight.endTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                mNightLight.stopHour = Integer.parseInt(s);
            } catch (Exception e) {
                mNightLight.stopHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                mNightLight.stopMinute = Integer.parseInt(s);
            } catch (Exception e) {
                mNightLight.stopMinute = 0;
            }
        }

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " timingNightLightRequest:" + String.valueOf(mNightLight));
        }
        getHandler().obtainMessage(MSG_SET_NIGHT_LIGHT_TIMING, mNightLight).sendToTarget();
    }


}
