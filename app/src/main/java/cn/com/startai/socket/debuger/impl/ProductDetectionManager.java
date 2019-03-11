package cn.com.startai.socket.debuger.impl;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.adapter.DetectionRecyclerAdapter;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.sign.js.impl.JsManager;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.util.IpUtil;
import cn.com.swain.baselib.util.StrUtil;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/17 0017
 * desc
 */
public class ProductDetectionManager implements DetectionRecyclerAdapter.OnClick, IDebugerProtocolStream, IService {

    public static final String TAG = "ProductDetectionManager";

    private IProductDetectionCallBack mRecyclerAdapter;

    private DetectionHandler mDetectionHandler;
    private UIHandler mUIHandler;

    private JsManager jsManager;
    private SocketScmManager scmManager;

    private StringBufferUtil mSbUtil;

    private DataCache mRecDataCache;
    private DataCache mSendDataCache;

    public ProductDetectionManager(IProductDetectionCallBack mRecyclerAdapter) {
        this.mRecyclerAdapter = mRecyclerAdapter;

        Looper workLooper = LooperManager.getInstance().getWorkLooper();

        mDetectionHandler = new DetectionHandler(this, workLooper);

        Looper mainLooper = Looper.getMainLooper();
        mUIHandler = new UIHandler(this, mainLooper);

        mSbUtil = new StringBufferUtil(mUIHandler, mRecyclerAdapter.getApp());

        scmManager = Controller.getInstance().getScmManager();
        jsManager = Controller.getInstance().getJsManager();

        mRecDataCache = new DataCache(this.mRecyclerAdapter, DataCache.MODE_REC_DATA);
        mSendDataCache = new DataCache(this.mRecyclerAdapter, DataCache.MODE_SEND_DATA);
    }

    private String mCurDevice = H5Config.DEFAULT_MAC;

    public void setDetectDevice(String device) {
        if (device != null) {
            this.mCurDevice = device;
        }

    }

    private List<DetectInfo> mDatas = new ArrayList<>();

    @Override
    public void onSCreate() {

        mSbUtil.append("");
        Controller.getInstance().getScmManager().regIDebugerProtocolStream(this);
        Controller.getInstance().getProtocolWrapper().regIDebugerProtocolStream(this);


        String connectedAddress = Debuger.getInstance().getProductDevice();
        Tlog.v(TAG, "address:" + connectedAddress);
        if (connectedAddress == null) {
//            mUIHandler.obtainMessage(REFRESH_TOAST, "Ble没有连接").sendToTarget(); // fail
            Toast.makeText(mRecyclerAdapter.getApp(), "Ble No Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSResume() {

    }

    @Override
    public void onSPause() {

    }

    public List<DetectInfo> initData() {

        mDatas.clear();
        mUIHandler.removeCallbacksAndMessages(null);
        mDetectionHandler.removeCallbacksAndMessages(null);

        DetectInfo mDetect = new DetectInfo();
        mDetect.name = "openRelay";
        mDetect.mUIName = "开继电器";
        mDetect.type = TYPE_OPEN_RELAY;
        mDatas.add(mDetect);

        DetectInfo mDetect1 = new DetectInfo();
        mDetect1.name = "closeRelay";
        mDetect1.mUIName = "关继电器";
        mDetect1.type = TYPE_CLOSE_RELAY;
        mDatas.add(mDetect1);

        DetectInfo mDetect2 = new DetectInfo();
        mDetect2.name = "queryRelayState";
        mDetect2.mUIName = "查询继电器";
        mDetect2.type = TYPE_QUERY_RELAY_STATUS;
        mDatas.add(mDetect2);

        DetectInfo mDetect3 = new DetectInfo();
        mDetect3.name = "queryTime";
        mDetect3.mUIName = "查询时间";
        mDetect3.type = TYPE_QUERY_TIME;
        mDatas.add(mDetect3);

        DetectInfo mDetect4 = new DetectInfo();
        mDetect4.name = "queryTiming";
        mDetect4.mUIName = "查询定时";
        mDetect4.type = TYPE_QUERY_TIMING;
        mDatas.add(mDetect4);

        DetectInfo mDetect5 = new DetectInfo();
        mDetect5.name = "queryCountdown";
        mDetect5.mUIName = "查询倒计时";
        mDetect5.type = TYPE_QUERY_COUNTDOWN;
        mDatas.add(mDetect5);

        return mDatas;

    }

    @SuppressLint("StaticFieldLeak")
    public void generalVerificationReport() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                String msg = mSbUtil.toString();
                mSbUtil = new StringBufferUtil(mUIHandler, mRecyclerAdapter.getApp());
                mSbUtil.append("");

                StringBuilder sb = new StringBuilder(128);
                sb.append("\t******TEST REPORT******\n");
                for (DetectInfo nDetect : mDatas) {
                    sb.append("[");
                    sb.append(String.valueOf(nDetect.name));
                    sb.append("]");
                    sb.append(" detection report :\n");
                    sb.append(" total send count : ");
                    sb.append(String.valueOf(nDetect.mTotalSendCount));
                    sb.append(" , ");
                    sb.append(" total receive count : ");
                    sb.append(String.valueOf(nDetect.mTotalRecCount));
                    sb.append(" , ");
                    int diff = nDetect.mTotalSendCount - nDetect.mTotalRecCount;
                    if (diff >= 0) {
                        sb.append(" lose ");
                    } else {
                        sb.append(" more ");
                    }
                    sb.append(String.valueOf(Math.abs(diff)));
                    sb.append(" package\n");
                }

                final String reportStr = sb.toString() + msg;
                return FileManager.getInstance().saveProductDetectionLog(reportStr);
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);

                if (aVoid != null) {
                    Toast.makeText(mRecyclerAdapter.getApp(), R.string.generating_verification_success, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mRecyclerAdapter.getApp(), R.string.generating_verification_fail, Toast.LENGTH_SHORT).show();
                }

                mRecyclerAdapter.setVerificationPath(aVoid);
                mRecyclerAdapter.clearReceiveResponse();

            }
        }.execute();

    }


    @Override
    public void onSDestroy() {
        mSbUtil = null;
        mDatas.clear();
        mDetectionHandler.removeCallbacksAndMessages(null);
        Controller.getInstance().getScmManager().unregIDebugerProtocolStream(this);
        Controller.getInstance().getProtocolWrapper().unregIDebugerProtocolStream(this);

    }

    @Override
    public void onSFinish() {

    }

    @Override
    public void onClick(DetectInfo detect) {
        detect.toggle();
        sendWorkMsg(detect);
        mUIHandler.sendEmptyMessage(REFRESH_ADAPTER);
    }

    private void sendWorkMsg(DetectInfo detect) {

        if (detect.startup) {

            mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " (onClick)startup " + detect.name + " \n");

            if (!mDetectionHandler.hasMessages(detect.type)) {
                mDetectionHandler.obtainMessage(detect.type, detect).sendToTarget();
            }

        } else {

            mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " (onClick)finish " + detect.name + " \n");

            detect.setSendFinish();
            if (mDetectionHandler.hasMessages(detect.type)) {
                mDetectionHandler.removeMessages(detect.type);
            }
        }
    }

    /**
     * 控制继电器
     */
    private static final int TYPE_OPEN_RELAY = 0x00;
    private static final int TYPE_CLOSE_RELAY = 0x01;

    /**
     * 查询继电器状态
     */
    private static final int TYPE_QUERY_RELAY_STATUS = 0x02;

    /**
     * 查询时间
     */
    private static final int TYPE_QUERY_TIME = 0x03;

    /**
     * 查询定时
     */
    private static final int TYPE_QUERY_TIMING = 0x04;

    /**
     * 查询倒计时
     */
    private static final int TYPE_QUERY_COUNTDOWN = 0x05;

    private void handleMessage(Message msg) {
        DetectInfo mDetect = (DetectInfo) msg.obj;
        mDetect.sendOnce();

        switch (msg.what) {
            case TYPE_OPEN_RELAY:
//                mScmManager.switchRelay(mScmManager.getConAddress(), true);

                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event open relay;\n");
                jsManager.onJSMSwitchRelay(Debuger.getInstance().getProductDevice(), true);

                break;
            case TYPE_CLOSE_RELAY:
//                mScmManager.switchRelay(mScmManager.getConAddress(), false);
                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event close relay;\n");
                jsManager.onJSMSwitchRelay(Debuger.getInstance().getProductDevice(), false);

                break;
            case TYPE_QUERY_RELAY_STATUS:
//                mScmManager.queryRelayState(mScmManager.getConAddress());

                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event query relay;\n");
                jsManager.onJSMQueryRelayStatus(Debuger.getInstance().getProductDevice());

                break;

            case TYPE_QUERY_TIME:
                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event query time;\n");
                jsManager.onJSQueryScmTime(Debuger.getInstance().getProductDevice());

                break;

            case TYPE_QUERY_TIMING:
                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event query timing;\n");
                jsManager.onJSTQueryTimingListData(Debuger.getInstance().getProductDevice());
                break;

            case TYPE_QUERY_COUNTDOWN:
                mSbUtil.append(mDateFormatRes.format(new Date(System.currentTimeMillis())) + " event query countdown;\n");
                jsManager.onJSCQueryCountdownData(Debuger.getInstance().getProductDevice());
                break;

        }

        if (mDetect.isSend()) {
            Message message = mDetectionHandler.obtainMessage(msg.what, mDetect);
            mDetectionHandler.sendMessageDelayed(message, mDetect.mSendInterval);
        } else {
            mDetect.setSendFinish();
        }

        refreshAdapterInThread();
    }


    private SimpleDateFormat mDateFormatRec = new SimpleDateFormat("HH:mm:ss.SSS");


    @Override
    public void receiveFail(FailTaskResult mFailTask) {

        String data = mDateFormatRec.format(new Date(System.currentTimeMillis())) + " FAIL: " + mFailTask.toString() + "\n";
        mSbUtil.append(data);

        String description = mFailTask.description;
        if (null == description) {
            description = "收到一包错误的数据";
        }
        mUIHandler.obtainMessage(REFRESH_TOAST, description).sendToTarget();

    }

    @Override
    public void receiveData(ReceivesData mReceiverData) {
        String msg = StrUtil.toString(mReceiverData.data);
        String data = mDateFormatRec.format(new Date(System.currentTimeMillis())) + " R: " + mReceiverData.fromID + "-" + msg + "\n";
        mSbUtil.append(data);
        mUIHandler.obtainMessage(REFRESH_RECEIVE_BYTE, data).sendToTarget();

    }

    private SimpleDateFormat mDateFormatRes = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    public void responseData(ResponseData mResponseData) {
        String msg = StrUtil.toString(mResponseData.data);
        String data = mDateFormatRes.format(new Date(System.currentTimeMillis())) + " S: " + mResponseData.toID + "-" + msg + "\n";
        mSbUtil.append(data);
        mUIHandler.obtainMessage(REFRESH_RESPONSE_BYTE, data).sendToTarget();
    }

    private boolean checkModelIsLan(Object model) {
        return (model instanceof String && IpUtil.ipMatches((String) model));
    }

    private boolean canCount(Object model, String mac) {

        return true;

/*        if (mCurDevice.equalsIgnoreCase(mac)) {
            if (CustomManager.getInstance().isBleProject()) {
                return true;
            }
            if (checkModelIsLan(model)) {
                return true;
            }
            Tlog.e(TAG, " receive one pkg ,model not lan ; model:" + model);
            return false;
        }
        Tlog.e(TAG, " receive one pkg ,mac not match ; mac:" + mac + " device:" + mCurDevice);
        return false;
        */

    }


    @Override
    public void receiveOpenRelay(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive open relay;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_OPEN_RELAY);
        }
    }

    @Override
    public void receiveCloseRelay(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive close relay;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_CLOSE_RELAY);
        }
    }

    @Override
    public void receiveQueryRelay(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive query relay;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_QUERY_RELAY_STATUS);
        }
    }

    @Override
    public void receiveQueryTime(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive query time;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_QUERY_TIME);
        }
    }

    @Override
    public void receiveTiming(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive query timing;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_QUERY_TIMING);
        }
    }

    @Override
    public void receiveCountdown(Object model, String mac) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + mac + " receive query countdown;\n");
        if (canCount(model, mac)) {
            recOnePkg(TYPE_QUERY_COUNTDOWN);
        }
    }

    @Override
    public void connected(String address) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + " connected :" + address + "\n");
    }

    @Override
    public void disconnected(String address) {
        mSbUtil.append(mDateFormatRec.format(new Date(System.currentTimeMillis())) + " disconnected :+" + address + "\n");
        mDetectionHandler.removeCallbacksAndMessages(null);
        if (mDatas.size() > 0) {
            for (DetectInfo mDetect : mDatas) {
                mDetect.setFinish();
            }
        }
        mUIHandler.obtainMessage(REFRESH_TOAST, "disconnected").sendToTarget();
        mUIHandler.sendEmptyMessage(REFRESH_ADAPTER);
    }

    @Override
    public void receiveControlFlashState(Object obj, String id, boolean on) {
        mUIHandler.obtainMessage(REFRESH_FLASH_STATE, on).sendToTarget();
    }

    @Override
    public void receiveQueryFlashState(Object obj, String id, boolean on) {
        mUIHandler.obtainMessage(REFRESH_FLASH_STATE, on).sendToTarget();
    }

    @Override
    public void receiveProtocolAnalysisResult(byte[] protocolParams) {
        mUIHandler.obtainMessage(RECEIVE_TEST_ANALYSIS_RESULT, protocolParams).sendToTarget();
    }

    @Override
    public void receiveNightLightSet(Object obj, NightLightTiming mNightLightTiming) {
        mUIHandler.obtainMessage(REFRESH_NIGHT_LIGHT_SET, mNightLightTiming).sendToTarget();
    }

    @Override
    public void receiveNightLightQuery(Object obj, NightLightTiming mNightLightTiming) {
        mUIHandler.obtainMessage(REFRESH_NIGHT_LIGHT_QUERY, mNightLightTiming).sendToTarget();
    }

    @Override
    public void receiveQueryRGB(Object obj, boolean result, ColorLampRGB mRGB) {
        mUIHandler.obtainMessage(REFRESH_NIGHT_RGB_QUERY, mRGB).sendToTarget();
    }

    @Override
    public void receiveSetRGB(Object obj, boolean result, ColorLampRGB mRGB) {
        mUIHandler.obtainMessage(REFRESH_NIGHT_RGB_SET, mRGB).sendToTarget();
    }

    private void recOnePkg(int type) {
        if (mDatas.size() > 0) {
            for (DetectInfo mDetect : mDatas) {
                if (mDetect.type == type) {
                    mDetect.receiveOnce();
                    if (!mDetect.isRec()) {
                        mDetect.setRecFinish();
                    }
                    refreshAdapterInThread();
                    break;
                }
            }
        }
    }

    private void refreshAdapterInThread() {

        if (!mUIHandler.hasMessages(REFRESH_ADAPTER)) {
            mUIHandler.sendEmptyMessageDelayed(REFRESH_ADAPTER, 1000);
        }

    }

    private static final int REFRESH_ADAPTER = 0x00;
    private static final int REFRESH_RECEIVE_BYTE = 0x01;
    private static final int REFRESH_RESPONSE_BYTE = 0x02;
    private static final int REFRESH_TOAST = 0x03;
    private static final int REFRESH_FLASH_STATE = 0x04;
    private static final int RECEIVE_TEST_ANALYSIS_RESULT = 0x05;
    private static final int REFRESH_NIGHT_LIGHT_SET = 0x06;
    private static final int REFRESH_NIGHT_LIGHT_QUERY = 0x07;
    private static final int REFRESH_NIGHT_RGB_QUERY = 0x08;
    private static final int REFRESH_NIGHT_RGB_SET = 0x09;

    private void uiHandlerMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_ADAPTER:
                mRecyclerAdapter.refreshUI();

                boolean startup = false;
                if (mDatas.size() > 0) {
                    for (DetectInfo mDetect : mDatas) {
                        if (mDetect.startup) {
                            startup = true;
                            break;
                        }
                    }
                }

                if (!startup) {
                    mRecDataCache.forceRefresh();
                    mSendDataCache.forceRefresh();
                }

                break;
            case REFRESH_RECEIVE_BYTE:
                String dataRec = (String) msg.obj;
//                mRecyclerAdapter.refreshReceiveData(dataRes);
                mRecDataCache.pushData(dataRec);
                break;
            case REFRESH_RESPONSE_BYTE:
                String dataRes = (String) msg.obj;
//                mRecyclerAdapter.refreshResponseData(dataRes);
                mSendDataCache.pushData(dataRes);

                break;
            case REFRESH_TOAST:
                String toastStr = (String) msg.obj;
                Tlog.v(TAG, toastStr);
                mRecyclerAdapter.toast(toastStr);
//                Toast.makeText(mRecyclerAdapter.getApp(), toastStr, Toast.LENGTH_LONG).show();
                break;
            case REFRESH_FLASH_STATE:

                Boolean on = (Boolean) msg.obj;
                mRecyclerAdapter.flashModel(on);

                break;
            case RECEIVE_TEST_ANALYSIS_RESULT:
                mRecyclerAdapter.receiveProtocolAnalysisResult((byte[]) msg.obj);
                break;

            case REFRESH_NIGHT_LIGHT_SET:
                mRecyclerAdapter.nightLightSetResult((NightLightTiming) msg.obj);
                break;

            case REFRESH_NIGHT_LIGHT_QUERY:
                mRecyclerAdapter.nightLightQueryResult((NightLightTiming) msg.obj);
                break;

            case REFRESH_NIGHT_RGB_QUERY:
                mRecyclerAdapter.rgbQueryResult((ColorLampRGB) msg.obj);
                break;
            case REFRESH_NIGHT_RGB_SET:
                mRecyclerAdapter.rgbSetResult((ColorLampRGB) msg.obj);
                break;
        }

    }

    private static class UIHandler extends Handler {
        private WeakReference<ProductDetectionManager> wr;

        UIHandler(ProductDetectionManager manager, Looper mainLooper) {
            super(mainLooper);
            wr = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProductDetectionManager manager;
            if ((manager = wr.get()) != null) {
                manager.uiHandlerMessage(msg);
            } else {
                Tlog.e(TAG, " UIHandler wr.get()=null ");
            }
        }
    }

    private static class DetectionHandler extends Handler {
        private WeakReference<ProductDetectionManager> wr;

        private DetectionHandler(ProductDetectionManager manager, Looper mLooper) {
            super(mLooper);
            wr = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProductDetectionManager manager;
            if ((manager = wr.get()) != null) {
                manager.handleMessage(msg);
            }

        }
    }


}
