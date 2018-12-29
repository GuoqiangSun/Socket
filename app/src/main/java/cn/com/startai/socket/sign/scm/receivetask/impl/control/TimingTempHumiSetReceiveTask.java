package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
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

public class TimingTempHumiSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimingTempHumiSetReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimingTempHumiSetReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] buf = mSocketDataArray.getProtocolParams();

        if (buf == null || buf.length < 16) {
            Tlog.e(TAG, " TimingSetReceiveTask params is error ... ");
//            if (mCallBack != null) {
//                mCallBack.onSetTimingTempHumiResult(false, mSocketDataArray.getID(), null);
//            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(buf[0]);

        TimingTempHumiData mAdvanceData = new TimingTempHumiData();
        mAdvanceData.mac = mSocketDataArray.getID();
        mAdvanceData.result = result;

        mAdvanceData.type = buf[1];
        mAdvanceData.id = buf[2];

        mAdvanceData.confirm = buf[3];

        mAdvanceData.startHour = buf[4] & 0xFF;
        mAdvanceData.startMinute = buf[5] & 0xFF;
        mAdvanceData.setOnTime(mAdvanceData.startHour + ":" + mAdvanceData.startMinute);

        mAdvanceData.endHour = buf[6] & 0xFF;
        mAdvanceData.endMinute = buf[7] & 0xFF;
        mAdvanceData.setOffTime(mAdvanceData.endHour + ":" + mAdvanceData.endMinute);

        mAdvanceData.on = SocketSecureKey.Util.on(buf[8]);

        mAdvanceData.onIntervalHour = buf[9] & 0xFF;
        mAdvanceData.onIntervalMinute = buf[10] & 0xFF;
        mAdvanceData.onIntervalTime = mAdvanceData.onIntervalHour + ":" + mAdvanceData.onIntervalMinute;

        mAdvanceData.offIntervalHour = buf[11] & 0xFF;
        mAdvanceData.offIntervalMinute = buf[12] & 0xFF;
        mAdvanceData.offIntervalTime = mAdvanceData.offIntervalHour + ":" + mAdvanceData.offIntervalMinute;

        mAdvanceData.startup = SocketSecureKey.Util.startup(buf[13]);
        mAdvanceData.week = buf[14];

        mAdvanceData.alarmValue = buf[15];
        mAdvanceData.model = buf[16];

        Tlog.v(TAG, " TimingSetReceiveTask : " + String.valueOf(mAdvanceData));

        if (mCallBack != null) {
            mCallBack.onSetTimingTempHumiResult(result, mSocketDataArray.getID(), mAdvanceData);
        }

    }
}
