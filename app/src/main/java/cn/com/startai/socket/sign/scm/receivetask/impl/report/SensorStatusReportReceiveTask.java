package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class SensorStatusReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public SensorStatusReportReceiveTask(OnTaskCallBack mTaskCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        Tlog.e(TAG, " new SensorStatusReportReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " SensorStatusReportReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        byte sensor = protocolParams[0];
        boolean status = SocketSecureKey.Util.isRunning(protocolParams[1]);

        Tlog.e(TAG, " SensorStatusReportReceiveTask  sensor:" + sensor + " status:" + status);

        if (mTaskCallBack != null) {
            if (SocketSecureKey.Util.isTempSensor(sensor)) {
                mTaskCallBack.onReportTempSensorResult(mSocketDataArray.getID(), status);
            }else if (SocketSecureKey.Util.isBleDevice(sensor)) {

                mTaskCallBack.onQueryBleDeviceSensorResult(true, mSocketDataArray.getID(), status);

            } else if (SocketSecureKey.Util.isElectricQuantity(sensor)) {

                mTaskCallBack.onQueryTElectricQuantitySensorResult(true, mSocketDataArray.getID(), status);
            }
        }

    }
}
