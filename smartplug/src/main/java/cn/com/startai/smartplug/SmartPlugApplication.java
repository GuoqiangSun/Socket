package cn.com.startai.smartplug;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class SmartPlugApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initSmartPlugProject();
        super.onCreate();
    }
}
