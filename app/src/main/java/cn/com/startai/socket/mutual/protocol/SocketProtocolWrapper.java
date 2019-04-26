package cn.com.startai.socket.mutual.protocol;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.IRegDebugerProtocolStream;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.IO.ProtocolWrapper;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SocketProtocolWrapper extends ProtocolWrapper implements IRegDebugerProtocolStream, IService {

    public static final String TAG = "SocketProtocolWrapper";

    public SocketProtocolWrapper() {

    }

    @Override
    public void onInputProtocolData(ReceivesData mReceiverData) {

        if (Debuger.isLogDebug) {
            Tlog.w(SocketResponseTask.TAG, "SocketProtocolWrapper onInputServerData() :" + mReceiverData.toString());
        }

        super.onInputProtocolData(mReceiverData);

        if (Debuger.isProductDetection) {
            if (productDetectionManager != null) {
                productDetectionManager.receiveData(mReceiverData);
            }
        }
    }

    @Override
    public void onOutputProtocolData(ResponseData mResponseData) {
        super.onOutputProtocolData(mResponseData);

        if (Debuger.isProductDetection) {
            if (productDetectionManager != null) {
                productDetectionManager.responseData(mResponseData);
            }
        }

        if (Debuger.isLogDebug) {
            Tlog.d(SocketResponseTask.TAG, "SocketProtocolWrapper onOutputProtocolData() :" + mResponseData.toString());
        }
    }

    @Override
    public void onBroadcastProtocolData(ResponseData mResponseData) {
        super.onBroadcastProtocolData(mResponseData);

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


    @Override
    public void onSCreate() {

    }

    @Override
    public void onSResume() {

    }

    @Override
    public void onSPause() {

    }

    @Override
    public void onSFinish() {

    }

    @Override
    public void onSDestroy() {
        releaseInputBase();
        releaseOutputBase();
    }
}
