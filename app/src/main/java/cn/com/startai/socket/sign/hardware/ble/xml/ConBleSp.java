package cn.com.startai.socket.sign.hardware.ble.xml;

import android.content.Context;

import cn.com.swain.baselib.sp.BaseSpTool;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/21 0021
 * desc :
 */
public class ConBleSp extends BaseSpTool {

    private static final String NAME_CON_BLE = "ConBleSp";

    private ConBleSp(Context mCtx) {
        super(mCtx, NAME_CON_BLE);
    }

    private static ConBleSp mConBleSp;

    public static ConBleSp getConBleSp(Context mCtx) {
        if (mConBleSp == null) {
            synchronized (NAME_CON_BLE) {
                if (mConBleSp == null) {
                    mConBleSp = new ConBleSp(mCtx);
                }
            }
        }
        return mConBleSp;
    }

    private static final String KEY_CON_BLE_MAC = "conBleMac";

    public void setConMAC(String language) {
        putString(KEY_CON_BLE_MAC, language);
    }

    public String getConMAC(String def) {
        return getString(KEY_CON_BLE_MAC, def);
    }


    private static final String KEY_ENABLE_BLE_MAC = "enableBleMac";

    public void setEnableDevice(String mac) {
        putString(KEY_ENABLE_BLE_MAC, mac);
    }

    public String getEnableDevice(String mac) {
        return getString(KEY_ENABLE_BLE_MAC, mac);
    }

}
