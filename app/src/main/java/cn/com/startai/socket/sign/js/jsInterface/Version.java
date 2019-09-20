package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/11 0011
 * desc :
 */
public class Version extends AbsHandlerJsInterface {


    public interface IJSVersionCallBack {

        void onJSQueryScmVersion(String mac);

        void onJSUpdateScm(String mac);
    }

    public static final class Method {

        private static final String QUERY_VERSION = "javascript:checkFirmwareVersionResponse('$mac',$update,'$version','$curVersion')";

        public static String callJsScmVersion(String mac, boolean update,
                                              double newVersion, double curVersion) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return QUERY_VERSION.replace("$mac", mac)
                    .replace("$update", String.valueOf(update))
                    .replace("$version", String.valueOf(newVersion))
                    .replace("$curVersion", String.valueOf(curVersion));
        }

        private static final String UPDATE_VERSION= "javascript:firmwareUpgradeResponse('$mac','$name',$update)";

        public static String callJsScmUpdate(String mac, String name, boolean update) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return UPDATE_VERSION.replace("$mac", mac).replace("$name", String.valueOf(name))
                    .replace("$update", String.valueOf(update));
        }

    }

    private final IJSVersionCallBack mCallBack;

    public static final String NAME_JSI = "Version";

    private String TAG = H5Config.TAG;

    public Version(Looper mLooper, IJSVersionCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void checkFirmwareVersionRequest(String mac) {
        Tlog.v(TAG, " checkFirmwareVersionRequest mac:" + mac);
        getHandler().obtainMessage(QUERY_SCM_VERSION, mac).sendToTarget();
    }

    @JavascriptInterface
    public void firmwareUpgradeRequest(String mac) {
        Tlog.v(TAG, " firmwareUpgradeRequest mac:" + mac);
        getHandler().obtainMessage(UPDATE_SCM_VERSION, mac).sendToTarget();
    }

    private static final int QUERY_SCM_VERSION = 0x2D;
    private static final int UPDATE_SCM_VERSION = 0x2E;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == QUERY_SCM_VERSION) {
            if (mCallBack != null) {
                mCallBack.onJSQueryScmVersion((String) msg.obj);
            }
        } else if (msg.what == UPDATE_SCM_VERSION) {
            if (mCallBack != null) {
                mCallBack.onJSUpdateScm((String) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
