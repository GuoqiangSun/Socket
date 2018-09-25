package cn.com.startai.socket.sign.hardware.WiFi.util.udp;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */
public interface ISocketResult {

    void onSocketInitResult(boolean result, String ip, int port);

    void onSocketReceiveData(String ip, int port, byte[] data);

//    void broadc

}
