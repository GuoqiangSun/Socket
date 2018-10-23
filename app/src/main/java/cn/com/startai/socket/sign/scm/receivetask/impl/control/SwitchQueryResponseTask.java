package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.MySocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class SwitchQueryResponseTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public SwitchQueryResponseTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new SwitchQueryResponseTask() ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " SwitchQueryResponseTask params is error ... ");
            return;
        }

//        result==0 ? success :fail;

        boolean on = SocketSecureKey.Util.on(protocolParams[1]);


        byte model = SocketSecureKey.Model.MODEL_RELAY;

        if (protocolParams.length >= 3) {
            model = protocolParams[2];
        }


        Tlog.v(TAG, "query switch result: " + SocketSecureKey.Util.resultIsOk(protocolParams[0]) + " on:" + on + " model:" + model);

        if (MySocketSecureKey.MUtil.isRelayModel(model)) {

            if (mCallBack != null) {
                mCallBack.onRelayResult(mSocketDataArray.getID(), on);

                IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
                if (iDebugerStream != null) {
                    iDebugerStream.receiveQueryRelay(mSocketDataArray.getObj(), mSocketDataArray.getID());
                }
            }


        } else if (MySocketSecureKey.MUtil.isBackLightModel(model)) {
            Tlog.e(TAG, "query switch model is backLight ");

        } else if (MySocketSecureKey.MUtil.isFlashLightModel(model)) {
            Tlog.e(TAG, "control switch is FlashLight ");

            IDebugerProtocolStream IDebugerStream = mCallBack.getIDebugerStream();
            if (IDebugerStream != null) {
                IDebugerStream.receiveQueryFlashState(mSocketDataArray.getObj(), mSocketDataArray.getID(), on);
            }


        }


    }
}
