package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

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
        cumuParams.electricity = (((protocolParams[1] & 0xFF) << 24) | ((protocolParams[2] & 0xFF) << 16)
                | ((protocolParams[3] & 0xFF) << 8) | (protocolParams[4] & 0xFF)) * 1000L;

        cumuParams.time = (((protocolParams[5] & 0xFF) << 24) | ((protocolParams[6] & 0xFF) << 16)
                | ((protocolParams[7] & 0xFF) << 8) | (protocolParams[8] & 0xFF)) * 1000L;

        cumuParams.GHG = (((protocolParams[9] & 0xFF) << 24) | ((protocolParams[10] & 0xFF) << 16)
                | ((protocolParams[11] & 0xFF) << 8) | (protocolParams[12] & 0xFF)) * 1000L;

        Tlog.e(TAG, " CumuParamQueryReceiveTask result:" + result + cumuParams.toString());

        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryCumuParamsResult(result, cumuParams);
        }

    }
}
