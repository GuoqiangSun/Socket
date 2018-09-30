package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class HeartbeatReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public HeartbeatReceiveTask(OnTaskCallBack mCallBack) {
        Tlog.e(TAG, " new HeartbeatReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null) {
            Tlog.e(TAG, " protocolParams == null");
            return;
        }

        Tlog.v(TAG, "Heartbeat result:" + SocketSecureKey.Util.resultIsOk(protocolParams[0]) + " -value:" + protocolParams[0]);

        if (mCallBack != null) {
            mCallBack.onHeartbeatResult(mSocketDataArray.getID(), SocketSecureKey.Util.resultIsOk(protocolParams[0]));

            if (SocketSecureKey.Util.resultTokenInvalid(protocolParams[0])) {
                mCallBack.onTokenInvalid(mSocketDataArray.getID());
            }

        }

    }
}
