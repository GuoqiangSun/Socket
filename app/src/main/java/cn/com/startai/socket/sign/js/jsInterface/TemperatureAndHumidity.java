package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.ConstTempTiming;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

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

        void onJSQueryTemperatureSensor(String mac);

        void onJSQueryElectricQuantity(String mac);

        void onJSQueryBleDevice(String mac);

        void onJSTHQueryConstTemperatureTimingAlarm(String mac, int model);

        void onJSTHSetConstTemperatureTimingAlarm(ConstTempTiming mConstTempTiming);

        void onJSTHDelConstTemperatureTimingAlarm(ConstTempTiming mConstTempTiming);

    }

    public static final class Method {

        private static final String METHOD_TEMPERATURE_SENSOR_STATE_REPORT
                = "javascript:temperatureSensorReportStateResponse('$mac',$state)";


        public static final String callJsTemperatureSensorStateReport(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMPERATURE_SENSOR_STATE_REPORT.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }


        private static final String METHOD_TEMPERATURE_SENSOR_STATE
                = "javascript:temperatureSensorStateResponse('$mac',$state)";

        public static final String callJsTemperatureSensorState(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMPERATURE_SENSOR_STATE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }


        private static final String METHOD_BLE_SENSOR_STATE
                = "javascript:temperatureSensorStateResponse('$mac',$state)";

        public static final String callJsBleSensorState(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_BLE_SENSOR_STATE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }


        private static final String METHOD_POWER_SENSOR_STATE
                = "javascript:temperatureSensorPowerStateResponse('$mac',$state)";

        public static final String callJsPowerSensorState(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_POWER_SENSOR_STATE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }

        private static final String METHOD_QUERY_CONST_TEMP_TIMING_
                = "javascript:timingConstTemperatureDataResponse('$mac',$model,'$data')";

        public static final String callJsQueryConstTempTiming(String mac, int model, String jsonData) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_CONST_TEMP_TIMING_.replace("$mac", mac)
                    .replace("$model", String.valueOf(model)).replace("$data", jsonData);
        }

        private static final String METHOD_SET_CONST_TEMP_TIMING_
                = "javascript:timingConstTemperatureDataSetResponse('$mac',$result,$id,$model,$startup,$minTemp,$maxTemp,$week,$startHour,$startMinute,$endHour,$endMinute)";

        public static final String callJsSetConstTempTiming(String mac,int result, int id, int model, int startup,
                                                            int minTemp, int maxTemp, int week, int startHour, int startMinute,
                                                            int endHour, int endMinute) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_SET_CONST_TEMP_TIMING_.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$id", String.valueOf(id))
                    .replace("$model", String.valueOf(model))
                    .replace("$startup", String.valueOf(startup))
                    .replace("$minTemp", String.valueOf(minTemp))
                    .replace("$maxTemp", String.valueOf(maxTemp))
                    .replace("$week", String.valueOf(week))
                    .replace("$startHour", String.valueOf(startHour))
                    .replace("$startMinute", String.valueOf(startMinute))
                    .replace("$endHour", String.valueOf(endHour))
                    .replace("$endMinute", String.valueOf(endMinute))
                    ;
        }

        private static final String METHOD_DEL_CONST_TEMP_TIMING_
                = "javascript:timingConstTemperatureDataDeleteResponse('$mac',$result,$id,$model)";

        public static final String callJsDelConstTempTiming(String mac, int result, int id,int model) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_DEL_CONST_TEMP_TIMING_.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$id", String.valueOf(id))
                    .replace("$model", String.valueOf(model));
        }

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

    @JavascriptInterface
    public void temperatureSensorPowerStateRequest(String mac) {
        Tlog.v(TAG, " temperatureSensorPowerStateRequest mac:" + mac);
        getHandler().obtainMessage(MSG_QUERY_ELECTRIC_QUANTITY, mac).sendToTarget();
    }

    @JavascriptInterface
    public void temperatureSensorBlueStateRequest(String mac) {
        Tlog.v(TAG, " temperatureSensorBlueStateRequest mac:" + mac);
        getHandler().obtainMessage(MSG_QUERY_BLE_DEVICE, mac).sendToTarget();
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

    ///////
    @JavascriptInterface
    public void timingConstTemperatureDataRequest(String mac, int model) {
        Tlog.v(TAG, " timingConstTemperatureDataRequest mac:" + mac + " model:" + model);
        getHandler().obtainMessage(MSG_QUERY_CONST_TEMP_TIMING, model, model, mac).sendToTarget();
    }


    @JavascriptInterface
    public void timingConstTemperatureDataSet(String mac, int id, int model, int startup, int minTemp, int maxTemp, int week,
                                              int startHour, int startMinute,
                                              int endHour, int endMinute) {
        Tlog.v(TAG, " timingConstTemperatureDataSet mac:" + mac + " id:" + id
                + " model:" + model + " startup:" + startup + " minTemp:" + minTemp
                + " maxTemp:" + maxTemp + " week:" + week
                + " startHour:" + startHour + " startMinute:" + startMinute
                + " endHour:" + endHour + " endMinute:" + endMinute);

        ConstTempTiming mTiming = new ConstTempTiming();
        mTiming.mac = mac;
        mTiming.id = id;
        mTiming.model = model;
        mTiming.startup = startup;
        mTiming.minTemp = minTemp;
        mTiming.maxTemp = maxTemp;
        mTiming.week = week;
        mTiming.startHour = startHour;
        mTiming.startMinute = startMinute;
        mTiming.endHour = endHour;
        mTiming.endMinute = endMinute;

        getHandler().obtainMessage(MSG_SET_CONST_TEMP_TIMING, mTiming).sendToTarget();

    }

    @JavascriptInterface
    public void timingConstTemperatureDataDelete(String mac, int id, int model) {
        Tlog.v(TAG, " timingConstTemperatureDataDelete mac:" + mac + " id:" + id + " model:" + model);
        ConstTempTiming mTiming = new ConstTempTiming();
        mTiming.mac = mac;
        mTiming.id = id;
        mTiming.model = model;
        getHandler().obtainMessage(MSG_DEL_CONST_TEMP_TIMING, mTiming).sendToTarget();
    }

    ///////
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


    @JavascriptInterface
    public void temperatureSensorStateRequest(String mac) {
        Tlog.v(TAG, " temperatureSensorStateRequest mac:" + mac);
        getHandler().obtainMessage(MSG_QUERY_TEMP_SENSOR, mac).sendToTarget();
    }

    private static final int MSG_QUERY_TEMP_HUMIDITY = 0x0F;

    private static final int MSG_SET_TEMP_ALARM = 0x10;

    private static final int MSG_SET_HUMIDITY_ALARM = 0x11;

    private static final int MSG_SET_TEMP_TIMING = 0x12;

    private static final int MSG_QUERY_TEMP_TIMING = 0x13;

    private static final int MSG_QUERY_TEMP_SENSOR = 0x14;

    private static final int MSG_QUERY_ELECTRIC_QUANTITY = 0x15;
    private static final int MSG_QUERY_BLE_DEVICE = 0x16;


    private static final int MSG_QUERY_CONST_TEMP_TIMING = 0x17;
    private static final int MSG_SET_CONST_TEMP_TIMING = 0x18;
    private static final int MSG_DEL_CONST_TEMP_TIMING = 0x19;

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
        } else if (msg.what == MSG_QUERY_TEMP_SENSOR) {
            if (mCallBack != null) {
                mCallBack.onJSQueryTemperatureSensor((String) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_ELECTRIC_QUANTITY) {
            if (mCallBack != null) {
                mCallBack.onJSQueryElectricQuantity((String) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_BLE_DEVICE) {
            if (mCallBack != null) {
                mCallBack.onJSQueryBleDevice((String) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_CONST_TEMP_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTHQueryConstTemperatureTimingAlarm((String) msg.obj, msg.arg1);
            }
        } else if (msg.what == MSG_SET_CONST_TEMP_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTHSetConstTemperatureTimingAlarm((ConstTempTiming) msg.obj);
            }
        } else if (msg.what == MSG_DEL_CONST_TEMP_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTHDelConstTemperatureTimingAlarm((ConstTempTiming) msg.obj);
            }
        }

    }
}
