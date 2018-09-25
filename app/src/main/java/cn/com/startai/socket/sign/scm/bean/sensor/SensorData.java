package cn.com.startai.socket.sign.scm.bean.sensor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class SensorData {

    public Humidity mHumidity = new Humidity();
    public Power mPower = new Power();
    public Temperature mTemperature = new Temperature();

    public float powerFactor;

    public float voltage;
    public float maxVoltage;

    public float electricity;
    public float frequency;

    public int weight;
    public int cost;

    public boolean on; //
    public long time; // 倒计时

    public void clear() {
        this.weight = 0;
        this.cost = 0;
        this.time = 0;
        this.on = false;
        this.voltage = 0;
        this.electricity = 0;
        this.frequency = 0;

        this.mTemperature.clear();
        this.mPower.clear();
        this.mHumidity.clear();
    }

    private static final String NULL_SENSOR = "{}";

    public String toJsonStr() {

        SensorData mSensorData = this;

        try {

            JSONObject root = new JSONObject();

            JSONObject powerJson = new JSONObject();
            powerJson.put("value", mSensorData.mPower.value);
            powerJson.put("averageValue", mSensorData.mPower.averageValue);
            powerJson.put("maximumValue", mSensorData.mPower.maximumValue);
            root.put("power", powerJson);


            root.put("powerFactor", mSensorData.powerFactor);
            root.put("voltage", mSensorData.voltage);
            root.put("maxVoltage", mSensorData.maxVoltage);

            root.put("electricity", mSensorData.electricity);
            root.put("frequency", mSensorData.frequency);

            JSONObject temperatureJson = new JSONObject();
            temperatureJson.put("value", mSensorData.mTemperature.value);
            temperatureJson.put("alarmValue", mSensorData.mTemperature.alarmValue);
            root.put("temperature", temperatureJson);

            JSONObject humidityJson = new JSONObject();
            humidityJson.put("value", mSensorData.mHumidity.value);
            humidityJson.put("alarmValue", mSensorData.mHumidity.alarmValue);
            root.put("humidity", humidityJson);

            root.put("weight", mSensorData.weight);
            root.put("cost", mSensorData.cost);
            root.put("time", mSensorData.time);
            root.put("countDownType", mSensorData.on ? "ON" : "OFF");

            return root.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return NULL_SENSOR;
    }


//         *              "{
//        *             Power: {
//         *             value：2339,
//         *             averageValue: 1258,
//         *             maximumValue: 3410
//                *             },

//            *             Voltage： 22.3,
//            *             electricity： 22.4,
//            *             frequency：22.5,

//            *             temperature: {
//         *             value: 21,
//         *             alarmValue: 100
//                *             },

//            *             humidity: {
//         *             value: 22,
//         *             alarmValue: 60
//                *             },

//            *             weight: 23,
//            *             cost: 166,
//            *             countDown: 1523261370549

//            *             }"

}
