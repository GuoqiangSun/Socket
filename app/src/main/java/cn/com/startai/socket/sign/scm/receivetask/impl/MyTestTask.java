package cn.com.startai.socket.sign.scm.receivetask.impl;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/12 0012
 * desc :
 */

public class MyTestTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public MyTestTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new MyTestTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        Tlog.d(TAG, " MyTestTask:" + mSocketDataArray.toString());

        if (mTaskCallBack != null) {
            mTaskCallBack.onTestResult(mSocketDataArray.getProtocolParams());
        }

        Tlog.d(TAG, " MyTestTask success ");
    }
}
