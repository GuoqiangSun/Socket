package cn.com.startai.socket.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.com.swain.baselib.log.Tlog;

public class CrossWebView extends WebView {


    public CrossWebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public CrossWebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);

        WebViewClient client = new WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };
        this.setWebViewClient(client);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {
        Context context = getContext();
        initWebViewSettings(context.getDir("appcache", 0).getPath(),
                context.getDir("databases", 0).getPath(),
                context.getDir("geolocation", 0).getPath());
    }

    private void initWebViewSettings(String appcache, String dbcache, String geocache) {

        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);

        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        if (appcache != null) {
            webSetting.setAppCachePath(appcache);
        }
        if (dbcache != null) {
            webSetting.setDatabasePath(dbcache);
        }
        if (geocache != null) {
            webSetting.setGeolocationDatabasePath(geocache);
        }
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        // extension settings 的设计

    }

    private boolean status = true;

    public void disableGoBack(boolean status) {
        this.status = status;
        Tlog.e(" navigationItem disableGoBack status: " + status);
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
            } else {
                if (canGoBack()) {
                    goBack();
                    return true;
                }else {
                    return super.dispatchKeyEvent(event);
                }
            }
        }
        return super.dispatchKeyEvent(event);

    }
}
