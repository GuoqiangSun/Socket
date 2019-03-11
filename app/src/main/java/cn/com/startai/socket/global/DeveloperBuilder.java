package cn.com.startai.socket.global;

import cn.com.startai.mqttsdk.mqtt.MqttInitParam;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/9 0009
 * desc :
 */
public class DeveloperBuilder {


    public static MqttInitParam getMqttInitParam() {

        MqttInitParam mqttInitParam = null;

        if (CustomManager.getInstance().getCustom() ==
                SocketSecureKey.Custom.CUSTOM_WAN) {

            if (CustomManager.getInstance().getProduct() ==
                    SocketSecureKey.Custom.PRODUCT_GROWROOMATE) {
                mqttInitParam = new DeveloperBuilder.SmartSocketDeveloper();

            } else if (CustomManager.getInstance().getProduct() ==
                    SocketSecureKey.Custom.PRODUCT_TRIGGER_WIFI) {
                mqttInitParam = new DeveloperBuilder.WiFiSocketDeveloper();

            } else if (CustomManager.getInstance().getProduct() ==
                    SocketSecureKey.Custom.PRODUCT_NB_AIRTEMP) {

                mqttInitParam = new DeveloperBuilder.AirtempNBDeveloper();
            }


        } else if (CustomManager.getInstance().getCustom() ==
                SocketSecureKey.Custom.CUSTOM_STARTAI) {

            if (CustomManager.getInstance().getProduct() ==
                    SocketSecureKey.Custom.PRODUCT_MUSIK) {
                mqttInitParam = new DeveloperBuilder.SuperSocketDeveloper();
            }

        }
//        else {
//            mqttInitParam = DeveloperBuilder.buildMqttInitParam(new DeveloperBuilder.SuperSocketDeveloper());
//        }
        return mqttInitParam;
    }


    /**
     * Ble插座-单片机的开发者信息
     */
    public static final class BleSocketScmDeveloper extends MqttInitParam {

        public BleSocketScmDeveloper() {
            super.domain = "okaylight";
            super.m_ver = "Json_1.2.4_9.2.1";
            super.appid = "294dd313cd74dafae8e2199d4eb93616";
            super.apptype = "smartOlBle/controlled/nonos";
        }

    }


    /**
     * WIFI插座android开发者信息
     * <p>
     * 万总的
     */
    public static final class WiFiSocketDeveloper extends MqttInitParam {

        public WiFiSocketDeveloper() {
            super.domain = "okaylight";
            super.m_ver = "Json_1.2.4_9.2.1";
            super.appid = "f818c2704026de3c35c5aee06120ff98";
            super.apptype = "smartOlWifi/controll/android";
        }

    }

    /**
     * 英国插座android开发者信息
     * <p>
     * 万总的
     */
    public static final class SmartSocketDeveloper extends MqttInitParam {

        public SmartSocketDeveloper() {
            super.domain = "okaylight";
            super.m_ver = "Json_1.2.4_9.2.1";
            super.appid = "8040ab3093804dc1a8aeba3e24d3b97c";
            super.apptype = "smartOlWifi/controll/android";
        }

    }


    /**
     * 供暖 android开发者信息
     * <p>
     * 万总的
     */
    public static final class AirtempNBDeveloper extends MqttInitParam {

        public AirtempNBDeveloper() {
            super.domain = "okaylight";
            super.m_ver = "Json_1.2.4_9.2.1";
            super.appid = "qxb050edbace01cbd8";
            super.apptype = "smartOlWifi/controll/android";
        }

    }


    /**
     * Musik插座android开发者信息
     * <p>
     * startai的
     */
    public static final class SuperSocketDeveloper extends MqttInitParam {

        public SuperSocketDeveloper() {
            super.domain = "startai";
            super.m_ver = "Json_1.2.4_9.2.1";
            super.appid = "6e3788eedb60442c88b647bfaa1d285b";
            super.apptype = "smartOlWifi/controll/android";
        }

    }


}
