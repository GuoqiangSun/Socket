package cn.com.startai.socket.sign.scm.receivetask.impl.report;

import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Temperature;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class TempHumiRelayReportReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TempHumiRelayReportReceiveTask(OnTaskCallBack mCallBack, IDataProtocolOutput mResponse) {
        super(mResponse);
        Tlog.e(TAG, " new TempHumiRelayReportReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();
        byte seq = (byte) mSocketDataArray.getProtocolSequence();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " TempHumiRelayReportReceiveTask params is error ... " + mSocketDataArray.toString());
//            ResponseData mResponseData = ProtocolDataCache.getTempHumidityExecuteReport(mSocketDataArray.getID(), false, seq);
//            response(mResponseData);
            return;
        }


        String mac = mSocketDataArray.getID();
        boolean startup = SocketSecureKey.Util.on(protocolParams[0]);
        boolean on = SocketSecureKey.Util.on(protocolParams[1]);
        byte model = protocolParams[2];
        byte limit = protocolParams[3]; // 上限 下限


        int value_int;
        int value_deci;


        value_int = protocolParams[4];
        value_deci = protocolParams[5] & 0xFF;
        float alarmValue = Float.valueOf(value_int + "." + value_deci);
        alarmValue = (float) (Math.round(alarmValue * 100)) / 100;


        value_int = protocolParams[6];
        value_deci = protocolParams[7] & 0xFF;
        float curValue = Float.valueOf(value_int + "." + value_deci);
        curValue = (float) (Math.round(curValue * 100)) / 100;

        Tlog.v(TAG, " model:" + model + " relayOn:" + on + " startup:" + startup + " limit:" + limit + " alarmValue:" + alarmValue + " curValue:" + curValue);

        if (mCallBack != null) {

            if (model == SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE) {

                mCallBack.onRelayResult(mac, on);

                Temperature mTemperature = new Temperature();
                mTemperature.limit = limit;
                if (SocketSecureKey.Util.isLimitUp(limit)) {
                    mTemperature.hotAlarmSwitch = startup;
                    mTemperature.hotAlarmValue = alarmValue;
                } else {
                    mTemperature.codeAlarmSwitch = startup;
                    mTemperature.codeAlarmValue = alarmValue;
                }
                mTemperature.currentValue = curValue;
                mCallBack.onQueryTemperatureResult(mac, true, mTemperature);

            } else if (model == SocketSecureKey.Model.ALARM_MODEL_HUMIDITY) {

                Tlog.v(TAG, " humidity ");
                mCallBack.onRelayResult(mac, on);

            } else {
                Tlog.e(TAG, " model error ... ");
            }

        }

//        ResponseData mResponseData = ProtocolDataCache.getTempHumidityExecuteReport(mSocketDataArray.getID(), true, seq);
//        response(mResponseData);
    }


}
