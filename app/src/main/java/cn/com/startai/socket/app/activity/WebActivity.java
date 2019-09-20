package cn.com.startai.socket.app.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.view.CrossWebView;
import cn.com.startai.socket.app.view.DialogUtils;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.swain.baselib.display.StatusBarUtil;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class WebActivity extends AppCompatActivity {

    protected String TAG = "chromium";

    private CrossWebView mWebView;

    private ProgressBar mProgressBar;

    private long loadUrlTs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.fullScreenHideStatusBar(getWindow(), false);
        }


        String path = getIntent().getStringExtra("path");
        if (path == null) {
            Toast.makeText(getApplicationContext(), "UNKNOWN_URL", Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_web);

        mProgressBar = findViewById(R.id.progressBarLarge);
        mProgressBar.bringToFront();

        mWebView = findViewById(R.id.web_view);

        this.mWebView.setWebViewClient(new MWebViewClient());

        this.mWebView.setWebChromeClient(new MWebChromeClient());

        this.mWebView.setDownloadListener(new MDownloadListener());

        mWebView.disableGoBack(false);
        Tlog.v(TAG, " WebActivity loadUrl: " + path);
        mWebView.loadUrl(path);
        loadUrlTs = System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        releaseWeb();
        super.onDestroy();
    }


    public void releaseWeb() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private class MDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String arg0, String arg1, String arg2,
                                    String arg3, long arg4) {
        }
    }

    private class MWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                   JsResult arg3) {
            return super.onJsConfirm(arg0, arg1, arg2, arg3);
        }

        /**
         * 全屏播放配置
         */
        @Override
        public void onShowCustomView(View view,
                                     IX5WebChromeClient.CustomViewCallback customViewCallback) {
            super.onShowCustomView(view, customViewCallback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }


        @Override
        public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                 JsResult arg3) {
            //这里写入自定义的window alert
            DialogUtils.alert(WebActivity.this, arg2, arg3);
            return super.onJsAlert(arg0, arg1, arg2, arg3);
        }

    }

    private class MWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return false;
            if (url.startsWith("http:") || url.startsWith("https:")) {

//                view.loadUrl(url);
//                return false;

                return super.shouldOverrideUrlLoading(view, url);

            } else {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
                return true;
            }
        }

        private boolean firstLoad = true;
        private long pageStarted;

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            if (Debuger.isLogDebug && firstLoad) {
                pageStarted = System.currentTimeMillis();
                Tlog.d(TAG, "WebActivity first page load started take up time:"
                        + (pageStarted - loadUrlTs));
            }
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            super.onPageStarted(webView, s, bitmap);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (Debuger.isLogDebug && firstLoad) {
                Tlog.d(TAG, "WebActivity first page load finish take up time:"
                        + (System.currentTimeMillis() - pageStarted));
                Tlog.d(TAG, "WebActivity first load url finish take up time:"
                        + (System.currentTimeMillis() - loadUrlTs));
            }
            firstLoad = false;
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            super.onPageFinished(view, url);
        }
    }


}
