package cn.com.startai.socket.sign.scm.util;

import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/13 0013
 * desc :
 */
public class MySocketSecureKey extends SocketSecureKey {

    public static class MCustom {
        public static final byte PRODUCT_SMART_SOCKET = 0x06;
    }

    public static class MCmd {

        /******0x02control*/

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


        /******0x03report*/
        /**
         * 五分钟上报一次的数据
         */
        public static final byte CMD_ELECTRICITY_REPORT = 0x0B;
        public static final byte CMD_ELECTRICITY_REPORT_RESPONSE = 0x0C;

    }

    public static class MModel {

        /**
         * 继电器开关
         */
        public static final byte MODEL_RELAY = 0x01;

        /**
         * 背光开关
         */
        public static final byte MODEL_BACKLIGHT = 0x02;
    }

    public static class MUtil {

        public static boolean isRelayModel(byte model) {
            return (MModel.MODEL_RELAY == model);
        }

        public static boolean isBackLightModel(byte model) {
            return (MModel.MODEL_BACKLIGHT == model);
        }

    }


}
