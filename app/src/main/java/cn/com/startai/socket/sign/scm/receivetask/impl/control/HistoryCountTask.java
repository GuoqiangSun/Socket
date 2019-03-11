package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class HistoryCountTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

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

        String startTime = String.valueOf(protocolParams[1] + 2000)
                + "/" + String.valueOf(protocolParams[2])
                + "/" + String.valueOf(protocolParams[3]);

        int day = protocolParams[4];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        long startTimeMillis;

        try {
            startTimeMillis= dateFormat.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            startTimeMillis = DateUtils.fastFormatTsToDayOfOffset(day-1);
        }


        int oneLength = 8; // 一组数据大小
        int dataLength = (protocolParams.length - 1 - 5);//数据大小长度
        final byte[] countData = new byte[oneLength];

        QueryHistoryCount mCount = new QueryHistoryCount();
        mCount.mac = mSocketDataArray.getID();
        mCount.startTime = startTime;
        mCount.startTimeMillis = startTimeMillis;

        mCount.day = day;
        mCount.mDayArray = new ArrayList<>(day);


        int mo = dataLength / oneLength; // 多少个数据
        mCount.mDataArray = new ArrayList<>(mo);


        QueryHistoryCount.Data mData;
        QueryHistoryCount.Day mDay;

        int oneDaySize = 60 / 5 * 24; //一天数据个数
        int oneDayBytes = oneDaySize * oneLength; // 一天数据长度

        int oneDayRemainBytes = dataLength % oneDayBytes;// 剩下多少数据
        if (Debuger.isLogDebug) {
            Tlog.w(TAG, " HistoryCountTask result:" + result + " startTime:" + startTime
                    + " startTimeMillis:" + startTimeMillis + " " + dateFormat.format(new Date(startTimeMillis))
                    + "\n day:" + day
                    + " dataLength:" + dataLength + " oneDaySize:" + oneDaySize
                    + " oneDayBytes:" + oneDayBytes + " oneDayRemainBytes:" + oneDayRemainBytes
            );
        }

        StringBuilder sbLog = new StringBuilder();

        for (int k = 0; k < day; k++) {

            mDay = new QueryHistoryCount.Day();

            byte[] oneDayData = new byte[oneDayBytes];
            if (k == day - 1 && oneDayRemainBytes != 0) {
                System.arraycopy(protocolParams, k * oneDayBytes + 5, oneDayData, 0, oneDayRemainBytes);
            } else {
                System.arraycopy(protocolParams, k * oneDayBytes + 5, oneDayData, 0, oneDayBytes);
            }

            mDay.countData = oneDayData;
            mDay.startTime = startTimeMillis + k * ONE_DAY;

            mCount.mDayArray.add(mDay);

            String format = dateFormat.format(mDay.startTime);
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, " HistoryCountTask  startTime:" + startTime + " " + format);
            }

            for (int j = 0; j < oneDaySize; j++) {

                System.arraycopy(oneDayData, j * oneLength, countData, 0, oneLength);

                int e = (countData[0] & 0xFF) << 24 | (countData[1] & 0xFF) << 16
                        | (countData[2] & 0xFF) << 8 | (countData[3] & 0xFF);

                int s = (countData[4] & 0xFF) << 24 | (countData[5] & 0xFF) << 16
                        | (countData[6] & 0xFF) << 8 | (countData[7] & 0xFF);

                mData = new QueryHistoryCount.Data();

                mData.e = e /1000F;
                mData.s = s /1000F;

                if (Debuger.isLogDebug) {
                    sbLog.append(" ").append(j).append(". e:").append(e).append(" s:").append(s);
                }

                mCount.mDataArray.add(mData);
            }

        }

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " HistoryCountTask:" + sbLog.toString());
        }

        mCount.interval = protocolParams[protocolParams.length - 1];
        mCount.msgSeq = mSocketDataArray.getProtocolSequence();

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryHistoryCountResult(result, mCount);
        }

    }
}
