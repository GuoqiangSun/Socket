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

    private long sequence;

    private byte[] electricity;

    // 一天
    public static final int SIZE = 60 / 5 * 24;

    @Generated(hash = 387654504)
    public CountElectricity(Long id, String mac, long timestamp, long sequence,
                            byte[] electricity) {
        this.id = id;
        this.mac = mac;
        this.timestamp = timestamp;
        this.sequence = sequence;
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

    public long getSequence() {
        return this.sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
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

}
