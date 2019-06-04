package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class CosntTempTimingDelReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CosntTempTimingDelReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CosntTempTimingDelReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " CosntTempTimingDelReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        byte result = protocolParams[0];
        byte id = protocolParams[1];
        byte model = protocolParams[2];

        Tlog.e(TAG, " CosntTempTimingDelReceiveTask result:" + result + " id:" + id + " model:" + model);

        if (mTaskCallBack != null) {
            mTaskCallBack.onDelConstTempTimingResult(mSocketDataArray.getID(), result, id, model);
        }

    }
}
