package cn.com.startai.socket.debuger.impl;

import android.content.Context;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/18 0018
 * desc :
 */
public interface IProductDetectionCallBack {

    void refreshUI();

    void clearReceiveResponse();

    void toast(String msg);

    void refreshReceiveData(String msg);

    void refreshResponseData(String msg);

    void setVerificationPath(String path);

    void flashModel(boolean on);

    Context getApp();

    void receiveProtocolAnalysisResult(byte[] protocolParams);

    void nightLightSetResult(NightLightTiming obj);

    void nightLightQueryResult(NightLightTiming obj);

    void rgbQueryResult(ColorLampRGB obj);

    void rgbSetResult(ColorLampRGB obj);
}
