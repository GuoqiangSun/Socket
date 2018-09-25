package cn.com.startai.socket.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.activity.ProductDetectionActivity;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class DetectionReportFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " DetectionReportFragment onCreate() ");
    }

    private TextView mResponseTxt;
    private TextView mReceiveTxt;
    private TextView mVerificationPath;

    private ScrollView mRecScroll;
    private ScrollView mSendScroll;

    private TextView mToastTxt;

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " DetectionReportFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.framgment_detection_report,
                null);

        mResponseTxt = view.findViewById(R.id.response_txt);
        mReceiveTxt = view.findViewById(R.id.receive_txt);

        mSendScroll = view.findViewById(R.id.send_scroll);
        mRecScroll = view.findViewById(R.id.receive_scroll);

        mToastTxt = view.findViewById(R.id.toast_txt);

        TextView mEmptySendView = view.findViewById(R.id.empty_send_view);
        mEmptySendView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mSendScroll.fullScroll(ScrollView.FOCUS_DOWN);
                return false;
            }
        });

        TextView mEmptyRecView = view.findViewById(R.id.empty_rec_view);
        mEmptyRecView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mRecScroll.fullScroll(ScrollView.FOCUS_DOWN);
                return false;
            }
        });

        mVerificationPath = view.findViewById(R.id.verification_path);

        mReceiveTxt.setText(" ");
        mResponseTxt.setText(" ");

        Button mBtnClearSendData = view.findViewById(R.id.clear_send_data_btn);
        Button mBtnClearRecData = view.findViewById(R.id.clear_rec_data_btn);


        mBtnClearSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResponseTxt != null) {
                    mResponseTxt.setText(" ");
                }
            }
        });

        mBtnClearRecData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mReceiveTxt != null) {
                    mReceiveTxt.setText(" ");
                }

                if (mToastTxt != null) {
                    mToastTxt.setText("");
                }

            }
        });


        Button mBtnVerificationBtn = view.findViewById(R.id.genera_verification_report_btn);

        mBtnVerificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ProductDetectionActivity) getActivity()).generalVerificationReport();

            }
        });

        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " DetectionReportFragment onCreateView() ");


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void clearAll() {
        mReceiveDataTimes = mSendDataTimes = 0;
        if (mResponseTxt != null) {
            mResponseTxt.setText(" ");
        }
        if (mReceiveTxt != null) {
            mReceiveTxt.setText(" ");
        }
        if (mToastTxt != null) {
            mToastTxt.setText("");
        }
    }

    private static final int MAX_APPEND_TIMES = 500;

    private int mReceiveDataTimes = 0;

    public void setReceiveData(String msg) {

        if (++mReceiveDataTimes >= MAX_APPEND_TIMES) {
            mReceiveDataTimes = 0;
            if (mReceiveTxt != null) {
                mReceiveTxt.setText("");
            }
        }

        if (mReceiveTxt != null) {
            mReceiveTxt.append(msg);
        }


    }

    private int mSendDataTimes = 0;

    public void setResponseData(String msg) {

        if (++mSendDataTimes >= MAX_APPEND_TIMES) {
            mSendDataTimes = 0;
            if (mResponseTxt != null) {
                mResponseTxt.append(msg);
            }
        }

        if (mResponseTxt != null) {
            mResponseTxt.append(msg);
        }
    }

    public void setVerificationPath(String path) {
        if (mVerificationPath != null) {
            if (path == null) {
                mVerificationPath.setText("");
            } else {
                mVerificationPath.setText(path);
            }
        }
    }

    private int append = 0;
    private final int MAX_SHOW = 3;

    public void setToast(String msg) {

        if (mToastTxt != null) {
            if (msg == null) {
                mToastTxt.setText("");
            } else {
                if (++append >= MAX_SHOW) {
                    mToastTxt.setText("");
                    mToastTxt.append(msg);
                    mToastTxt.append(",");
                    append = 0;
                } else {
                    mToastTxt.append(msg);
                    mToastTxt.append(",");
                }

            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tlog.v(TAG, " DetectionReportFragment onDestroyView() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " DetectionReportFragment onDestroy() ");
    }


}
