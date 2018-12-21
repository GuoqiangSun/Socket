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

        if (buf == null || buf.length < 3) {
            Tlog.e(TAG, " TimingTempHumiQueryReceiveTask params is error ... ");
//            if (mCallBack != null) {
//                mCallBack.onQueryTimingTempHumiResult(false, mSocketDataArray.getID(), null);
//            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(buf[0]);

        int start = 3; // 新的是从三开始
        int m = (buf.length - start) / 16;

        byte type = 0x00; // 定温度 定湿度
        byte model = 0x00; // 制冷，制热

        if (m * 16 + 3 != buf.length) {
            start = 1;
            m = (buf.length - start) / 16;
        }else {
            type = buf[1];
            model = buf[2];
        }

        Tlog.v(TAG, " TimingTempHumiQueryReceiveTask.size:" + buf.length
                + " m:" + m + " start:" + start + " type:" + type + " model:" + model);

        TimingTempHumiData mAdvanceData;

        ArrayList<TimingTempHumiData> mDataLst = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            mAdvanceData = new TimingTempHumiData();
            mDataLst.add(mAdvanceData);

            mAdvanceData.mac = mSocketDataArray.getID();
            mAdvanceData.result = result;

            type =  mAdvanceData.type = buf[i * 16 + start];
            mAdvanceData.id = buf[i * 16 + start + 1];

            mAdvanceData.confirm = buf[i * 16 + start + 2];

            mAdvanceData.startHour = buf[i * 16 + start + 3] & 0xFF;
            mAdvanceData.startMinute = buf[i * 16 + start + 4] & 0xFF;
            mAdvanceData.setOnTime(mAdvanceData.startHour + ":" + mAdvanceData.startMinute);

            mAdvanceData.endHour = buf[i * 16 + start + 5] & 0xFF;
            mAdvanceData.endMinute = buf[i * 16 + start + 6] & 0xFF;
            mAdvanceData.setOffTime(mAdvanceData.endHour + ":" + mAdvanceData.endMinute);

            mAdvanceData.on = SocketSecureKey.Util.on(buf[i * 16 + start + 7]);

            mAdvanceData.onIntervalHour = buf[i * 16 + start + 8] & 0xFF;
            mAdvanceData.onIntervalMinute = buf[i * 16 + start + 9] & 0xFF;
            mAdvanceData.onIntervalTime = mAdvanceData.onIntervalHour + ":" + mAdvanceData.onIntervalMinute;

            mAdvanceData.offIntervalHour = buf[i * 16 + start + 10] & 0xFF;
            mAdvanceData.offIntervalMinute = buf[i * 16 + start + 11] & 0xFF;
            mAdvanceData.offIntervalTime = mAdvanceData.offIntervalHour + ":" + mAdvanceData.offIntervalMinute;

            mAdvanceData.startup = SocketSecureKey.Util.startup(buf[i * 16 + start + 12]);
            mAdvanceData.week = buf[i * 16 + start + 13];

            mAdvanceData.alarmValue = buf[i * 16 + start + 14];
            model= mAdvanceData.model = buf[i * 16 + start + 15];

            Tlog.v(TAG, " TimingTempHumiQueryReceiveTask :" + String.valueOf(mAdvanceData));
        }

        if (mCallBack != null) {
            mCallBack.onQueryTimingTempHumiResult(result, mSocketDataArray.getID(), mDataLst);

        }

    }
}
