package cn.com.startai.socket.sign.scm.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/17 0017
 * desc :
 */
public class CumuParams {

    public String mac;

    public long time;

    @Override
    public String toString() {
        return "CumuParams{" +
                "time=" + time +
                ", GHG=" + GHG +
                ", electricity=" + electricity +
                '}';
    }

    public long GHG;
    public long electricity;
}
