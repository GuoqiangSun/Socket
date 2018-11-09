package cn.com.startai.superplug;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class SuperPlugApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initMUSIKProject();
        super.onCreate();
    }
}
