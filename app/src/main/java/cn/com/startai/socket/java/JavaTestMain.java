package cn.com.startai.socket.java;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * author: Guoqiang_Sun
 * date: 2018/11/19 0019
 * Desc:
 */
public class JavaTestMain {

    public static void main(String[] args) {
//        c();
    }

    public static void d() {
        JSONObject mObj = new JSONObject();
        try {
            mObj.put("name", "123");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        byte[] bytes = {0x01, 0x02, 0x03};

        JSONArray mArray = new JSONArray();
        mArray.put(bytes[0]);
        mArray.put(bytes[1]);
        mArray.put(bytes[2]);

        try {
            mObj.put("data", mArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String s = mObj.toString();
        System.out.println(s);
    }

    private static void c() {

        String s = "123";

        System.out.println(s.hashCode());

        System.out.println("123".hashCode());

        new Thread() {
            @Override
            public void run() {
                super.run();

                String s = "123";

                System.out.println("t:" + s.hashCode());

                System.out.println("t :" + "123".hashCode());
            }
        }.start();
    }

    private static void b() {
        byte[] protocolParams = new byte[2];
        protocolParams[0] = (byte) 0x9a;
        protocolParams[1] = 0x00;

        int temp_int = protocolParams[0];
        System.out.println(temp_int);

        int temp_deci = protocolParams[1] & 0xFF;
        float tempF = Float.valueOf(temp_int + "." + temp_deci);


        float temp = (float) (Math.round(tempF * 100)) / 100;

        System.out.println(temp);

    }

    private static void a() {

        TimeZone aDefault = TimeZone.getDefault();

        String displayName = aDefault.getDisplayName(false, TimeZone.SHORT);

        System.out.println(" " + displayName);

        String displayName1 = aDefault.getDisplayName();
        System.out.println(" " + displayName1);

        String displayName2 = aDefault.getDisplayName(Locale.getDefault());
        System.out.println(" " + displayName2);

        String s = aDefault.getID();
        System.out.println(" " + s);

        String timezone = "GMT+08:00";
//        timezone.substring()


        int i = timezone.indexOf("+");
        System.out.println(" " + i);

        boolean isA = i > 0;

        if (!isA) {
            i = timezone.indexOf("-");
            System.out.println(" " + i);
        }

        int i1 = timezone.indexOf(":");
        System.out.println(" " + i1);

        String substring = timezone.substring(i + 1, i1);
        System.out.println(" " + substring);

        int i2 = Integer.parseInt(substring);
        System.out.println(" " + i2);

        int timezoneInt = isA ? i2 : -i2;
        System.out.println(" " + timezoneInt);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long curMillis = 1542902400000L;
        long startTimestampFromStr = 1542816000000L;

        System.out.println(" cur: " + dateFormat.format(new Date(curMillis)));
        System.out.println(" startTimestampFromStr: " + dateFormat.format(new Date(startTimestampFromStr)));
    }


}
