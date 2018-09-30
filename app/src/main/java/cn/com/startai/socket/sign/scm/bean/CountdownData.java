package cn.com.startai.socket.sign.scm.bean;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.startai.socket.db.gen.DaoSession;
import cn.com.startai.socket.db.gen.PowerCountdownDao;
import cn.com.startai.socket.db.manager.DBManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/4 0004
 * desc :
 */
public class CountdownData {

    @Override
    public String toString() {
        return "CountdownData{" +
                "mac='" + mac + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", allTime=" + allTime +
                ", Switchgear=" + Switchgear +
                ", countdownSwitch=" + countdownSwitch +
                '}';
    }

    /**
     * '{
     * "hour": 1,
     * "minute": 50,
     * "Switchgear": true,
     * "countdownSwitch": false
     * }'
     */

    public String mac;

//    public boolean result;

    public int hour;
    public int minute;

    public int allTime = -1;

    /**
     * 开机关机 on
     */
    public boolean Switchgear;

    /**
     * 启动结束   startup
     */
    public boolean countdownSwitch;

    private static final String NULL_DATA = "{}";

    public String toJsonStr() {
        try {
            JSONObject jo = new JSONObject();
            jo.put("hour", hour);
            jo.put("minute", minute);
            jo.put("allTime", allTime);
            jo.put("Switchgear", Switchgear);
            jo.put("countdownSwitch", countdownSwitch);
            return jo.toString();
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return NULL_DATA;
    }

}
