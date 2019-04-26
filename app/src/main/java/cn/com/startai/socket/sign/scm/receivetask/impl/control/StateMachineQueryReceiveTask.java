package cn.com.startai.socket.sign.scm.receivetask.impl.control;

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
public class StateMachineQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public StateMachineQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new StateMachineQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 9) {
            Tlog.e(TAG, " StateMachineQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }
        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

//        status[0].bit0~bit7:继电器开关状态、彩灯开关状态、睡眠开关状态、USB开关状态、指示灯开关状态
//        status[1].bit0~bit7:定时彩灯任务状态、定时睡眠灯任务状态、
//        status[2].bit0~bit7:定时任务状态、定时高级设置状态、倒计时任务状态、USB定时任务状态
//        status[3].bit0~bit7:温度任务状态、温感控头状态、湿度任务状态
//        status[4].bit0~bit7:定量任务任务状态、定费任务任务状态

        Tlog.e(TAG, " StateMachineQueryReceiveTask result:" + result);

        if (mTaskCallBack != null) {
        }

    }
}
