package cn.com.startai.socket.sign.hardware.ble.impl;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.util.List;

import cn.com.startai.socket.R;
import cn.com.startai.socket.db.gen.DisplayBleDeviceDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.bean.DisplayBleDevice;
import cn.com.startai.socket.mutual.js.impl.AndJsBridge;
import cn.com.startai.socket.sign.hardware.AbsBle;
import cn.com.startai.socket.sign.hardware.ble.array.BleArray;
import cn.com.startai.socket.sign.hardware.ble.array.BleArrayUtils;
import cn.com.startai.socket.sign.hardware.ble.util.DeviceEnablePost;
import cn.com.startai.socket.sign.hardware.ble.util.InsertDisplayDeviceDaoUtil;
import cn.com.startai.socket.sign.hardware.ble.xml.ConBleSp;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.permission.PermissionHelper;
import cn.com.swain.baselib.permission.PermissionRequest;
import cn.com.swain.baselib.util.StrUtil;
import cn.com.swain.support.ble.connect.AbsBleConnect;
import cn.com.swain.support.ble.connect.BleConnectEngine;
import cn.com.swain.support.ble.connect.BleConnectResult;
import cn.com.swain.support.ble.connect.IBleConCallBack;
import cn.com.swain.support.ble.connect.IBleConnectCheckResult;
import cn.com.swain.support.ble.enable.AbsBleEnable;
import cn.com.swain.support.ble.enable.BleEnabler;
import cn.com.swain.support.ble.enable.BleStateResult;
import cn.com.swain.support.ble.scan.AbsBleScan;
import cn.com.swain.support.ble.scan.BleScanAuto;
import cn.com.swain.support.ble.scan.BleScanResult;
import cn.com.swain.support.ble.scan.IBleScanObserver;
import cn.com.swain.support.ble.scan.ScanBle;
import cn.com.swain.support.ble.send.AbsBleSend;
import cn.com.swain.support.ble.send.BleDataSendProduce;
import cn.com.swain.support.ble.send.SendDataQueue;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolInput;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;


/**
 * author: Guoqiang_Sun
 * date : 2018/4/4 0004
 * desc :
 */

public class BleManager extends AbsBle implements IBleScanObserver, IBleConCallBack {

    public static final String TAG = "BleManager";

    private final Application app;

    public BleManager(Application app) {
        this.app = app;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Tlog.d(TAG, "STATE_OFF BLE turn off");

                        bleEnable = false;
                        abortAutoCon();
                        if (mBmCallJs != null) {
                            mBmCallJs.onResultHWStateOff();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Tlog.d(TAG, "STATE_TURNING_OFF STATE_ON BLE turning off");
                        if (mBleScan != null) {
                            mBleScan.bsBleStateChange(false);
                        }
                        bleEnable = false;
                        if (mScanning) {
                            Tlog.v(TAG, " ble is being scanned ,stop scan");
                            if (mBleScan != null) {
                                mBleScan.bsStopScan();
                            }
                        }

                        break;
                    case BluetoothAdapter.STATE_ON:
                        Tlog.d(TAG, "STATE_ON BLE turn startup");
                        if (mBleScan != null) {
                            mBleScan.bsBleStateChange(true);
                        }

                        bleEnable = true;
                        if (mScanning) {
                            Tlog.v(TAG, " last time ,ble is scanning ,start scan");
                            if (mBleScan != null) {
                                mBleScan.bsScanBleOnce();
                            }
                        }
                        if (mBmCallJs != null) {
                            mBmCallJs.onResultHWStateOn();
                        }

                        if (!mLastBleEnabled) {
                            // 收到蓝牙激活的广播，如果上次激活失败，主动回调js蓝牙激活了。
                            mLastBleEnabled = true;
                            if (mBmCallJs != null) {
                                mBmCallJs.onResultHWEnable();
                            }
                        }

//                        if (mLastConScanBle != null) {
//                            canAutoCon();
//                            mBleCon.connect(mLastConScanBle);
//                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Tlog.d(TAG, "STATE_TURNING_ON BLE turning startup");
                        bleEnable = true;
                        break;
                }
            }
        }
    };


    private IDataProtocolInput mProtocolInput;

    @Override
    public void regIProtocolInput(IDataProtocolInput mReceives) {
        this.mProtocolInput = mReceives;
    }

    private IBleResultCallBack mBmCallJs;

    @Override
    public void regIBleResultCallBack(IBleResultCallBack mHWCallBack) {
        this.mBmCallJs = mHWCallBack;
    }

    private AbsBleEnable mBleEnabler;
    private AbsBleScan mBleScan;
    private AbsBleConnect mBleCon;
    private AbsBleSend absBleSend;

    private boolean bleEnable;


    @Override
    public void onSCreate() {

        Tlog.v(TAG, " BleManager onSCreate()");

        Looper workLooper = LooperManager.getInstance().getWorkLooper();

        mBleEnabler = new BleEnabler(app, workLooper);

        mBleScan = new BleScanAuto(app, workLooper, this);

        mBleCon = new BleConnectEngine(app, workLooper, this);

        IntentFilter mBleFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        app.registerReceiver(mReceiver, mBleFilter);

        bleEnable = mBleEnabler.beIsBleEnable();

    }

    @Override
    public void onSResume() {
        Tlog.v(TAG, " BleManager onSResume()");
    }

    @Override
    public void onSPause() {
        Tlog.v(TAG, " BleManager onSPause()");
    }


    @Override
    public void onSDestroy() {
        Tlog.v(TAG, " BleManager onSDestroy()");
        mBleArray.clearAll();

        if (mBleScan != null) {
            mBleScan.bsStopScan();
            mBleScan = null;
        }

        if (mBleCon != null) {
            mBleCon.release();
            mBleCon = null;
        }

        if (absBleSend != null) {
            absBleSend.removeMsg();
            absBleSend.closeGatt();
            absBleSend = null;
        }

        try {
            app.unregisterReceiver(mReceiver);
        } catch (Exception e) {

        }

    }

    @Override
    public void onSFinish() {
        Tlog.v(TAG, " BleManager onSFinish()");

        if (mBleScan != null) {
            mBleScan.bsStopScan();
            mBleScan = null;
        }

        if (mBleCon != null) {
            mBleCon.release();
            mBleCon = null;
        }

        if (absBleSend != null) {
            absBleSend.removeMsg();
            absBleSend.closeGatt();
            absBleSend = null;
        }

    }

    /**
     * 上次建立过连接的设备
     */
    private ScanBle mLastConScanBle;

    private final BleArray mBleArray = new BleArray();

    /**
     * 已经连接上的ble
     */
    private ScanBle mCurConnectedBle = null;

    public String getConnectedAddress() {
        return (mCurConnectedBle != null) ? mCurConnectedBle.address : null;
    }

    private boolean releaseConnectedBle(ScanBle mItem) {
        if (BleArrayUtils.isSame(mCurConnectedBle, mItem)) {
            Tlog.v(TAG, " set mCurConnectedBle == null  ");
            mCurConnectedBle = null;
            absBleSend = null;
            return true;
        }
        return false;
    }

    /**
     * 正在连接的ble
     */
    private ScanBle mCurConnectingBle = null;

    private boolean releaseConnectingBle(ScanBle mItem) {
        if (BleArrayUtils.isSame(mCurConnectingBle, mItem)) {
            Tlog.v(TAG, " set mCurConnectingBle == null  ");
            mCurConnectingBle = null;
            mAutoReconBle = null;
            return true;
        }
        return false;
    }


    /***************/

    @Override
    public boolean isBleEnabled() {
        return mBleEnabler.beIsBleEnable();
    }

    private boolean mLastBleEnabled = true;

    @Override
    public boolean enableBle() {
        boolean b = mBleEnabler.beEnableBle();
        checkBleStatus();
        mLastBleEnabled = b;
        return b;
    }

    private void checkBleStatus() {
        BleStateResult mBleResult = new BleStateResult();
        mBleResult.setTotalTimes(3);
        mBleResult.setDelay(1000);
        mBleResult.mBleStateCallBack = mResult -> {

            Tlog.v(TAG, " Ble check Result : ble is enabled? " + mResult.isEnabled());
            if (mResult.isEnabled()) {
                mResult.disCheck();
                mLastBleEnabled = true;
                if (mBmCallJs != null) {
                    mBmCallJs.onResultHWEnable();
                }
            } else {
                if (mResult.checkFinish()) {
                    mLastBleEnabled = false;
                    if (mBmCallJs != null) {
                        mBmCallJs.onResultHWNotEnable();
                    }
                }
            }
        };
        mBleEnabler.checkBleState(mBleResult);
    }

    /***************/

    @Override
    public void onBsStartScan() {
        Tlog.v(TAG, " onBsStartScan");
    }

    @Override
    public void onBsStopScan() {
        Tlog.v(TAG, " onBsStopScan ");
    }

    @Override
    public void onResultBsGattScan(ScanBle mBle) {

        if (!mScanning) {
            Tlog.e(TAG, "onResultBsGattScan() " + mBle.toString() + "already stop scanning");
            return;
        }
        if (!mBle.isValid()) {
            if (Debuger.isLogDebug) {
                Tlog.w(TAG, " ScanBle is unValid " + mBle.address
                        + "--" + mBle.name + "--" + mBle.getFirstBroadUUID());
            }
            return;
        }

        if (
                !Debuger.isDebug &&
                        !Debuger.isBleDebug
                        && CustomManager.getInstance().isTriggerBle()
                        && !mBle.address.startsWith("90:00")
        ) {

            if (Debuger.isLogDebug) {
                Tlog.w(TAG, " ScanBle isTriggerBle " + mBle.address + " not startsWith 90:00");
            }
            return;

        }

//            if (!mBle.matchBroadUUID(mShowUuid)) {
//                if (Debuger.isLogDebug) {
//                    Tlog.w(TAG, " broadUuid not match " + mBle.address
//                            + "--" + mBle.name + "--" + mBle.getFirstBroadUUID());
//                }
//                return;
//            }


        if (Debuger.isLogDebug) {
            mBle.name += "n";
            Tlog.w(TAG, " onResultBsGattScan " + mBle.toString());
        }

        displayBle(mBle);


    }

    private void displayBle(ScanBle mBle) {
        if (mBmCallJs != null) {
            if (mBleArray.addDisplayBle(mBle)) {
                DisplayBleDevice mDisplayDevice = new DisplayBleDevice().memorize(mBle);
                mBmCallJs.onResultHWDisplay(mDisplayDevice);
            }
        }
    }

    private void displayConnectedBle() {
        final ScanBle mBle = mCurConnectedBle;
        if (mBle != null) {
            mBle.setConnected();
        }
        displayBle(mBle);
    }

    private void displayAutoConnectBle() {
        final ScanBle mBle = mAutoReconBle;
        if (!hasDisplay) {
            hasDisplay = true;
            if (mBle != null) {
                displayBle(mBle);
            }
        } else {
            Tlog.e(TAG, "displayAutoConnectBle has already Display");
        }
    }

    /*********************/

    private volatile boolean mScanning = false;

    private static final long SCAN_TIME_OUT = 1000 * 30;


    @Override
    public void scanningBle() {
        Tlog.v(TAG, " scanningBle() ");

//        com.blankj.utilcode.util.PermissionUtils permission =
//                com.blankj.utilcode.util.PermissionUtils.permission(com.blankj.utilcode.constant.PermissionConstants.LOCATION);
//        permission.callback(new PermissionUtils.SimpleCallback() {
//            @Override
//            public void onGranted() {
//                scanBle();
//            }
//
//            @Override
//            public void onDenied() {
//
//            }
//        });
//        permission.request();

        PermissionHelper.requestSinglePermission(app, new PermissionRequest.OnPermissionResult() {

            @Override
            public boolean onPermissionRequestResult(String permission, boolean granted) {

                Tlog.v(TAG, " scanningBle() PermissionHelper : " + permission + " granted:" + granted);

                if (granted) {
                    scanBle();
                } else {
                    alert();
                }
                return true;
            }
        }, Manifest.permission.ACCESS_COARSE_LOCATION);

    }


    private void alert() {
        AlertDialog.Builder b = new AlertDialog.Builder(app);
        b.setTitle(R.string.permission_request_title);
        b.setMessage(R.string.permission_request);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndJsBridge andJsBridge = Controller.getInstance().getAndJsBridge();
                if (andJsBridge != null) {
                    andJsBridge.onJSFinish();
                }
            }
        });
        b.setCancelable(false);
        b.create().show();
    }

    private void scanBle() {

        if (mScanning) {
            stopScanningBle();
        }

        mScanning = true;
        if (bleEnable) {
            // 扫描时，把已经连接的ble添加进来
            displayConnectedBle();
            if (mBleScan != null) {
                mBleScan.bsScanBleOnce();
            }

            BleScanResult mBleScanResult = new BleScanResult(mBleScanResult1 -> {
                Tlog.v(TAG, "checkBleScanResult OnBleScanResult " + mBleArray.getDisplaySize() + " mScanning:" + mScanning);
                if (mBleArray.getDisplaySize() <= 0 && mCurConnectedBle == null) {
                    // 没有扫描到；
                    if (mScanning) {
                        stopScanningBle();
                        if (mBmCallJs != null) {
                            mBmCallJs.onResultScanHWIsNull();
                        }
                    }
                }
            }, SCAN_TIME_OUT, null);

            if (mBleScan != null) {
                mBleScan.checkBleScanResult(true, mBleScanResult);
            }

        } else {
            Tlog.e(TAG, " ble is not Enable");
        }
    }

    @Override
    public void stopScanningBle() {
        Tlog.v(TAG, " stopScanningBle() ");
        mScanning = false;
        if (mBleScan != null) {
            mBleScan.removeCheckScanResult();
            if (bleEnable) {
                mBleScan.bsStopScan();
            } else {
                Tlog.e(TAG, " ble is not Enable");
            }
        }
        mBleArray.clearDisplayLst();
        System.gc();
    }

    private ScanBle mAutoReconBle = null;
    private boolean hasDisplay = false;

    @Override
    public void reconDevice(String mac) {

        if (mCurConnectedBle != null) {
            Tlog.e(TAG, " reconDevice() ; mac:" + mac + " CurConnectedBle:" + mCurConnectedBle.address);

            if (mBmCallJs != null) {
                if (mac.equalsIgnoreCase(mCurConnectedBle.address)) {
                    mBmCallJs.onResultHWConnection(mac, true, true);
                } else {
                    mBmCallJs.onResultHWConnection(mac, true, false);
                }
            }
            return;
        }

        if (mCurConnectingBle != null) {
            Tlog.e(TAG, " reconDevice() ; mac:" + mac + " CurConnectingBle:" + mCurConnectingBle.address);
            if (mBmCallJs != null) {
                if (!mac.equalsIgnoreCase(mCurConnectingBle.address)) {
                    mBmCallJs.onResultHWConnection(mac, true, false);
                }
            }
            return;
        }

        DisplayBleDeviceDao displayDeviceDao = DBManager.getInstance().getDaoSession().getDisplayBleDeviceDao();
        List<DisplayBleDevice> list = displayDeviceDao.queryBuilder().where(DisplayBleDeviceDao.Properties.Address.eq(mac)).list();
        DisplayBleDevice displayDevice = null;
        if (list.size() > 0) {
            displayDevice = list.get(0);
        }

        if (displayDevice != null && bleEnable) {
            ScanBle mItem = displayDevice.copyToScanBle();
            mItem.setDisconnected();
            Tlog.v(TAG, " reconDevice()  mac:" + mItem.toString());
            mCurConnectingBle = mAutoReconBle = mItem;
            canAutoCon();
            if (mBleCon != null) {
                mBleCon.connect(mItem);
            }
            checkConResult(mItem);
        } else {
            Tlog.e(TAG, " reconDevice fail... ");
            mAutoReconBle = null;
            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mac, true, false);
            }
        }
        hasDisplay = false;
        displayAutoConnectBle();

    }

    @Override
    public void requestIsFirstBinding() {

        String conMAC = ConBleSp.getConBleSp(app).getConMAC("");
        DisplayBleDevice displayDevice = null;

        if (conMAC != null && !conMAC.equalsIgnoreCase("")) {

            DisplayBleDeviceDao displayDeviceDao = DBManager.getInstance().getDaoSession().getDisplayBleDeviceDao();
            List<DisplayBleDevice> list = displayDeviceDao.queryBuilder().where(DisplayBleDeviceDao.Properties.Address.eq(conMAC)).list();

            if (list.size() > 0) {
                displayDevice = list.get(0);
            }

            if (displayDevice != null) {
                displayDevice.con = false;
            }
        }

        if (mBmCallJs != null) {
            mBmCallJs.onResultIsFirstBinding((displayDevice == null), displayDevice);
        }

    }

    private static final int REUQEST_LOCATION = 0x6598;

    @Override
    public void enableLocation() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        if (mBmCallJs != null) {
            mBmCallJs.onResultStartActivityForResult(i, REUQEST_LOCATION);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REUQEST_LOCATION) {

            LooperManager.getInstance().getWorkHandler().post(new Runnable() {
                @Override
                public void run() {
                    LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
                    boolean providerEnabledGps = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean providerEnabledNet = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (mBmCallJs != null) {
                        mBmCallJs.onResultLocationEnabled(providerEnabledGps || providerEnabledNet);
                    }

                }
            });

        }

    }

    @Override
    public void queryLocationEnabled() {
        LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabledGps = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean providerEnabledNet = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Tlog.d(TAG, " queryLocationEnabled providerEnabledGps :" + providerEnabledGps + " providerEnabledNet:" + providerEnabledNet);
        if (mBmCallJs != null) {
            mBmCallJs.onResultLocationEnabled(providerEnabledGps || providerEnabledNet);
        }
    }

    @Override
    public void connectBle(String mac) {

        Tlog.v(TAG, " connectBle()  mac:" + mac);

        if (mCurConnectingBle != null || mCurConnectedBle != null) {

            if (mCurConnectingBle != null) {
                if (mac.equalsIgnoreCase(mCurConnectingBle.address)) {
                    Tlog.e(TAG, " curConnectingBle mac is the same");
                    return;
                }

                Tlog.e(TAG, " ble is connecting : " + mCurConnectingBle.address + "-" + mCurConnectingBle.name);
            } else {

                if (mac.equalsIgnoreCase(mCurConnectedBle.address)) {
                    Tlog.e(TAG, " mCurConnectedBle mac is the same");
                    if (mBmCallJs != null) {
                        mBmCallJs.onResultHWConnection(mac, true, true);
                    }
                    return;
                }

                Tlog.e(TAG, " ble is connected : " + mCurConnectedBle.address + "-" + mCurConnectedBle.name);
            }

            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mac, true, false);
            }
            return;
        }

        // 重新连接
        ScanBle ble = mBleArray.getConnectedBle(mac);

        if (ble == null) {
            ble = mBleArray.getDisplayBle(mac);
        }

        if (ble == null) {

            Tlog.e(TAG, " BleManager connectBle ScanBle=null mac: " + mac);

            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mac, true, false);
            }

        } else {

            if (bleEnable) {
                mLastConScanBle = mCurConnectingBle = ble;
                canAutoCon();
                if (mBleCon != null) {
                    mBleCon.connect(ble);
                }
                checkConResult(ble);
            } else {
                Tlog.e(TAG, " ble is not Enable");
                if (mBmCallJs != null) {
                    mBmCallJs.onResultHWConnection(mac, true, false);
                }
            }

        }

    }

    private void checkConResult(ScanBle ble) {
        final BleConnectResult mResult = new BleConnectResult();
        mResult.mBle = ble;
        mResult.delay = 1000 * 10;
        mResult.mCallBack = mIBleConnectCheckResult;
        if (mBleCon != null) {
            mBleCon.checkConnectResult(true, mResult);
        }
    }

    private final IBleConnectCheckResult mIBleConnectCheckResult = new IBleConnectCheckResult() {
        @Override
        public void OnBleConnectResult(BleConnectResult mBleScanResult) {

            boolean bcon = mLastConScanBle != null || mAutoReconBle != null;

            if (mCurConnectedBle == null && bcon) {
                Tlog.e(TAG, " checkConResult disconnectBle ");
                if (mBleScanResult.mBle != null) {
                    disconnectBle(mBleScanResult.mBle.address);
                }
            } else {
                Tlog.e(TAG, " checkConResult connectedBle" + ((mCurConnectedBle != null) ? "!=null " : "=null "));
            }

        }
    };

    @Override
    public void disconnectBle(String mac) {

        Tlog.v(TAG, " disconnectBle()  mac:" + mac);

        ScanBle ble = mBleArray.getConnectedBle(mac);

        if (ble == null) {
            if (mCurConnectingBle != null && mCurConnectingBle.address.equalsIgnoreCase(mac)) {
                ble = mCurConnectingBle;
            } else {
                Tlog.e(TAG, " mCurConnectingBle : " + (mCurConnectingBle == null ? "null" : mCurConnectingBle.address));
            }
        }

        if (ble == null) {

            Tlog.e(TAG, " BleManager disconnectBle ScanBle==null mac: " + mac);
            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mac, false, true);
            }

        } else {
            if (bleEnable) {
                mLastConScanBle = null;
                abortAutoCon();
                if (mBleCon != null) {
                    mBleCon.disconnect(ble);
                }
            } else {
                Tlog.e(TAG, " ble is not Enable");
                if (mBmCallJs != null) {
                    mBmCallJs.onResultHWConnection(mac, false, true);
                }
            }
        }
    }

    /*********************/


    private static final int JUMP_AUTO_CON_TIMES = 3;
    private static final int NORMAL_AUTO_CON_TIMES = 2;
    private static final int MAX_AUTO_CON_TIMES = JUMP_AUTO_CON_TIMES * NORMAL_AUTO_CON_TIMES;

    private static final int FIRST_AUTO_CON = Math.max(JUMP_AUTO_CON_TIMES, NORMAL_AUTO_CON_TIMES);
    private static final int ABORT_AUTO_CON_TIMES = MAX_AUTO_CON_TIMES + 1; // 终止
    private static final int CNA_AUTO_CON_TIMES = 0;//可以

    private int autoConTimes = CNA_AUTO_CON_TIMES;

    private void abortAutoCon() {
        Tlog.v(TAG, " abortAutoCon() ");
        this.autoConTimes = ABORT_AUTO_CON_TIMES;
    }

    private void canAutoCon() {
        Tlog.v(TAG, " canAutoCon() ");
        this.autoConTimes = CNA_AUTO_CON_TIMES;
    }

    private boolean autoCon(boolean jump, ScanBle mItem) {
        Tlog.v(TAG, " autoConBle() reconTimes:" + autoConTimes);
        if (jump) {
            autoConTimes += JUMP_AUTO_CON_TIMES;
        } else {
            autoConTimes += NORMAL_AUTO_CON_TIMES;
        }
        final int times = autoConTimes;
        if (times <= MAX_AUTO_CON_TIMES) {
            // 需要自动重连

            if (!bleEnable) {
                Tlog.e(TAG, " autoCon() ble not Enable");
                return false;
            }

            mLastConScanBle = mCurConnectingBle = mItem;
            if (mBleCon != null) {
                mBleCon.connect(mItem);
            }

            if (times <= FIRST_AUTO_CON) {
                checkConResult(mItem);
            }

            return true;

        }
        return false;
    }

    @Override
    public void onResultConnect(boolean result, ScanBle mItem) {
        Tlog.e(TAG, " onResultConnect() result:" + result + " mac:" + mItem.address);

        if (!result && autoCon(false, mItem)) {
            return;
        }

        releaseConnectingBle(mItem);

        if (result) {
            // 连接成功，预防断开,所以可以重连接。
            if (mBleCon != null) {
                mBleCon.removeCheckConnectResult();
            }
            canAutoCon();
            mCurConnectedBle = mItem;
            mBleArray.addConnectedBle(mItem);
            // 连接成功后，等订阅成功后再回调JS.
            // ...
        } else {
            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mItem.address, true, false);
            }
        }

    }

    @Override
    public void onResultAlreadyConnected(ScanBle mItem) {
        Tlog.e(TAG, " onResultAlreadyConnected() " + " mac:" + mItem.address);
    }

    @Override
    public void onResultDisconnectPassively(boolean result, ScanBle mItem) {
        Tlog.e(TAG, " onResultDisconnectPassively() " + result + " mac:" + mItem.address);

        boolean b = releaseConnectedBle(mItem);

        if (autoCon(result, mItem)) {
            return;
        }

        abortAutoCon();

        boolean b1 = releaseConnectingBle(mItem);
        // 如果连接的或正在连接的和断开的是同一设备，认为真正断开


        if (b1 || b) { //防止重复提示

            // 有些设备断开后，又会连接
            if (mBleCon != null) {
                mBleCon.disconnect(mItem);
            }

            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mItem.address, false, true);
            }
        } else {
            Tlog.e(TAG, " onResultDisconnectPassively releaseConBle: maybe already release ");
        }

    }

    @Override
    public void onResultDisconnectActively(ScanBle mItem) {
        Tlog.e(TAG, " onResultDisconnectActively() mac:" + mItem.address);
        boolean b = releaseConnectedBle(mItem);
        boolean b1 = releaseConnectingBle(mItem);

        abortAutoCon();

        if (b1 || b) { //防止重复提示


            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(mItem.address, false, true);
            }

        } else {
            Tlog.e(TAG, " onResultDisconnectActively releaseConBle: maybe already release ");
        }

    }


    @Override
    public void onWriteDataFail(ScanBle mItem) {
        Tlog.e(TAG, " onWriteDataFail " + mItem.address);
    }

    @Override
    public void onResultServiceOrder(boolean result, ScanBle mItem, BluetoothGatt mConGatt) {
        Tlog.v(TAG, " onResultServiceOrder result:" + result);

        String address = null;
        if (mConGatt != null) {
            address = mConGatt.getDevice().getAddress();
        }

        boolean produceSuccess = false;

        if (result) {
            if (absBleSend != null) {
                Tlog.e(TAG, " onResultServiceOrder release last AbsBleSend:");
                absBleSend.removeMsg();
                absBleSend.closeGatt();
                absBleSend = null;
            }
            AbsBleSend mBleSend = BleDataSendProduce.produceFirstBleSend(mConGatt);
            if (mBleSend != null) {
                Tlog.v(TAG, " produceFirstBleSend Success :" + mBleSend.getUuidStr());
                absBleSend = new SendDataQueue(LooperManager.getInstance().getProtocolLooper(), mBleSend);
            } else {
                try {
                    Tlog.e(TAG, BleDataSendProduce.getServiceStr(mConGatt));
                } catch (Exception e) {
                    Tlog.e(TAG, " showService: ", e);
                }
            }

            produceSuccess = absBleSend != null;
        }

        if (produceSuccess) {
            // 连接成功，并且服务订阅成功，建立了真正的连接
            mLastConScanBle = mItem;
            if (mBmCallJs != null) {
                mBmCallJs.onResultHWConnection(address, true, true);
            }

            if (mItem != null) {
                ConBleSp.getConBleSp(app).setConMAC(mItem.address);
                new InsertDisplayDeviceDaoUtil(mItem).execute();
                new DeviceEnablePost(app, mItem.address).execute();
            }

        } else {

            Tlog.e(TAG, " produceFirstBleSend fail ...");

            if (address != null && mCurConnectedBle != null && address.equalsIgnoreCase(mCurConnectedBle.address)) {
                Tlog.v(TAG, " disconnect CurConnectedBle  ");
                disconnectBle(address);
            } else if (address != null && mCurConnectingBle != null && address.equalsIgnoreCase(mCurConnectingBle.address)) {
                Tlog.v(TAG, " disconnect CurConnectingBle  ");
                disconnectBle(address);
            } else {
                //  说明当前连接的设备已经断开连接了
                // 有可能是用户取消了连接，所以不做任何操作
                Tlog.v(TAG, " user cancel cur con device ");
            }

        }

    }

    /***************/

    @Override
    public void onPeripheralNotify(String mac, String uuidStr, byte[] data) {
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " BleRec:" + StrUtil.toString(data));
        }

        if (mProtocolInput != null) {
            ReceivesData mReceiverData = new ReceivesData(mac, data);
            mReceiverData.getReceiveModel().setModelIsBle();
            mReceiverData.obj = uuidStr;
            mProtocolInput.onInputProtocolData(mReceiverData);
        } else {
            Tlog.e(TAG, " onPeripheralNotify mProtocolInput=null ");
        }

    }


    @Override
    public void onOutputProtocolData(ResponseData mResponseData) {

        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " BleSend: " + mResponseData.toString());
        }

        if (absBleSend != null) {
            absBleSend.sendData(mResponseData.data);
        } else {

            Tlog.e(TAG, " onOutputDataToClient absBleSend==null ");

            if (mCurConnectingBle == null && mCurConnectedBle != null) {
                // 正在订阅服务
                Tlog.e(TAG, " maybe order service ");
            } else if (mCurConnectingBle != null && mCurConnectedBle == null) {
                // 正在重连接
                Tlog.e(TAG, " maybe auto con ");
            } else {
                if (mBmCallJs != null) {
                    mBmCallJs.onResultHWConnection(mResponseData.toID, false, true);
                }
            }


        }

    }

    @Override
    public void onBroadcastProtocolData(ResponseData mResponseData) {
        if (Debuger.isLogDebug) {
            Tlog.v(TAG, " BleBroad: " + mResponseData.toString());
        }
        onOutputProtocolData(mResponseData);
    }


}
