package cn.com.startai.socket.sign.scm.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.swain.baselib.log.Tlog;

/**
 * Author：Guoqiang_Sun
 * Date : on 2015/8/11 11:00
 * Description :
 */
public class TimezonePullUtil {

    private static final String TIMEZONE = "timezone";
    // private static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIME = "time";

    // private static final String TIMEZONES = "timezones";

    public static HashMap<String, TimezoneBean> parseXml(InputStream is) {

        HashMap<String, TimezoneBean> data = null;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            TimezoneBean timezoneBean = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        data = new HashMap<>();
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (TIMEZONE.equals(tag)) {
                            timezoneBean = new TimezoneBean();
                            timezoneBean.id = parser.getAttributeValue(0);
                        } else {
                            if (timezoneBean != null) {
                                if (NAME.equals(tag)) {
                                    timezoneBean.name = parser.nextText().trim();
                                } else if (TIME.equals(tag)) {
                                    timezoneBean.time = parser.nextText().trim();
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TIMEZONE.equals(parser.getName())) {
                            if (data != null && timezoneBean != null) {
                                data.put(timezoneBean.id, timezoneBean);
                            }
                        }
                        break;

                    default:
                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static void show(HashMap<String, TimezoneBean> mBeans) {
        if (Debuger.isLogDebug && mBeans != null) {
            for (Map.Entry<String, TimezoneBean> e : mBeans.entrySet()) {
                TimezoneBean value = e.getValue();
                Tlog.v(SocketScmManager.TAG, String.valueOf(value) + "\n");
            }
        }
    }

}
