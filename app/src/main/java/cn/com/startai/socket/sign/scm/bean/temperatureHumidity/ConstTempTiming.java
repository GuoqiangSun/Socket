package cn.com.startai.socket.sign.scm.bean.temperatureHumidity;

/**
 * author Guoqiang_Sun
 * date 2019/6/4
 * desc
 */
public class ConstTempTiming {
    public String mac;
    public int result;
    public int id;
    public int model;
    public int startup;
    public int minTemp;
    public int minTempF;
    public int maxTemp;
    public int maxTempF;
    public int week;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;


    public boolean hasDecimal;// 是否有小数点

    @Override
    public String toString() {
        return "ConstTempTiming{" +
                "mac='" + mac + '\'' +
                ", result=" + result +
                ", id=" + id +
                ", model=" + model +
                ", startup=" + startup +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", week=" + week +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                '}';
    }

}
