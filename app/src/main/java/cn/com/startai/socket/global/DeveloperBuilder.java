package cn.com.startai.socket.global;

import cn.com.startai.mqttsdk.mqtt.MqttInitParam;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/9 0009
 * desc :
 */
public class DeveloperBuilder {


    /**
     * Ble插座-单片机的开发者信息
     */
    public static final class BleSocketScmDeveloper extends DeveloperInfo {

        public static final String domain = "okaylight"; //开发者平台获取
        public static final String m_ver = "Json_1.2.4_9.2.1";//文档约定
        public static final String appid = "294dd313cd74dafae8e2199d4eb93616";//开发者平台获取
        public static final String apptype = "smartOlBle/controlled/nonos";//开发者平台获取


        @Override
        public String setDomain(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setVersion(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setAppid(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setApptype(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String getDomain() {
            return domain;
        }

        @Override
        public String getVersion() {
            return m_ver;
        }

        @Override
        public String getAppid() {
            return appid;
        }

        @Override
        public String getApptype() {
            return apptype;
        }
    }


    /**
     * WIFI插座android开发者信息
     */
    public static final class WiFiSocketDeveloper extends DeveloperInfo {

        public static final String domain = "okaylight"; //开发者平台获取
        public static final String m_ver = "Json_1.2.4_9.2.1";//文档约定
        public static final String appid = "f818c2704026de3c35c5aee06120ff98";//开发者平台获取
        public static final String apptype = "smartOlWifi/controll/android";//开发者平台获取


        @Override
        public String setDomain(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setVersion(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setAppid(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String setApptype(String s) {
            throw new RuntimeException(" not impl ");
        }

        @Override
        public String getDomain() {
            return domain;
        }

        @Override
        public String getVersion() {
            return m_ver;
        }

        @Override
        public String getAppid() {
            return appid;
        }

        @Override
        public String getApptype() {
            return apptype;
        }
    }


    public static MqttInitParam buildMqttInitParam(DeveloperInfo mDeveloperInfo) {
        return new MqttInitParam(mDeveloperInfo.getDomain(), mDeveloperInfo.getApptype(), mDeveloperInfo.getAppid(), mDeveloperInfo.getVersion());
    }


}
