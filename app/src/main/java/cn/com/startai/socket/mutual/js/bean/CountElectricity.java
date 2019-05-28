package cn.com.startai.socket.mutual.js.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/23 0023
 * Desc:
 */

@Entity
public class CountElectricity {

    @Id(autoincrement = true)
    private Long id;

    private String mac;

    private long timestamp;

    private int complete; // 1 完整 0 不完整

    private byte[] electricity;

    public static final int ONE_PKG_LENGTH = 8; // 一组数据大小

    // 一小时数据个数
    public static final int SIZE_ONE_HOUR = 60 / 5;

    // 一天数据个数
    public static final int SIZE_ONE_DAY = SIZE_ONE_HOUR * 24;

    public static final int ONE_DAY_BYTES = SIZE_ONE_DAY * ONE_PKG_LENGTH; // 一天数据长度



    public static final int NEW_ONE_PKG_LENGTH = 4; // 一组数据大小
    public static final int NEW_ONE_DAY_BYTES = SIZE_ONE_DAY * NEW_ONE_PKG_LENGTH; // 一天数据长度

    @Generated(hash = 1286204013)
    public CountElectricity(Long id, String mac, long timestamp, int complete,
            byte[] electricity) {
        this.id = id;
        this.mac = mac;
        this.timestamp = timestamp;
        this.complete = complete;
        this.electricity = electricity;
    }

    @Generated(hash = 1525155574)
    public CountElectricity() {
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getElectricity() {
        return this.electricity;
    }

    public void setElectricity(byte[] electricity) {
        this.electricity = electricity;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getComplete() {
        return this.complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

}
