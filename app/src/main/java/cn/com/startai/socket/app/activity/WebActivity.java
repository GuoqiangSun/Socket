package cn.com.startai.socket.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.xwalk.core.XWalkUIClient;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.app.view.CrossWebView;
import cn.com.swain.baselib.util.StatusBarUtil;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class WebActivity extends AppCompatActivity {

    protected String TAG = SocketApplication.TAG;

    private CrossWebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.fullScreenHideStatusBar(getWindow(), false);

        setContentView(R.layout.framgment_web);

        String path = getIntent().getStringExtra("path");
        if (path == null) {
            finish();
        }

        mWebView = findViewById(R.id.web_view);
        mWebView.setUIClient(new XWalkUIClient(mWebView));
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

}
