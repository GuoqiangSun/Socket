package cn.com.startai.socket.sign.scm.bean.sensor;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class Power {

    public int value;
    public int averageValue;
    public int maximumValue;
    public void clear(){
        this.value = 0;
        this.averageValue=0;
        this.maximumValue=0;
    }
}
