package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
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
public class RGBSetReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public RGBSetReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new RGBSetReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 5) {
            Tlog.e(TAG, " MaxOutputQueryReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        ColorLampRGB mRGB = new ColorLampRGB();
        mRGB.mac = mSocketDataArray.getID();
        mRGB.seq = protocolParams[1] & 0xFF;
        mRGB.r = protocolParams[2] & 0xFF;
        mRGB.g = protocolParams[3] & 0xFF;
        mRGB.b = protocolParams[4] & 0xFF;

        Tlog.e(TAG, " RGBSetReceiveTask result:" + result
                + String.valueOf(mRGB));

        if (mTaskCallBack != null) {
            mTaskCallBack.onRGBSetResult(result, mRGB);
        }


    }
}
