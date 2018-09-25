package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.content.Context;

import cn.com.swain.baselib.sp.BaseSpTool;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class NetworkData extends BaseSpTool {

    private static final String NAME_LOCAL_DATA = "NetworkData";

    private NetworkData(Context mCtx) {
        super(mCtx, NAME_LOCAL_DATA);
    }

    private static NetworkData mLocalData;

    public static NetworkData getLocalData(Context mCtx) {
        if (mLocalData == null) {
            synchronized (NAME_LOCAL_DATA) {
                if (mLocalData == null) {
                    mLocalData = new NetworkData(mCtx);
                }
            }
        }
        return mLocalData;
    }

    private static final String KEY_LOGIN_USER = "loginUser";

    public void setLastLoginUser(String mLoginUser) {
        putString(KEY_LOGIN_USER, mLoginUser);
    }

    public String getLastLoginUser(String def) {
        return getString(KEY_LOGIN_USER, def);
    }


}
