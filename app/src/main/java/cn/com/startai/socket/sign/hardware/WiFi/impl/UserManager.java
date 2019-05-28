package cn.com.startai.socket.sign.hardware.WiFi.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.JumpToBizProfile;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.startai.fssdk.FSDownloadCallback;
import cn.com.startai.fssdk.FSUploadCallback;
import cn.com.startai.fssdk.StartaiDownloaderManager;
import cn.com.startai.fssdk.StartaiUploaderManager;
import cn.com.startai.fssdk.db.entity.DownloadBean;
import cn.com.startai.fssdk.db.entity.UploadBean;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.BaseMessage;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8003;
import cn.com.startai.mqttsdk.busi.entity.C_0x8016;
import cn.com.startai.mqttsdk.busi.entity.C_0x8017;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.busi.entity.C_0x8021;
import cn.com.startai.mqttsdk.busi.entity.C_0x8022;
import cn.com.startai.mqttsdk.busi.entity.C_0x8023;
import cn.com.startai.mqttsdk.busi.entity.C_0x8024;
import cn.com.startai.mqttsdk.busi.entity.C_0x8025;
import cn.com.startai.mqttsdk.busi.entity.C_0x8027;
import cn.com.startai.mqttsdk.busi.entity.C_0x8033;
import cn.com.startai.mqttsdk.busi.entity.C_0x8034;
import cn.com.startai.mqttsdk.busi.entity.C_0x8035;
import cn.com.startai.mqttsdk.busi.entity.C_0x8036;
import cn.com.startai.mqttsdk.busi.entity.C_0x8037;
import cn.com.startai.mqttsdk.busi.entity.type.Type;
import cn.com.startai.mqttsdk.control.AreaConfig;
import cn.com.startai.mqttsdk.control.entity.AreaLocation;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.localbusi.SUserManager;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;
import cn.com.startai.scansdk.ChargerScanActivity;
import cn.com.startai.socket.db.gen.JsUserInfoDao;
import cn.com.startai.socket.db.gen.JsWeatherInfoDao;
import cn.com.startai.socket.db.gen.UserInfoDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.global.LoginHelp;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.global.WXLoginHelper;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.bean.JsUserInfo;
import cn.com.startai.socket.mutual.js.bean.JsWeatherInfo;
import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.ThirdLoginUser;
import cn.com.startai.socket.mutual.js.bean.UpdateProgress;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.impl.AndJsBridge;
import cn.com.startai.socket.sign.hardware.IControlWiFi;
import cn.com.startai.socket.sign.hardware.WiFi.bean.UserInfo;
import cn.com.startai.socket.sign.hardware.WiFi.util.AuthResult;
import cn.com.startai.socket.sign.hardware.WiFi.util.DownloadTask;
import cn.com.startai.socket.sign.hardware.WiFi.util.NetworkData;
import cn.com.startai.socket.sign.js.jsInterface.Login;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.permission.PermissionGroup;
import cn.com.swain.baselib.permission.PermissionHelper;
import cn.com.swain.baselib.permission.PermissionRequest;
import cn.com.swain.baselib.util.PhotoUtils;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/26 0026
 * desc :
 */
public class UserManager implements IService {

    public static final String TAG = "UserManager";

    private Application app;

    UserManager(Application app) {
        this.app = app;
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Tlog.e(TAG, " onLocationChanged " + String.valueOf(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Tlog.e(TAG, " onStatusChanged " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Tlog.e(TAG, " onProviderEnabled " + provider);
            if (mWorkHandler != null && !mWorkHandler.hasMessages(MSG_WHAT_LOCATION_ANDROID)) {
                mWorkHandler.sendEmptyMessageDelayed(MSG_WHAT_LOCATION_ANDROID, 1000);
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Tlog.e(TAG, " onProviderDisabled " + provider);

        }
    };

    private final GpsStatus.Listener gpsLsn = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {

            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Tlog.i(TAG, "第一次定位");

                    if (mWorkHandler != null && !mWorkHandler.hasMessages(MSG_WHAT_LOCATION_ANDROID)) {
                        mWorkHandler.sendEmptyMessageDelayed(MSG_WHAT_LOCATION_ANDROID, 1000);
                    }

                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Tlog.i(TAG, "卫星状态改变");


                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Tlog.i(TAG, "定位启动");


                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Tlog.i(TAG, "定位结束");


                    break;
            }
        }
    };


    private LocationManager locationManager;


    private Handler mWorkHandler;

    private int requestLocationTimes = 1;

    private final int MAX_REQUEST_LOCATION_TIMES = 4;

    private final int MAX_REQUEST_LOCATION_TIMES_ONLY = 3;

    private String locationProvider;

    private void resetRequestLocation() {
        this.requestLocationTimes = 1;
        this.locationProvider = null;
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }


    private synchronized String checkLocationProvider() {

        if (locationProvider == null) {

            String bestProvider = null;
            if (locationManager != null) {
                bestProvider = locationManager.getBestProvider(getCriteria(), true);
                Tlog.w(TAG, " checkLocationProvider getBestProvider " + bestProvider);
            }
            locationProvider = bestProvider;

            if (locationProvider == null) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }

        }

        return locationProvider;
    }


    private static final int MSG_WHAT_LOCATION_ANDROID = 0x00;

    private static final int MSG_WHAT_LOCATION_IP = 0x01;

    @SuppressLint("MissingPermission")
    @Override
    public void onSCreate() {

        if (CustomManager.getInstance().isAirtempNBProjectTest()) {
            setLoginUserID(NetworkData.USERID_DEFAULT);

        } else {
            setLoginUserID(getLastLoginUserID());
        }

        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);

        if (CustomManager.getInstance().isMUSIK()) {
            //监视地理位置变化
            if (PermissionHelper.isGranted(app, Manifest.permission.ACCESS_FINE_LOCATION)
                    && PermissionHelper.isGranted(app, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                locationManager.addGpsStatusListener(gpsLsn);
            }
        }

        mWorkHandler = new Handler(LooperManager.getInstance().getRepeatLooper()) {

            @SuppressLint("MissingPermission")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_WHAT_LOCATION_ANDROID) {

                    boolean providerEnabledGps = locationManager != null
                            && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean providerEnabledNet = locationManager != null
                            && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    boolean onlyOne = (providerEnabledGps && !providerEnabledNet) || (!providerEnabledGps && providerEnabledNet);

                    if (!(providerEnabledGps || providerEnabledNet)) {
                        resetRequestLocation();

                        Tlog.e(TAG, " getLastKnownLocation location not enabled ");

                        if (mWorkHandler != null) {
                            mWorkHandler.sendEmptyMessage(MSG_WHAT_LOCATION_IP);
                        }

                        return;
                    }

                    //获取所有可用的位置提供器
//                    List<String> providers = locationManager.getProviders(true);

                    String provider = checkLocationProvider();

                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location == null) {

                        // 获取不到location 切换下
                        if (LocationManager.NETWORK_PROVIDER.equalsIgnoreCase(provider)) {
                            if (providerEnabledGps) {
                                provider = LocationManager.GPS_PROVIDER;
                            }
                        } else {
                            if (providerEnabledNet) {
                                provider = LocationManager.NETWORK_PROVIDER;
                            }
                        }

                        Tlog.e(TAG, " location == null  change provider:" + provider);
                        ++requestLocationTimes;
                        if (requestLocationTimes > MAX_REQUEST_LOCATION_TIMES
                                || (onlyOne && requestLocationTimes > MAX_REQUEST_LOCATION_TIMES_ONLY)) {

                            Tlog.e(TAG, " requestLocationUpdates times > MAX_REQUEST_LOCATION_TIMES ");

                            resetRequestLocation();

                            if (locationManager != null) {
                                locationManager.removeUpdates(locationListener);
                            }

                            if (mWorkHandler != null) {
                                mWorkHandler.sendEmptyMessage(MSG_WHAT_LOCATION_IP);
                            }

                            return;
                        }

                        locationProvider = provider;
                        Tlog.d(TAG, " requestLocationUpdates provider: " + provider);
                        locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);

                        if (mWorkHandler != null) {
                            mWorkHandler.sendEmptyMessageDelayed(MSG_WHAT_LOCATION_ANDROID, 1000 * 3);
                        }

                    } else {
                        if (locationManager != null) {
                            locationManager.removeUpdates(locationListener);
                        }
                        String lat = String.valueOf(location.getLatitude());
                        String lng = String.valueOf(location.getLongitude());

                        Tlog.d(TAG, "getWeatherInfo lat:" + lat + " lng:" + lng);

                        C_0x8035.Req.ContentBean req = new C_0x8035.Req.ContentBean(lat, lng);
                        StartAI.getInstance().getBaseBusiManager().getWeatherInfo(req, new IOnCallListener() {
                            @Override
                            public void onSuccess(MqttPublishRequest request) {
                                Tlog.e(TAG, " getWeatherInfo msg send success ");
                            }

                            @Override
                            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                                Tlog.e(TAG, " getWeatherInfo msg send fail " + startaiError.getErrorCode());
                                if (mResultCallBack != null) {
                                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                                }
                            }

                        });

                    }

                } else if (msg.what == MSG_WHAT_LOCATION_IP) {

                    new GetLatLngTask(mResultCallBack).execute();

                }

            }
        };

    }

    private static final int REQUEST_SCAN_QR = 0x3593;


    public void scanQRCode(Activity act) {

        ChargerScanActivity.showActivityForResult(act, REQUEST_SCAN_QR);

    }


    //拨打电话（跳转到拨号界面，用户手动点击拨打）
    public void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        mResultCallBack.onResultStartActivityForResult(intent, -1);


        mResultCallBack.onResultCallPhone(true);
    }


    private LoginHelp mLoginHelp;

    private synchronized LoginHelp getLoginHelp() {

        if (mLoginHelp == null) {
            mLoginHelp = LoginHelp.getInstance();
            mLoginHelp.regLoginCallBack(new LoginHelp.OnLoginResult() {
                @Override
                public void onResult(boolean result, ThirdLoginUser mUser) {

                    if (result) {
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(true, "");
                        }
                    } else {
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(false, "");
                        }
                    }


                }

                @Override
                public void onFacebookResult(boolean result, JSONObject object) {
                    if (!result) {
                        Tlog.e(TAG, " onFacebookResult fail ");
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(false, "");
                        }
                        return;
                    }
                    C_0x8027.Req.ContentBean mBean = new C_0x8027.Req.ContentBean();
                    mBean.fromFacebookJSONObject(object);
                    StartAI.getInstance().getBaseBusiManager().loginWithThirdAccount(mBean, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {
                            Tlog.w(TAG, " login facebook msg send success  ");
                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                            Tlog.e(TAG, " login facebook msg send fail ");
                            if (mResultCallBack != null) {
                                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                            }
                        }

                    });
                }

                @Override
                public void onFacebookBindResult(boolean result, JSONObject object) {
                    if (!result) {
                        Tlog.e(TAG, " onFacebookBindResult fail ");
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(false, "");
                        }
                        return;
                    }
                    C_0x8037.Req.ContentBean contentBean = new C_0x8037.Req.ContentBean();
                    contentBean.fromFacebookJSONObject(object);
                    StartAI.getInstance().getBaseBusiManager().bindThirdAccount(contentBean, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {
                            Tlog.w(TAG, " bind facebook msg send success  ");
                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                            Tlog.e(TAG, " login facebook msg send fail ");
                            if (mResultCallBack != null) {
                                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                            }
                        }
                    });

                }

                @Override
                public void onGoogleResult(boolean result, GoogleSignInAccount account) {
                    if (!result || account == null) {
                        Tlog.e(TAG, " onGoogleResult fail ");
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(false, "");
                        }
                        return;
                    }
                    C_0x8027.Req.ContentBean mBean = new C_0x8027.Req.ContentBean();
                    C_0x8027.Req.ContentBean.UserinfoBean userinfo = new C_0x8027.Req.ContentBean.UserinfoBean();
                    userinfo.setUnionid(account.getId());
                    userinfo.setOpenid(account.getId());
                    userinfo.setNickname(account.getDisplayName());
                    userinfo.setLastName(account.getFamilyName());
                    userinfo.setFirstName(account.getGivenName());
                    Uri photoUrl = account.getPhotoUrl();
                    userinfo.setHeadimgurl(photoUrl != null ? photoUrl.toString() : null);
                    mBean.setUserinfo(userinfo);
                    mBean.setType(C_0x8027.THIRD_GOOGLE);

//                    StartAI.getInstance().getBaseBusiManager().bindThirdAccount();

                    StartAI.getInstance().getBaseBusiManager().loginWithThirdAccount(mBean, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {
                            Tlog.w(TAG, " login google msg send success  ");
                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                            Tlog.e(TAG, " login google msg send fail ");
                            if (mResultCallBack != null) {
                                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                            }
                        }

                    });
                }

                @Override
                public void onGoogleBindResult(boolean result, GoogleSignInAccount account) {
                    if (!result || account == null) {
                        Tlog.e(TAG, " onGoogleBindResult fail ");
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultThirdLogin(false, "");
                        }
                        return;
                    }
                    C_0x8037.Req.ContentBean mBean = new C_0x8037.Req.ContentBean();
                    C_0x8037.Req.ContentBean.UserinfoBean userinfo = new C_0x8037.Req.ContentBean.UserinfoBean();
                    userinfo.setUnionid(account.getId());
                    userinfo.setOpenid(account.getId());
                    userinfo.setNickname(account.getDisplayName());
                    userinfo.setLastName(account.getFamilyName());
                    userinfo.setFirstName(account.getGivenName());
                    Uri photoUrl = account.getPhotoUrl();
                    userinfo.setHeadimgurl(photoUrl != null ? photoUrl.toString() : null);
                    mBean.setUserinfo(userinfo);
                    mBean.setType(C_0x8027.THIRD_GOOGLE);

                    StartAI.getInstance().getBaseBusiManager().bindThirdAccount(mBean, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {
                            Tlog.w(TAG, " bind google msg send success  ");
                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                            Tlog.e(TAG, " bind google msg send fail ");
                            if (mResultCallBack != null) {
                                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                            }
                        }
                    });

                }
            });

        }
        return mLoginHelp;
    }


    public void skipWiFi(Activity act) {

        try {
            act.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        } catch (Exception e) {
            try {
                act.startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception ee) {

            }
        }
        if (mResultCallBack != null) {
            mResultCallBack.onResultSkipWiFi(true);
        }
    }

    public void thirdBind(String type, Activity act) {

        if (act == null) {
            Tlog.e(TAG, " login act == null ");
            return;
        }

        LoginHelp loginHelp = getLoginHelp();

        if (type.equalsIgnoreCase(Login.TYPE_LOGIN_FACEBOOK)) {

            loginHelp.bindFacebook(act);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_GOOGLE)) {

            loginHelp.bindGoogle(act);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_TWITTER)) {

            loginHelp.loginTwitter(act);

        } else {
            Tlog.e(TAG, " login unknown type ");
        }

    }


    public void thirdLogin(Activity act, String type) {

        if (act == null) {
            Tlog.e(TAG, " login act == null ");
            return;
        }

        LoginHelp loginHelp = getLoginHelp();

        if (type.equalsIgnoreCase(Login.TYPE_LOGIN_FACEBOOK)) {

            loginHelp.loginFacebook(act);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_GOOGLE)) {

            loginHelp.loginGoogle(act);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_TWITTER)) {

            loginHelp.loginTwitter(act);

        } else {
            Tlog.e(TAG, " login unknown type ");
        }

    }


    private static class GetLatLngTask extends AsyncTask<Void, Void, Void> {

        private IControlWiFi.IWiFiResultCallBack mResultCallBack;

        private GetLatLngTask(IControlWiFi.IWiFiResultCallBack mResultCallBack) {
            this.mResultCallBack = mResultCallBack;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //@see http://lbsyun.baidu.com/index.php?title=webapi/ip-api

            String ip = "http://api.map.baidu.com/location/ip?ak=zm1k2HTK57Rg6dRpl8MTOcCeXXNurFwb&coor=bd09ll&qq-pf-to=pcqq.group";

            try {

                URL url = new URL(ip);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10 * 1000);

                BufferedInputStream bis = null;
                if (200 == conn.getResponseCode()) {
                    InputStream inputStream = conn.getInputStream();
                    bis = new BufferedInputStream(inputStream);

                    int read = -1;
                    byte[] data = new byte[512];

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    while ((read = bis.read(data)) != -1) {

                        bos.write(data, 0, read);

                    }

                    if (bos.size() > 0) {
                        byte[] bytes = bos.toByteArray();
                        String responseMsg = new String(bytes);
                        Tlog.v(TAG, " GetLatLngTask : " + responseMsg);

                        try {
                            JSONObject obj = new JSONObject(responseMsg);

                            int status = obj.getInt("status");
                            Tlog.d(TAG, " status : " + status);

                            if (status == 0) {

                                String address = obj.getString("address");
                                Tlog.d(TAG, " address : " + address);

                                JSONObject content = obj.getJSONObject("content");

                                String addressContent = content.getString("address");
                                Tlog.d(TAG, " Content address : " + addressContent);

                                JSONObject address_detail = content.getJSONObject("address_detail");
                                String city = address_detail.getString("city");
                                Tlog.d(TAG, " city : " + city);
                                String city_code = address_detail.getString("city_code");
                                Tlog.d(TAG, " city_code : " + city_code);
                                String district = address_detail.getString("district");
                                Tlog.d(TAG, " district : " + district);
                                String province = address_detail.getString("province");
                                Tlog.d(TAG, " province : " + province);
                                String street = address_detail.getString("street");
                                Tlog.d(TAG, " street : " + street);
                                String street_number = address_detail.getString("street_number");
                                Tlog.d(TAG, " street_number : " + street_number);

                                JSONObject point = content.getJSONObject("point");

                                String x = point.getString("x");
                                String y = point.getString("y");
                                Tlog.d(TAG, " x : " + x + " y:" + y);

                                C_0x8035.Req.ContentBean req = new C_0x8035.Req.ContentBean(y, x);
                                StartAI.getInstance().getBaseBusiManager().getWeatherInfo(req, new IOnCallListener() {
                                    @Override
                                    public void onSuccess(MqttPublishRequest request) {
                                        Tlog.e(TAG, " getWeatherInfo msg send success ");
                                    }

                                    @Override
                                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                                        Tlog.e(TAG, " getWeatherInfo msg send fail " + startaiError.getErrorCode());
                                        if (mResultCallBack != null) {
                                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                                            mResultCallBack = null;
                                        }
                                    }

                                });


                            } else {
                                AreaLocation area = AreaConfig.getArea();
                                C_0x8035.Req.ContentBean req =
                                        new C_0x8035.Req.ContentBean(
                                                String.valueOf(area.getLat()),
                                                String.valueOf(area.getLon()));
                                Tlog.i(TAG, " AreaConfig.getArea() city:" + area.getCity()
                                        + " country:" + area.getCountry()
                                        + " lat:" + area.getLat()
                                        + " lng:" + area.getLon());
                                StartAI.getInstance().getBaseBusiManager().getWeatherInfo(req, new IOnCallListener() {
                                    @Override
                                    public void onSuccess(MqttPublishRequest request) {
                                        Tlog.e(TAG, " getWeatherInfo msg send success ");
                                    }

                                    @Override
                                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                                        Tlog.e(TAG, " getWeatherInfo msg send fail " + startaiError.getErrorCode());
                                        if (mResultCallBack != null) {
                                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                                            mResultCallBack = null;
                                        }
                                    }

                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Tlog.w(TAG, "GetLatLngTask JSONException ", e);
                        }


                    }

                }

                if (bis != null) {
                    bis.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Tlog.w(TAG, "GetLatLngTask JSONException ", e);
            }

            return null;
        }
    }

    @Override
    public void onSResume() {

    }

    @Override
    public void onSPause() {

    }

    @Override
    public void onSDestroy() {

    }

    @Override
    public void onSFinish() {

    }

    private volatile String mUserID;

    private synchronized void setLoginUserID(String userID) {
        Tlog.e(TAG, " setLoginUserID " + userID);
        this.mUserID = userID;
    }

    synchronized String getLoginUserID() {
//        return "b7e3c580dffe4f63";
        return mUserID;
    }

    private synchronized String getLastLoginUserID() {
        return SUserManager.getInstance().getUserId();
    }

    private synchronized String getLastLoginUserID1() {
        String lastLoginUser = NetworkData.getLocalData(app).getLastLoginUser("");

        boolean loginExpire = false; // 登录失效
        if (lastLoginUser == null || lastLoginUser.trim().equalsIgnoreCase("")) {
            lastLoginUser = getLoginUserID();
            if (lastLoginUser == null || lastLoginUser.equalsIgnoreCase("")) {
                loginExpire = true;
            }
        }

        if (!loginExpire) {
            UserInfoDao userInfoDao = DBManager.getInstance().getDaoSession().getUserInfoDao();
            List<UserInfo> list = userInfoDao.queryBuilder().where(UserInfoDao.Properties.Mid.eq(lastLoginUser)).list();

            loginExpire = list == null || list.size() <= 0;

            if (!loginExpire) {
                UserInfo userInfo = list.get(0);
                long expire_in = userInfo.getExpire_in();
                long lastLoginTime = userInfo.getLastLoginTime();
                long diff = Math.abs(System.currentTimeMillis() - lastLoginTime) / 1000;
                loginExpire = diff >= expire_in;
//                Tlog.e(TAG, " lastLoginTime " + lastLoginTime + " System.currentTimeMillis() " + System.currentTimeMillis());
                Tlog.e(TAG, "getLastLoginUserID() diff " + diff + " expire_in " + expire_in);

            } else {
                Tlog.e(TAG, " not have login user info");
            }

        }

        if (!loginExpire) {
            Tlog.e(TAG, " last login userInfo " + lastLoginUser);
            return lastLoginUser;
        }
        return null;

    }

    private final IOnCallListener mGetUserInfoLsn = new IOnCallListener() {

        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " getUserInfo msg send success ");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " mGetUserInfoLsn msg send fail " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }

        }

    };

    void getUserInfo() {

        StartAI.getInstance().getBaseBusiManager().getUserInfo(mGetUserInfoLsn);

    }

    void skipWxBiz() {

        IWXAPI wxApi = WXLoginHelper.getInstance().getWXApi(app);

        if (wxApi == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_UNKNOWN);
            }
            return;
        }

        if (wxApi.isWXAppInstalled()) {
            JumpToBizProfile.Req req = new JumpToBizProfile.Req();
            req.toUserName = "StartAI会员中心";
            req.extMsg = "";
            req.profileType = JumpToBizProfile.JUMP_TO_NORMAL_BIZ_PROFILE;
            wxApi.sendReq(req);

        } else {

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_NO_CLIENT);
            }

        }
    }

    private void getUserInfoFromDao() {

        String loginUserID = getLoginUserID();
        if (loginUserID != null) {
            JsUserInfoDao jsUserInfoDao = DBManager.getInstance().getDaoSession().getJsUserInfoDao();
            List<JsUserInfo> list = jsUserInfoDao.queryBuilder().where(JsUserInfoDao.Properties.Userid.eq(loginUserID)).list();

            JsUserInfo mUserInfo = null;
            if (list.size() > 0) {
                mUserInfo = list.get(0);
            }

            if (mUserInfo != null) {

                Tlog.d(TAG, " getUserInfoFromDao : " + mUserInfo.toJsonStr());

                if (mResultCallBack != null) {
                    mResultCallBack.onResultGetUserInfo(true, mUserInfo);
                }

            }

        }

    }

    void resendEmail(String email) {
        StartAI.getInstance().getBaseBusiManager().sendEmail(email, 1, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                Tlog.v(TAG, " resendEmail msg send success ");
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                Tlog.e(TAG, " resendEmail msg send fail " + startaiError.getErrorCode());
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });
    }

    void emailForgot(String email) {
        StartAI.getInstance().getBaseBusiManager().sendEmail(email, 2, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                Tlog.v(TAG, " emailForgot msg send success ");
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                Tlog.e(TAG, " emailForgot msg send fail " + startaiError.getErrorCode());
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });
    }

    public void queryLocationEnabled() {
        boolean providerEnabledGps = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean providerEnabledNet = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Tlog.d(TAG, " queryLocationEnabled providerEnabledGps :" + providerEnabledGps + " providerEnabledNet:" + providerEnabledNet);
        if (mResultCallBack != null) {
            mResultCallBack.onResultLocationEnabled(providerEnabledGps || providerEnabledNet);
        }

    }

    public void enableLocation() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        if (mResultCallBack != null) {
            mResultCallBack.onResultStartActivityForResult(i, REUQEST_LOCATION);
        }
    }

    void requestWeatherByIp() {
        Tlog.v(TAG, " requestWeatherByIp()  ");
        if (mWorkHandler != null) {
            if (mWorkHandler.hasMessages(MSG_WHAT_LOCATION_IP)) {
                mWorkHandler.removeMessages(MSG_WHAT_LOCATION_IP);
            }
            mWorkHandler.sendEmptyMessage(MSG_WHAT_LOCATION_IP);
        }
    }

    private void getWeatherFromDao() {

        JsWeatherInfoDao jsWeatherInfoDao = DBManager.getInstance().getDaoSession().getJsWeatherInfoDao();
        List<JsWeatherInfo> list = jsWeatherInfoDao.queryBuilder().list();

        if (list.size() > 0) {
            JsWeatherInfo jsWeatherInfo = list.get(0);
            if (mResultCallBack != null) {
                mResultCallBack.onResultWeatherInfo(jsWeatherInfo);
            }
        }

    }

    private static final int REUQEST_LOCATION = 0x6598;

    void requestWeather() {

        Tlog.v(TAG, " requestWeather()  ");
        resetRequestLocation();

        //获取Location

        PermissionHelper.requestSinglePermission(app, new PermissionRequest.OnPermissionResult() {

            @Override
            public boolean onPermissionRequestResult(String permission, boolean granted) {

                Tlog.v(TAG, " requestWeather() PermissionHelper : " + permission + " granted:" + granted);

                if (granted) {

                    if (mWorkHandler != null) {
                        if (mWorkHandler.hasMessages(MSG_WHAT_LOCATION_ANDROID)) {
                            mWorkHandler.removeMessages(MSG_WHAT_LOCATION_ANDROID);
                        }
                        mWorkHandler.sendEmptyMessage(MSG_WHAT_LOCATION_ANDROID);
                    }

                }

                return true;

            }
        }, PermissionGroup.LOCATION);


    }

    void isLogin() {

        String loginUserID = getLoginUserID();

        boolean isLogin = getLoginUserID() != null;

        Tlog.e(TAG, " isLogin " + isLogin + " getLoginUserID:" + loginUserID);

        if (mResultCallBack != null) {
            mResultCallBack.onResultIsLogin(isLogin);
        }

        if (isLogin) {
            getUserInfoFromDao();
        }

        if (CustomManager.getInstance().isMUSIK()) {
            getWeatherFromDao();
        }

    }

    public void bindPhone(MobileBind mMobileBind) {
        //发送请求 接口调用前需要先 调用 获取验证码，检验验证码
        StartAI.getInstance().getBaseBusiManager().checkIdentifyCode(mMobileBind.phone, mMobileBind.code,
                Type.CheckIdentifyCode.BIND_MOBILE_NUM, new IOnCallListener() {
                    @Override
                    public void onSuccess(MqttPublishRequest request) {
                        if (Debuger.isLogDebug) {
                            Tlog.v(TAG, "bind phone checkIdentifyCode msg send success ");
                        }
                    }

                    @Override
                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                        if (Debuger.isLogDebug) {
                            Tlog.e(TAG, "bind phone checkIdentifyCode msg send fail " + String.valueOf(startaiError));
                        }
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                        }
                    }

                });

    }

    void bindAli() {
        StartAI.getInstance().getBaseBusiManager().getAlipayAuthInfo(C_0x8033.AUTH_TYPE_AUTH, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                if (Debuger.isLogDebug) {
                    Tlog.v(TAG, "getAlipayAuthInfo bind msg send success ");
                }
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                if (Debuger.isLogDebug) {
                    Tlog.e(TAG, "getAlipayAuthInfo bind msg send fail " + String.valueOf(startaiError));
                }
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });
    }

    public void aliLogin() {

        StartAI.getInstance().getBaseBusiManager().getAlipayAuthInfo(C_0x8033.AUTH_TYPE_LOGIN, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                if (Debuger.isLogDebug) {
                    Tlog.v(TAG, "getAlipayAuthInfo login msg send success ");
                }
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                if (Debuger.isLogDebug) {
                    Tlog.e(TAG, "getAlipayAuthInfo login msg send fail " + String.valueOf(startaiError));
                }
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });

    }

    private final String WX_LOGIN = "diandi_wx_login";
    private final String WX_BIND = "diandi_wx_bind";


    void wxLogin() {

        IWXAPI wxApi = WXLoginHelper.getInstance().getWXApi(app);
        if (wxApi == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_UNKNOWN);
            }
            return;
        }

        if (wxApi.isWXAppInstalled()) {

            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = WX_LOGIN;
            //向微信发送请求
            Tlog.v(TAG, " wxApi  sendReq ");
            wxApi.sendReq(req);

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_NO_CLIENT);
            }
        }

    }


    public void bindWX() {

        IWXAPI wxApi = WXLoginHelper.getInstance().getWXApi(app);

        if (wxApi == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_UNKNOWN);
            }
        } else {

            if (!wxApi.isWXAppInstalled()) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_NO_CLIENT);
                }
            } else {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = WX_BIND;
                //向微信发送请求
                Tlog.v(TAG, " wxApi  sendReq ");
                wxApi.sendReq(req);
            }

        }

    }

    public void unbindWx() {

        //发送请求
        C_0x8036.Req.ContentBean req = new C_0x8036.Req.ContentBean(getLoginUserID(), C_0x8036.THIRD_WECHAT);
        //解绑 微信
        StartAI.getInstance().getBaseBusiManager().unBindThirdAccount(req, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                Tlog.v(TAG, " unbindWx msg send success ");
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                Tlog.e(TAG, " unbindWx msg send fail " + startaiError.getErrorCode());
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });
    }

    public void unbindAli() {
        //发送请求
        C_0x8036.Req.ContentBean req = new C_0x8036.Req.ContentBean(getLoginUserID(), C_0x8036.THIRD_ALIPAY);
        //解绑 微信
        StartAI.getInstance().getBaseBusiManager().unBindThirdAccount(req, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
                Tlog.v(TAG, " unbindAli msg send success ");
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                Tlog.e(TAG, " unbindAli msg send fail " + startaiError.getErrorCode());
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                }
            }

        });
    }


    public void onWxLoginResult(BaseResp baseResp) {

        Tlog.d(TAG, "onWxLoginResult : BaseResp-" + String.valueOf(baseResp));

        if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {

            String state = ((SendAuth.Resp) baseResp).state;
            String code = ((SendAuth.Resp) baseResp).code;

            if (WX_LOGIN.equalsIgnoreCase(state)) {
                Tlog.e(TAG, "onWxLoginSuccess code: " + code);

                StartAI.getInstance().getBaseBusiManager().loginWithThirdAccount(10, code, new IOnCallListener() {
                    @Override
                    public void onSuccess(MqttPublishRequest request) {
                        Tlog.v(TAG, " wxLogin msg send success ");
                    }

                    @Override
                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                        Tlog.e(TAG, " wxLogin msg send fail " + startaiError.getErrorCode());
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                        }
                    }

                });

            } else if (WX_BIND.equalsIgnoreCase(state)) {

                Tlog.e(TAG, "onWxBindSuccess code: " + code);

                //发送请求 接口调用前需要调用 微信的第三方登录SDK 授权api 拿到 code

                C_0x8037.Req.ContentBean req = new C_0x8037.Req.ContentBean();
                req.setCode(code); //code 来自微信授权返回
                req.setType(C_0x8037.THIRD_WECHAT); //绑定微信账号

                StartAI.getInstance().getBaseBusiManager().bindThirdAccount(req, new IOnCallListener() {
                    @Override
                    public void onSuccess(MqttPublishRequest mqttPublishRequest) {
                        Tlog.i(TAG, "bindThirdAccount wx msg send success ");
                    }

                    @Override
                    public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
                        if (Debuger.isDebug) {
                            Tlog.e(TAG, "bindThirdAccount wx msg send fail " + String.valueOf(startaiError));
                        }
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                        }
                    }

                });

            }

        } else {

            if (baseResp instanceof SendAuth.Resp) {
                String state = ((SendAuth.Resp) baseResp).state;
                Tlog.e(TAG, " user rejection wx :" + state);
            }

            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Tlog.e(TAG, " user rejection wx login");
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_USER_REJECTION);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Tlog.e(TAG, " user cancel wx login ");
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_USER_CANCEL);
                    }
                    break;
                default:
                    Tlog.e(TAG, " wx login fail errorCode: " + baseResp.errCode);
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_UNKNOWN);
                    }
                    break;
            }


        }

    }

    /**
     * wx登录错误 unknown
     */
    public static final String ERROR_CODE_WX_LOGIN_UNKNOWN = "0x830399";
    /**
     * wx登录没有客户端
     */
    public static final String ERROR_CODE_WX_LOGIN_NO_CLIENT = "0x830398";

    /**
     * wx登录用户取消
     */
    public static final String ERROR_CODE_WX_LOGIN_USER_CANCEL = "0x830397";

    /**
     * wx登录用户拒接
     */
    public static final String ERROR_CODE_WX_LOGIN_USER_REJECTION = "0x830396";

    private final IOnCallListener mGetLoginCodeLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " mGetLoginCodeLsn msg send success:");

        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " mGetLoginCodeLsn msg send failed: " + startaiError.getErrorCode());
            if (mResultCallBack != null) {
                mResultCallBack.onResultGetMobileLoginCode(false, 1);
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };

    private final IOnCallListener mGetBindCodeLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " mGetBindCodeLsn msg send success:");

        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " mGetBindCodeLsn msg send failed: " + startaiError.getErrorCode());
            if (mResultCallBack != null) {
                mResultCallBack.onResultGetMobileLoginCode(false, 5);
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };

    void getMobileLoginCode(String phone, int type) {
        Tlog.e(TAG, " getMobileLoginCode " + phone + " type:" + type);

        IOnCallListener callBack;
        if (type == 1) {
            callBack = mGetLoginCodeLsn;
        } else if (type == 5) {
            callBack = mGetBindCodeLsn;
        } else {
            callBack = mGetLoginCodeLsn;
        }

        StartAI.getInstance().getBaseBusiManager().getIdentifyCode(phone, type, callBack);

    }


    private final IOnCallListener mMobileLoginLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " mobile login msg send success ");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " mobile login msg send failed " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }

        }

    };

    void loginMobile(MobileLogin mLogin) {
        Tlog.v(TAG, " loginMobile " + mLogin.phone + " " + mLogin.code);
        StartAI.getInstance().getBaseBusiManager().login(mLogin.phone, "", mLogin.code, mMobileLoginLsn);
    }


    void loginOut() {

        String loginUserID = getLoginUserID();

        if (loginUserID == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultLogout(false);
            }
            return;
        }

        Tlog.e(TAG, " loginOut  " + loginUserID);
        StartAI.getInstance().getBaseBusiManager().logout();

        UserInfoDao userInfoDao = DBManager.getInstance().getDaoSession().getUserInfoDao();
        List<UserInfo> list = userInfoDao.queryBuilder().where(UserInfoDao.Properties.Mid.eq(loginUserID)).list();
        if (list.size() > 0) {
            UserInfo userInfo = list.get(0);
            userInfo.setExpire_in(0);
            userInfoDao.update(userInfo);
        }
    }


    private final IOnCallListener mEmailLoginLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " email login msg send success:");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {

            Tlog.e(TAG, " email login msg send failed: " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };

    public void emailLogin(MobileLogin mLogin) {

        if (mLogin == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError("0x801806");
            }
            return;
        }

        Tlog.v(TAG, " emailLogin  " + mLogin.email + " " + mLogin.emailPwd);
        StartAI.getInstance().getBaseBusiManager().login(mLogin.email, mLogin.emailPwd, "", mEmailLoginLsn);
    }


    private final IOnCallListener mEmailRegisterLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " email register msg send success:");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {

            Tlog.e(TAG, " email register msg send failed: " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };


    void emailRegister(UserRegister obj) {

        if (obj == null) {

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError("0x801806");
            }
            return;
        }

        Tlog.v(TAG, " emailRegister  " + obj.email + ";" + obj.pwd + ";" + obj.username);
        StartAI.getInstance().getBaseBusiManager().register(obj.email, obj.pwd, mEmailRegisterLsn);
    }


    private final IOnCallListener mUpdatePwdLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " mUpdatePwdLsn msg send success  ");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " mUpdatePwdLsn msg send fail " + startaiError.getErrorCode());
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };

    void updateUserPwd(UserUpdateInfo mPwd) {
        Tlog.v(TAG, "updateUserPwd() old:" + mPwd.oldPwd + " new:" + mPwd.newPwd);
        StartAI.getInstance().getBaseBusiManager().updateUserPwd(mPwd.oldPwd, mPwd.newPwd, mUpdatePwdLsn);
    }

    public void updateNickName(String nickName) {

        C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
        contentBean.setNickName(nickName);
        StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, mUpdateNameLsn);
    }

    private final IOnCallListener mUpdateNameLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };


    void updateUserName(UserUpdateInfo obj) {

        if (obj == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(StartaiError.ERROR_SEND_UNKOWN));
            }
            Tlog.e(TAG, "updateUserName() UserUpdateInfo is null ");
            return;
        }

        Tlog.v(TAG, "updateUserName() :" + obj.surname + " " + obj.name);

        C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();

        if ("surnam".equalsIgnoreCase(obj.surname)) {
            contentBean.setLastName(obj.name);
        } else if ("name".equalsIgnoreCase(obj.surname)) {
            contentBean.setFirstName(obj.name);
        } else {

            Tlog.e(TAG, "updateUserName() UserUpdateInfo surname invalid " + obj.surname);

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(StartaiError.ERROR_SEND_PARAM_INVALIBLE));
            }
            return;
        }

        contentBean.setUserid(getLoginUserID());
        StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, mUpdateNameLsn);
    }

    private Uri takePhotoUri;

    void takePhoto() {
        Tlog.v(TAG, "takePhoto() ");


        PermissionHelper.requestPermission(app, new PermissionRequest.OnPermissionResult() {
            @Override
            public boolean onPermissionRequestResult(String permission, boolean granted) {
                Tlog.v(TAG, "takePhoto()  " + permission + " granted:" + granted);
                return granted;
            }
        }, new PermissionRequest.OnPermissionFinish() {
            @Override
            public void onAllPermissionRequestFinish() {

                File savePhotoFile = getPhotoFile();
                if (savePhotoFile == null) {
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(UPDATE_HEAD_PIC_ERROR_NO_LOCAL_PERMISSION);
                    }
                    return;
                }

                Tlog.v(TAG, "takePhoto() " + savePhotoFile.getAbsolutePath());

                Uri imageUri = PhotoUtils.getTakePhotoURI(app, savePhotoFile);
                Intent intent = PhotoUtils.requestTakePhoto(imageUri);
                takePhotoUri = imageUri;

                if (mResultCallBack != null) {
                    mResultCallBack.onResultStartActivityForResult(intent, TAKE_PHOTO_CODE);
                }
            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    private File getPhotoFile() {
        // 判断存储卡是否可以用，可用进行存储

        if (!PermissionHelper.isGranted(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return null;
        }

        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        String filename = timeStampFormat.format(new Date());
        File cachePath = FileManager.getInstance().getCachePath();
        FileManager.getInstance().mkdirs(cachePath);
        return new File(cachePath, filename + ".jpg");
    }


    void localPhoto() {
        Tlog.v(TAG, "localPhoto() ");

        Intent intent = PhotoUtils.requestLocalPhoto();

//        Intent intent = new Intent(Intent.ACTION_PICK,null);
//        intent.setDataAndType(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                "image/*");

        if (mResultCallBack != null) {
            mResultCallBack.onResultStartActivityForResult(intent, LOCAL_PHOTO_CODE);
        }

    }

    private final IOnCallListener mHeadLogoCallLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, " updateUserInfo msg send success ");

        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " updateUserInfo msg send fail " + startaiError.getErrorCode());

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
        }

    };

    private final FSUploadCallback mLogoUploadCallBack = new FSUploadCallback() {
        @Override
        public void onStart(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onStart " + uploadBean.toString());

        }

        @Override
        public void onSuccess(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onSuccess " + uploadBean.toString());

            C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
            contentBean.setHeadPic(uploadBean.getHttpDownloadUrl());
            contentBean.setUserid(getLoginUserID());
            StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, mHeadLogoCallLsn);

        }

        @Override
        public void onFailure(UploadBean uploadBean, int i) {
            Tlog.v(TAG, " mLogoUploadCallBack onFailure " + i);
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(String.valueOf(i));
            }
        }

        @Override
        public void onProgress(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onProgress " + uploadBean.getProgress());
        }

        @Override
        public void onWaiting(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onWaiting ");
        }

        @Override
        public void onPause(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onPause ");
        }
    };

    private static final int LOCAL_PHOTO_CODE = 0x01;
    private static final int CROP_LOCAL_PHOTO = 0x02;

    private static final int TAKE_PHOTO_CODE = 0x03;
    private static final int CROP_TAKE_PHOTO = 0x04;

    private File localPhotoFile;
    private File takePhotoFile;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.d(TAG, " onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);

        if (mLoginHelp != null) {
            mLoginHelp.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_SCAN_QR) {
            String scanResult = data.getStringExtra("result");
            Tlog.i(TAG, "scanResult = " + scanResult);

            if (mResultCallBack != null) {
                mResultCallBack.onResultScanQRCode(resultCode == Activity.RESULT_OK, scanResult);
            }

        }

        if (requestCode == REUQEST_LOCATION) {

            LooperManager.getInstance().getWorkHandler().post(new Runnable() {
                @Override
                public void run() {

                    boolean providerEnabledGps = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean providerEnabledNet = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (mResultCallBack != null) {
                        mResultCallBack.onResultLocationEnabled(providerEnabledGps || providerEnabledNet);
                    }

                }
            });

            return;
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            Tlog.d(TAG, " onActivityResult user cancel ");
            return;
        }

        if (resultCode != Activity.RESULT_OK && (requestCode >= LOCAL_PHOTO_CODE && requestCode <= CROP_TAKE_PHOTO)) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(UPDATE_HEAD_PIC_ERROR);
            }
            return;
        }

        if (requestCode == LOCAL_PHOTO_CODE) {

            Uri imageUri = data.getData();
            Tlog.d(TAG, " onActivityResult LOCAL_PHOTO_CODE success " + String.valueOf(imageUri));

            localPhotoFile = crop(imageUri, CROP_LOCAL_PHOTO);

        } else if (requestCode == TAKE_PHOTO_CODE) {
            Tlog.d(TAG, " onActivityResult TAKE_PHOTO_CODE success " + String.valueOf(takePhotoUri));
            takePhotoFile = crop(takePhotoUri, CROP_TAKE_PHOTO);

        } else if (requestCode == CROP_TAKE_PHOTO) {

            cropSuccess(takePhotoFile);

        } else if (requestCode == CROP_LOCAL_PHOTO) {

            cropSuccess(localPhotoFile);
        }

    }

    /**
     * 更新头像失败
     */
    private static final String UPDATE_HEAD_PIC_ERROR = "0x802599";
    /**
     * 更新头像失败,没有文件存储权限
     */
    private static final String UPDATE_HEAD_PIC_ERROR_NO_LOCAL_PERMISSION = "0x802598";

    private volatile boolean uploadInit;

    private StartaiUploaderManager getStartaiUploaderManager() {
        if (!uploadInit) {
            uploadInit = true;
            //初始文件上传模块
            StartaiUploaderManager.getInstance().init(app, null);
        }
        return StartaiUploaderManager.getInstance();
    }

    // 裁剪成功
    private void cropSuccess(File path) {

        String filePath = "";

        if (path != null && path.exists()) {
            filePath = path.getAbsolutePath();
            try {
                PhotoUtils.compressImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Tlog.d(TAG, " onActivityResult CROP_PHOTO_SUCCESS:" + filePath);

        if (!"".equalsIgnoreCase(filePath)) {
            //示例代码
            UploadBean uploadentity = new UploadBean.Builder()
                    .localPath(String.valueOf(filePath)) //本地文件路径
                    .build();

            if (mResultCallBack != null) {
                mResultCallBack.onResultModifyHeadLogo(true);
            }

            getStartaiUploaderManager().startUpload(uploadentity, mLogoUploadCallBack);

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(UPDATE_HEAD_PIC_ERROR);
            }
        }

    }


    // 裁剪
    private File crop(Uri imageUri, int code) {

        File path = getPhotoFile();

        if (path == null) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(UPDATE_HEAD_PIC_ERROR_NO_LOCAL_PERMISSION);
            }
            return null;
        }

        Uri outUri = Uri.fromFile(path);

//        Intent intent = PhotoUtils.cropImg(imageUri, outUri);

        Intent intent = PhotoUtils.cropHeadpic(imageUri, outUri);

        // 启动裁剪程序
        if (mResultCallBack != null) {
            mResultCallBack.onResultStartActivityForResult(intent, code);
        }

        return path;
    }

    private final IOnCallListener mGetVersionLsn = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest mqttPublishRequest) {
            Tlog.v(TAG, "mGetVersionLsn msg send success ");
        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, "mGetVersionLsn msg send fail " + startaiError.getErrorCode());

            if (mResultCallBack != null) {

//                mResultCallBack.onResultIsLatestVersion(false, String.valueOf(startaiError.getErrorCode()), false);

                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));

            }
        }

    };

    void checkIsLatestVersion() {
        final String os = "android";
        String packageName = app.getApplicationContext().getPackageName();
        Tlog.v(TAG, "checkIsLatestVersion() :" + packageName);
        StartAI.getInstance().getBaseBusiManager().getLatestVersion(os, packageName, mGetVersionLsn);
    }

    private final FSDownloadCallback mDownloadAppListener = new FSDownloadCallback() {
        @Override
        public void onStart(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onStart() " + String.valueOf(downloadBean));
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }
        }

        @Override
        public void onSuccess(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onSuccess() " + String.valueOf(downloadBean));

            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }

            AppUtils.installApp(downloadBean.getLocalPath());
            Tlog.v(TAG, " path: " + downloadBean.getLocalPath());

        }

        @Override
        public void onFailure(DownloadBean downloadBean, int i) {
            Tlog.e(TAG, "FSDownloadCallback  onFailure() " + i);
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);

                mResultCallBack.onResultMsgSendError(String.valueOf(i));

            }
        }

        @Override
        public void onProgress(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onProgress() " + String.valueOf(downloadBean));

            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }

        }

        @Override
        public void onWaiting(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onWaiting() " + String.valueOf(downloadBean));
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }
        }

        @Override
        public void onPause(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onPause() ");
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }
        }
    };

    private String downloadUrl = null;

    private volatile boolean downloadInit = false;

    private StartaiDownloaderManager getStartaiDownloaderManager() {
        if (!downloadInit) {
            downloadInit = true;
            StartaiDownloaderManager.getInstance().init(app, null);
        }
        return StartaiDownloaderManager.getInstance();
    }

    public void updateApp() {
        Tlog.v(TAG, "updateApp() :" + downloadUrl);

        if (downloadUrl != null) {

            //示例代码
            DownloadBean downloadBean = new DownloadBean.Builder()
                    .url(downloadUrl) //需要下载的文件
//                .fileName(fileName) //文件保存名，选填
                    .build();

            getStartaiDownloaderManager().startDownload(downloadBean, mDownloadAppListener);


            if (Debuger.isLogDebug) {
//                new DownloadTask(downloadUrl).execute();
            }

        }

    }

    public void cancelUpdate() {
        Tlog.v(TAG, "cancelUpdate() :" + downloadUrl);

        DownloadBean downloadBeanByUrl = StartaiDownloaderManager.getInstance().getFDBManager().getDownloadBeanByUrl(downloadUrl);

        if (downloadBeanByUrl != null && downloadBeanByUrl.getStatus() == 2) {
            // 已经下载成功 ，按了取消
            Tlog.e(TAG, " user cancelUpdate but already download success ");
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBeanByUrl);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }

            return;
        }

        if (downloadUrl != null) {
            StartaiDownloaderManager.getInstance().stopDownloader(downloadUrl);
        }
    }

    /**************************/

    private IControlWiFi.IWiFiResultCallBack mResultCallBack;

    void regWiFiResultCallBack(IControlWiFi.IWiFiResultCallBack mResultCallBack) {
        this.mResultCallBack = mResultCallBack;
    }

    public void onLogoutResult(int result, String errorCode, String errorMsg) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, "onLogoutResult  result:" + result + " errorCode:" + errorCode + " errorMsg:" + errorMsg);
        }

        if (result == 1) {
            setLoginUserID(null);
            NetworkData.getLocalData(app).setLastLoginUser("");
        }

        if (mResultCallBack != null) {
            mResultCallBack.onResultLogout(result == 1);
        }

    }

    public void onLoginResult(C_0x8018.Resp resp) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, "onLoginResult  " + String.valueOf(resp));
        }

        C_0x8018.Resp.ContentBean loginInfo = resp.getContent();

        if (resp.getResult() != 1) {

        } else {

            String userID = loginInfo.getUserid();
            Tlog.e(TAG, " login success : " + userID);
            if (!CustomManager.getInstance().isAirtempNBProjectTest()) {
                // 测试模式下，固定userid
                setLoginUserID(userID);
            }
            NetworkData.getLocalData(app).setLastLoginUser(userID);

            UserInfoDao userInfoDao = DBManager.getInstance().getDaoSession().getUserInfoDao();
            List<UserInfo> list = userInfoDao.queryBuilder().where(UserInfoDao.Properties.Mid.eq(userID)).list();

            UserInfo userInfo = null;
            if (list != null && list.size() > 0) {
                Tlog.v(TAG, " register login ; user size : " + list.size());
                userInfo = list.get(0);
            } else {
                Tlog.v(TAG, " register login ; user size : " + 0);
            }

            if (userInfo == null) {
                userInfo = new UserInfo();
            }

            userInfo.setMid(userID);
            userInfo.setType(loginInfo.getType());
            userInfo.setExpire_in(loginInfo.getExpire_in());
            userInfo.setLastLoginTime(System.currentTimeMillis());

            switch (loginInfo.getType()) {

                case 1: // email
                    userInfo.setEmail(loginInfo.getuName());
                    break;
                case 2://mobile + code
                case 3://mobile + pwd
                case 5:// mobile + code + pwd
                    userInfo.setMobile(loginInfo.getuName());
                    break;
                case 4:// user + pwd
                    userInfo.setUserName(loginInfo.getuName());
                    break;
                case Type.Login.THIRD_WECHAT:
                    userInfo.setUserName(loginInfo.getuName());
                    break;
                case Type.Login.THIRD_ALIPAY:
                    userInfo.setUserName(loginInfo.getuName());
                    break;
                case Type.Login.THIRD_FACEBOOK:
                    userInfo.setUserName(loginInfo.getuName());
                    break;

            }

            if (userInfo.getGid() == null) {
                long insert = userInfoDao.insert(userInfo);
                Tlog.v(TAG, "onLoginResult UserInfoDao insert " + insert);
            } else {
                userInfoDao.update(userInfo);
                Tlog.v(TAG, "onLoginResult UserInfoDao update " + userInfo.getGid());
            }

        }

        if (resp.getResult() != 1) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
            return;
        }

        switch (loginInfo.getType()) {

            case 1: // email
                if (mResultCallBack != null) {
                    mResultCallBack.onResultEmailLogin(resp.getResult() == 1, resp.getContent().getErrcode());
                }

                break;
            case 2://mobile + code
            case 3://mobile + pwd
            case 5:// mobile + code + pwd
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMobileLogin(resp.getResult() == 1, resp.getContent().getErrcode());
                }

                break;
            case 4:// user + pwd

                break;
            case 10:
                if (mResultCallBack != null) {
                    mResultCallBack.onResultWxLogin(resp.getResult() == 1, resp.getContent().getErrcode());
                }
                break;
            case Type.Login.THIRD_ALIPAY:

                if (mResultCallBack != null) {
                    mResultCallBack.onResultAliLogin(resp.getResult() == 1, resp.getContent().getErrcode());
                }

                break;

            case Type.Login.THIRD_FACEBOOK:
            case Type.Login.THIRD_GOOGLE:
            case Type.Login.THIRD_TWITTER:


                if (mResultCallBack != null) {
                    mResultCallBack.onResultThirdLogin(resp.getResult() == 1, String.valueOf(loginInfo.getType()));
                }

                break;


        }

    }

    public void onUnActivateResult(C_0x8003.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, "onUnActivateResult  " + String.valueOf(resp));
        }
        if (resp.getResult() == 1) {
        }
    }

    public void onRegisterResult(C_0x8017.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, "onRegisterResult   " + String.valueOf(resp));
        }

        if (resp.getResult() != 1) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
            return;
        }

        switch (resp.getContent().getType()) {

            case 0x01: // email

                if (mResultCallBack != null) {
                    mResultCallBack.onResultEmailRegister(resp.getResult() == 1, resp.getContent().getErrcode());
                }

                break;
            case 0x02://mobile + code
            case 0x03://mobile + pwd
            case 0x05:// mobile + code + pwd

                break;
            case 0x04:// user + pwd

                break;

        }

    }

    public void onGetIdentifyCodeResult(C_0x8021.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onGetIdentifyCodeResult : " + String.valueOf(resp));
        }
        if (mResultCallBack != null) {
            mResultCallBack.onResultGetMobileLoginCode(resp.getResult() == 1, resp.getContent().getType());
        }
    }

    public void onUpdateUserPwdResult(C_0x8025.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onUpdateUserPwdResult : " + String.valueOf(resp));
        }
        if (mResultCallBack != null) {
            if (resp.getResult() == 1) {
                mResultCallBack.onResultUpdatePwd(resp.getResult() == 1, resp.getContent().getErrcode());
            } else {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        }
    }

    public void onGetLatestVersionResult(C_0x8016.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onGetLatestVersionResult :" + String.valueOf(resp));
        }

        downloadUrl = null;

        if (resp.getResult() == 1) {
            boolean isLatestVersion = true;
            try {
                Context applicationContext = app.getApplicationContext();
                PackageInfo packageInfo = applicationContext.getPackageManager()
                        .getPackageInfo(applicationContext.getPackageName(), 0);

                Tlog.v(TAG, " myVersionCode:" + packageInfo.versionCode
                        + " sVersionCode:" + resp.getContent().getVersionCode());

                isLatestVersion = packageInfo.versionCode < resp.getContent().getVersionCode();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            downloadUrl = resp.getContent().getUpdateUrl();
            Tlog.v(TAG, " onGetLatestVersionResult:  downloadUrl : " + downloadUrl);

            if (mResultCallBack != null) {
                mResultCallBack.onResultIsLatestVersion(resp.getResult() == 1,
                        resp.getContent().getErrcode(), isLatestVersion);
            }

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        }

    }

    public void onUpdateUserInfoResult(C_0x8020.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onUpdateUserInfoResult : " + String.valueOf(resp));
        }

        C_0x8020.Resp.ContentBean contentBean = resp.getContent();

        if (resp.getResult() == 1) {

            String userid = contentBean.getUserid();

            if (userid == null) {
                userid = getLoginUserID();
            }

            JsUserInfo mUserInfo = null;
            JsUserInfoDao jsUserInfoDao = null;

            if (userid != null) {
                jsUserInfoDao = DBManager.getInstance().getDaoSession().getJsUserInfoDao();
                List<JsUserInfo> list = jsUserInfoDao.queryBuilder()
                        .where(JsUserInfoDao.Properties.Userid.eq(userid)).list();

                if (list.size() > 0) {
                    mUserInfo = list.get(0);
                } else {
                    mUserInfo = new JsUserInfo();
                }

            } else {
                mUserInfo = new JsUserInfo();
            }

            if (contentBean.getAddress() != null) {
                mUserInfo.setAddress(contentBean.getAddress());
            }

            if (contentBean.getBirthday() != null) {
                mUserInfo.setBirthday(contentBean.getBirthday());
            }

            if (contentBean.getCity() != null) {
                mUserInfo.setCity(contentBean.getCity());
            }

            if (contentBean.getFirstName() != null) {
                mUserInfo.setFirstName(contentBean.getFirstName());
            }

            if (contentBean.getHeadPic() != null) {
                mUserInfo.setHeadPic(contentBean.getHeadPic());
            }

            if (contentBean.getLastName() != null) {
                mUserInfo.setLastName(contentBean.getLastName());
            }

            if (contentBean.getNickName() != null) {
                mUserInfo.setNickName(contentBean.getNickName());
            }

            if (contentBean.getProvince() != null) {
                mUserInfo.setProvince(contentBean.getProvince());
            }

            if (contentBean.getSex() != null) {
                mUserInfo.setSex(contentBean.getSex());
            }

            if (contentBean.getTown() != null) {
                mUserInfo.setTown(contentBean.getTown());
            }

            if (contentBean.getUserName() != null) {
                mUserInfo.setUserName(contentBean.getUserName());
            }

            if (jsUserInfoDao != null) {
                if (mUserInfo.getId() != null) {
                    jsUserInfoDao.update(mUserInfo);
                } else {
                    jsUserInfoDao.insert(mUserInfo);
                }
            }

            if (mResultCallBack != null) {
                mResultCallBack.onResultModifyUserInformation(true, mUserInfo);
            }
        } else {

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(contentBean.getErrcode());
            }
        }

    }

    public void onGetUserInfoResult(C_0x8024.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onGetUserInfoResult : " + String.valueOf(resp));
        }

        C_0x8024.Resp.ContentBean contentBean = resp.getContent();

        if (resp.getResult() == 1) {

            JsUserInfoDao jsUserInfoDao = DBManager.getInstance().getDaoSession().getJsUserInfoDao();
            List<JsUserInfo> list = jsUserInfoDao.queryBuilder().where(JsUserInfoDao.Properties.Userid.eq(contentBean.getUserid())).list();

            JsUserInfo mUserInfo;
            if (list.size() > 0) {
                mUserInfo = list.get(0);
            } else {
                mUserInfo = new JsUserInfo();
            }

            mUserInfo.setUserid(contentBean.getUserid());
            mUserInfo.setAddress(contentBean.getAddress());
            mUserInfo.setBirthday(contentBean.getBirthday());
            mUserInfo.setCity(contentBean.getCity());
            mUserInfo.setFirstName(contentBean.getFirstName());

            String headPic = contentBean.getHeadPic();
            mUserInfo.setHeadPic(headPic);

            mUserInfo.setLastName(contentBean.getLastName());
            mUserInfo.setNickName(contentBean.getNickName());
            mUserInfo.setProvince(contentBean.getProvince());
            mUserInfo.setSex(contentBean.getSex());
            mUserInfo.setTown(contentBean.getTown());
            mUserInfo.setUserName(contentBean.getUserName());
            mUserInfo.setIsHavePwd(contentBean.getIsHavePwd());
            mUserInfo.setEmail(contentBean.getEmail());
            mUserInfo.setMobile(contentBean.getMobile());

            List<C_0x8024.Resp.ContentBean.ThirdInfosBean> thirdInfos = contentBean.getThirdInfos();

            if (thirdInfos != null && thirdInfos.size() > 0) {
                List<JsUserInfo.ThirdInfos> mthirdInfos = new ArrayList<>(thirdInfos.size());
                JsUserInfo.ThirdInfos mJsThirdInfos;
                for (C_0x8024.Resp.ContentBean.ThirdInfosBean mBean : thirdInfos) {
                    mJsThirdInfos = new JsUserInfo.ThirdInfos();
                    mJsThirdInfos.setNickName(mBean.getNickName());
                    mJsThirdInfos.setType(mBean.getType());
                    mthirdInfos.add(mJsThirdInfos);
                }

                mUserInfo.setThirdInfos(mthirdInfos);
            } else {
                mUserInfo.setThirdInfos(null);
            }

            if (mUserInfo.getId() == null) {
                jsUserInfoDao.insert(mUserInfo);
            } else {
                jsUserInfoDao.update(mUserInfo);
            }


            File cacheDownPath = DownloadTask.getCacheDownPath(headPic);
            if (!cacheDownPath.exists() && PermissionHelper.isGranted(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Tlog.w(TAG, " start down headPic ");
                new DownloadTask(headPic).execute();
            }

            if (mResultCallBack != null) {
                mResultCallBack.onResultGetUserInfo(true, mUserInfo);
            }
        } else {

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(contentBean.getErrcode());
            }
        }

    }

    public void onSendEmailResult(C_0x8023.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onSendEmailResult : " + String.valueOf(resp));
        }
        if (resp.getResult() == 1) {
            if (resp.getContent().getType() == 2) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultEmailForgot(true);
                }
            } else if (resp.getContent().getType() == 1) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultResendEmail(true);
                }
            }
        } else {
            mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
        }
    }


    public void onGetAlipayAuthInfoResult(C_0x8033.Resp resp) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onGetAlipayPrivateKeyResult :" + String.valueOf(resp));
        }


        if (resp.getResult() == BaseMessage.RESULT_SUCCESS) {

            Tlog.d(TAG, "支付宝密钥获取成功，准备登录 ");

            String AUTH_INFO = resp.getContent().getAliPayAuthInfo();
            aliAuthInfo(AUTH_INFO);

        } else {

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
//
//            String authTargetId = resp.getContent().getAuthTargetId(); // AUTH_1543265456453123 | LOGIN_15264564564
//            if (authTargetId != null) {
//                if (authTargetId.startsWith(C_0x8033.AUTH_TYPE_LOGIN)) {
//
//                } else if (authTargetId.startsWith(C_0x8033.AUTH_TYPE_AUTH)) {
//                }
//            } else {
//                Tlog.e(TAG, " unknown auth ");
//            }


        }

    }


    // 支付宝登陆
    private void aliAuthInfo(String AUTH_INFO) {

        Tlog.d(TAG, " aliAuthInfo autoInfo = " + AUTH_INFO);

        AndJsBridge andJsBridge = Controller.getInstance().getAndJsBridge();
        Activity activity = andJsBridge != null ? andJsBridge.getActivity() : null;

        if (activity == null) {
            Tlog.e(TAG, " activity == null ");

            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError("0x000000");
            }

            return;
        }

        // 调用授权接口，获取授权结果
        AuthTask authTask = new AuthTask(activity);
        Map<String, String> result = authTask.authV2(AUTH_INFO, true);
        AuthResult authResult = new AuthResult(result, true);


        String targetId = authResult.getTargetId();

        Tlog.e(TAG, " aliAuthInfo: " + String.valueOf(authResult));

        if (targetId == null) {

            String resultStatus = authResult.getResultStatus();
            if ("6001".equalsIgnoreCase(resultStatus)) {
                resultStatus = "60016001";
            }
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resultStatus);
            }

        } else if (targetId.startsWith(C_0x8033.AUTH_TYPE_LOGIN)) {

            aliLogin(authResult);

        } else if (targetId.startsWith(C_0x8033.AUTH_TYPE_AUTH)) {

            // alibind
            aliBind(authResult);

        } else {

            String resultStatus = authResult.getResultStatus();
            if ("6001".equalsIgnoreCase(resultStatus)) {
                resultStatus = "60016001";
            }
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resultStatus);
            }

        }

    }

    private void aliLogin(AuthResult authResult) {

        String resultStatus = authResult.getResultStatus();
        String resultCode = authResult.getResultCode();

        // 判断resultStatus 为“9000”且result_code为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
        Tlog.d(TAG, "aliLogin resultStatus:" + resultStatus + " resultCode:" + resultCode);

        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(resultCode, "200")) {
            // 获取alipay_open_id，调支付时作为参数extern_token 的value
            // 传入，则支付账户为该授权账户

            StartAI.getInstance().getBaseBusiManager().loginWithThirdAccount(Type.Login.THIRD_ALIPAY, authResult.getAuthCode(), new IOnCallListener() {
                @Override
                public void onSuccess(MqttPublishRequest mqttPublishRequest) {
                    Tlog.i(TAG, "loginWithThirdAccount ali msg send success ");
                }

                @Override
                public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
                    if (Debuger.isDebug) {
                        Tlog.e(TAG, "loginWithThirdAccount ali msg send fail " + String.valueOf(startaiError));
                    }
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                    }

                }

            });


        } else {
            // 其他状态值则为授权失败
//                            result_status
//                            9000	请求处理成功
//                            4000	系统异常
//                            6001	用户中途取消
//                            6002	网络连接出错

//                            result_code
//                            200	业务处理成功，会返回authCode
//                            1005	账户已冻结，如有疑问，请联系支付宝技术支持
//                            202	系统异常，请稍后再试或联系支付宝技术支持
            if ("6001".equalsIgnoreCase(resultStatus)) {
                resultStatus = "60016001";
            }
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resultStatus);
            }

        }
    }


    private void aliBind(AuthResult authResult) {

        String resultStatus = authResult.getResultStatus();
        String resultCode = authResult.getResultCode();

        // 判断resultStatus 为“9000”且result_code为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
        Tlog.d(TAG, "aliBind resultStatus:" + resultStatus + " resultCode:" + resultCode);

        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(resultCode, "200")) {
            // 获取alipay_open_id，调支付时作为参数extern_token 的value
            // 传入，则支付账户为该授权账户

            C_0x8037.Req.ContentBean req = new C_0x8037.Req.ContentBean();
            req.setCode(authResult.getAuthCode()); //code 来自ali授权返回
            req.setType(C_0x8037.THIRD_ALIPAY); //绑定ali账号

            StartAI.getInstance().getBaseBusiManager().bindThirdAccount(req, new IOnCallListener() {
                @Override
                public void onSuccess(MqttPublishRequest mqttPublishRequest) {
                    Tlog.i(TAG, "bindThirdAccount ali msg send success ");
                }

                @Override
                public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
                    if (Debuger.isDebug) {
                        Tlog.e(TAG, "bindThirdAccount ali msg send fail " + String.valueOf(startaiError));
                    }
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                    }

                }

            });

        } else {
            // 其他状态值则为授权失败
//                            result_status
//                            9000	请求处理成功
//                            4000	系统异常
//                            6001	用户中途取消
//                            6002	网络连接出错

//                            result_code
//                            200	业务处理成功，会返回authCode
//                            1005	账户已冻结，如有疑问，请联系支付宝技术支持
//                            202	系统异常，请稍后再试或联系支付宝技术支持
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resultStatus);
            }

        }
    }

    public void onBindThirdAccountResult(C_0x8037.Resp resp) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onBindThirdAccountResult :" + String.valueOf(resp));
        }

        if (resp.getResult() != 1) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        } else {
            // 绑定成功，js没有回调接口,所有查询一遍用户信息
            getUserInfo();
        }

    }

    public void onCheckIdetifyResult(C_0x8022.Resp resp) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onCheckIdentifyResult " + String.valueOf(resp));
        }


        if (resp.getResult() == 1) {

            C_0x8022.Resp.ContentBean content = resp.getContent();

            if (content.getType() == Type.CheckIdentifyCode.BIND_MOBILE_NUM) {
                C_0x8034.Req.ContentBean req = new C_0x8034.Req.ContentBean(getLoginUserID(), content.getMobile());
                //mobile 需要绑定的手机号
                StartAI.getInstance().getBaseBusiManager().bindMobileNum(req, new IOnCallListener() {
                    @Override
                    public void onSuccess(MqttPublishRequest request) {
                        Tlog.i(TAG, "bindMobileNum  msg send success ");
                    }

                    @Override
                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                        if (Debuger.isDebug) {
                            Tlog.e(TAG, "bindMobileNum msg send fail " + String.valueOf(startaiError));
                        }
                        if (mResultCallBack != null) {
                            mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                        }
                    }

                });
            }

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        }

    }

    public void onBindMobileNumResult(C_0x8034.Resp resp) {

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onBindMobileNumResult " + String.valueOf(resp));
        }

        if (resp.getResult() != 1) {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
                mResultCallBack.onResultBindPhone(false);
            }

            C_0x8034.Resp.ContentBean content = resp.getContent();

            JsUserInfoDao jsUserInfoDao = DBManager.getInstance().getDaoSession().getJsUserInfoDao();
            List<JsUserInfo> list = jsUserInfoDao.queryBuilder().where(JsUserInfoDao.Properties.Userid.eq(content.getUserid())).list();

            if (list.size() > 0) {
                JsUserInfo mUserInfo = list.get(0);
                mUserInfo.setMobile(content.getMobile());
                jsUserInfoDao.update(mUserInfo);
            }

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultBindPhone(true);
            }
        }

    }

    public void onGetWeatherInfoResult(C_0x8035.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onGetWeatherInfoResult " + String.valueOf(resp));
        }

        if (resp.getResult() == 1) {

            JsWeatherInfoDao jsWeatherInfoDao = DBManager.getInstance().getDaoSession().getJsWeatherInfoDao();
            List<JsWeatherInfo> list = jsWeatherInfoDao.queryBuilder().list();

            JsWeatherInfo jsWeatherInfo;
            if (list.size() > 0) {
                jsWeatherInfo = list.get(0);
            } else {
                jsWeatherInfo = new JsWeatherInfo();
            }

            C_0x8035.Resp.ContentBean content = resp.getContent();

            jsWeatherInfo.setLat(content.getLat());
            jsWeatherInfo.setLng(content.getLng());
            jsWeatherInfo.setProvince(content.getProvince());
            jsWeatherInfo.setCity(content.getCity());
            jsWeatherInfo.setDistrict(content.getDistrict());
            jsWeatherInfo.setQlty(content.getQlty());
            jsWeatherInfo.setTmp(content.getTmp());
            jsWeatherInfo.setWeather(content.getWeather());
            String weatherPic = content.getWeatherPic();
            jsWeatherInfo.setWeatherPic(weatherPic);
            jsWeatherInfo.setTimestamp(System.currentTimeMillis());

            if (jsWeatherInfo.getId() == null) {
                jsWeatherInfoDao.insert(jsWeatherInfo);
            } else {
                jsWeatherInfoDao.update(jsWeatherInfo);
            }

            File cacheDownPath = DownloadTask.getCacheDownPath(weatherPic);
            if (!cacheDownPath.exists() && PermissionHelper.isGranted(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Tlog.w(TAG, " start down weatherPic ");
                new DownloadTask(weatherPic).execute();
            }

            if (mResultCallBack != null) {
                mResultCallBack.onResultWeatherInfo(jsWeatherInfo);
            }
        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        }

    }

    public void onUnBindThirdAccountResult(C_0x8036.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " onUnBindThirdAccountResult " + String.valueOf(resp));
        }

        if (resp.getResult() == 1) {

            if (resp.getContent().getType() == C_0x8036.THIRD_WECHAT) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultUnbindWX(true);
                }
            } else if (resp.getContent().getType() == C_0x8036.THIRD_ALIPAY) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultUnbindAli(true);
                }
            }

            // 解绑成功，查询一下用户信息更新数据
            getUserInfo();

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
            }
        }
    }

}
