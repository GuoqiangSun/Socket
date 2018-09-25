package cn.com.startai.socket.sign.scm.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/12 0012
 * desc :
 */
public class SpendingElectricityData {

    public static final String MODEL_POWER = "power";
    public static final String MODEL_COST = "cost";

    public String mac;
    public int model;
    public int alarmValue;
    public boolean alarmSwitch;
    public int year;
    public int month;
    public int day;
    public int currentValue;


    public JSONObject toJsonObj() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("mac", mac);
            obj.put("alarmValue", alarmValue);
            obj.put("alarmSwitch", alarmSwitch);
            obj.put("year", year);
            obj.put("month", month);
            obj.put("day", day);
            obj.put("currentValue", currentValue);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void memor(SpendingElectricityData mSpendingElectricityData) {
        if (mSpendingElectricityData == null) {
            return;
        }

        this.mac = mSpendingElectricityData.mac;
        this.model = mSpendingElectricityData.model;
        this.alarmValue = mSpendingElectricityData.alarmValue;
        this.alarmSwitch = mSpendingElectricityData.alarmSwitch;
        this.year = mSpendingElectricityData.year;
        this.month = mSpendingElectricityData.month;
        this.day = mSpendingElectricityData.day;
        this.currentValue = mSpendingElectricityData.currentValue;

    }

}
