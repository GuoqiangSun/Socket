package cn.com.startai.socket.debuger.impl;

import android.content.Context;

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
}
