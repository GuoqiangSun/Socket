package cn.com.startai.socket.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.app.adapter.DetectionRecyclerAdapter;
import cn.com.startai.socket.app.adapter.FragmentPagerAdapter;
import cn.com.startai.socket.app.fragment.CheckThreadFragment;
import cn.com.startai.socket.app.fragment.ColorLightFragment;
import cn.com.startai.socket.app.fragment.DBFragment;
import cn.com.startai.socket.app.fragment.DetectionFragment;
import cn.com.startai.socket.app.fragment.DetectionReportFragment;
import cn.com.startai.socket.app.fragment.RGBFragment;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.debuger.impl.DetectInfo;
import cn.com.startai.socket.debuger.impl.IProductDetectionCallBack;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/16 0016
 * desc :
 */
public class ProductDetectionActivity extends AppCompatActivity implements IProductDetectionCallBack {

    private String TAG = SocketApplication.TAG;

    public static final String NAME_CUR_DEVICE = "curDevice";
    private String curDevice;


    private ProductDetectionManager productDetectionManager;

    private DetectionReportFragment mDetectionReportFragment;
    private DetectionFragment mDetectionFragment;
    private CheckThreadFragment mTmpFunctionFragment;
    private DBFragment mDbFragment;

    private RGBFragment mRGBFragment;
    private ColorLightFragment mColorLightFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        Tlog.v(TAG, "ProductDetectionActivity onCreate() ");

        curDevice = getIntent().getStringExtra(NAME_CUR_DEVICE);
        Debuger.getInstance().skipProduceDetection(getApplicationContext(), true, curDevice);
        Toast.makeText(getApplicationContext(), R.string.skip_product_detection, Toast.LENGTH_SHORT).show();
        productDetectionManager = Debuger.getInstance().newProductDetectionManager(this);
        productDetectionManager.setDetectDevice(curDevice);
        productDetectionManager.onSCreate();

        ViewPager mFrameVp = findViewById(R.id.frame_viewpager);
        ArrayList<Fragment> mFragments = new ArrayList<>();

        mDetectionFragment = new DetectionFragment();
        mDetectionReportFragment = new DetectionReportFragment();
        mTmpFunctionFragment = new CheckThreadFragment();
        mDbFragment = new DBFragment();


        mFragments.add(mDetectionFragment);
        mFragments.add(mDetectionReportFragment);
        mFragments.add(mTmpFunctionFragment);
        mFragments.add(mDbFragment);

        if (CustomManager.getInstance().isMUSIK()) {
            mRGBFragment = new RGBFragment();
            mFragments.add(mRGBFragment);

            mColorLightFragment = new ColorLightFragment();
            mFragments.add(mColorLightFragment);
        }

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mFrameVp.setAdapter(mAdapter);
        mFrameVp.setCurrentItem(0);
        mFrameVp.setOffscreenPageLimit(mFragments.size());

    }


    private long mLastFinishTs;
    private int mFinishTimes = 0;
    private static final int FINISH_DELAY = 1000 * 2;
    private static final int MAX_FINISH_PRESS_COUNT = 2;


    @Override
    public void onBackPressed() {

        long diff = System.currentTimeMillis() - mLastFinishTs;
        if (diff > 0 && diff <= FINISH_DELAY && ++mFinishTimes >= MAX_FINISH_PRESS_COUNT) {
            // finish
            mFinishTimes = 0;

            super.onBackPressed();
            return;

        } else if (Math.abs(diff) > FINISH_DELAY) {
            mFinishTimes = 1;
        }

        if ((MAX_FINISH_PRESS_COUNT - mFinishTimes) == 1) {
            showBackToast(R.string.goBack_again);
        }

        mLastFinishTs = System.currentTimeMillis();
    }

    private Toast mBackToast;

    private void showBackToast(int res) {
        if (mBackToast == null) {
            mBackToast = Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT);
        } else {
            mBackToast.setText(res);
        }
        mBackToast.show();
    }

    @Override
    protected void onDestroy() {
        Tlog.v(TAG, "ProductDetectionActivity onDestroy() ");
        Debuger.getInstance().skipProduceDetection(getApplicationContext(), false, curDevice);
        showBackToast(R.string.sign_out_product_detection);
        productDetectionManager.onSDestroy();
        super.onDestroy();
    }

    @Override
    public void refreshUI() {
        mDetectionFragment.refreshUI();
    }

    @Override
    public void clearReceiveResponse() {
        mDetectionReportFragment.clearAll();
    }

    @Override
    public void toast(String msg) {

        mDetectionReportFragment.setToast(msg);
    }

    @Override
    public void refreshReceiveData(String msg) {
        mDetectionReportFragment.setReceiveData(msg);
    }

    @Override
    public void refreshResponseData(String msg) {
        mDetectionReportFragment.setResponseData(msg);
    }

    @Override
    public void setVerificationPath(String path) {
        mDetectionReportFragment.setVerificationPath(path);
    }

    @Override
    public void flashModel(boolean on) {
        if (mRGBFragment != null) {
            mRGBFragment.flashModel(on);
        }
    }

    @Override
    public Context getApp() {
        return getApplicationContext();
    }

    @Override
    public void receiveProtocolAnalysisResult(byte[] protocolParams) {
        mTmpFunctionFragment.receiveProtocolAnalysisResult(protocolParams);
    }

    @Override
    public void nightLightSetResult(NightLightTiming obj) {
        if (mRGBFragment != null) {
            mRGBFragment.nightLightSetResult(obj);
        }
    }

    @Override
    public void nightLightQueryResult(NightLightTiming obj) {
        if (mRGBFragment != null) {
            mRGBFragment.nightLightQueryResult(obj);
        }
    }

    @Override
    public void rgbQueryResult(ColorLampRGB obj) {
        if (mRGBFragment != null) {
            mRGBFragment.rgbQueryResult(obj);
        }
        if (mColorLightFragment != null) {
            mColorLightFragment.rgbQueryResult(obj);
        }
    }

    @Override
    public void rgbSetResult(ColorLampRGB obj) {
        if (mRGBFragment != null) {
            mRGBFragment.rgbQueryResult(obj);
        }
        if (mColorLightFragment != null) {
            mColorLightFragment.rgbSetResult(obj);
        }
    }


    public void onFragmentInitFinish(DetectionRecyclerAdapter mRecyclerAdapter) {
        if (productDetectionManager != null) {
            mRecyclerAdapter.setOnClick(productDetectionManager);
            List<DetectInfo> detects = productDetectionManager.initData();
            mRecyclerAdapter.addAllData(detects);
        }
    }

    public void generalVerificationReport() {
        if (productDetectionManager != null) {
            productDetectionManager.generalVerificationReport();
        }
    }
}
