package cn.com.startai.socket.sign.scm.bean.Timing;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/9 0009
 * desc :
 */
public class TimingCommonData {

    private boolean startup; // 启动 结束

    public void setStartup(boolean startup) {
        this.startup = startup;
    }

    public boolean getStartup() {
        return this.startup;
    }

    private String mac;

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return this.mac;
    }


    /**
     * 模式
     */
    private byte model;

    public void setModel(byte model) {
        this.model = model;
    }

    public void setModelIsCommon() {
        this.model = SocketSecureKey.Model.TIMING_COMMON;
    }

    public boolean modelIsCommon() {
        return SocketSecureKey.Util.isCommonTiming(model);
    }


    /**
     * id
     */
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public byte getId() {
        return (byte) (id & 0xFF);
    }

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

    public byte getState() {
        return state;
    }

    /**
     * 保存 删除
     */
    private byte state;

    /**
     * 关闭电源  开启电源
     */
    private boolean on;

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setIsOn() {
        this.on = true;
    }

    public void setIsOff() {
        this.on = false;
    }

    public boolean isOn() {
        return on;
    }

    /**
     * 星期
     */
    private int week;

    public byte getWeek() {
        return (byte) (week & 0xFF);
    }

    public void setWeek(int week) {
        this.week = week;
    }

    private String time;
    private int hour;
    private int minute;

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public byte getHour() {
        return (byte) (hour & 0xFF);
    }

    public byte getMinute() {
        return (byte) (minute & 0xFF);
    }

    public void setTime(String time) {

        this.time = time;

        if (this.time == null) {
            this.hour = this.minute = 0;
            return;
        }

        String[] split = this.time.replaceAll(" ", "").split(":");

        if (split.length >= 1) {
            String s = split[0].trim();
            try {
                this.hour = Integer.parseInt(s);
            } catch (Exception e) {
                this.hour = 0;
            }
        }

        if (split.length >= 2) {
            String s = split[1].trim();
            try {
                this.minute = Integer.parseInt(s);
            } catch (Exception e) {
                this.minute = 0;
            }
        }

    }

    public String getTime() {
        return this.time;
    }

    public JSONObject toJsonObj() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("switch", this.on);
            obj.put("time", this.time);
            obj.put("id", this.id);
            obj.put("week", this.week);
            obj.put("state", this.startup);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String toJsonStr() {
        return toJsonObj().toString();
    }

    @Override
    public String toString() {
        return toJsonStr();
    }
}
