package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
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
                mCallBack.onSetTimingResult(mSocketDataArray.getID(), false);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        byte model = protocolParams[1]; // 0x01 普通模式 0x02进阶模式
        byte id = protocolParams[2];//id
        byte state = protocolParams[3];//保存删除
        byte on = protocolParams[4];//onOff
        byte week = (byte) (protocolParams[5] & 0xFF);//week
        byte hour = protocolParams[6];//hour
        byte minute = protocolParams[7];//minute
//        byte startup = protocolParams[8];//minute

        Tlog.v(TAG, " result:" + result + " model:" + model + " id:" + id + " state:" + state + " on:" + on + " week:" + week + " hour:" + hour + " minute:" + minute);

        if (mCallBack != null) {

            mCallBack.onSetTimingResult(mSocketDataArray.getID(), result);

        }

    }
}
