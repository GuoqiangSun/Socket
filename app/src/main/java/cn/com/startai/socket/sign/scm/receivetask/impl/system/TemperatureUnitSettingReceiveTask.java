package cn.com.startai.socket.sign.scm.receivetask.impl.system;

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
public class TemperatureUnitSettingReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public TemperatureUnitSettingReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new TemperatureUnitSettingReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onSettingTemperatureUnitResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        int mTemperatureUnit = (protocolParams[1] & 0xFF);

        Tlog.v(TAG, "TemperatureUnit result : " + result + " mTemperatureUnit:" + mTemperatureUnit);

        if (mTaskCallBack != null) {
            mTaskCallBack.onSettingTemperatureUnitResult(mSocketDataArray.getID(), result, mTemperatureUnit);
        }

    }
}
