package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class TempHumiAlarmSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TempHumiAlarmSetReceiveTask(OnTaskCallBack mCallBack) {
        Tlog.e(TAG, " new TempHumiAlarmSetReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 4) {
            Tlog.e(TAG, " TempHumiAlarmSetReceiveTask params is error ... ");
            return;
        }

        byte model = protocolParams[2];
        byte limit = protocolParams[3];

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        boolean startup = SocketSecureKey.Util.startup(protocolParams[1]);

        String modelStr = SocketSecureKey.Util.isTemperature(model) ? "temperature" : SocketSecureKey.Util.isHumidity(model) ? "humidity" : "unknown";
        Tlog.v(TAG, " result:" + result + " startup:" + startup + " model:" + modelStr + " limit:" + limit);

        if (mCallBack != null) {
            mCallBack.onSetTempHumiAlarmResult(mSocketDataArray.getID(), result, startup, model, limit);
        }

    }

}
