package cn.com.startai.socket.java;

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
