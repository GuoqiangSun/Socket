package cn.com.startai.socket.mutual.js.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class NightLightTiming {

    public String mac;
    public int id;
    public boolean startup;
    public int startHour;
    public int startMinute;
    public int stopHour;
    public int stopMinute;

    public String startTime;
    public String endTime;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isStartup() {
        return startup;
    }

    public void setStartup(boolean state) {
        this.startup = state;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getStopHour() {
        return stopHour;
    }

    public void setStopHour(int stopHour) {
        this.stopHour = stopHour;
    }

    public int getStopMinute() {
        return stopMinute;
    }

    public void setStopMinute(int stopMinute) {
        this.stopMinute = stopMinute;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return " NightLightTiming{" +
                "mac='" + mac + '\'' +
                ", id=" + id +
                ", startup=" + startup +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", stopHour=" + stopHour +
                ", stopMinute=" + stopMinute +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }


//    {
//        "intelligence": false,
//            "timing": {
//        "id": 1,
//                "state": false,
//                "startTime": "00:00",
//                "endTime": "23:59",
//                "mode": 0,
//                "week": 5 // 十进制转二进制表示星期（周一、周三）
//    }
//    }

    public String toJsonStr() {

        JSONObject mObj = new JSONObject();

        try {
            mObj.put("id", id);
            mObj.put("state", startup);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject timing = new JSONObject();
        try {
            timing.put("startTime", startTime);
            timing.put("endTime", endTime);
            mObj.put("timing", timing);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mObj.toString();
    }


}
