package cn.com.startai.socket.sign.scm.receivetask.impl.control;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class TimingListQueryReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mCallBack;

    public TimingListQueryReceiveTask(OnTaskCallBack mCallBack) {
        this.mCallBack = mCallBack;
        Tlog.e(TAG, " new TimingListQueryReceiveTask() ");
    }

    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 2) {
            Tlog.e(TAG, " TimingListQueryReceiveTask params is error ... " + mSocketDataArray.toString());
            return;
        }

        String mac = mSocketDataArray.getID();
        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        byte model = protocolParams[1]; // 0x01 普通模式 0x02进阶模式

        int paramLength = protocolParams.length;
        int listByteLength = paramLength - 2;

        Tlog.v(TAG, " result:" + result + " model:" + model + " length:" + paramLength);

        TimingListData mData = new TimingListData();
        mData.setModel(model);

        if (!result) {
            if (mCallBack != null) {

                mCallBack.onQueryTimingResult(mSocketDataArray.getID(), false, mData);

                IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
                if (iDebugerStream != null) {
                    iDebugerStream.receiveTiming(mSocketDataArray.getObj(), mSocketDataArray.getID());
                }

                return;
            }
        }

        if (mData.isCommonModel()) {
            final int onePkgLength = 6;
            if ((listByteLength >= onePkgLength) && (listByteLength % onePkgLength == 0)) {
                int number = listByteLength / onePkgLength;
                byte[] buf = new byte[onePkgLength];

                for (int i = 0; i < number; i++) {
                    System.arraycopy(protocolParams, 2 + i * onePkgLength, buf, 0, onePkgLength);
                    byte id = buf[0];
                    boolean on = SocketSecureKey.Util.on(buf[1]);
                    byte week = (byte) (buf[2] & 0xFF);
                    int hour = buf[3] & 0xFF;
                    int minute = buf[4] & 0xFF;
                    String time = hour + ":" + minute;
                    boolean startup = SocketSecureKey.Util.startup(buf[5]);
                    Tlog.v(TAG, "  common ArrayCopy id:" + id + " startup:" + on + " time:" + time + " week:" + week + " startup:" + startup);
                    mData.putCommonData(mac,id, on, week, time, startup);
                }
            }
        } else if (mData.isAdvanceModel()) {
            int onePkgLength;

            if (listByteLength % 11 == 0) {
                onePkgLength = 11;
            } else if (listByteLength % 12 == 0) {
                onePkgLength = 12;
            } else {
                onePkgLength = 11;
            }

            if ((listByteLength >= onePkgLength) && (listByteLength % onePkgLength == 0)) {
                int number = listByteLength / onePkgLength;
                byte[] buf = new byte[onePkgLength];

                for (int i = 0; i < number; i++) {
                    System.arraycopy(protocolParams, 2 + i * onePkgLength, buf, 0, onePkgLength);

                    TimingAdvanceData mAdvanceData = new TimingAdvanceData();
                    mAdvanceData.mac = mac;
                    mAdvanceData.id = buf[0];
                    mAdvanceData.startHour = buf[1] & 0xFF;
                    mAdvanceData.startMinute = buf[2] & 0xFF;
                    mAdvanceData.setOnTime(mAdvanceData.startHour + ":" + mAdvanceData.startMinute);
                    mAdvanceData.endHour = buf[3] & 0xFF;
                    mAdvanceData.endMinute = buf[4] & 0xFF;
                    mAdvanceData.setOffTime(mAdvanceData.endHour + ":" + mAdvanceData.endMinute);
                    mAdvanceData.on = SocketSecureKey.Util.on(buf[5]);
                    mAdvanceData.onIntervalHour = buf[6] & 0xFF;
                    mAdvanceData.onIntervalMinute = buf[7] & 0xFF;
                    mAdvanceData.offIntervalHour = buf[8] & 0xFF;
                    mAdvanceData.offIntervalMinute = buf[9] & 0xFF;
                    mAdvanceData.startup = SocketSecureKey.Util.startup(buf[10]);
                    if (buf.length > 11) {
                        mAdvanceData.week = buf[11];
                    }
                    Tlog.v(TAG, " advance Array copy : " + mAdvanceData.toString());
                    mData.putAdvanceData(mAdvanceData);

                }
            }
        }

        if (mCallBack != null) {

            mCallBack.onQueryTimingResult(mSocketDataArray.getID(), result, mData);

            IDebugerProtocolStream iDebugerStream = mCallBack.getIDebugerStream();
            if (iDebugerStream != null) {
                iDebugerStream.receiveTiming(mSocketDataArray.getObj(), mSocketDataArray.getID());
            }

        }

    }
}
