package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.ConstTempTiming;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class CosntTempTimingSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CosntTempTimingSetReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CosntTempTimingSetReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 11) {
            Tlog.e(TAG, " CosntTempTimingSetReceiveTask error:" + mSocketDataArray.toString());

            return;
        }

        ConstTempTiming mConstTempTiming = new ConstTempTiming();

        mConstTempTiming.mac = mSocketDataArray.getID();
        mConstTempTiming.result = protocolParams[0] & 0xFF;
        mConstTempTiming.id = protocolParams[1] & 0xFF;
        mConstTempTiming.model = protocolParams[2] & 0xFF;
        mConstTempTiming.startup = protocolParams[3] & 0xFF;
        mConstTempTiming.minTemp = protocolParams[4] & 0xFF;
        mConstTempTiming.maxTemp = protocolParams[5] & 0xFF;
        mConstTempTiming.week = protocolParams[6] & 0xFF;
        mConstTempTiming.startHour = protocolParams[7] & 0xFF;
        mConstTempTiming.startMinute = protocolParams[8] & 0xFF;
        mConstTempTiming.endHour = protocolParams[9] & 0xFF;
        mConstTempTiming.endMinute = protocolParams[10] & 0xFF;

        if (mTaskCallBack != null) {
            mTaskCallBack.onSetConstTempTimingResult(mConstTempTiming);
        }

    }
}
