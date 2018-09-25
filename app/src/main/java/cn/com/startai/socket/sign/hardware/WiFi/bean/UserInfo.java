package cn.com.startai.socket.sign.hardware.WiFi.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/10 0010
 * desc :
 */
@Entity
public class UserInfo {

    @Id(autoincrement = true)
    private Long gid;


    private String mid;

    private String email;

    private String mobile;

    private String userName;

    private long expire_in;

    private long lastLoginTime;

    private int type;


    @Generated(hash = 1068764848)
    public UserInfo(Long gid, String mid, String email, String mobile,
            String userName, long expire_in, long lastLoginTime, int type) {
        this.gid = gid;
        this.mid = mid;
        this.email = email;
        this.mobile = mobile;
        this.userName = userName;
        this.expire_in = expire_in;
        this.lastLoginTime = lastLoginTime;
        this.type = type;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getExpire_in() {
        return this.expire_in;
    }

    public void setExpire_in(long expire_in) {
        this.expire_in = expire_in;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

}
