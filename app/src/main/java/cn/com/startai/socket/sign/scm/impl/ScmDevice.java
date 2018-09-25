package cn.com.startai.socket.sign.scm.impl;

import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.startai.socket.global.LooperManager;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.bean.sensor.SensorData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.TempHumidityData;
import cn.com.startai.socket.sign.scm.responsetask.Heartbeat;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.Repeat.RepeatMsg;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/2 0002
 * desc :
 */
class ScmDevice implements Heartbeat.OnHeartbeatCallBack {

    public static final String DEFAULT_MAC = "00:00:00:00:00:00";

    private String address;

    private ScmDevice.OnHeartbeatCallBack mHeartbeatCallBack;

    private RepeatMsg mRepeatMsg;

    ScmDevice(String address, IDataProtocolOutput mResponse, ScmDevice.OnHeartbeatCallBack mHeartbeatCallBack) {

        this.address = address;
        this.createTimestamp = System.currentTimeMillis();
        this.mHeartbeatCallBack = mHeartbeatCallBack;
        Looper mRepeatLooper = LooperManager.getInstance().getRepeatLooper();
        this.mHeartbeat = new Heartbeat(mRepeatLooper, mResponse, this, address);
        this.mRepeatMsg = new RepeatMsg(address, mRepeatLooper, mResponse);

    }


    private int conFailTimes;

    public final int getConFailTimes() {
        return conFailTimes;
    }

    private final void setConFailTimes(int conFailTimes) {
        this.conFailTimes = conFailTimes;
    }

    public void setConResult(boolean result) {
        if (!result) {
            int i = getConFailTimes();
            setConFailTimes(++i);
        } else {
            setConFailTimes(0);
            // 连接成功，默认为接收到心跳，预防上次接收心跳失败。
            onReceiveHeartbeat(true);
        }
    }


    private int token;

    public final int getToken() {
        return token;
    }

    public final void setToken(int token) {
        this.token = token;
        if (this.mHeartbeat != null) {
            this.mHeartbeat.setToken(token);
        }
    }

    public final void putIp(String ip) {
        if (mHeartbeat != null) {
            mHeartbeat.setCanSendHeartbeat(ip != null);
        }
    }

    public final void recordSendMsg(ResponseData mResponseData, long timeOut) {
        if (mRepeatMsg != null) {
            mRepeatMsg.recordSendMsg(mResponseData, timeOut);
        }
    }

    public final void receiveOnePkg(int what, int seq) {
        if (mRepeatMsg != null) {
            mRepeatMsg.receiveOnePkg(what, seq);
        }
    }


    public interface OnHeartbeatCallBack {
        void onStartSendHeartbeat(ScmDevice mScmDevice);

        void onHeartbeatLose(String mac, int diff);
    }


    private long createTimestamp;

    private final Heartbeat mHeartbeat;

    private boolean hasUpdateTime;

    private boolean publish;

    private int sendHeartTimes;
//    private int recHeartTimes;

    private final SpendingElectricityData mElectricityData = new SpendingElectricityData();

    public final SpendingElectricityData getElectricityData() {
        return mElectricityData;
    }


    private final SpendingElectricityData mSpendingData = new SpendingElectricityData();

    public final SpendingElectricityData getSpendingData() {
        return mSpendingData;
    }

    private final SensorData mSensorData = new SensorData();

    public final SensorData getSensorData() {
        return mSensorData;
    }

    private final TimingListData mTimingListData = new TimingListData();

    public final TimingListData getTimingListData() {
        return mTimingListData;
    }

    private final TempHumidityData mTempHumi = new TempHumidityData();

    public final TempHumidityData getTempHumidityData() {
        return mTempHumi;
    }

    public final void setTempHotAlarmData(float tempHotAlarmData) {
        this.mTempHumi.setTempHotAlarmData(tempHotAlarmData);
    }

    public final void setTempCodeAlarmData(float tempCodeAlarmData) {
        this.mTempHumi.setTempCodeAlarmData(tempCodeAlarmData);
    }

    private int requestRandom;

    public final void putRequestTokenRandom(int random) {
        this.requestRandom = random;
    }

    public final int getRequestRandom() {
        return requestRandom;
    }

    public final String getAddress() {
        return address;
    }

    public final boolean isUpdateScmTime() {
        return hasUpdateTime;
    }

    public final void setIsUpdateTime(boolean flag) {
        hasUpdateTime = flag;
    }

    public final boolean isPublish() {
        return publish;
    }

    public final void setIsPublish(boolean flag) {
        publish = flag;
    }

    @Override
    public void onStartSendHeartbeat(String mac) {
        ++sendHeartTimes;
        if (mHeartbeatCallBack != null) {
            mHeartbeatCallBack.onStartSendHeartbeat(this);
        }

        int diff = getLostHeartbeatTimes();

        if (diff > 2) {
            mHeartbeat.check(diff, 1000 * 3);
        }

    }

    @Override
    public void onCheckHeartbeat(String toID, int diff) {
        if (mHeartbeatCallBack != null) {
            if (diff <= getLostHeartbeatTimes()) {
                mHeartbeatCallBack.onHeartbeatLose(toID, diff);
            }
        }
    }

    public final void onReceiveHeartbeat(boolean result) {
        if (result) {
            sendHeartTimes = 0;
        } else {
            sendHeartTimes--;
        }
    }

    private int getLostHeartbeatTimes() {
        return sendHeartTimes;
    }

    public final void connected() {
        starHeartbeat();
    }

    public final void disconnected() {
        setIsPublish(false);
        stopHeartbeat();
    }

    public final void release() {
        disconnected();
        mRepeatMsg.removeCallbacksAndMessages(null);
    }

    public final void clearSensor() {

        this.mSensorData.clear();
        this.mTimingListData.clearAll();
        this.mTempHumi.clearAll();
    }

    private boolean checkAddress() {
        return address != null && !address.equalsIgnoreCase(DEFAULT_MAC);
    }

    public final void starHeartbeat() {

        if (!checkAddress()) {
            Tlog.e("SocketScmManager", " starHeartbeat byte address unInvalid " + address);
            return;
        }

        this.sendHeartTimes = 0;

        if (mHeartbeat != null) {
            mHeartbeat.start();
        }
    }

    public final void stopHeartbeat() {
        this.sendHeartTimes = 0;
        if (mHeartbeat != null) {
            mHeartbeat.stop();
        }
    }


    @Override
    public String toString() {
        return "createTimestamp:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(createTimestamp));
    }

}
