package cn.com.startai.socket.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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

import cn.com.startai.socket.R;
import cn.com.swain169.log.Tlog;
import retrofit2.Call;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class LoginFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " LoginFragment onCreate() ");
    }

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " LoginFragment inflateView() ");
        View inflate = View.inflate(getActivity(), R.layout.framgment_login,
                null);

//        loginFacebook(inflate);
//
        loginGoogle(inflate);
//
//        loginTwitter(inflate);

        return inflate;
    }

    private TwitterAuthClient mTwitterAuthClient;

    private void loginTwitter(View inflate) {
//        twitter:

//        /initialize sdk
        Context context = getContext();
        TwitterConfig authConfig = new TwitterConfig.Builder(context)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(context.getResources().getString(R.string.twitter_api_key),
                        context.getResources().getString(R.string.twitter_secret_key)))
                .debug(true)
                .build();
        Twitter.initialize(authConfig);

        mTwitterAuthClient = new TwitterAuthClient();

        mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;

                Log.v("abc", " success:" + session.getUserId() + " ");

                //load user data.
                getUserData();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.v("abc", " failure:", exception);
            }
        });

    }

    /**
     * Load twitter user profile.
     */
    private void getUserData() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService statusesService = twitterApiClient.getAccountService();
        Call<User> call = statusesService.verifyCredentials(true, true, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                //Do something with result

                //parse the response
                String name = userResult.data.name;
                String email = userResult.data.email;
                String description = userResult.data.description;
                String pictureUrl = userResult.data.profileImageUrl;
                String bannerUrl = userResult.data.profileBannerUrl;
                String language = userResult.data.lang;
                long id = userResult.data.id;

            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }


    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private void loginGoogle(View inflate) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        showAccount(account);

        SignInButton signInButton = inflate.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
                Tlog.v(TAG, " isGooglePlayServicesAvailable : " + googlePlayServicesAvailable);
                if (googlePlayServicesAvailable != ConnectionResult.SUCCESS) {
                    Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), googlePlayServicesAvailable, 0);
                    errorDialog.show();
                } else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                }


            }
        });


    }

    private void signOutGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    private void revokeGoogleAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void showAccount(GoogleSignInAccount acct) {
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
            Tlog.v(TAG, " personPhoto : " + personPhoto.toString());

        } else {
            Tlog.v(TAG, " GoogleSignInAccount == null ");
        }
    }


    private CallbackManager mFacebookCallbackManager;

    private void loginFacebook(View inflate) {

        // facebook
        FacebookSdk.sdkInitialize(getActivity().getApplication());
        AppEventsLogger.activateApp(getActivity().getApplication());

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Tlog.v(TAG, " LoginFragment isLoggedIn : " + isLoggedIn);

        //        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        LoginButton loginButton = (LoginButton) inflate.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Tlog.v(TAG, " facebook loginResult onSuccess() ");
            }

            @Override
            public void onCancel() {
                Tlog.v(TAG, " facebook loginResult onCancel() ");
            }

            @Override
            public void onError(FacebookException error) {
                Tlog.v(TAG, " facebook loginResult onError() ");
            }
        });

//        LoginManager.getInstance().registerCallback();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " LoginFragment onCreateView() ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tlog.v(TAG, " LoginFragment onDestroyView() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " LoginFragment onDestroy() ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.v(TAG, " LoginFragment onActivityResult " + requestCode);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showAccount(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Tlog.v(TAG, " ApiException ", e);
            }

        } else {

            // facebook
            if (mFacebookCallbackManager != null) {
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            }

            if (mTwitterAuthClient != null) {
                mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface ILoginResult {

        void onLoginIn();

        void onLoginUp();

    }

}
