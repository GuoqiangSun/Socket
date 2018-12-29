package cn.com.startai.socket.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.activity.ProductDetectionActivity;
import cn.com.startai.socket.app.adapter.DetectionRecyclerAdapter;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class DetectionFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " DetectionFragment onCreate() ");
    }

    private DetectionRecyclerAdapter mRecyclerAdapter;


    @Override
    protected View inflateView() {
        Tlog.v(TAG, " DetectionFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.framgment_detection,
                null);
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerAdapter = new DetectionRecyclerAdapter(getActivity().getApplicationContext(), mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // 给每个item添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

//        GridLayoutManager glm = new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
//        mRecyclerView.setLayoutManager(glm);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);


        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " DetectionFragment onCreateView() ");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((ProductDetectionActivity) getActivity()).onFragmentInitFinish(mRecyclerAdapter);
        return view;
    }


    @Override
    public void onDestroyView() {
        Tlog.v(TAG, " DetectionFragment onDestroyView() ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " DetectionFragment onDestroy() ");
    }

    public void refreshUI() {
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.refreshUI();
        }
    }


}
