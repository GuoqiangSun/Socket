package cn.com.startai.socket.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cn.com.startai.socket.R;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.sign.hardware.WiFi.impl.NetworkManager;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain.baselib.util.IpUtil;
import cn.com.swain.support.protocolEngine.pack.ComModel;
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


    @Override
    protected View inflateView() {
        Tlog.v(TAG, " TmpFunctionFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.framgment_function,
                null);

        Handler mWorkHandler = new Handler(LooperManager.getInstance().getWorkLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Toast.makeText(getContext(),
                        " workThread Running",
                        Toast.LENGTH_SHORT).show();

            }
        };

        Button mCheckWorkThreadBtn = view.findViewById(R.id.check_work_thread_btn);
        mCheckWorkThreadBtn.setOnClickListener(v -> mWorkHandler.sendEmptyMessage(0));

        Handler mProtocolHandler = new Handler(LooperManager.getInstance().getProtocolLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Toast.makeText(getContext(),
                        " protocolThread Running",
                        Toast.LENGTH_SHORT).show();
            }
        };


        Button mCheckProtocolThreadBtn = view.findViewById(R.id.check_protocol_thread_btn);
        mCheckProtocolThreadBtn.setOnClickListener(v -> mProtocolHandler.sendEmptyMessage(0));

        Handler mRepeatHandler = new Handler(LooperManager.getInstance().getRepeatLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Toast.makeText(getContext(),
                        " repeatThread Running",
                        Toast.LENGTH_SHORT).show();
            }
        };

        Button mCheckRepeatThreadBtn = view.findViewById(R.id.check_repeat_thread_btn);
        mCheckRepeatThreadBtn.setOnClickListener(v -> mRepeatHandler.sendEmptyMessage(0));

        EditText mInputEdt = view.findViewById(R.id.protocol_engine_input_edt);

        Button mCheckProtocolEngineBtn = view.findViewById(R.id.check_protocolEngine_btn);
        mCheckProtocolEngineBtn.setOnClickListener(v -> {
            SocketScmManager scmManager = Controller.getInstance().getScmManager();
            if (scmManager != null) {
                String s = mInputEdt.getText().toString();
                scmManager.testProtocolAnalysis(Debuger.getInstance().getProductDevice(), s, ComModel.MODEL_LAN);
            } else {
                Toast.makeText(getContext(), " scmManager=null ", Toast.LENGTH_SHORT).show();
            }
        });

        EditText mInputWEdt = view.findViewById(R.id.protocol_engineW_input_edt);
        Button mCheckProtocolEngineWBtn = view.findViewById(R.id.check_protocolEngineW_btn);
        mCheckProtocolEngineWBtn.setOnClickListener(v -> {
            SocketScmManager scmManager = Controller.getInstance().getScmManager();
            if (scmManager != null) {
                String s = mInputWEdt.getText().toString();
                scmManager.testProtocolAnalysis(Debuger.getInstance().getProductDevice(), s, ComModel.MODEL_WAN);
            } else {
                Toast.makeText(getContext(), " scmManager=null ", Toast.LENGTH_SHORT).show();
            }
        });


        EditText mInputCEdt = view.findViewById(R.id.protocol_engineC_input_edt);
        Button mCheckProtocolEngineCBtn = view.findViewById(R.id.check_protocolEngineC_btn);
        mCheckProtocolEngineCBtn.setOnClickListener(v -> {
            SocketScmManager scmManager = Controller.getInstance().getScmManager();
            if (scmManager != null) {
                String s = mInputCEdt.getText().toString();
                scmManager.testProtocolAnalysis(Debuger.getInstance().getProductDevice(), s, ComModel.MODEL_CASUAL);
            } else {
                Toast.makeText(getContext(), " scmManager=null ", Toast.LENGTH_SHORT).show();
            }
        });

        TextView mIpTxt = view.findViewById(R.id.ip_txt);
        TextView mPortTxt = view.findViewById(R.id.port_txt);
        String ip = null;
        int port = -1;

        NetworkManager networkManager = Controller.getInstance().getNetworkManager();
        if (networkManager != null) {
            ip =
                    IpUtil.getLocalIpV4Address() + "--" +
                            networkManager.getUdpLanComIp() + "--" +
                            IpUtil.getBroadcastAddress(Objects.requireNonNull(getContext()));
            port = networkManager.getUdpLanComPort();
        }

        mIpTxt.setText(String.valueOf(ip));
        mPortTxt.setText(String.valueOf(port));

        return view;
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


    public void receiveProtocolAnalysisResult(byte[] protocolParams) {
        String s = null;
        if (protocolParams != null) {
            s = new String(protocolParams);
        }
        Toast.makeText(getContext(), "Test success " + s, Toast.LENGTH_SHORT).show();
    }

}
