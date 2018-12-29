package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/12 0012
 * desc :
 */
public class SpendingCountdown extends AbsHandlerJsInterface {


    public interface IJSSpendingCallBack {

        void onJSQuerySpendingCountdownData(String mac);

        void onJSSetSpendingCountdownAlarm(SpendingElectricityData mSpendingCountdownData);

    }

    public static final class Method {
        private static final String SPENDING_COUNTDOWN_DATA
                = "javascript:spendingCountdownDataResponse('$mac',$result,'$data')";

        public static String callJsSpendingCountdownData(String mac, boolean result, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return SPENDING_COUNTDOWN_DATA.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$data", data);
        }

        private static final String SPENDING_COUNTDOWN_ALARM
                = "javascript:spendingCountdownAlarmResponse('$mac',$model,$state,$result)";

        public static String callJsSpendingCountdownAlarm(String mac, int model, boolean state, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return SPENDING_COUNTDOWN_ALARM.replace("$mac", mac)
                    .replace("$model", String.valueOf(model))
                    .replace("$state", String.valueOf(state))
                    .replace("$result", String.valueOf(result));
        }

    }

    public static final String NAME_JSI = "SpendingCountdown";

    private String TAG = H5Config.TAG;

    private final IJSSpendingCallBack mCallBack;

    public SpendingCountdown(Looper mLooper, IJSSpendingCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }


    @JavascriptInterface
    public void spendingCountdownDataRequest(String mac) {
        Tlog.v(TAG, " spendingCountdownDataRequest ");

        getHandler().obtainMessage(MSG_QUERY_SPENDING_COUNTDOWN_DATA, mac).sendToTarget();
    }

    @JavascriptInterface
    public void spendingCountdownAlarmRequest(String mac, int model,
                                              int alarmValue, boolean alarmSwitch,
                                              int year, int month, int day) {
        Tlog.v(TAG, " spendingCountdownAlarmRequest model:" + model
                + " alarmValue:" + alarmValue + " alarmSwitch:" + alarmSwitch
                + " year:" + year + " month:" + month + " day:" + day);

        SpendingElectricityData mSpendingCountdownData = new SpendingElectricityData();
        mSpendingCountdownData.mac = mac;
        mSpendingCountdownData.model = model;
        mSpendingCountdownData.alarmValue = alarmValue;
        mSpendingCountdownData.alarmSwitch = alarmSwitch;
        mSpendingCountdownData.year = year - 2000;
        mSpendingCountdownData.month = month;
        mSpendingCountdownData.day = day;
        getHandler().obtainMessage(MSG_SET_SPENDING_COUNTDOWN_DATA, mSpendingCountdownData).sendToTarget();
    }

    private static final int MSG_QUERY_SPENDING_COUNTDOWN_DATA = 0x2E;

    private static final int MSG_SET_SPENDING_COUNTDOWN_DATA = 0x2F;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_SPENDING_COUNTDOWN_DATA) {
            if (mCallBack != null) {
                mCallBack.onJSQuerySpendingCountdownData((String) msg.obj);
            }
        } else if (msg.what == MSG_SET_SPENDING_COUNTDOWN_DATA) {
            if (mCallBack != null) {
                mCallBack.onJSSetSpendingCountdownAlarm((SpendingElectricityData) msg.obj);
            }
        }
    }

}
