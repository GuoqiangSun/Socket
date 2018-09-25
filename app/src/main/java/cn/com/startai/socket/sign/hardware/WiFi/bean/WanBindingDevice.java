package cn.com.startai.socket.sign.hardware.WiFi.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import cn.com.startai.mqttsdk.busi.entity.C_0x8002;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/4 0004
 * desc :
 */

@Entity
public class WanBindingDevice {

    @Id(autoincrement = true)
    private Long gid;

    //    apptype	String	对方的类型
    private String apptype;

    private long bindingtime;

    //    connstatus	int	连接状态	1表示在线0表示离线
    private int connstatus;


    //    id	String	 设备的sn或对方userid
    private String oid;

    private int type;

    //    featureid	String	功能id
    private String alias;

    //    topic	String	对端的topic	在点对点发消息时需要携带此参数
    private String topic;

    private String mac;

    @Override
    public String toString() {
        return "WanBindingDevice{" +
                "gid=" + gid +
                ", apptype='" + apptype + '\'' +
                ", bindingtime=" + bindingtime +
                ", connstatus=" + connstatus +
                ", oid='" + oid + '\'' +
                ", type=" + type +
                ", alias='" + alias + '\'' +
                ", topic='" + topic + '\'' +
                ", mac='" + mac + '\'' +
                ", mid='" + mid + '\'' +
                ", isAdmin=" + isAdmin +
                ", hasBindingByWan=" + hasBindingByWan +
                ", hasBindingByLan=" + hasBindingByLan +
                ", cpuInfo='" + cpuInfo + '\'' +
                ", token=" + token +
                ", tokenInsterTimes=" + tokenInsterTimes +
                '}';
    }

    private String mid; // 我的userid

    private boolean isAdmin;// 是否是管理员

    private boolean hasBindingByWan; // 是否已经远程建立了绑定关系

    private boolean hasBindingByLan; // 是否在局域网内建立了绑定关系

    private String cpuInfo; // 设备cpuInfo

    private int token; // 回话token

    private long tokenInsterTimes; // 回话token插入时间


    @Generated(hash = 900102256)
    public WanBindingDevice(Long gid, String apptype, long bindingtime, int connstatus, String oid, int type, String alias,
                            String topic, String mac, String mid, boolean isAdmin, boolean hasBindingByWan, boolean hasBindingByLan,
                            String cpuInfo, int token, long tokenInsterTimes) {
        this.gid = gid;
        this.apptype = apptype;
        this.bindingtime = bindingtime;
        this.connstatus = connstatus;
        this.oid = oid;
        this.type = type;
        this.alias = alias;
        this.topic = topic;
        this.mac = mac;
        this.mid = mid;
        this.isAdmin = isAdmin;
        this.hasBindingByWan = hasBindingByWan;
        this.hasBindingByLan = hasBindingByLan;
        this.cpuInfo = cpuInfo;
        this.token = token;
        this.tokenInsterTimes = tokenInsterTimes;
    }

    @Generated(hash = 1369897669)
    public WanBindingDevice() {
    }


    public static WanBindingDevice memor(C_0x8005.Resp.ContentBean mContentBean) {
        WanBindingDevice tBindingDevice = new WanBindingDevice();
        tBindingDevice.setAlias(mContentBean.getAlias());
        tBindingDevice.setApptype(mContentBean.getApptype());
        tBindingDevice.setBindingtime(mContentBean.getBindingtime());
        tBindingDevice.setConnstatus(mContentBean.getConnstatus());
        tBindingDevice.setOid(mContentBean.getId());
        tBindingDevice.setMac(mContentBean.getMac());
        tBindingDevice.setTopic(mContentBean.getTopic());
        tBindingDevice.setType(mContentBean.getType());
        return tBindingDevice;
    }

    public static WanBindingDevice memor(C_0x8002.Resp.ContentBean.BebindingBean bebinding) {
        WanBindingDevice tBindingDevice = new WanBindingDevice();
        tBindingDevice.setApptype(bebinding.getApptype());
        tBindingDevice.setConnstatus(bebinding.getConnstatus());
        tBindingDevice.setOid(bebinding.getId().trim());
        tBindingDevice.setMac(bebinding.getMac());
        tBindingDevice.setTopic(bebinding.getTopic());
        tBindingDevice.setHasBindingByWan(true);
        return tBindingDevice;
    }


    public Long getGid() {
        return this.gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public String getApptype() {
        return this.apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public long getBindingtime() {
        return this.bindingtime;
    }

    public void setBindingtime(long bindingtime) {
        this.bindingtime = bindingtime;
    }

    public int getConnstatus() {
        return this.connstatus;
    }

    public void setConnstatus(int connstatus) {
        this.connstatus = connstatus;
    }


    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public String getCpuInfo() {
        return this.cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public int getToken() {
        return this.token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public boolean getHasBindingByLan() {
        return this.hasBindingByLan;
    }

    public void setHasBindingByLan(boolean hasBindingByLan) {
        this.hasBindingByLan = hasBindingByLan;
    }

    public boolean getHasBindingByWan() {
        return this.hasBindingByWan;
    }

    public void setHasBindingByWan(boolean hasBindingByWan) {
        this.hasBindingByWan = hasBindingByWan;
    }

    public long getTokenInsterTimes() {
        return this.tokenInsterTimes;
    }

    public void setTokenInsterTimes(long tokenInsterTimes) {
        this.tokenInsterTimes = tokenInsterTimes;
    }

}
