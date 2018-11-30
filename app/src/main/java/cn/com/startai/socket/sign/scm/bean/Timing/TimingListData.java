package cn.com.startai.socket.sign.scm.bean.Timing;

import org.json.JSONArray;

import java.util.ArrayList;

import cn.com.startai.socket.sign.scm.util.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/8 0008
 * desc :
 */
public class TimingListData {

    public int model;

    public void setModel(byte model) {
        this.model = model;
    }

    public void setModelIsCommon() {
        this.model = SocketSecureKey.Model.TIMING_COMMON;
    }

    public boolean isCommonModel() {
        return SocketSecureKey.Util.isCommonTiming((byte) model);
    }

    public void setModelIsAdvance() {
        this.model = SocketSecureKey.Model.TIMING_ADVANCE;
    }

    public boolean isAdvanceModel() {
        return SocketSecureKey.Util.isAdvanceTiming((byte) model);
    }

    private ArrayList<TimingCommonData> mCommonArray = new ArrayList<>(10);

    public ArrayList<TimingCommonData> getCommonDataArray() {
        return mCommonArray;
    }

    private final Object syncObj = new Object();

    public void clearAll() {
        synchronized (syncObj) {
            mCommonArray.clear();
            mAdvanceArray.clear();
        }
    }

    public void commonDataArrayCopy(ArrayList<TimingCommonData> tCommonArray) {
        synchronized (syncObj) {
            if (tCommonArray == null) {
                return;
            }
            mCommonArray.clear();
            if (tCommonArray.size() <= 0) {
                return;
            }

            mCommonArray.addAll(tCommonArray);
        }

    }

    public void putCommonData(String mac, int id, boolean on, byte week, String time, boolean startup) {
        TimingCommonData mData = new TimingCommonData();
        mData.setMac(mac);
        mData.setOn(on);
        mData.setId((byte) id);
        mData.setWeek(week & 0xFF);
        mData.setTime(time);
        mData.setStartup(startup);
        synchronized (syncObj) {
            mCommonArray.add(mData);
        }
    }

    private ArrayList<TimingAdvanceData> mAdvanceArray = new ArrayList<>(10);


    public ArrayList<TimingAdvanceData> getAdvanceDataArray() {
        return mAdvanceArray;
    }

    public void advanceDataArrayCopy(ArrayList<TimingAdvanceData> tAdvanceArray) {
        synchronized (syncObj) {
            if (tAdvanceArray == null) {
                return;
            }
            mAdvanceArray.clear();
            if (tAdvanceArray.size() <= 0) {
                return;
            }

            mAdvanceArray.addAll(tAdvanceArray);
        }

    }

    public void putAdvanceData(TimingAdvanceData mData) {
        synchronized (syncObj) {
            mAdvanceArray.add(mData);
        }
    }

    private static final String NULL_DATA = "[]";

    public String toCommonDataJsonStr() {
        synchronized (syncObj) {
            int size = mCommonArray.size();
            if (size <= 0) {
                return NULL_DATA;
            }
            JSONArray array = new JSONArray();
            for (int i = 0; i < mCommonArray.size(); i++) {
                try {
                    TimingCommonData mData = mCommonArray.get(i);
                    if (mData != null) {
                        array.put(mData.toJsonObj());
                    }
                } catch (Exception e) {

                }
            }

            return array.toString();
        }

    }

    public String toAdvanceJsonStr() {
        synchronized (syncObj) {
            int size = mAdvanceArray.size();
            if (size <= 0) {
                return NULL_DATA;
            }
            JSONArray array = new JSONArray();
            for (int i = 0; i < size; i++) {
                TimingAdvanceData mData = mAdvanceArray.get(i);
                if (mData != null) {
                    array.put(mData.toJsonObj());
                }
            }

            return array.toString();
        }
    }


}
