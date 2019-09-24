package cn.com.startai.socket.app.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


import cn.com.startai.socket.R;
import cn.com.startai.socket.app.view.X5WebView;
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

    private X5WebView mXWWebView;

    private volatile boolean firstAdd = true;

    private IWebFragmentCallBack mCallBack;

    private synchronized void onXwalkReady(View mRootView) {
        Tlog.w(TAG, " WebFragment onXwalReady() exe ");
        Activity activity = getActivity();
        mCallBack = (IWebFragmentCallBack) activity;
        if (mCallBack != null) {
            String loadUrl = mCallBack.getLoadUrl();
            initXW(mRootView, loadUrl);
        } else {
            Tlog.w(TAG, " WebFragment onXwalkReady mCallBack=null ");
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
    public void onResume() {
        super.onResume();
        if (mXWWebView != null) {
            mXWWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mXWWebView != null) {
            mXWWebView.onPause();
        }
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
            mXWWebView.destroy();
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

    private long loadUrlTs;

    private void initXW(View mRootView, String loadUrl) {

        Tlog.v(TAG, " initXW::" + loadUrl);

        mXWWebView = mRootView.findViewById(R.id.web_view);

//        mXWWebView = new CrossWebView(getContext());
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        ((ViewGroup) mRootView).addView(mXWWebView, params);


        JsManager mJsManager = Controller.getInstance().getJsManager();
        if (mJsManager != null) {
            mJsManager.regJsInterface(mXWWebView);
        }

        loadUrlTs = System.currentTimeMillis();

        this.mXWWebView.setWebViewClient(new MWebViewClient());

        this.mXWWebView.setWebChromeClient(new MWebChromeClient());

        this.mXWWebView.setDownloadListener(new MDownloadListener());

        if (loadUrl != null) {
            mXWWebView.loadUrl(loadUrl);
        } else {
            mXWWebView.setBackgroundColor(Color.parseColor("#ff00ff"));
            firstAdd = false;
            if (mCallBack != null) {
                mCallBack.onWebLoadFinish();
            }
        }

        CookieSyncManager.createInstance(getContext());
        CookieSyncManager.getInstance().sync();

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
            DialogUtils.alert(getActivity(), arg2, arg3);
            return super.onJsAlert(arg0, arg1, arg2, arg3);
        }

    }

    private class MWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        private boolean firstLoad = true;
        private long pageStarted;

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            if (Debuger.isLogDebug && firstLoad) {
                pageStarted = System.currentTimeMillis();
                Tlog.d(TAG, "WebFragment first page load started take up time:"
                        + (pageStarted - loadUrlTs));
            }
            super.onPageStarted(webView, s, bitmap);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (Debuger.isLogDebug && firstLoad) {
                Tlog.d(TAG, "WebFragment first page load finish take up time:"
                        + (System.currentTimeMillis() - pageStarted));
                Tlog.d(TAG, "WebFragment first load url finish take up time:"
                        + (System.currentTimeMillis() - loadUrlTs));
                firstLoad = false;
            }

            if (firstAdd) {
                firstAdd = false;
                if (mCallBack != null) {
                    mCallBack.onWebLoadFinish();
                }
            }
            super.onPageFinished(view, url);
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
