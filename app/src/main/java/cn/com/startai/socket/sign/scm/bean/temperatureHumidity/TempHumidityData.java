package cn.com.startai.socket.sign.scm.bean.temperatureHumidity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/7 0007
 * desc :
 */
public class TempHumidityData {

    /**
     * '{
     "temperature": {
     "currentValue": 23,
     "alarmValue": 50,
     "alarmSwitch": false
     },
     "humidity": {
     "currentValue": 30,
     "alarmValue": 40,
     "alarmSwitch": false
     }
     }'
     */


    /**
     * '{
     * "temperature": {
     * "currentValue": 23,
     * "hotAlarmValue": 50,
     * "hotAlarmSwitch": false,
     * "codeAlarmValue": 50,
     * "codeAlarmSwitch": false
     * },
     * "humidity": { // 目前可不返回数据
     * "currentValue": 30,
     * "alarmValue": 40,
     * "alarmSwitch": false
     * }
     * }'
     */


    private float tempHotAlarmData;

    public void setTempHotAlarmData(float tempHotAlarmData) {
        this.tempHotAlarmData = tempHotAlarmData;
    }

    public void useSetTempHotAlarmData() {
        if (tempHotAlarmData != 0F) {
            mTemperature.hotAlarmValue = tempHotAlarmData;
        }
    }


    private float tempCodeAlarmData;

    public void setTempCodeAlarmData(float tempCodeAlarmData) {
        this.tempCodeAlarmData = tempCodeAlarmData;
    }

    public void useSetTempCodeAlarmData() {
        if (tempCodeAlarmData != 0F) {
            mTemperature.codeAlarmValue = tempCodeAlarmData;
        }
    }

    public Temperature mTemperature = new Temperature();
    public Humidity mHumidity = new Humidity();

    public void clearAll() {
        mTemperature.clearAll();
        mHumidity.clearAll();
        tempHotAlarmData = 0F;
        tempCodeAlarmData = 0F;
    }


    private static final String NULL_DATA = "{}";

    public String toJsonStr() {

        try {

            JSONObject mRootJsonObj = new JSONObject();

            JSONObject mTempJson = new JSONObject();
            mTempJson.put("currentValue", this.mTemperature.currentValue);
            mTempJson.put("hotAlarmValue", this.mTemperature.hotAlarmValue);
            mTempJson.put("hotAlarmSwitch", this.mTemperature.hotAlarmSwitch);

            mTempJson.put("codeAlarmValue", this.mTemperature.codeAlarmValue);
            mTempJson.put("codeAlarmSwitch", this.mTemperature.codeAlarmSwitch);

            mRootJsonObj.put("temperature", mTempJson);

            JSONObject mHumidityJson = new JSONObject();
            mHumidityJson.put("currentValue", this.mHumidity.currentValue);
            mHumidityJson.put("alarmValue", this.mHumidity.alarmValue);
            mHumidityJson.put("alarmSwitch", this.mHumidity.alarmSwitch);
            mRootJsonObj.put("humidity", mHumidityJson);

            return mRootJsonObj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return NULL_DATA;
    }

}
