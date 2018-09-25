package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.ProtocolDataCache;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class TimingExecuteReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimingExecuteReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimingExecuteReportReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getSeq();

        if (protocolParams == null || protocolParams.length < 6) {
            Tlog.e(TAG, " TimingExecuteReportReceiveTask params is error ... " + mSocketDataArray.toString());
            ResponseData mResponseData = ProtocolDataCache.getTimingExecuteReport(mSocketDataArray.getID(), true, seq);
            response(mResponseData);
            return;
        }


        TimingCommonData mCommonData = null;
        TimingAdvanceData mAdvanceData = null;

        if (SocketSecureKey.Util.isCommonTiming(protocolParams[0])) {
            mCommonData = new TimingCommonData();
            mCommonData.setModel(protocolParams[0]);// 0x01 普通模式 0x02进阶模式
            mCommonData.setId(protocolParams[1]);
            mCommonData.setOn(SocketSecureKey.Util.on(protocolParams[2]));
            mCommonData.setWeek(protocolParams[3] & 0xFF);
            int hour = protocolParams[4] & 0xFF;
            mCommonData.setHour(hour);
            int minute = protocolParams[5] & 0xFF;
            mCommonData.setMinute(minute);
            String time = hour + ":" + minute;
            mCommonData.setTime(time);
            byte startup = (byte) (protocolParams[6] & 0xFF);
            mCommonData.setStartup(SocketSecureKey.Util.startup(startup));
            Tlog.v(TAG, " common timing : " + mCommonData.toString());
        } else if (SocketSecureKey.Util.isAdvanceTiming(protocolParams[0])) {

            mAdvanceData = new TimingAdvanceData();
            mAdvanceData.mac = mSocketDataArray.getID();
            mAdvanceData.model = protocolParams[0];
            mAdvanceData.id = protocolParams[1];
            mAdvanceData.startHour = protocolParams[2] & 0xFF;
            mAdvanceData.startMinute = protocolParams[3] & 0xFF;
            mAdvanceData.endHour = protocolParams[4] & 0xFF;
            mAdvanceData.endMinute = protocolParams[5] & 0xFF;
            mAdvanceData.on = SocketSecureKey.Util.on(protocolParams[6]);
            mAdvanceData.onIntervalHour = protocolParams[7] & 0xFF;
            mAdvanceData.onIntervalMinute = protocolParams[8] & 0xFF;
            mAdvanceData.offIntervalHour = protocolParams[9] & 0xFF;
            mAdvanceData.offIntervalMinute = protocolParams[10] & 0xFF;
            mAdvanceData.startup = SocketSecureKey.Util.startup(protocolParams[11]);
            Tlog.v(TAG, " advance timing : " + mAdvanceData.toString());

        }

        if (mCallBack != null) {
            if (mCommonData != null) {
                mCallBack.onTimingCommonExecuteResult(mSocketDataArray.getID(), mCommonData);
            }
            if (mAdvanceData != null) {
                mCallBack.onTimingAdvanceExecuteResult(mSocketDataArray.getID(), mAdvanceData);
            }
        }
        ResponseData mResponseData = ProtocolDataCache.getTimingExecuteReport(mSocketDataArray.getID(), true, seq);
        response(mResponseData);
    }
}
