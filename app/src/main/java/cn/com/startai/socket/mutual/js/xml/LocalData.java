package cn.com.startai.socket.mutual.js.xml;

import android.content.Context;

import cn.com.swain.baselib.sp.BaseSpTool;


/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public class LocalData extends BaseSpTool {

    private static final String NAME_LOCAL_DATA = "LocalData";

    private LocalData(Context mCtx) {
        super(mCtx, NAME_LOCAL_DATA);
    }

    private static LocalData mLocalData;

    public static LocalData getLocalData(Context mCtx) {
        if (mLocalData == null) {
            synchronized (NAME_LOCAL_DATA) {
                if (mLocalData == null) {
                    mLocalData = new LocalData(mCtx);
                }
            }
        }
        return mLocalData;
    }

    private static final String KEY_LANGUAGE = "language";

    public void setLanguage(String language) {
        putString(KEY_LANGUAGE, language);
    }

    public String getLanguage(String def) {
        return getString(KEY_LANGUAGE, def);
    }


}
