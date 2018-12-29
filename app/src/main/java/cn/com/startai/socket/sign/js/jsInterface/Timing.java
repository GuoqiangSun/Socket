package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/7 0007
 * desc :
 */
public class Timing extends AbsHandlerJsInterface {


    public interface IJSTimingCallBack {

        void onJSTQueryTimingListData(String mac);

        void onJSTSetCommonTiming(TimingCommonData mTimingCommonData);

        void onJSTSetPatternTiming(TimingAdvanceData mTimingAdvanceData);

        void onJSTQueryComTimingListData(String mac);

        void onJSTQueryAdvTimingListData(String mac);
    }

    public static class Method {

        private static final String METHOD_TIMING_LIST_DATA
                = "javascript:commonPatternListDataResponse('$mac','$commonData','$advancedData')";

        /**
         * 请求列表数据
         *
         * @param mac
         * @param commonData
         * @param advancedData
         * @return
         */
        public static String callJsTimingListData(String mac, String commonData, String advancedData) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TIMING_LIST_DATA.replace("$mac", mac)
                    .replace("$commonData", commonData)
                    .replace("$advancedData", advancedData);
        }

        private static final String METHOD_TIMING_COMMON_SET_RESPONSE
                = "javascript:commonPatternTimingResponse('$mac',$result,$startup,$id,$model)";

        /**
         * 新建定时列表回复
         *
         * @param mac
         * @param result
         * @return
         */
        public static String callJsTimingCommonSet(String mac, boolean result, boolean startup, int id, int model) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TIMING_COMMON_SET_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$startup", String.valueOf(startup))
                    .replace("$id", String.valueOf(id))
                    .replace("$model", String.valueOf(model));
        }

    }

    public static final String NAME_JSI = "Timing";
    private String TAG = H5Config.TAG;

    private final IJSTimingCallBack mCallBack;

    public Timing(Looper mLooper, IJSTimingCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    /**
     * 定时列表数据
     */
    @JavascriptInterface
    public void commonPatternListDataRequest(String mac) {
        Tlog.v(TAG, " commonPatternListDataRequest " + mac);

        getHandler().obtainMessage(MSG_QUERY_TIMING_DATA, mac).sendToTarget();

    }

    /**
     * 定时列表数据
     */
    @JavascriptInterface
    public void commonPatternListDataRequest(String mac, int model) {
        Tlog.v(TAG, " commonPatternListDataRequest mac:" + mac + " model:" + model);

        if (model != 1 && model != 2) {
            model = 1;
        }

        if (model == 1) {
            getHandler().obtainMessage(MSG_QUERY_TIMING_COM_DATA, mac).sendToTarget();
        } else {
            getHandler().obtainMessage(MSG_QUERY_TIMING_ADV_DATA, mac).sendToTarget();
        }


    }

    @Deprecated
    @JavascriptInterface
    public void commonPatternNewTimingRequest(String mac, int id, boolean on, String time, int week, boolean startup) {
        Tlog.v(TAG, " commonPatternNewTimingRequest id:" + id + " startup:" + on
                + " time:" + time + " week:" + week + " startup:" + startup);
    }

    /**
     * 普通模式新建定时
     */
    @JavascriptInterface
    public void commonPatternNewTimingRequest(String mac, int id, boolean on,
                                              String time, int week, boolean startup, int model,
                                              String onTimeInterval, String offTimeInterval, String offTime) {
        Tlog.v(TAG, " commonPatternNewTimingRequest");
        if ((id & 0xFF) != 0xFF) {
            id = -1;
        }

//        TimingCommonData mTimingData = new TimingCommonData();
//        mTimingData.setMac(mac);
//        mTimingData.setModelIsCommon();
//        mTimingData.setId((byte) id);
//        mTimingData.setStateIsConfirm();
//        mTimingData.setOn(startup);
//        mTimingData.setTime(time);
//        mTimingData.setWeek((byte) (week & 0xFF));
//        mTimingData.setStartup(startup);
//        getHandler().obtainMessage(MSG_SET_TIMING, mTimingData).sendToTarget();

        commonPatternEditTimingRequest(mac, id, on, time, week,
                startup, model, onTimeInterval, offTimeInterval, offTime);

    }

    /**
     * 普通模式编辑定时
     */
    @JavascriptInterface
    public void commonPatternEditTimingRequest(String mac, int id, boolean on,
                                               String time, int week, boolean startup, int model,
                                               String onTimeInterval, String offTimeInterval, String offTime) {
        Tlog.v(TAG, " commonPatternEditTimingRequest id:" + id + " startup:" + on
                + " time:" + time + " week:" + week + " startup:" + startup);
        Tlog.v(TAG, " model:" + model + " offTimeInterval:"
                + offTimeInterval + " onTimeInterval:" + onTimeInterval + " offTime:" + offTime);

        if (SocketSecureKey.Util.isCommonTiming((byte) model)) {

            TimingCommonData mTimingCommonData = new TimingCommonData();
            mTimingCommonData.setMac(mac);
            mTimingCommonData.setModelIsCommon();
            mTimingCommonData.setId((byte) id);
            mTimingCommonData.setStateIsConfirm();
            mTimingCommonData.setOn(on);
            mTimingCommonData.setTime(time);
            mTimingCommonData.setWeek((byte) (week & 0xFF));
            mTimingCommonData.setStartup(startup);
            getHandler().obtainMessage(MSG_SET_COMMON_TIMING, mTimingCommonData).sendToTarget();

        } else if (SocketSecureKey.Util.isAdvanceTiming((byte) model)) {

            TimingAdvanceData mTimingAdvanceData = new TimingAdvanceData();
            mTimingAdvanceData.setModelIsAdvance();
            mTimingAdvanceData.mac = mac;
            mTimingAdvanceData.id = (byte) id;
            mTimingAdvanceData.setStateIsConfirm();
            mTimingAdvanceData.on = on;
            mTimingAdvanceData.setOnTimeSplit(time);
            mTimingAdvanceData.week = week;
            mTimingAdvanceData.startup = startup;
            mTimingAdvanceData.setOnIntervalTime(onTimeInterval);
            mTimingAdvanceData.setOffIntervalTime(offTimeInterval);
            mTimingAdvanceData.setOffTimeSplit(offTime);
            getHandler().obtainMessage(MSG_SET_ADVANCE_TIMING, mTimingAdvanceData).sendToTarget();
        }

    }

    /**
     * 删除定时
     */
    @JavascriptInterface
    public void commonPatternDeleteTimingRequest(String mac, String id, int model) {
        Tlog.v(TAG, " commonPatternDeleteTimingRequest id:" + id + " mac:" + mac + " model:" + model);

        if (SocketSecureKey.Util.isCommonTiming((byte) model)) {

            TimingCommonData mTimingData = new TimingCommonData();
            mTimingData.setMac(mac);
            mTimingData.setModelIsCommon();
            mTimingData.setId((byte) Integer.parseInt(id.trim()));
            mTimingData.setStateIsDelete();
            mTimingData.setOn(false);
            mTimingData.setTime("00:00");
            mTimingData.setWeek(0);
            getHandler().obtainMessage(MSG_SET_COMMON_TIMING, mTimingData).sendToTarget();

        } else if (SocketSecureKey.Util.isAdvanceTiming((byte) model)) {

            TimingAdvanceData mTimingAdvanceData = new TimingAdvanceData();
            mTimingAdvanceData.setModelIsAdvance();
            mTimingAdvanceData.mac = mac;
            try {
                mTimingAdvanceData.id = (byte) Integer.parseInt(id.trim());
            } catch (Exception e) {

            }
            mTimingAdvanceData.on = false;
            mTimingAdvanceData.startup = false;
            mTimingAdvanceData.setStateIsDelete();
            getHandler().obtainMessage(MSG_SET_ADVANCE_TIMING, mTimingAdvanceData).sendToTarget();

        }


    }

    private static final int MSG_QUERY_TIMING_DATA = 0x12;

    private static final int MSG_SET_COMMON_TIMING = 0x11;

    private static final int MSG_SET_ADVANCE_TIMING = 0x13;

    private static final int MSG_QUERY_TIMING_COM_DATA = 0x14;
    private static final int MSG_QUERY_TIMING_ADV_DATA = 0x15;

    @Override
    protected void handleMessage(Message msg) {

        if (msg.what == MSG_QUERY_TIMING_DATA) {
            if (mCallBack != null) {
                mCallBack.onJSTQueryTimingListData((String) msg.obj);
            }
        } else if (msg.what == MSG_SET_COMMON_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTSetCommonTiming((TimingCommonData) msg.obj);
            }
        } else if (msg.what == MSG_SET_ADVANCE_TIMING) {
            if (mCallBack != null) {
                mCallBack.onJSTSetPatternTiming((TimingAdvanceData) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_TIMING_COM_DATA) {
            if (mCallBack != null) {
                mCallBack.onJSTQueryComTimingListData((String) msg.obj);
            }
        } else if (msg.what == MSG_QUERY_TIMING_ADV_DATA) {
            if (mCallBack != null) {
                mCallBack.onJSTQueryAdvTimingListData((String) msg.obj);
            }
        }

    }

}
