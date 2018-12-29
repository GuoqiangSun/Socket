package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
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
public class RGBQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public RGBQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new RGBQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 5) {
            Tlog.e(TAG, " RGBQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        ColorLampRGB mRGB = new ColorLampRGB();
        mRGB.mac = mSocketDataArray.getID();
        mRGB.seq = protocolParams[1] & 0xFF;
        mRGB.r = protocolParams[2] & 0xFF;
        mRGB.g = protocolParams[3] & 0xFF;
        mRGB.b = protocolParams[4] & 0xFF;

        if (protocolParams.length >= 6) {
            mRGB.model = protocolParams[5] & 0xFF;
        } else {
            mRGB.model = SocketSecureKey.Model.MODEL_COLOR_LAMP;
        }

        Tlog.e(TAG, " RGBQueryReceiveTask result:" + result
                + " " + String.valueOf(mRGB));

        if (mTaskCallBack != null) {
            if (mRGB.model == SocketSecureKey.Model.MODEL_COLOR_LAMP) {
                mTaskCallBack.onRGBQueryResult(result, mRGB);
            }

            IDebugerProtocolStream iDebugerStream = mTaskCallBack.getIDebugerStream();
            if (iDebugerStream != null) {
                iDebugerStream.receiveQueryRGB(mSocketDataArray.getObj(), result, mRGB);
            }
        }


    }
}
