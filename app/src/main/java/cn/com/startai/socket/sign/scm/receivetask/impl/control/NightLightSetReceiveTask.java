package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
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

public class NightLightSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public NightLightSetReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new NightLightSetReceiveTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 7) {
            Tlog.e(TAG, " NightLightSetReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        NightLightTiming mNightLightTiming = new NightLightTiming();
        mNightLightTiming.mac = mSocketDataArray.getID();
        mNightLightTiming.id = protocolParams[1];
        mNightLightTiming.startup = SocketSecureKey.Util.startup(protocolParams[2]);
        mNightLightTiming.startHour = protocolParams[3] & 0xFF;
        mNightLightTiming.startMinute = protocolParams[4] & 0xFF;
        mNightLightTiming.stopHour = protocolParams[5] & 0xFF;
        mNightLightTiming.stopMinute = protocolParams[6] & 0xFF;

        mNightLightTiming.startTime = mNightLightTiming.startHour + ":" + mNightLightTiming.startMinute;
        mNightLightTiming.endTime = mNightLightTiming.stopHour + ":" + mNightLightTiming.stopMinute;

        Tlog.v(TAG, "NightLightSetReceiveTask set: " + String.valueOf(mNightLightTiming));

        if (mCallBack != null) {
            mCallBack.onSetNightLightResult(result, mNightLightTiming);

            IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
            if (iDebugerStream != null) {
                iDebugerStream.receiveNightLightSet(mSocketDataArray.getObj(), mNightLightTiming);
            }

        }

    }
}
