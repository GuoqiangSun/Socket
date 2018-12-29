package cn.com.startai.socket.sign.scm.receivetask.impl;

import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.ProtocolErrorTask;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/17 0017
 * desc :
 */
public class ScmErrorTask extends ProtocolErrorTask {

    private OnTaskCallBack mTaskCallBack;

    public ScmErrorTask(int errorCode, OnTaskCallBack mTaskCallBack) {
        super(errorCode);
        Tlog.e(TAG, " new ScmErrorTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {
       super.doTask(mSocketDataArray);
        if (mTaskCallBack != null) {
            mTaskCallBack.onFail(mTask);
        }
    }

}
