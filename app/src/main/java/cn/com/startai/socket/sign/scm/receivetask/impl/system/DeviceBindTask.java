package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.util.Bit;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/1 0001
 * desc :
 */
public class DeviceBindTask extends SocketResponseTask {

    private OnTaskCallBack mOnTaskCallBack;

    public DeviceBindTask(OnTaskCallBack mOnTaskCallBack) {
        this.mOnTaskCallBack = mOnTaskCallBack;
        Tlog.e(TAG, " new DeviceBindTask ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams.length < 77) {
            Tlog.e(TAG, " param length error : " + protocolParams.length);
            if (mOnTaskCallBack != null) {
                mOnTaskCallBack.onLanBindResult(false, null);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);


        LanBindingDevice mLanBindingDevice = new LanBindingDevice();

        boolean admin = protocolParams[1] == 0x01; // 管理员
        mLanBindingDevice.setIsAdmin(admin);

        String deviceUserID = new String(protocolParams, 2, 32).trim(); // userID
        mLanBindingDevice.setOid(deviceUserID);

        String userID = new String(protocolParams, 2 + 32, 32).trim(); // userID
        mLanBindingDevice.setMid(userID);

        String mac = MacUtil.byteToMacStr(protocolParams, 2 + 32 + 32);
        mLanBindingDevice.setOmac(mac);

        byte[] CPU_BUF = new byte[4];
        CPU_BUF[3] = protocolParams[2 + 32 + 32 + 6];
        CPU_BUF[2] = protocolParams[2 + 32 + 32 + 6 + 1];
        CPU_BUF[1] = protocolParams[2 + 32 + 32 + 6 + 2];
        CPU_BUF[0] = protocolParams[2 + 32 + 32 + 6 + 3];

        String cpuInfo = String.format("%x%x%x%x", CPU_BUF[0], CPU_BUF[1], CPU_BUF[2], CPU_BUF[3]);
        mLanBindingDevice.setCpuInfo(cpuInfo);

        int isBindResult = protocolParams[2 + 32 + 32 + 6 + 4];

        Tlog.v(TAG, " deviceID:" + deviceUserID
                + " userID:" + userID + " mac:" + mac
                + " cpuInfo:" + cpuInfo
                + " " + Integer.toBinaryString(isBindResult & 0xFF));

        if (H5Config.DEFAULT_MAC.equalsIgnoreCase(mac)) {
            Tlog.e(TAG, " find 00 mac device ");
            return;
        }

        if (mOnTaskCallBack != null) {
            if (Bit.isOne(isBindResult, 0)) {
                mOnTaskCallBack.onLanBindResult(result, mLanBindingDevice);
            } else {
                mOnTaskCallBack.onLanUnBindResult(result, mLanBindingDevice);
            }

        }

    }


}
