package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.startai.socket.mutual.js.bean.CountElectricity;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class NewHistoryCountTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public NewHistoryCountTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new NewHistoryCountTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 6) {
            Tlog.e(TAG, " NewHistoryCountTask error:" + mSocketDataArray.toString());
//            if (mTaskCallBack != null) {
//                mTaskCallBack.onQueryHistoryCountResult(false, null);
//            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        String startTime = String.valueOf(protocolParams[1] + 2000)
                + "/" + String.valueOf(protocolParams[2])
                + "/" + String.valueOf(protocolParams[3]);

        int day = protocolParams[4] & 0xFF;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        long startTimeMillis;

        try {
            startTimeMillis = dateFormat.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            startTimeMillis = DateUtils.fastFormatTsToDayOfOffset(day - 1);
        }


        int oneLength = CountElectricity.NEW_ONE_PKG_LENGTH; // 一组数据大小
        int dataLength = (protocolParams.length - 1 - 5);//数据大小长度
        final byte[] countData = new byte[oneLength];

        QueryHistoryCount mCount = new QueryHistoryCount();
        mCount.mac = mSocketDataArray.getID();
        mCount.startTime = startTime;
        mCount.startTimeMillis = startTimeMillis;
        byte interval = protocolParams[protocolParams.length - 1];
        mCount.interval = interval;
//        mCount.day = day;
        mCount.mDayArray = new ArrayList<>(day);


        int mo = dataLength / oneLength; // 多少个数据
        mCount.mDataArray = new ArrayList<>(mo);


        QueryHistoryCount.Data mData;
        QueryHistoryCount.Day mDay;


        int oneDaySize; //一天数据个数
        int oneDayBytes; // 一天数据长度

        if (SocketSecureKey.Util.isIntervalMinute(interval)) {

            oneDaySize = CountElectricity.SIZE_ONE_DAY;
            oneDayBytes = CountElectricity.NEW_ONE_DAY_BYTES;

        } else if (SocketSecureKey.Util.isIntervalHour(interval)) {

            oneDaySize = 24;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH * 24;

        } else if (SocketSecureKey.Util.isIntervalDay(interval)) {

            oneDaySize = 1;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH;

        } else if (SocketSecureKey.Util.isIntervalWeek(interval)) {

            oneDaySize = 1;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH;

        } else if(SocketSecureKey.Util.isIntervalMonth(interval)){

            oneDaySize = 1;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH;

        }else if (SocketSecureKey.Util.isIntervalYear(interval)) {

            oneDaySize = 1;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH;

        } else {
            oneDaySize = 1;
            oneDayBytes = CountElectricity.NEW_ONE_PKG_LENGTH;
        }


        mCount.complete = dataLength >= oneDayBytes;

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

                mData = new QueryHistoryCount.Data();

                mData.e = e / 1000F;

                if (Debuger.isLogDebug) {
                    sbLog.append(" ").append(j).append(". e:").append(e);
                }

                mCount.mDataArray.add(mData);
                mCount.day++;

            }

        }

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " HistoryCountTask:" + sbLog.toString());
        }


        mCount.msgSeq = mSocketDataArray.getProtocolSequence();

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryNewHistoryCountResult(result, mCount);

            IDebugerProtocolStream iDebugerStream = mTaskCallBack.getIDebugerStream();

            if (iDebugerStream != null) {
                iDebugerStream.receiveHistory(mSocketDataArray.getObj(), result);
            }
        }

    }
}
