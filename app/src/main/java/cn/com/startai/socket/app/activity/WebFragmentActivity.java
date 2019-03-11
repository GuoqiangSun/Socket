package cn.com.startai.socket.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.app.fragment.BaseFragment;
import cn.com.startai.socket.app.fragment.GuideFragment;
import cn.com.startai.socket.app.fragment.WebFragment;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/4 0004
 * desc :
 */
public class WebFragmentActivity extends XWalkWebActivity implements WebFragment.IWebFragmentCallBack {

    protected String TAG = SocketApplication.TAG;

    private final ArrayList<BaseFragment> mFragments = new ArrayList<>(2);
    private static final int ID_GUIDE = 0x00;
    private static final int ID_WEB = 0x01;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragments.add(ID_GUIDE, new GuideFragment());
        fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_GUIDE), String.valueOf(ID_GUIDE));
        if (isXWalkReady()) {
            mFragments.add(ID_WEB, new WebFragment());
            fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_WEB), String.valueOf(ID_WEB));
        }

        if (mFragments.size() > ID_WEB) {
            fragmentTransaction.hide(mFragments.get(ID_WEB));
        }
        fragmentTransaction.show(mFragments.get(ID_GUIDE));

        fragmentTransaction.commit();

    }

    @Override
    protected void onXWalkReady(Bundle savedInstanceState) {
        super.onXWalkReady(savedInstanceState);

        if (mFragments.size() <= ID_WEB) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragments.add(ID_WEB, new WebFragment());
            fragmentTransaction.add(R.id.frame_content, mFragments.get(ID_WEB), String.valueOf(ID_WEB));
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragments.clear();
    }

    @Override
    public void onWebLoadFinish() {

        Tlog.e(TAG, " onWebLoadFinish ");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.hide(mFragments.get(ID_GUIDE));
        fragmentTransaction.show(mFragments.get(ID_WEB));

        fragmentTransaction.commit();
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
}
