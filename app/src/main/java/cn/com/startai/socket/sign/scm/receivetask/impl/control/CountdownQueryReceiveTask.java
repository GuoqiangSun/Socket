package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.sign.scm.bean.CountdownData;
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

public class CountdownQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public CountdownQueryReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new CountdownQueryReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 5) {
            Tlog.e(TAG, " CountdownQueryReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        boolean startup = SocketSecureKey.Util.startup(protocolParams[1]);// 启动or结束
        boolean on = SocketSecureKey.Util.on(protocolParams[2]);// 开启or关闭
        int hour = (protocolParams[3] & 0xFF);
        int minute = (protocolParams[4] & 0xFF);

        Tlog.v(TAG, " CountdownQueryReceiveTask result: " + result + " startup:" + startup + " on:" + on + " hour: " + hour + " minute: " + minute);

//         protocolParams[0]  == 0 success  ==1 params fail
//         protocolParams[1]  == 1 启动    ==2 结束
//         protocolParams[1]  == 0 关机   ==1 开机

        CountdownData mCountdownData = new CountdownData();
        mCountdownData.mac = mSocketDataArray.getID();
        mCountdownData.Switchgear = on;
        mCountdownData.countdownSwitch = startup;
        mCountdownData.hour = hour;
        mCountdownData.minute = minute;

        if (protocolParams.length >= 7) {
            mCountdownData.allTime = (protocolParams[5] & 0xFF) * 60;
            mCountdownData.allTime += protocolParams[6] & 0xFF;
        }

//        mCountdownData.checkAllTime();

        if (mCallBack != null) {
            mCallBack.onQueryCountdownResult(mSocketDataArray.getID(), result, mCountdownData);

            IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
            if (iDebugerStream != null) {
                iDebugerStream.receiveCountdown(mSocketDataArray.getObj(), mSocketDataArray.getID());
            }

        }

    }
}
