package cn.com.startai.socket.mutual.js;

import android.app.Activity;
import android.content.Intent;

import cn.com.startai.socket.mutual.js.bean.StatusBarBean;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/4 0004
 * desc :
 */

public interface IAndJSCallBack {

    void ajMainGoBack();

    void ajLoadJs(String methodStr);

    void skipProductDetection(String conMac);

    void ajDisableGoBack(boolean status);

    void onAjStartActivityForResult(Intent intent, int requestPhotoCode);

    void ajSkipWebActivity(String path);

    void ajSetStatusBar(StatusBarBean mStatusBar);

    Activity getActivity();
}
