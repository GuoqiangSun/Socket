package cn.com.startai.socket.sign.hardware.WiFi.util;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/9 0009
 * desc :
 * 一个检测手机摇晃的监听器 加速度传感器 values[0]： x-axis 方向加速度 values[1]： y-axis 方向加速度
 * values[2]： z-axis 方向加速度
 */

public class ShakeListener implements SensorEventListener {

    private String TAG = "shake";

    private static final double SPEED_SHRESHOLD_DEFAULT = 3.2;
    private static final int UPTATE_INTERVAL_TIME_DEFAULT = 70;


    // 速度的阈值，当摇晃速度达到这值后产生作用
    private static final double SPEED_SHRESHOLD = SPEED_SHRESHOLD_DEFAULT;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = UPTATE_INTERVAL_TIME_DEFAULT;


    // 传感器管理器
    private SensorManager sensorManager;
    // 重力感应监听器
    private OnShakeListener onShakeListener;
    // 上下文
    private Context mContext;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    // 构造器
    public ShakeListener(Context c) {
        // 获得监听对象
        mContext = c;
    }

    // 开始
    public void start() {
        // 获得传感器管理器
        sensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // 注册
            if (sensor != null) {

                // 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                // 根据不同应用，需要的反应速率不同，具体根据实际情况设定
                sensorManager.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
        pause = false;
    }

    // 停止检测
    public void stop() {
        pause = true;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private boolean pause;

    public void resume() {
        pause = false;
    }

    public void pause() {
        pause = true;
    }

    // 设置重力感应监听器
    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    private final float[] gravity = new float[3];

    // 重力感应器感应获得变化数据
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (pause) {
            return;
        }

        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME)
            return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;

        final float alpha = 0.8f;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float x = event.values[0] - gravity[0];
        float y = event.values[1] - gravity[1];
        float z = event.values[2] - gravity[2];

        // 获得x,y,z坐标
//        float x = event.values[0];
//        float y = event.values[1];
//        float z = event.values[2];

        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
                / timeInterval * 10;

//        if (speed > 3) {
//        Tlog.v(TAG, "g speed:" + speed);
//        }

        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            if (onShakeListener != null) {
                onShakeListener.onShake();
            }
            Tlog.e(TAG, "g Speed:" + speed);
        }

    }

    // 当传感器精度改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 摇晃监听接口
    public interface OnShakeListener {
        void onShake();
    }

}
