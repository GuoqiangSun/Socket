package cn.com.startai.socket.sign.hardware.WiFi.impl;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.startai.fssdk.FSDownloadCallback;
import cn.com.startai.fssdk.FSUploadCallback;
import cn.com.startai.fssdk.StartaiDownloaderManager;
import cn.com.startai.fssdk.StartaiUploaderManager;
import cn.com.startai.fssdk.db.entity.DownloadBean;
import cn.com.startai.fssdk.db.entity.UploadBean;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8003;
import cn.com.startai.mqttsdk.busi.entity.C_0x8016;
import cn.com.startai.mqttsdk.busi.entity.C_0x8017;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.busi.entity.C_0x8021;
import cn.com.startai.mqttsdk.busi.entity.C_0x8023;
import cn.com.startai.mqttsdk.busi.entity.C_0x8024;
import cn.com.startai.mqttsdk.busi.entity.C_0x8025;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;
import cn.com.startai.socket.db.gen.UserInfoDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.global.WXLoginHelper;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.UpdateProgress;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.sign.hardware.IControlWiFi;
import cn.com.startai.socket.sign.hardware.WiFi.bean.UserInfo;
import cn.com.startai.socket.sign.hardware.WiFi.util.NetworkData;
import cn.com.startai.socket.sign.js.JsUserInfo;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.util.PhotoUtils;
import cn.com.swain.baselib.log.Tlog;

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

    @Override
    public void onSCreate() {

        if (CustomManager.getInstance().isAirtempNBProjectTest()) {
            setLoginUserID(NetworkData.USERID_DEFAULT);
//            setLoginUserID("d61a2ee17966d6f1b8b2f93c5a6d5eea");

        } else {
            setLoginUserID(getLastLoginUserID());
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
        return mUserID;
    }

    private synchronized String getLastLoginUserID() {
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

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    void getUserInfo() {

        StartAI.getInstance().getBaseBusiManager().getUserInfo(mGetUserInfoLsn);

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

            @Override
            public boolean needUISafety() {
                return false;
            }
        });
    }

    void isLogin() {

        boolean isLogin = getLastLoginUserID() != null;

//
//        if (!isLogin) {
//            StartAI.getInstance().getBaseBusiManager().logout();
//        }

        Tlog.e(TAG, " isLogin " + isLogin + " " + getLoginUserID());

        if (mResultCallBack != null) {
            mResultCallBack.onResultIsLogin(isLogin);
        }

    }


    void wxLogin() {

        if (cn.com.swain.baselib.util.AppUtils.isAppInstalled(app, "com.tencent.mm")) {

            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "diandi_wx_login";
            //向微信发送请求

            IWXAPI wxApi = WXLoginHelper.getInstance().getWXApi(app);

            if (wxApi == null) {
                if (mResultCallBack != null) {
                    mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_UNKNOWN);
                }
            } else {
                Tlog.v(TAG, " wxApi  sendReq ");
                wxApi.sendReq(req);
            }

        } else {
            if (mResultCallBack != null) {
                mResultCallBack.onResultMsgSendError(ERROR_CODE_WX_LOGIN_NO_CLIENT);
            }
        }

    }


    public void onWxLoginResult(BaseResp baseResp) {

        if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {

            String code = ((SendAuth.Resp) baseResp).code;
            Tlog.e(TAG, "onWxLoginSuccess code: " + code);

            StartAI.getInstance().getBaseBusiManager().loginWithThirdAccount(10, code, new IOnCallListener() {
                @Override
                public void onSuccess(MqttPublishRequest request) {

                }

                @Override
                public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                    Tlog.e(TAG, " wxLogin msg send fail " + startaiError.getErrorCode());
                    if (mResultCallBack != null) {
                        mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
                    }
                }

                @Override
                public boolean needUISafety() {
                    return false;
                }
            });

        } else {

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
            Tlog.v(TAG, " getIdentifyCode msg send success:");

        }

        @Override
        public void onFailed(MqttPublishRequest mqttPublishRequest, StartaiError startaiError) {
            Tlog.e(TAG, " getIdentifyCode msg send failed: " + startaiError.getErrorCode());
            if (mResultCallBack != null) {
                mResultCallBack.onResultGetMobileLoginCode(false);
                mResultCallBack.onResultMsgSendError(String.valueOf(startaiError.getErrorCode()));
            }
            mLastSendMillis = 0L;
        }

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    private long mLastSendMillis;
    private String mLastSendPhone;

    void getMobileLoginCode(String phone) {
        Tlog.e(TAG, " getMobileLoginCode " + phone);

        long currentTimeMillis = System.currentTimeMillis();

        if (!phone.equalsIgnoreCase(mLastSendPhone)) {

            StartAI.getInstance().getBaseBusiManager().getIdentifyCode(phone, 1, mGetLoginCodeLsn);

        } else {
            if (Math.abs(currentTimeMillis - mLastSendMillis) > 1000 * 60) {

                StartAI.getInstance().getBaseBusiManager().getIdentifyCode(phone, 1, mGetLoginCodeLsn);

            } else {

                Tlog.e(TAG, " getMobileLoginCode too fast ");

                if (mResultCallBack != null) {
                    mResultCallBack.onResultGetMobileLoginCode(false);
                }
            }

        }
        mLastSendMillis = currentTimeMillis;
        mLastSendPhone = phone;

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

        @Override
        public boolean needUISafety() {
            return false;
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

        @Override
        public boolean needUISafety() {
            return false;
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

        @Override
        public boolean needUISafety() {
            return false;
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

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    void updateUserPwd(UserUpdateInfo mPwd) {
        Tlog.v(TAG, "updateUserPwd() old:" + mPwd.oldPwd + " new:" + mPwd.newPwd);
        StartAI.getInstance().getBaseBusiManager().updateUserPwd(mPwd.oldPwd, mPwd.newPwd, mUpdatePwdLsn);
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

        @Override
        public boolean needUISafety() {
            return false;
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

    private static boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(Utils.getApp(), permission);
    }

    private File getPhotoFile() {
        // 判断存储卡是否可以用，可用进行存储

        if (!isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

        @Override
        public boolean needUISafety() {
            return false;
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

        Intent intent = PhotoUtils.cropImg(imageUri, outUri);

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

        @Override
        public boolean needUISafety() {
            return false;
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
            Tlog.v(TAG, "FSDownloadCallback  onStart() " + downloadBean.toString());
            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }
        }

        @Override
        public void onSuccess(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onSuccess() " + downloadBean.toString());

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
            Tlog.v(TAG, "FSDownloadCallback  onProgress() " + downloadBean.toString());

            if (mResultCallBack != null) {
                UpdateProgress mProgress = new UpdateProgress(downloadBean);
                mResultCallBack.onResultUpdateProgress(true, mProgress);
            }

        }

        @Override
        public void onWaiting(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onWaiting() " + downloadBean);
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

        setLoginUserID(null);
        NetworkData.getLocalData(app).setLastLoginUser("");

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

            setLoginUserID(null);

        } else {

            String userID = loginInfo.getUserid();
            Tlog.e(TAG, " login success : " + userID);
            setLoginUserID(userID);
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
                case 10:
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

        }

    }

    public void onUnActivateResult(C_0x8003.Resp resp) {
        if (Debuger.isLogDebug) {
            Tlog.d(TAG, "onUnActivateResult  " + String.valueOf(resp));
        }
        if (resp.getResult() == 1) {
            setLoginUserID(null);
            NetworkData.getLocalData(app).setLastLoginUser("");
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
        mLastSendMillis = 0L;
        if (mResultCallBack != null) {
            mResultCallBack.onResultGetMobileLoginCode(resp.getResult() == 1);
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
            Tlog.v(TAG, " onGetLatestVersionResult:  downloadUrl" + downloadUrl);

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
            JsUserInfo mUserInfo = new JsUserInfo();
            mUserInfo.setAddress(contentBean.getAddress());
            mUserInfo.setBirthday(contentBean.getBirthday());
            mUserInfo.setCity(contentBean.getCity());
            mUserInfo.setFirstName(contentBean.getFirstName());
            mUserInfo.setHeadPic(contentBean.getHeadPic());
            mUserInfo.setLastName(contentBean.getLastName());
            mUserInfo.setNickName(contentBean.getNickName());
            mUserInfo.setProvince(contentBean.getProvince());
            mUserInfo.setSex(contentBean.getSex());
            mUserInfo.setTown(contentBean.getTown());
            mUserInfo.setUserName(contentBean.getUserName());
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
            JsUserInfo mUserInfo = new JsUserInfo();
            mUserInfo.setAddress(contentBean.getAddress());
            mUserInfo.setBirthday(contentBean.getBirthday());
            mUserInfo.setCity(contentBean.getCity());
            mUserInfo.setFirstName(contentBean.getFirstName());
            mUserInfo.setHeadPic(contentBean.getHeadPic());
            mUserInfo.setLastName(contentBean.getLastName());
            mUserInfo.setNickName(contentBean.getNickName());
            mUserInfo.setProvince(contentBean.getProvince());
            mUserInfo.setSex(contentBean.getSex());
            mUserInfo.setTown(contentBean.getTown());
            mUserInfo.setUserName(contentBean.getUserName());
            mUserInfo.setIsHavePwd(contentBean.getIsHavePwd());
            mUserInfo.setEmail(contentBean.getEmail());
            mUserInfo.setMobile(contentBean.getMobile());
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
            }
        } else {
            mResultCallBack.onResultMsgSendError(resp.getContent().getErrcode());
        }
    }

}
