package cn.com.startai.socket.mutual.js.bean;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/30 0030
 * Desc:
 */
public class ColorLampRGB {

    public String mac;
    public int seq;
    public int r;
    public int g;
    public int b;

    public int model;

    @Override
    public String toString() {
        return "ColorLampRGB{" +
                "mac='" + mac + '\'' +
                ", seq=" + seq +
                ", r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", model=" + model +
                '}';
    }
}
