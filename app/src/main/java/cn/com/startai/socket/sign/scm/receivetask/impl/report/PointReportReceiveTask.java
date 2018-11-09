package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
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
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " PointReportReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String format = dateFormat.format(new Date(tsl));


        int electricityInt = ((protocolParams[4] << 24) & 0xFF) | ((protocolParams[5] << 16) & 0xFF)
                | ((protocolParams[6] << 8) & 0xFF) | (protocolParams[7] & 0xFF);

        float electricity = electricityInt / 1000;

        Tlog.e(TAG, " PointReportReceiveTask ts. " + ts + " " + format + " electricityInt:" + electricityInt);

        QueryHistoryCount mCount = new QueryHistoryCount();
        mCount.mac = mSocketDataArray.getID();
        mCount.startTime = format;
        mCount.day = 1;
        mCount.mDataArray = new ArrayList<>(1);
        QueryHistoryCount.Data mData = new QueryHistoryCount.Data();
        mData.e = electricity;
        mData.s = 0f;
        mCount.mDataArray.add(mData);
        mCount.interval = 1;

        if (mCallBack != null) {
            mCallBack.onQueryHistoryCountResult(true, mCount);
        }

    }
}
