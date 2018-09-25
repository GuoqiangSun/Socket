package cn.com.startai.socket.debuger.impl;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/17 0017
 * desc :
 */
public interface IRegDebugerProtocolStream {

    void regIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager);

    void unregIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager);


}
