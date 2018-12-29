package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class NightLightQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public NightLightQueryReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new NightLightQueryReceiveTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 7) {
            Tlog.e(TAG, " NightLightQueryReceiveTask params is error ... " + String.valueOf(mSocketDataArray));

            if (protocolParams != null && protocolParams.length >= 2) {
                boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
                NightLightTiming mNightLightTiming = new NightLightTiming();
                mNightLightTiming.mac = mSocketDataArray.getID();
                mNightLightTiming.id = protocolParams[1];
                if (mCallBack != null) {
                    mCallBack.onQueryNightLightResult(result, mNightLightTiming);
                }
            }

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

        Tlog.v(TAG, "NightLightQueryReceiveTask query: " + String.valueOf(mNightLightTiming));

        if (mCallBack != null) {
            mCallBack.onQueryNightLightResult(result, mNightLightTiming);

            IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
            if (iDebugerStream != null) {
                iDebugerStream.receiveNightLightQuery(mSocketDataArray.getObj(), mNightLightTiming);
            }

        }

    }
}
