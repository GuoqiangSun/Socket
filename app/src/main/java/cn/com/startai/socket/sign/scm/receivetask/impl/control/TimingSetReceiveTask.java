package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

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

        if (SocketSecureKey.Util.isAdvanceTiming(protocolParams[1])) {

            mResult.mac = mSocketDataArray.getID();
            mResult.result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
            mResult.model = protocolParams[1]; // 0x01 普通模式 0x02进阶模式

            mResult.id = protocolParams[2];//id
            mResult.state = protocolParams[3];//保存删除

            if (protocolParams.length > 13) {
                mResult.startup = SocketSecureKey.Util.startup(protocolParams[13]);//onOff
            }

            if (protocolParams.length > 14) {
                mResult.week = (byte) (protocolParams[14] & 0xFF);//week
            }

        } else if (SocketSecureKey.Util.isCommonTiming(protocolParams[1])) {
            mResult.mac = mSocketDataArray.getID();
            mResult.result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
            mResult.model = protocolParams[1]; // 0x01 普通模式 0x02进阶模式
            mResult.id = protocolParams[2];//id
            mResult.state = protocolParams[3];//保存删除

            mResult.startup = SocketSecureKey.Util.startup(protocolParams[8]);//onOff
            mResult.week = (byte) (protocolParams[5] & 0xFF);//week
//             protocolParams[6];//hour
//           protocolParams[7];//minute
        }

        Tlog.v(TAG, " TimingSetReceiveTask : " + String.valueOf(mResult));

        if (mCallBack != null) {

            mCallBack.onSetTimingResult(mSocketDataArray.getID(), mResult);

        }

    }
}
