package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class TimingSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimingSetReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimingSetReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " TimingSetReceiveTask params is error ... ");
            if (mCallBack != null) {
                mCallBack.onSetTimingResult(mSocketDataArray.getID(), null);
            }
            return;
        }

        TimingSetResult mResult = new TimingSetResult();
        mResult.mac = mSocketDataArray.getID();
        mResult.result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        mResult.model = protocolParams[1]; // 0x01 普通模式 0x02进阶模式
        mResult.id = protocolParams[2];//id
        mResult.state = protocolParams[3];//保存删除
        mResult.on = SocketSecureKey.Util.on(protocolParams[4]);//onOff
        mResult.week = (byte) (protocolParams[5] & 0xFF);//week
        mResult.hour = protocolParams[6];//hour
        mResult.minute = protocolParams[7];//minute
//        byte startup = protocolParams[8];//minute

        Tlog.v(TAG, String.valueOf(mResult));

        if (mCallBack != null) {

            mCallBack.onSetTimingResult(mSocketDataArray.getID(), mResult);

        }

    }
}
