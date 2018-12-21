package cn.com.startai.socket.sign.scm.bean.sensor;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class Temperature {

    public float value;
    public float alarmValue;
    public void clear(){
        this.value = 0;
        this.alarmValue=0;
    }
}
