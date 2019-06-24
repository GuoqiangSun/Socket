package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.util.ArrayList;

import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.ConstTempTiming;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/26 0026
 * desc :
 */
public class CosntTempTimingQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mTaskCallBack;

    public CosntTempTimingQueryReceiveTask(OnTaskCallBack mTaskCallBack) {
        Tlog.e(TAG, " new CosntTempTimingQueryReceiveTask() ");
        this.mTaskCallBack = mTaskCallBack;
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 11) {
            Tlog.e(TAG, " CosntTempTimingQueryReceiveTask error:" + mSocketDataArray.toString());

            if (mTaskCallBack != null) {
                mTaskCallBack.onQueryConstTempTimingResult(mSocketDataArray.getID(), 0, null);
            }
            return;
        }

        int onePkgLength = 10;
        byte[] data = new byte[onePkgLength];

        int length = protocolParams.length - 1;
        int j = length / 10;

        ArrayList<ConstTempTiming> mArray = new ArrayList<ConstTempTiming>();
        ConstTempTiming mConstTempTiming;

        int model = 1;

        for (int i = 0; i < j; i++) {
            mConstTempTiming = new ConstTempTiming();
            System.arraycopy(protocolParams, 1 + i * onePkgLength, data, 0, onePkgLength);

            mConstTempTiming.mac = mSocketDataArray.getID();
            mConstTempTiming.id = data[0] & 0xFF;
            model = mConstTempTiming.model = data[1] & 0xFF;
            mConstTempTiming.startup = data[2] & 0xFF;
            mConstTempTiming.minTemp = data[3] & 0xFF;
            mConstTempTiming.maxTemp = data[4] & 0xFF;
            mConstTempTiming.week = data[5] & 0xFF;
            mConstTempTiming.startHour = data[6] & 0xFF;
            mConstTempTiming.startMinute = data[7] & 0xFF;
            mConstTempTiming.endHour = data[8] & 0xFF;
            mConstTempTiming.endMinute = data[9] & 0xFF;

            mArray.add(mConstTempTiming);
        }


        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryConstTempTimingResult(mSocketDataArray.getID(), model, mArray);
        }

    }
}