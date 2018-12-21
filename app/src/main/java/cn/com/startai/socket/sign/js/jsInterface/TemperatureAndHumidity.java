package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
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

        void onJSTHSetTemperatureTimingAlarm(TimingTempHumiData obj);

        void onJSTHQueryTemperatureTimingAlarm(String obj, int model);
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
                = "javascript:alarmHumidityValueResponse('$mac',$state,$result,$limit)";

        /**
         * 设置告警湿度值返回
         *
         * @param mac
         * @param result
         * @return
         */
        public static final String callJsHumidityAlarmValue(String mac, boolean state, boolean result, int limit) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_HUMIDITY_ALARM_VALUE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state))
                    .replace("$result", String.valueOf(result))
                    .replace("$limit", String.valueOf(limit));
        }


        private static final String METHOD_TEMP_TIMING_ALARM_VALUE
                = "javascript:alarmTimerAndTemperatureValueResponse('$mac',$state,$model,$result)";

        public static final String callJsTempTimingSetAlarmValue(String mac, boolean state, int model, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMP_TIMING_ALARM_VALUE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state))
                    .replace("$model", String.valueOf(model))
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_TEMP_TIMING_QUERY_ALARM_VALUE
                = "javascript:timingAndTemperatureDataResponse('$mac','$data')";

        public static final String callJsTempTimingQueryAlarmValue(String mac, String jsonData) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMP_TIMING_QUERY_ALARM_VALUE.replace("$mac", mac)
                    .replace("$data", String.valueOf(jsonData));
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

    @JavascriptInterface
    public void timingAndTemperatureDataRequest(String mac, int model) {
        Tlog.v(TAG, " timingAndTemperatureDataRequest mac:" + mac + " " + model);
        getHandler().obtainMessage(MSG_QUERY_TEMP_TIMING, model, model, mac).sendToTarget();
    }


    @JavascriptInterface
    public void alarmTimingAndTemperatureValueRequest(String mac, int id, boolean on,
                                                      String time, String offTime, int week, boolean startup,
                                                      String onTimeInterval, String offTimeInterval,
                                                      int model, int alarmValue) {

        Tlog.v(TAG, " alarmTimingAndTemperatureValueRequest id:" + id + " on:" + on
                + " time:" + time + " week:" + week + " startup:" + startup +
                " model:" + model + " offTimeInterval:"
                + offTimeInterval + " onTimeInterval:" + onTimeInterval
                + " offTime:" + offTime
                + " alarmValue:" + alarmValue
        );

        TimingTempHumiData mTimingAdvanceData = new TimingTempHumiData();
        mTimingAdvanceData.setTypeIsTemp();
        mTimingAdvanceData.setAlarmValue(alarmValue);
        mTimingAdvanceData.setModel((byte) model);
        mTimingAdvanceData.mac = mac;
        mTimingAdvanceData.id = (byte) id;
        mTimingAdvanceData.setStateIsConfirm();
        mTimingAdvanceData.on = on;
        mTimingAdvanceData.setOnTimeSplit(time);
        mTimingAdvanceData.week = week;
        mTimingAdvanceData.startup = startup;
        mTimingAdvanceData.setOnIntervalTime(onTimeInterval);
        mTimingAdvanceData.setOffIntervalTime(offTimeInterval);
        mTimingAdvanceData.setOffTimeSplit(offTime);
        getHandler().obtainMessage(MSG_SET_TEMP_TIMING, mTimingAdvanceData).sendToTarget();

    }

    private static final int MSG_QUERY_TEMP_HUMIDITY = 0x0F;

    private static final int MSG_SET_TEMP_ALARM = 0x10;

    private static final int MSG_SET_HUMIDITY_ALARM = 0x11;

    private static final int MSG_SET_TEMP_TIMING = 0x12;

    private static final int MSG_QUERY_TEMP_TIMING = 0x13;

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
        } else if (msg.what == MSG_SET_TEMP_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTHSetTemperatureTimingAlarm((TimingTempHumiData) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_TEMP_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTHQueryTemperatureTimingAlarm((String) msg.obj, msg.arg1);
            }
        }

    }
}
