package cn.com.startai.socket.sign.scm.util;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/9 0009
 * desc :
 */

public class SocketSecureKey {

    public static class Custom {

        public static final byte CUSTOM_WAN = 0x00;//万总
        public static final byte PRODUCT_TRIGGER_BLE = 0x00; // triggerHomeBle
        public static final byte PRODUCT_TRIGGER_WIFI = 0x02; // triggerHomeWiFi
        public static final byte PRODUCT_SOCKET_PM90 = 0x04;// PM90
        public static final byte PRODUCT_GROWROOMATE = 0x06;// 英国插座
        public static final byte PRODUCT_SOCKET_RPX = 0x08;// RPX
        public static final byte PRODUCT_NB_AIRTEMP = 0x0A;// NB-iot 供暖

        public static final byte CUSTOM_LI = 0x02;// 李总
        public static final byte PRODUCT_PASS_THROUGH = 0x00;

        public static final byte CUSTOM_STARTAI = 0x08; // startai
        public static final byte PRODUCT_MUSIK = 0x08; //musik
    }


    public static class Type {

        /**
         * error
         */
        public static final byte TYPE_ERROR = 0x00;

        /**
         * 系统
         */
        public static final byte TYPE_SYSTEM = 0x01;

        /**
         * 控制
         */
        public static final byte TYPE_CONTROLLER = 0x02;

        /**
         * 上报
         */
        public static final byte TYPE_REPORT = 0x03;

        /**
         * 设置
         */
        public static final byte TYPE_SETTING = 0x04;

    }

    public static class Cmd {


        /**********0x01system**************/
        /**
         * 错误
         */
        public static final byte CMD_ERROR = 0x00;

        /**
         * TEST
         */
        public static final byte CMD_TEST = 0x7F;


        /**
         * 心跳
         */
        public static final byte CMD_HEARTBEAT = 0x01;
        public static final byte CMD_HEARTBEAT_RESPONSE = 0x02;

        /**
         * 发现设备
         */
        public static final byte CMD_DISCOVERY_DEVICE = 0x03;
        public static final byte CMD_DISCOVERY_DEVICE_RESPONSE = 0x04;

        /**
         * 绑定设备
         */
        public static final byte CMD_BIND_DEVICE = 0x05;
        public static final byte CMD_BIND_DEVICE_RESPONSE = 0x06;

        /**
         * 升级
         */
        public static final byte CMD_UPDATE = 0x09;
        public static final byte CMD_UPDATE_RESPONSE = 0x0A;

        /**
         * rename
         */
        public static final byte CMD_RENAME = 0x0B;
        public static final byte CMD_RENAME_RESPONSE = 0x0C;

        // 蓝牙插座还在用这两条协议，其他插座移到 type=setting里去了.
        public static final byte CMD_SET_UNIT_TEMPERATURE_BLE = 0x1B;
        public static final byte CMD_SET_UNIT_TEMPERATURE_RESPONSE_BLE = 0x1C;
        public static final byte CMD_QUERY_UNIT_TEMPERATURE_BLE = 0x1D;
        public static final byte CMD_QUERY_UNIT_TEMPERATURE_RESPONSE_BLE = 0x1E;

        /**
         * 查询name
         */
        public static final byte CMD_QUERY_NAME = 0x0D;
        public static final byte CMD_QUERY_NAME_RESPONSE = 0x0E;

        public static final byte CMD_SET_RECOVERY_SCM = 0x27;
        public static final byte CMD_SET_RECOVERY_SCM_RESPONSE = 0x28;

        //帮助scm注册
        public static final byte CMD_REGISTER_SCM = 0x2B;
        public static final byte CMD_REGISTER_SCM_RESPONSE = 0x2C;

        public static final byte CMD_REQUEST_TOKEN = 0x2D;
        public static final byte CMD_REQUEST_TOKEN_RESPONSE = 0x2E;

        public static final byte CMD_CONTROL_TOKEN = 0x2F;
        public static final byte CMD_CONTROL_TOKEN_RESPONSE = 0x30;

        public static final byte CMD_SLEEP_TOKEN = 0x31;
        public static final byte CMD_SLEEP_TOKEN_RESPONSE = 0x32;

        public static final byte CMD_DISCONTROL_TOKEN = 0x33;
        public static final byte CMD_DISCONTROL_TOKEN_RESPONSE = 0x34;

        public static final byte CMD_QUICK_CONTROL_SWITCH = 0x35;
        public static final byte CMD_WUICK_CONTROL_SWITCH_RESPONSE = 0x36;

        public static final byte CMD_QUICK_QUERY_SWITCH = 0x37;
        public static final byte CMD_WUICK_QUERY_SWITCH_RESPONSE = 0x38;

        public static final byte CMD_SET_TIMEZONE = 0x39;
        public static final byte CMD_SET_TIMEZONE_RESPONSE = 0x3A;

        public static final byte CMD_QUERY_TIMEZONE = 0x3B;
        public static final byte CMD_QUERY_TIMEZONE_RESPONSE = 0x3C;

        public static final byte CMD_QUERY_SSID = 0x3D;
        public static final byte CMD_QUERY_SSID_RESPONSE = 0x3E;


        /**********0x02control**************/


        /**
         * 设置继电器
         */
        public static final byte CMD_SET_RELAY_SWITCH = 0x01;
        public static final byte CMD_SET_RELAY_SWITCH_RESPONSE = 0x02;

        /**
         * 设置时间
         */
        public static final byte CMD_SET_TIME = 0x03;
        public static final byte CMD_SET_TIME_RESPONSE = 0x04;

        /**
         * 设置定时
         */
        public static final byte CMD_SET_TIMING = 0x05;
        public static final byte CMD_SET_TIMING_RESPONSE = 0x06;


        /**
         * 插座倒计时
         */
        public static final byte CMD_SET_COUNTDOWN = 0x07;
        public static final byte CMD_SET_COUNTDOWN_RESPONSE = 0x08;

        /**
         * 温度湿度设置
         */
        public static final byte CMD_SET_ALARM = 0x09;
        public static final byte CMD_SET_ALARM_RESPONSE = 0x0A;


        /**
         * 查询继电器
         */
        public static final byte CMD_QUERY_RELAY_STATUS = 0x0B;
        public static final byte CMD_QUERY_RELAY_STATUS_RESPONSE = 0x0C;


        /**
         * 查询倒计时
         */
        public static final byte CMD_QUERY_COUNTDOWN_DATA = 0x0d;
        public static final byte CMD_QUERY_COUNTDOWN_DATA_RESPONSE = 0x0E;

        /**
         * 查询时间
         */
        public static final byte CMD_QUERY_TIME = 0x0F;
        public static final byte CMD_QUERY_TIME_RESPONSE = 0x10;

        /**
         * 查询温度湿度
         */
        public static final byte CMD_QUERY_TEMPERATURE_HUMIDITY_DATA = 0x11;
        public static final byte CMD_QUERY_TEMPERATURE_HUMIDITY_DATA_RESPONSE = 0x12;


        /**
         * 查询插座定时
         */
        public static final byte CMD_QUERY_TIMING_LIST_DATA = 0x13;
        public static final byte CMD_QUERY_TIMING_LIST_DATA_RESPONSE = 0x14;

        /**
         * 设置定时花费
         */
        public static final byte CMD_SET_SPENDING_ELECTRICITY_DATA = 0x15;
        public static final byte CMD_SET_SPENDING_ELECTRICITY_DATA_RESPONSE = 0x16;
        /**
         * 查询定时花费
         */
        public static final byte CMD_QUERY_SPENDING_ELECTRICITY_DATA = 0x17;
        public static final byte CMD_QUERY_SPENDING_ELECTRICITY_DATA_RESPONSE = 0x18;

        /**
         * 查询统计数据
         */
        public static final byte CMD_QUERY_HISTORY_COUNT = 0x19;
        public static final byte CMD_QUERY_HISTORY_COUNT_RESPONSE = 0x1A;

        /**
         * 设置费率(PM60项目定义)
         */
        public static final byte CMD_SET_COST_RATE = 0x1D;
        public static final byte CMD_SET_COST_RATE_RESPONSE = 0x1E;

        /**
         * 查询费率
         */
        public static final byte CMD_QUERY_COST_RATE = 0x1F;
        public static final byte CMD_QUERY_COST_RATE_RESPONSE = 0x20;

        /**
         * 查询设备积累参数
         */
        public static final byte CMD_QUERY_CUMU_PARAM = 0x21;
        public static final byte CMD_QUERY_CUMU_PARAM_RESPONSE = 0x22;

        /**
         * 查询输出的最大值
         */
        public static final byte CMD_QUERY_MAX_OUTPUT = 0x23;
        public static final byte CMD_QUERY_MAX_OUTPUT_RESPONSE = 0x24;

        /**
         * 设置灯的颜色
         */
        public static final byte CMD_SET_LIGHT_COLOR = 0x25;
        public static final byte CMD_SET_LIGHT_COLOR_RESPONSE = 0x26;

        /**
         * 查询灯的颜色
         */
        public static final byte CMD_QUERY_LIGHT_COLOR = 0x29;
        public static final byte CMD_QUERY_LIGHT_COLOR_RESPONSE = 0x2A;


        /**
         * 温度湿度进阶模式设置
         */
        public static final byte CMD_SET_TEMP_HUMI_ALARM = 0x2B;
        public static final byte CMD_SET_TEMP_HUMI_ALARM_RESPONSE = 0x2C;

        /**
         * 温度湿度进阶模式查询
         */
        public static final byte CMD_QUERY_TEMP_HUMI_ALARM = 0x2D;
        public static final byte CMD_QUERY_TEMP_HUMI_ALARM_RESPONSE = 0x2E;


        /**
         * 设置小夜灯模式
         */
        public static final byte CMD_SET_NIGHT_LIGHT = 0x35;
        public static final byte CMD_SET_NIGHT_LIGHT_RESPONSE = 0x36;

        /**
         * 查询小夜灯模式
         */
        public static final byte CMD_QUERY_NIGHT_LIGHT = 0x37;
        public static final byte CMD_QUERY_NIGHT_LIGHT_RESPONSE = 0x38;

        /**
         * 查询温度传感器是否正常工作
         */
        public static final byte CMD_QUERY_TEMP_SENSOR_STATUS = 0x39;
        public static final byte CMD_QUERY_TEMP_SENSOR_STATUS_RESPONSE = 0x3A;

        /**
         * 一键控制所有闪光灯
         */
        public static final byte CMD_CONTROL_ANYNET_FLASH = 0x3B;
        public static final byte CMD_CONTROL_ANYNET_FLASH_RESPONSE = 0x3C;

        /**
         * 查询一键控制所有闪光灯
         */
        public static final byte CMD_QUERY_ANYNET_FLASH = 0x3D;
        public static final byte CMD_QUERY_ANYNET_FLASH_RESPONSE = 0x3E;

        /**
         * 查询状态机状态
         */
        public static final byte CMD_QUERY_STATE_MACHINE = 0x3F;
        public static final byte CMD_QUERY_STATE_MACHINE_RESPONSE = 0x40;


        /**********report0x03**************/

        /**
         * 温度湿度上报
         */
        public static final byte CMD_TEMP_HUMI_REPORT = 0x01;
        public static final byte CMD_TEMP_HUMI_REPORT_RESPONSE = 0x02;

        /**
         * 电压电流 功率 频率上报
         */
        public static final byte CMD_POWER_FREQ_REPORT = 0x03;
        public static final byte CMD_POWER_FREQ_REPORT_RESPONSE = 0x04;


        /**
         * 温度湿度上报
         */
        public static final byte CMD_TEMPERATURE_HUMIDITY_REPORT = 0x05;
        public static final byte CMD_TEMPERATURE_HUMIDITY_REPORT_RESPONSE = 0x06;


        /**
         * 倒计时上报
         */
        public static final byte CMD_COUNTDOWN_REPORT = 0x07;
        public static final byte CMD_COUNTDOWN_REPORT_RESPONSE = 0x08;

        /**
         * 定时上报
         */
        public static final byte CMD_TIMING_REPORT = 0x09;
        public static final byte CMD_TIMING_REPORT_RESPONSE = 0x0A;

        /**
         * 五分钟上报一次的数据
         */
        public static final byte CMD_ELECTRICITY_REPORT = 0x0B;
        public static final byte CMD_ELECTRICITY_REPORT_RESPONSE = 0x0C;

        /**
         * 温度传感器上报
         */
        public static final byte CMD_TEMP_SENSOR_REPORT = 0x11;
        public static final byte CMD_TEMP_SENSOR_RESPONSE = 0x12;

        /**
         * 状态上报
         */
        public static final byte CMD_STATE_MACHINE_REPORT = 0x13;
        public static final byte CMD_STATE_MACHINE_RESPONSE = 0x14;

        /**********Setting0x04**************/

        public static final byte CMD_SET_VOLTAGE_ALARM_VALUE = 0x0F;
        public static final byte CMD_SET_VOLTAGE_ALARM_VALUE_RESPONSE = 0x10;
        public static final byte CMD_QUERY_VOLTAGE_ALARM_VALUE = 0x11;
        public static final byte CMD_QUERY_VOLTAGE_ALARM_VALUE_RESPONSE = 0x12;

        public static final byte CMD_SET_CURRENT_ALARM_VALUE = 0x13;
        public static final byte CMD_SET_CURRENT_ALARM_VALUE_RESPONSE = 0x14;
        public static final byte CMD_QUERY_CURRENT_ALARM_VALUE = 0x15;
        public static final byte CMD_QUERY_CURRENT_ALARM_VALUE_RESPONSE = 0x16;

        public static final byte CMD_SET_POWER_ALARM_VALUE = 0x17;
        public static final byte CMD_SET_POWER_ALARM_VALUE_RESPONSE = 0x18;
        public static final byte CMD_QUERY_POWER_ALARM_VALUE = 0x19;
        public static final byte CMD_QUERY_POWER_ALARM_VALUE_RESPONSE = 0x1A;

        public static final byte CMD_SET_UNIT_TEMPERATURE = 0x1B;
        public static final byte CMD_SET_UNIT_TEMPERATURE_RESPONSE = 0x1C;
        public static final byte CMD_QUERY_UNIT_TEMPERATURE = 0x1D;
        public static final byte CMD_QUERY_UNIT_TEMPERATURE_RESPONSE = 0x1E;

        public static final byte CMD_SET_UNIT_MONETARY = 0x1F;
        public static final byte CMD_SET_UNIT_MONETARY_RESPONSE = 0x20;
        public static final byte CMD_QUERY_UNIT_MONETARY = 0x21;
        public static final byte CMD_QUERY_UNIT_MONETARY_RESPONSE = 0x22;

        public static final byte CMD_SET_PRICES_ELECTRICITY = 0x23;
        public static final byte CMD_SET_PRICES_ELECTRICITY_RESPONSE = 0x24;
        public static final byte CMD_QUERY_PRICES_ELECTRICITY = 0x25;
        public static final byte CMD_QUERY_PRICES_ELECTRICITY_RESPONSE = 0x26;


    }

    public static class Model {

        /**
         * 小夜灯,智能
         */
        public static final byte MODEL_NIGHT_LIGHT_WISDOM = 0x01;

        /**
         * 小夜灯,定时
         */
        public static final byte MODEL_NIGHT_LIGHT_TIMING = 0x02;

        /**
         * 小夜灯,正在运行
         */
        public static final byte MODEL_NIGHT_LIGHT_RUNNING = (byte) 0xFF;


        /**
         * 彩灯
         */
        public static final byte MODEL_COLOR_LAMP = 0x01;

        /**
         * 黄灯
         */
        public static final byte MODEL_YELLOW_LIGHT = 0x02;


        /**
         * 成功
         */
        public static final byte MODEL_RESULT_SUCCESS = 0x00;

        /**
         * 失败
         */
        public static final byte MODEL_RESULT_FAIL = 0x01;

        /**
         * 失败未绑定
         */
        public static final byte MODEL_RESULT_UNBIND = 0x02;

        /**
         * token失效
         */
        public static final byte MODEL_RESULT_TOKEN_INVALID = 0x03;

        /**
         * 开
         */
        public static final byte MODEL_SWITCH_ON = 0x01;
        /**
         * 关
         */
        public static final byte MODEL_SWITCH_OFF = 0x00;


        /**
         * 启动
         */
        public static final byte MODEL_START_UP = 0x01;
        /**
         * 结束
         */
        public static final byte MODEL_FINISH = 0x02;

        /**
         * 正常
         */
        public static final byte MODEL_RUNNING = 0x01;

        /**
         * 不正常
         */
        public static final byte MODEL_ERROR = 0x02;

        /**
         * true
         */
        public static final byte MODEL_TRUE = 0x01;


        /**
         * false
         */
        public static final byte MODEL_FALSE = 0x00;


        /**
         * 继电器
         */
        public static final byte MODEL_RELAY = 0x01;

        /**
         * 背光开关
         */
        public static final byte MODEL_BACKLIGHT = 0x02;

        /**
         * 闪光开关
         */
        public static final byte MODEL_FLASHLIGHT = 0x03;

        /**
         * usb开关
         */
        public static final byte MODEL_USB = 0x04;

        /**
         * 睡眠灯
         */
        public static final byte MODEL_NIGHT_LIGHT = 0x05;

        /**
         * 定温度
         */
        public static final byte ALARM_MODEL_TEMPERATURE = 0x01;
        /**
         * 定湿度
         */
        public static final byte ALARM_MODEL_HUMIDITY = 0x02;

        /**
         * 报警上限(加热)
         */
        public static final byte ALARM_LIMIT_UP = 0x01;
        /**
         * 报警下限
         */
        public static final byte ALARM_LIMIT_DOWN = 0x02;

        /**
         * 定时普通
         */
        public static final byte TIMING_COMMON = 0x01;

        /**
         * 定时高级
         */
        public static final byte TIMING_ADVANCE = 0x02;

        public static final byte DISCOVERY_MODEL_BLE = 0x01;
        public static final byte DISCOVERY_MODEL_WIFI = 0x02;

        /**
         * 定电量
         */
        public static final byte SPENDING_ELECTRICITY_E = 0x01;
        /**
         * 定花费
         */
        public static final byte SPENDING_ELECTRICITY_S = 0x02;


        /**
         * 升级
         */
        public static final byte MODEL_UPDATE = 0x01;
        /**
         * 查询版本
         */
        public static final byte MODEL_QUERY_VERSION = 0x02;
        /**
         * 强制升级
         */
        public static final byte MODEL_FORCE_UPDATE = 0x03;


        /**
         * 五分钟
         */
        public static final byte MODEL_INTERVAL_MINUTE = 0x01;
        /**
         * 小时
         */
        public static final byte MODEL_INTERVAL_HOUR = 0x02;
        /**
         * 天
         */
        public static final byte MODEL_INTERVAL_DAY = 0x03;
        /**
         * 周
         */
        public static final byte MODEL_INTERVAL_WEEK = 0x04;
        /**
         * 月
         */
        public static final byte MODEL_INTERVAL_MONTH = 0x05;
        /**
         * 年
         */
        public static final byte MODEL_INTERVAL_YEAR = 0x06;


        /**
         * 温度传感器
         */
        public static final byte MODEL_TEMP_SENSOR = 0x01;

    }

    public static class Util {

        public static boolean isTempSensor(byte sensor) {
            return sensor == Model.MODEL_TEMP_SENSOR;
        }

        public static boolean isIntervalMinute(byte interval) {
            return interval == Model.MODEL_INTERVAL_MINUTE;
        }

        public static boolean isIntervalHour(byte interval) {
            return interval == Model.MODEL_INTERVAL_HOUR;
        }

        public static boolean isIntervalDay(byte interval) {
            return interval == Model.MODEL_INTERVAL_DAY;
        }

        public static boolean isIntervalWeek(byte interval) {
            return interval == Model.MODEL_INTERVAL_WEEK;
        }

        public static boolean isIntervalMonth(byte interval) {
            return interval == Model.MODEL_INTERVAL_MONTH;
        }

        public static boolean isIntervalYear(byte interval) {
            return interval == Model.MODEL_INTERVAL_YEAR;
        }

        public static boolean isTrue(byte flag) {
            return (flag == Model.MODEL_TRUE);
        }

        public static boolean resultIsOk(byte result) {

            return (result == Model.MODEL_RESULT_SUCCESS);
        }

        public static boolean isRunning(byte flag) {
            return (flag == Model.MODEL_RUNNING);
        }

        public static boolean resultIsUnbind(byte result) {

            return (result == Model.MODEL_RESULT_UNBIND);
        }

        public static boolean resultTokenInvalid(byte result) {

            return (result == Model.MODEL_RESULT_TOKEN_INVALID);
        }


        public static byte resultSuccess(boolean success) {
            return (success ? Model.MODEL_RESULT_SUCCESS : Model.MODEL_RESULT_FAIL);
        }

        public static boolean startup(byte param) {

            return (param == Model.MODEL_START_UP);
        }

        public static byte startup(boolean startup) {
            return (startup ? Model.MODEL_START_UP : Model.MODEL_FINISH);
        }

        public static boolean on(byte param) {

            return (param == Model.MODEL_SWITCH_ON);
        }

        public static byte on(boolean on) {
            return (on ? Model.MODEL_SWITCH_ON : Model.MODEL_SWITCH_OFF);
        }


        public static boolean isBleProduct(byte product) {
            return (product == Custom.PRODUCT_TRIGGER_BLE);
        }

        public static byte getCommonTiming() {
            return Model.TIMING_COMMON;
        }

        public static byte getAdvanceTiming() {
            return Model.TIMING_ADVANCE;
        }

        public static boolean isCommonTiming(byte model) {
            return (model == Model.TIMING_COMMON);
        }

        public static boolean isAdvanceTiming(byte model) {
            return (model == Model.TIMING_ADVANCE);
        }

        public static boolean isTemperature(byte model) {
            return (model == Model.ALARM_MODEL_TEMPERATURE);
        }

        public static byte getTemperature() {
            return Model.ALARM_MODEL_TEMPERATURE;
        }

        public static boolean isHumidity(byte model) {
            return (model == Model.ALARM_MODEL_HUMIDITY);
        }

        public static byte getHumidity() {
            return Model.ALARM_MODEL_TEMPERATURE;
        }

        public static boolean isLimitUp(byte limit) {
            return (limit == Model.ALARM_LIMIT_UP);
        }

        public static boolean isLimitDown(byte limit) {
            return (limit == Model.ALARM_LIMIT_DOWN);
        }

        public static byte limitUp(boolean limitUp) {
            return (limitUp ? Model.ALARM_LIMIT_UP : Model.ALARM_LIMIT_DOWN);
        }

        public static boolean isBleModel(byte model) {
            return (model == Model.DISCOVERY_MODEL_BLE);
        }

        public static boolean isWiFiModel(byte model) {
            return (model == Model.DISCOVERY_MODEL_WIFI);
        }

        public static boolean isElectricity(byte model) {
            return (model == Model.SPENDING_ELECTRICITY_E);
        }


        public static boolean isRelayModel(byte model) {
            return (Model.MODEL_RELAY == model);
        }

        public static boolean isBackLightModel(byte model) {
            return (Model.MODEL_BACKLIGHT == model);
        }

        public static boolean isFlashLightModel(byte model) {
            return (Model.MODEL_FLASHLIGHT == model);
        }

        public static boolean isUSBModel(byte model) {
            return (Model.MODEL_USB == model);
        }

        public static boolean isNightLight(byte model) {
            return (Model.MODEL_NIGHT_LIGHT == model);
        }

        public static boolean isQueryVersionAction(byte action) {
            return (Model.MODEL_QUERY_VERSION == action);
        }

        public static boolean isUpdateModel(byte action) {
            return (Model.MODEL_UPDATE == action) || (Model.MODEL_FORCE_UPDATE == action);
        }

        public static boolean isUpdateModelOnly(byte action) {
            return (Model.MODEL_UPDATE == action);
        }

    }

}
