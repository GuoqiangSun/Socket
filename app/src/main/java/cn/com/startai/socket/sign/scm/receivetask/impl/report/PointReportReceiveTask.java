package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.com.startai.socket.sign.scm.bean.PointReport;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

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
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " PointReportReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        PointReport mPointReport = new PointReport();

        byte[] buf2 = new byte[8];
        buf2[0] = 0x00;
        buf2[1] = 0x00;
        buf2[2] = 0x00;
        buf2[3] = 0x00;
        buf2[4] = protocolParams[0];
        buf2[5] = protocolParams[1];
        buf2[6] = protocolParams[2];
        buf2[7] = protocolParams[3];
        long ts = ByteBuffer.wrap(buf2, 0, 8).getLong();

        long tsl = ts * 1000;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String format = dateFormat.format(new Date(tsl));


        int electricityInt = ((protocolParams[4] & 0xFF) << 24)  | ((protocolParams[5] & 0xFF)<< 16)
                | ((protocolParams[6] & 0xFF) << 8) | (protocolParams[7] & 0xFF);

        float electricity = electricityInt / 1000F;

        Tlog.e(TAG, " PointReportReceiveTask ts. " + ts + " " + format
                + " electricityInt:" + electricityInt + " electricity:" + electricity);

        mPointReport.mac = mSocketDataArray.getID();
        mPointReport.ts = tsl;
        mPointReport.electricity = electricity;
        mPointReport.data = new byte[4];
        mPointReport.data[0] = protocolParams[4];
        mPointReport.data[1] = protocolParams[5];
        mPointReport.data[2] = protocolParams[6];
        mPointReport.data[3] = protocolParams[7];

        if (mCallBack != null) {
            mCallBack.onElectricityReportResult(true, mPointReport);
        }

    }
}
