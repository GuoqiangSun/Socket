package cn.com.startai.socket.sign.scm.bean;

import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/4 0004
 * desc :
 */
public class TempHumidityAlarmData {

    public TempHumidityAlarmData() {
    }

    public void setTypeIsTemp() {
        this.type = TYPE_TEMPERATURE;
    }

    public void setTypeIsHumidity() {
        this.type = TYPE_HUMIDITY;
    }

    public boolean isTemperatureType() {
//        return (type == TYPE_TEMPERATURE);
        return SocketSecureKey.Util.isTemperature((byte) type);
    }

    public boolean isHumidityType() {
//        return (type == TYPE_HUMIDITY);
        return SocketSecureKey.Util.isHumidity((byte) type);
    }

    public boolean isLimitUp() {
        return SocketSecureKey.Util.isLimitUp((byte) (limit & 0xFF));
    }

    public boolean isLimitDown() {
        return SocketSecureKey.Util.isLimitDown((byte) (limit & 0xFF));
    }

    public static final int TYPE_TEMPERATURE = SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE;
    public static final int TYPE_HUMIDITY = SocketSecureKey.Model.ALARM_MODEL_HUMIDITY;

    private int limit;

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    private int type;
    private String mac;

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    private boolean startup;

    public boolean isStartup() {
        return this.startup;
    }

    public void setStartup(boolean startup) {
        this.startup = startup;
    }

    private float alarmValue;

    public void setAlarmValue(float alarmValue) {
        this.alarmValue = alarmValue;
    }

    public float getOriginalAlarmValue() {
        return this.alarmValue;
    }

    public int getAlarmValue() {
        return (int) this.alarmValue;
    }

    public int getAlarmValueDeci() {
        int alarmInt = getAlarmValue() * 100;
        int originAlarmInt = (int) (alarmValue * 100);
        int deci = Math.abs(originAlarmInt - alarmInt);
        return deci;
    }

}
