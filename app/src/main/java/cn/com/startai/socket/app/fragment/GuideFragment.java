package cn.com.startai.socket.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.startai.socket.R;
import cn.com.startai.socket.global.CustomManager;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class GuideFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " GuideFragment onCreate() ");
    }

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " GuideFragment inflateView() ");

        if (CustomManager.getInstance().isGrowroomate()) {

            return View.inflate(getActivity(), R.layout.framgment_guide_smart_socket,
                    null);

        } else if(CustomManager.getInstance().isMUSIK()){
            return View.inflate(getActivity(), R.layout.framgment_guide_super_socket,
                    null);
        } else {
            return View.inflate(getActivity(), R.layout.framgment_guide,
                    null);

        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " GuideFragment onCreateView() ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tlog.v(TAG, " GuideFragment onDestroyView() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " GuideFragment onDestroy() ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.v(TAG, " GuideFragment onActivityResult ");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
