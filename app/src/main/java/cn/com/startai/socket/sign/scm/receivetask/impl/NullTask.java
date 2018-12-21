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

public class NullTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public NullTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new NullTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        Tlog.e(TAG, String.valueOf(mSocketDataArray));

    }

}
