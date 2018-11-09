package cn.com.startai.socket.sign.scm.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/13 0013
 * desc :
 */
public class QueryHistoryCount {

    public String mac;
    public String startTime;
    public long startTimeMillis;
    public String endTime;
    public int interval;


    public int day;
    public ArrayList<Data> mDataArray;

    public String toJsonArrayData() {
        JSONArray mArray = new JSONArray();

        if (mDataArray != null) {
            for (Data mData : mDataArray) {
                mArray.put(mData.toJsonObjData());
            }
        }

        return mArray.toString();
    }

    public ArrayList<Day> mDayArray;

    public static class Day {

        public byte[] countData;
        public long startTime;
    }

    public static class Data {
        public float e;
        public float s;


        public String toJsonObjData() {
            JSONObject mObj = new JSONObject();

            try {
                mObj.put("e", e);
                mObj.put("s", s);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return mObj.toString();
        }
    }


}
