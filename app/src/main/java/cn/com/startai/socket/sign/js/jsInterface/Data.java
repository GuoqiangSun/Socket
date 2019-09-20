package cn.com.startai.socket.sign.js.jsInterface;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/29 0029
 * desc :
 */
public class Data {

    public static final class Method {
        private static final String METHOD_RESPONSE = "javascript:dataInteractionResponse('$data')";

        public static final String callJs(String json) {
            return METHOD_RESPONSE.replace("$data", String.valueOf(json));
        }
    }

    public Data() {

    }

    public static final String NAME_JSI = "Data";

    private String TAG = H5Config.TAG;

    @JavascriptInterface
    public void dataInteractionRequest(String json) {
        Tlog.v(TAG, " dataInteractionRequest " + json);
        String j = "{\"msgType\":\"0000020101\",\"message\":\"01\",\"result\":\"01\"}";
        String s = Method.callJs(j);
        Controller.getInstance().getAndJsBridge().loadJs(s);
    }

}
