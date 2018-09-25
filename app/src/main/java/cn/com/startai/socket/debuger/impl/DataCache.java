package cn.com.startai.socket.debuger.impl;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/26 0026
 * desc :
 */
class DataCache {

    private final IProductDetectionCallBack mRecyclerAdapter;
    private String TAG = ProductDetectionManager.TAG;

    private final int model;

    public static final int MODE_SEND_DATA = 0x00;
    public static final int MODE_REC_DATA = 0x01;
    private final String MODEL_STR;

    DataCache(IProductDetectionCallBack mRecyclerAdapter, int model) {
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.model = model;
        this.MODEL_STR = (model == MODE_SEND_DATA) ? "S" : "R";
    }

    // 快速模式，数据先添加如内存中
    private boolean skipQuickModel = false;
    private long mLastRecMillis;
    private int quickTimes = 0;
    private int lowTimes = 0;

    public void pushData(String dataRec) {

        Long mCurMillis = System.currentTimeMillis();

        if (Math.abs(mCurMillis - mLastRecMillis) < 800) {
            quickTimes++;

            if (quickTimes > 6) {
                changeModel(true);
                quickTimes = 0;
                lowTimes = 0;
            }

        } else {

            lowTimes++;

            if (lowTimes > 5) {
                changeModel(false);
                lowTimes = 0;
            }

        }

        if (skipQuickModel) {
            appendToBuffer(dataRec);
        } else {
            if (hasAppend) {

                appendToBuffer(dataRec);

                if (hasAppend) {
                    send(mReceiveBuffer.toString());
                    hasAppend = false;
                }

            } else {
                send(dataRec);
            }
        }

        mLastRecMillis = mCurMillis;
    }


    private StringBuffer mReceiveBuffer = new StringBuffer(512);
    private int receiveTimes = 0;
    private boolean hasAppend = false;

    private void appendToBuffer(String dataRec) {

        if (!hasAppend) {
            mReceiveBuffer = new StringBuffer(512);
            receiveTimes = 0;
        }
        hasAppend = true;
        mReceiveBuffer.append(dataRec);

        if (++receiveTimes >= 20) {
            send(mReceiveBuffer.toString());
            hasAppend = false;
            receiveTimes = 0;
        }
    }

    private void send(String msg) {
        switch (model) {
            case MODE_SEND_DATA:

                mRecyclerAdapter.refreshResponseData(msg);

                break;

            case MODE_REC_DATA:
            default:

                mRecyclerAdapter.refreshReceiveData(msg);

                break;
        }

    }

    public synchronized void forceRefresh() {
        if (hasAppend) {
            send(mReceiveBuffer.toString());
            hasAppend = false;
        }
        changeModel(false);
    }

    private void changeModel(boolean model) {

        if (skipQuickModel != model) {
            skipQuickModel = model;
            if (model) {
                mRecyclerAdapter.toast(MODEL_STR + " entering fast model");
            } else {
                mRecyclerAdapter.toast(MODEL_STR + " entering low model");
            }
        }
    }
}
