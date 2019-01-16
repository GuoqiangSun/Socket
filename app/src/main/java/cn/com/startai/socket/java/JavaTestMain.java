package cn.com.startai.socket.java;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cn.com.swain.baselib.util.HexUtils;

/**
 * author: Guoqiang_Sun
 * date: 2018/11/19 0019
 * Desc:
 */
public class JavaTestMain {

    public static void main(String[] args) {
//        c();
//        e();


//     f();

//        g();

        splitUrl();

    }

    private static void splitUrl() {
        String url = "http://thirdwx.qlogo.cn/mmopen/vi_32/7rmnEumJia3WnaNAqxhKkU5HqeibPGJ1jIQ3coWTib59cysdy0mqbu7libZqUPjUVgPwcEUYicnbZNsejKugkkGvIwQ/132";
        int i = url.lastIndexOf("/");
        String substring = url.substring(i + 1);

        int i1 = substring.indexOf(".");
        if(i1<0){
            substring+=".jpg";
        }

        System.out.println(substring);
    }

    private static void g() {
        ArrayList<String> dat = new ArrayList<>();

        dat.add("1");
        dat.add("2");

        Object[] objects = dat.toArray();

        String[] str = dat.toArray(new String[0]);

        System.out.println(objects.length);

        for (Object o : objects) {
            System.out.println(String.valueOf(o));
        }

        System.out.println(str.length);

        for (String o : str) {
            System.out.println(String.valueOf(o));
        }

    }

    private static void f() {

        String intStr = "19502";

        int i = Integer.parseInt(intStr);

        byte i1 = (byte) ((i >> 8) & 0xFF);

        byte i2 = (byte) (i & 0xFF);
        String s2 = Integer.toHexString(i1);
        if (s2.length() == 1) {
            s2 = "0" + s2;
        }
        System.out.println(s2);
        String s1 = Integer.toHexString(i2);
        if (s1.length() == 1) {
            s1 = "0" + s1;
        }
        System.out.println(s1);

        String s = s2 + s1;

        System.out.println(s);

    }

    private static void e() {

//        5c2c6620
        byte[] protocolParams = new byte[4];
        protocolParams[0] = 0x5c;
        protocolParams[1] = 0x2c;
        protocolParams[2] = 0x66;
        protocolParams[3] = 0x20;

        byte[] buf2 = new byte[8];
        buf2[0] = 0x00;
        buf2[1] = 0x00;
        buf2[2] = 0x00;
        buf2[3] = 0x00;
        buf2[4] = protocolParams[0];
        buf2[5] = protocolParams[1];
        buf2[6] = protocolParams[2];
        buf2[7] = protocolParams[3];

        long l = HexUtils.byteToLong(buf2);

        System.out.println(l);

        long ts = ByteBuffer.wrap(buf2, 0, 8).getLong();

        System.out.println(ts);

        long tsl = ts * 1000;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String format = dateFormat.format(new Date(tsl));

        System.out.println(format);

    }


    private static void d() {
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
