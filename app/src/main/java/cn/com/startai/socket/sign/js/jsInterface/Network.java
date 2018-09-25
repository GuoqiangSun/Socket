package cn.com.startai.socket.sign.js.jsInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsJsInterface;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/29 0029
 * desc :
 */
public class Network extends AbsJsInterface {

    public static final class Method {

        private static final String METHOD_NETWORK_STATE = "javascript:networkStatusResponse('$type',$status,'$error')";

        public static String callJsWebServerStatusResponse(boolean status, String error) {
            return callJsStatusResponse("wideNetwork", status, error);
        }

        public static String callJsStatusResponse(String type, boolean status, String error) {
            return METHOD_NETWORK_STATE.replace("$type", String.valueOf(type))
                    .replace("$status", String.valueOf(status)).replace("$error", String.valueOf(error));
        }

    }

    public static final String NAME_JSI = "Network";

    private static String TAG = H5Config.TAG;

    public Network() {
        super(NAME_JSI);
    }

}
