package cn.com.startai.socket.global;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONObject;

import java.util.Arrays;

import cn.com.startai.socket.mutual.js.bean.ThirdLoginUser;
import cn.com.startai.socket.sign.hardware.WiFi.impl.UserManager;
import cn.com.swain.baselib.log.Tlog;
import retrofit2.Call;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/7 0007
 * desc :
 */
public class LoginHelp {

    private LoginHelp() {

    }

    private static LoginHelp mLoginHelp;

    public static LoginHelp getInstance() {
        if (mLoginHelp == null) {
            synchronized (LoginHelp.class) {
                if (mLoginHelp == null) {
                    mLoginHelp = new LoginHelp();
                }
            }
        }
        return mLoginHelp;
    }

    private OnLoginResult mOnLoginResult;

    public void regLoginCallBack(OnLoginResult mOnLoginResult) {
        this.mOnLoginResult = mOnLoginResult;
    }

    private String TAG = UserManager.TAG;

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInClient getGoogleSignInClient(final Activity mContext) {

        if (mGoogleSignInClient == null) {

        }

        return mGoogleSignInClient;
    }


    private GoogleApiClient mGoogleApiClient;

    private GoogleApiClient getGoogleApiClient(final FragmentActivity mContext) {

        if (mGoogleApiClient == null) {
            GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestId();

//            String serverClientId = mContext.getResources().getString(R.string.server_client_id);
//            if (serverClientId != null) builder.requestIdToken(serverClientId);
            GoogleSignInOptions gso = builder.build();

            Context applicationContext = mContext.getApplicationContext();
            GoogleApiClient.Builder builder1 = new GoogleApiClient.Builder(applicationContext).enableAutoManage(mContext, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            }).addApi(Auth.GOOGLE_SIGN_IN_API, gso);
            mGoogleApiClient = builder1.build();

        }

        return mGoogleApiClient;
    }

    private static final int RC_BIND_IN = 9002;

    public void bindGoogle(Activity mAct) {

        Tlog.v(TAG, " bindGoogle() ");

        Context applicationContext = mAct.getApplicationContext();
        GoogleApiClient mGoogleApiClient = getGoogleApiClient((FragmentActivity) mAct);

//        GoogleSignInClient mGoogleSignInClient = getGoogleSignInClient(mAct);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(applicationContext);
//        getGoogleAccount(account);

        int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext);
        Tlog.v(TAG, " isGooglePlayServicesAvailable : " + googlePlayServicesAvailable);

        if (googlePlayServicesAvailable != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mAct, googlePlayServicesAvailable, 0);
            errorDialog.show();
        } else {
//            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//            mAct.startActivityForResult(signInIntent, RC_SIGN_IN);


            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mAct.startActivityForResult(signInIntent, RC_BIND_IN);

        }

    }

    private static final int RC_SIGN_IN = 9001;

    public void loginGoogle(Activity mAct) {

        Tlog.v(TAG, " loginGoogle() ");

        Context applicationContext = mAct.getApplicationContext();
        GoogleApiClient mGoogleApiClient = getGoogleApiClient((FragmentActivity) mAct);

//        GoogleSignInClient mGoogleSignInClient = getGoogleSignInClient(mAct);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(applicationContext);
//        getGoogleAccount(account);

        int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext);
        Tlog.v(TAG, " isGooglePlayServicesAvailable : " + googlePlayServicesAvailable);

        if (googlePlayServicesAvailable != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mAct, googlePlayServicesAvailable, 0);
            errorDialog.show();
        } else {
//            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//            mAct.startActivityForResult(signInIntent, RC_SIGN_IN);


            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mAct.startActivityForResult(signInIntent, RC_SIGN_IN);

        }

    }


    public void signOutGoogle(final Activity mAct) {
        final GoogleSignInClient mGoogleSignInClient = getGoogleSignInClient(mAct);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(mAct, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void revokeGoogleAccess(final Activity mAct) {
        final GoogleSignInClient mGoogleSignInClient = getGoogleSignInClient(mAct);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(mAct, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


///////////////////////////////////////

    private TwitterAuthClient mTwitterAuthClient;

    private TwitterAuthClient getTwitterAuthClient() {
        return mTwitterAuthClient;
    }

    private boolean twitterSdkInit = false;

    public void loginTwitter(Activity mAct) {
        Tlog.v(TAG, " loginTwitter() ");
        Context context = mAct.getApplication();

        String twitterApiKey = "lIMEObknf7gHEsOj9mutp3guq";
        String twitterSecretKey = "4bPsFNeRm2EoYenuZi1hxoTRtHlOo91eQaK8w5o8weOQPMiYBU";

//        /initialize sdk
        if (!twitterSdkInit) {
            twitterSdkInit = true;
            TwitterConfig authConfig = new TwitterConfig.Builder(context)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(new TwitterAuthConfig(twitterApiKey,
                            twitterSecretKey))
                    .debug(true)
                    .build();
            Twitter.initialize(authConfig);
        }

        mTwitterAuthClient = new TwitterAuthClient();

//        mTwitterAuthClient.requestEmail(session, new Callback<String>() {
//            @Override
//            public void success(Result<String> result) {
//                Tlog.v(TAG, "loginTwitter success:" + session.getUserId() + " token: " + session.getAuthToken().token);
//                getTwitterUserData();
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Tlog.v(TAG, "loginTwitter success:" + session.getUserId() + " ");
//            }
//        });

        mTwitterAuthClient.authorize(mAct, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;

                Tlog.v(TAG, "loginTwitter success:" + session.getUserId() + " ");

                //load user data.
                getTwitterUserData();
            }

            @Override
            public void failure(TwitterException exception) {
                Tlog.e(TAG, "loginTwitter failure:", exception);

                if (mOnLoginResult != null) {
                    mOnLoginResult.onResult(false, null);
                }

            }
        });

    }


    /**
     * Load twitter user profile.
     */
    private void getTwitterUserData() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService statusesService = twitterApiClient.getAccountService();

        Call<User> call = statusesService.verifyCredentials(true, true, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                //Do something with result

                User data = userResult.data;
                long id = data.id;
                Tlog.v(TAG, " getTwitterUserData success: " + id);
                ThirdLoginUser mUser = new ThirdLoginUser();
                mUser.userID = data.idStr;
                mUser.name = data.name;
                mUser.linkURL = data.profileImageUrl;

                if (mOnLoginResult != null) {
                    mOnLoginResult.onResult(true, mUser);
                }

            }

            public void failure(TwitterException exception) {
                //Do something on failure

                Tlog.v(TAG, " getTwitterUserData failure : ");

                if (mOnLoginResult != null) {
                    mOnLoginResult.onResult(false, null);
                }
            }
        });
    }

///////////////////////////////////////

    private CallbackManager mFacebookCallbackManager;

    private CallbackManager getFacebookCallbackManager() {
        return mFacebookCallbackManager;
    }

    private boolean FacebookSdkInit = false;

    private void getFacebookLoginInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Tlog.v(TAG, " getFacebookLoginInfo：" + String.valueOf(object));

                if (mOnLoginResult != null) {
                    mOnLoginResult.onFacebookResult(true, object);
                }

//                if (object != null) {
//
//                    Tlog.v(TAG, "" + object.toString());
//
//                    String id = object.optString("id");   //比如:1565455221565
//                    String name = object.optString("name");  //比如：Zhang San
//                    String gender = object.optString("gender");  //性别：比如 male （男）  female （女）
//                    String emali = object.optString("email");  //邮箱：比如：56236545@qq.com
//
//                    String lastName = object.optString("last_name");
//                    String firstName = object.optString("first_name");
//
//
//                    //获取用户头像
//                    JSONObject object_pic = object.optJSONObject("picture");
//                    JSONObject object_data = object_pic.optJSONObject("data");
//                    String photo = object_data.optString("url");
//
//                    //获取地域信息
//                    String locale = object.optString("locale");   //zh_CN 代表中文简体
//
//                    ThirdLoginUser mUser = new ThirdLoginUser();
//                    mUser.userID = id;
//                    mUser.name = name;
//                    mUser.lastName = lastName;
//                    mUser.firstName = firstName;
//                    mUser.linkURL = object_pic.toString();
//                    mUser.linkURL = photo;
//
//
//                } else {
//                    Tlog.v(TAG, " object==null ");
//                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void getFacebookBindInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Tlog.v(TAG, " getFacebookBindInfo：" + String.valueOf(object));

                if (mOnLoginResult != null) {
                    mOnLoginResult.onFacebookBindResult(true, object);
                }

//                if (object != null) {
//
//                    Tlog.v(TAG, "" + object.toString());
//
//                    String id = object.optString("id");   //比如:1565455221565
//                    String name = object.optString("name");  //比如：Zhang San
//                    String gender = object.optString("gender");  //性别：比如 male （男）  female （女）
//                    String emali = object.optString("email");  //邮箱：比如：56236545@qq.com
//
//                    String lastName = object.optString("last_name");
//                    String firstName = object.optString("first_name");
//
//
//                    //获取用户头像
//                    JSONObject object_pic = object.optJSONObject("picture");
//                    JSONObject object_data = object_pic.optJSONObject("data");
//                    String photo = object_data.optString("url");
//
//                    //获取地域信息
//                    String locale = object.optString("locale");   //zh_CN 代表中文简体
//
//                    ThirdLoginUser mUser = new ThirdLoginUser();
//                    mUser.userID = id;
//                    mUser.name = name;
//                    mUser.lastName = lastName;
//                    mUser.firstName = firstName;
//                    mUser.linkURL = object_pic.toString();
//                    mUser.linkURL = photo;
//
//
//                } else {
//                    Tlog.v(TAG, " object==null ");
//                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private CallbackManager mFacebookBindCallbackManager;

    private CallbackManager getFacebookBindCallbackManager() {
        return mFacebookBindCallbackManager;
    }

    public void bindFacebook(Activity mAct) {

        Tlog.v(TAG, " bindFacebook() ");

        if (!FacebookSdkInit) {
            FacebookSdkInit = true;
            FacebookSdk.sdkInitialize(mAct.getApplication());
            AppEventsLogger.activateApp(mAct.getApplication());
        }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Tlog.v(TAG, " Facebook isLoggedIn : " + isLoggedIn);

        mFacebookBindCallbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance().registerCallback(mFacebookBindCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Tlog.v(TAG, " facebook loginResult onSuccess() ");

                AccessToken accessToken1 = loginResult.getAccessToken();
                getFacebookBindInfo(accessToken1);

            }

            @Override
            public void onCancel() {
                Tlog.v(TAG, " facebook loginResult onCancel() ");

            }

            @Override
            public void onError(FacebookException error) {
                Tlog.e(TAG, " facebook loginResult onError() ", error);

                if (mOnLoginResult != null) {
                    mOnLoginResult.onFacebookBindResult(false, null);
                }

            }
        });

        LoginManager.getInstance().logInWithReadPermissions(mAct, Arrays.asList("public_profile"
//                , "user_friends", "email"
        ));

    }


    public void loginFacebook(Activity mAct) {

        Tlog.v(TAG, " loginFacebook() ");

        if (!FacebookSdkInit) {
            FacebookSdkInit = true;
            FacebookSdk.sdkInitialize(mAct.getApplication());
            AppEventsLogger.activateApp(mAct.getApplication());
        }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Tlog.v(TAG, " Facebook isLoggedIn : " + isLoggedIn);

        mFacebookCallbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Tlog.v(TAG, " facebook loginResult onSuccess() ");

                AccessToken accessToken1 = loginResult.getAccessToken();
                getFacebookLoginInfo(accessToken1);

            }

            @Override
            public void onCancel() {
                Tlog.v(TAG, " facebook loginResult onCancel() ");

            }

            @Override
            public void onError(FacebookException error) {
                Tlog.e(TAG, " facebook loginResult onError() ", error);

                if (mOnLoginResult != null) {
                    mOnLoginResult.onFacebookResult(false, null);
                }

            }
        });

        LoginManager.getInstance().logInWithReadPermissions(mAct, Arrays.asList("public_profile"
//                , "user_friends", "email"
        ));

    }


    private void callGoogleBindIn(GoogleSignInResult result) {

        GoogleSignInAccount acct = null;

        if (result != null && result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();

            if (acct != null) {

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                Tlog.v(TAG, " personName : " + personName);
                Tlog.v(TAG, " personGivenName : " + personGivenName);
                Tlog.v(TAG, " personFamilyName : " + personFamilyName);
                Tlog.v(TAG, " personEmail : " + personEmail);
                Tlog.v(TAG, " personId : " + personId);

                if (personPhoto != null) {
                    Tlog.v(TAG, " personPhoto : " + personPhoto.toString());
                } else {
                    Tlog.v(TAG, " personPhoto ==null ");

                }

                if (mOnLoginResult != null) {
                    mOnLoginResult.onGoogleBindResult(true, acct);
                }

            } else {
                Tlog.e(TAG, " GoogleSignInAccount == null ");
                if (mOnLoginResult != null) {
                    mOnLoginResult.onGoogleBindResult(false, null);
                }
            }

        } else {
            Tlog.e(TAG, " result fail ");
            if (mOnLoginResult != null) {
                mOnLoginResult.onGoogleBindResult(false, null);
            }
        }


    }

    private void callGoogleLoginIn(GoogleSignInResult result) {

        GoogleSignInAccount acct = null;

        if (result != null && result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();

            if (acct != null) {

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                Tlog.v(TAG, " personName : " + personName);
                Tlog.v(TAG, " personGivenName : " + personGivenName);
                Tlog.v(TAG, " personFamilyName : " + personFamilyName);
                Tlog.v(TAG, " personEmail : " + personEmail);
                Tlog.v(TAG, " personId : " + personId);

                if (personPhoto != null) {
                    Tlog.v(TAG, " personPhoto : " + personPhoto.toString());
                } else {
                    Tlog.v(TAG, " personPhoto ==null ");

                }

                if (mOnLoginResult != null) {
                    mOnLoginResult.onGoogleResult(true, acct);
                }

            } else {
                Tlog.e(TAG, " GoogleSignInAccount == null ");
                if (mOnLoginResult != null) {
                    mOnLoginResult.onGoogleResult(false, null);
                }
            }

        } else {
            Tlog.e(TAG, " result fail ");
            if (mOnLoginResult != null) {
                mOnLoginResult.onGoogleResult(false, null);
            }
        }


    }

    ///////////////////////////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.v(TAG, " Login onActivityResult " + requestCode);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            callGoogleLoginIn(task);

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            callGoogleLoginIn(result);


        } else if (requestCode == RC_BIND_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            callGoogleLoginIn(task);

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            callGoogleBindIn(result);
        } else {

            // facebook

            try {

                CallbackManager facebookCallbackManager = getFacebookCallbackManager();
                if (facebookCallbackManager != null) {
                    facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                }

            } catch (Exception e) {
                Tlog.e(TAG, " facebook login Error", e);
                if (mOnLoginResult != null) {
                    mOnLoginResult.onResult(false, null);
                }
            }

            try {

                CallbackManager facebookBindCallbackManager = getFacebookBindCallbackManager();
                if (facebookBindCallbackManager != null) {
                    facebookBindCallbackManager.onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {

            }

            try {
                TwitterAuthClient twitterAuthClient = getTwitterAuthClient();
                if (twitterAuthClient != null) {
                    twitterAuthClient.onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {
                Tlog.e(TAG, " twitter login Error", e);
                if (mOnLoginResult != null) {
                    mOnLoginResult.onResult(false, null);
                }
            }


        }

    }


    public interface OnLoginResult {

        void onResult(boolean result, ThirdLoginUser mUser);

        void onFacebookResult(boolean result, JSONObject object);

        void onFacebookBindResult(boolean result, JSONObject object);

        void onGoogleResult(boolean result, GoogleSignInAccount account);

        void onGoogleBindResult(boolean result, GoogleSignInAccount account);

    }

}
