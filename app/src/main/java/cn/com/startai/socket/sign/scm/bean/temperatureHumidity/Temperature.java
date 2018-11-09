package cn.com.startai.socket.sign.scm.bean.temperatureHumidity;

import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/7 0007
 * desc :
 */
public class Temperature {

//     "currentValue": 23,
//             "alarmValue": 50,
//             "alarmSwitch": false


//        "currentValue": 23,
//                "hotAlarmValue": 50,
//                "hotAlarmSwitch": false,
//                "codeAlarmValue": 50,
//                "codeAlarmSwitch": false
//


    public int limit;

    public boolean typeIsHot() {
        return typeIsHot((byte) limit);
    }

    public boolean typeIsCode() {
        return typeIsCode((byte) limit);
    }

    public boolean typeIsHot(byte limit) {
        return SocketSecureKey.Util.isLimitUp(limit);
    }

    public boolean typeIsCode(byte limit) {
        return SocketSecureKey.Util.isLimitDown(limit);
    }

    public float currentValue;

    public float hotAlarmValue;
    public boolean hotAlarmSwitch;

    public float codeAlarmValue;
    public boolean codeAlarmSwitch;

    public void clearAll() {
        this.limit = 0;
        this.currentValue = 0;

        this.hotAlarmValue = 0;
        this.hotAlarmSwitch = false;

        this.codeAlarmValue = 0;
        this.codeAlarmSwitch = false;
    }

    @Override
    public String toString() {

        return "hotAlarmValue:" + hotAlarmValue + " hotAlarmSwitch:" + hotAlarmSwitch + "; codeAlarmValue:" + codeAlarmValue + " codeAlarmSwitch:" + codeAlarmSwitch;


    }
}
