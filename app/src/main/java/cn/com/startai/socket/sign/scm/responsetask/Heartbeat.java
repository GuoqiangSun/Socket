package cn.com.startai.socket.sign.scm.responsetask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.startai.socket.sign.scm.util.ProtocolDataCache;
import cn.com.swain.support.protocolEngine.utils.SEQ;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/23 0023
 * desc :
 */
public class Heartbeat {

    private String TAG = SocketScmManager.TAG;

    private final HeartbeatHandler mHeartbeatHandler;

    private final IDataProtocolOutput mProtocolDataOutput;

    private ResponseData mResponseData = new ResponseData();

    private final OnHeartbeatCallBack mCallBack;

    private final SEQ mSeq;

    public Heartbeat(Looper mWorkLooper, IDataProtocolOutput mProtocolDataOutput, OnHeartbeatCallBack mCallBack, String mac) {

        this.mCallBack = mCallBack;
        this.mProtocolDataOutput = mProtocolDataOutput;
        this.mHeartbeatHandler = new HeartbeatHandler(mWorkLooper, this);
        this.mResponseData.toID = mac;
        this.mSeq = new SEQ(mac);

    }

    private static final int FIRST_DELAY = 1000;
    private static final int CYCLE = 1000 * 10;

    private static final int MSG_WHAT_HEARTBEAT = 0x01;
    private static final int MSG_WHAT_CHECK = 0x02;
    private boolean start = false;

    public void start() {

        if (mHeartbeatHandler.hasMessages(MSG_WHAT_HEARTBEAT)) {
            mHeartbeatHandler.removeMessages(MSG_WHAT_HEARTBEAT);
        } else {
            mSeq.resetSeq();
        }
        start = true;
        mHeartbeatHandler.sendEmptyMessageDelayed(MSG_WHAT_HEARTBEAT, FIRST_DELAY);
        Tlog.e(TAG, mResponseData.toID + " heartbeat start ");
    }

    public void stop() {
        start = false;
        if (mHeartbeatHandler.hasMessages(MSG_WHAT_HEARTBEAT)) {
            mHeartbeatHandler.removeMessages(MSG_WHAT_HEARTBEAT);
        }
        Tlog.e(TAG, mResponseData.toID + " heartbeat stop ");
    }

    public void check(int diff, long delay) {
        Message message = mHeartbeatHandler.obtainMessage(MSG_WHAT_CHECK, diff);
        mHeartbeatHandler.sendMessageDelayed(message, delay);
    }

    public void release() {
        if (mHeartbeatHandler.hasMessages(MSG_WHAT_HEARTBEAT)) {
            mHeartbeatHandler.removeMessages(MSG_WHAT_HEARTBEAT);
        }
    }

    private boolean canSendHeartbeat = true;

    public void setCanSendHeartbeat(boolean flag) {
        canSendHeartbeat = flag;
    }

    private void handleMessage(Message msg) {

        if (msg.what == MSG_WHAT_HEARTBEAT) {
            if (canSendHeartbeat) {
                if (mCallBack != null) {
                    mCallBack.onStartSendHeartbeat(mResponseData.toID);
                }

                ResponseData responseData = ProtocolDataCache.getHeartbeat(mResponseData.toID, token, mSeq.getSelfAddSeq());
                responseData.getSendModel().setSendModelIsLan();
                mProtocolDataOutput.onOutputDataToServer(responseData);
            } else {
                Tlog.w(TAG, " heartbeat handle msg ; can not send heartbeat");
            }

            if (start) {
                mHeartbeatHandler.sendEmptyMessageDelayed(MSG_WHAT_HEARTBEAT, CYCLE);
            } else {
                Tlog.w(TAG, " re send cycle heartbeat, but not start ");
            }

        } else if (msg.what == MSG_WHAT_CHECK) {

            if (mCallBack != null) {
                mCallBack.onCheckHeartbeat(mResponseData.toID, (Integer) msg.obj);
            }

        }

    }

    private int token;

    public void setToken(int token) {
        this.token = token;
        Tlog.v(TAG, " heartbeat setToken:" + Integer.toHexString(token));
    }

    public interface OnHeartbeatCallBack {
        void onStartSendHeartbeat(String mac);

        void onCheckHeartbeat(String toID, int diff);
    }

    private static final class HeartbeatHandler extends Handler {
        private final WeakReference<Heartbeat> wr;

        HeartbeatHandler(Looper mLooper, Heartbeat mThis) {
            super(mLooper);

            wr = new WeakReference<>(mThis);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Heartbeat mHeartbeat;

            if (wr != null && (mHeartbeat = wr.get()) != null) {
                mHeartbeat.handleMessage(msg);
            }

        }
    }

}
