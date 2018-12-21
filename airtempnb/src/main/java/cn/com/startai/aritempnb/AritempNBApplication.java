package cn.com.startai.aritempnb;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/12/20 0006
 * desc :
 */
public class AritempNBApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initAirtempNBProject();
        super.onCreate();
    }
}
