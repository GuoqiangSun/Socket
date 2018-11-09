package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/11 0011
 * desc :
 */
public class State extends AbsHandlerJsInterface {


    public interface IJSStateCallBack {

        void onJSQueryHistoryCount(QueryHistoryCount mQueryCount);

        void onJSQueryCostRate(String mac);

        void onJSQueryCumuParam(String mac);
    }

    public static final class Method {

        private static final String RENAME = "javascript:deviceHistoryDataResponse('$mac','$time',$day,$interval,'$data')";

        public static String callJsHistoryData(String mac, String time, int day, int interval, String data) {
            return RENAME.replace("$mac", String.valueOf(mac)).replace("$time", String.valueOf(time))
                    .replace("$day", String.valueOf(day)).replace("$interval", String.valueOf(interval))
                    .replace("$data", String.valueOf(data));
        }

        private static final String COST_RATE = "javascript:deviceRateResponse('$mac',$hour1,$minute1" +
                ",$price1,$hour2,$minute2,$price2)";


        public static String callJsQueryCostRate(String mac, int hour1, int minute1, float price1,
                                                 int hour2, int minute2, float price2) {
            return COST_RATE.replace("$mac", String.valueOf(mac))
                    .replace("$hour1", String.valueOf(hour1)).replace("$minute1", String.valueOf(minute1))
                    .replace("$price1", String.valueOf(price1)).replace("$hour2", String.valueOf(hour2))
                    .replace("$minute2", String.valueOf(minute2)).replace("$price2", String.valueOf(price2));
        }

        private static final String CUMU_PARAMS = "javascript:deviceAccumulationParameterResponse('$mac',$time,$ghg,$electricity)";

        public static String callJsCumuParams(String mac, long time, long ghg, long electrycity) {
            return CUMU_PARAMS.replace("$mac", String.valueOf(mac)).replace("$time", String.valueOf(time))
                    .replace("$ghg", String.valueOf(ghg)).replace("$electrycity", String.valueOf(electrycity));
        }

    }

    private final IJSStateCallBack mCallBack;

    public static final String NAME_JSI = "State";

    private String TAG = H5Config.TAG;

    public State(Looper mLooper, IJSStateCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void deviceHistoryDataRequest(String mac, String startTime, String endTime, int interval) {
        Tlog.v(TAG, " deviceHistoryDataRequest startTime:" + startTime);
        final QueryHistoryCount mQueryCount = new QueryHistoryCount();
        mQueryCount.mac = mac;
        mQueryCount.startTime = startTime;
        mQueryCount.endTime = endTime;
        mQueryCount.interval = interval;
        getHandler().obtainMessage(MSG_HISTORY, mQueryCount).sendToTarget();

    }


    @JavascriptInterface
    public void deviceAccumulationParameterRequest(String mac) {
        getHandler().obtainMessage(MSG_CUMU_PARAM, mac).sendToTarget();
    }

    @JavascriptInterface
    public void deviceRateRequest(String mac) {
        getHandler().obtainMessage(MSG_COST_RATE_PARAM, mac).sendToTarget();
    }

    private static final int MSG_HISTORY = 0x2D;
    private static final int MSG_CUMU_PARAM = 0x2E;
    private static final int MSG_COST_RATE_PARAM = 0x2F;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_HISTORY) {
            if (mCallBack != null) {
                mCallBack.onJSQueryHistoryCount((QueryHistoryCount) msg.obj);
            }
        } else if (msg.what == MSG_CUMU_PARAM) {

            if (mCallBack != null) {
                mCallBack.onJSQueryCumuParam((String) msg.obj);
            }

        } else if (msg.what == MSG_COST_RATE_PARAM) {
            if (mCallBack != null) {
                mCallBack.onJSQueryCostRate((String) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
