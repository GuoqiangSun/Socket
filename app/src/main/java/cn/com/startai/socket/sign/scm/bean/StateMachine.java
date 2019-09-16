package cn.com.startai.socket.sign.scm.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author Guoqiang_Sun
 * date 2019/4/25
 * desc
 */
public class StateMachine {


//    继电器开关状态 power
//    彩灯开关状态 colorLight
//    睡眠开关状态 sleepLight
//    USB开关状态 UsbPower
//    指示灯开关状态 pilotLight
//
//    定时彩灯任务状态 colorLightTimer
//    定时睡眠灯任务状态 sleepLightTimer
//
//    定时任务状态 timer
//    定时高级设置状态 highTimer
//    倒计时任务状态 countDown
//    USB定时任务状态 UsbTimer
//
//    温度任务状态 temperature
//    温感控头状态 temperatureSensing
//    湿度任务状态 humidity
//
//    定量任务任务状态 setMeasure
//    定费任务任务状态 setCost

    public String mac;

    public boolean power;
    public boolean timer;
    public boolean countDown;
    public boolean powerManage;
    public boolean sleepLight;
    public boolean colorLight;
    public boolean UsbPower;
    public boolean pilotLight;
    public boolean colorLightTimer;
    public boolean sleepLightTimer;
    public boolean highTimer;
    public boolean UsbTimer;
    public boolean temperature;
    public boolean highTemperature;
    public boolean temperatureSensing;
    public boolean humidity;
    public boolean setMeasure;
    public boolean setCost;


//    {
//        "power":boolean,
//        "timer":boolean,
//        "countDown":boolean,
//        "powerManage":boolean,
//        "sleepLight":boolean,
//        "colorLight":boolean,
//        "UsbPower":boolean,

//        "pilotLight":boolean,
//        "colorLightTimer":boolean,

//        "sleepLightTimer":boolean,
//        "highTimer":boolean,
//        "UsbTimer":boolean,

//        "temperature":boolean,
//        "temperatureSensing":boolean,
//        "humidity":boolean,

//        "setMeasure":boolean,
//        "setCost":boolean,
//    }


    @Override
    public String toString() {
        return toJsonStr();
    }

    public String toJsonStr() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("power", power);
            obj.put("timer", timer);
            obj.put("countDown", countDown);
            obj.put("powerManage", powerManage);
            obj.put("sleepLight", sleepLight);
            obj.put("colorLight", colorLight);
            obj.put("UsbPower", UsbPower);

            obj.put("pilotLight", pilotLight);
            obj.put("colorLightTimer", colorLightTimer);

            obj.put("sleepLightTimer", sleepLightTimer);
            obj.put("highTimer", highTimer);
            obj.put("UsbTimer", UsbTimer);

            obj.put("temperature", temperature);
            obj.put("temperatureSensing", temperatureSensing);
            obj.put("humidity", humidity);
            obj.put("highTemperature", highTemperature);

            obj.put("setMeasure", setMeasure);
            obj.put("setCost", setCost);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
