package cn.com.startai.socket.sign.hardware.ble.array;

import java.util.ArrayList;
import java.util.List;

import cn.com.swain.support.ble.scan.ScanBle;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class BleArrayUtils {

    public static ScanBle get(List<ScanBle> mBleArray, String mac) {
        if (mac == null) {
            return null;
        }
        ScanBle mTmpBle = null;
        for (int i = 0; i < mBleArray.size(); i++) {
            ScanBle mScanBle = mBleArray.get(i);
            if (mac.trim().equalsIgnoreCase(mScanBle.address)) {
                mTmpBle = mScanBle;
                break;
            }
        }
        return mTmpBle;

    }

    public static boolean update(ArrayList<ScanBle> mBleArray, ScanBle mScanBle) {
        if (mScanBle == null) {
            return false;
        }
        for (int i = 0; i < mBleArray.size(); i++) {
            ScanBle ble = mBleArray.get(i);
            if (ble.address.equalsIgnoreCase(mScanBle.address) && ble.name.equals(mScanBle.name)) {
                if (ble.rssi == mScanBle.rssi) {
                    return false;
                } else {
                    ble.rssi = mScanBle.rssi;
                    return true;
                }
            }
        }
        mBleArray.add(mScanBle);
        return true;
    }

    public static boolean add(List<ScanBle> mBleArray, ScanBle mScanBle) {
        if (mScanBle == null) {
            return false;
        }
        boolean add = true;
        for (int i = 0; i < mBleArray.size(); i++) {
            ScanBle ble = mBleArray.get(i);
            if (ble.address.equalsIgnoreCase(mScanBle.address) && ble.name.equals(mScanBle.name)) {
                add = false;
                break;
            }
        }
        if (add) {
            mBleArray.add(mScanBle);
            return true;
        }
        return false;
    }

    public static boolean remove(List<ScanBle> mBleArray, ScanBle mScanBle) {
        if (mScanBle == null) {
            return false;
        }

        for (int i = 0; i < mBleArray.size(); i++) {
            ScanBle ble = mBleArray.get(i);
            if (ble.address.equalsIgnoreCase(mScanBle.address) && ble.name.equals(mScanBle.name)) {
                mBleArray.remove(ble);
                return true;
            }
        }

        return false;
    }

    public static boolean isSame(ScanBle mScanBle, ScanBle mItem) {

        if (mScanBle == null) {
            return false;
        }

        if (mItem == null) {
            return false;
        }

        if (mScanBle == mItem) {
            return true;
        }

        if (mScanBle.address.equalsIgnoreCase(mItem.address)) {
            return true;
        }

        return false;

    }

}
