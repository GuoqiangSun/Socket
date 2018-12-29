package cn.com.startai.socket.app.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import cn.com.startai.socket.R;
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

    private CrossWebView mWebView;

    private volatile boolean firstAdd = true;

    private IWebFragmentCallBack mCallBack;

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " WebFragment inflateView() " + hashCode());
        View mRootView = View.inflate(getActivity(), R.layout.framgment_web,
                null);

        Activity activity = getActivity();
        if (activity instanceof IWebFragmentCallBack) {
            mCallBack = (IWebFragmentCallBack) activity;
        }
        String loadUrl = mCallBack.getLoadUrl();


        mWebView = mRootView.findViewById(R.id.web_view);

        if (loadUrl != null) {

            JsManager mJsManager = Controller.getInstance().getJsManager();
            if (mJsManager != null) {
                mJsManager.regJsInterface(mWebView);
            }

            mWebView.setUIClient(new MXWalkUIClient(mWebView));
            mWebView.loadUrl(loadUrl);
        } else {
            mWebView.setBackgroundColor(Color.parseColor("#ff00ff"));
            if (mCallBack != null) {
                mCallBack.onWebLoadFinish();
            }
        }

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
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.onDestroy();
            mWebView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " WebFragment onDestroy() ");
    }

    public void disableGoBack(boolean status) {
        if (mWebView != null) {
            mWebView.disableGoBack(status);
        }
    }

    public void loadJs(String method) {
        if (mWebView != null) {
            if (Debuger.isLogDebug) {
                Tlog.e(H5Config.TAG, method);
            }
            mWebView.loadUrl(method);
        } else {
            Tlog.e(TAG, "WebFragment loadJs mWebView=null ");
        }
    }

    private class MXWalkUIClient extends XWalkUIClient {

        MXWalkUIClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            super.onPageLoadStarted(view, url);
            Tlog.v(TAG, "MXWalkUIClient onPageLoadStarted() ");
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
            super.onPageLoadStopped(view, url, status);
            Tlog.v(TAG, "MXWalkUIClient onPageLoadStopped() ");
            if (firstAdd) {
                firstAdd = false;
//                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
                if (mCallBack != null) {
                    mCallBack.onWebLoadFinish();
                }
            }
        }

        @Override
        public boolean onJsAlert(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
            Tlog.v(TAG, "MXWalkUIClient onJsAlert() " + message);
            DialogUtils.alert(getActivity(), message, result);
            return true;
        }

        @Override
        public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
            Tlog.v(TAG, "MXWalkUIClient onJsConfirm() ");
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
            Tlog.v(TAG, "MXWalkUIClient onJsPrompt() ");
            return super.onJsPrompt(view, url, message, defaultValue, result);
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
