package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class IndicatorStatusQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public IndicatorStatusQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new IndicatorStatusQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " IndicatorStatusQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        byte seq = protocolParams[1];
        boolean on = SocketSecureKey.Util.on(protocolParams[2]);

        Tlog.e(TAG, " IndicatorStatusQueryReceiveTask result:" + result + " seq:" + seq + " on:" + on);

        if (mTaskCallBack != null) {
            mTaskCallBack.onIndicatorStatusResult(mSocketDataArray.getID(),result, seq, on);
        }

    }
}
