package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class CountdownSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public CountdownSetReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new CountdownSetReceiveTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " CountdownSetReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        boolean startup = SocketSecureKey.Util.startup(protocolParams[1]);
        boolean on = SocketSecureKey.Util.on(protocolParams[2]);

        Tlog.v(TAG, " CountdownSetReceiveTask result: " + result + " startup:" + startup + " on:" + on);

//         protocolParams[0]  == 0 success  ==1 params fail
//         protocolParams[1]  == 1 启动    ==2 结束
//         protocolParams[1]  == 0 关机   ==1 开机
        if (mCallBack != null) {
            mCallBack.onSetCountdownResult(mSocketDataArray.getID(), result, startup, on);
        }

    }
}
