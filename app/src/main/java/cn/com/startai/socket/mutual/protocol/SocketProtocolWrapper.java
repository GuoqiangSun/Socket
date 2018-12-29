package cn.com.startai.socket.mutual.protocol;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.IRegDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;
import cn.com.swain.support.protocolEngine.IO.ProtocolWrapper;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SocketProtocolWrapper extends ProtocolWrapper implements IRegDebugerProtocolStream {

    public static final String TAG = "SocketProtocolWrapper";

    public SocketProtocolWrapper() {

    }

    @Override
    public void onInputServerData(ReceivesData mReceiverData) {

        if (Debuger.isLogDebug) {
            Tlog.w(SocketResponseTask.TAG, "SocketProtocolWrapper onInputServerData() :" + mReceiverData.toString());
        }

        super.onInputServerData(mReceiverData);

        if (Debuger.isProductDetection) {
            if (productDetectionManager != null) {
                productDetectionManager.receiveData(mReceiverData);
            }
        }
    }

    @Override
    public void onOutputDataToServer(ResponseData mResponseData) {
        super.onOutputDataToServer(mResponseData);

        if (Debuger.isProductDetection) {
            if (productDetectionManager != null) {
                productDetectionManager.responseData(mResponseData);
            }
        }

        if (Debuger.isLogDebug) {
            Tlog.d(SocketResponseTask.TAG, "SocketProtocolWrapper onOutputDataToServer() :" + mResponseData.toString());
        }
    }

    @Override
    public void onBroadcastDataToServer(ResponseData mResponseData) {
        super.onOutputDataToServer(mResponseData);

        if (Debuger.isProductDetection) {
            if (productDetectionManager != null) {
                productDetectionManager.responseData(mResponseData);
            }
        }

        if (Debuger.isLogDebug) {
            Tlog.d(SocketResponseTask.TAG, "onBroadcastDataToServer() :" + mResponseData.toString());
        }

    }


    private IDebugerProtocolStream productDetectionManager;

    @Override
    public void regIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager) {
        Tlog.v(ProductDetectionManager.TAG, "SocketProtocolWrapper regIDebugerProtocolStream");
        this.productDetectionManager = productDetectionManager;
    }

    @Override
    public void unregIDebugerProtocolStream(IDebugerProtocolStream productDetectionManager) {
        Tlog.v(ProductDetectionManager.TAG, "SocketProtocolWrapper unregIDebugerProtocolStream");
        this.productDetectionManager = null;
    }


}
