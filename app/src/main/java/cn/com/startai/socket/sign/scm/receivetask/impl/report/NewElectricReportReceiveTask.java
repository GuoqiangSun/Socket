package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class NewElectricReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public NewElectricReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        Tlog.e(TAG, " new NewElectricReportReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 16) {
            Tlog.e(TAG, " NewElectricReportReceiveTask params is error ... " + mSocketDataArray.toString());
//            ResponseData mResponseData = ProtocolDataCache.getElectricValueReport(mSocketDataArray.getID(), false, seq);
//            response(mResponseData);
            return;
        }

        int relpower = (protocolParams[0] & 0xFF) << 8 | (protocolParams[1] & 0xFF);
        int avepower = (protocolParams[2] & 0xFF) << 8 | (protocolParams[3] & 0xFF);
        int maxpower = (protocolParams[4] & 0xFF) << 8 | (protocolParams[5] & 0xFF);

        float freq = ((protocolParams[6] & 0xFF) << 8 | (protocolParams[7] & 0xFF)) / 100F;

        float voltage = ((protocolParams[8] & 0xFF) << 8 | (protocolParams[9] & 0xFF)) / 100F;

        float current = ((protocolParams[10] & 0xFF) << 8 | (protocolParams[11] & 0xFF)) / 1000F;

        float maxCurrent = ((protocolParams[12] & 0xFF) << 8 | (protocolParams[13] & 0xFF)) / 1000F;
        float powerFactory = ((protocolParams[14] & 0xFF) << 8 | (protocolParams[15] & 0xFF)) / 1000F;

        Tlog.v(TAG, "relpower: " + relpower + " avepower: " + avepower
                + " maxpower: " + maxpower + " voltage: " + voltage
                + " current: " + current + " maxCurrent:" + maxCurrent + " powerFactory:" + powerFactory);

        if (mCallBack != null) {
            mCallBack.onNewElectricResult(mSocketDataArray.getID(), relpower, avepower, maxpower,freq, voltage, current, maxCurrent, powerFactory);
        }
//        ResponseData mResponseData = ProtocolDataCache.getElectricValueReport(mSocketDataArray.getID(), true, seq);
//        response(mResponseData);
    }

}
