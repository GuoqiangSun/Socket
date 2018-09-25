package cn.com.startai.socket.mutual.js;

import cn.com.startai.socket.sign.hardware.AbsWiFi;
import cn.com.startai.socket.sign.hardware.IControlBle;
import cn.com.startai.socket.sign.hardware.IControlWiFi;
import cn.com.startai.socket.sign.js.AbsJsManager;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.AbsSocketScm;
import cn.com.startai.socket.sign.scm.IVirtualSocketScm;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public abstract class AbsAndJsBridge implements AbsJsManager.IJSManagerCallback,//
        IControlBle.IBleResultCallBack, AbsSocketScm.IScmResultCallBack,//
        AbsWiFi.IWiFiResultCallBack {

    public static final String TAG = H5Config.TAG;

    public void regIJsManager(AbsJsManager mJsManager) {
        if (mJsManager != null) {
            mJsManager.regJsManagerCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsManager()  AbsJsManager==null ");
        }
    }

    protected IControlBle mBleManager;

    public void regIJsCallBle(IControlBle mBleManager) {
        this.mBleManager = mBleManager;
        if (this.mBleManager != null) {
            this.mBleManager.regIBleResultCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsCallHW()  IControlBle==null ");
        }
    }

    protected IVirtualSocketScm mScmVirtual;

    public void regIScm(IVirtualSocketScm mScmVirtual) {
        this.mScmVirtual = mScmVirtual;
        if (this.mScmVirtual != null) {
            this.mScmVirtual.regIScmResultCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsCallHW()  AbsProtocolWrapper==null ");
        }
    }

    protected IControlWiFi mNetworkManager;

    public void regIJsCallWiFi(IControlWiFi mWiFi) {
        this.mNetworkManager = mWiFi;

        if (this.mNetworkManager != null) {
            mWiFi.regWiFiResultCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsCallWiFi()  IControlWiFi==null ");
        }
    }

    public abstract void loadJs(String method);

}
