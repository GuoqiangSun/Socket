package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import android.webkit.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.startai.socket.sign.scm.bean.Label;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/11 0011
 * desc :
 */
public class Weather extends AbsHandlerJsInterface {

    public interface IJSWeatherCallBack {

        void onJSQueryWeather();

        void onJSQueryLocationEnabled();

        void onJSEnableLocation();

        void onJSQueryWeatherByIp();

        void onJSQueryRunningTime(String mac);

        void onJSQueryOnlineRunningTime(String mac);

        void onJSQueryLabel(String mac);

        void onJSSetLabel(Label mLabel);
    }

    public static final class Method {

        private static final String QUERY_WEATHER =
                "javascript:localWeatherResponse('$lat','$lng','$province','$city','$district','$qlty','$tmp','$weather','$bgpic')";


//        lat	是	string	经度
//        lng	是	string	纬度
//        province	是	string	省份
//        city	是	string	城市
//        district	是	string	区
//        qlty	是	string	空气质量
//        tmp	是	string	温度
//        weather	是	string	天气
//        weatherPic	是	string	天气背景

        public static String callJsWeatherInfo(String lat, String lng,
                                               String province, String city, String district,
                                               String qlty, String tmp, String weather, String weatherPic) {
            return QUERY_WEATHER.replace("$lat", String.valueOf(lat))
                    .replace("$lng", String.valueOf(lng))
                    .replace("$province", String.valueOf(province))
                    .replace("$city", String.valueOf(city))
                    .replace("$district", String.valueOf(district))
                    .replace("$qlty", String.valueOf(qlty))
                    .replace("$tmp", String.valueOf(tmp))
                    .replace("$weather", String.valueOf(weather))
                    .replace("$bgpic", String.valueOf(weatherPic));
        }

        private static final String QUERY_WEATHER_2 =
                "javascript:localWeatherResponse($result,'$data')";


//        lat	是	string	经度
//        lng	是	string	纬度
//        province	是	string	省份
//        city	是	string	城市
//        district	是	string	区
//        qlty	是	string	空气质量
//        tmp	是	string	温度
//        weather	是	string	天气
//        weatherPic	是	string	天气背景

        public static String callJsWeatherInfo2(boolean result, String data) {
            return QUERY_WEATHER_2.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }


        private static final String QUERY_LOCATION =
                "javascript:positioningSwitchStatusResponse($result,$enable)";

        public static String callJsLocation(boolean result, boolean enabled) {
            return QUERY_LOCATION.replace("$result", String.valueOf(result))
                    .replace("$enable", String.valueOf(enabled));
        }

        private static final String QUERY_RUNNING_TIME =
                "runningTimeResponse($time,$result,'$mac')";

        public static String callJsQueryRunningTime(long time, boolean result, String mac) {
            return QUERY_RUNNING_TIME.replace("$time", String.valueOf(time))
                    .replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));
        }

        private static final String QUERY_ONLINE_RUNNING_TIME =
                "runningTimeOnlineResponse($time,$result,'$mac')";

        public static String callJsQueryOnlineRunningTime(long time, boolean result, String mac) {
            return QUERY_ONLINE_RUNNING_TIME.replace("$time", String.valueOf(time))
                    .replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));
        }

        private static final String QUERY_LABEl =
                "deviceLabelResponse('$mac','$label',$result)";

        public static String callJsQueryLabel(String mac, String label, boolean result) {
            return QUERY_LABEl.replace("$label", String.valueOf(label))
                    .replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));

        }

        private static final String SET_LABEl =
                "setDeviceLabelResponse('$mac','$label',$result)";

        public static String callJsSetLabel(String mac, String label, boolean result) {
            return SET_LABEl.replace("$label", String.valueOf(label))
                    .replace("$result", String.valueOf(result))
                    .replace("$mac", String.valueOf(mac));
        }
    }

    private final IJSWeatherCallBack mCallBack;

    public static final String NAME_JSI = "Weather";

    private String TAG = H5Config.TAG;

    public Weather(Looper mLooper, IJSWeatherCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void localWeatherRequest() {
        Tlog.v(TAG, " localWeatherRequest ");
        getHandler().sendEmptyMessage(QUERY_WEATHER);
    }

    @JavascriptInterface
    public void netWeatherRequest() {
        Tlog.v(TAG, " netWeatherRequest ");
        getHandler().sendEmptyMessage(QUERY_WEATHER_BY_IP);
    }

    @JavascriptInterface
    public void positioningSwitchStatusRequest() {
        Tlog.v(TAG, " positioningSwitchStatusRequest ");
        getHandler().sendEmptyMessage(QUERY_LOCATION_ENABLED);
    }

    @JavascriptInterface
    public void positioningSwitchControlRequest(boolean state) {
        Tlog.v(TAG, " positioningSwitchStatusRequest " + state);

        getHandler().sendEmptyMessage(QUERY_ENABLE_LOCATION);
    }


    @JavascriptInterface
    public void runningTimeRequest(String mac) {
        Tlog.v(TAG, " runningTimeRequest " + mac);

        getHandler().obtainMessage(QUERY_RUNNING_TIME, mac).sendToTarget();
    }


    @JavascriptInterface
    public void runningTimeOnlineRequest(String mac) {
        Tlog.v(TAG, " runningTimeOnlineRequest " + mac);

        getHandler().obtainMessage(QUERY_RUNNING_TIME_ONLINE, mac).sendToTarget();
    }

    @JavascriptInterface
    public void deviceLabelRequest(String mac) {
        Tlog.v(TAG, " deviceLabelRequest " + mac);

        getHandler().obtainMessage(QUERY_RUNNING_LABEL, mac).sendToTarget();
    }


    @JavascriptInterface
    public void setDeviceLabelRequest(String mac, String label) {
        Tlog.v(TAG, " setDeviceLabelRequest " + mac + ":" + label);
        Label ll = new Label();
        ll.mac = mac;
        ll.label = label;
        getHandler().obtainMessage(SET_LABEL, ll).sendToTarget();
    }

    private static final int QUERY_WEATHER = 0x2D;
    private static final int QUERY_LOCATION_ENABLED = 0x2E;
    private static final int QUERY_ENABLE_LOCATION = 0x2F;
    private static final int QUERY_WEATHER_BY_IP = 0x30;
    private static final int QUERY_RUNNING_TIME = 0x31;
    private static final int QUERY_RUNNING_TIME_ONLINE = 0x32;
    private static final int QUERY_RUNNING_LABEL = 0x33;
    private static final int SET_LABEL = 0x34;

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == QUERY_WEATHER) {
            if (mCallBack != null) {
                mCallBack.onJSQueryWeather();
            }
        } else if (msg.what == QUERY_LOCATION_ENABLED) {
            if (mCallBack != null) {
                mCallBack.onJSQueryLocationEnabled();
            }
        } else if (msg.what == QUERY_ENABLE_LOCATION) {
            if (mCallBack != null) {
                mCallBack.onJSEnableLocation();
            }
        } else if (msg.what == QUERY_WEATHER_BY_IP) {
            if (mCallBack != null) {
                mCallBack.onJSQueryWeatherByIp();
            }
        } else if (msg.what == QUERY_RUNNING_TIME) {
            if (mCallBack != null) {
                mCallBack.onJSQueryRunningTime((String) msg.obj);
            }
        } else if (msg.what == QUERY_RUNNING_TIME_ONLINE) {
            if (mCallBack != null) {
                mCallBack.onJSQueryOnlineRunningTime((String) msg.obj);
            }
        } else if (msg.what == QUERY_RUNNING_LABEL) {
            if (mCallBack != null) {
                mCallBack.onJSQueryLabel((String) msg.obj);
            }
        } else if (msg.what == SET_LABEL) {
            if (mCallBack != null) {
                mCallBack.onJSSetLabel((Label) msg.obj);
            }
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
