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
public class SensorStatusQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public SensorStatusQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new SensorStatusQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 3) {
            Tlog.e(TAG, " CostRateQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        byte sensor = protocolParams[1];
        boolean status = SocketSecureKey.Util.isRunning(protocolParams[2]);

        Tlog.e(TAG, " CostRateQueryReceiveTask result:" + result + " sensor:" + sensor + " status:" + status);

        if (mTaskCallBack != null) {
            if (SocketSecureKey.Util.isTempSensor(sensor)) {
                mTaskCallBack.onQueryTempSensorResult(result, mSocketDataArray.getID(), status);

//                mTaskCallBack.onReportTempSensorResult(mSocketDataArray.getID(), false);

            } else if (SocketSecureKey.Util.isBleDevice(sensor)) {

                mTaskCallBack.onQueryBleDeviceSensorResult(result, mSocketDataArray.getID(), status);

            } else if (SocketSecureKey.Util.isElectricQuantity(sensor)) {

                mTaskCallBack.onQueryTElectricQuantitySensorResult(result, mSocketDataArray.getID(), status);
            }

        }

    }
}
