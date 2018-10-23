package cn.com.startai.socket.sign.scm.bean;

import cn.com.startai.socket.sign.scm.util.MySocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/30 0030
 * desc :
 */
public class UpdateVersion {

    public String mac;
    public byte action;
    public int progress;

    public boolean isQueryVersionAction() {
        return MySocketSecureKey.MUtil.isQueryVersionAction(action);
    }

    public boolean isUpdateVersionAction() {
        return MySocketSecureKey.MUtil.isUpdateModel(action);
    }

    public int curVersion;
    public int newVersion;

    @Override
    public String toString() {
        return "UpdateVersion{" +
                "mac='" + mac + '\'' +
                ", action=" + action +
                ", curVersion=" + curVersion +
                ", newVersion=" + newVersion +
                ", progress=" + progress +
                '}';
    }
}
