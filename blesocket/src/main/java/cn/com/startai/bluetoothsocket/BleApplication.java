package cn.com.startai.bluetoothsocket;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.global.CustomManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class BleApplication extends SocketApplication {

    @Override
    public void onCreate() {
        CustomManager.getInstance().initTriggerBleProject();
        super.onCreate();
    }
}
