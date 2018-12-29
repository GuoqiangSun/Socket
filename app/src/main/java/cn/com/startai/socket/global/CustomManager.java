package cn.com.startai.socket.global;

import android.app.Application;

import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.baselib.app.IApp.IApp;
import cn.com.swain.support.protocolEngine.ProtocolBuild;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/4 0004
 * desc :
 */
public class CustomManager implements IApp {

    private CustomManager() {
    }

    private static final class ClassHolder {
        private static final CustomManager FLAVORS = new CustomManager();
    }

    public static CustomManager getInstance() {
        return ClassHolder.FLAVORS;
    }

    private boolean isMUSIKProject = false;
    private boolean isGrowroomateProject = false;
    private boolean isTriggerWiFiProject = false;
    private boolean isTriggerBleProject = false;
    private boolean isAirtempNBProject = false;

    private boolean isTestSocketProject = false;

    public boolean isMUSIK() {
        return isMUSIKProject;
    }

    public boolean isGrowroomate() {
        return isGrowroomateProject;
    }

    public boolean isTriggerWiFi() {
        return isTriggerWiFiProject;
    }

    public boolean isTriggerBle() {
        return isTriggerBleProject;
    }

    public boolean isAirtempNBProject() {
        return isAirtempNBProject;
    }

    public boolean isAirtempNBProjectTest() {
        return isAirtempNBProject;
    }

    public boolean isTestProject() {
        return isTestSocketProject;
    }


    private volatile byte CUSTOM;
    private volatile byte PRODUCT;
    private volatile byte PROTOCOL_VERSION;

    public byte getCustom() {
        return CUSTOM;
    }

    public byte getProduct() {
        return PRODUCT;
    }

    public byte getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public void initAirtempNBProject() {
        Tlog.i(" is airtempNB socket project ");
        isAirtempNBProject = true;
    }

    public void initTestSocketProject() {
        Tlog.i(" is Test socket project ");
        this.isTestSocketProject = true;
    }

    public void initTriggerBleProject() {
        Tlog.i(" is Trigger Ble project ");
        this.isTriggerBleProject = true;
    }

    public void initTriggerWiFiProject() {
        Tlog.i(" is Trigger WiFi project ");
        this.isTriggerWiFiProject = true;
    }

    public void initSmartPlugProject() {
        Tlog.i(" is smart plug project ");
        this.isGrowroomateProject = true;
    }

    public void initMUSIKProject() {
        Tlog.i(" is startAI musik project ");
        this.isMUSIKProject = true;
    }


    @Override
    public void init(Application app) {
        Tlog.i("CustomManager init : ");

        if (isTriggerBle()) {
            CUSTOM = SocketSecureKey.Custom.CUSTOM_WAN;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_TRIGGER_BLE;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_0;
        } else if (isTriggerWiFi()) {
            CUSTOM = SocketSecureKey.Custom.CUSTOM_WAN;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_TRIGGER_WIFI;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_SEQ;
        } else if (isGrowroomate()) {
            CUSTOM = SocketSecureKey.Custom.CUSTOM_WAN;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_GROWROOMATE;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_SEQ;
        } else if (isMUSIK()) {

            CUSTOM = SocketSecureKey.Custom.CUSTOM_STARTAI;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_MUSIK;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_SEQ;

        } else if (isAirtempNBProject()) {

            CUSTOM = SocketSecureKey.Custom.CUSTOM_WAN;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_NB_AIRTEMP;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_SEQ;

        } else {
            CUSTOM = SocketSecureKey.Custom.CUSTOM_STARTAI;
            PRODUCT = SocketSecureKey.Custom.PRODUCT_MUSIK;
            PROTOCOL_VERSION = ProtocolBuild.VERSION.VERSION_SEQ;
        }

    }


}
