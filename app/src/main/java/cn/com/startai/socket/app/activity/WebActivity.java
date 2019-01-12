package cn.com.startai.socket.app.activity;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xwalk.core.XWalkHttpAuthHandler;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.view.CrossWebView;
import cn.com.startai.socket.app.view.DialogUtils;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.util.StatusBarUtil;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class WebActivity extends AppCompatActivity {

    protected String TAG = "chromium";

    private CrossWebView mWebView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.fullScreenHideStatusBar(getWindow(), false);
        }

        setContentView(R.layout.activity_web);

        String path = getIntent().getStringExtra("path");
        if (path == null) {
            Toast.makeText(getApplicationContext(), "UNKNOWN_URL", Toast.LENGTH_SHORT).show();
            finish();
        }

        mProgressBar = findViewById(R.id.progressBarLarge);
        mProgressBar.bringToFront();

        mWebView = findViewById(R.id.web_view);

        mWebView.setUIClient(new MXWalkUIClient(mWebView));
        mWebView.setResourceClient(new MXWalkResourceClient(mWebView));
        mWebView.disableGoBack(false);
        Tlog.v(TAG, " WebActivity loadUrl: " + path);
        mWebView.loadUrl(path);

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
            mWebView.onDestroy();
            mWebView = null;
        }
    }

    private class MXWalkResourceClient extends XWalkResourceClient {

        public MXWalkResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onLoadStarted() " + url);
            }
            super.onLoadStarted(view, url);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onLoadFinished() " + url);
            }
            super.onLoadFinished(view, url);

        }

        //        true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载
        //        false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity shouldOverrideUrlLoading() " + url);
            }

            if (url.startsWith("http:") || url.startsWith("https:")) {

//                view.loadUrl(url);
//                return false;

                return super.shouldOverrideUrlLoading(view, url);

            } else {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
                return true;
            }

//            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onReceivedLoadError() "
                        + String.valueOf(errorCode)
                        + " description:" + description
                        + " failingUrl:" + failingUrl);
            }
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onReceivedSslError() " + String.valueOf(error));
            }
            super.onReceivedSslError(view, callback, error);
        }

        @Override
        public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onReceivedHttpAuthRequest() " + String.valueOf(host) + " realm:" + realm);
            }
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {

            if (Debuger.isLogDebug) {
                StringBuilder sb = new StringBuilder();
                sb.append(" getMethod: ");
                sb.append(request.getMethod());

                sb.append(", getUrl: ");
                sb.append(request.getUrl());

                sb.append(", getRequestHeaders: ");
                sb.append(String.valueOf(request.getRequestHeaders()));

                Tlog.e(TAG, "WebActivity shouldInterceptLoadRequest() " + sb.toString());

            }

            return super.shouldInterceptLoadRequest(view, request);
        }


    }

    private class MXWalkUIClient extends XWalkUIClient {

        MXWalkUIClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            super.onPageLoadStarted(view, url);
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "WebActivity onPageLoadStarted() " + url);
            }
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
            super.onPageLoadStopped(view, url, status);
            if (Debuger.isLogDebug) {
                Tlog.d(TAG, "WebActivity onPageLoadStopped() " + url);
            }
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onJsAlert() " + message);
            }
            DialogUtils.alert(WebActivity.this, message, result);
            return true;
        }

        @Override
        public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onJsConfirm() ");
            }
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue,
                                  XWalkJavascriptResult result) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onJsPrompt() ");
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url,
                                               String message, String defaultValue, XWalkJavascriptResult result) {
            if (Debuger.isLogDebug) {
                Tlog.v(TAG, "WebActivity onJavascriptModalDialog() ");
            }
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }


    }


}
