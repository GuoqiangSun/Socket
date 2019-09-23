package cn.com.startai.socket.sign.scm.receivetask.impl.system;

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
public class LabelReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public LabelReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new LabelReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 257) {
            Tlog.e(TAG, " LabelReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        byte result = protocolParams[0];
        String label = new String(protocolParams, 1, protocolParams.length - 1);
        label = label.trim();
        Tlog.e(TAG, " RunningTimeReceiveTask result:" + result + " id:" + mSocketDataArray.getID()
                + " label:" + label);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryLabelResult(mSocketDataArray.getID(),
                    SocketSecureKey.Util.resultIsOk(result),
                    label);
        }

    }
}
