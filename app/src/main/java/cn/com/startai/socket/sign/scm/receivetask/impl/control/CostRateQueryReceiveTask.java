package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.CostRate;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class CostRateQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CostRateQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CostRateQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 9) {
            Tlog.e(TAG, " CostRateQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }
        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        CostRate mCostRate = new CostRate();
        mCostRate.mac = mSocketDataArray.getID();
        mCostRate.hour1 = protocolParams[1];
        mCostRate.minute1 = protocolParams[2];
        mCostRate.price1 = (((protocolParams[3] << 8) & 0xFF) | (protocolParams[4] & 0xFF)) / 1000F;
        mCostRate.hour2 = protocolParams[5];
        mCostRate.minute2 = protocolParams[6];
        mCostRate.price2 = (((protocolParams[7] << 8) & 0xFF) | (protocolParams[8] & 0xFF)) / 1000F;

        Tlog.e(TAG, " CostRateQueryReceiveTask result:" + result + String.valueOf(mCostRate));

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryCostRateResult(result, mCostRate);
        }

    }
}
