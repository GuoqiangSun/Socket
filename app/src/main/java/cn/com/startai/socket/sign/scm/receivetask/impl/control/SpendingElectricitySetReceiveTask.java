package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SpendingElectricitySetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public SpendingElectricitySetReceiveTask(OnTaskCallBack mCallBack) {
        Tlog.e(TAG, " new SpendingElectricityQueryReceiveTask() ");
        this.mCallBack = mCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 10) {
            Tlog.e(TAG, " SpendingElectricityQueryReceiveTask params is error ... ");
            return;
        }

        SpendingElectricityData mSpendingElectricityData = new SpendingElectricityData();
        mSpendingElectricityData.mac = mSocketDataArray.getID();

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        mSpendingElectricityData.alarmSwitch = SocketSecureKey.Util.on(protocolParams[1]);
        // 定电量 定花费
        mSpendingElectricityData.model = protocolParams[2];

        mSpendingElectricityData.year = protocolParams[3] & 0xFF;
        mSpendingElectricityData.month = protocolParams[4] & 0xFF;
        mSpendingElectricityData.day = protocolParams[5] & 0xFF;

        if (protocolParams.length <= 10) {
            mSpendingElectricityData.alarmValue = ((protocolParams[6] << 8) & 0xFF) | (protocolParams[7] & 0xFF);
            mSpendingElectricityData.currentValue = ((protocolParams[8] << 8) & 0xFF) | (protocolParams[9] & 0xFF);

        } else if (protocolParams.length <= 14) {
            mSpendingElectricityData.alarmValue = (((protocolParams[6] << 24) & 0xFF) | ((protocolParams[7] << 16) & 0xFF)
                    | ((protocolParams[8] << 8) & 0xFF) | (protocolParams[9] & 0xFF));
            mSpendingElectricityData.currentValue = (((protocolParams[10] << 24) & 0xFF) | ((protocolParams[11] << 16) & 0xFF)
                    | ((protocolParams[12] << 8) & 0xFF) | (protocolParams[13] & 0xFF));
        }


        Tlog.v(TAG, "query SpendingElectricityData: " + result + ";" + mSpendingElectricityData.toJsonObj().toString());

        if (mCallBack != null) {
            mCallBack.onSetSpendingElectricityResult(mSpendingElectricityData.mac, result, mSpendingElectricityData);
        }


    }

}
