package cn.com.startai.socket.app.activity;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.plugin.exdevice.jni.C2JavaExDevice;
import com.tencent.mm.plugin.exdevice.jni.Java2CExDevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.com.startai.socket.app.SocketApplication;
import cn.com.startai.socket.db.gen.DaoSession;
import cn.com.startai.socket.db.gen.PowerCountdownDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.receivetask.ProtocolTaskImpl;
import cn.com.swain.support.ble.connect.AbsBleConnect;
import cn.com.swain.support.ble.connect.BleConnectEngine;
import cn.com.swain.support.ble.connect.IBleConCallBack;
import cn.com.swain.support.ble.scan.BleScanAuto;
import cn.com.swain.support.ble.scan.IBleScanObserver;
import cn.com.swain.support.ble.scan.ScanBle;
import cn.com.swain.support.protocolEngine.ProtocolProcessor;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.datagram.dataproducer.SocketDataQueueProducer;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/30 0030
 * desc :
 */

public class TestActivity extends AppCompatActivity {

    private static String TAG = SocketApplication.TAG;

    public static void main(String[] args) {
        long t = 1537954715260L;
        Date d = new Date(t);
        System.out.print(d.toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tlog.v(TAG, " testActivity onCreate ");

//        testSocketDataArray();
//        testProtocolManager();

//        IO io = new IO();
//        io.testException();

//        testBle();

//        testHandler();


//        WebView w;

//        testHandler1();


//        testDB();

//        byte[] buf = new byte[1024*1024*12];
//        StringBuffer sb = new StringBuffer(1024*1024*12);
//        sb.append("hello");

//        localDa
        testAirKiss();

//        com.android.internal.R.array.networkAttributes;

//        ConnectivityService c;
//        WifiCommand d;

    }


    private Long t;

    private void testAirKiss() {

        C2JavaExDevice.getInstance().setAirKissListener(new C2JavaExDevice.OnAirKissListener() {
            @Override
            public void onAirKissSuccess() {
                String msg = "配网成功 用时 " + ((System.currentTimeMillis() - t) / 1000) + "s";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAirKissFailed(int error) {
                String msg = "配置失败 errorCode = " + error + "";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                Java2CExDevice.stopAirKiss();
            }

        });

        t = System.currentTimeMillis();
        String ssid = "TP-LINK-STARTAI";
        String pwd = "13332965499";
        String aesKey = "";
        int processPeroid = 0;
        int datePeroid = 5;
        Java2CExDevice.startAirKissWithInter(pwd, ssid, aesKey.getBytes(), 1000 * 90, processPeroid, datePeroid);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pm != null) {
            pm.release();
        }

    }

    private void testDB() {


        DaoSession daoSession = DBManager.getInstance().getDaoSession();
        PowerCountdownDao powerCountdownDao = daoSession.getPowerCountdownDao();

        PowerCountdown pcd = new PowerCountdown();
        pcd.setSysTime(System.currentTimeMillis());
        pcd.setMac("00:00:00:00:00:00:00");
        pcd.setHour(8);
        pcd.setMinute(50);
        pcd.setStatus(true);
        powerCountdownDao.insert(pcd);

        pcd = new PowerCountdown();
        pcd.setSysTime(System.currentTimeMillis());
        pcd.setMac("00:00:00:00:00:00:11");
        pcd.setHour(6);
        pcd.setMinute(20);
        pcd.setStatus(true);
        powerCountdownDao.insert(pcd);


        Tlog.v(TAG, " queryData() ");

        List<PowerCountdown> users = powerCountdownDao.loadAll();
        for (int i = 0; i < users.size(); i++) {
            Tlog.v(TAG, users.get(i).toString());
        }

//        powerCountdownDao.load()

//        powerCountdownDao.lo

    }

    AbsBleConnect mBleCon;
    BleScanAuto mBleScan;
    private ScanBle mConBle;

    private void testBle() {

        final String NAME = "TRSPX-BLE";

        mBleCon = new BleConnectEngine(this, LooperManager.getInstance().getWorkLooper(), new IBleConCallBack() {
            @Override
            public void onResultConnect(boolean result, ScanBle mItem) {

            }

            @Override
            public void onResultAlreadyConnected(ScanBle mItem) {

            }

            @Override
            public void onResultDisconnectPassively(boolean result, ScanBle mItem) {

            }

            @Override
            public void onResultDisconnectActively(ScanBle mItem) {

            }

            @Override
            public void onResultServiceOrder(boolean result, ScanBle mItem, BluetoothGatt mConGatt) {

            }


            @Override
            public void onPeripheralNotify(String mac, String uuidStr, byte[] data) {

            }

            @Override
            public void onWriteDataFail(ScanBle mItem) {

            }
        }
        );

        mBleScan = new BleScanAuto(this, LooperManager.getInstance().getWorkLooper(), new IBleScanObserver() {
            @Override
            public void onBsStartScan() {

            }

            @Override
            public void onBsStopScan() {

            }

            @Override
            public void onResultBsGattScan(ScanBle mBle) {
                if (NAME.equals(mBle.name)) {
                    mConBle = mBle;
                    mBleCon.connect(mBle);
                }
            }

        });


//        mBleScan.


    }

    private ProtocolProcessor pm;

    private void testProtocolManager() {

//        ProtocolProcessorFactory.newSingleThreadAnalysisMutilTask(LooperManager.getInstance().getProtocolLooper(), new ProtocolTaskImpl(null, null),2);

        pm = new ProtocolProcessor(LooperManager.getInstance().getProtocolLooper(),
                new ProtocolTaskImpl(null, null),
                new SocketDataQueueProducer(0),
                3);

        new Thread() {
            @Override
            public void run() {
                super.run();

                int times = 0;

                while (true) {
//                    if (++times >= 20) {
//                        break;
//                    }
//        byte[] buf = new byte[]{(byte) 0xff,0,6,0,0,1,1,2,(byte)0x55,(byte)0xaa,(byte)0xeb,(byte)0xee };
//                    byte[] buf = new byte[]{(byte) 0xff, 0x02, 0x05, 0x00, 0x00, 0x01, 0x01, 0x01, 0x31, (byte) 0xcc};
//
//                    ReceivesData mData = new ReceivesData();
//                    mData.fromID = "123";
//                    mData.data = buf;
//                    pm.onInReceiveData(mData);

                    ReceivesData mData0 = new ReceivesData();
                    byte[] buf0 = new byte[]{(byte) 0xff, 0x00, 0x05, 0x00, 0x00, 0x01, 0x01, 0x01, 0x31, (byte) 0xee};
                    mData0.data = buf0;
                    pm.onInReceiveData(mData0);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

    private void testSocketDataArray() {
        SocketDataArray mSocketDataArray = new SocketDataArray(0);

//        心跳	0xff	0x0	0x5	0x0	0x0	0x01	0x01	0x01	效验（0x31）	0xee

        byte[] buf = new byte[]{(byte) 0xff, 0x00, 0x05, 0x00, 0x00, 0x01, 0x01, 0x01, 0x31, (byte) 0xee};

        buf = new byte[]{(byte) 0xff, 0, 6, 0, 0, 1, 1, 2, (byte) 0x55, (byte) 0xaa, (byte) 0xeb, (byte) 0xee};

        mSocketDataArray.onAddPackageReverse(buf);
        Tlog.v(TAG, mSocketDataArray.toString());

        mSocketDataArray.reset();

        buf = new byte[]{
                (byte) 0xff,
                0x00, 0x07, // vl
                0x00, 0x00,
                0x01, 0x01,
                0x02, (byte) 0xFF, (byte) 0xEE,
                (byte) 0xEB,  // crc
                (byte) 0xee};


        mSocketDataArray.onAddPackageEscape(buf);
        Tlog.v(TAG, mSocketDataArray.toString());


        ByteArrayOutputStream bos;


    }

    Handler h;

    private void testHandler() {

        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0:
                        Tlog.v(TAG, "case 0 ; has 0 " + h.hasMessages(0) + " has 1 " + h.hasMessages(1));
                        break;
                    case 1:
                        Tlog.v(TAG, "case 1 ; has 0 " + h.hasMessages(0) + " has 1 " + h.hasMessages(1));
                        break;
                }

            }
        };

        h.sendEmptyMessage(0);
        h.sendEmptyMessageDelayed(1, 2000);
        h.sendEmptyMessageDelayed(1, 3000);
        h.removeMessages(1);
    }

    private void testHandler1() {


    }

    public class IO {

        private void testException() throws INDEX {
            Tlog.v(TAG, "testException");
        }
    }

    private class INDEX extends IOException {

    }


}
