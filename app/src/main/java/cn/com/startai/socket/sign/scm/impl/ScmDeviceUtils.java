package cn.com.startai.socket.sign.scm.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/2 0002
 * desc :
 */
public class ScmDeviceUtils {

    private IDataProtocolOutput mResponse;
    private ScmDevice.OnScmCallBack mScmCallBack;

    ScmDeviceUtils(IDataProtocolOutput mResponse, ScmDevice.OnScmCallBack mScmCallBack) {
        this.mResponse = mResponse;
        this.mScmCallBack = mScmCallBack;
    }

    /**
     * 更新时间记录
     * <p>
     * key mac
     * obj timestamp
     */
    private final Map<String, ScmDevice> mConnectDeviceMap = Collections.synchronizedMap(new HashMap<>());


    final void cleanMap() {
        for (Map.Entry<String, ScmDevice> tmpEntries : mConnectDeviceMap.entrySet()) {
            ScmDevice value = tmpEntries.getValue();
            if (value != null) {
                value.release();
            }
        }
        mConnectDeviceMap.clear();
    }

    private final Object synObj = new byte[1];

    final ScmDevice getScmDevice(String mac) {
        ScmDevice scmData = mConnectDeviceMap.get(mac);

        if (scmData == null) {
            synchronized (synObj) {
                scmData = mConnectDeviceMap.get(mac);
                if (scmData == null) {
                    scmData = new ScmDevice(mac, mResponse, mScmCallBack);
                    mConnectDeviceMap.put(mac, scmData);
                }
            }
        }

        return scmData;
    }

    final ScmDevice containScmDevice(String mac) {
        return mConnectDeviceMap.get(mac);
    }

    final void showConnectDevice() {
        for (Map.Entry<String, ScmDevice> tmpEntries : mConnectDeviceMap.entrySet()) {
            String key = tmpEntries.getKey();
            ScmDevice value = tmpEntries.getValue();
            Tlog.e(SocketScmManager.TAG, "showConnectDevice() mac:" + key + ";  " + value.toString());
        }

    }

    public synchronized void onNetworkChange() {

        for (Map.Entry<String, ScmDevice> tmpEntries : mConnectDeviceMap.entrySet()) {
            ScmDevice value = tmpEntries.getValue();
            value.putIp(null);
        }

    }
}
