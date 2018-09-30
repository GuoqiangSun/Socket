package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
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
public class UpdateReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public UpdateReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new UpdateReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 6) {
            Tlog.e(TAG, " UpdateReceiveTask error:" + mSocketDataArray.toString());
            if (mTaskCallBack != null) {
                mTaskCallBack.onUpdateVersionResult(false, null);
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        UpdateVersion mVersion = new UpdateVersion();
        mVersion.mac = mSocketDataArray.getID();
        mVersion.action = protocolParams[1];
        mVersion.curVersion = ((protocolParams[2] & 0xFF) << 8 | (protocolParams[3] & 0xFF));
        mVersion.newVersion = ((protocolParams[4] & 0xFF) << 8 | (protocolParams[5] & 0xFF));

        Tlog.e(TAG, " UpdateReceiveTask result:" + result + " params:" + mVersion.toString());

        if (mTaskCallBack != null) {
            mTaskCallBack.onUpdateVersionResult(result, mVersion);
        }

    }
}
