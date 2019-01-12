package cn.com.startai.socket.mutual.js.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/7 0007
 * desc :
 */
public class MobileBind {

    //手机登录
    public String phone;
    public String code;


    @Override
    public String toString() {
        return "phone:" + phone + " code:" + code;
    }
}
