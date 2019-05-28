package cn.com.startai.socket.sign.scm;

import java.util.ArrayList;

import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.scm.bean.CostRate;
import cn.com.startai.socket.sign.scm.bean.CountdownData;
import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.PointReport;
import cn.com.startai.socket.sign.scm.bean.PowerCountdown;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.RenameBean;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.StateMachine;
import cn.com.startai.socket.sign.scm.bean.TempHumidityAlarmData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.startai.socket.sign.scm.bean.sensor.SensorData;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.TempHumidityData;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/28 0028
 * desc :
 */
public interface IVirtualSocketScm {


    /**
     * 是否可以向js下发sensor数据
     *
     * @param publish
     */
    void publishSensorData(String mac, boolean publish);

    /**
     * 继电器开关
     *
     * @param mac
     * @param status
     */
    void switchRelay(String mac, boolean status);

    void switchFlash(String mac, boolean status);

    /**
     * 查询继电器的状态
     *
     * @param mac
     */
    void queryRelayState(String mac);

    /**
     * 查询闪光灯的状态
     *
     * @param mac
     */
    void queryFlashState(String mac);


    /**
     * 设置倒计时
     *
     * @param powerCountdown
     */
    void setPowerCountdown(PowerCountdown powerCountdown);


    /**
     * 设置定时
     */
    void setCommonTiming(TimingCommonData mTimingCommonData);

    /**
     * 设置报警温度湿度
     *
     * @param mAlarm
     */
    void setTempHumidityAlarm(TempHumidityAlarmData mAlarm);

    /**
     * 查询温度湿度设置的报警阈值
     *
     * @param mac
     */
    void queryTempHumidityData(String mac);

    /**
     * 查询倒计时数据
     *
     * @param mac
     */
    void queryCountdownData(String mac);

    /**
     * 查询定时列表
     *
     * @param mac
     */
    void queryTimingData(String mac);

    /**
     * 查询单片机的时间
     *
     * @param mac
     */
    void queryScmTime(String mac);

    /**
     * 查询单片机的时间
     *
     * @param mac
     */
    void queryScmTimezone(String mac);

    /**
     * 设置单片机的系统时间
     *
     * @param mac
     */
    void setScmTime(String mac);

    /**
     * 设置单片机的系统时间
     *
     * @param mac
     */
    void setScmTimezone(String mac);

    /**
     * 设备连接
     *
     * @param mac
     */
    void onConnected(String mac);

    /**
     * 设备断开连接
     *
     * @param mac
     */
    void onDisconnected(String mac);

    void lanComModel(boolean result, String mac);

    void setSetVoltageAlarmValue(String mac, int value);

    void setSetCurrentAlarmValue(String mac, int value);

    void setSetPowerAlarmValue(String mac, int value);

    void setSetTemperatureUnit(String mac, int value);

    void setSetMonetaryUnit(String mac, int value);

    void setSetRecoveryScm(String mac);

    void setSetElectricityPrice(String mac, int value);

    void queryVoltageAlarmValue(String mac);

    void queryCurrentAlarmValue(String mac);

    void queryPowerAlarmValue(String mac);

    void queryTemperatureUnit(String mac);

    void queryMonetaryUnit(String mac);

    void queryElectricityPrice(String mac);

    void rename(RenameBean obj);

    void queryDeviceName(String mac);

    void querySpendingElectricity(String mac);

    void setSpendingCountdown(SpendingElectricityData obj);

    void setAdvanceTiming(TimingAdvanceData mTimingAdvanceData);

    void bindDevice(LanBindInfo mLanBindInfo, String userID);

    void unbindDevice(String mac, String userID);

    void requestToken(String mac, String userID);

    void controlDevice(String mac, String userID, int token);

    void appSleep(String mac, String userID, int token);

    void disconnectDevice(String mac, String userID, int token);

    /**
     * @param mScmResultCallBack
     */
    void regIScmResultCallBack(IScmResultCallBack mScmResultCallBack);

    void onNetworkChange();

    void quickControlRelay(String mac, boolean on);

    void quickQueryRelay(String mac);

    void queryHistoryCount(QueryHistoryCount mQueryCount);

    void queryNewHistoryCount(QueryHistoryCount mQueryCount);

    void queryCostRate(String mac);

    void queryCumuParam(String mac);

    void queryVersion(String mac);

    void querySSID(String mac);

    void update(String mac);

    void setColorLamp(String mac, int i, int r, int g, int b);

    void setLightRGB(ColorLampRGB obj);

    void queryUSBState(String mac);

    void setUSBState(String mac, boolean state);

    int getScmToken(String mac);

    void setTemperatureTimingAlarm(TimingTempHumiData obj);

    void queryTemperatureTimingAlarm(String mac, int model);

    void queryYellowLightRGB(String mac);

    void queryColourLampRGB(String mac);

    void queryComTimingListData(String mac);

    void queryAdvTimingListData(String mac);

    void setNightLightTiming(NightLightTiming nightLightTiming);

    void setNightLightWisdom(NightLightTiming nightLightTiming);

    void openWisdomNightLight(String mac);

    void closeWisdomNightLight(String mac);

    void queryWisdomNightLight(String mac);

    void queryTimingNightLight(String mac);

    void queryNightLight(String mac);

    void switchNightLight(String mac, boolean b);

    void queryRunningNightLight(String mac);

    void setNightLightColor(ColorLampRGB obj);

    void queryIndicatorState(String mac);

    void controlIndicatorState(String mac, boolean b);

    void queryTemperatureSensor(String mac);

    void onJSQueryTotalElectric(SpendingElectricityData obj);

    void queryMachineState(String mac);

    void queryElectricQuantity(String mac);

    void queryBleDevice(String mac);

    /**
     * author: Guoqiang_Sun
     * date : 2018/4/10 0010
     * desc : 协议结果
     */

    interface IScmResultCallBack {

        void onResultPublishSensorData(String mac, SensorData mSensorData);

        void onResultSwitchRelay(String mac, boolean status);

        void onResultSetCountdown(String mac, boolean result, boolean status);

        void onResultQueryTemperatureHumidityData(String mac, TempHumidityData mTempHumidityData);

        void onResultSetTemperatureAlarm(String mac, boolean result, boolean startup, int limit);

        void onResultSetHumidityAlarm(String mac, boolean result, boolean startup, int limit);

        void onResultQueryCountdown(String mac, CountdownData mCountdownData);

        void onResultQueryTiming(String mac, TimingListData mTimingListData);

        void onResultSetTiming(String mac, TimingSetResult mResult);

        void onResultScmTime(String mac, boolean result, long time);

        void onResultSettingVoltage(String mac, boolean result);

        void onResultSettingCurrent(String mac, boolean result);

        void onResultSettingPower(String mac, boolean result);

        void onResultSettingTemperatureUnit(String mac, boolean result);

        void onResultSettingMonetaryUnit(String mac, boolean result, int mMonetaryUnit);

        void onResultSettingElectricityPrice(String mac, boolean result);

        void onResultSettingRecovery(String mac, boolean result);

        void onResultQueryCurrentAlarmValue(String id, boolean result, float value);

        void onResultQueryElectricityPrice(String id, boolean result, int mElectricityPrices);

        void onResultQueryMonetaryUnit(String id, boolean result, int value);

        void onResultQueryPowerAlarmValue(String id, boolean result, int mAlarmPowerValue);

        void onResultQueryTemperatureUnit(String id, boolean result, int value);

        void onResultQueryVoltageAlarmValue(String id, boolean result, int mAlarmVoltageValue);

        void onResultDeviceDiscovery(String id, boolean result, LanDeviceInfo mDevice);

        void onResultRename(String id, boolean result, String name);

        void onResultQuerySpendingElectricity(String id, boolean result, SpendingElectricityData mElectricityData, SpendingElectricityData mSpendingData);

        void onResultSetSpendingElectricity(String id, int model, boolean alarmSwitch, boolean result);

        void onResultDeviceLanBind(boolean result, LanBindingDevice mLanBindingDevice);

        void onResultHeartbeatLose(String mac, int loseTimes);

        void onTokenInvalid(String mac);

        void onLanUnBindResult(boolean result, LanBindingDevice mLanBindingDevice);

        void onResultRequestToken(String mac, int token);

        void onResultConnect(boolean result, String id);

        void onResultSleep(boolean result, String id);

        void onResultDisconnect(boolean result, String id);

        void onQueryHistoryCountResult(boolean result, QueryHistoryCount mCount);

        void onResultQueryCostRate(boolean result, CostRate mCostRate);

        void onResultQueryCumuParams(boolean result, CumuParams cumuParams);

        void onResultHeartbeatReceive(String mac, boolean result);

        void onResultUpdateVersion(boolean result, UpdateVersion mVersion);

        void onResultUSBState(String id, boolean on);

        void onElectricityReportResult(boolean result, PointReport mElectricity);

        int getTokenFromDB(String mac);

        void onRGBSetResult(boolean result, ColorLampRGB mColorLampRGB);

        void onRGBQueryResult(boolean result, ColorLampRGB mColorLampRGB);

        void onResultSetTimingTempHumi(boolean result, String id, TimingTempHumiData mAdvanceData);

        void onResultQueryTimingTempHumi(boolean result, String id, ArrayList<TimingTempHumiData> mDataLst);

        void onResultQueryRename(String id, boolean result, String name);

        void onResultQueryDeviceSSID(String id, boolean result, int rssi, String ssid);

        void onResultSetNightLight(boolean result, NightLightTiming mNightLightTiming);

        void onResultQueryNightLight(boolean result, NightLightTiming mNightLightTiming);

        void onNightLightResult(String id, boolean on);

        void onResultColorLam(String id, boolean b);

        void onRGBYellowSetResult(boolean b, ColorLampRGB mColorLamp);

        void onResultIndicatorStatus(String mac, boolean result, byte seq, boolean on);

        void onResultQueryTempSensor(boolean result, String mac, boolean status);

        void onResultTempSensorReport(String mac, boolean status);

        void onResultTotalElectricData(SpendingElectricityData obj);

        void onResultStateMachine(StateMachine mStateMachine);

        void onResultQueryBleDeviceSensor(boolean result, String id, boolean status);

        void onResultQueryTElectricQuantitySensor(boolean result, String id, boolean status);
    }


}
