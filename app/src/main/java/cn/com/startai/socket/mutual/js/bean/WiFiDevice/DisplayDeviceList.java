package cn.com.startai.socket.mutual.js.bean.WiFiDevice;

import org.json.JSONArray;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.sign.hardware.WiFi.impl.DeviceManager;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class DisplayDeviceList {

    private String TAG = DeviceManager.TAG;

    public DisplayDeviceList() {

    }

    public DisplayDeviceList(LanDeviceInfo mDevice) {
        LanDeviceInfo clone = mDevice.clone();
        if (clone != null) {
            mIDArray.put(clone.deviceID, clone);
            mMacArray.put(clone.mac, clone);
            mIPArray.put(clone.ip, clone);
        }
    }

    /**
     * key deviceID
     * obj LanDeviceInfo
     */
    private final Map<String, LanDeviceInfo> mIDArray = Collections.synchronizedMap(new HashMap<>());

    /**
     * key deviceMac
     * obj LanDeviceInfo
     */
    private final Map<String, LanDeviceInfo> mMacArray = Collections.synchronizedMap(new HashMap<>());

    /**
     * key deviceIP
     * obj mac
     */
    private final Map<String, LanDeviceInfo> mIPArray = Collections.synchronizedMap(new HashMap<>());

    public synchronized void add(LanDeviceInfo mDevice) {

        LanDeviceInfo clone = mDevice.clone();

        if (clone != null) {
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " DisplayDeviceList.add() :" + String.valueOf(clone));
            }
            mIDArray.put(clone.deviceID, clone);
            mMacArray.put(clone.mac, clone);
            mIPArray.put(clone.ip, clone);
        } else {
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " DisplayDeviceList.add() clone error :" + String.valueOf(mDevice));
            }
        }
    }


    public synchronized Map<String, LanDeviceInfo> addAll(Map<String, LanDeviceInfo> tmpArray) {

        final Map<String, LanDeviceInfo> mIDArrayCopy = new HashMap<>(mIDArray);

        Tlog.v(TAG, " DisplayDeviceList addAll  size:" + tmpArray.size());

        if (tmpArray.size() > 0) {

            for (Map.Entry<String, LanDeviceInfo> tmpEntries : tmpArray.entrySet()) {
                LanDeviceInfo tmpDisplayLanDevice = tmpEntries.getValue();

                LanDeviceInfo displayDeviceByMac = getDisplayDeviceByMac(tmpDisplayLanDevice.getMac());
                if (displayDeviceByMac != null) {
                    tmpDisplayLanDevice.setRelayState(displayDeviceByMac.getRelayState());
//                    tmpDisplayLanDevice.setState(displayDeviceByMac.getState());
                }

//                Tlog.e(DeviceManager.TAG, " DisplayDeviceList.addAll() put " + tmpDisplayLanDevice.deviceID + " " + tmpDisplayLanDevice.mac);
                add(tmpDisplayLanDevice);

                mIDArrayCopy.remove(tmpDisplayLanDevice.deviceID);

            }

        }

        for (Map.Entry<String, LanDeviceInfo> tmpEntries : mIDArrayCopy.entrySet()) {
            remove(tmpEntries.getValue());
            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " mIDArray.remove:" + tmpEntries.getValue().toString());
            }
        }

        return mIDArrayCopy;

    }


    public synchronized void deviceDiscoveryUpdateDevice(LanDeviceInfo mDevice) {

        LanDeviceInfo displayLanDevice = getDisplayDeviceByMac(mDevice.mac);

        if (displayLanDevice != null) {

            String mLastIp = displayLanDevice.ip;
            String deviceID = displayLanDevice.deviceID;
            boolean relayState = displayLanDevice.relayState;
            boolean isWanBind = displayLanDevice.isWanBind;
            boolean isLanBind = displayLanDevice.isLanBind;
            displayLanDevice.copy(mDevice);
            displayLanDevice.isWanBind = isWanBind;
            displayLanDevice.isLanBind = isLanBind;
            displayLanDevice.relayState = relayState;
            displayLanDevice.deviceID = deviceID;
            displayLanDevice.state = true;

            if (mLastIp != null && !mLastIp.equalsIgnoreCase(mDevice.ip)) {
                mIPArray.remove(mLastIp);
                mIPArray.put(mDevice.ip, mDevice);
            }

        }

    }

    public synchronized void remove(LanDeviceInfo sameDisplayDevice) {

        mIDArray.remove(sameDisplayDevice.deviceID);
        mMacArray.remove(sameDisplayDevice.mac);
        mIPArray.remove(sameDisplayDevice.ip);

    }

    public synchronized void clear() {
        mIDArray.clear();
        mMacArray.clear();
        mIPArray.clear();
    }


    public synchronized boolean rename(String mac, String name) {

        LanDeviceInfo mSameDisplayDevice = getDisplayDeviceByMac(mac);

        if (mSameDisplayDevice != null) {

            String nameOriginal = mSameDisplayDevice.getName();
            mSameDisplayDevice.setName(name);

            LanDeviceInfo displayDeviceById = getDisplayDeviceById(mSameDisplayDevice.deviceID);

            if (displayDeviceById != null) {
                displayDeviceById.setName(name);
            }

            return !nameOriginal.equals(name);
        }

        return false;
    }


    public synchronized LanDeviceInfo getDisplayDeviceByIp(String ip) {
        return mIPArray.get(ip);
    }

    public synchronized LanDeviceInfo getDisplayDeviceByMac(String mac) {

        return mMacArray.get(mac);
    }

    public synchronized LanDeviceInfo getDisplayDeviceById(String fromId) {
        return mIDArray.get(fromId);
    }

    public synchronized String toJsonStr() {

        JSONArray JsonArray = new JSONArray();

        try {
            for (Map.Entry<String, LanDeviceInfo> entries : mIDArray.entrySet()) {
                LanDeviceInfo mDevice = entries.getValue();
                if (mDevice != null) {
                    JsonArray.put(mDevice.toJsonObj());
                }

            }
        } catch (Exception e) {

        }

        return JsonArray.toString();

    }


    public void updateConnectStatus(String sn, String mac, boolean status) {

        LanDeviceInfo displayDeviceById = getDisplayDeviceById(sn);
        if (displayDeviceById != null) {
            displayDeviceById.setState(status);
        }

        LanDeviceInfo displayDeviceByMac = getDisplayDeviceByMac(mac);
        if (displayDeviceByMac != null) {
            displayDeviceByMac.setState(status);
        }

    }

}
