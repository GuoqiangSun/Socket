package cn.com.startai.socket.sign.scm.receivetask.impl.setting;

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
public class TemperatureUnitQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public TemperatureUnitQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new TemperatureUnitQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onQueryTemperatureUnitResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        int mTemperatureUnit = (protocolParams[1] & 0xFF);

        Tlog.v(TAG, "TemperatureQuery result : " + result + " mTemperatureUnit:" + mTemperatureUnit);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryTemperatureUnitResult(mSocketDataArray.getID(), result, mTemperatureUnit);
        }

    }
}
