package cn.com.startai.socket.sign.scm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/20 0020
 * desc :
 */
@Entity
public class PowerCountdown {

    @Id(autoincrement = true)
    private Long id;
    private boolean status;//启动，结束
    private boolean switchGear;//开机，关机
    private int hour;
    private int minute;
    private long sysTime;
    private String mac;



    @Generated(hash = 210481107)
    public PowerCountdown(Long id, boolean status, boolean switchGear, int hour,
            int minute, long sysTime, String mac) {
        this.id = id;
        this.status = status;
        this.switchGear = switchGear;
        this.hour = hour;
        this.minute = minute;
        this.sysTime = sysTime;
        this.mac = mac;
    }

    @Generated(hash = 1704666428)
    public PowerCountdown() {
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getSwitchGear() {
        return this.switchGear;
    }

    public void setSwitchGear(boolean switchGear) {
        this.switchGear = switchGear;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public long getSysTime() {
        return this.sysTime;
    }

    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(128);
        sb.append("ID:" + getId() + ",");
        sb.append("mac:" + getMac() + ",");
        sb.append("status:" + getStatus() + ",");
        sb.append("switchGear:" + getSwitchGear() + ",");
        sb.append("hour:" + getHour() + ",");
        sb.append("minute:" + getMinute() + ",");
        sb.append("sysTime:" + getSysTime() + ".");
        return sb.toString();

    }

}
