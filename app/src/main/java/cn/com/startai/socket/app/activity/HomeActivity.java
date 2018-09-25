package cn.com.startai.socket.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Queue;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.app.fragment.BaseFragment;
import cn.com.startai.socket.app.fragment.GuideFragment;
import cn.com.startai.socket.app.fragment.WebFragment;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.global.LoginHelp;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.IAndJSCallBack;
import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.mutual.js.bean.ThirdLoginUser;
import cn.com.startai.socket.sign.hardware.WiFi.impl.NetworkManager;
import cn.com.startai.socket.sign.js.jsInterface.Language;
import cn.com.startai.socket.sign.js.jsInterface.Login;
import cn.com.startai.socket.sign.js.jsInterface.Router;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.Queue.LimitQueue;
import cn.com.swain.baselib.util.PermissionRequest;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/28 0028
 * desc :
 * <p>
 * View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。
 * View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。
 * View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
 * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
 * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 * View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
 * View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
 */
public class HomeActivity extends AppCompatActivity implements IAndJSCallBack,
        WebFragment.IWebFragmentCallBack, PermissionRequest.OnPermissionFinish {

    private static final String TAG = SocketApplication.TAG;

    private UiHandler mUiHandler;


    // activity onPause
    private volatile boolean mActPause = false;
    // webFragment show
    private volatile boolean mWebShowed = false;
    // webView loaded
    private volatile boolean mWebLoaded = false;

    private PermissionRequest mPermissionRequest;

    private final ArrayList<BaseFragment> mFragments = new ArrayList<>(2);
    private static final int ID_GUIDE = 0x00;
    private static final int ID_WEB = 0x01;

    private int mCurFrame = ID_GUIDE;

    private void hideStatsBar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int model;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN //隐藏状态栏
            );

            model =
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏导航图标
            ;

//            model |= decorView.getSystemUiVisibility();

//            //在使用LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES的时候，状态栏会显示为白色，这和主内容区域颜色冲突,
//            //所以我们要开启沉浸式布局模式，即真正的全屏模式,以实现状态和主体内容背景一致
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            window.setAttributes(lp);

        } else {

            model = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        }

        decorView.setSystemUiVisibility(model);
    }

    private void showStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();


            window.clearFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 隐藏导航图标
            );

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //api21新增接口
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    private volatile boolean showStatusBar = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mUiHandler != null) {
            mUiHandler.obtainMessage(MSG_WHAT_CHANGE_STATUS_BAR, showStatusBar).sendToTarget();
        }
        Tlog.v(TAG, "HomeActivity  onWindowFocusChanged() ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        Tlog.v(TAG, "HomeActivity  onCreate() ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            BleScanPermissionUtil.initPermission(this);
//            FilePermissionUtil.initPermission(this);

            String[] permissionArray = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

            int[] res = new int[]{
                    R.string.file_allow_permission,
                    R.string.scan_ble_allow_permission,
            };

            Tlog.v(TAG, "HomeActivity  requestPermission() ");
            mPermissionRequest = new PermissionRequest(this, this, permissionArray, res);
            mPermissionRequest.requestPermission();

        }


        if (mUiHandler == null) {
            Tlog.d(TAG, "activity new UiHandler(this);");
            mUiHandler = new UiHandler(this);
        }

//        startService(new Intent(this, CoreService.class));

        Tlog.v(TAG, " Controller hashCode: " + Controller.getInstance().hashCode());

        Controller.getInstance().init(getApplication(), this);
        Controller.getInstance().onSCreate();

        restoreFragment(savedInstanceState);

        Tlog.d(TAG, "activity mFragment : " + mFragments.hashCode());
    }

    private synchronized void restoreFragment(Bundle savedInstanceState) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment fragmentGuide = getFragmentByCache(savedInstanceState, String.valueOf(ID_GUIDE));
        if (fragmentGuide == null) {
            Tlog.w(TAG, " mFragments add new guideFragment");
            mFragments.add(ID_GUIDE, new GuideFragment());
            fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_GUIDE), String.valueOf(ID_GUIDE));
        } else {
            Tlog.w(TAG, " mFragments add cache guideFragment");
            if (mFragments.size() <= ID_GUIDE) {
                mFragments.add(ID_GUIDE, (BaseFragment) fragmentGuide);
            }
        }

        Fragment fragmentWeb = getFragmentByCache(savedInstanceState, String.valueOf(ID_WEB));
        if (fragmentWeb == null) {
            Tlog.w(TAG, " mFragments add new webFragment");
            mFragments.add(ID_WEB, new WebFragment());
            fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_WEB), String.valueOf(ID_WEB));
        } else {
            Tlog.w(TAG, " mFragments add cache webFragment");
            if (mFragments.size() <= ID_WEB) {
                mFragments.add(ID_WEB, (BaseFragment) fragmentWeb);
            }
        }

        if (mCurFrame == ID_GUIDE) {
            fragmentTransaction.show(mFragments.get(ID_GUIDE));
            fragmentTransaction.hide(mFragments.get(ID_WEB));
        } else if (mCurFrame == ID_WEB) {
            fragmentTransaction.hide(mFragments.get(ID_GUIDE));
            fragmentTransaction.show(mFragments.get(ID_WEB));
        }

        fragmentTransaction.commit();

    }


    private Fragment getFragmentByCache(Bundle savedInstanceState, String tag) {
        Fragment fragmentByTag = null;
        if (savedInstanceState != null) {
            fragmentByTag = getSupportFragmentManager().getFragment(savedInstanceState, tag);
        }
        if (fragmentByTag == null) {
            fragmentByTag = getSupportFragmentManager().findFragmentByTag(tag);
        }
        return fragmentByTag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tlog.v(TAG, "HomeActivity onResume() ");
        mActPause = false;
        if (!mWebShowed && mWebLoaded) {
            showWeb(600);
        }
        Controller.getInstance().onSResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Tlog.v(TAG, "HomeActivity onPause() ");
        mActPause = true;
        Controller.getInstance().onSPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tlog.v(TAG, "HomeActivity onConfigurationChanged () " + newConfig.locale.getLanguage());
        String type = Language.changeSystemLangToH5Lang(newConfig.locale.getLanguage());
        String method = Language.Method.callJsSetSystemLanguage(true, type);
        ajLoadJs(method);
    }

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
        Tlog.v(TAG, "HomeActivity onBackPressed() ");

        if (status) {
            String method = Router.Method.callJsPressBack();
            ajLoadJs(method);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Tlog.d(TAG, "HomeActivity onRestoreInstanceState() ");
        mCurFrame = ID_WEB;
        restoreFragment(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Tlog.d(TAG, "HomeActivity onSaveInstanceState() ");

        if (mFragments.size() > ID_GUIDE) {
            Tlog.d(TAG, "HomeActivity onSaveInstanceState() putFragment:" + String.valueOf(ID_GUIDE));
            getSupportFragmentManager().putFragment(outState, String.valueOf(ID_GUIDE), mFragments.get(ID_GUIDE));
        }

        if (mFragments.size() > ID_WEB) {
            Tlog.d(TAG, "HomeActivity onSaveInstanceState() putFragment:" + String.valueOf(ID_WEB));
            getSupportFragmentManager().putFragment(outState, String.valueOf(ID_WEB), mFragments.get(ID_WEB));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, "HomeActivity onDestroy() ");
        if (mPermissionRequest != null) {
            mPermissionRequest.release();
        }

        Controller.getInstance().onSDestroy();
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
            mUiHandler.release();
            mUiHandler = null;
        }
//        stopService(new Intent(this, CoreService.class));

        mFragments.clear();
        mMethodCache.clear();

    }

    @Override
    public void onPermissionRequestFinish() {

        Tlog.v(TAG, "HomeActivity onPermissionRequestFinish() ");

        FileManager.getInstance().recreate(getApplication());
        Debuger.getInstance().reCheckLogRecord(this);
        if (mPermissionRequest != null) {
            mPermissionRequest.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mPermissionRequest != null) {
            mPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /*******************/

    @Override
    public void ajMainGoBack() {
        Tlog.e(TAG, "ajMainGoBack");

        Controller.getInstance().onSFinish();
        if (mUiHandler != null) {
            mUiHandler.sendEmptyMessageDelayed(MSG_WHAT_FINISH, 500);
        }
    }

//    private final ArrayList<String> mMethodCache = new ArrayList<>();

    private final Queue<String> mMethodCache = new LimitQueue<>(12);

    @Override
    public void ajLoadJs(String method) {

        if (mWebShowed) {
            if (mUiHandler != null) {
                mUiHandler.obtainMessage(MSG_WHAT_LOAD_JS, method).sendToTarget();
            } else {
                Tlog.e(TAG, " ajLoadJs mUiHandler is null; " + method);
            }
        } else {

            Tlog.e(TAG, " ajLoadJs() web not showed; add to cache: " + method);
            mMethodCache.offer(method);

        }

    }

    @Override
    public void skipProductDetection(String conMac) {
        Intent i = new Intent(this, ProductDetectionActivity.class);
        i.putExtra(ProductDetectionActivity.NAME_CUR_DEVICE, conMac);
        startActivity(i);
    }

    private LoginHelp mLoginHelp;

    @Override
    public void login(String type) {

        if (mLoginHelp == null) {
            mLoginHelp = LoginHelp.getInstance();
            mLoginHelp.regLoginCallBack(new LoginHelp.OnLoginResult() {
                @Override
                public void onResult(boolean result, ThirdLoginUser mUser) {


                    String data;

                    if (mUser != null) {
                        Tlog.v(H5Config.TAG, " loginResult :" + mUser.toString());
                        data = mUser.toJsonStr();
                    } else {
                        data = "{}";
                    }

                    String method = Login.Method.callJsThirdLogin(result, data);
                    ajLoadJs(method);

                }
            });

        }

        if (type.equalsIgnoreCase(Login.TYPE_LOGIN_FACEBOOK)) {

            mLoginHelp.loginFacebook(this);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_GOOGLE)) {

            mLoginHelp.loginGoogle(this);

        } else if (type.equalsIgnoreCase(Login.TYPE_LOGIN_TWITTER)) {

            mLoginHelp.loginTwitter(this);

        } else {
            Tlog.e(H5Config.TAG, " login unknown type ");
        }

    }

    private boolean status = true;

    @Override
    public void ajDisableGoBack(boolean status) {
        this.status = status;
        BaseFragment baseFragment = mFragments.get(ID_WEB);
        if (baseFragment != null) {
            WebFragment mWebFragment = (WebFragment) baseFragment;
            mWebFragment.disableGoBack(status);
        } else {
            Tlog.e(TAG, " ajDisableGoBack baseFragment=null");
        }
    }

    @Override
    public void onAjStartActivityForResult(Intent intent, int requestPhotoCode) {
        Tlog.d(TAG, "onAjStartActivityForResult " + requestPhotoCode);
        this.startActivityForResult(intent, requestPhotoCode);
    }

    private static final int SKIP_WEB = 0x6352;

    @Override
    public void ajSkipWebActivity(String path) {
        Tlog.d(TAG, "ajSkipWebActivity " + path);

        if (path == null) {
            Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        ajDisableGoBack(false);
        Intent i = new Intent(this, WebActivity.class);
        i.putExtra("path", path);
        startActivityForResult(i, SKIP_WEB);

    }


    @Override
    public void ajSetStatusBar(StatusBarBean mStatusBar) {
        Tlog.d(TAG, "ajSetStatusBar " + mStatusBar.toString());
        if (mUiHandler != null) {
            if (mStatusBar.show) {
                mUiHandler.obtainMessage(MSG_WHAT_CHANGE_STATUS_BAR, true).sendToTarget();
            } else {
                mUiHandler.obtainMessage(MSG_WHAT_CHANGE_STATUS_BAR, false).sendToTarget();
            }
        }
    }

    /*******************/

    @Override
    public void onWebLoadFinish() {
        mWebLoaded = true;
        showWeb(500);
    }

    @Override
    public String getLoadUrl() {

        File localH5Resource = Debuger.getInstance().getLocalH5Resource();
        if (localH5Resource != null) {
            Toast.makeText(getApplicationContext(), "load from sdcard", Toast.LENGTH_LONG).show();
            return "file://" + localH5Resource.getAbsolutePath();
        } else {
            return H5Config.URL_H5_SOCKET_INDEX;
        }
    }

    private void showWeb(long delay) {
        if (!mActPause) {
            if (mUiHandler != null) {
                mUiHandler.sendEmptyMessageDelayed(MSG_WHAT_WEB_VIEW_LOAD_FINISH, delay);
            }
        } else {
            Tlog.e(TAG, " onWebLoadFinish activity is pause . ");
        }
    }

    /*******************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tlog.d(TAG, " onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);

        if (requestCode == SKIP_WEB) {
            Tlog.e(TAG, " onActivityResult is from webActivity");
            ajDisableGoBack(true);
            return;
        }

        if (mLoginHelp != null) {
            mLoginHelp.onActivityResult(requestCode, resultCode, data);
        }

        NetworkManager networkManager = Controller.getInstance().getNetworkManager();
        if (networkManager != null) {
            networkManager.onActivityResult(requestCode, resultCode, data);
        } else {
            Tlog.e(TAG, " NetworkManager == null ");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Tlog.e(TAG, " onTouchEvent event:" + event.getX() + " " + event.getY());
        return super.onTouchEvent(event);
    }

    /*******************/

    private static final int MSG_WHAT_WEB_VIEW_LOAD_FINISH = 0x02;

    private static final int MSG_WHAT_LOAD_JS = 0x01;

    private static final int MSG_WHAT_FINISH = 0x03;

    private static final int MSG_WHAT_CHANGE_STATUS_BAR = 0x04;


    private void handleMessage(Message msg) {

        if (msg.what == MSG_WHAT_LOAD_JS) {

//            Tlog.d(TAG, "handleMessage mFragment : " + mFragments.hashCode());

            BaseFragment baseFragment = mFragments.get(ID_WEB);
            if (baseFragment != null) {
                WebFragment mWebFragment = (WebFragment) baseFragment;
                String method = (String) msg.obj;
                mWebFragment.loadJs(method);
            } else {
                Tlog.e(TAG, " loadJs baseFragment=null");
            }

        } else if (msg.what == MSG_WHAT_WEB_VIEW_LOAD_FINISH) {

            if (!mActPause && !mWebShowed && mFragments.size() > ID_WEB) {
                mWebShowed = true;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mFragments.get(ID_GUIDE));
                fragmentTransaction.show(mFragments.get(ID_WEB));
                fragmentTransaction.commit();
                mCurFrame = ID_WEB;

                Tlog.d(TAG, " show mWebFragment ");

                if (mMethodCache.size() > 0) {

                    String method = null;
                    while ((method = mMethodCache.poll()) != null) {
                        Tlog.e(TAG, " load cache : " + method);
                        ajLoadJs(method);
                    }
                }

            } else {
                Tlog.e(TAG, "WebView loaded ; pause:" + mActPause + " show:" + mWebShowed + " " + mFragments.size());
            }

        } else if (msg.what == MSG_WHAT_FINISH) {

            Tlog.e(TAG, "handleMessage finish ");

            this.finish();

        } else if (msg.what == MSG_WHAT_CHANGE_STATUS_BAR) {
            showStatusBar = (Boolean) (msg.obj);
            if (showStatusBar) {
                showStatusBar();
            } else {
                hideStatsBar();
            }
        }

    }

    private static class UiHandler extends Handler {

        private WeakReference<HomeActivity> wr;

        public void release() {
            if (wr != null) {
                wr.clear();
            }
            wr = null;
        }


        UiHandler(HomeActivity act) {
            super(Looper.getMainLooper());

            wr = new WeakReference<>(act);

        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            HomeActivity act;

            if (wr != null && (act = wr.get()) != null) {
                act.handleMessage(msg);
            } else {
                Tlog.e(TAG, "<UiHandler> HomeActivity == null ");
            }

        }
    }

}