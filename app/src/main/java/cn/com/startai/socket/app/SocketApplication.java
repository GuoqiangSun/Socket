package cn.com.startai.socket.app;

import android.content.res.Configuration;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.tencent.smtt.sdk.QbSdk;

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
        Tlog.setGlobalTag(TAG);
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Tlog.d(TAG, " HDTrainerApp onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
                Tlog.d(TAG, " HDTrainerApp onCoreInitFinished  ");
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);


        Language.changeLanguage(getApplicationContext());

        CustomManager.getInstance().init(this);
        FileManager.getInstance().init(this);
        Debuger.getInstance().init(this);
        LooperManager.getInstance().init(this);
        DBManager.getInstance().init(this);

        if (Debuger.isDebug || Debuger.isLogDebug || Debuger.isH5Debug) {
            Stetho.initializeWithDefaults(this); //chrome://inspect
        }

        Tlog.i("SocketApplication onCreate(); pid:" + android.os.Process.myPid()
                + "; SDK_INT :" + Build.VERSION.SDK_INT + " RELEASE:" + Build.VERSION.RELEASE);

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
        throw new RuntimeException(msg);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
    }
}
