package cn.com.startai.socket.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkNavigationItem;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import java.util.Locale;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/28 0028
 * desc :
 */

public class CrossWebView extends XWalkView {

    private static final String TAG = SocketApplication.TAG;

    public CrossWebView(Context context) {
        super(context);
        init();
    }

    public CrossWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        // crossWalk 在浏览器调试
        if (Debuger.isDebug || Debuger.isH5Debug) {
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        }


        setDrawingCacheEnabled(true);
        setChildrenDrawingCacheEnabled(true);

        XWalkSettings settings = getSettings();

        // 支持 JavaScript
//        settings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //缩放操作
        settings.setSupportZoom(false);


        //其他细节操作
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        //js弹窗
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        settings.setSupportMultipleWindows(true);
        settings.setAllowFileAccessFromFileURLs(true);
        // 跨域
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setLoadsImagesAutomatically(true);

        settings.setDomStorageEnabled(true);
        settings.setCacheMode(XWalkSettings.LOAD_NO_CACHE);

        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private final String JAVA_SCRIPT = "javascript:";

    @Deprecated
    public void loadJs(String url, Object... args) {
        try {

            if (!url.startsWith(JAVA_SCRIPT)) {
                url = JAVA_SCRIPT + url;
            }

            String formatUrl = String.format(Locale.CHINA, url, args);
//            Tlog.d(TAG, getUrl() + "\n" + formatUrl);
            this.loadUrl(formatUrl);

        } catch (Exception e) {
            e.printStackTrace();

            Tlog.e(TAG, " loadJs Exception : ", e);

        }
    }

    private boolean status = true;

    public void disableGoBack(boolean status) {
        this.status = status;
        Tlog.e(TAG, " navigationItem disableGoBack status: " + status);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (!status) {
//            // 禁止返回按键
//            return super.dispatchKeyEvent(event);
//        }
//        return event.getKeyCode() != KeyEvent.KEYCODE_BACK && super.dispatchKeyEvent(event);


        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (status) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);


    }

    private boolean goforward(){

        // Go forward
        if (
                getNavigationHistory().canGoForward()) {
            getNavigationHistory().navigate(
                    XWalkNavigationHistory.Direction.FORWARD, 1);
            return false;
        }
        return true;
    }

    private boolean goback() {
        if (getNavigationHistory().canGoBack()) {
//            XWalkNavigationItem navigationItem = getNavigationHistory().getCurrentItem();
            getNavigationHistory().navigate(
                    XWalkNavigationHistory.Direction.BACKWARD, 1);
            Tlog.d(TAG, " navigationItem navigate: BACKWARD ");
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
