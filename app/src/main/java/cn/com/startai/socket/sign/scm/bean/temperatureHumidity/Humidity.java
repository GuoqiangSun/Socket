package cn.com.startai.socket.sign.scm.bean.temperatureHumidity;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/7 0007
 * desc :
 */
public class Humidity {

    /**
     "currentValue": 30,
     "alarmValue": 40,
     "alarmSwitch": false
     */

    public float currentValue;
    public float alarmValue;
    public boolean alarmSwitch;

    public void clearAll() {
        currentValue = 0;
        alarmValue = 0;
        alarmSwitch = false;
    }
}
