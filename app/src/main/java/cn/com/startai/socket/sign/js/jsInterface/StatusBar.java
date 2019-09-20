package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/11 0011
 * desc :
 */
public class StatusBar extends AbsHandlerJsInterface {


    public interface IJSStatusBarCallBack {

        void onJSSetStatusBar(StatusBarBean mStatusBar);

    }

    public static final class Method {

//        private static final String RENAME = "javascript:deviceHistoryDataResponse('$time',$day,'$data')";
//
//        public static String callJsHistoryData(String time, int day, String data) {
//            return RENAME.replace("$time", String.valueOf(time))
//                    .replace("$day", String.valueOf(day)).replace("$data", String.valueOf(data));
//        }

    }

    private final IJSStatusBarCallBack mCallBack;

    public static final String NAME_JSI = "statusBar";

    private String TAG = H5Config.TAG;

    public StatusBar(Looper mLooper, IJSStatusBarCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void setStatusBarRequest(boolean show, String color) {
        Tlog.v(TAG, " setStatusBarRequest show:" + show);

        StatusBarBean mStatusBar = new StatusBarBean();
        mStatusBar.show = show;
        mStatusBar.color = color;
        getHandler().obtainMessage(MSG_SET_STATUS_BAR, mStatusBar).sendToTarget();

    }

    private static final int MSG_SET_STATUS_BAR = 0x2D;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_SET_STATUS_BAR) {
            if (mCallBack != null) {
                mCallBack.onJSSetStatusBar((StatusBarBean) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
