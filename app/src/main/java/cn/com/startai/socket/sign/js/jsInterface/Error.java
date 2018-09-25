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
public class Error extends AbsHandlerJsInterface {

    public interface IJSErrorCallBack {

        void onJSError(String msg);

    }

    public static final class Method {

        private static final String METHOD_ERROR_CODE = "javascript:errorCodeResponse('$error')";

        public static String callJsErrorResponse(String error) {
            return METHOD_ERROR_CODE.replace("$error", String.valueOf(error));
        }

    }

    public static final String NAME_JSI = "Error";

    private static String TAG = H5Config.TAG;

    private IJSErrorCallBack mCallBack;

    public Error(Looper mLooper, IJSErrorCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void errorHandlerRequest(String msg) {
        Tlog.v(TAG, " errorHandlerRequest ");

        getHandler().obtainMessage(MSG_CATCH_H5_ERROR, msg).sendToTarget();

    }


    private static final int MSG_CATCH_H5_ERROR = 0x34;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_CATCH_H5_ERROR) {
            if (mCallBack != null) {
                mCallBack.onJSError((String) msg.obj);
            }
        }
    }
}
