package cn.com.startai.socket.sign.scm.bean;


import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

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
        return SocketSecureKey.Util.isQueryVersionAction(action);
    }

    public boolean isUpdateVersionAction() {
        return SocketSecureKey.Util.isUpdateModel(action);
    }


    public byte curVersionMain;
    public byte curVersionSub;

    public double getDoubleCurVersion() {
        int m = (curVersionMain & 0xFF);
        int s = (curVersionSub & 0xFF);
        String v = m + "." + s;
        double v1;
        try {
            v1 = Double.parseDouble(v);
        } catch (Exception e) {
            v1 = curVersion;
        }
        return v1;
    }

    public int curVersion;

    public int newVersion;

    public byte newVersionMain;
    public byte newVersionSub;


    public double getDoubleNewVersion() {
        int m = (newVersionMain & 0xFF);
        int s = (newVersionSub & 0xFF);
        String v = m + "." + s;
        double v1;
        try {
            v1 = Double.parseDouble(v);
        } catch (Exception e) {
            v1 = newVersion;
        }
        return v1;
    }

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
