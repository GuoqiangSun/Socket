package cn.com.startai.socket.app.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class CoreService extends Service {

    public static final int NOTIFICATION_ID = 0x11;

    public static final String TAG = SocketApplication.TAG;

    public CoreService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Tlog.v(TAG, " CoreService onCreate() ");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Tlog.v(TAG, " startForeground null Notification ");
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            Tlog.v(TAG, " startForeground Notification ");
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, CancelNotificationService.class));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " CoreService onDestroy() ");
    }
}
