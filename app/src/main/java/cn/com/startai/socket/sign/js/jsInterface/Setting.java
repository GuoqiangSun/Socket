package cn.com.startai.socket.sign.js.jsInterface;

import org.xwalk.core.JavascriptInterface;

import cn.com.startai.socket.sign.js.util.H5Config;
import cn.com.swain.baselib.jsInterface.AbsJsInterface;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/29 0029
 * desc :
 */
public class Setting extends AbsJsInterface {

    public interface IJSSettingCallBack extends Language.IJSLanguageCallBack {

        void onJSSetVoltageAlarm(String mac, int value);

        void onJSSetCurrentAlarm(String mac, int value);

        void onJSSetPowerAlarm(String mac, int value);

        void onJSSetTemperatureUnit(String mac, int unit);

        void onJSSetMonetaryUnit(String mac, int unit);

        void onJSSetLocalElectricity(String mac, int value);

        void onJSSetRecovery(String mac);

        void onJSQueryVoltageAlarmValue(String mac);

        void onJSQueryCurrentAlarmValue(String mac);

        void onJSQueryPowerAlarmValue(String mac);

        void onJSQueryTemperatureUnit(String mac);

        void onJSQueryMonetaryUnit(String mac);

        void onJSQueryElectricityPrices(String mac);

        void onJSQueryBackupTimeDirectory(String mac);

        void onJSSaveBackupData(String mac, String jsonData);

        void onJSRecoveryData(String mac);

        void onJSQueryIndicatorLightState(String mac);

        void onJSControlIndicatorLightState(String mac, boolean on);
    }

    public static final class Method {
        private static final String METHOD_ALARM_VOLTAGE_RESPONSE
                = "javascript:settingAlarmVoltageResponse('$mac',$result)";

        public static String callJsSetAlarmVoltageResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_ALARM_VOLTAGE_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_ALARM_CURRENT_RESPONSE
                = "javascript:settingAlarmCurrentResponse('$mac',$result)";

        public static String callJsSetAlarmCurrentResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_ALARM_CURRENT_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_ALARM_POWER_RESPONSE
                = "javascript:settingAlarmPowerResponse('$mac',$result)";

        public static String callJsSetAlarmPowerResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_ALARM_POWER_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_TEMPERATURE_UNIT_RESPONSE
                = "javascript:settingTemperatureUnitResponse('$mac',$result)";

        public static String callJsSetTemperatureUnitResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_TEMPERATURE_UNIT_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_MONETARY_UNIT_RESPONSE
                = "javascript:settingMonetarytUnitResponse('$mac',$result)";

        public static String callJsSetMonetaryUnitResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_MONETARY_UNIT_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_LOCAL_ELECTRICITY_RESPONSE
                = "javascript:settingLocalElectricityResponse('$mac',$result)";

        public static String callJsSetElectricityResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_LOCAL_ELECTRICITY_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_RESUME_SETUP_RESPONSE
                = "javascript:settingResumeSetupResponse('$mac',$result)";

        public static String callJsSetRecoveryResult(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_RESUME_SETUP_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_QUERY_VOLTAGE_ALARM_VALUE
                = "javascript:queryAlarmVoltageResponse('$mac',$result,$value)";

        public static String callJsQueryVoltageAlarmValue(String mac, boolean result, int value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_VOLTAGE_ALARM_VALUE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_CURRENT_ALARM_VALUE
                = "javascript:queryAlarmCurrentResponse('$mac',$result,$value)";

        public static String callJsQueryCurrentAlarmValue(String mac, boolean result, float value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_CURRENT_ALARM_VALUE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_POWER_ALARM_VALUE
                = "javascript:queryAlarmPowerResponse('$mac',$result,$value)";

        public static String callJsQueryPowerAlarmValue(String mac, boolean result, int value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_POWER_ALARM_VALUE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_TEMPERATURE_UNIT
                = "javascript:queryTemperatureUnitResponse('$mac',$result,$value)";

        public static String callJsQueryTemperatureUnit(String mac, boolean result, int value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_TEMPERATURE_UNIT.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_MONETARY_UNIT
                = "javascript:queryMonetarytUnitResponse('$mac',$result,$value)";

        public static String callJsQueryMonetaryUnit(String mac, boolean result, int value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_MONETARY_UNIT.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_ELECTRICITY_PRICES
                = "javascript:queryLocalElectricityResponse('$mac',$result,$value)";

        public static String callJsQueryElectricityPrices(String mac, boolean result, int value) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_ELECTRICITY_PRICES.replace("$mac", mac)
                    .replace("$result", String.valueOf(result))
                    .replace("$value", String.valueOf(value));
        }

        private static final String METHOD_QUERY_BACKUPDIR_RESPONSE
                = "javascript:BackupTimeAndDirectoryResponse('$mac',$timestamp,'$dir')";

        public static String callJsQueryBackupdir(String mac, long timestamp, String dir) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_QUERY_BACKUPDIR_RESPONSE.replace("$mac", mac)
                    .replace("$timestamp", String.valueOf(timestamp))
                    .replace("$dir", dir);
        }

        private static final String METHOD_SAVE_BACKUPDATA_RESPONSE
                = "javascript:BackupDataResponse('$mac',$result)";

        public static String callJsSaveBackupdir(String mac, boolean result) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_SAVE_BACKUPDATA_RESPONSE.replace("$mac", mac)
                    .replace("$result", String.valueOf(result));
        }

        private static final String METHOD_BACKUPRECOVERY_RESPONSE
                = "javascript:BackupRecoveryDataResponse('$mac','$data')";

        public static String callJsRecoveryData(String mac, String data) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_BACKUPRECOVERY_RESPONSE.replace("$mac", mac)
                    .replace("$data", String.valueOf(data));
        }

        private static final String METHOD_INDICATOR_RESPONSE
                = "javascript:indicatorLightStateResponse('$mac',$state)";

        public static String callJsIndicatorData(String mac, boolean state) {
            if (mac == null || "".equals(mac)) mac = H5Config.DEFAULT_MAC;
            return METHOD_INDICATOR_RESPONSE.replace("$mac", mac)
                    .replace("$state", String.valueOf(state));
        }

    }

    private final IJSSettingCallBack mCallBack;

    public Setting(IJSSettingCallBack mCallBack) {
        super(NAME_JSI);
        this.mCallBack = mCallBack;

    }

    public static final String NAME_JSI = "Setting";

    private String TAG = H5Config.TAG;

    @JavascriptInterface
    public void settingAlarmVoltageRequest(String mac, int value) {
        Tlog.v(TAG, " settingAlarmVoltageRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetVoltageAlarm(mac, value);
        }
    }

    @JavascriptInterface
    public void settingAlarmCurrentRequest(String mac, int value) {
        Tlog.v(TAG, " settingAlarmCurrentRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetCurrentAlarm(mac, value);
        }
    }

    @JavascriptInterface
    public void settingAlarmPowerRequest(String mac, int value) {
        Tlog.v(TAG, " settingAlarmPowerRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetPowerAlarm(mac, value);
        }
    }

    //    1 ℃
// 2 ℉
    @JavascriptInterface
    public void settingTemperatureUnitRequest(String mac, int value) {
        Tlog.v(TAG, " settingTemperatureUnitRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetTemperatureUnit(mac, value);
        }
    }

    //    1 人民币 2 英镑 3 美元 4 欧元 5 日元
    @JavascriptInterface
    public void settingMonetarytUnitRequest(String mac, int value) {
        Tlog.v(TAG, " settingMonetarytUnitRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetMonetaryUnit(mac, value);
        }
    }

    @JavascriptInterface
    public void settingLocalElectricityRequest(String mac, int value) {
        Tlog.v(TAG, " settingLocalElectricityRequest mac:" + mac + " value:" + value);
        if (mCallBack != null) {
            mCallBack.onJSSetLocalElectricity(mac, value);
        }
    }

    @JavascriptInterface
    public void settingResumeSetupRequest(String mac) {
        Tlog.v(TAG, " settingResumeSetupRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSSetRecovery(mac);
        }
    }

    @JavascriptInterface
    public void queryAlarmVoltageRequest(String mac) {
        Tlog.v(TAG, " queryAlarmVoltageRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryVoltageAlarmValue(mac);
        }
    }

    @JavascriptInterface
    public void queryAlarmCurrentRequest(String mac) {
        Tlog.v(TAG, " queryAlarmCurrentRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryCurrentAlarmValue(mac);
        }
    }

    @JavascriptInterface
    public void queryAlarmPowerRequest(String mac) {
        Tlog.v(TAG, " queryAlarmPowerRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryPowerAlarmValue(mac);
        }
    }

    @JavascriptInterface
    public void queryTemperatureUnitRequest(String mac) {
        Tlog.v(TAG, " queryTemperatureUnitRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryTemperatureUnit(mac);
        }
    }

    @JavascriptInterface
    public void queryMonetarytUnitRequest(String mac) {
        Tlog.v(TAG, " queryMonetarytUnitRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryMonetaryUnit(mac);
        }
    }

    @JavascriptInterface
    public void queryLocalElectricityRequest(String mac) {
        Tlog.v(TAG, " queryLocalElectricityRequest mac:" + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryElectricityPrices(mac);
        }
    }

    /***********/

    @JavascriptInterface
    public void systemLanguageRequest() {
        Tlog.v(TAG, " systemLanguageRequest ");
        if (mCallBack != null) {
            mCallBack.onJSLRequestSystemLanguage();
        }
    }

    @JavascriptInterface
    public void setSystemLanguageRequest(String lan) {
        Tlog.v(TAG, " setSystemLanguageRequest " + lan);
        if (mCallBack != null) {
            mCallBack.onJSLSetLanguage(lan);
        }
    }

    //    备份时间目录查询
    @JavascriptInterface
    public void BackupTimeAndDirectoryRequest(String mac) {
        Tlog.v(TAG, " BackupTimeAndDirectoryRequest " + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryBackupTimeDirectory(mac);
        }
    }

    //    备份数据到手机
    @JavascriptInterface
    public void BackupDataRequest(String mac, String jsonData) {
        Tlog.v(TAG, " BackupDataRequest " + mac + " jsonData:" + jsonData);
        if (mCallBack != null) {
            mCallBack.onJSSaveBackupData(mac, jsonData);
        }
    }

    //    备份数据到手机返回
    @JavascriptInterface
    public void BackupRecoveryDataRequest(String mac) {
        Tlog.v(TAG, " BackupDataRequest " + mac);
        if (mCallBack != null) {
            mCallBack.onJSRecoveryData(mac);
        }
    }


    @JavascriptInterface
    public void indicatorLightStateRequest(String mac) {
        Tlog.v(TAG, " indicatorLightStateRequest " + mac);
        if (mCallBack != null) {
            mCallBack.onJSQueryIndicatorLightState(mac);
        }
    }

    @JavascriptInterface
    public void indicatorLightSwitchRequest(String mac, boolean on) {
        Tlog.v(TAG, " indicatorLightStateRequest " + mac + " on:" + on);
        if (mCallBack != null) {
            mCallBack.onJSControlIndicatorLightState(mac, on);
        }
    }

}
