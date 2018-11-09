package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class TemperatureAndHumidity extends AbsHandlerJsInterface {


    public interface ITemperatureHumidityCallBack {

        void onJSTHQueryTempHumidity(String mac);

        /**
         * 设置温度报警值
         */
        void onJSTHSetTemperatureAlarmValue(TempHumidityAlarmData mAlarmData);

        /**
         * 设置湿度的报警值
         */
        void onJSTHSetHumidityAlarmValue(TempHumidityAlarmData mAlarmData);

    }

    public static final class Method {

        private static final String METHOD_TEMPERATURE_HUMIDITY_DATA
                = "javascript:temperatureAndHumidityDataResponse('$mac','$data')";

        /**
         * 温湿度数据返回
         *
         * @param mac
         * @param data
         * @return
         */
        public static final String callJsTemperatureHumidity(String mac, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMPERATURE_HUMIDITY_DATA.replace("$mac", mac).replace("$data", data);
        }

        private static final String METHOD_TEMPERATURE_ALARM_VALUE
                = "javascript:alarmTemperatureValueResponse('$mac',$state,$model,$result)";

        /**
         * 设置告警温度值返回
         *
         * @param mac
         * @param state
         * @param result
         * @return
         */
        public static final String callJsTemperatureAlarmValue(String mac, boolean state, boolean result, int model) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMPERATURE_ALARM_VALUE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state))
                    .replace("$result", String.valueOf(result))
                    .replace("$model", String.valueOf(model));
        }

        private static final String METHOD_HUMIDITY_ALARM_VALUE
                = "javascript:alarmHumidityValueResponse('$mac',$state,$result)";

        /**
         * 设置告警湿度值返回
         *
         * @param mac
         * @param result
         * @return
         */
        public static final String callJsHumidityAlarmValue(String mac, boolean state, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_HUMIDITY_ALARM_VALUE.replace("$mac", mac).replace("$state", String.valueOf(state)).replace("$result", String.valueOf(result));
        }

    }

    public static final String NAME_JSI = "TemperatureAndHumidity";

    private String TAG = H5Config.TAG;

    private final ITemperatureHumidityCallBack mCallBack;

    public TemperatureAndHumidity(Looper mLooper, ITemperatureHumidityCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }


    @JavascriptInterface
    public void temperatureAndHumidityDataRequest(String mac) {
        Tlog.v(TAG, " temperatureAndHumidityDataRequest  mac:" + mac);

        getHandler().obtainMessage(MSG_QUERY_TEMP_HUMIDITY, mac).sendToTarget();

    }

    /**
     * 设置告警温度值请求
     */
    @JavascriptInterface
    public void alarmTemperatureValueRequest(String mac, boolean powerState, float alarmValue, int limit) {
        Tlog.v(TAG, " alarmTemperatureValueRequest mac:" + mac
                + " powerState:" + powerState
                + " alarmValue:" + alarmValue
                + " limit:" + limit);

        TempHumidityAlarmData mAlarmData = new TempHumidityAlarmData();
        mAlarmData.setTypeIsTemp();
        mAlarmData.setMac(mac);
        mAlarmData.setStartup(powerState);
        mAlarmData.setAlarmValue(alarmValue);
        mAlarmData.setLimit(limit);
        getHandler().obtainMessage(MSG_SET_TEMP_ALARM, mAlarmData).sendToTarget();

    }

    /**
     * 设置告警湿度值请求
     */
    @JavascriptInterface
    public void alarmHumidityValueRequest(String mac, boolean powerState, float alarmValue, int limit) {
        Tlog.v(TAG, " alarmHumidityValueRequest mac:" + mac
                + " powerState:" + powerState
                + " alarmValue:" + alarmValue
                + " limit:" + limit);

        TempHumidityAlarmData mAlarmData = new TempHumidityAlarmData();
        mAlarmData.setTypeIsHumidity();
        mAlarmData.setMac(mac);
        mAlarmData.setStartup(powerState);
        mAlarmData.setAlarmValue(alarmValue);
        mAlarmData.setLimit(limit);
        getHandler().obtainMessage(MSG_SET_HUMIDITY_ALARM, mAlarmData).sendToTarget();


    }

    private static final int MSG_QUERY_TEMP_HUMIDITY = 0x0F;

    private static final int MSG_SET_TEMP_ALARM = 0x10;

    private static final int MSG_SET_HUMIDITY_ALARM = 0x11;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_TEMP_HUMIDITY) {

            if (mCallBack != null) {
                mCallBack.onJSTHQueryTempHumidity((String) msg.obj);
            }
        } else if (msg.what == MSG_SET_TEMP_ALARM) {
            if (mCallBack != null) {
                mCallBack.onJSTHSetTemperatureAlarmValue((TempHumidityAlarmData) msg.obj);
            }
        } else if (msg.what == MSG_SET_HUMIDITY_ALARM) {
            if (mCallBack != null) {
                mCallBack.onJSTHSetHumidityAlarmValue((TempHumidityAlarmData) msg.obj);
            }
        }

    }
}
