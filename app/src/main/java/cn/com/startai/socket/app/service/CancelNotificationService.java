package cn.com.startai.socket.app.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Tlog.v(TAG, " CancelNotificationService onHandleIntent ");

        //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(CoreService.NOTIFICATION_ID, builder.build());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Tlog.v(TAG, " cancel Notification ");
        stopForeground(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(CoreService.NOTIFICATION_ID);

    }


}
