package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class QueryTimezoneReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public QueryTimezoneReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new QueryTimezoneReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " QueryTimezoneReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        Tlog.e(TAG, " QueryTimezoneReceiveTask result:" + result + " params:" + protocolParams[0] + " zone:" + protocolParams[1]);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryTimezoneResult(result, mSocketDataArray.getID(), protocolParams[1]);
        }

    }
}
