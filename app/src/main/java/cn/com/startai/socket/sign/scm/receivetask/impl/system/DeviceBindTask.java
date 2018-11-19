package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.baselib.util.Bit;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

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

        String deviceUserID = new String(protocolParams, 2, 32); // userID
        mLanBindingDevice.setOid(deviceUserID.trim());

        String userID = new String(protocolParams, 2 + 32, 32); // userID
        mLanBindingDevice.setMid(userID.trim());

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
                + " userID" + userID + " mac:" + mac
                + " cpuInfo:" + cpuInfo
                + " " + Integer.toBinaryString(isBindResult & 0xFF));
        if (mOnTaskCallBack != null) {
            if (Bit.isOne(isBindResult, 0)) {
                mOnTaskCallBack.onLanBindResult(result, mLanBindingDevice);
            } else {
                mOnTaskCallBack.onLanUnBindResult(result, mLanBindingDevice);
            }

        }

    }


}
