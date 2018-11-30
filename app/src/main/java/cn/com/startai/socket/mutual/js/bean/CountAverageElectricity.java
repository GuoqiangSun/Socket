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
public class CountAverageElectricity {

    @Id(autoincrement = true)
    private Long id;

    private String mac;

    private long timestamp;

    private float electricity;

    private float price;

    private int interval;



    @Generated(hash = 1806556964)
    public CountAverageElectricity(Long id, String mac, long timestamp,
            float electricity, float price, int interval) {
        this.id = id;
        this.mac = mac;
        this.timestamp = timestamp;
        this.electricity = electricity;
        this.price = price;
        this.interval = interval;
    }

    @Generated(hash = 1849768559)
    public CountAverageElectricity() {
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


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public float getElectricity() {
        return this.electricity;
    }

    public void setElectricity(float electricity) {
        this.electricity = electricity;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
