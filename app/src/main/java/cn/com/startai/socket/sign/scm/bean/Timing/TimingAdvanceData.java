package cn.com.startai.socket.sign.scm.bean.Timing;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/3 0003
 * desc :
 */
public class TimingAdvanceData {

    public String mac;


    /**
     * 模式
     */
    public byte model;

    public void setModel(byte model) {
        this.model = model;
    }

    public void setModelIsAdvance() {
        this.model = SocketSecureKey.Model.TIMING_ADVANCE;
    }

    public boolean modelIsAdvance() {
        return SocketSecureKey.Util.isAdvanceTiming(model);
    }

    public byte id;

    public boolean on;

    public boolean startup;
    public int week;

    @Override
    public String toString() {

        return "  id:" + id + " startHour:" + startHour + " startMinute:" + startMinute + " endHour:" + endHour + " endMinute:" + endMinute +
                "  on :" + on + " onIntervalHour:" + onIntervalHour + " onIntervalMinute:" + onIntervalMinute + " offIntervalHour:" + offIntervalHour + " offIntervalMinute:" + offIntervalMinute + " startup:" + startup;

    }

//    {
//        "time": "21:25",
//            "time2": "23:25",
//            "id":1,
//            "state": false //  true启动
//        "week": 5   // 10进制转换成二进制00000101表示
//        周三、周一
//    }

    public JSONObject toJsonObj() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("time", this.onTime);
            obj.put("time2", this.offTime);
            obj.put("id", this.id);
            obj.put("state", this.startup);
            obj.put("week", this.week);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public int startHour;
    public int startMinute;
    private String onTime;

    /**
     * 保存
     */
    public static final int STATE_CONFIRM = 0x01;
    /**
     * 删除
     */
    public static final int STATE_DELETE = 0x02;


    public void setStateIsConfirm() {
        this.state = STATE_CONFIRM;
    }

    public boolean stateIsConfirm() {
        return (state == STATE_CONFIRM);
    }

    public void setStateIsDelete() {
        this.state = STATE_DELETE;
    }

    public boolean stateIsDelete() {
        return (state == STATE_DELETE);
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    /**
     * 保存 删除
     */
    public byte state;

    public void setOnTimeSplit(String onTime) {

        this.onTime = onTime;

        if (this.onTime == null) {
            this.startHour = this.startMinute = 0;
            return;
        }

        String[] split = this.onTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                this.startHour = Integer.parseInt(s);
            } catch (Exception e) {
                this.startHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                this.startMinute = Integer.parseInt(s);
            } catch (Exception e) {
                this.startMinute = 0;
            }
        }

    }

    public int endHour;
    public int endMinute;
    private String offTime;

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }


    public void setOffTimeSplit(String offTime) {
        this.offTime = offTime;

        if (this.offTime == null) {
            this.endHour = this.endMinute = 0;
            return;
        }

        String[] split = this.offTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                this.endHour = Integer.parseInt(s);
            } catch (Exception e) {
                this.endHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                this.endMinute = Integer.parseInt(s);
            } catch (Exception e) {
                this.endMinute = 0;
            }
        }

    }

    public int onIntervalHour;
    public int onIntervalMinute;
    private String onIntervalTime;

    public void setOnIntervalTime(String onIntervalTime) {
        this.onIntervalTime = onIntervalTime;

        if (this.onIntervalTime == null) {
            this.onIntervalHour = this.onIntervalMinute = 0;
            return;
        }

        String[] split = this.onIntervalTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                this.onIntervalHour = Integer.parseInt(s);
            } catch (Exception e) {
                this.onIntervalHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                this.onIntervalMinute = Integer.parseInt(s);
            } catch (Exception e) {
                this.onIntervalMinute = 0;
            }
        }

    }

    public int offIntervalHour;
    public int offIntervalMinute;
    private String offIntervalTime;

    public void setOffIntervalTime(String offIntervalTime) {
        this.offIntervalTime = offIntervalTime;


        if (this.offIntervalTime == null) {
            this.offIntervalHour = this.offIntervalMinute = 0;
            return;
        }

        String[] split = this.offIntervalTime.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                this.offIntervalHour = Integer.parseInt(s);
            } catch (Exception e) {
                this.offIntervalHour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                this.offIntervalMinute = Integer.parseInt(s);
            } catch (Exception e) {
                this.offIntervalMinute = 0;
            }
        }


    }

}
