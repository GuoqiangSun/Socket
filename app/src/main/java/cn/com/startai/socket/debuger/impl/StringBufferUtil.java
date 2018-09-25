package cn.com.startai.socket.debuger.impl;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import cn.com.startai.socket.R;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/22 0022
 * desc :
 */
public class StringBufferUtil {

    private final StringBuffer sb;
    private final Handler mUIHandler;
    private final Context mApp;
    private static final int CAPACITY_SAVE_LOG = 1024 * 1024 * 5;

    StringBufferUtil(Handler mUIHandler, Context mApp) {
        this.sb = new StringBuffer(CAPACITY_SAVE_LOG);
        this.mUIHandler = mUIHandler;
        this.mApp = mApp;
    }

    private int toastTimes = 0;

    public void append(String msg) {

        if (sb.length() > CAPACITY_SAVE_LOG - 1024 * 10) {
            // warn
            if (++toastTimes <= 20) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mApp, R.string.out_of_memory_need_clear, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

        if (sb.length() < CAPACITY_SAVE_LOG - 1024 * 2) {
            sb.append(msg);
        }

    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
