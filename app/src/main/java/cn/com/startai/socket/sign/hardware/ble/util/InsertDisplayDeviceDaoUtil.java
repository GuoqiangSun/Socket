package cn.com.startai.socket.sign.hardware.ble.util;

import android.os.AsyncTask;

import java.util.List;

import cn.com.startai.socket.db.gen.DisplayBleDeviceDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.mutual.js.bean.DisplayBleDevice;
import cn.com.startai.socket.sign.hardware.ble.impl.BleManager;
import cn.com.swain.support.ble.scan.ScanBle;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/16 0016
 * desc :
 */
public class InsertDisplayDeviceDaoUtil extends AsyncTask<Void, Void, String> {

    private final ScanBle mItem;

    public InsertDisplayDeviceDaoUtil(ScanBle mItem) {
        this.mItem = mItem;
    }

    @Override
    protected String doInBackground(Void... voids) {

        DisplayBleDevice mDisplayDevice = new DisplayBleDevice().memorize(mItem);
        DisplayBleDeviceDao displayDeviceDao = DBManager.getInstance().getDaoSession().getDisplayBleDeviceDao();
        List<DisplayBleDevice> list = displayDeviceDao.queryBuilder()
                .where(DisplayBleDeviceDao.Properties.Address.eq(mItem.address)).list();

        if (list.size() <= 0) {
            long insert = displayDeviceDao.insert(mDisplayDevice);
            Tlog.v(BleManager.TAG, " InsertDisplayDeviceDaoUtil insert displayDevice :" + insert);
        } else {
            DisplayBleDevice displayBleDevice = list.get(0);
            mDisplayDevice.setId(displayBleDevice.getId());
            mDisplayDevice.setHasRemoteActivation(displayBleDevice.getHasRemoteActivation());
            displayDeviceDao.update(mDisplayDevice);
            Tlog.v(BleManager.TAG, " InsertDisplayDeviceDaoUtil update displayDevice :" + displayBleDevice.getId());
        }

        return null;
    }


}
