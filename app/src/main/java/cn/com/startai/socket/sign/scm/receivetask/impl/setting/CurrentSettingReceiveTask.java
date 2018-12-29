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
public class CurrentSettingReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CurrentSettingReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CurrentSettingReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 1) {
            Tlog.e(TAG, " protocolParams == null");
            if (mTaskCallBack != null) {
                mTaskCallBack.onSettingCurrentResult(mSocketDataArray.getID(), false, 0);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        float mAlarmCurrentValue = 0F;
        if (protocolParams.length >= 3) {
            mAlarmCurrentValue = ((protocolParams[1] & 0xFF) << 8 | (protocolParams[2] & 0xFF)) / 100F;
        }
        Tlog.v(TAG, "CurrentSetting result : " + result + " mAlarmCurrentValue:" + mAlarmCurrentValue);

        if (mTaskCallBack != null) {
            mTaskCallBack.onSettingCurrentResult(mSocketDataArray.getID(), result, mAlarmCurrentValue);
        }

    }
}
