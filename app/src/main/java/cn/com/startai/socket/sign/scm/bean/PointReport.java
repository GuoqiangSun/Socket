package cn.com.startai.socket.sign.scm.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/17 0017
 * desc :
 */
public class PointReport {

    public String mac;

    public long ts;

    public float electricity;

    public byte[] data;

    public String toJsonStr() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("e", electricity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    @Override
    public String toString() {
        return " ts:" + ts + " electricity:" + electricity;
    }
}
