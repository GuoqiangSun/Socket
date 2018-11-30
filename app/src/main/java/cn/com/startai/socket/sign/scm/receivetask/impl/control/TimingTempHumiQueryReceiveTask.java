package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.util.ArrayList;

import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
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

public class TimingTempHumiQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimingTempHumiQueryReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimingTempHumiQueryReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] buf = mSocketDataArray.getProtocolParams();

        if (buf == null || buf.length < 17) {
            Tlog.e(TAG, " TimingTempHumiQueryReceiveTask params is error ... ");
            if (mCallBack != null) {
                mCallBack.onQueryTimingTempHumiResult(false, mSocketDataArray.getID(), null);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(buf[0]);

        int m = (buf.length - 1) / 16;

        Tlog.v(TAG, " TimingTempHumiQueryReceiveTask.size:" + buf.length + " m:" + m);

        TimingTempHumiData mAdvanceData;

        ArrayList<TimingTempHumiData> mDataLst = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            mAdvanceData = new TimingTempHumiData();
            mDataLst.add(mAdvanceData);

            mAdvanceData.mac = mSocketDataArray.getID();
            mAdvanceData.result = result;

            mAdvanceData.type = buf[i * 16 + 1];
            mAdvanceData.id = buf[i * 16 + 2];

            mAdvanceData.confirm = buf[i * 16 + 3];

            mAdvanceData.startHour = buf[i * 16 + 4] & 0xFF;
            mAdvanceData.startMinute = buf[i * 16 + 5] & 0xFF;
            mAdvanceData.setOnTime(mAdvanceData.startHour + ":" + mAdvanceData.startMinute);

            mAdvanceData.endHour = buf[i * 16 + 6] & 0xFF;
            mAdvanceData.endMinute = buf[i * 16 + 7] & 0xFF;
            mAdvanceData.setOffTime(mAdvanceData.endHour + ":" + mAdvanceData.endMinute);

            mAdvanceData.on = SocketSecureKey.Util.on(buf[i * 16 + 8]);

            mAdvanceData.onIntervalHour = buf[i * 16 + 9] & 0xFF;
            mAdvanceData.onIntervalMinute = buf[i * 16 + 10] & 0xFF;
            mAdvanceData.onIntervalTime = mAdvanceData.onIntervalHour + ":" + mAdvanceData.onIntervalMinute;

            mAdvanceData.offIntervalHour = buf[i * 16 + 11] & 0xFF;
            mAdvanceData.offIntervalMinute = buf[i * 16 + 12] & 0xFF;
            mAdvanceData.offIntervalTime = mAdvanceData.offIntervalHour + ":" + mAdvanceData.offIntervalMinute;

            mAdvanceData.startup = SocketSecureKey.Util.startup(buf[i * 16 + 13]);
            mAdvanceData.week = buf[i * 16 + 14];

            mAdvanceData.alarmValue = buf[i * 16 + 15];
            mAdvanceData.model = buf[i * 16 + 16];

            Tlog.v(TAG, " TimingTempHumiQueryReceiveTask :" + String.valueOf(mAdvanceData));

        }

        if (mCallBack != null) {
            mCallBack.onQueryTimingTempHumiResult(result, mSocketDataArray.getID(), mDataLst);

        }

    }
}
