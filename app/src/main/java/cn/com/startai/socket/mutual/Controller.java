package cn.com.startai.socket.mutual;

import android.app.Application;

import java.util.ArrayList;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.mutual.js.IAndJSCallBack;
import cn.com.startai.socket.mutual.js.impl.AndJsBridge;
import cn.com.startai.socket.mutual.protocol.SocketProtocolWrapper;
import cn.com.startai.socket.sign.hardware.WiFi.impl.NetworkManager;
import cn.com.startai.socket.sign.hardware.manager.AbsHardwareManager;
import cn.com.startai.socket.sign.hardware.manager.HardwareManager;
import cn.com.startai.socket.sign.js.impl.JsManager;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class Controller implements IService {

    private Controller() {
        this.isInit = false;
    }

    private static final class ClassHolder {
        private static final Controller C = new Controller();
    }

    public static Controller getInstance() {
        return ClassHolder.C;
    }

    private final ArrayList<IService> mServices = new ArrayList<>();

    private volatile boolean isInit;

    public void init(Application app, IAndJSCallBack mAndJSCallBack) {

        if (isInit) {
            Tlog.e(" Controller.init(); is already init");
            return;
        }

        isInit = true;

        //协议包裹
        final SocketProtocolWrapper mProtocolWrapper = new SocketProtocolWrapper();
        // android(ble&scm) To Js
        final AndJsBridge mAndJsBridge = new AndJsBridge(app, mAndJSCallBack);

        // js
        final JsManager mJsManager = new JsManager();

        final AbsHardwareManager mHardware = new HardwareManager(app);

        // 协议输入，jsToScm,
        final SocketScmManager mScmManager = new SocketScmManager(app);

        /******************/

        mProtocolWrapper.regOutputBase(mHardware);
        mProtocolWrapper.regInputBase(mScmManager);

        mAndJsBridge.regIJsCallHardware(mHardware);
        mAndJsBridge.regIJsManager(mJsManager);
        mAndJsBridge.regIScm(mScmManager);

        /****************/

        mServices.add(INDEX_PROTOCOL_WRAPPER, mProtocolWrapper);
        mServices.add(INDEX_AND_JS_BRIDGE, mAndJsBridge);

        mServices.add(INDEX_HW_MANAGER, mHardware);

        mServices.add(INDEX_SCM_MANAGER, mScmManager);
        mServices.add(INDEX_JS_MANAGER, mJsManager);
    }

    public SocketProtocolWrapper getProtocolWrapper() {
        if (mServices.size() <= INDEX_PROTOCOL_WRAPPER) {
            return null;
        }
        return (SocketProtocolWrapper) mServices.get(INDEX_PROTOCOL_WRAPPER);
    }

    public AndJsBridge getAndJsBridge() {
        if (mServices.size() <= INDEX_AND_JS_BRIDGE) {
            return null;
        }
        return (AndJsBridge) mServices.get(INDEX_AND_JS_BRIDGE);
    }

    public AbsHardwareManager getHardwareManager() {
        if (mServices.size() <= INDEX_HW_MANAGER) {
            return null;
        }
        return (AbsHardwareManager) mServices.get(INDEX_HW_MANAGER);
    }


    public NetworkManager getNetworkManager() {
        AbsHardwareManager hw = getHardwareManager();
        return ((HardwareManager) hw).getNetworkManager();
    }

    public SocketScmManager getScmManager() {
        if (mServices.size() <= INDEX_SCM_MANAGER) {
            return null;
        }
        return (SocketScmManager) mServices.get(INDEX_SCM_MANAGER);
    }

    public JsManager getJsManager() {
        if (mServices.size() <= INDEX_JS_MANAGER) {
            return null;
        }
        return (JsManager) mServices.get(INDEX_JS_MANAGER);
    }

    private static final int INDEX_PROTOCOL_WRAPPER = 0;
    private static final int INDEX_AND_JS_BRIDGE = 1;
    private static final int INDEX_HW_MANAGER = 2;
    private static final int INDEX_SCM_MANAGER = 3;
    private static final int INDEX_JS_MANAGER = 4;

    private volatile boolean create = false;

    @Override
    public void onSCreate() {
        if (create) {
            Tlog.e(" Controller.onSCreate(); is already create");
            return;
        }
        create = true;
        Debuger.getInstance().onSCreate();
        if (mServices.size() > 0) {
            for (IService mIService : mServices) {
                mIService.onSCreate();
            }
        }
        Tlog.v(" Controller onSCreate");
    }

    @Override
    public void onSResume() {
        if (mServices.size() > 0) {
            for (IService mIService : mServices) {
                mIService.onSResume();
            }
        }
        Debuger.getInstance().onSCreate();
        Tlog.v(" Controller onSResume");
    }

    @Override
    public void onSPause() {
        if (mServices.size() > 0) {
            for (IService mIService : mServices) {
                mIService.onSPause();
            }
        }
        Debuger.getInstance().onSPause();
        Tlog.v(" Controller onSPause");
    }

    @Override
    public void onSDestroy() {
        if (mServices.size() > 0) {
            for (IService mIService : mServices) {
                mIService.onSDestroy();
            }
        }

        mServices.clear();
        this.isInit = false;
        this.create = false;
        Debuger.getInstance().onSDestroy();
        Tlog.v(" Controller onSDestroy");
    }

    @Override
    public void onSFinish() {
        if (mServices.size() > 0) {
            for (IService mIService : mServices) {
                mIService.onSFinish();
            }
        }
        this.isInit = false;
        this.create = false;
        Debuger.getInstance().onSFinish();
        Tlog.v(" Controller onSFinish");
    }
}
