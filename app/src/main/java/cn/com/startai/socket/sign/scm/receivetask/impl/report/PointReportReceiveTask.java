package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class PointReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public PointReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new PointReportReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getSeq();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " PointReportReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        int ts = ((protocolParams[0] << 24) & 0xFF) | ((protocolParams[1] << 16) & 0xFF)
                | ((protocolParams[2] << 8) & 0xFF) | (protocolParams[3] & 0xFF);

        int electricityInt = ((protocolParams[4] << 24) & 0xFF) | ((protocolParams[5] << 16) & 0xFF)
                | ((protocolParams[6] << 8) & 0xFF) | (protocolParams[7] & 0xFF);

        float electricity = electricityInt / 1000;

        Tlog.e(TAG, " PointReportReceiveTask ts. " + ts + " electricityInt:" + electricityInt);
    }
}
