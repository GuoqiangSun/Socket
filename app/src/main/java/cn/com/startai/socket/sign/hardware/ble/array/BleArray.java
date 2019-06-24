package cn.com.startai.socket.sign.hardware.ble.array;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.swain.support.ble.scan.ScanBle;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class BleArray {

    public void clearAll() {
        clearDisplayLst();
        clearConnectedLst();
//        clearConnectingLst();
    }


    /**
     * 建立过连接并且连接成功的ble集合
     */
    private Map<String, ScanBle> mBleConnectedArray = new HashMap<>();


    public ScanBle getConnectedBle(String mac) {
        return mBleConnectedArray.get(mac);
//        return BleArrayUtils.get(mBleConnectedArray, mac);
    }

    public boolean addConnectedBle(ScanBle mScanBle) {
        if (mScanBle == null) {
            return false;
        }
        ScanBle scanBle = mBleConnectedArray.get(mScanBle.address);
        if (scanBle == null) {
            mBleConnectedArray.put(mScanBle.address, mScanBle);
            return true;
        }

        if (scanBle.address.equalsIgnoreCase(mScanBle.address)
                && scanBle.name.equals(mScanBle.name)) {
            return false;
        }
        mBleConnectedArray.put(mScanBle.address, mScanBle);
        return true;
//        return BleArrayUtils.add(mBleConnectedArray, mScanBle);
    }

    public void clearConnectedLst() {
        mBleConnectedArray.clear();
    }

    /**
     * 扫描到的ble
     */
    private Map<String, ScanBle> mBleDisplayArray = Collections.synchronizedMap(new HashMap<>());

    public int getDisplaySize() {
        return mBleDisplayArray.size();
    }

    public synchronized boolean addDisplayBle(ScanBle mScanBle) {
        if (mScanBle == null) {
            return false;
        }
        ScanBle scanBle = mBleDisplayArray.get(mScanBle.address);
        if (scanBle == null) {
            mBleDisplayArray.put(mScanBle.address, mScanBle);
            return true;
        }

        if (scanBle.address.equalsIgnoreCase(mScanBle.address)
                && scanBle.name.equals(mScanBle.name)) {
            return false;
        }
        mBleDisplayArray.put(mScanBle.address, mScanBle);
        return true;
//        return BleArrayUtils.add(mBleDisplayArray, mScanBle);
    }


    public synchronized ScanBle getDisplayBle(String mac) {
        return mBleDisplayArray.get(mac);
//        return BleArrayUtils.get(mBleDisplayArray, mac);
    }

    public void removeDisplayItem(ScanBle ble) {
        if(ble!=null){
        mBleDisplayArray.remove(ble.address);
        }
//        BleArrayUtils.remove(mBleDisplayArray, ble);
    }

    public void clearDisplayLst() {
        mBleDisplayArray.clear();
    }

}
