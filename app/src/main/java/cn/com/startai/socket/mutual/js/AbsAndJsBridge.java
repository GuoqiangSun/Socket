package cn.com.startai.socket.mutual.js;

import cn.com.startai.socket.sign.hardware.AbsWiFi;
import cn.com.startai.socket.sign.hardware.IControlBle;
import cn.com.startai.socket.sign.hardware.manager.AbsHardwareManager;
import cn.com.startai.socket.sign.js.AbsJsManager;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.AbsSocketScm;
import cn.com.startai.socket.sign.scm.IVirtualSocketScm;
import cn.com.swain.baselib.log.Tlog;

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


    protected IVirtualSocketScm mScmVirtual;

    public void regIScm(IVirtualSocketScm mScmVirtual) {
        this.mScmVirtual = mScmVirtual;
        if (this.mScmVirtual != null) {
            this.mScmVirtual.regIScmResultCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsCallHW()  AbsProtocolWrapper==null ");
        }
    }

    protected AbsHardwareManager mHardwareManager;

    public void regIJsCallHardware(AbsHardwareManager mHardware) {
        this.mHardwareManager = mHardware;
        if (this.mHardwareManager != null) {
            this.mHardwareManager.regIBleResultCallBack(this);
            this.mHardwareManager.regWiFiResultCallBack(this);
        } else {
            Tlog.e(TAG, " AbsAndJsBridge regIJsCallHardware()  HardwareManager==null ");
        }
    }

    public abstract void loadJs(String method);

}
