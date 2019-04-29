package cn.com.startai.socket.sign.js.jsInterface;

import android.os.Looper;
import android.os.Message;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsHandlerJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/17 0017
 * desc :
 */
public class User extends AbsHandlerJsInterface {

    public static class Method {

        private static final String METHOD_CALL_PHONE = "javascript:makePhoneCallResponse($result)";

        public static String callJsCallPhone(boolean result) {
            return METHOD_CALL_PHONE.replace("$result", String.valueOf(result));
        }


        private static final String METHOD_UNBIND_ALI = "javascript:untiedAlipayResponse($result)";

        public static String callJsUnbindAliResult(boolean result) {
            return METHOD_UNBIND_ALI.replace("$result", String.valueOf(result));
        }

        private static final String METHOD_UNBIND_WX = "javascript:untiedWechatResponse($result)";

        public static String callJsUnbindWXResult(boolean result) {
            return METHOD_UNBIND_WX.replace("$result", String.valueOf(result));
        }

        private static final String METHOD_PHOTO_PATH_RESULT = "javascript:getPhotoResponse('$path')";

        public static String callJsGetPhotoPathResult(String path) {
            return METHOD_PHOTO_PATH_RESULT.replace("$path", String.valueOf(path));
        }

        private static final String METHOD_CHANGE_PWD_RESULT = "javascript:changePasswordResponse($result,'$errorCode')";

        public static String callJsChangePwdResult(boolean result, String errorCode) {
            return METHOD_CHANGE_PWD_RESULT.replace("$result", String.valueOf(result))
                    .replace("$errorCode", String.valueOf(errorCode));
        }

        private static final String METHOD_CHECK_LATEST_VERSION_RESULT
                = "javascript:inspectVersionUpdateResponse($result,'$errorCode',$isLatest)";

        public static String callJsCheckIsLaterVersionResult(boolean result, String errorCode, boolean isLatest) {
            return METHOD_CHECK_LATEST_VERSION_RESULT
                    .replace("$result", String.valueOf(result))
                    .replace("$errorCode", String.valueOf(errorCode))
                    .replace("$isLatest", String.valueOf(isLatest));
        }


        private static final String METHOD_MODIFY_NAME_RESULT = "javascript:modifyUsernameResponse($result,'$errorCode')";

        public static String callJsModifyNameResult(boolean result, String errorCode) {
            return METHOD_MODIFY_NAME_RESULT.replace("$result", String.valueOf(result))
                    .replace("$errorCode", String.valueOf(errorCode));
        }

        private static final String METHOD_USER_INFO_RESULT = "javascript:userInformationResponse($result,'$data')";

        public static String callJsUserInfoResult(boolean result, String data) {
            return METHOD_USER_INFO_RESULT.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }

        private static final String METHOD_MODIFY_USER_INFO_RESULT = "javascript:modifyUserInformationResponse($result,'$data')";

        public static String callJsModifyUserInfoResult(boolean result, String data) {
            return METHOD_MODIFY_USER_INFO_RESULT.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }

        private static final String METHOD_VERSION_NAME_RESULT = "javascript:versionNumberResponse('$data')";

        public static String callJsGetVersionResult(String data) {
            return METHOD_VERSION_NAME_RESULT
                    .replace("$data", String.valueOf(data));
        }

        private static final String METHOD_VERSION_UPDATE_RESULT = "javascript:versionUpdateResponse($result,'$data')";

        public static String callJsUpdateResult(boolean result, String data) {
            return METHOD_VERSION_UPDATE_RESULT.replace("$result", String.valueOf(result))
                    .replace("$data", String.valueOf(data));
        }

        private static final String METHOD_MODIFY_HEAD_LOGO_RESULT = "javascript:modifyAvatarSendedResponse($result)";

        public static String callJsModifyHeadLogoResult(boolean result) {
            return METHOD_MODIFY_HEAD_LOGO_RESULT.replace("$result", String.valueOf(result));
        }


        private static final String METHOD_BIND_PHONE_RESULT = "javascript:bindMobilePhoneResponse($result)";

        public static String callJsBindPhoneResult(boolean result) {
            return METHOD_BIND_PHONE_RESULT.replace("$result", String.valueOf(result));
        }

    }

    public interface IJSUserCallBack {

        void onJSUpdateUserPwd(UserUpdateInfo mPwd);

        void onJSCheckIsLatestVersion();

        void onJSUpdateApp();

        void onJSUpdateUserName(UserUpdateInfo obj);

        void onJSRequestTakePhoto();

        void onJSRequestLocalPhoto();

        void onJSQueryUserInformation();

        void onJSQueryVersion();

        void onJSCancelUpdate();

        void onJSUpdateNickName(String nickName);

        void onJSBindWX();

        void onJSBindAli();

        void onJSBindPhone(MobileBind mMobileBind);

        void onJSUnBindWX();

        void onJSUnBindAli();

        void onJSCallPhone(String phone);

        void onJSBindThird(String type);
    }

    public static final String NAME_JSI = "User";

    private String TAG = H5Config.TAG;

    private final IJSUserCallBack mCallBack;

    public User(Looper mLooper, IJSUserCallBack mCallBack) {
        super(NAME_JSI, mLooper);
        this.mCallBack = mCallBack;
    }

    @JavascriptInterface
    public void takePhotoRequest() {
        Tlog.v(TAG, " takePhotoRequest ");
        getHandler().sendEmptyMessage(MSG_TAKE_PHOTO);
    }

    @JavascriptInterface
    public void localPhotoRequest() {
        Tlog.v(TAG, " localPhotoRequest ");
        getHandler().sendEmptyMessage(MSG_LOCAL_PHOTO);
    }

    @JavascriptInterface
    public void changePasswordRequest(String newPwd, String oldPwd) {
        Tlog.v(TAG, " changePasswordRequest new:" + newPwd + "- old:" + oldPwd);
        final UserUpdateInfo mPwd = new UserUpdateInfo();
        mPwd.newPwd = newPwd;
        mPwd.oldPwd = oldPwd;
        getHandler().obtainMessage(MSG_UPDATE_PWD, mPwd).sendToTarget();
    }

    @JavascriptInterface
    public void inspectversionUpdateRequest() {
        Tlog.v(TAG, " inspectversionUpdateRequest ");
        getHandler().sendEmptyMessage(MSG_CHECK_IS_NEED_UPDATE);
    }

    @JavascriptInterface
    public void versionUpdateRequest() {
        Tlog.v(TAG, " versionUpdateRequest ");
        getHandler().sendEmptyMessage(MSG_NEED_UPDATE);
    }

    @JavascriptInterface
    public void cancelVersionUpdateRequest() {
        Tlog.v(TAG, " cancelVersionUpdateRequest ");
        getHandler().sendEmptyMessage(MSG_CANCEL_UPDATE);
    }

    @JavascriptInterface
    public void modifyUsernameRequest(String surname, String name) {
        Tlog.v(TAG, " modifyUsernameRequest " + surname + " name:" + name);
        final UserUpdateInfo mUserUpdateName = new UserUpdateInfo();
        mUserUpdateName.surname = surname;
        mUserUpdateName.name = name;
        getHandler().obtainMessage(MSG_UPDATE_USERNAME, mUserUpdateName).sendToTarget();
    }

    @JavascriptInterface
    public void modifyNicknameRequest(String niceName) {
        Tlog.v(TAG, " modifyNicknameRequest " + niceName);
        getHandler().obtainMessage(MSG_UPDATE_NICKNAME, niceName).sendToTarget();
    }

    @JavascriptInterface
    public void userInformationRequest() {
        Tlog.v(TAG, " userInformationRequest ");
        getHandler().sendEmptyMessage(MSG_QUERY_INFORMATION);
    }

    @JavascriptInterface
    public void versionNumberRequest() {
        Tlog.v(TAG, " versionNumberRequest ");
        getHandler().sendEmptyMessage(MSG_REQUEST_VERSION);
    }

    @JavascriptInterface
    public void bindingWeChatRequest() {
        Tlog.v(TAG, " bindingWeChatRequest ");
        getHandler().sendEmptyMessage(MSG_BIND_WECHAT);
    }

    @JavascriptInterface
    public void bindingAlipayRequest() {
        Tlog.v(TAG, " bindingAlipayRequest ");
        getHandler().sendEmptyMessage(MSG_BIND_ALI);
    }

    @JavascriptInterface
    public void bindMobilePhoneRequest(String phone, String code) {
        Tlog.v(TAG, " bindMobilePhoneRequest ");
        MobileBind mBind = new MobileBind();
        mBind.phone = phone;
        mBind.code = code;
        getHandler().obtainMessage(MSG_BIND_PHONE, mBind).sendToTarget();
    }

    @JavascriptInterface
    public void untiedWechatRequest() {
        Tlog.v(TAG, " untiedWechatRequest ");
        getHandler().sendEmptyMessage(MSG_UNBIND_WX);
    }

    @JavascriptInterface
    public void untiedAlipayRequest() {
        Tlog.v(TAG, " untiedAlipayRequest ");
        getHandler().sendEmptyMessage(MSG_UNBIND_ALI);
    }

    @JavascriptInterface
    public void makePhoneCallRequest(String phone) {
        Tlog.v(TAG, " makePhoneCallRequest " + phone);
        getHandler().obtainMessage(MSG_MAKE_PHONE, phone).sendToTarget();
    }

    @JavascriptInterface
    public void bindThirdPartyAccountRequest(String type) {
        Tlog.v(TAG, " bindThirdPartyAccountRequest " + type);
        getHandler().obtainMessage(MSG_BIND_THIRD, type).sendToTarget();
    }

    private static final int MSG_UPDATE_PWD = 0x02;
    private static final int MSG_CHECK_IS_NEED_UPDATE = 0x03;
    private static final int MSG_NEED_UPDATE = 0x04;
    private static final int MSG_UPDATE_USERNAME = 0x05;

    private static final int MSG_TAKE_PHOTO = 0x06;
    private static final int MSG_LOCAL_PHOTO = 0x07;

    private static final int MSG_QUERY_INFORMATION = 0x08;

    private static final int MSG_REQUEST_VERSION = 0x09;

    private static final int MSG_CANCEL_UPDATE = 0x0A;

    private static final int MSG_UPDATE_NICKNAME = 0x0B;

    private static final int MSG_BIND_WECHAT = 0x0C;

    private static final int MSG_BIND_ALI = 0x0D;

    private static final int MSG_BIND_PHONE = 0x0E;

    private static final int MSG_UNBIND_WX = 0x10;

    private static final int MSG_UNBIND_ALI = 0x11;

    private static final int MSG_MAKE_PHONE = 0x12;

    private static final int MSG_BIND_THIRD = 0x13;

    @Override
    protected void handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_UPDATE_PWD:

                if (mCallBack != null) {
                    mCallBack.onJSUpdateUserPwd((UserUpdateInfo) msg.obj);
                }

                break;

            case MSG_CHECK_IS_NEED_UPDATE:
                if (mCallBack != null) {
                    mCallBack.onJSCheckIsLatestVersion();
                }
                break;

            case MSG_NEED_UPDATE:

                if (mCallBack != null) {
                    mCallBack.onJSUpdateApp();
                }

                break;

            case MSG_UPDATE_USERNAME:

                if (mCallBack != null) {
                    mCallBack.onJSUpdateUserName((UserUpdateInfo) msg.obj);
                }

                break;
            case MSG_TAKE_PHOTO:
                if (mCallBack != null) {
                    mCallBack.onJSRequestTakePhoto();
                }
                break;
            case MSG_LOCAL_PHOTO:

                if (mCallBack != null) {
                    mCallBack.onJSRequestLocalPhoto();
                }

                break;
            case MSG_QUERY_INFORMATION:

                if (mCallBack != null) {
                    mCallBack.onJSQueryUserInformation();
                }

                break;

            case MSG_REQUEST_VERSION:

                if (mCallBack != null) {
                    mCallBack.onJSQueryVersion();
                }

                break;
            case MSG_CANCEL_UPDATE:

                if (mCallBack != null) {
                    mCallBack.onJSCancelUpdate();
                }

                break;

            case MSG_UPDATE_NICKNAME:

                if (mCallBack != null) {
                    mCallBack.onJSUpdateNickName((String) msg.obj);
                }

                break;

            case MSG_BIND_WECHAT:

                if (mCallBack != null) {
                    mCallBack.onJSBindWX();
                }

                break;

            case MSG_BIND_ALI:
                if (mCallBack != null) {
                    mCallBack.onJSBindAli();
                }
                break;

            case MSG_BIND_PHONE:
                if (mCallBack != null) {
                    mCallBack.onJSBindPhone((MobileBind) msg.obj);
                }
                break;

            case MSG_UNBIND_WX:
                if (mCallBack != null) {
                    mCallBack.onJSUnBindWX();
                }
                break;

            case MSG_UNBIND_ALI:
                if (mCallBack != null) {
                    mCallBack.onJSUnBindAli();
                }
                break;

            case MSG_MAKE_PHONE:
                if (mCallBack != null) {
                    mCallBack.onJSCallPhone((String) msg.obj);
                }
                break;

            case MSG_BIND_THIRD:
                if (mCallBack != null) {
                    mCallBack.onJSBindThird((String) msg.obj);
                }
                break;
        }

    }
}
