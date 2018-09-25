package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.util.ArrayList;

import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class HistoryCountTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public HistoryCountTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new HistoryCountTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 6) {
            Tlog.e(TAG, " HistoryCountTask error:" + mSocketDataArray.toString());
//            if (mTaskCallBack != null) {
//                mTaskCallBack.onQueryHistoryCountResult(false, null);
//            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        String startTime = String.valueOf(protocolParams[1] + 2000) + "/" + String.valueOf(protocolParams[2]) + "/" + String.valueOf(protocolParams[3]);

        int day = protocolParams[4];

        Tlog.e(TAG, " HistoryCountTask result:" + result + " startTime:" + startTime + " day:" + day);

        int oneLength = 8;
        byte[] countData = new byte[oneLength];
        int mo = (protocolParams.length - 1 - 5) / oneLength;


        QueryHistoryCount mCount = new QueryHistoryCount();
        mCount.mac = mSocketDataArray.getID();
        mCount.startTime = startTime;
        mCount.day = day;
        mCount.mDataArray = new ArrayList<>(mo);
        QueryHistoryCount.Data mData;

        for (int i = 0; i < mo; i++) {
            System.arraycopy(protocolParams, i * oneLength + 5, countData, 0, oneLength);
            int e = (countData[0] & 0xFF) << 24 | (countData[1] & 0xFF) << 16 | (countData[2] & 0xFF) << 8 | (countData[3] & 0xFF);
            int s = (countData[4] & 0xFF) << 24 | (countData[5] & 0xFF) << 16 | (countData[6] & 0xFF) << 8 | (countData[7] & 0xFF);
            Tlog.d(TAG, " HistoryCountTask e:" + e + " s:" + s);
            mData = new QueryHistoryCount.Data();
            mData.e = e;
            mData.s = s;
            mCount.mDataArray.add(mData);
        }

        mCount.interval = protocolParams[protocolParams.length - 1];

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryHistoryCountResult(result, mCount);
        }

    }
}
