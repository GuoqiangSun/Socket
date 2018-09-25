package cn.com.startai.socket.sign.scm.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/17 0017
 * desc :
 */
public class CostRate {

    public String mac;

    public int hour1;
    public int minute1;
    public float price1;

    public int hour2;
    public int minute2;
    public float price2;

    @Override
    public String toString() {
        return " hour1:" + hour1 + " minute1:" + minute1 + " price1:" + price1
                + " hour2:" + hour2 + " minute2:" + minute2 + " price2:" + price2;
    }
}
