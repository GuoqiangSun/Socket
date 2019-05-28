package cn.com.startai.socket.sign.hardware;

import android.content.Intent;

import cn.com.startai.socket.mutual.js.bean.DisplayBleDevice;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/28 0028
 * desc :
 */
public interface IControlBle {


    /**
     * HW 是否激活
     *
     * @return
     */
    boolean isBleEnabled();

    /**
     * 激活HW
     */
    boolean enableBle();


    /**
     * 扫描HW
     */
    void scanningBle();

    /**
     * 停止扫描HW
     */
    void stopScanningBle();

    /**
     * @param mac
     */
    void connectBle(String mac);

    /**
     * @param mac
     */
    void disconnectBle(String mac);


    void regIBleResultCallBack(IBleResultCallBack mHWCallBack);

    void reconDevice(String mac);

    void requestIsFirstBinding();

    void enableLocation();

    void queryLocationEnabled();

    void onActivityResult(int requestCode, int resultCode, Intent data);


    interface IBleResultCallBack {

        /**
         * HW 激活
         */
        void onResultHWEnable();

        /**
         * HW没有激活
         */
        void onResultHWNotEnable();

        /**
         * 没有扫描到HW
         */
        void onResultScanHWIsNull();

        /**
         * 扫描到HW设备
         */
        void onResultHWDisplay(DisplayBleDevice mDevice);

        /**
         * HW连接状态
         *
         * @param mac
         * @param state  true 连接 false 断开连接
         * @param result true state success false state fail
         */
        void onResultHWConnection(String mac, boolean state, boolean result);

        /**
         * HW关闭
         */
        void onResultHWStateOff();

        /**
         * HW开启
         */
        void onResultHWStateOn();

        void onResultIsFirstBinding(boolean b, DisplayBleDevice displayDevice);

        void onResultStartActivityForResult(Intent intent, int requestPhotoCode);

        void onResultLocationEnabled(boolean b);
    }


}
