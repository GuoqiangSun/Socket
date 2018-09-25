package cn.com.startai.socket.sign.hardware.WiFi.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/10 0010
 * desc :
 */
public class ControlDeviceUtil {


    private final Map<String, ControlDevice> mControlMap = Collections.synchronizedMap(new HashMap<>());


    public boolean containsKey(String mac) {
        return mControlMap.containsKey(mac);
    }

    public ControlDevice get(String mac) {
        return mControlMap.get(mac);
    }

    public void put(String mac, ControlDevice mControlDevice) {
        mControlMap.put(mac, mControlDevice);
    }

    public void remove(String mac) {
        mControlMap.remove(mac);
    }

    public void onNetworkStateChange() {
        for (Map.Entry<String, ControlDevice> entries : mControlMap.entrySet()) {
            ControlDevice value = entries.getValue();
            value.onNetworkStateChange();
        }
    }
}
