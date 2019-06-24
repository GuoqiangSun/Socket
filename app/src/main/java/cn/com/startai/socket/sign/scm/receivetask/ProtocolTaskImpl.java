package cn.com.startai.socket.sign.scm.receivetask;

import android.app.Application;

import cn.com.startai.socket.global.CustomManager;
import cn.com.startai.socket.sign.scm.receivetask.impl.MyErrorTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.MyTestTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.NullTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.ScmErrorTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CosntTempTimingDelReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CosntTempTimingQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CosntTempTimingSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CostRateQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CostRateSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CountdownQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CountdownSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.CumuParamQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.HistoryCountTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.IndicatorStatusControlReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.IndicatorStatusQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.MaxOutputQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.NewHistoryCountTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.NightLightQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.NightLightSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.RGBQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.RGBSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.SensorStatusQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.SpendingElectricityQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.SpendingElectricitySetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.StateMachineQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.SwitchControllerReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.SwitchQueryResponseTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TempHumiAlarmSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TempHumiQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimeQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimeSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimingListQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimingSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimingTempHumiQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.control.TimingTempHumiSetReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.CountdownReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.NewElectricReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.PointReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.SensorStatusReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.StateMachineReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.TempHumiRelayReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.TempHumiReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.report.TimingExecuteReportReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.CurrentQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.CurrentSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.ElectricityPricesQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.ElectricityPricesSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.HeartbeatReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.MonetaryUnitQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.MonetaryUnitSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.PowerQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.PowerSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.TemperatureUnitQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.TemperatureUnitSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.VoltageQueryReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.setting.VoltageSettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.ControlReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.DeviceBindTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.DeviceDiscoveryTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.DisControlReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.QueryNameReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.QuerySSIDReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.QueryTimezoneReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.RecoverySettingReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.RenameReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.RequestTokenReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.SetTimezoneReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.SleepReceiveTask;
import cn.com.startai.socket.sign.scm.receivetask.impl.system.UpdateReceiveTask;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.ProtocolCode;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.result.SimpleProtocolResult;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;
import cn.com.swain.support.protocolEngine.task.SocketResponseTask;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/8 0008
 * desc :
 */

public class ProtocolTaskImpl extends SimpleProtocolResult {

    private String TAG = SocketResponseTask.TAG;

    private IDataProtocolOutput mResponse;

    public void attachResponse(IDataProtocolOutput mResponse) {
        if (this.mResponse != null) {
            Tlog.e(TAG, " ProtocolTaskImpl attachResponse mResponse!=null ");
            return;
        }
        this.mResponse = mResponse;
    }

    private final OnTaskCallBack mTaskCallBack;

    private final Application app;

    public ProtocolTaskImpl(IDataProtocolOutput mResponse, OnTaskCallBack mTaskCallBack, Application app) {
        this.mResponse = mResponse;
        this.mTaskCallBack = mTaskCallBack;
        this.app = app;
    }

    @Override
    public void onFail(FailTaskResult failTaskResult) {
        if (mTaskCallBack != null) {
            mTaskCallBack.onFail(failTaskResult);
        }
        parseCrcError(failTaskResult);
    }

    private void parseCrcError(FailTaskResult failTaskResult) {

        if (CustomManager.getInstance().isAirtempNBProjectTest()) {
            if (failTaskResult.type == SocketSecureKey.Type.TYPE_REPORT
                    && failTaskResult.cmd == SocketSecureKey.Cmd.CMD_TEMP_HUMI_REPORT) {

                if (failTaskResult.data != null && failTaskResult.data.length >= 14) {

                    int temp_int = failTaskResult.data[13];
                    int temp_deci = failTaskResult.data[14] & 0xFF;
                    float tempF = Float.valueOf(temp_int + "." + temp_deci);
                    float temp = (float) (Math.round(tempF * 100)) / 100;

                    float humi = 0F;

                    if (failTaskResult.data.length >= 16) {
                        int humi_int = failTaskResult.data[15];
                        int humi_deci = failTaskResult.data[16] & 0xFF;
                        float humiF = Float.valueOf(humi_int + "." + humi_deci);
                        humi = (float) (Math.round(humiF * 100)) / 100;
                    }

                    if (mTaskCallBack != null) {
                        mTaskCallBack.onTempHumiResult(failTaskResult.mac, temp, humi);
                    }
                }

            }
        }

    }

    @Override
    public void onSuccess(SocketDataArray mParam) {

//        Tlog.v(TAG, " onSuccess SocketDataArray hasCode :" + mParam.hashCode());

        final byte protocolType = mParam.getProtocolType();
        final byte protocolCmd = mParam.getProtocolCmd();

        if (mTaskCallBack != null) {
            mTaskCallBack.onSuccess(mParam.getID(), protocolType, protocolCmd, mParam.getProtocolSequence());
        }

        switch (protocolType) {

            case SocketSecureKey.Type.TYPE_ERROR:

                switch (protocolCmd) {
                    case SocketSecureKey.Cmd.CMD_ERROR:
                        new MyErrorTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_TEST:
                        new MyTestTask(mTaskCallBack).execute(mParam);
                        break;
                    default:
                        new ScmErrorTask(ProtocolCode.ERROR_CODE_RESOLVE_CMD, mTaskCallBack).execute(mParam);
                        break;
                }
                break;

            case SocketSecureKey.Type.TYPE_SYSTEM:
                switch (protocolCmd) {
                    case SocketSecureKey.Cmd.CMD_HEARTBEAT_RESPONSE:
                        new HeartbeatReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_DISCOVERY_DEVICE_RESPONSE:
                        new DeviceDiscoveryTask(mTaskCallBack, app).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_BIND_DEVICE_RESPONSE:
                        new DeviceBindTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_UPDATE_RESPONSE:
                        new UpdateReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_RENAME_RESPONSE:
                        new RenameReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_NAME_RESPONSE:
                        new QueryNameReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_RECOVERY_SCM_RESPONSE:
                        new RecoverySettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_REGISTER_SCM_RESPONSE:
                        new NullTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_REQUEST_TOKEN_RESPONSE:
                        new RequestTokenReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_CONTROL_TOKEN_RESPONSE:
                        new ControlReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SLEEP_TOKEN_RESPONSE:
                        new SleepReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_DISCONTROL_TOKEN_RESPONSE:
                        new DisControlReceiveTask(mTaskCallBack).execute(mParam);
                        break;

                    case SocketSecureKey.Cmd.CMD_WUICK_CONTROL_SWITCH_RESPONSE:
                        new SwitchControllerReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_WUICK_QUERY_SWITCH_RESPONSE:
                        new SwitchQueryResponseTask(mTaskCallBack).execute(mParam);
                        break;

                    case SocketSecureKey.Cmd.CMD_SET_TIMEZONE_RESPONSE:
                        new SetTimezoneReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TIMEZONE_RESPONSE:
                        new QueryTimezoneReceiveTask(mTaskCallBack).execute(mParam);
                        break;

                    case SocketSecureKey.Cmd.CMD_SET_UNIT_TEMPERATURE_RESPONSE_BLE:
                        new TemperatureUnitSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_UNIT_TEMPERATURE_RESPONSE_BLE:
                        new TemperatureUnitQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_SSID_RESPONSE:
                        new QuerySSIDReceiveTask(mTaskCallBack).execute(mParam);
                        break;

                    default:
                        new ScmErrorTask(ProtocolCode.ERROR_CODE_RESOLVE_CMD, mTaskCallBack).execute(mParam);
                        break;
                }
                break;

            case SocketSecureKey.Type.TYPE_CONTROLLER:
                switch (protocolCmd) {
                    case SocketSecureKey.Cmd.CMD_SET_RELAY_SWITCH_RESPONSE:
                        new SwitchControllerReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_TIME_RESPONSE:
                        new TimeSetReceiveTask().execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_TIMING_RESPONSE:
                        new TimingSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_COUNTDOWN_RESPONSE:
                        new CountdownSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_ALARM_RESPONSE:
                        new TempHumiAlarmSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_RELAY_STATUS_RESPONSE:
                        new SwitchQueryResponseTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_COUNTDOWN_DATA_RESPONSE:
                        new CountdownQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TIME_RESPONSE:
                        new TimeQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TEMPERATURE_HUMIDITY_DATA_RESPONSE:
                        new TempHumiQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TIMING_LIST_DATA_RESPONSE:
                        new TimingListQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_SPENDING_ELECTRICITY_DATA_RESPONSE:
                        new SpendingElectricitySetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_SPENDING_ELECTRICITY_DATA_RESPONSE:
                        new SpendingElectricityQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_HISTORY_COUNT_RESPONSE:
                        new HistoryCountTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_COST_RATE_RESPONSE:
                        new CostRateSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_COST_RATE_RESPONSE:
                        new CostRateQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_CUMU_PARAM_RESPONSE:
                        new CumuParamQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_MAX_OUTPUT_RESPONSE:
                        new MaxOutputQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_LIGHT_COLOR_RESPONSE:
                        new RGBSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_LIGHT_COLOR_RESPONSE:
                        new RGBQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_TEMP_HUMI_ALARM_RESPONSE:
                        new TimingTempHumiSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TEMP_HUMI_ALARM_RESPONSE:
                        new TimingTempHumiQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_NIGHT_LIGHT_RESPONSE:
                        new NightLightSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_NIGHT_LIGHT_RESPONSE:
                        new NightLightQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_TEMP_SENSOR_STATUS_RESPONSE:
                        new SensorStatusQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_ANYNET_FLASH_RESPONSE:
                        new IndicatorStatusQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_CONTROL_ANYNET_FLASH_RESPONSE:
                        new IndicatorStatusControlReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_STATE_MACHINE_RESPONSE:
                        new StateMachineQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_NEW_HISTORY_RESPONSE:
                        new NewHistoryCountTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_CONST_TEMPERATURE_TIMING_RESPONSE:
                        new CosntTempTimingSetReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_CONST_TEMPERATURE_TIMING_RESPONSE:
                        new CosntTempTimingQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_DEL_CONST_TEMPERATURE_TIMING_RESPONSE:
                        new CosntTempTimingDelReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    default:
                        new ScmErrorTask(ProtocolCode.ERROR_CODE_RESOLVE_CMD, mTaskCallBack).execute(mParam);
                        break;
                }
                break;

            case SocketSecureKey.Type.TYPE_REPORT:

                switch (protocolCmd) {
                    case SocketSecureKey.Cmd.CMD_TEMP_HUMI_REPORT:
                        new TempHumiReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_POWER_FREQ_REPORT:
//                        new ElectricReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        new NewElectricReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_TEMPERATURE_HUMIDITY_REPORT:
                        new TempHumiRelayReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_COUNTDOWN_REPORT:
                        new CountdownReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_TIMING_REPORT:
                        new TimingExecuteReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_ELECTRICITY_REPORT:
                        new PointReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_TEMP_SENSOR_REPORT:
                        new SensorStatusReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_STATE_MACHINE_REPORT:
                        new StateMachineReportReceiveTask(mTaskCallBack, mResponse).execute(mParam);
                        break;
                    default:
                        new ScmErrorTask(ProtocolCode.ERROR_CODE_RESOLVE_CMD, mTaskCallBack).execute(mParam);
                        break;
                }
                break;

            case SocketSecureKey.Type.TYPE_SETTING:

                switch (protocolCmd) {
                    case SocketSecureKey.Cmd.CMD_SET_VOLTAGE_ALARM_VALUE_RESPONSE:
                        new VoltageSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_VOLTAGE_ALARM_VALUE_RESPONSE:
                        new VoltageQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_CURRENT_ALARM_VALUE_RESPONSE:
                        new CurrentSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_CURRENT_ALARM_VALUE_RESPONSE:
                        new CurrentQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_POWER_ALARM_VALUE_RESPONSE:
                        new PowerSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_POWER_ALARM_VALUE_RESPONSE:
                        new PowerQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_UNIT_TEMPERATURE_RESPONSE:
                        new TemperatureUnitSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_UNIT_TEMPERATURE_RESPONSE:
                        new TemperatureUnitQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_UNIT_MONETARY_RESPONSE:
                        new MonetaryUnitSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_UNIT_MONETARY_RESPONSE:
                        new MonetaryUnitQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_SET_PRICES_ELECTRICITY_RESPONSE:
                        new ElectricityPricesSettingReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                    case SocketSecureKey.Cmd.CMD_QUERY_PRICES_ELECTRICITY_RESPONSE:
                        new ElectricityPricesQueryReceiveTask(mTaskCallBack).execute(mParam);
                        break;
                }

                break;

            default:
                new ScmErrorTask(ProtocolCode.ERROR_CODE_RESOLVE_TYPE, mTaskCallBack).execute(mParam);
                break;
        }
    }

}
