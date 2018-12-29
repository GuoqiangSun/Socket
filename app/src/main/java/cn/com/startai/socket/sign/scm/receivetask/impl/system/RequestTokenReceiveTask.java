package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class RequestTokenReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public RequestTokenReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new RequestTokenReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 9) {
            Tlog.e(TAG, " protocolParams error:" + mSocketDataArray.toString());
            if (mTaskCallBack != null) {
                mTaskCallBack.onRequestTokenResult(false, mSocketDataArray.getID(), -1, -1);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        int random = (protocolParams[1] & 0xFF) << 24 | (protocolParams[2] & 0xFF) << 16 |
                (protocolParams[3] & 0xFF) << 8 | (protocolParams[4] & 0xFF);

        int token = (protocolParams[5] & 0xFF) << 24 | (protocolParams[6] & 0xFF) << 16 |
                (protocolParams[7] & 0xFF) << 8 | (protocolParams[8] & 0xFF);

        String id = mSocketDataArray.getID();

        Tlog.e(TAG, " result :" + result + ":" + protocolParams[0] + " id:" + id + " random : " + random + " token: 0x" + Integer.toHexString(token));


        if (mTaskCallBack != null) {
            mTaskCallBack.onRequestTokenResult(result, id, random, token);
        }

    }
}
