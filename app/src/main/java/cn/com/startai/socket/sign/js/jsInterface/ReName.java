package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/11 0011
 * desc :
 */
public class ReName extends AbsHandlerJsInterface {


    public interface IJSRenameCallBack {

        void onJSRename(RenameBean mDisplayDevice);

    }

    public static final class Method {

        private static final String RENAME = "javascript:reNameResponse('$mac',$result)";

        public static String callJsRename(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return RENAME.replace("$mac", mac).replace("$result", String.valueOf(result));
        }

    }

    private final IJSRenameCallBack mCallBack;

    public static final String NAME_JSI = "ReName";

    private String TAG = H5Config.TAG;

    public ReName(Looper mLooper, IJSRenameCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void reNameRequest(String mac, String name) {
        Tlog.v(TAG, " reNameRequest name:" + name);
        final RenameBean mDisplayDevice = new RenameBean();
        mDisplayDevice.address = mac;
        mDisplayDevice.name = name;
        getHandler().obtainMessage(MSG_RENAME, mDisplayDevice).sendToTarget();

    }

    private static final int MSG_RENAME = 0x2D;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_RENAME) {
            if (mCallBack != null) {
                mCallBack.onJSRename((RenameBean) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
