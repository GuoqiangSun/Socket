package shared.startai.com.cn.myapplication;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class TestApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initTestSocketProject();
        super.onCreate();
    }
}
