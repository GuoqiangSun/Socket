package cn.com.startai.socket.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.startai.socket.R;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class TmpFunctionFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " TmpFunctionFragment onCreate() ");
    }

    private TextView mStateTxt;

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " TmpFunctionFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.framgment_function,
                null);

        EditText rEdt = view.findViewById(R.id.r_edt);
        EditText gEdt = view.findViewById(R.id.g_edt);
        EditText bEdt = view.findViewById(R.id.b_edt);
        Button mSubmitBtn = view.findViewById(R.id.submit_btn);

        mStateTxt = view.findViewById(R.id.flash_state);

        mSubmitBtn.setOnClickListener(v -> {

            Context context = getContext();
            if (context == null) {
                return;
            }

            String s;

            s = rEdt.getText().toString();
            int r;
            if ((r = strToInt(s)) == -1) return;

            s = gEdt.getText().toString();
            int g;
            if ((g = strToInt(s)) == -1) return;

            s = bEdt.getText().toString();
            int b;
            if ((b = strToInt(s)) == -1) return;

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            SocketScmManager scmManager =
                    Controller.getInstance().getScmManager();
            if (scmManager != null) {
                scmManager.setLightRGB(Debuger.getInstance().getProductDevice(), 0, r, g, b);
            } else {
                Toast.makeText(context, "scm is null", Toast.LENGTH_SHORT).show();
            }

        });

        Button mQueryBtn = view.findViewById(R.id.query_state_btn);
        mQueryBtn.setOnClickListener(v -> {
            SocketScmManager scmManager =
                    Controller.getInstance().getScmManager();
            if (scmManager != null) {
                scmManager.queryFlashState(Debuger.getInstance().getProductDevice());
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });

        Button mOpenBtn = view.findViewById(R.id.open_btn);
        mOpenBtn.setOnClickListener(v -> {
            SocketScmManager scmManager =
                    Controller.getInstance().getScmManager();
            if (scmManager != null) {
                scmManager.switchFlash(Debuger.getInstance().getProductDevice(), true);
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });

        Button mCloseBtn = view.findViewById(R.id.close_btn);
        mCloseBtn.setOnClickListener(v -> {
            SocketScmManager scmManager =
                    Controller.getInstance().getScmManager();
            if (scmManager != null) {
                scmManager.switchFlash(Debuger.getInstance().getProductDevice(), false);
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private int strToInt(String s) {

        if ("".equals(s)) {
            Toast.makeText(getContext(), "please input", Toast.LENGTH_SHORT).show();
            return -1;
        }

        try {

            int i = Integer.parseInt(s);

            if (i > 255) {
                Toast.makeText(getContext(), "input must less than 255", Toast.LENGTH_SHORT).show();
                return -1;
            }

            if (i < 0) {
                Toast.makeText(getContext(), "input must more than 0", Toast.LENGTH_SHORT).show();
                return -1;
            }

            return i;
        } catch (Exception e) {
            Toast.makeText(getContext(), "please input number", Toast.LENGTH_SHORT).show();
            return -1;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " TmpFunctionFragment onCreateView() ");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }


    @Override
    public void onDestroyView() {
        Tlog.v(TAG, " TmpFunctionFragment onDestroyView() ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " TmpFunctionFragment onDestroy() ");
    }


    public void flashModel(boolean on) {
        if (mStateTxt != null) {
            mStateTxt.setText(on ? "no" : "off");
        }
    }
}
