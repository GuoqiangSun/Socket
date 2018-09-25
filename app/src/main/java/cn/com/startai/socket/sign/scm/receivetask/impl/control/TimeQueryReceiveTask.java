package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.util.Calendar;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class TimeQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimeQueryReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimeQueryReceiveTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " TimeQueryReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        int year = protocolParams[1] + 2000;
        byte week = protocolParams[2];
        byte month = protocolParams[3];
        byte day = protocolParams[4];
        byte hour = protocolParams[5];
        byte minute = protocolParams[6];
        byte second = protocolParams[7];

        Tlog.v(TAG, " relay result: " + result + " year:" + year + " month:" + month + " day:" + day + "  week:" + week + " hour:" + hour + " minute:" + minute + " second:" + second);

        String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        Tlog.v(TAG, "scm time:" + time);

        Calendar instance = Calendar.getInstance();
        instance.set(year, month, day, hour, minute, second);
        long millis = instance.getTime().getTime();

        if (mCallBack != null) {
            mCallBack.onScmTimeResult(mSocketDataArray.getID(), result, millis);

            IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();

            if (iDebugerStream != null) {
                iDebugerStream.receiveQueryTime(mSocketDataArray.getObj(), mSocketDataArray.getID());
            }

        }

    }
}
