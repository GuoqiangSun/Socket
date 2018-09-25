package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.ProtocolDataCache;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class ElectricReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public ElectricReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        Tlog.e(TAG, " new ElectricReportReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getSeq();

        if (protocolParams == null || protocolParams.length < 13) {
            Tlog.e(TAG, " ElectricReportReceiveTask params is error ... " + mSocketDataArray.toString());
            ResponseData mResponseData = ProtocolDataCache.getElectricValueReport(mSocketDataArray.getID(), false, seq);
            response(mResponseData);
            return;
        }

        int power = (protocolParams[0] & 0xFF) << 8 | (protocolParams[1] & 0xFF);
        int avepower = (protocolParams[2] & 0xFF) << 8 | (protocolParams[3] & 0xFF);
        int maxpower = (protocolParams[4] & 0xFF) << 8 | (protocolParams[5] & 0xFF);

        int freq_int = protocolParams[6] & 0xFF;
        int freq_deci = protocolParams[7] & 0xFF;
        float freq = Float.valueOf(freq_int + "." + freq_deci);
        freq = (float) (Math.round(freq * 100)) / 100;


        int voltage_int = (protocolParams[8] & 0xFF) << 8 | (protocolParams[9] & 0xFF);
        int voltage_deci = protocolParams[10] & 0xFF;
        float voltage = Float.valueOf(voltage_int + "." + voltage_deci);
        voltage = (float) (Math.round(voltage * 100)) / 100;

        int current_int = protocolParams[11] & 0xFF;
        int current_deci = protocolParams[12] & 0xFF;
        float current = Float.valueOf(current_int + "." + current_deci);
        current = (float) (Math.round(current * 100)) / 100;

        Tlog.v(TAG, "power: " + power + " avepower: " + avepower + " maxpower: " + maxpower + " freq: " + freq + " voltage: " + voltage + " freq: " + current);

        if (mCallBack != null) {
            mCallBack.onElectricResult(mSocketDataArray.getID(), power, avepower, maxpower, freq, voltage, current);
        }
        ResponseData mResponseData = ProtocolDataCache.getElectricValueReport(mSocketDataArray.getID(), true, seq);
        response(mResponseData);
    }

}
