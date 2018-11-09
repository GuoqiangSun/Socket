package cn.com.startai.socket.mutual.js.bean.WiFiDevice;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/6 0006
 * desc :
 */

@Entity
public class LanDeviceInfo implements Cloneable {

    @Id(autoincrement = true)
    private Long id;

    public int model; // ble wifi
    public int mainVersion; // 主版本号
    public int subVersion; // 次版本号
    public boolean hasAdmin;// 有管理员
    public boolean isAdmin; //是否是管理员
    public boolean hasRemote;//是否已经远程连接
    public boolean bindNeedPwd;// 绑定需要密码
    public boolean hasActivate;// 已经激活

    //    public boolean isBind; // 是否已经绑定
    public boolean isLanBind;//是否已经绑定
    public boolean isWanBind;//是否已经绑定

    public boolean state = true;// 连接的状态;是否在线

    public String deviceID; // 服务器唯一标示符
    public String mac;// mac 和js通信的唯一标示
    public String name;

    //
    public String ip; // 局域网内ip
    public int port; // 局域网内port

    public String ssid;
    public int rssi; // rssi;

    public boolean relayState; // 继电器的状态

    public String cpuInfo; // 设备cpuInfo

    @Override
    public String toString() {
        return "LanDeviceInfo{" +
                "id=" + id +
                ", model=" + model +
                ", mainVersion=" + mainVersion +
                ", subVersion=" + subVersion +
                ", hasAdmin=" + hasAdmin +
                ", isAdmin=" + isAdmin +
                ", hasRemote=" + hasRemote +
                ", bindNeedPwd=" + bindNeedPwd +
                ", hasActivate=" + hasActivate +
                ", isLanBind=" + isLanBind +
                ", isWanBind=" + isWanBind +
                ", state=" + state +
                ", deviceID='" + deviceID + '\'' +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                ", relayState=" + relayState +
                ", cpuInfo='" + cpuInfo + '\'' +
                '}';
    }


    @Generated(hash = 1914885960)
    public LanDeviceInfo(Long id, int model, int mainVersion, int subVersion,
                         boolean hasAdmin, boolean isAdmin, boolean hasRemote,
                         boolean bindNeedPwd, boolean hasActivate, boolean isLanBind,
                         boolean isWanBind, boolean state, String deviceID, String mac,
                         String name, String ip, int port, String ssid, int rssi,
                         boolean relayState, String cpuInfo) {
        this.id = id;
        this.model = model;
        this.mainVersion = mainVersion;
        this.subVersion = subVersion;
        this.hasAdmin = hasAdmin;
        this.isAdmin = isAdmin;
        this.hasRemote = hasRemote;
        this.bindNeedPwd = bindNeedPwd;
        this.hasActivate = hasActivate;
        this.isLanBind = isLanBind;
        this.isWanBind = isWanBind;
        this.state = state;
        this.deviceID = deviceID;
        this.mac = mac;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.ssid = ssid;
        this.rssi = rssi;
        this.relayState = relayState;
        this.cpuInfo = cpuInfo;
    }

    @Generated(hash = 126986990)
    public LanDeviceInfo() {
    }

    public LanDeviceInfo copy(LanDeviceInfo mWiFiDevice) {

        this.model = mWiFiDevice.model;
        this.mainVersion = mWiFiDevice.mainVersion;
        this.subVersion = mWiFiDevice.subVersion;
        this.hasAdmin = mWiFiDevice.hasAdmin;
        this.isAdmin = mWiFiDevice.isAdmin; //是否是管理员
        this.hasRemote = mWiFiDevice.hasRemote;
        this.bindNeedPwd = mWiFiDevice.bindNeedPwd;
        this.hasActivate = mWiFiDevice.hasActivate;

//        this.isBind = mWiFiDevice.isBind;
        this.isLanBind = mWiFiDevice.isLanBind;
        this.isWanBind = mWiFiDevice.isWanBind;

        this.state = mWiFiDevice.state;

        this.deviceID = mWiFiDevice.deviceID;
        this.mac = mWiFiDevice.mac;
        this.name = mWiFiDevice.name;

        this.ip = mWiFiDevice.ip;
        this.port = mWiFiDevice.port;
        this.relayState = mWiFiDevice.relayState;

        this.ssid = mWiFiDevice.ssid;
        this.rssi = mWiFiDevice.rssi;
        return this;
    }


    @Override
    protected LanDeviceInfo clone() {
        LanDeviceInfo clone = null;
        try {
            clone = (LanDeviceInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public JSONObject toJsonObj() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("ip", ip);
            obj.put("name", name);
            obj.put("state", state);
            obj.put("switch", relayState);
            obj.put("mac", mac);
            obj.put("encrypted", bindNeedPwd);
            obj.put("isBinding", isWanBind);
            obj.put("version", getVersion());
            JSONObject mWiFiInfo = new JSONObject();
            mWiFiInfo.put("ssid", ssid);
            mWiFiInfo.put("strength", rssi);

            obj.put("wifiInfomation", mWiFiInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public int getVersion() {
        return ((mainVersion << 8) & 0xFF) | (subVersion & 0xFF);
    }


    public static LanDeviceInfo fromJson(String deviceJson) {
        LanDeviceInfo mWiFiDevice = new LanDeviceInfo();

        try {
            JSONObject obj = new JSONObject(deviceJson);
            mWiFiDevice.name = obj.optString("name");
            mWiFiDevice.ip = obj.optString("ip");
            mWiFiDevice.mac = obj.optString("mac");
            mWiFiDevice.relayState = obj.optBoolean("state");
            mWiFiDevice.bindNeedPwd = obj.getBoolean("encrypted");
            mWiFiDevice.isLanBind = obj.getBoolean("isBinding");

            JSONObject wifiInfomation = (JSONObject) obj.opt("wifiInfomation");
            mWiFiDevice.ssid = wifiInfomation.optString("ssid");
            mWiFiDevice.rssi = wifiInfomation.optInt("strength");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mWiFiDevice;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getModel() {
        return this.model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getMainVersion() {
        return this.mainVersion;
    }

    public void setMainVersion(int mainVersion) {
        this.mainVersion = mainVersion;
    }

    public int getSubVersion() {
        return this.subVersion;
    }

    public void setSubVersion(int subVersion) {
        this.subVersion = subVersion;
    }

    public boolean getHasAdmin() {
        return this.hasAdmin;
    }

    public void setHasAdmin(boolean hasAdmin) {
        this.hasAdmin = hasAdmin;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getHasRemote() {
        return this.hasRemote;
    }

    public void setHasRemote(boolean hasRemote) {
        this.hasRemote = hasRemote;
    }

    public boolean getBindNeedPwd() {
        return this.bindNeedPwd;
    }

    public void setBindNeedPwd(boolean bindNeedPwd) {
        this.bindNeedPwd = bindNeedPwd;
    }

    public boolean getHasActivate() {
        return this.hasActivate;
    }

    public void setHasActivate(boolean hasActivate) {
        this.hasActivate = hasActivate;
    }


    public boolean getIsLanBind() {
        return this.isLanBind;
    }

    public void setIsLanBind(boolean isLanBind) {
        this.isLanBind = isLanBind;
    }

    public boolean getIsWanBind() {
        return this.isWanBind;
    }

    public void setIsWanBind(boolean isWanBind) {
        this.isWanBind = isWanBind;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void checkName() {
        if (this.name == null) {
            String tMac = this.mac;
            if (tMac != null) {
                tMac = tMac.replace(":", "");
                if (tMac.length() > 6) {
                    tMac = "socket" + tMac.substring(6);
                }
            }
            setName(tMac);
        }
    }

    public boolean getState() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getRelayState() {
        return this.relayState;
    }

    public void setRelayState(boolean relayState) {
        this.relayState = relayState;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSsid() {
        return this.ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getCpuInfo() {
        return this.cpuInfo;
    }


    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

}
