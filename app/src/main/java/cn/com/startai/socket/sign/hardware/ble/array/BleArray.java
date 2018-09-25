package cn.com.startai.socket.sign.hardware.ble.array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private ArrayList<ScanBle> mBleConnectedArray = new ArrayList<>();


    public ScanBle getConnectedBle(String mac) {
        return BleArrayUtils.get(mBleConnectedArray, mac);
    }

    public boolean addConnectedBle(ScanBle mScanBle) {
        return BleArrayUtils.add(mBleConnectedArray, mScanBle);
    }

    public void clearConnectedLst() {
        mBleConnectedArray.clear();
    }

    /**
     * 扫描到的ble
     */
    private List<ScanBle> mBleDisplayArray = Collections.synchronizedList(new ArrayList<>());

    public int getDisplaySize() {
        return mBleDisplayArray.size();
    }

    public synchronized boolean addDisplayBle(ScanBle mScanBle) {
        return BleArrayUtils.add(mBleDisplayArray, mScanBle);
    }


    public synchronized ScanBle getDisplayBle(String mac) {
        return BleArrayUtils.get(mBleDisplayArray, mac);
    }

    public void removeDisplayItem(ScanBle ble) {
        BleArrayUtils.remove(mBleDisplayArray, ble);
    }

    public void clearDisplayLst() {
        mBleDisplayArray.clear();
    }

}
