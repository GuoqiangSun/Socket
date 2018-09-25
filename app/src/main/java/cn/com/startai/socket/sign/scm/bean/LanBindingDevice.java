package cn.com.startai.socket.sign.scm.bean;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/10 0010
 * desc :
 */

public class LanBindingDevice {

    private Long gid;

    private String mid; // 我的id

    private String oid; // 对方的id

    private String omac; // 对方的mac

    private boolean isAdmin;// 是否是管理员

    private boolean hasBindingByRemote; // 是否已经远程建立了绑定关系

    private String cpuInfo;

    private int token;

    public LanBindingDevice(Long gid, String mid, String oid, String omac,
            boolean isAdmin, boolean hasBindingByRemote, String cpuInfo,
            int token) {
        this.gid = gid;
        this.mid = mid;
        this.oid = oid;
        this.omac = omac;
        this.isAdmin = isAdmin;
        this.hasBindingByRemote = hasBindingByRemote;
        this.cpuInfo = cpuInfo;
        this.token = token;
    }

    public LanBindingDevice() {
    }



    public Long getGid() {
        return this.gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
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

    public String getOmac() {
        return this.omac;
    }

    public void setOmac(String omac) {
        this.omac = omac;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getHasBindingByRemote() {
        return this.hasBindingByRemote;
    }

    public void setHasBindingByRemote(boolean hasBindingByRemote) {
        this.hasBindingByRemote = hasBindingByRemote;
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

}
