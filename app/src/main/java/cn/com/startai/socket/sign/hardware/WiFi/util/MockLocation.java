package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2019/1/16 0016
 * Desc:
 */
public class MockLocation {

    public static MockLocation getInstance(Context mContext) {
        return new MockLocation(mContext);
    }

    Context mContext;
    LocationManager mLocationManager;

    public MockLocation(Context mContext) {
        this.mContext = mContext;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    //启动模拟位置服务
    private boolean initLocation() {

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        try {
            //如果未开启模拟位置服务，则添加模拟位置服务
            mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    0,
                    5);
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        } catch (Exception e) {
            Tlog.e(e);
            return false;
        }
        return true;
    }

    boolean mbUpdate;

    public void startMockLocation() {
        mbUpdate = initLocation();
        mlocation = new Location(LocationManager.GPS_PROVIDER);
    }

    //停止模拟位置服务
    public void stopMockLocation() {
        mbUpdate = false;

        if (mLocationManager != null) {
            try {
                mLocationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
                mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Tlog.e(e);
            }
        }
    }

    private Bundle bundle = new Bundle();
    double testData = 0.0;
    Location mlocation;

    public void asynTaskUpdateCallBack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mbUpdate) {

                    //测试的location数据
                    mlocation.setLongitude(testData++);
                    mlocation.setLatitude(testData++);
                    mlocation.setAltitude(testData++);
                    mlocation.setTime(System.currentTimeMillis());
                    mlocation.setBearing((float) 1.2);
                    mlocation.setSpeed((float) 1.2);
                    mlocation.setAccuracy((float) 1.2);

                    //额外的自定义数据，使用bundle来传递
                    bundle.putString("test1", "666");
                    bundle.putString("test2", "66666");
                    mlocation.setExtras(bundle);
                    try {
                        mlocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        mLocationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, 100, bundle,
                                System.currentTimeMillis());
                        mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mlocation);

                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Tlog.e(e);
                        return;
                    }
                }

            }
        }).start();
    }

}
