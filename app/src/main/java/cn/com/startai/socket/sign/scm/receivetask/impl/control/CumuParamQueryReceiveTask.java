package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class CumuParamQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CumuParamQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CumuParamQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 13) {
            Tlog.e(TAG, " CumuParamQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        CumuParams cumuParams = new CumuParams();
        cumuParams.mac = mSocketDataArray.getID();
        cumuParams.electricity = (((protocolParams[1] << 24) & 0xFF) | ((protocolParams[2] << 16) & 0xFF)
                | ((protocolParams[3] << 8) & 0xFF) | (protocolParams[4] & 0xFF)) * 1000L;

        cumuParams.time = (((protocolParams[5] << 24) & 0xFF) | ((protocolParams[6] << 16) & 0xFF)
                | ((protocolParams[7] << 8) & 0xFF) | (protocolParams[8] & 0xFF)) * 1000L;

        cumuParams.GHG = (((protocolParams[9] << 24) & 0xFF) | ((protocolParams[10] << 16) & 0xFF)
                | ((protocolParams[11] << 8) & 0xFF) | (protocolParams[12] & 0xFF)) * 1000L;

        Tlog.e(TAG, " CumuParamQueryReceiveTask result:" + result + cumuParams.toString());

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryCumuParamsResult(result, cumuParams);
        }

    }
}
