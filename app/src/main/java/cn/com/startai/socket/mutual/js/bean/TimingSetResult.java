package cn.com.startai.socket.mutual.js.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/7 0007
 * desc :
 */
public class TimingSetResult {

    public String mac;
    public boolean result;
    public boolean startup;
    public int id;
    public int model;

    public byte state;
    public byte week;

    @Override
    public String toString() {
        return "TimingSetResult{" +
                "mac='" + mac + '\'' +
                ", result=" + result +
                ", startup=" + startup +
                ", id=" + id +
                ", model=" + model +
                ", state=" + state +
                ", week=" + week +
                '}';
    }
}
