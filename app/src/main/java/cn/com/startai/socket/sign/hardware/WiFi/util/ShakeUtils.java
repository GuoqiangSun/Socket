package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/12/20 0020
 * Desc:
 */
public class ShakeUtils {

    private String TAG = "shake";

    private Handler Handler;
    private ShakeListener mShakeListener = null;
    private Vibrator mVibrator;

    public ShakeUtils(Context app) {
        this(app, null);
    }


    public ShakeUtils(Context app, Handler mHandler) {
        if (mHandler == null) {
            this.Handler = new Handler(Looper.getMainLooper());
        } else {
            this.Handler = mHandler;
        }

        // 获得振动器服务
        mVibrator = (Vibrator) app.getSystemService(
                Context.VIBRATOR_SERVICE);
        // 实例化加速度传感器检测类
        mShakeListener = new ShakeListener(app);

        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

            public void onShake() {
                mShakeListener.pause();

                Tlog.e(TAG, "startVibrato() ");

                if (yaoyiyao) {
                    startVibrato(); // 开始 震动
                }

                exeShake();

                if (Handler != null) {
                    Handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mVibrator != null && yaoyiyao) {
                                mVibrator.cancel();
                            }
                            mShakeListener.resume();
                        }
                    }, 2000);
                }else {
                    mShakeListener.resume();
                }

            }
        });
    }

    protected void exeShake() {

    }


    // 定义震动
    private void startVibrato() {
        if (mVibrator != null) {
            mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
        }
        // 第一个｛｝里面是节奏数组，
        // 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
    }


    public void onCreate() {

        if (mShakeListener != null) {
            mShakeListener.start();
        }
    }

    public void onDestroy() {
        if (mShakeListener != null) {
            mShakeListener.stop();
        }
    }

    private boolean yaoyiyao;

    public void setYaoyiyao(boolean b) {
        this.yaoyiyao = b;
    }
}
