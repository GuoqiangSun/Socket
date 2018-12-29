package cn.com.startai.socket.app;

import android.content.res.Configuration;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.sign.js.jsInterface.Language;
import cn.com.swain.baselib.log.Tlog;


/**
 * author: Guoqiang_Sun
 * date : 2018/3/28 0027
 * desc :
 */

public class SocketApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "socketApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        Tlog.setGlobalTag(TAG);

        Language.changeLanguage(getApplicationContext());

        CustomManager.getInstance().init(this);
        FileManager.getInstance().init(this);
        Debuger.getInstance().init(this);
        LooperManager.getInstance().init(this);
        DBManager.getInstance().init(this);

        if (Debuger.isDebug || Debuger.isLogDebug) {
            Stetho.initializeWithDefaults(this); //chrome://inspect
        }

        Tlog.i("SocketApplication onCreate(); pid:" + android.os.Process.myPid()
                + "; Build.VERSION.SDK_INT :" + Build.VERSION.SDK_INT);

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Tlog.e(TAG, " SocketApplication caughtException ", e);
        FileManager.getInstance().saveAppException(t, e);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tlog.v(TAG, "SocketApplication onConfigurationChanged() " + newConfig.toString());
    }

    public static void uncaughtH5Exception(String msg) {
        Tlog.e(TAG, " SocketApplication uncaughtH5Exception :\n" + msg);
        FileManager.getInstance().saveH5Exception(msg);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
