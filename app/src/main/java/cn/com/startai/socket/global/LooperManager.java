package cn.com.startai.socket.global;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.swain.baselib.app.IApp.IApp;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/2 0002
 * desc :
 */

public class LooperManager implements IApp {

    private LooperManager() {
    }

    private static final class ClassHolder {
        private static final LooperManager LM = new LooperManager();
    }

    public static LooperManager getInstance() {
        return ClassHolder.LM;
    }

    private static final ExecutorService pool = Executors.newSingleThreadExecutor();

    public void execute(Runnable r) {
        pool.execute(r);
    }

    private Handler mWorkHandler;

    public Handler getWorkHandler() {
        initWorkHandler();
        return mWorkHandler;
    }

    @Override
    public void init(Application app) {
//        startProtocolThread();
//        startWorkThread();
//        startRepeatThread();
//        mWorkHandler = new Handler(getWorkLooper());
        Tlog.i(" LooperManager init success...");
    }

    private void initWorkHandler() {
        if (mWorkHandler == null) {
            synchronized (this) {
                if (mWorkHandler == null) {
                    mWorkHandler = new Handler(getWorkLooper());
                }
            }
        }
    }

    /**
     * 协议解析的线程
     */
    private HandlerThread mProtocolThread;

    private void startProtocolThread() {
        if (mProtocolThread == null) {
            synchronized (this) {
                if (mProtocolThread == null) {
                    mProtocolThread = new HandlerThread("Protocol", Process.THREAD_PRIORITY_DISPLAY);
                    mProtocolThread.start();
                }

            }
        }
    }

    public final Looper getProtocolLooper() {
        startProtocolThread();
        return mProtocolThread.getLooper();
    }


    /**
     * 工作的线程
     */
    private HandlerThread mWorkThread;

    private void startWorkThread() {
        if (mWorkThread == null) {
            synchronized (this) {
                if (mWorkThread == null) {
                    mWorkThread = new HandlerThread("Work", Process.THREAD_PRIORITY_DISPLAY);
                    mWorkThread.start();
                }
            }
        }
    }

    public final Looper getWorkLooper() {
        startWorkThread();
        return mWorkThread.getLooper();
    }

    /**
     * 重复的线程
     */
    private HandlerThread mRepeatThread;

    private void startRepeatThread() {
        if (mRepeatThread == null) {
            synchronized (this) {
                if (mRepeatThread == null) {
                    mRepeatThread = new HandlerThread("Repeat", Process.THREAD_PRIORITY_DISPLAY);
                    mRepeatThread.start();
                }

            }
        }
    }

    public final Looper getRepeatLooper() {
        startRepeatThread();
        return mRepeatThread.getLooper();
    }

}

