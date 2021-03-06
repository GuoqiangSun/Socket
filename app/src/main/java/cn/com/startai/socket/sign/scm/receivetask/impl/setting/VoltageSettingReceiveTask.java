package cn.com.startai.socket.sign.scm.receivetask.impl.setting;

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
public class VoltageSettingReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public VoltageSettingReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new VoltageSettingReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 1) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onSettingVoltageResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        int mAlarmVoltageValue = 0;
        if (protocolParams.length > 3) {
            mAlarmVoltageValue = (protocolParams[1] & 0xFF) << 8 | (protocolParams[2] & 0xFF);
        }

        Tlog.v(TAG, "Heartbeat result : " + result + " mAlarmVoltageValue:" + mAlarmVoltageValue);

        if (mTaskCallBack != null) {
            mTaskCallBack.onSettingVoltageResult(mSocketDataArray.getID(), result, mAlarmVoltageValue);
        }

    }
}
