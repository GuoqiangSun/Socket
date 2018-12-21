package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Humidity;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Temperature;
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

public class TempHumiQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TempHumiQueryReceiveTask(OnTaskCallBack mCallBack) {
        Tlog.e(TAG, " new TempHumiQueryReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 8) {
            Tlog.e(TAG, " TempHumiQueryReceiveTask params is error ... ");
            return;
        }

        String mac = mSocketDataArray.getID();
        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        boolean startup = SocketSecureKey.Util.on(protocolParams[1]);
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

        Tlog.v(TAG, " result:" + result
                + " startup:" + startup
                + " alarmValue:" + alarmValue
                + " curValue:" + curValue
                + " limit:" + limit);

        if (mCallBack != null) {

            if (model == SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE) {
                Tlog.v(TAG, " temperature ");

                final Temperature mTemperature = new Temperature();

                mTemperature.limit = limit;

                if (SocketSecureKey.Util.isLimitUp(limit)) {
                    mTemperature.hotAlarmSwitch = startup;
                    mTemperature.hotAlarmValue = alarmValue;
                } else {
                    mTemperature.codeAlarmSwitch = startup;
                    mTemperature.codeAlarmValue = alarmValue;
                }

                mTemperature.currentValue = curValue;

                mCallBack.onQueryTemperatureResult(mac, result, mTemperature);

            } else if (model == SocketSecureKey.Model.ALARM_MODEL_HUMIDITY) {

                Tlog.v(TAG, " humidity ");

                Humidity mHumidity = new Humidity();
                mHumidity.limit = limit;

                if (SocketSecureKey.Util.isLimitUp(limit)) {
                    mHumidity.hotAlarmSwitch = startup;
                    mHumidity.hotAlarmValue = alarmValue;
                } else {
                    mHumidity.codeAlarmSwitch = startup;
                    mHumidity.codeAlarmValue = alarmValue;
                }

                mHumidity.currentValue = curValue;

                mCallBack.onQueryHumidityResult(mac, result, mHumidity);

            } else {
                Tlog.e(TAG, " model error ... ");
            }

        }

    }

}
