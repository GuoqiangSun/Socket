package cn.com.startai.socket.debuger.impl;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/17 0017
 * desc :
 */
public interface IDebugerProtocolStream {


    /**
     * 发送的数据
     *
     * @param mResponseData
     */
    void responseData(ResponseData mResponseData);

    /////////////////////////////////

    void receiveFail(FailTaskResult mFailTask);

    /**
     * 接收数据
     *
     * @param mReceiverData
     */
    void receiveData(ReceivesData mReceiverData);

    /**
     * 开继电器
     */
    void receiveOpenRelay(Object model, String mac);

    /**
     * 关继电器
     */
    void receiveCloseRelay(Object model, String mac);

    /**
     * 查询继电器
     */
    void receiveQueryRelay(Object model, String mac);


    /**
     * 查询时间
     */
    void receiveQueryTime(Object model, String mac);

    /**
     * 查询倒计时
     */
    void receiveTiming(Object model, String mac);

    /**
     * 查询倒计时
     */
    void receiveCountdown(Object model, String mac);


    void connected(String address);

    void disconnected(String address);

    void receiveControlFlashState(Object obj, String id, boolean on);

    void receiveQueryFlashState(Object obj, String id, boolean on);

    void receiveProtocolAnalysisResult(byte[] protocolParams);

    void receiveNightLightSet(Object obj, NightLightTiming mNightLightTiming);

    void receiveNightLightQuery(Object obj, NightLightTiming mNightLightTiming);

    void receiveQueryRGB(Object obj, boolean result, ColorLampRGB mRGB);

    void receiveSetRGB(Object obj, boolean result, ColorLampRGB mRGB);

    void receiveHistory(Object obj, boolean result);
}
