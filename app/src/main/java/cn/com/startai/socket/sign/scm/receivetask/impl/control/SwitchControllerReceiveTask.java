package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class SwitchControllerReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public SwitchControllerReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new SwitchControllerReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " SwitchControllerReceiveTask params is error ... ");
            return;
        }

//        result==0 ? success :fail;


        boolean on = SocketSecureKey.Util.on(protocolParams[1]);


        byte model;

        if (protocolParams.length >= 3) {
            model = protocolParams[2];
        } else {
            model = SocketSecureKey.Model.MODEL_RELAY;
        }


        Tlog.v(TAG, "control switch result:" + SocketSecureKey.Util.resultIsOk(protocolParams[0])
                + "; on:" + on + " model:" + model);

        if (SocketSecureKey.Util.isRelayModel(model)) {
            if (mCallBack != null) {
                mCallBack.onRelayResult(mSocketDataArray.getID(), on);

                IDebugerProtocolStream IDebugerStream = mCallBack.getIDebugerStream();
                if (IDebugerStream != null) {
                    if (on) {
                        IDebugerStream.receiveOpenRelay(mSocketDataArray.getObj(), mSocketDataArray.getID());
                    } else {
                        IDebugerStream.receiveCloseRelay(mSocketDataArray.getObj(), mSocketDataArray.getID());
                    }
                }
            }


        } else if (SocketSecureKey.Util.isBackLightModel(model)) {
            Tlog.e(TAG, "control switch is BackLight ");


        } else if (SocketSecureKey.Util.isFlashLightModel(model)) {
            Tlog.e(TAG, "control switch is FlashLight ");


            IDebugerProtocolStream IDebugerStream = mCallBack.getIDebugerStream();
            if (IDebugerStream != null) {
                IDebugerStream.receiveControlFlashState(mSocketDataArray.getObj(), mSocketDataArray.getID(), on);
            }


        } else if (SocketSecureKey.Util.isUSBModel(model)) {
            Tlog.e(TAG, "control switch is USB ");
            if (mCallBack != null) {
                mCallBack.onUSBResult(mSocketDataArray.getID(), on);
            }
        } else if (SocketSecureKey.Util.isNightLight(model)) {
            Tlog.e(TAG, "control switch is nightLight ");
            if (mCallBack != null) {
                mCallBack.onNightLightResult(mSocketDataArray.getID(), on);
            }
        }

    }
}
