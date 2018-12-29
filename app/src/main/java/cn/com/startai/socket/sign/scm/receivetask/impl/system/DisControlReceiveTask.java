package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class DisControlReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public DisControlReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new DisControlReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 1) {
            Tlog.e(TAG, " protocolParams error:" + mSocketDataArray.toString());
            if (mTaskCallBack != null) {
                mTaskCallBack.onDisconnectResult(false, mSocketDataArray.getID());
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        if (mTaskCallBack != null) {
            mTaskCallBack.onDisconnectResult(result, mSocketDataArray.getID());
        }

    }
}
