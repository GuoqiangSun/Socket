package cn.com.startai.socket.sign.scm.receivetask.impl.system;

import cn.com.startai.socket.sign.scm.receivetask.OnTaskCallBack;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/1 0001
 * desc :
 */
public class QuerySSIDReceiveTask extends SocketResponseTask {

    private OnTaskCallBack mOnTaskCallBack;

    public QuerySSIDReceiveTask(OnTaskCallBack mOnTaskCallBack) {
        this.mOnTaskCallBack = mOnTaskCallBack;
        Tlog.e(TAG, " new QuerySSIDReceiveTask ");
    }


    @Override
    protected void doTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams.length < 33) {
            if (mOnTaskCallBack != null) {
                mOnTaskCallBack.onQueryDeviceSSIDResult(mSocketDataArray.getID(), false, -100, "UNKNOWN");
            }
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        int rssi = protocolParams[1];
        if (rssi > 0) {
            rssi -= 100;
        }

        String ssid;
        ssid = new String(protocolParams, 2, 32);
        ssid = ssid.trim().replaceAll("\\s*", "");
        Tlog.v(TAG, " ssid :" + ssid + " rssi:" + rssi + " result:" + result);

//        if (StrUtil.isSpecialName(deviceName)) {
//            int random = (int) ((Math.random() * 9 + 1) * 100000);
//            deviceName = "UNKNOWN" + random;
//        }

        if (mOnTaskCallBack != null) {
            mOnTaskCallBack.onQueryDeviceSSIDResult(mSocketDataArray.getID(), result, rssi, ssid);
        }

    }
}
