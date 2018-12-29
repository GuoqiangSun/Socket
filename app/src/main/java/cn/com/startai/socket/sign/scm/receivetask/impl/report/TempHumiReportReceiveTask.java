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

public class TempHumiReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TempHumiReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        Tlog.e(TAG, " new TempHumiReportReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 4) {
            Tlog.e(TAG, " TempHumiReportReceiveTask params is error ... ");
//            ResponseData mResponseData = ProtocolDataCache.getTempHumiValueReport(mSocketDataArray.getID(),
//                    false, seq);
//            response(mResponseData);
            return;
        }

        int temp_int = protocolParams[0];
        int temp_deci = protocolParams[1] & 0xFF;
        float tempF = Float.valueOf(temp_int + "." + temp_deci);
        float temp = (float) (Math.round(tempF * 100)) / 100;

        int humi_int = protocolParams[2];
        int humi_deci = protocolParams[3] & 0xFF;
        float humiF = Float.valueOf(humi_int + "." + humi_deci);
        float humi = (float) (Math.round(humiF * 100)) / 100;

        Tlog.v(TAG, "tempF: " + tempF + " temp: " + temp + " humiF: " + humiF + " humi: " + humi);

        if (mCallBack != null) {
            mCallBack.onTempHumiResult(mSocketDataArray.getID(), temp, humi);
        }

//        ResponseData mResponseData = ProtocolDataCache.getTempHumiValueReport(mSocketDataArray.getID(), true, seq);
//        response(mResponseData);
    }

}
