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

    public String toJsonStr() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("power", power);
            obj.put("timer", power);
            obj.put("countDown", power);
            obj.put("powerManage", power);
            obj.put("sleepLight", power);
            obj.put("colorLight", power);
            obj.put("UsbPower", power);

            obj.put("pilotLight", power);
            obj.put("colorLightTimer", power);

            obj.put("sleepLightTimer", power);
            obj.put("highTimer", power);
            obj.put("UsbTimer", power);

            obj.put("temperature", power);
            obj.put("temperatureSensing", power);
            obj.put("humidity", power);

            obj.put("setMeasure", power);
            obj.put("setCost", power);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
