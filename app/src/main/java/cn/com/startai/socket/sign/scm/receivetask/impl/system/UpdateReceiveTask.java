package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
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

        mVersion.curVersionMain = protocolParams[2];
        mVersion.curVersionSub = protocolParams[3];

        mVersion.newVersionMain = protocolParams[4];
        mVersion.newVersionSub = protocolParams[5];

        mVersion.curVersion = ((protocolParams[2] & 0xFF) << 8 | (protocolParams[3] & 0xFF));
        mVersion.newVersion = ((protocolParams[4] & 0xFF) << 8 | (protocolParams[5] & 0xFF));

        if (SocketSecureKey.Util.isUpdateModelOnly(protocolParams[1])) {

            if (mVersion.newVersion > mVersion.curVersion) {
                // 固件升级完,当前版本还是旧的,所有转换下
                mVersion.curVersion = mVersion.newVersion;
                mVersion.curVersionMain = mVersion.newVersionMain;
                mVersion.curVersionSub = mVersion.newVersionSub;
            }

        }


        if (protocolParams.length > 6) {
            mVersion.progress = protocolParams[6];
        }

        Tlog.e(TAG, " UpdateReceiveTask result:" + result + " params:" + String.valueOf(mVersion));

        if (mTaskCallBack != null) {
            mTaskCallBack.onUpdateVersionResult(result, mVersion);
        }

    }
}
