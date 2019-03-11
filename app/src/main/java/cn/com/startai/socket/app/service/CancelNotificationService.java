package cn.com.startai.socket.app.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import cn.com.startai.socket.R;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class CancelNotificationService extends IntentService {


    public CancelNotificationService() {
        super("CancelNotificationService");
    }

    private String TAG = CoreService.TAG;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Tlog.v(TAG, " CancelNotificationService onCreate ");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Tlog.v(TAG, " CancelNotificationService onDestroy ");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Tlog.v(TAG, " CancelNotificationService onHandleIntent ");

        //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Tlog.v(TAG, " CancelNotificationService startForeground  ");
            startForeground(CoreService.NOTIFICATION_ID, new Notification());
        } else {
            Tlog.v(TAG, " CancelNotificationService startForeground Notification ");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                //发送通知请求
                return;
            }

            String id = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //创建通知渠道

                int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要性级别
                NotificationChannel mChannel = new NotificationChannel(CoreService.TICKER, CoreService.TICKER, importance);

//            String description = "渠道描述1";
//            mChannel.setDescription(description);//渠道描述
                mChannel.enableLights(true);//是否显示通知指示灯
//            mChannel.enableVibration(true);//是否振动
                mChannel.setSound(null, null);

                //创建通知渠道
                notificationManager.createNotificationChannel(mChannel);
                id = mChannel.getId();
            }


            /**
             *  实例化通知栏构造器
             */

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CoreService.TICKER);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
            Notification build = mBuilder.build();

            startForeground(CoreService.NOTIFICATION_ID, build);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stopForeground(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && id != null) {
                Tlog.v(TAG, " CancelNotificationService deleteNotificationChannel ");
                notificationManager.deleteNotificationChannel(id);
            } else {
                Tlog.v(TAG, " CancelNotificationService cancel Notification ");
                notificationManager.cancel(CoreService.NOTIFICATION_ID);
            }

        }


    }
}