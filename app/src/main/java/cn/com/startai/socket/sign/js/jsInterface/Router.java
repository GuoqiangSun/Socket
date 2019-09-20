package cn.com.startai.socket.sign.js.jsInterface;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/29 0029
 * desc :
 */

public class Router extends AbsJsInterface {


    public interface IJSRouterCallBack {

        void onJSRFinish();

        void onJSRDisableGoBack(boolean status);
    }

    public static final class Method {
        private static final String METHOD_PRESS_BACK = "javascript:goBackResponse()";

        /**
         * 手机返回物理按键
         *
         * @return
         */
        public static final String callJsPressBack() {
            return METHOD_PRESS_BACK;
        }
    }

    public static final String NAME_JSI = "Router";

    private String TAG = H5Config.TAG;

    private final IJSRouterCallBack mCallBack;

    public Router(IJSRouterCallBack mCallBack) {
        super(NAME_JSI);
        this.mCallBack = mCallBack;
    }


    /**
     * 首页返回事件
     */
    @JavascriptInterface
    public void goBackRequest() {
        Tlog.v(TAG, " goBackRequest ");
        if (mCallBack != null) {
            mCallBack.onJSRFinish();
        }

    }

    /**
     * 是否返回事件
     */
    @JavascriptInterface
    public void disableGoBackRequest(boolean status) {
        Tlog.v(TAG, " disableGoBackRequest " + status);
        if (mCallBack != null) {
            mCallBack.onJSRDisableGoBack(status);
        }
    }


}
