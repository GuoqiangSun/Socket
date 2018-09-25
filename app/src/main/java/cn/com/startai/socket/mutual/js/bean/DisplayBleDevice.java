package cn.com.startai.socket.mutual.js.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.swain.support.ble.scan.ScanBle;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
@Entity
public class DisplayBleDevice {

    @Id(autoincrement = true)
    private Long id;
    public String name;
    public String address;
    public boolean con;
    public boolean hasRemoteActivation;
    public int rssi;

    public DisplayBleDevice memorize(ScanBle mScanBle) {

        if (mScanBle == null) {
            return this;
        }

        this.name = mScanBle.name;
        this.address = mScanBle.address;
        this.con = mScanBle.isConnected();
        this.rssi = mScanBle.rssi;
        return this;
    }

    public ScanBle copyToScanBle() {
        return copyToScanBle(new ScanBle());
    }

    public ScanBle copyToScanBle(ScanBle mItem) {

        if (mItem == null) {
            mItem = new ScanBle();
        }

        mItem.rssi = this.rssi;
        mItem.address = this.address;
        mItem.name = this.name;
        if (con) {
            mItem.setConnected();
        } else {
            mItem.setDisconnected();
        }
        return mItem;
    }


    private static final String NULL_BLE = "[]";

    @Generated(hash = 1265072576)
    public DisplayBleDevice(Long id, String name, String address, boolean con,
                            boolean hasRemoteActivation, int rssi) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.con = con;
        this.hasRemoteActivation = hasRemoteActivation;
        this.rssi = rssi;
    }

    @Generated(hash = 480540335)
    public DisplayBleDevice() {
    }

    public String toJsonStr() {

        try {
            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("mac", this.address);
            jo.put("state", this.con ? 1 : 0);
            jo.put("signal", rssi);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return NULL_BLE;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getCon() {
        return this.con;
    }

    public void setCon(boolean con) {
        this.con = con;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getHasRemoteActivation() {
        return this.hasRemoteActivation;
    }

    public void setHasRemoteActivation(boolean hasRemoteActivation) {
        this.hasRemoteActivation = hasRemoteActivation;
    }


}
