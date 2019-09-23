package cn.com.startai.socket.sign.scm.receivetask;

import java.util.ArrayList;

import cn.com.startai.socket.debuger.impl.IDebugerProtocolStream;
import cn.com.startai.socket.mutual.js.bean.ColorLampRGB;
import cn.com.startai.socket.mutual.js.bean.NightLightTiming;
import cn.com.startai.socket.mutual.js.bean.TimingSetResult;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.scm.bean.CostRate;
import cn.com.startai.socket.sign.scm.bean.CountdownData;
import cn.com.startai.socket.sign.scm.bean.CumuParams;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.PointReport;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.bean.SpendingElectricityData;
import cn.com.startai.socket.sign.scm.bean.StateMachine;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingAdvanceData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingCommonData;
import cn.com.startai.socket.sign.scm.bean.Timing.TimingListData;
import cn.com.startai.socket.sign.scm.bean.TimingTempHumiData;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.ConstTempTiming;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Humidity;
import cn.com.startai.socket.sign.scm.bean.temperatureHumidity.Temperature;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public interface OnTaskCallBack {

    IDebugerProtocolStream getIDebugerStream();

    void onFail(FailTaskResult mFailTask);

    void onSuccess(String mac, byte tye, byte cmd, int seq);

    void onElectricResult(String mac, int power, int avePower, int maxPower, float freq, float voltage, float current);

    void onTempHumiResult(String mac, float temp, float humi);

    void onScmTimeResult(String mac, boolean result, long millis);

    /**
     * 继电器的状态
     *
     * @param mac
     * @param result
     */
    void onRelayResult(String mac, boolean result);

    /**
     * 设置倒计时结果
     *
     * @param mac
     * @param result  true success: false fail
     * @param startup true start ; false stop
     * @param on      true open ; false close
     */
    void onSetCountdownResult(String mac, boolean result, boolean startup, boolean on);

    /**
     * 查询倒计时数据结果
     */
    void onQueryCountdownResult(String mac, boolean result, CountdownData mCountdownData);

    /**
     * 倒计时数据上报结果
     */
    void onCountdownReportResult(String mac, CountdownData mCountdownData);

    /**
     * 温度湿度设置
     *
     * @param mac
     * @param result
     * @param startup
     * @param model
     * @param limit
     */
    void onSetTempHumiAlarmResult(String mac, boolean result, boolean startup, int model, int limit);


    /**
     * 查询温度
     *
     * @param mac
     * @param result
     * @param mTemperature
     */
    void onQueryTemperatureResult(String mac, boolean result, Temperature mTemperature);

    /**
     * 查询湿度
     *
     * @param mac
     * @param result
     * @param mHumidity
     */
    void onQueryHumidityResult(String mac, boolean result, Humidity mHumidity);


    /**
     * 查询定时
     *
     * @param mac
     * @param mData
     */
    void onQueryTimingResult(String mac, boolean result, TimingListData mData);


    /**
     * 设置定时结果
     *
     * @param mac
     * @param mResult
     */
    void onSetTimingResult(String mac, TimingSetResult mResult);


    /**
     * 定时器执行结果
     *
     * @param mac
     * @param mData
     */
    void onTimingCommonExecuteResult(String mac, TimingCommonData mData);

    void onHeartbeatResult(String mac, boolean result);

    void onSettingVoltageResult(String mac, boolean result, int mAlarmVoltageValue);

    void onSettingCurrentResult(String mac, boolean result, float mAlarmCurrentValue);

    void onSettingPowerResult(String mac, boolean result, int mAlarmPowerValue);

    void onSettingTemperatureUnitResult(String mac, boolean result, int mTemperatureUnit);

    void onSettingMonetaryUnitResult(String mac, boolean result, int mMonetaryUnit);

    void onSettingElectricityPriceResult(String mac, boolean result, int mElectricityPrice);

    void onSettingRecoveryResult(String mac, boolean result);

    void onQueryCurrentResult(String id, boolean result, float value);

    void onQueryElectricityPriceResult(String id, boolean result, int mElectricityPrices);

    void onQueryMonetaryUnitResult(String id, boolean result, int value);

    void onQueryPowerResult(String id, boolean result, int mAlarmPowerValue);

    void onQueryTemperatureUnitResult(String id, boolean result, int value);

    void onQueryVoltageResult(String id, boolean result, int mAlarmVoltageValue);

    void onDeviceDiscoveryResult(String id, boolean result, LanDeviceInfo mWiFiDevice);

    void onDeviceRenameResult(String id, boolean result, String name);

    void onQueryDeviceNameResult(String id, boolean result, String name);

    void onQueryDeviceSSIDResult(String id, boolean result, int rssi, String ssid);

    void onQuerySpendingElectricityResult(String id, boolean result, SpendingElectricityData mSpendingElectricityData);

    void onSetSpendingElectricityResult(String id, boolean result, SpendingElectricityData mSpendingElectricityData);

    void onTimingAdvanceExecuteResult(String id, TimingAdvanceData mAdvanceData);

    void onLanBindResult(boolean result, LanBindingDevice mLanBindingDevice);

    void onTokenInvalid(String mac);

    void onLanUnBindResult(boolean result, LanBindingDevice mLanBindingDevice);

    void onRequestTokenResult(boolean result, String mac, int random, int token);

    void onConnectResult(boolean result, String id);

    void onSleepResult(boolean result, String id);

    void onDisconnectResult(boolean result, String id);

    void onQueryHistoryCountResult(boolean result, QueryHistoryCount mCount);

    void onQueryNewHistoryCountResult(boolean result, QueryHistoryCount mCount);

    void onElectricityReportResult(boolean result, PointReport mElectricity);

    void onCostRateSetResult(boolean result, byte model);

    void onNewElectricResult(String id, int relpower, int avepower, int maxpower, float freq, float voltage, float current, float maxCurrent, float powerFactory);

    void onQueryCostRateResult(boolean result, CostRate mCostRate);

    void onQueryCumuParamsResult(boolean result, CumuParams cumuParams);

    void onUpdateVersionResult(boolean result, UpdateVersion mVersion);

    void onUSBResult(String id, boolean on);

    void onNightLightResult(String id, boolean on);

    void onSetTimezoneResult(boolean result, String id, byte zone);

    void onQueryTimezoneResult(boolean result, String id, byte zone);

    void onRGBSetResult(boolean result, ColorLampRGB mColorLampRGB);

    void onRGBQueryResult(boolean result, ColorLampRGB mColorLampRGB);

    void onSetTimingTempHumiResult(boolean result, String id, TimingTempHumiData mAdvanceData);

    void onQueryTimingTempHumiResult(boolean result, String id, ArrayList<TimingTempHumiData> mDataLst);


    void onRemoveCmd(String id, byte paramType, byte paramCmd, int seq);

    void onTestResult(byte[] protocolParams);

    void onSetNightLightResult(boolean result, NightLightTiming mNightLightTiming);

    void onQueryNightLightResult(boolean result, NightLightTiming mNightLightTiming);

    void onColorLamResult(String id, boolean b);

    void onIndicatorStatusResult(String mac, boolean result, byte seq, boolean on);

    void onQueryTempSensorResult(boolean result, String mac,boolean status);

    void onReportTempSensorResult( String mac,boolean status);

    void onStateMachineResult(StateMachine mStateMachine);

    void onQueryBleDeviceSensorResult(boolean result, String id, boolean status);

    void onQueryTElectricQuantitySensorResult(boolean result, String id, boolean status);

    void onQueryConstTempTimingResult(String id, int model, ArrayList<ConstTempTiming> mArray);

    void onSetConstTempTimingResult(ConstTempTiming mConstTempTiming);

    void onDelConstTempTimingResult(String id, byte result, byte id1, byte model);

    void onQueryRunningTimeResult(String id, boolean result, long time);

    void onQueryOnlineRunningTimeResult(String id, boolean result, long time);

    void onQueryLabelResult(String id, boolean resultIsOk, String label);

    void onSetQueryLabelResult(String id, boolean resultIsOk, String label);
}
