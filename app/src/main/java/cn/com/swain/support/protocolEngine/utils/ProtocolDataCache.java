package cn.com.swain.support.protocolEngine.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.swain.baselib.app.IApp.IService;
import cn.com.swain.support.protocolEngine.ProtocolBuild;
import cn.com.swain.support.protocolEngine.Repeat.RepeatMsgModel;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.datagram.dataproducer.ISocketDataProducer;
import cn.com.swain.support.protocolEngine.datagram.dataproducer.SyncSocketDataQueueProducer;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/10 0010
 * desc :
 */

public class ProtocolDataCache implements IService {

    public static class BuildParams {
        int mProtocolVersion;
        byte mCustom;
        byte mProduct;

        void release() {

        }

        public void setCustom(byte custom) {
            this.mCustom = custom;
        }

        public void setProduct(byte product) {
            this.mProduct = product;
        }

        public void setProtocolVersion(byte protocolVersion) {
            this.mProtocolVersion = protocolVersion;
        }

    }

    protected ProtocolDataCache() {

    }

    private static final class ClassHolder {
        private static final ProtocolDataCache CACHE = new ProtocolDataCache();
    }

    public static ProtocolDataCache getInstance() {
        return ClassHolder.CACHE;
    }

    private static int protocolVersion = ProtocolBuild.VERSION.VERSION_0;

    private static byte custom;
    private static byte product;

    public byte getProduct() {
        return product;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public static final String TAG = "ProtocolDataCache";

    public void init(BuildParams mParams) {
        if (mParams != null) {
            custom = mParams.mCustom;
            product = mParams.mProduct;
            protocolVersion = mParams.mProtocolVersion;
        }
    }


    @Override
    public void onSCreate() {

        Tlog.v(TAG, " custom:" + custom + " product:" + product + " protocolVersion:" + protocolVersion);

        checkISocketDataProducer();

    }

    @Override
    public void onSResume() {

    }

    @Override
    public void onSPause() {

    }

    @Override
    public void onSDestroy() {

        if (mDeviceMap != null) {
            mDeviceMap.clear();
        }

        if (mSocketDataProducer != null) {
            mSocketDataProducer.clear();
            mSocketDataProducer = null;
        }

    }

    @Override
    public void onSFinish() {

    }

    private static final Map<String, SEQ> mDeviceMap = Collections.synchronizedMap(new HashMap<String, SEQ>());

    private static final Object synObj = new byte[1];

    protected static SEQ getDevice(String mac) {
        SEQ device = mDeviceMap.get(mac);
        if (device == null) {
            synchronized (synObj) {
                device = mDeviceMap.get(mac);
                if (device == null) {
                    device = new SEQ(mac);
                    mDeviceMap.put(mac, device);
                }
            }
        }
        return device;
    }

    private ISocketDataProducer mSocketDataProducer;

    protected void checkISocketDataProducer() {
        if (mSocketDataProducer == null) {
            synchronized (synObj) {
                if (mSocketDataProducer == null) {
                    mSocketDataProducer = new SyncSocketDataQueueProducer(protocolVersion);
                }
            }
        }
    }

    protected ISocketDataProducer getSocketDataProducer() {
        checkISocketDataProducer();
        return mSocketDataProducer;
    }

    protected synchronized SocketDataArray produceSocketDataArray(String mac) {
        final SocketDataArray mSecureDataPack = produceSocketDataArrayNoSeq();
        mSecureDataPack.setSeq((byte) (getDevice(mac).getSelfAddSeq() & 0xFF));
        return mSecureDataPack;
    }

    protected synchronized SocketDataArray produceSocketDataArrayNoSeq() {
        final SocketDataArray mSecureDataPack = getSocketDataProducer().produceSocketDataArray();
        mSecureDataPack.setISUsed();
        mSecureDataPack.setParams(null);
        mSecureDataPack.reset();
        mSecureDataPack.changeStateToEscape();
        mSecureDataPack.setCustom(custom);
        mSecureDataPack.setProduct(product);
        return mSecureDataPack;
    }

    protected static ResponseData newResponseDataNoRecord(String mac, SocketDataArray mPack) {
        return newResponseData(mac, mPack, false);
    }

    protected static ResponseData newResponseDataRecord(String mac, SocketDataArray mPack) {
        return newResponseData(mac, mPack, true);
    }

    protected static ResponseData newResponseData(String mac, SocketDataArray mPack, boolean record) {
        final ResponseData responseData = new ResponseData(mac, mPack.organizeProtocolData());
        RepeatMsgModel repeatMsgModel = responseData.getRepeatMsgModel();
        repeatMsgModel.setMsgSeq(mPack.getSeq() & 0xFF);
        repeatMsgModel.setProductType(mPack.getProduct());
        repeatMsgModel.setMsgWhat((mPack.getType() & 0xFF) << 8 | (mPack.getCmd() & 0xFF));
        repeatMsgModel.setNeedRepeatSend(record);
        mPack.setISUnUsed();
        return responseData;
    }


    protected static ResponseData newResponseDataReport(String mac, SocketDataArray mPack) {
        final byte[] bytes = mPack.organizeProtocolData();
        final ResponseData responseData = new ResponseData(mac, bytes);
        mPack.setISUnUsed();
        return responseData;
    }


    /********控制*********/


    /**
     * 继电器开关的数据包
     *
     * @return ResponseData ResponseData
     */
    public static ResponseData getQuickSetRelaySwitch(String mac, boolean on) {
        ResponseData responseData = getSetRelaySwitch(mac, on, false);
        responseData.getSendModel().setSendModelIsWan();
        return responseData;
    }


    /**
     * 继电器开关的数据包
     *
     * @return ResponseData ResponseData
     */
    public static ResponseData getSetRelaySwitch(String mac, boolean on) {

        return getSetRelaySwitch(mac, on, true);
    }


    public static ResponseData getSetRelaySwitch(String mac, boolean on, boolean record) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_RELAY_SWITCH);
        final byte[] params = new byte[2];
        params[0] = SocketSecureKey.Model.MODEL_RELAY;
        params[1] = SocketSecureKey.Util.on(on);
        mSecureDataPack.setParams(params);
        return newResponseData(mac, mSecureDataPack, record);
    }

    /**
     * 倒计时
     *
     * @return ResponseData
     */
    public static ResponseData getSetCountdown(String mac, boolean status, boolean switchGear, int hour, int minute) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_COUNTDOWN);

        final byte[] params = new byte[4];
        params[0] = SocketSecureKey.Util.startup(status); // 1 启动 2 结束
        params[1] = SocketSecureKey.Util.on(switchGear); // 1 开 0 关
        params[2] = (byte) (hour & 0xFF);
        params[3] = (byte) (minute & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /**
     * 温度 湿度 (上限)
     *
     * @return ResponseData
     */
    public static ResponseData getSetTempHumidityAlarm(String mac, boolean startup, int model,
                                                       int valueInt, int valueDeci, boolean limitUp) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_ALARM);

        final byte[] params = new byte[5];
        params[0] = SocketSecureKey.Util.startup(startup);
        params[1] = (byte) model;
        params[2] = SocketSecureKey.Util.limitUp(limitUp);
        params[3] = (byte) (valueInt & 0xFF);
        params[4] = (byte) (valueDeci & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }


    public static ResponseData getSetCommonTiming(String mac, byte id, byte state, boolean on, byte week,
                                                  byte hour, byte minute, boolean startup) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_TIMING);

        final byte[] params = new byte[8];
        params[0] = SocketSecureKey.Model.TIMING_COMMON;//model 普通模式
        params[1] = id;
        params[2] = state;
        params[3] = SocketSecureKey.Util.on(on);
        params[4] = week;
        params[5] = hour;
        params[6] = minute;
        params[7] = SocketSecureKey.Util.startup(startup);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getSetAdvanceTiming(String mac, byte id, byte startHour, byte startMinute,
                                                   byte stopHour, byte stopMinute, boolean on, byte onIntervalHour,
                                                   byte onIntervalMinute, byte offIntervalHour, byte offIntervalMinute, boolean startup) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_TIMING);

        final byte[] params = new byte[12];
        params[0] = SocketSecureKey.Model.TIMING_ADVANCE;//model 高级模式
        params[1] = id;
        params[2] = startHour;
        params[3] = startMinute;
        params[4] = stopHour;
        params[5] = stopMinute;
        params[6] = SocketSecureKey.Util.on(on);
        params[7] = onIntervalHour;
        params[8] = onIntervalMinute;
        params[9] = offIntervalHour;
        params[10] = offIntervalMinute;
        params[11] = SocketSecureKey.Util.startup(startup);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }


    public static ResponseData getSetTime(String mac, byte year, byte month, byte day, byte hour, byte minute, byte second, byte week) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_TIME);

        final byte[] params = new byte[7];
        params[0] = year;
        params[1] = week;
        params[2] = month;
        params[3] = day;
        params[4] = hour;
        params[5] = minute;
        params[6] = second;
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /********查询*********/

    /**
     * 查询继电器开关的数据包
     *
     * @return ResponseData
     */
    public static ResponseData getQuickQueryRelayStatus(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_RELAY_STATUS);
        mSecureDataPack.setParams(new byte[]{SocketSecureKey.Model.MODEL_RELAY});
        ResponseData responseData = newResponseDataNoRecord(mac, mSecureDataPack);
        responseData.getSendModel().setSendModelIsWan();
        return responseData;
    }

    /**
     * 查询继电器开关的数据包
     *
     * @return ResponseData
     */
    public static ResponseData getQueryRelayStatus(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_RELAY_STATUS);
        mSecureDataPack.setParams(new byte[]{SocketSecureKey.Model.MODEL_RELAY});
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryCountdown(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_COUNTDOWN_DATA);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryTemperatureLimitUp(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_TEMPERATURE_HUMIDITY_DATA);

        final byte[] params = new byte[]{SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE,
                SocketSecureKey.Model.ALARM_LIMIT_UP};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryTemperatureLimitDown(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_TEMPERATURE_HUMIDITY_DATA);

        final byte[] params = new byte[]{SocketSecureKey.Model.ALARM_MODEL_TEMPERATURE,
                SocketSecureKey.Model.ALARM_LIMIT_DOWN,};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryHumidity(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_TEMPERATURE_HUMIDITY_DATA);

        final byte[] params = new byte[]{SocketSecureKey.Model.ALARM_MODEL_HUMIDITY};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryCommonTimingList(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_TIMING_LIST_DATA);

        final byte[] params = new byte[]{SocketSecureKey.Model.TIMING_COMMON};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryTime(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_TIME);

        return newResponseDataRecord(mac, mSecureDataPack);
    }


    /**
     * 查询定电量
     *
     * @param mac id
     * @return ResponseData
     */
    public static ResponseData getQuerySpendingElectricityE(String mac) {
        return getQuerySpendingElectricity(mac, SocketSecureKey.Model.SPENDING_ELECTRICITY_E);
    }

    /**
     * 查询定花费
     *
     * @param mac id
     * @return ResponseData
     */
    public static ResponseData getQuerySpendingElectricityS(String mac) {
        return getQuerySpendingElectricity(mac, SocketSecureKey.Model.SPENDING_ELECTRICITY_S);
    }

    private static ResponseData getQuerySpendingElectricity(String mac, byte model) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_SPENDING_ELECTRICITY_DATA);

        mSecureDataPack.setParams(new byte[]{model});
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getSetSpendingCountdown(String mac, boolean startup, byte model, byte y, byte m, byte d, int value) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_SPENDING_ELECTRICITY_DATA);

        byte[] params = new byte[7];
        params[0] = SocketSecureKey.Util.startup(startup);
        params[1] = model;
        params[2] = y;
        params[3] = m;
        params[4] = d;
        params[5] = (byte) ((value >> 8) & 0xFF);
        params[6] = (byte) (value & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }


    /********上报的回复**********/

    /**
     * 温度湿度response
     *
     * @param mac     id
     * @param success 是否成功
     * @return ResponseData
     */
    public static ResponseData getTempHumiValueReport(String mac, boolean success, byte seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_REPORT);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_TEMP_HUMI_REPORT_RESPONSE);
        mSecureDataPack.setSeq(seq);
        final byte[] params = new byte[]{SocketSecureKey.Util.resultSuccess(success)};
        mSecureDataPack.setParams(params);
        return newResponseDataReport(mac, mSecureDataPack);
    }

    /**
     * 温度湿度设置结束后的回复
     *
     * @param mac     id
     * @param success 是否成功
     * @return ResponseData
     */
    public static ResponseData getTempHumidityExecuteReport(String mac, boolean success, byte seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_REPORT);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_TEMPERATURE_HUMIDITY_REPORT_RESPONSE);
        mSecureDataPack.setSeq(seq);
        final byte[] params = new byte[]{SocketSecureKey.Util.resultSuccess(success)};
        mSecureDataPack.setParams(params);
        return newResponseDataReport(mac, mSecureDataPack);
    }

    /**
     * 电源的response
     *
     * @param mac     id
     * @param success 是否成功
     * @return ResponseData
     */
    public static ResponseData getElectricValueReport(String mac, boolean success, byte seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_REPORT);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_POWER_FREQ_REPORT_RESPONSE);
        mSecureDataPack.setSeq(seq);
        final byte[] params = new byte[]{SocketSecureKey.Util.resultSuccess(success)};
        mSecureDataPack.setParams(params);
        return newResponseDataReport(mac, mSecureDataPack);
    }

    public static ResponseData getTimingExecuteReport(String mac, boolean success, byte seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_REPORT);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_TIMING_REPORT_RESPONSE);
        mSecureDataPack.setSeq(seq);
        final byte[] params = new byte[]{SocketSecureKey.Util.resultSuccess(success)};
        mSecureDataPack.setParams(params);
        return newResponseDataReport(mac, mSecureDataPack);
    }

    public static ResponseData getCountdownExecuteReport(String mac, boolean success, byte seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_REPORT);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_COUNTDOWN_REPORT_RESPONSE);
        mSecureDataPack.setSeq(seq);
        final byte[] params = new byte[]{SocketSecureKey.Util.resultSuccess(success)};
        mSecureDataPack.setParams(params);
        return newResponseDataReport(mac, mSecureDataPack);
    }


    /********System**********/

    /**
     * 心跳报
     *
     * @return ResponseData
     */
    public static ResponseData getHeartbeat(String mac, int token, int seq) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArrayNoSeq();
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_HEARTBEAT);
        mSecureDataPack.setSeq((byte) (seq & 0xFF));
        final byte[] params = new byte[4];
        params[0] = (byte) ((token >> 24) & 0xFF);
        params[1] = (byte) ((token >> 16) & 0xFF);
        params[2] = (byte) ((token >> 8) & 0xFF);
        params[3] = (byte) ((token) & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataNoRecord(mac, mSecureDataPack);
    }


    public static ResponseData getVoltageAlarmValue(String mac, int value) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_VOLTAGE_ALARM_VALUE);

        final byte[] params = new byte[2];
        params[0] = (byte) ((value >> 8) & 0xFF);
        params[1] = (byte) (value & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getCurrentAlarmValue(String mac, byte value) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_CURRENT_ALARM_VALUE);

        final byte[] params = new byte[]{(byte) (value & 0xFF)};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getPowerAlarmValue(String mac, int value) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_POWER_ALARM_VALUE);

        final byte[] params = new byte[2];
        params[0] = (byte) ((value >> 8) & 0xFF);
        params[1] = (byte) (value & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getTempUnit(String mac, byte unit) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_UNIT_TEMPERATURE);

        final byte[] params = new byte[]{(byte) (unit & 0xFF)};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getMonetaryUnit(String mac, byte unit) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_UNIT_MONETARY);

        final byte[] params = new byte[]{(byte) (unit & 0xFF)};
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getElectricityPrice(String mac, int prices) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_PRICES_ELECTRICITY);

        final byte[] params = new byte[2];
        params[0] = (byte) ((prices >> 8) & 0xFF);
        params[1] = (byte) (prices & 0xFF);
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getRecovery(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_RECOVERY_SCM);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryVoltageAlarmValue(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_VOLTAGE_ALARM_VALUE);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryCurrentAlarmValue(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_CURRENT_ALARM_VALUE);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryPowerAlarmValue(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_POWER_ALARM_VALUE);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryTemperatureUnit(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_UNIT_TEMPERATURE);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryMonetaryUnit(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_UNIT_MONETARY);

        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getQueryElectricityPrices(String mac) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_QUERY_PRICES_ELECTRICITY);

        return newResponseDataRecord(mac, mSecureDataPack);
    }


    public synchronized static ResponseData getBindDevice(String mac, byte[] userIDByte, byte info, byte[] pwdBytes) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_BIND_DEVICE);

        final byte[] params = new byte[65];
        int length = 0;
        if (userIDByte != null) {
            length = userIDByte.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);
        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userIDByte, 0, params, 0, length);
        }
//
        int pointInfo = 32;
        params[pointInfo] = info;
//
        length = 0;
        if (pwdBytes != null) {
            length = pwdBytes.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, pointInfo + 1, 32);
        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(pwdBytes, 0, params, pointInfo + 1, length);
        }

        mSecureDataPack.setParams(params);
        ResponseData responseData = newResponseDataRecord(mac, mSecureDataPack);
        responseData.getSendModel().setSendModelIsLan();
        return responseData;
    }

    public synchronized static ResponseData getDiscoveryDevice(String mac, byte[] userIDByte) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_DISCOVERY_DEVICE);

        final byte[] params = new byte[32];

        int length = 0;
        if (userIDByte != null) {
            length = userIDByte.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);
        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userIDByte, 0, params, 0, length);
        }
        mSecureDataPack.setParams(params);
        return newResponseDataNoRecord(mac, mSecureDataPack);
    }

    private static final byte[] EMPTY_BYTES_32 = new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    public synchronized static ResponseData getRename(String mac, byte[] nameBytes) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_RENAME);

        final byte[] params = new byte[32];
        int length = 0;
        if (nameBytes != null) {
            length = nameBytes.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);

        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(nameBytes, 0, params, 0, length);
        }
        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    public static ResponseData getRequestToken(String mac, byte[] userID, int random) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_REQUEST_TOKEN);

        final byte[] params = new byte[32 + 4];
        int length = 0;
        if (userID != null) {
            length = userID.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);

        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userID, 0, params, 0, length);
        }

        params[32] = (byte) ((random >> 24) & 0xFF);
        params[32 + 1] = (byte) ((random >> 16) & 0xFF);
        params[32 + 2] = (byte) ((random >> 8) & 0xFF);
        params[32 + 3] = (byte) ((random) & 0xFF);

        mSecureDataPack.setParams(params);
        ResponseData responseData = newResponseDataRecord(mac, mSecureDataPack);
        responseData.getSendModel().setSendModelIsLan();
        return responseData;
    }

    public static ResponseData getControlDevice(String mac, byte[] userID, int token) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_CONTROL_TOKEN);


        final byte[] params = new byte[32 + 4];
        int length = 0;
        if (userID != null) {
            length = userID.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);

        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userID, 0, params, 0, length);
        }

        params[32] = (byte) ((token >> 24) & 0xFF);
        params[32 + 1] = (byte) ((token >> 16) & 0xFF);
        params[32 + 2] = (byte) ((token >> 8) & 0xFF);
        params[32 + 3] = (byte) ((token) & 0xFF);

        mSecureDataPack.setParams(params);

        ResponseData responseData = newResponseDataRecord(mac, mSecureDataPack);
        responseData.getSendModel().setSendModelIsLan();
        return responseData;
    }

    public static ResponseData getAppSleep(String mac, byte[] userID, int token) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SLEEP_TOKEN);


        final byte[] params = new byte[32 + 4];
        int length = 0;
        if (userID != null) {
            length = userID.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);

        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userID, 0, params, 0, length);
        }

        params[32] = (byte) ((token >> 24) & 0xFF);
        params[32 + 1] = (byte) ((token >> 16) & 0xFF);
        params[32 + 2] = (byte) ((token >> 8) & 0xFF);
        params[32 + 3] = (byte) ((token) & 0xFF);

        mSecureDataPack.setParams(params);
        ResponseData responseData = newResponseDataRecord(mac, mSecureDataPack);
        responseData.getSendModel().setSendModelIsLan();
        return responseData;
    }


    public static ResponseData getDisconnectDevice(String mac, byte[] userID, int token) {
        SocketDataArray mSecureDataPack = getInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_DISCONTROL_TOKEN);


        final byte[] params = new byte[32 + 4];
        int length = 0;
        if (userID != null) {
            length = userID.length;
        }
        System.arraycopy(EMPTY_BYTES_32, 0, params, 0, 32);

        if (length > 32) {
            length = 32;
        }
        if (length > 0) {
            System.arraycopy(userID, 0, params, 0, length);
        }

        params[32] = (byte) ((token >> 24) & 0xFF);
        params[32 + 1] = (byte) ((token >> 16) & 0xFF);
        params[32 + 2] = (byte) ((token >> 8) & 0xFF);
        params[32 + 3] = (byte) ((token) & 0xFF);

        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }


}
