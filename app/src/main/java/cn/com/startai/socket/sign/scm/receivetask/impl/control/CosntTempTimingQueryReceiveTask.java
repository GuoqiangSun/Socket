package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import java.util.ArrayList;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.CustomManager;
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

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " CosntTempTimingQueryReceiveTask error:" + mSocketDataArray.toString());

            return;
        }

        int model = protocolParams[1];

        int length = protocolParams.length - 2;

        int onePkgLength;
        if (CustomManager.getInstance().isTriggerWiFi()) {
            if (length % 12 == 0) { // 不用管 onePkgLength=10的情况
                onePkgLength = 12;
            } else if (length % 10 == 0) {
                onePkgLength = 10;
            } else {
                if(length>12){
                    onePkgLength = 12;
                } else  {
                Tlog.e(TAG, " CosntTempTimingQueryReceiveTask error::" + mSocketDataArray.toString());
                    if (mTaskCallBack != null){
                    mTaskCallBack.onQueryConstTempTimingResult(mSocketDataArray.getID(), model, null);
                    }
                    return;
                }

            }
        } else if (CustomManager.getInstance().isTriggerBle()) {
            if (length % 13 == 0) {
                onePkgLength = 13;
            } else if (length % 10 == 0) { // 要管 onePkgLength=10的情况，蓝牙已经出货
                onePkgLength = 10;
            } else {
                Tlog.e(TAG, " CosntTempTimingQueryReceiveTask error:" + mSocketDataArray.toString());
                if (mTaskCallBack != null) {
                    mTaskCallBack.onQueryConstTempTimingResult(mSocketDataArray.getID(), model, null);
                }
                return;
            }
        } else {
            onePkgLength = 10; // 最开始的协议 一包长度为10
        }


        int j = length / onePkgLength;

        byte[] data = new byte[onePkgLength];

        ArrayList<ConstTempTiming> mArray = new ArrayList<>();
        ConstTempTiming mConstTempTiming;



        for (int i = 0; i < j; i++) {
            mConstTempTiming = new ConstTempTiming();
            System.arraycopy(protocolParams, 2 + i * onePkgLength, data, 0, onePkgLength);

            mConstTempTiming.mac = mSocketDataArray.getID();
            if (onePkgLength == 10) {
                mConstTempTiming.id = data[0] & 0xFF;
                mConstTempTiming.model = data[1] & 0xFF;
                mConstTempTiming.startup = data[2] & 0xFF;
                mConstTempTiming.minTemp = data[3] & 0xFF;
                mConstTempTiming.maxTemp = data[4] & 0xFF;
                mConstTempTiming.week = data[5] & 0xFF;
                mConstTempTiming.startHour = data[6] & 0xFF;
                mConstTempTiming.startMinute = data[7] & 0xFF;
                mConstTempTiming.endHour = data[8] & 0xFF;
                mConstTempTiming.endMinute = data[9] & 0xFF;
            } else {
                mConstTempTiming.id = data[0] & 0xFF;
                mConstTempTiming.model = data[1] & 0xFF;
                mConstTempTiming.startup = data[2] & 0xFF;
                mConstTempTiming.minTemp = data[3] & 0xFF;
                mConstTempTiming.minTempF = data[4] & 0xFF;
                mConstTempTiming.maxTemp = data[5] & 0xFF;
                mConstTempTiming.maxTempF = data[6] & 0xFF;
                mConstTempTiming.week = data[7] & 0xFF;
                mConstTempTiming.startHour = data[8] & 0xFF;
                mConstTempTiming.startMinute = data[9] & 0xFF;
                mConstTempTiming.endHour = data[10] & 0xFF;
                mConstTempTiming.endMinute = data[11] & 0xFF;
                mConstTempTiming.hasDecimal = true;
            }


            if (Debuger.isLogDebug) {
                Tlog.v(TAG, i + " :" + mConstTempTiming);
            }

            mArray.add(mConstTempTiming);
        }


        if (mTaskCallBack != null) {
            mTaskCallBack.onQueryConstTempTimingResult(mSocketDataArray.getID(), model, mArray);
        }

    }
}
