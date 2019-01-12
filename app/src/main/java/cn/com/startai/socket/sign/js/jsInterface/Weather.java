package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
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
    public void positioningSwitchStatusRequest() {
        Tlog.v(TAG, " positioningSwitchStatusRequest ");
        getHandler().sendEmptyMessage(QUERY_LOCATION_ENABLED);
    }

    @JavascriptInterface
    public void positioningSwitchControlRequest(boolean state) {
        Tlog.v(TAG, " positioningSwitchStatusRequest " + state);

        getHandler().sendEmptyMessage(QUERY_ENABLE_LOCATION);
    }

    private static final int QUERY_WEATHER = 0x2D;
    private static final int QUERY_LOCATION_ENABLED = 0x2E;
    private static final int QUERY_ENABLE_LOCATION = 0x2F;

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
        } else {
            Tlog.e(TAG, NAME_JSI + " handleMessage unknown what:" + msg.what);
        }
    }

}
