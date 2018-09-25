package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class Countdown extends AbsHandlerJsInterface {


    public interface IJSCountdownCallBack {

        /**
         * 倒计时页面数据请求
         *
         * @param mac
         */
        void onJSCQueryCountdownData(String mac);

        /**
         * 电源倒计时开关
         */
        void onJSCPowerCountdown(PowerCountdown mPowerCountDown);

    }


    public static final class Method {

        private static final String METHOD_POWER_COUNTDOWN_RESPONSE = "javascript:powerSwitchCountdownResponse('$mac',$state,$result)";

        /**
         * 电源倒计时设置
         *
         * @param mac
         * @param status
         * @return
         */
        public static final String callJsPowerCountdown(String mac, boolean result, boolean status) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_POWER_COUNTDOWN_RESPONSE.replace("$mac", mac).replace("$state", String.valueOf(status)).replace("$result", String.valueOf(result));
        }

        private static final String METHOD_POWER_COUNTDOWN_DATA_RESPONSE = "javascript:countdownDataResponse('$mac','$data')";

        /**
         * 倒计时详情数据
         *
         * @param mac
         * @param jsonData
         * @return
         */
        public static final String callJsPowerCountdownData(String mac, String jsonData) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_POWER_COUNTDOWN_DATA_RESPONSE.replace("$mac", mac).replace("$data", jsonData);
        }
    }


    public static final String NAME_JSI = "Countdown";

    private String TAG = H5Config.TAG;

    private final IJSCountdownCallBack mCallBack;

    public Countdown(Looper mLooper, IJSCountdownCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void countdownDataRequest(String mac) {
        Tlog.v(TAG, " countdownDataRequest mac : " + mac);
        getHandler().obtainMessage(MSG_QUERY_COUNTDOWN_DATA, mac).sendToTarget();


    }

    /**
     * @param mac
     * @param hour
     * @param minute
     * @param startup 开机 关机
     * @param on      启动，结束
     */
    @JavascriptInterface
    public void powerSwitchCountdownRequest(String mac, int hour, int minute, boolean startup, boolean on) {
        Tlog.v(TAG, " powerSwitchCountdownRequest mac: " + mac + " on:" + on + " startup:" + startup + " hour:" + hour + " minute:" + minute);


        PowerCountdown mPowerCountDown = new PowerCountdown();
        mPowerCountDown.setMac(mac);
        mPowerCountDown.setStatus(on);
        mPowerCountDown.setSwitchGear(startup);
        mPowerCountDown.setHour(hour);
        mPowerCountDown.setMinute(minute);
        mPowerCountDown.setSysTime(System.currentTimeMillis());

        getHandler().obtainMessage(MSG_SET_COUNTDOWN, mPowerCountDown).sendToTarget();


    }

    private static final int MSG_QUERY_COUNTDOWN_DATA = 0x0D;
    private static final int MSG_SET_COUNTDOWN = 0x0E;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_COUNTDOWN_DATA) {

            if (mCallBack != null) {
                mCallBack.onJSCQueryCountdownData((String) msg.obj);
            }

        } else if (msg.what == MSG_SET_COUNTDOWN) {
            if (mCallBack != null) {
                mCallBack.onJSCPowerCountdown((PowerCountdown) msg.obj);
            }
        }

    }

}
