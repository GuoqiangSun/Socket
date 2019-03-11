package cn.com.startai.socket.app.fragment;

import android.content.Context;
import android.content.Intent;
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
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class RGBFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " RGBFragment onCreate() ");
    }

    private TextView mFlashStateTxt;

    EditText mStartHourEdt;
    EditText mStartMinuteEdt;
    EditText mStopHourEdt;
    EditText mStopMinuteEdt;

    EditText rEdt;
    EditText gEdt;
    EditText bEdt;

    EditText mYREdt;
    EditText mYGEdt;
    EditText mYBEdt;

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " RGBFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.framgment_rgb,
                null);

        SocketScmManager scmManager =
                Controller.getInstance().getScmManager();


        rEdt = view.findViewById(R.id.r_edt);
        gEdt = view.findViewById(R.id.g_edt);
        bEdt = view.findViewById(R.id.b_edt);
        Button mSubmitBtn = view.findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(v -> {


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

            Context context = getContext();
            if (context == null) {
                return;
            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            if (scmManager != null) {
                scmManager.setColorLamp(Debuger.getInstance().getProductDevice(), 0, r, g, b);
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }

        });

        Button mRGBBtn = view.findViewById(R.id.query_rgb_btn);
        mRGBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (scmManager != null) {
                    scmManager.queryColourLampRGB(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mYREdt = view.findViewById(R.id.y_r_edt);
        mYGEdt = view.findViewById(R.id.y_g_edt);
        mYBEdt = view.findViewById(R.id.y_b_edt);
        Button mQueryYRGBBtn = view.findViewById(R.id.query_yrgb_btn);
        mQueryYRGBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    scmManager.queryYellowLightRGB(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button mSubmitYRGBBtn = view.findViewById(R.id.y_submit_btn);
        mSubmitYRGBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    ColorLampRGB rgb = new ColorLampRGB();
                    rgb.mac = Debuger.getInstance().getProductDevice();
                    rgb.r = 255;
                    rgb.g = 255;
                    rgb.b = 0;
                    rgb.model = SocketSecureKey.Model.MODEL_YELLOW_LIGHT;
                    scmManager.setLightRGB(rgb);
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFlashStateTxt = view.findViewById(R.id.flash_state);

        Button mQueryBtn = view.findViewById(R.id.query_flash_state_btn);
        mQueryBtn.setOnClickListener(v -> {
            if (scmManager != null) {
                scmManager.queryFlashState(Debuger.getInstance().getProductDevice());
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });


        Button mOpenBtn = view.findViewById(R.id.open_flash_btn);
        mOpenBtn.setOnClickListener(v -> {
            if (scmManager != null) {
                scmManager.switchFlash(Debuger.getInstance().getProductDevice(), true);
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });

        Button mCloseBtn = view.findViewById(R.id.close_flash_btn);
        mCloseBtn.setOnClickListener(v -> {
            if (scmManager != null) {
                scmManager.switchFlash(Debuger.getInstance().getProductDevice(), false);
            } else {
                Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
            }
        });


        mStartHourEdt = view.findViewById(R.id.s_h_edt);
        mStartMinuteEdt = view.findViewById(R.id.s_m_edt);
        mStopHourEdt = view.findViewById(R.id.t_h_edt);
        mStopMinuteEdt = view.findViewById(R.id.t_m_edt);

        Button queryTimingBtn = view.findViewById(R.id.query_timing_btn);
        queryTimingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    scmManager.queryTimingNightLight(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button openTimingBtn = view.findViewById(R.id.open_timing_btn);
        openTimingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NightLightTiming NightLightTiming = new NightLightTiming();
                NightLightTiming.mac = Debuger.getInstance().getProductDevice();
                NightLightTiming.startup = true;

                String sh = mStartHourEdt.getText().toString();
                int i = parseHour(sh);
                if (i == -1) {
                    return;
                }
                NightLightTiming.startHour = i;


                String sm = mStartMinuteEdt.getText().toString();
                i = parseMinute(sm);
                if (i == -1) {
                    return;
                }
                NightLightTiming.startMinute = i;

                String th = mStopHourEdt.getText().toString();
                i = parseHour(th);
                if (i == -1) {
                    return;
                }
                NightLightTiming.stopHour = i;

                String tm = mStopMinuteEdt.getText().toString();
                i = parseMinute(tm);
                if (i == -1) {
                    return;
                }
                NightLightTiming.stopMinute = i;

                Context context = getContext();
                if (context == null) {
                    return;
                }

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (scmManager != null) {
                    scmManager.setNightLightTiming(NightLightTiming);
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button closeTimingBtn = view.findViewById(R.id.close_timing_btn);
        closeTimingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    NightLightTiming NightLightTiming = new NightLightTiming();
                    NightLightTiming.mac = Debuger.getInstance().getProductDevice();
                    NightLightTiming.id = SocketSecureKey.Model.MODEL_NIGHT_LIGHT_TIMING;
                    NightLightTiming.startup = false;
                    scmManager.setNightLightTiming(NightLightTiming);
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });


        timingTxt = view.findViewById(R.id.timing_state);


        Button queryWisdomBtn = view.findViewById(R.id.query_wisdom_btn);
        queryWisdomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    scmManager.queryWisdomNightLight(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button openWisdomBtn = view.findViewById(R.id.open_wisdom_btn);
        openWisdomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    scmManager.openWisdomNightLight(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button closeWisdomBtn = view.findViewById(R.id.close_wisdom_btn);
        closeWisdomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scmManager != null) {
                    scmManager.closeWisdomNightLight(Debuger.getInstance().getProductDevice());
                } else {
                    Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        wisdomTxt = view.findViewById(R.id.wisdom_state);


        return view;

    }

    public void flashModel(boolean on) {
        if (mFlashStateTxt != null) {
            mFlashStateTxt.setText(on ? "on" : "off");
        }
    }

    public void rgbQueryResult(ColorLampRGB obj) {

        if (obj != null) {

            if (obj.model == SocketSecureKey.Model.MODEL_COLOR_LAMP) {
                if (rEdt != null) {
                    rEdt.setText(String.valueOf(obj.r));
                }
                if (gEdt != null) {
                    gEdt.setText(String.valueOf(obj.g));
                }
                if (bEdt != null) {
                    bEdt.setText(String.valueOf(obj.b));
                }
            } else if (obj.model == SocketSecureKey.Model.MODEL_YELLOW_LIGHT) {
                if (mYREdt != null) {
                    mYREdt.setText(String.valueOf(obj.r));
                }
                if (mYGEdt != null) {
                    mYGEdt.setText(String.valueOf(obj.g));
                }
                if (mYBEdt != null) {
                    mYBEdt.setText(String.valueOf(obj.b));
                }
            }
        }

    }

    TextView timingTxt;
    TextView wisdomTxt;

    public void nightLightSetResult(NightLightTiming obj) {

        if (obj != null) {
            if (SocketSecureKey.Model.MODEL_NIGHT_LIGHT_WISDOM == obj.id) {
                if (wisdomTxt != null) {
                    wisdomTxt.setText(obj.startup ? "on" : "off");
                }
            } else if (SocketSecureKey.Model.MODEL_NIGHT_LIGHT_TIMING == obj.id) {
                if (timingTxt != null) {
                    timingTxt.setText(obj.startup ? "on" : "off");
                }

            } else if (0x00 == obj.id) {
                Toast.makeText(getContext(), " no nightLight task", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), " nightLightSetResult obi =null", Toast.LENGTH_SHORT).show();
        }

    }

    public void nightLightQueryResult(NightLightTiming obj) {

        if (obj != null) {
            if (SocketSecureKey.Model.MODEL_NIGHT_LIGHT_WISDOM == obj.id) {
                if (wisdomTxt != null) {
                    wisdomTxt.setText(obj.startup ? "on" : "off");
                }
            } else if (SocketSecureKey.Model.MODEL_NIGHT_LIGHT_TIMING == obj.id) {
                if (timingTxt != null) {
                    timingTxt.setText(obj.startup ? "on" : "off");
                }

                if (mStartHourEdt != null) {
                    mStartHourEdt.setText(String.valueOf(obj.startHour));
                }

                if (mStartMinuteEdt != null) {
                    mStartMinuteEdt.setText(String.valueOf(obj.startMinute));
                }

                if (mStopHourEdt != null) {
                    mStopHourEdt.setText(String.valueOf(obj.stopHour));
                }

                if (mStopMinuteEdt != null) {
                    mStopMinuteEdt.setText(String.valueOf(obj.stopMinute));
                }

            } else if (0x00 == obj.id) {
                String off = "off";
                if (wisdomTxt != null) {
                    wisdomTxt.setText(off);
                }
                if (timingTxt != null) {
                    timingTxt.setText(off);
                }
                Toast.makeText(getContext(), " no nightLight task", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), " nightLightSetResult obi =null", Toast.LENGTH_SHORT).show();
        }

    }

    private int parseHour(String sh) {

        int startHour;
        try {

            startHour = Integer.parseInt(sh);
        } catch (Exception e) {
            Toast.makeText(getContext(), " input error", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (startHour < 0) {
            Toast.makeText(getContext(), " hour <0", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (startHour > 24) {
            Toast.makeText(getContext(), " hour >24", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return startHour;
    }

    private int parseMinute(String sm) {

        int startHour;
        try {

            startHour = Integer.parseInt(sm);
        } catch (Exception e) {
            Toast.makeText(getContext(), " input error", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (startHour < 0) {
            Toast.makeText(getContext(), " minute <0", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (startHour > 60) {
            Toast.makeText(getContext(), " minute >60", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return startHour;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " RGBFragment onCreateView() ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tlog.v(TAG, " RGBFragment onDestroyView() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " RGBFragment onDestroy() ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.v(TAG, " RGBFragment onActivityResult ");
        super.onActivityResult(requestCode, resultCode, data);
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

}
