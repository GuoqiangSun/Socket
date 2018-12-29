package cn.com.startai.socket.sign.scm.receivetask.impl.control;

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
public class CostRateSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CostRateSetReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CostRateSetReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " CostRateSetReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        byte model = protocolParams[1];

        Tlog.e(TAG, " CostRateSetReceiveTask result:" + result + " model:" + model);

        if (mTaskCallBack != null) {
            mTaskCallBack.onCostRateSetResult(result, model);
        }

    }
}
