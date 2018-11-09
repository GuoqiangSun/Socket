package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/29 0029
 * desc :
 */
public class Store extends AbsHandlerJsInterface {

    public interface IJSStoreCallBack {

        void onJSGoToMall(String path);

    }


    public static final String NAME_JSI = "Store";

    private static String TAG = H5Config.TAG;

    private IJSStoreCallBack mCallBack;

    public Store(Looper mLooper, IJSStoreCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void goToMallRequest(String path) {
        Tlog.v(TAG, " goToMallRequest " + path);

        getHandler().obtainMessage(MSG_GO_MALL, path).sendToTarget();

    }


    private static final int MSG_GO_MALL = 0x01;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_GO_MALL) {
            if (mCallBack != null) {
                mCallBack.onJSGoToMall((String) msg.obj);
            }
        }
    }
}
