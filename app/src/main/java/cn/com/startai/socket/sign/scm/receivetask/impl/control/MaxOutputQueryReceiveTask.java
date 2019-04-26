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
public class MaxOutputQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public MaxOutputQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new MaxOutputQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 5) {
            Tlog.e(TAG, " MaxOutputQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        float maxCurrent = (((protocolParams[1] & 0xFF)<< 8)  | (protocolParams[2] & 0xFF)) / 1000F;
        float maxPower = (((protocolParams[3]  & 0xFF)<< 8) | (protocolParams[4] & 0xFF)) / 1000F;

        Tlog.e(TAG, " MaxOutputQueryReceiveTask result:" + result
                + " maxCurrent:" + maxCurrent + " maxPower:" + maxPower);


    }
}
