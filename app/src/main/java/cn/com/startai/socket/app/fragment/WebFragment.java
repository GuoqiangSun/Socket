package cn.com.startai.socket.app.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.crash.h5.H5JavaScriptInterface;

import org.xwalk.core.XWalkHttpAuthHandler;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.activity.XWalkWebActivity;
import cn.com.startai.socket.app.view.CrossWebView;
import cn.com.startai.socket.app.view.DialogUtils;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.sign.js.impl.JsManager;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class WebFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " WebFragment onCreate() " + hashCode());
    }

    private CrossWebView mXWWebView;

    private volatile boolean firstAdd = true;

    private IWebFragmentCallBack mCallBack;

    public void onXwalReady() {
        onXwalkReady(getRootView());
    }

    private synchronized void onXwalkReady(View mRootView) {
        Tlog.w(TAG, " WebFragment onXwalReady() exe ");
        if (mXWWebView == null) {
            Activity activity = getActivity();
            mCallBack = (IWebFragmentCallBack) activity;
            if (mCallBack != null) {
                String loadUrl = mCallBack.getLoadUrl();
                if (((XWalkWebActivity)activity).isXWalkReady()) {
                    initXW(mRootView, loadUrl);
                } else {
                    Tlog.w(TAG, " WebFragment isXReady false ");
                }
            } else {
                Tlog.w(TAG, " WebFragment onXwalkReady mCallBack=null ");
            }
        } else {
            Tlog.w(TAG, " WebFragment onXwalkReady mXWWebView=null ");
        }
    }


    @Override
    protected View inflateView() {
        Tlog.v(TAG, " WebFragment inflateView() " + hashCode());
        View mRootView = View.inflate(getActivity(), R.layout.framgment_web_xwalk,
                null);

        onXwalkReady(mRootView);

        return mRootView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " WebFragment onCreateView() " + +hashCode());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Tlog.v(TAG, " WebFragment onDestroyView() ");
        releaseWeb();
        super.onDestroyView();
    }

    public void releaseWeb() {
        if (mXWWebView != null) {
            mXWWebView.stopLoading();
            mXWWebView.onDestroy();
            mXWWebView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " WebFragment onDestroy() ");
    }

    public void disableGoBack(boolean status) {
        if (mXWWebView != null) {
            mXWWebView.disableGoBack(status);
        }
    }

    public void loadJs(String method) {
        if (Debuger.isLogDebug) {
            Tlog.e(H5Config.TAG, String.valueOf(method));
        }
        if (mXWWebView != null) {
            mXWWebView.loadUrl(method);
        } else {
            Tlog.e(TAG, "WebFragment loadJs mWebView=null ");
        }
    }


    private void initXW(View mRootView, String loadUrl) {


//        mXWWebView = mRootView.findViewById(R.id.web_view);

        mXWWebView = new CrossWebView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ((ViewGroup) mRootView).addView(mXWWebView, params);

        if (loadUrl != null) {

            JsManager mJsManager = Controller.getInstance().getJsManager();
            if (mJsManager != null) {
                mJsManager.regJsInterface(mXWWebView);
            }

            mXWWebView.setUIClient(new MXWalkUIClient(mXWWebView));
            mXWWebView.setResourceClient(new MXWalkResourceClient(mXWWebView));
            mXWWebView.loadUrl(loadUrl);

        } else {
            mXWWebView.setBackgroundColor(Color.parseColor("#ff00ff"));
            if (mCallBack != null) {
                mCallBack.onWebLoadFinish();
            }
        }

    }

    private class MXWalkResourceClient extends XWalkResourceClient {

        public MXWalkResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
            Tlog.v(TAG, "WebFragment shouldOverrideUrlLoading() " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
            Tlog.v(TAG, "WebFragment onReceivedLoadError() "
                    + String.valueOf(errorCode)
                    + " description:" + description
                    + " failingUrl:" + failingUrl);
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
            Tlog.v(TAG, "WebFragment onReceivedSslError() " + String.valueOf(error));
            super.onReceivedSslError(view, callback, error);
        }

        @Override
        public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
            Tlog.v(TAG, "WebFragment onReceivedHttpAuthRequest() " + String.valueOf(host) + " realm:" + realm);
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

                Tlog.v(TAG, "WebActivity shouldInterceptLoadRequest() " + sb.toString());

            }

            return super.shouldInterceptLoadRequest(view, request);
        }
    }

    CrashReport.WebViewInterface mCrashReportWebView = new CrashReport.WebViewInterface() {
        /**
         * 获取WebView URL.
         *
         * @return WebView URL
         */
        @Override
        public String getUrl() {
            // 下面仅为例子，请用真正逻辑代替
            return mXWWebView.getUrl();
        }

        /**
         * 开启JavaScript.
         *
         * @param flag true表示开启，false表示关闭
         */
        @Override
        public void setJavaScriptEnabled(boolean flag) {
            // 下面仅为例子，请用真正逻辑代替
            XWalkSettings settings = mXWWebView.getSettings();
            settings.setJavaScriptEnabled(flag);
        }

        /**
         * 加载URL.
         *
         * @param url 要加载的URL
         */
        @Override
        public void loadUrl(String url) {
            // 下面仅为例子，请用真正逻辑代替
            mXWWebView.loadUrl(url);
        }

        /**
         * 添加JavaScript接口对象.
         *
         * @param jsInterface JavaScript接口对象
         * @param name JavaScript接口对象名称
         */
        @Override
        public void addJavascriptInterface(H5JavaScriptInterface jsInterface, String name) {
            // 下面仅为例子，请用真正逻辑代替
            mXWWebView.addJavascriptInterface(jsInterface, name);
        }

        /**
         * 获取WebView的内容描述.
         *
         * @return WebView的内容描述.
         */
        @Override
        public CharSequence getContentDescription() {
            // 下面仅为例子，请用真正逻辑代替
            return mXWWebView.getContentDescription();
        }
    };
// 调用Bugly设置JS异常捕获接口时传入创建的WebView接口对象即可


    private class MXWalkUIClient extends XWalkUIClient {

        MXWalkUIClient(XWalkView view) {
            super(view);
//            CrashReport.setJavascriptMonitor(mCrashReportWebView, true);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            super.onPageLoadStarted(view, url);
            Tlog.v(TAG, "WebFragment onPageLoadStarted() ");
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
            super.onPageLoadStopped(view, url, status);
            Tlog.v(TAG, "WebFragment onPageLoadStopped() ");
            if (firstAdd) {
                firstAdd = false;
                if (mCallBack != null) {
                    mCallBack.onWebLoadFinish();
                }
            }
        }

        @Override
        public boolean onJsAlert(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
            Tlog.v(TAG, "WebFragment onJsAlert() " + message);
            DialogUtils.alert(getActivity(), message, result);
            return true;
        }

        @Override
        public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
            Tlog.v(TAG, "WebFragment onJsConfirm() ");
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
            Tlog.v(TAG, "WebFragment onJsPrompt() ");
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
            Tlog.v(TAG, "WebFragment onJavascriptModalDialog() ");
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }


    }

    /**
     * author: Guoqiang_Sun
     * date : 2018/4/16 0016
     * desc :
     */
    public interface IWebFragmentCallBack {

        void onWebLoadFinish();

        String getLoadUrl();

    }


}
