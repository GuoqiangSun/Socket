package cn.com.startai.socket.sign.js.jsInterface;

import android.content.Context;
import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import java.util.Locale;

import cn.com.startai.socket.mutual.js.xml.LocalData;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.util.ChangeLanguageHelper;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/5 0005
 * desc :
 */
public class Language extends AbsHandlerJsInterface {


    public interface IJSLanguageCallBack {

        void onJSLSetLanguage(String type);

        void onJSLRequestSystemLanguage();

    }

    public static final class Method {

        private static final String METHOD_LANGUAGE = "javascript:systemLanguageResponse('$lang')";

        public static String callJsSystemLanguage(String lang) {
            return METHOD_LANGUAGE.replace("$lang", String.valueOf(lang));
        }

        private static final String METHOD_SET_LANGUAGE = "javascript:setSystemLanguageResponse($result,'$lang')";

        public static String callJsSetSystemLanguage(boolean result, String lang) {
            return METHOD_SET_LANGUAGE.replace("$result", String.valueOf(result))
                    .replace("$lang", String.valueOf(lang));
        }

    }

    public static final String NAME_JSI = "Language";

    private static String TAG = H5Config.TAG;

    private IJSLanguageCallBack mCallBack;

    public Language(Looper mLooper, IJSLanguageCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void systemLanguageRequest() {
        Tlog.v(TAG, " systemLanguageRequest ");
        getHandler().sendEmptyMessage(MSG_REQUEST_LANGUAGE);
    }

    public static final String ZH = "zh";
    public static final String EN = "en";

    @JavascriptInterface
    public void setSystemLanguageRequest(String lan) {
        Tlog.v(TAG, " setSystemLanguageRequest " + lan);

        getHandler().obtainMessage(MSG_SET_LANGUAGE, lan).sendToTarget();
    }


    public static String changeSystemLangToH5Lang(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        return changeSystemLangToH5Lang(locale.getLanguage());

    }

    public static String changeSystemLangToH5Lang(String language) {
        Tlog.v(TAG, " system language " + language);
        String type;
        if (language.startsWith("en")) {
            type = Language.EN;
        } else if (language.startsWith("zh")) {
            type = Language.ZH;
        } else {
            type = Language.EN;
        }
        return type;
    }

    public static void changeLanguage(Context mCtx) {
        Tlog.v(TAG, " execute changeLanguage() ");

        String sysLanguage = mCtx.getResources().getConfiguration().locale.getLanguage();
        String h5Language = LocalData.getLocalData(mCtx).getLanguage(sysLanguage);
        Tlog.v(TAG, " system language : " + sysLanguage + " H5 language :" + h5Language);

        boolean change = true;
        if (sysLanguage.startsWith(h5Language)) {
            change = false;
        } else if (sysLanguage.startsWith(h5Language.toUpperCase())) {
            change = false;
        } else if (sysLanguage.startsWith(h5Language.toLowerCase())) {
            change = false;
        }

        if (change) {
            int appLanguage = ChangeLanguageHelper.getAppLanguage(h5Language);
            ChangeLanguageHelper.init(mCtx, appLanguage);

            String curLanguage = mCtx.getResources().getConfiguration().locale.getLanguage();
            Tlog.v(TAG, " change app Language , cur language:" + curLanguage);
        }
    }

    private static final int MSG_REQUEST_LANGUAGE = 0x23;
    private static final int MSG_SET_LANGUAGE = 0x24;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_REQUEST_LANGUAGE) {
            if (mCallBack != null) {
                mCallBack.onJSLRequestSystemLanguage();
            }
        } else if (msg.what == MSG_SET_LANGUAGE) {
            if (mCallBack != null) {
                mCallBack.onJSLSetLanguage((String) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }

    }

}
