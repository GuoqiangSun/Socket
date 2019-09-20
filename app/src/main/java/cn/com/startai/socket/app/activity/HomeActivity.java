package cn.com.startai.socket.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
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
import cn.com.startai.socket.app.service.CoreService;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.IAndJSCallBack;
import cn.com.startai.socket.mutual.js.bean.StatusBarBean;
import cn.com.startai.socket.sign.hardware.manager.AbsHardwareManager;
import cn.com.startai.socket.sign.js.jsInterface.Language;
import cn.com.startai.socket.sign.js.jsInterface.Router;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.Queue.LimitQueue;
import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.baselib.display.StatusBarUtil;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.permission.PermissionGroup;
import cn.com.swain.baselib.permission.PermissionHelper;
import cn.com.swain.baselib.permission.PermissionRequest;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/28 0028
 * desc :
 * <p>
 */
public class HomeActivity extends AppCompatActivity implements IAndJSCallBack,
        WebFragment.IWebFragmentCallBack {

    private static final String TAG = SocketApplication.TAG;

    private UiHandler mUiHandler;

    // activity onPause
    private volatile boolean mActPause = false;
    // webFragment show
    private volatile boolean mWebShowed = false;
    // webView loaded
    private volatile boolean mWebLoaded = false;
    // webView loaded ,can call js
    private volatile boolean mCallJs = false;

    private PermissionRequest mPermissionRequest;

    private final ArrayList<BaseFragment> mFragments = new ArrayList<>(2);
    private static final int ID_GUIDE = 0x00;
    private static final int ID_WEB = 0x01;

    private int mCurFrame = ID_GUIDE;

    private volatile boolean showStatusBar = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mUiHandler != null) {
            mUiHandler.obtainMessage(MSG_WHAT_CHANGE_STATUS_BAR, showStatusBar).sendToTarget();
        }
        Tlog.v(TAG, "HomeActivity  onWindowFocusChanged() ");
    }


    IService IService;

    private long createTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, "HomeActivity  onCreate() ");

        StatusBarUtil.fullscreenShowBarFontWhite(getWindow());

        setContentView(R.layout.activity_home);

        createTs = System.currentTimeMillis();

        if (mUiHandler == null) {
            Tlog.d(TAG, "activity new UiHandler(this);");
            mUiHandler = new UiHandler(this);
        }

        Tlog.d(TAG, "activity mFragment : " + mFragments.hashCode());

        restoreFragment(savedInstanceState);
        requestPermission();

        if (CustomManager.getInstance().isMUSIK()) {
            startService(new Intent(this, CoreService.class));
        }

        Tlog.v(TAG, " Controller hashCode: " + Controller.getInstance().hashCode());

        Controller.getInstance().init(getApplication(), this);
        IService = Controller.getInstance();
        IService.onSCreate();

    }

    private void requestPermission() {

        Context applicationContext = getApplicationContext();

        ArrayList<String> permissions = new ArrayList<>(2);

        if (!PermissionHelper.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || !PermissionHelper.isGranted(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            permissions.add(PermissionGroup.STORAGE);

        } else {
            FileManager.getInstance().recreate(getApplication());
            Debuger.getInstance().reCheckLogRecord(this);
        }

        if (!PermissionHelper.isGranted(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                || !PermissionHelper.isGranted(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Tlog.v(TAG, " permissions.add(PermissionGroup.LOCATION) ");
            permissions.add(PermissionGroup.LOCATION); // 开启蓝牙,wifi配网 需要此权限
        } else {
            Tlog.v(TAG, " has PermissionGroup.LOCATION ");
        }

        if (permissions.size() > 0) {

            String[] per = permissions.toArray(new String[0]);

            if (mPermissionRequest == null) {
                Tlog.v(TAG, "HomeActivity new PermissionRequest() ");
                mPermissionRequest = new PermissionRequest(this);
            }

            mPermissionRequest.requestPermissions(new PermissionRequest.OnPermissionFinish() {
                @Override
                public void onAllPermissionRequestFinish() {
                    Tlog.v(TAG, "HomeActivity onPermissionRequestFinish() ");
                }
            }, new PermissionRequest.OnPermissionResult() {
                @Override
                public boolean onPermissionRequestResult(String permission, boolean granted) {
                    Tlog.v(TAG, "HomeActivity onPermissionRequestResult permission :" + permission + " granted:" + granted);
                    if (granted && PermissionGroup.STORAGE.equalsIgnoreCase(permission)) {
                        FileManager.getInstance().recreate(getApplication());
                        Debuger.getInstance().reCheckLogRecord(HomeActivity.this);
                    } else if (!granted && PermissionGroup.LOCATION.equalsIgnoreCase(permission)) {
                        if (CustomManager.getInstance().isTriggerBle()) {
                            alert();
                        }
                    }
                    return true;
                }
            }, per);
        }
    }

    private void alert() {
        AlertDialog.Builder b = new AlertDialog.Builder(HomeActivity.this);
        b.setTitle(R.string.permission_request_title);
        b.setMessage(R.string.permission_request);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.setCancelable(false);
        b.create().show();
    }

    private void addGuideFragment(FragmentTransaction fragmentTransaction, Bundle savedInstanceState) {
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

    }

    private void addWebFragment(FragmentTransaction fragmentTransaction, Bundle savedInstanceState) {

        Fragment fragmentWeb = getFragmentByCache(savedInstanceState, String.valueOf(ID_WEB));
        if (fragmentWeb == null) {
            mCallJs = false;
            Tlog.w(TAG, " mFragments add new webFragment");
            mFragments.add(ID_WEB, new WebFragment());
            fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_WEB), String.valueOf(ID_WEB));
        } else {
            Tlog.w(TAG, " mFragments add cache webFragment");
            if (mFragments.size() <= ID_WEB) {
                mFragments.add(ID_WEB, (BaseFragment) fragmentWeb);
            }
        }

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
        if (IService != null) {
            IService.onSResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Tlog.v(TAG, "HomeActivity onPause() ");
        mActPause = true;
        if (IService != null) {
            IService.onSPause();
        }
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
        restoreFragment(savedInstanceState);
    }

    private synchronized void restoreFragment(Bundle savedInstanceState) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        addGuideFragment(fragmentTransaction, savedInstanceState);
        addWebFragment(fragmentTransaction, savedInstanceState);

        if (mCurFrame == ID_GUIDE) {
            fragmentTransaction.show(mFragments.get(ID_GUIDE));
            if (mFragments.size() > ID_WEB) {
                fragmentTransaction.hide(mFragments.get(ID_WEB));
            }
        } else if (mCurFrame == ID_WEB) {
            fragmentTransaction.hide(mFragments.get(ID_GUIDE));
            if (mFragments.size() > ID_WEB) {
                fragmentTransaction.show(mFragments.get(ID_WEB));
            }
        }

        if (mActPause) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }

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
            mPermissionRequest = null;
        }

        if (IService != null) {
            IService.onSDestroy();
        }

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

    private final Queue<String> mMethodCache = new LimitQueue<>(Byte.MAX_VALUE);

    @Override
    public void ajLoadJs(String method) {

        if (
//                mWebShowed &&
                mCallJs) {
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
        if (requestPhotoCode <= 0) {
            this.startActivity(intent);
        } else {
            this.startActivityForResult(intent, requestPhotoCode);
        }
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

    @Override
    public Activity getActivity() {
        return this;
    }

    /*******************/

    private static final long MAX_DELAY_SHOW_WEB = 1000 * 3 + 200;//ms

    @Override
    public void onWebLoadFinish() {
        mWebLoaded = true;

        long loadDuration = (System.currentTimeMillis() - createTs);
        long delay;
        if (loadDuration >= MAX_DELAY_SHOW_WEB) {
            delay = 0L;
        } else if (loadDuration <= 0) {
            delay = MAX_DELAY_SHOW_WEB;
        } else {
            delay = MAX_DELAY_SHOW_WEB - loadDuration;
        }

        Tlog.d(TAG, " onWebLoadFinish load:" + loadDuration + " delay " + delay + " show ");

        if (mUiHandler != null) {
            mUiHandler.sendEmptyMessageDelayed(MSG_WHAT_WEB_VIEW_LOAD_FINISH, delay);
        }
    }

    @Override
    public String getLoadUrl() {

        if (Debuger.isH5Debug && PermissionHelper.isGranted(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            File localH5Resource = Debuger.getInstance().getLocalH5Resource();
            if (localH5Resource != null && localH5Resource.exists()) {
                Toast.makeText(getApplicationContext(), "load from sdcard", Toast.LENGTH_LONG).show();
                return "file://" + localH5Resource.getAbsolutePath();
            }
        }

        return H5Config.URL_H5_SOCKET_INDEX;
    }

    private void showWeb(long delay) {
        if (!mActPause) {
            if (mUiHandler != null) {
                mUiHandler.sendEmptyMessageDelayed(MSG_WHAT_SHOW_WEB, delay);
            }
        } else {
            Tlog.e(TAG, " showWeb activity is pause . ");
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

        AbsHardwareManager hardwareManager = Controller.getInstance().getHardwareManager();
        if (hardwareManager != null) {
            hardwareManager.onActivityResult(requestCode, resultCode, data);
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


    private static final int MSG_WHAT_LOAD_JS = 0x01;

    private static final int MSG_WHAT_WEB_VIEW_LOAD_FINISH = 0x02;

    private static final int MSG_WHAT_FINISH = 0x03;

    private static final int MSG_WHAT_CHANGE_STATUS_BAR = 0x04;

    private static final int MSG_WHAT_SHOW_WEB = 0x05;


    private void handleMessage(Message msg) {

        if (msg.what == MSG_WHAT_LOAD_JS) {

//            Tlog.d(TAG, "handleMessage mFragment : " + mFragments.hashCode());

            BaseFragment baseFragment = mFragments.get(ID_WEB);
            if (baseFragment != null) {
                String method = (String) msg.obj;
                ((WebFragment) baseFragment).loadJs(method);
            } else {
                Tlog.e(TAG, " loadJs baseFragment=null");
            }

        } else if (msg.what == MSG_WHAT_WEB_VIEW_LOAD_FINISH) {

            new LoadCacheJsTask(HomeActivity.this).execute();

        } else if (msg.what == MSG_WHAT_FINISH) {

            Tlog.e(TAG, "handleMessage finish ");

            this.finish();

        } else if (msg.what == MSG_WHAT_CHANGE_STATUS_BAR) {
            showStatusBar = (Boolean) (msg.obj);
            if (showStatusBar) {
                StatusBarUtil.fullscreenShowBarFontWhite(getWindow());
            } else {
                StatusBarUtil.fullScreenHideStatusBar(getWindow(), true);
            }
        } else if (msg.what == MSG_WHAT_SHOW_WEB) {
            mCallJs = true;
            if (!mActPause && !mWebShowed && mFragments.size() > ID_WEB) {
                mWebShowed = true;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mFragments.get(ID_GUIDE));
                fragmentTransaction.show(mFragments.get(ID_WEB));
                fragmentTransaction.commit();
                mCurFrame = ID_WEB;
                Tlog.d(TAG, " show mWebFragment ");

            } else {
                Tlog.e(TAG, "WebView loaded ; pause:" + mActPause + " show:" + mWebShowed + " " + mFragments.size());
            }
        }

    }

    private static class LoadCacheJsTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<HomeActivity> wr;

        private LoadCacheJsTask(HomeActivity mHomeActivity) {
            wr = new WeakReference<>(mHomeActivity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Tlog.v(TAG, " LoadCacheJsTask doInBackground");

            HomeActivity homeActivity = wr.get();
            if (homeActivity == null) {
                Tlog.e(TAG, " LoadCacheJsTask homeActivity=null");
                return null;
            }

            Queue<String> mMethodCache = homeActivity.mMethodCache;

            if (mMethodCache.size() > 0) {
                String method = null;
                while ((method = mMethodCache.poll()) != null) {
                    Tlog.e(H5Config.TAG, " poll cache method: " + method);

                    if (homeActivity.mUiHandler != null) {
                        homeActivity.mUiHandler.obtainMessage(MSG_WHAT_LOAD_JS, method).sendToTarget();
                    } else {
                        Tlog.e(TAG, " ajLoadJs mUiHandler is null; " + method);
                    }
                }
            }

            homeActivity.mCallJs = true;
            homeActivity.showWeb(300);

            return null;
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
