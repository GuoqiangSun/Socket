package cn.com.startai.socket.sign.scm.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/13 0013
 * desc :
 */
public class QueryHistoryCount {


    public QueryHistoryCount cloneMyself() {
        QueryHistoryCount mQueryHistoryCount = new QueryHistoryCount();
        mQueryHistoryCount.mac = mac;
        mQueryHistoryCount.startTime = startTime;
        mQueryHistoryCount.startTimeMillis = startTimeMillis;
        mQueryHistoryCount.endTime = endTime;
        mQueryHistoryCount.interval = interval;
        mQueryHistoryCount.day = day;
        mQueryHistoryCount.complete = complete;
        return mQueryHistoryCount;
    }

    public boolean complete;

    public int msgSeq;

    public boolean needQueryFromServer;

    public String mac;
    public String startTime;
    public long startTimeMillis;

    public long getStartTimestampFromStr() {

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long startTimestamp = 0L;

        if (startTime != null) {
            try {
                Date date = mFormat.parse(startTime);
                startTimestamp = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }

        return startTimestamp;
    }


    public String endTime;

    public long getEndTimestampFromStr() {

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long endTimestamp = 0L;

        if (endTime != null) {
            try {
                Date date = mFormat.parse(endTime);
                endTimestamp = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }

        return endTimestamp;
    }


    public int interval;


    public int day;


    public ArrayList<Data> mDataArray;

    public String toJsonArrayData() {
        JSONArray mArray = new JSONArray();

        if (mDataArray != null) {
            for (Data mData : mDataArray) {
                mArray.put(mData.toJsonObj());
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

        public Data() {
        }

        public Data(float e, float s) {
            this.e = e;
            this.s = s;
        }

        public float e;
        public float s;

        public JSONObject toJsonObj() {
            JSONObject mObj = new JSONObject();

            try {
                mObj.put("e", e);
                mObj.put("s", s);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return mObj;
        }

    }

    @Override
    public String toString() {
        return "QueryHistoryCount{" +
                "complete=" + complete +
                ", msgSeq=" + msgSeq +
                ", needQueryFromServer=" + needQueryFromServer +
                ", mac='" + mac + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startTimeMillis=" + startTimeMillis +
                ", endTime='" + endTime + '\'' +
                ", interval=" + interval +
                '}';
    }
}
