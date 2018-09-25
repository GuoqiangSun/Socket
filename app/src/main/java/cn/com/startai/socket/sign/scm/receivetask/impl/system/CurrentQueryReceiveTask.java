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
public class CurrentQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CurrentQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CurrentQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onQueryCurrentResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        float mAlarmCurrentValue = ((protocolParams[1] & 0xFF) << 8 | (protocolParams[2] & 0xFF)) / 100F;

        Tlog.v(TAG, "CurrentQuery result : " + result + " mAlarmCurrentValue:" + mAlarmCurrentValue);

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryCurrentResult(mSocketDataArray.getID(), result, mAlarmCurrentValue);
        }

    }
}
