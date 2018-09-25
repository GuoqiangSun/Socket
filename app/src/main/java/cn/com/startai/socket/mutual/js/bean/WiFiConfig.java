package cn.com.startai.socket.mutual.js.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class WiFiConfig {

    private String ssid;
    private String pwd;

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSsid() {
        return this.ssid;
    }

    public String getPwd() {
        return this.pwd;
    }

    @Override
    public String toString() {
        return "ssid:" + ssid + " pwd:" + pwd;
    }
}
