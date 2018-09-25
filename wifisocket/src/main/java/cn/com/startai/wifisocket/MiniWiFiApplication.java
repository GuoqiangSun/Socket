package cn.com.startai.wifisocket;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class MiniWiFiApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initWiFiSocketProject();
        super.onCreate();
    }
}
