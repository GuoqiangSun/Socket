package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class PowerQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public PowerQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new PowerSettingReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onQueryPowerResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        int mAlarmPowerValue = (protocolParams[1] & 0xFF) << 8 | (protocolParams[2] & 0xFF);

        Tlog.v(TAG, "PowerQuery result : " + result + " mAlarmPowerValue:" + mAlarmPowerValue);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryPowerResult(mSocketDataArray.getID(), result, mAlarmPowerValue);
        }

    }
}
