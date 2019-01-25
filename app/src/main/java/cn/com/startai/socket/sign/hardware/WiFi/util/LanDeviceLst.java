package cn.com.startai.socket.sign.hardware.WiFi.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/31 0031
 * desc :
 */
public class LanDeviceLst {


    /**
     * key ip
     * obj mac
     */
    private final Map<String, LanDeviceInfo> mLanDeviceArrayIP = Collections.synchronizedMap(new HashMap<>());

    /**
     * key mac
     * obj lanDeviceInfo
     */
    private final Map<String, LanDeviceInfo> mLanDeviceArrayMac = Collections.synchronizedMap(new HashMap<>());

    public synchronized void deviceDiscoveryUpdateDevice(LanDeviceInfo mDevice) {
        mLanDeviceArrayIP.put(mDevice.ip, mDevice);
        mLanDeviceArrayMac.put(mDevice.mac, mDevice);
    }

    public void clear() {

        mLanDeviceArrayMac.clear();
        mLanDeviceArrayIP.clear();
    }

    public synchronized LanDeviceInfo getLanDeviceByMac(String mac) {
        return mLanDeviceArrayMac.get(mac);
    }

    public synchronized LanDeviceInfo getLanDeviceByIP(String ip) {
        return mLanDeviceArrayIP.get(ip);
    }

    public synchronized void removeDeviceByMac(String mac) {
        LanDeviceInfo lanDeviceInfo = mLanDeviceArrayMac.get(mac);
        mLanDeviceArrayMac.remove(mac);
        if (lanDeviceInfo != null) {
            mLanDeviceArrayIP.remove(lanDeviceInfo.ip);
        }
    }
}
