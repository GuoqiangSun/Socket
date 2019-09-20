package cn.com.startai.socket.sign.js.jsInterface;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;

import android.webkit.JavascriptInterface;

import java.util.Locale;

import cn.com.startai.socket.mutual.js.xml.LocalData;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

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

    public static final String H5_ZH = "zh";//中文
    public static final String H5_EN = "en";//英文
    public static final String H5_GE = "ge";//德语


    @JavascriptInterface
    public void setSystemLanguageRequest(String lan) {
        Tlog.v(TAG, " setSystemLanguageRequest " + lan);

        getHandler().obtainMessage(MSG_SET_LANGUAGE, lan).sendToTarget();
    }


    public static String changeSystemLangToH5Lang(Context mContext) {

        String language = LocalData.getLocalData(mContext).getLanguage("");
        if (language == null || "".equals(language)) {

            Locale locale = mContext.getResources().getConfiguration().locale;
            return changeSystemLangToH5Lang(locale.getLanguage());

        }
        return language;

    }

    public static String changeSystemLangToH5Lang(String language) {
        Tlog.v(TAG, " system language " + language);
        String type;
        if (language.startsWith("en")) {
            type = Language.H5_EN;
        } else if (language.startsWith("zh")) {
            type = Language.H5_ZH;
        } else if (language.startsWith("de")) {
            type = Language.H5_GE;
        } else {
            type = Language.H5_EN;
        }
        return type;
    }

    private static String convertH5LanToSys(String h5Lan) {
        if (H5_GE.equals(h5Lan)) {
            return "de";
        }
        return h5Lan;
    }

    public static void changeLanguage(Context mCtx) {
        Tlog.v(TAG, " execute changeLanguage() ");

        String sysLanguage = mCtx.getResources().getConfiguration().locale.getLanguage();
        String h5Language = LocalData.getLocalData(mCtx).getLanguage(sysLanguage);
        String language = convertH5LanToSys(h5Language);
        Tlog.v(TAG, " system language : " + sysLanguage + " H5 language :" + h5Language + " language:" + language);


        boolean change = true;
        if (sysLanguage.startsWith(language)) {
            change = false;
        } else if (sysLanguage.startsWith(language.toUpperCase())) {
            change = false;
        } else if (sysLanguage.startsWith(language.toLowerCase())) {
            change = false;
        }

        if (change) {
            int appLanguage = getAppLanguage(h5Language);
            init(mCtx, appLanguage);

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


    public static final int CHANGE_LANGUAGE_CHINA = 1;
    public static final int CHANGE_LANGUAGE_ENGLISH = 2;
    public static final int CHANGE_LANGUAGE_GERMAN = 3;
    public static final int CHANGE_LANGUAGE_DEFAULT = 0;

    public static int getAppLanguage(String h5lang) {

        if (h5lang.equalsIgnoreCase(H5_ZH)) {
            return CHANGE_LANGUAGE_CHINA;
        } else if (h5lang.equalsIgnoreCase(H5_EN)) {
            return CHANGE_LANGUAGE_ENGLISH;
        } else if (h5lang.equalsIgnoreCase(H5_GE)) {
            return CHANGE_LANGUAGE_GERMAN;
        }
        return CHANGE_LANGUAGE_ENGLISH;
    }

//    private static String country = null;

    public static void init(Context context, int appLanguage) {
        Resources mResources = context.getResources();
        changeLanguage(mResources, appLanguage);
    }


    private static void changeLanguage(Resources mResources, int language) {

        Configuration config = mResources.getConfiguration();     // 获得设置对象
        DisplayMetrics dm = mResources.getDisplayMetrics();
        switch (language) {
            case CHANGE_LANGUAGE_CHINA:
                config.locale = Locale.SIMPLIFIED_CHINESE;     // 中文
                config.setLayoutDirection(Locale.SIMPLIFIED_CHINESE);


                break;
            case CHANGE_LANGUAGE_ENGLISH:
                config.locale = Locale.ENGLISH;   // 英文
                config.setLayoutDirection(Locale.ENGLISH);


                break;
            case CHANGE_LANGUAGE_GERMAN:
                config.locale = Locale.GERMANY;
                config.setLayoutDirection(Locale.GERMANY);
                break;
            case CHANGE_LANGUAGE_DEFAULT:

                String country = Locale.getDefault().getCountry();

                Locale mDefaultLocale;
                if ("CN".equals(country)) {
                    mDefaultLocale = Locale.SIMPLIFIED_CHINESE;
                } else {
                    mDefaultLocale = Locale.ENGLISH;
                }

                config.locale = mDefaultLocale;         // 系统默认语言
                config.setLayoutDirection(mDefaultLocale);

                break;
        }

        mResources.updateConfiguration(config, dm);

    }


}
