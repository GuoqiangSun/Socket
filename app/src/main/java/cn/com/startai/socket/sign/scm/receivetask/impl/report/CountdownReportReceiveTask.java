package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.bean.CountdownData;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class CountdownReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public CountdownReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new CountdownReportReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 4) {
            Tlog.e(TAG, " CountdownReportReceiveTask params is error ... " + mSocketDataArray.toString());
//            ResponseData mResponseData = ProtocolDataCache.getCountdownExecuteReport(mSocketDataArray.getID(), false, seq);
//            response(mResponseData);
            return;
        }

        boolean startup = SocketSecureKey.Util.startup(protocolParams[0]);// 启动or结束
        boolean on = SocketSecureKey.Util.on(protocolParams[1]);// 开启or关闭
        int hour = (protocolParams[2] & 0xFF);
        int minute = (protocolParams[3] & 0xFF);

        CountdownData mCountdownData = new CountdownData();
        mCountdownData.mac = mSocketDataArray.getID();
        mCountdownData.Switchgear = on;
        mCountdownData.countdownSwitch = startup;
        mCountdownData.hour = hour;
        mCountdownData.minute = minute;

        if (protocolParams.length >= 6) {
            mCountdownData.allTime = (protocolParams[4] & 0xFF) * 60;
            mCountdownData.allTime += protocolParams[5] & 0xFF;
            if (protocolParams.length >= 7) {
                mCountdownData.seconds = (protocolParams[6] & 0xFF);
            }
        }

        Tlog.v(TAG, " CountdownReportReceiveTask  :" + String.valueOf(mCountdownData));
//        mCountdownData.checkAllTime();

//         protocolParams[0]  == 1 启动    ==2 结束
//         protocolParams[1]  == 0 关机   ==1 开机
        if (mCallBack != null) {
            mCallBack.onCountdownReportResult(mSocketDataArray.getID(), mCountdownData);
        }
//        ResponseData mResponseData = ProtocolDataCache.getCountdownExecuteReport(mSocketDataArray.getID(), true, seq);
//        response(mResponseData);
    }
}
