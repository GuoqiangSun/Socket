package cn.com.startai.socket.mutual.js.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/7 0007
 * desc :
 */
public class MobileLogin {

    //手机登录
    public String phone;
    public String code;

    // 邮箱登录
    public String email;
    public String emailPwd;

    @Override
    public String toString() {
        return "phone:" + phone + " code:" + code;
    }
}
