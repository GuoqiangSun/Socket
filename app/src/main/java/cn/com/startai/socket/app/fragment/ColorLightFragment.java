package cn.com.startai.socket.app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.view.BlurMaskCircularView;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.view.colorpicker.ColorPicker;


/**
 * 七彩灯
 */
public class ColorLightFragment extends BaseFragment {

    private BlurMaskCircularView mMaskFilterView;//中间的发光圆
    private ColorPicker mColorPicker;
    private AppCompatSeekBar mSeekBarS, mSeekBarV, mSeekBarH;
    private TextView tvSeekBarS, tvSeekBarV, tvSeekBarH;

    private Handler mWorkHandler;

    private static final int MSG_COLOR_LAMP = 0x01;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " RGBFragment onCreate() ");
    }

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " RGBFragment inflateView() ");
        View view = View.inflate(getActivity(), R.layout.fragment_color_light,
                null);
        initUI(view);

        SocketScmManager scmManager =
                Controller.getInstance().getScmManager();


        if (scmManager != null) {
            scmManager.queryColourLampRGB(Debuger.getInstance().getProductDevice());
        } else {
            Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
        }

        mWorkHandler = new Handler(LooperManager.getInstance().getWorkLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_COLOR_LAMP) {

                    int color = msg.arg1;

                    int r = (color & 0xff0000) >> 16;
                    int g = (color & 0x00ff00) >> 8;
                    int b = (color & 0x0000ff);

                    if (scmManager != null) {
                        scmManager.setColorLamp(Debuger.getInstance().getProductDevice(), 0, r, g, b);
                    } else {
                        Toast.makeText(getContext(), "scm is null", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        };

        return view;
    }

    private void changeHsvUI(int mColor) {

        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);

        mHue = hsv[0];
        int hProgress = (int) (mHue / 3.6f);
        mSeekBarH.setProgress(hProgress);
        tvSeekBarH.setText(hProgress + "%");

        mSat = hsv[1];
        int sProgress = (int) (mSeekBarS.getMax() * mSat);
        mSeekBarS.setProgress(sProgress);
        tvSeekBarS.setText(sProgress + "%");

        mVal = hsv[2];
        int vProgress = (int) (mSeekBarV.getMax() * mVal);
        mSeekBarV.setProgress(vProgress);
        tvSeekBarV.setText(vProgress + "%");

    }

    public void rgbQueryResult(ColorLampRGB obj) {


        if (obj != null) {

            if (obj.model == SocketSecureKey.Model.MODEL_COLOR_LAMP) {

                int color = Color.rgb(obj.r, obj.g, obj.b);

                Tlog.v(TAG, " rgbQueryResult color :" + color);

                if (mColor != color) {
                    if (mMaskFilterView != null) {
                        mMaskFilterView.setColor(color);
                    }
                    mColor = color;
                    changeHsvUI(color);
                }


            } else if (obj.model == SocketSecureKey.Model.MODEL_YELLOW_LIGHT) {

                if (obj.r != 0 && obj.g != 0 && obj.b != 0) {
                    int color = Color.rgb(obj.r, obj.g, obj.b);

                    if (mColor != color) {
                        if (mMaskFilterView != null) {
                            mMaskFilterView.setColor(color);
                        }
                        mColor = color;
                        changeHsvUI(color);
                    }
                }

            }
        }

    }

    public void rgbSetResult(ColorLampRGB obj) {
//        rgbQueryResult(obj);
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


    public void initUI(View view) {
        findView(view);
        initListener();

        mColor = mMaskFilterView.getColor();
        mColorPicker.setColor(mColor);

    }

    /**
     *
     */
    private void findView(View view) {
        mMaskFilterView = view.findViewById(R.id.bg_view);
        mColorPicker = view.findViewById(R.id.color_picker);
        mSeekBarS = view.findViewById(R.id.seek1);
        mSeekBarV = view.findViewById(R.id.seek2);
        mSeekBarH = view.findViewById(R.id.seek3);
        tvSeekBarS = view.findViewById(R.id.tv_seek1);
        tvSeekBarV = view.findViewById(R.id.tv_seek2);
        tvSeekBarH = view.findViewById(R.id.tv_seek3);
    }

    private int mColor;
    private float mHue;//色调范围0-360
    private float mSat;//饱和度范围0-1
    private float mVal;//亮度范围0-1

    private void sendToDevice(int color) {
        if (mWorkHandler != null) {

            if (mWorkHandler.hasMessages(MSG_COLOR_LAMP)) {
                mWorkHandler.removeMessages(MSG_COLOR_LAMP);
            }

            Message message = mWorkHandler.obtainMessage(MSG_COLOR_LAMP, color, color);
            mWorkHandler.sendMessageDelayed(message, 600);
        }
    }

    /**
     * 事件监听
     */
    @SuppressLint("SetTextI18n")
    private void initListener() {
        //取色盘提取颜色（松开手）
        mColorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                Tlog.v(TAG, " mColorPicker selected color :" + color);
                mColor = color;
                mMaskFilterView.setColor(color);
                sendToDevice(color);
                changeHsvUI(color);

//                float[] hsv = new float[3];
//                Color.colorToHSV(color, hsv);
//                mHue = hsv[0];
//                mSat = hsv[1];
//                mVal = hsv[2];
            }
        });
        //取色盘提取颜色（不松开手）
        mColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mMaskFilterView.setColor(color);
            }
        });

        //饱和度
        mSeekBarS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                float[] hsv = new float[3];
                Color.colorToHSV(mColor, hsv);

                mHue = hsv[0];
                int hProgress = (int) (mHue / 3.6f);
                mSeekBarH.setProgress(hProgress);
                tvSeekBarH.setText(hProgress + "%");

                mSat = (float) progress / seekBar.getMax();
                tvSeekBarS.setText(progress + "%");

                mVal = hsv[2];
                int vProgress = (int) (mSeekBarV.getMax() * mVal);
                mSeekBarV.setProgress(vProgress);
                tvSeekBarV.setText(vProgress + "%");

                int i = Color.HSVToColor(new float[]{mHue, mSat, mVal});
                mColorPicker.setColor(i);
                mMaskFilterView.setColor(i);
                mColor = i;
                sendToDevice(i);

                Tlog.v(TAG, " mSeekBarS progress change :" + progress + " color:" + mColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //亮度
        mSeekBarV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                float[] hsv = new float[3];
                Color.colorToHSV(mColor, hsv);

                mHue = hsv[0];
                int hProgress = (int) (mHue / 3.6f);
                mSeekBarH.setProgress(hProgress);
                tvSeekBarH.setText(hProgress + "%");

                mSat = hsv[1];
                int sProgress = (int) (mSeekBarS.getMax() * mSat);
                mSeekBarS.setProgress(sProgress);
                tvSeekBarS.setText(sProgress + "%");

                mVal = (float) progress / seekBar.getMax();
                if (mVal < 0.35) {
                    mVal = 0.35f;
                }
                tvSeekBarV.setText(progress + "%");

                int i = Color.HSVToColor(new float[]{mHue, mSat, mVal});
                mColorPicker.setColor(i);
                mMaskFilterView.setColor(i);
                mColor = i;
                sendToDevice(i);

                Tlog.v(TAG, " mSeekBarV progress change :" + progress + " color:" + mColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //色调
        mSeekBarH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                float[] hsv = new float[3];
                Color.colorToHSV(mColor, hsv);

                mHue = (3.6f * progress);
                tvSeekBarH.setText(progress + "%");


                mSat = hsv[1];
                int sProgress = (int) (mSeekBarS.getMax() * mSat);
                mSeekBarS.setProgress(sProgress);
                tvSeekBarS.setText(sProgress + "%");

                mVal = hsv[2];
                int vProgress = (int) (mSeekBarV.getMax() * mVal);
                mSeekBarV.setProgress(vProgress);
                tvSeekBarV.setText(vProgress + "%");

                int i = Color.HSVToColor(new float[]{mHue, mSat, mVal});
                mColorPicker.setColor(i);
                mMaskFilterView.setColor(i);
                mColor = i;
                sendToDevice(i);

                Tlog.v(TAG, " mSeekBarH progress change :" + progress + " color:" + mColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
