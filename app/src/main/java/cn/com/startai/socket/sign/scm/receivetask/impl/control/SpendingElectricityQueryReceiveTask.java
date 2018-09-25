package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SpendingElectricityQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public SpendingElectricityQueryReceiveTask(OnTaskCallBack mCallBack) {
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

        int year = protocolParams[3] & 0xFF;
        mSpendingElectricityData.year = year + 2000;
        mSpendingElectricityData.month = protocolParams[4] & 0xFF;
        mSpendingElectricityData.day = protocolParams[5] & 0xFF;

        mSpendingElectricityData.alarmValue = ((protocolParams[6] & 0xFF) << 8) | (protocolParams[7] & 0xFF);
        mSpendingElectricityData.currentValue = ((protocolParams[8] & 0xFF) << 8) | (protocolParams[9] & 0xFF);

        Tlog.v(TAG, "set SpendingElectricityData: " + result + ";" + mSpendingElectricityData.toJsonObj().toString());

        if (mCallBack != null) {
            mCallBack.onQuerySpendingElectricityResult(mSpendingElectricityData.mac, result, mSpendingElectricityData);
        }


    }

}