package cn.com.startai.socket.sign.scm.receivetask.impl.setting;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class MonetaryUnitSettingReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public MonetaryUnitSettingReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new MonetaryUnitSettingReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onSettingMonetaryUnitResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        int mMonetaryUnit = (protocolParams[1] & 0xFF);

        Tlog.v(TAG, "MonetaryUnit result : " + result + " mMonetaryUnit:" + mMonetaryUnit);

        if (mTaskCallBack != null) {
            mTaskCallBack.onSettingMonetaryUnitResult(mSocketDataArray.getID(), result, mMonetaryUnit);
        }

    }
}
