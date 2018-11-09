package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class TimeSetReceiveTask extends SocketResponseTask {

    public TimeSetReceiveTask() {
        Tlog.e(TAG, " new TimeSetReceiveTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length != 1) {
            Tlog.e(TAG, " TimeSetReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        Tlog.v(TAG, " relay result: " + result);


    }
}
