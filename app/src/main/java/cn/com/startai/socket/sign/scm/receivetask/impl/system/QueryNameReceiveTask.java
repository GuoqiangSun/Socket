package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import java.nio.charset.StandardCharsets;

import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/1 0001
 * desc :
 */
public class QueryNameReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mOnTaskCallBack;

    public QueryNameReceiveTask(OnTaskCallBack mOnTaskCallBack) {
        this.mOnTaskCallBack = mOnTaskCallBack;
        Tlog.e(TAG, " new QueryNameReceiveTask ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams.length < 33) {
            if (mOnTaskCallBack != null) {
                mOnTaskCallBack.onDeviceRenameResult(mSocketDataArray.getID(), false, "UNKNOWN");
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        String deviceName;
        if (CustomManager.getInstance().isTriggerBle()) {
            deviceName = new String(protocolParams, 1, 32, StandardCharsets.US_ASCII);
        } else {
            deviceName = new String(protocolParams, 1, 32);
        }
        deviceName = deviceName.trim().replaceAll("\\s*", "");
        Tlog.v(TAG, " deviceName :" + deviceName);

//        if (StrUtil.isSpecialName(deviceName)) {
//            int random = (int) ((Math.random() * 9 + 1) * 100000);
//            deviceName = "UNKNOWN" + random;
//        }

        if (mOnTaskCallBack != null) {
            mOnTaskCallBack.onQueryDeviceNameResult(mSocketDataArray.getID(), result, deviceName);
        }

    }
}
