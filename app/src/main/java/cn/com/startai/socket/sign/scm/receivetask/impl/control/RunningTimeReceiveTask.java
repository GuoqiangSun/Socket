package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.util.HexUtils;
import cn.com.swain.baselib.util.StrUtil;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class RunningTimeReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public RunningTimeReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new RunningTimeReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 5) {
            Tlog.e(TAG, " RunningTimeReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        byte result = protocolParams[0];
        long time = ((protocolParams[0] & 0xff) << 24) | ((protocolParams[1] & 0xff) << 16)
                | ((protocolParams[2] & 0xff) << 8) | (protocolParams[3] & 0xff);
        time = time << 32 >>> 32;

        Tlog.e(TAG, " RunningTimeReceiveTask result:" + result + " id:" + mSocketDataArray.getID()
                + " time:" + time);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryRunningTimeResult(mSocketDataArray.getID(),
                    SocketSecureKey.Util.resultIsOk(result),
                    time);
        }

    }
}
