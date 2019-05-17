package cn.com.startai.socket.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

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
    public static final String TICKER = "keepRunning";

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
            Tlog.v(TAG, " CoreService startForeground null Notification ");
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            Tlog.v(TAG, " CoreService startForeground Notification ");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                //发送通知请求
                return;
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //创建通知渠道

                int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要性级别
                NotificationChannel mChannel = new NotificationChannel(TICKER, TICKER, importance);

//            String description = "渠道描述1";
//            mChannel.setDescription(description);//渠道描述
                mChannel.enableLights(true);//是否显示通知指示灯
//            mChannel.enableVibration(true);//是否振动
                mChannel.setSound(null, null);
                //创建通知渠道
                notificationManager.createNotificationChannel(mChannel);
            }


            /**
             *  实例化通知栏构造器
             */

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, TICKER);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
            Notification build = mBuilder.build();

            try {
                startForeground(NOTIFICATION_ID, build);
                startService(new Intent(this, CancelNotificationService.class));
            } catch (Exception e) {
            }
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
