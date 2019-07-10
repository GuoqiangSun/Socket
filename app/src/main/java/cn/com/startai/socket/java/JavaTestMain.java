package cn.com.startai.socket.java;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;

import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.swain.baselib.util.HexUtils;

/**
 * author: Guoqiang_Sun
 * date: 2018/11/19 0019
 * Desc:
 */
public class JavaTestMain {

    public static void main(String[] args) {

//        a();

//        c();
//        e();


//     f();

//        g();

//        splitUrl();

//        scan();

//        time();

//        upload();

//        uploadFile();

//        uploadTcp();

//        ll();

//        hs();

//        testHash();

//        cth();

//        escape();

//        FThexStr();

//        for (int i = 0; i < 10; i++) {
//            cal();
////            cal2();
//        }


        cpl();

        System.out.println(Double.toHexString(2.2D));
        System.out.println(Double.toHexString(3.2D));
        System.out.println(Double.toHexString(3.3D));
        System.out.println(Double.toHexString(33.3D));
        System.err.println("end ");
    }

    private static void cpl() {
        Long ll = 1L;
        Long lll = 1L;
        System.err.println("ll=lll " + (ll.equals(lll)));
    }


    private static void fenlei() {
        int sum = 0;
        for (int i = 1; i < 100; i++) {

            sum += i;

            if (i % 2 == 0) {
                System.out.println(i + " 是偶数");
            } else {
                System.out.println(i + " 是奇数");
            }

        }

        System.out.println("100以内整数和:" + sum);

    }

    private static void cal() {
        byte[] md5 = new byte[16];
        Random r = new Random();

        for (int i = 0; i < 16; i++) {
            md5[i] = (byte) r.nextInt();
//            System.out.println(" md5:" + md5[i]);
        }
//        byte and = 0x03;
//        System.out.println(and + " :" + Integer.toBinaryString(and));
//        System.out.println(" md5[14]:" + (md5[14] & 0xFF) + " " + Integer.toBinaryString(md5[14] & 0xFF));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 6) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 6));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 4) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 4));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 2) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 2));
//        System.out.println(" md5[15]:" + (md5[15] & 0xFF) + " " + Integer.toBinaryString(md5[15] & 0xFF));

        ag0(md5);
//        ag1(md5);
//        ag2(md5);
    }

    private static void ag0(byte[] md5) {
        int a = c5((md5[14] & 0xFF) & 0x07);
        int b = c5(((md5[15] & 0xFF) >> 6) & 0x07);
        int c = c5(((md5[15] & 0xFF) >> 4) & 0x07);
        int d = c5(((md5[15] & 0xFF) >> 2) & 0x07);
        int e = c5(((md5[15] & 0xFF) & 0x07));
        System.out.println(" a :" + a + " b :" + b + " c :" + c + " d :" + d + " e :" + e);
    }

    private static int c5(int a) {
//        a = a & 0x07 + 1;
        return a > 0x05 ? a & 0x03 + 2 : ((a <= 0) ? 1 : a);
    }

    private static void ag1(byte[] md5) {
        int a = ((md5[14] & 0xFF) & 0x03) + 2;
        int b = (((md5[15] & 0xFF) >> 6) & 0x03) + 2;
        int c = (((md5[15] & 0xFF) >> 4) & 0x03) + 2;
        int d = (((md5[15] & 0xFF) >> 2) & 0x03) + 1;
        int e = ((md5[15] & 0xFF) & 0x03) + 2;
        System.out.println(" a :" + a + " b :" + b + " c :" + c + " d :" + d + " e :" + e + " -------");
    }

    private static void ag2(byte[] md5) {
        int a = md5[15] & 0x07;
        a = c5(a);
        int b = md5[15] & 0x38 >> 3;
        b = c5(b);
        int c = md5[15] & 0xE0 >> 5;
        c = c5(c);
        int d = md5[15] & 0x1c >> 2;
        d = c5(d);
        int e = md5[14] & 0x07;
        e = c5(e);
        System.out.println("------- a :" + a + " b :" + b + " c :" + c + " d :" + d + " e :" + e);
    }


    private static void cal2() {
        byte[] md5 = new byte[16];
        Random r = new Random();

        for (int i = 0; i < 16; i++) {
            md5[i] = (byte) r.nextInt();
//            System.out.println(" md5:" + md5[i]);
        }
//        byte and = 0x03;
//        System.out.println(and + " :" + Integer.toBinaryString(and));
//        System.out.println(" md5[14]:" + (md5[14] & 0xFF) + " " + Integer.toBinaryString(md5[14] & 0xFF));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 6) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 6));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 4) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 4));
//        System.out.println(" md5[15]:" + ((md5[15] & 0xFF) >> 2) + " " + Integer.toBinaryString((md5[15] & 0xFF) >> 2));
//        System.out.println(" md5[15]:" + (md5[15] & 0xFF) + " " + Integer.toBinaryString(md5[15] & 0xFF));
        int a = ((md5[14] & 0xFF) & 0x03) + 2;
        int b = (((md5[15] & 0xFF) >> 6) & 0x03) + 2;
        int c = (((md5[15] & 0xFF) >> 4) & 0x03) + 2;
        int d = (((md5[15] & 0xFF) >> 2) & 0x03) + 1;
        int e = ((md5[15] & 0xFF) & 0x03) + 2;
        System.out.println(" a :" + a + " b :" + b + " c :" + c + " d :" + d + " e :" + e + " -------");

//        System.out.println(" md5[15]:" + (md5[15] & 0xFF) + " " + Integer.toBinaryString(md5[15] & 0xFF));

    }


    private static void strEncord() {

        String bleName = "123abc公司";
        try {
            byte[] bytes = bleName.getBytes();
            for (byte b : bytes) {
                System.out.print(" " + Integer.toHexString(b & 0xFF));
            }
            System.out.println();

            byte[] asciis = bleName.getBytes("US-ASCII");
            for (byte b : asciis) {
                System.out.print(" " + Integer.toHexString(b & 0xFF));
            }
            System.out.println();

            byte[] gbks = bleName.getBytes("GBK");
            for (byte b : gbks) {
                System.out.print(" " + Integer.toHexString(b & 0xFF));
            }
            System.out.println();

            String s = new String(asciis);
            System.out.println(s);

            s = new String(asciis, StandardCharsets.US_ASCII);
            System.out.println(s);

            s = new String(asciis, "GBK");
            System.out.println(s);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String name = Charset.defaultCharset().name();
        System.err.println("Charset " + name);

    }

    static HashMap<Long, String> map = new HashMap<>();


    private static void FThexStr() {
        long from = 0xFF0C0000123L;  // 280427116560675
        int FROM_LEN = 0xFF0C; // 65292
        long FROM_ID = 0x0000123; // 111870243
//        0000967295

        byte[] buf = new byte[6];
        buf[0] = (byte) 0xFF;
        buf[0] = (byte) 0xFF;


        System.out.println(" from:" + from);
        System.out.println(" FROM_LEN:" + FROM_LEN);
        System.out.println(" FROM_ID:" + FROM_ID);

        String topic = Long.toHexString(from); // topic:ff0c06ab0123
        System.out.println(" topic:" + topic);

        map.put(from, topic);

//        String s = map.get(from);
//        if(s==null){
//            map.put(from,topic);
//        }

        String topic_dan = Integer.toHexString(FROM_LEN) + Long.toHexString(FROM_ID);
        System.out.println(" topic_dan:" + topic_dan); //  topic_dan:ff0c06ab0123

        byte a2 = (byte) ((from >> 40) & 0xFF);
        byte a1 = (byte) ((from >> 32) & 0xFF);
        byte a = (byte) ((from >> 24) & 0xFF);
        byte b = (byte) ((from >> 16) & 0xFF);
        byte c = (byte) ((from >> 8) & 0xFF);
        byte d = (byte) ((from >> 0) & 0xFF);

        int aa = ((a2 & 0xFF) << 8) | (a1 & 0xFF);
        System.out.println(" aa:" + aa);
        int bb = ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8) | (d & 0xFF);
        System.out.println(" bb:" + bb);

        String topic_int = aa + "" + bb; //  topic_int:65292111870243
        System.out.println(" topic_int:" + topic_int);

        String topic_int_dan = FROM_LEN + "" + FROM_ID; //  topic_int_dan:65292111870243
        System.out.println(" topic_int_dan:" + topic_int_dan);


        int appid = 0x0001A3B6;
        System.out.println(" appid:" + appid); //  appid:107446
        System.out.println(" appid hex:" + Integer.toHexString(appid)); // appid hex:1a3b6
    }


    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }


    private static void escape() {
        String appid = "6e3788eedb60442c88b647bfaa1d285b";
        int hashCode = appid.hashCode();
        System.out.println("hashCode:" + hashCode);
        System.out.println("hashCode HEX:" + Integer.toHexString(hashCode));


        ByteBuffer buffer = ByteBuffer.allocate(8);
        byte a = (byte) ((hashCode >> 24) & 0xFF);
        byte b = (byte) ((hashCode >> 16) & 0xFF);
        byte c = (byte) ((hashCode >> 8) & 0xFF);
        byte d = (byte) ((hashCode >> 0) & 0xFF);
        byte[] bytes = {0, 0, 0, 0, a, b, c, d};
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        long hashCode_LL = buffer.getLong();
        System.out.println(" hashCode_L:" + hashCode_LL);
        System.out.println(" hashCode_L:" + Long.toHexString(hashCode_LL));

        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        System.out.println(" num:" + num);
        System.out.println(" num:" + Long.toHexString(num));

        long ll = hashCode;
        ll = (ll << 32) >>> 32;
        System.out.println(" ll:" + ll);
        System.out.println(" ll:" + Long.toHexString(ll));

    }

    private static void cth() {
        byte[] bytes = "SA".getBytes();
        for (byte b : bytes) {
            System.out.println(Integer.toHexString(b));
        }


    }

    private static void testHash() {


        HashMap map = new HashMap();
        int len = 3;
        char[] chars = new char[len];
        tryBit(chars, len, map);
        System.out.println((int) Math.pow(offset, len) + ":" + dup);

        String aa = "Aa";
        String bb = "BB";
        System.out.println("aa:" + aa.hashCode());
        System.out.println("bb:" + bb.hashCode());


        hash(aa);
        hash(bb);
    }

    private static void hash(String s) {
        char[] chars1 = s.toCharArray();
        int h = 0;
        int hash;
        final int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = chars1[i];
            int i1 = 31 * h;
            h = +c;
            System.out.println("h:" + h + " i1:" + i1 + " c:" + (int) c);
        }
        hash = h;
        System.out.println("----------" + s + " .hash:" + hash);
    }


    private static char startChar = 'A';

    private static char endChar = 'z';

    private static int offset = endChar - startChar + 1;

    private static int dup = 0;


    private static void tryBit(char[] chars, int i, HashMap map) {
        for (char j = startChar; j <= endChar; j++) {
            chars[i - 1] = j;
            if (i > 1)
                tryBit(chars, i - 1, map);
            else
                test(chars, map);
        }
    }

    private static void test(char[] chars, HashMap map) {

        String str = new String(chars).replaceAll("[^a-zA-Z_]", "").toLowerCase();// 195112:0
//        String str = new String(chars).toLowerCase();//195112:6612
//        String str = new String(chars).replaceAll("[^a-zA-Z_]","");//195112:122500
        //String str = new String(chars);//195112:138510
        int hash = str.hashCode();
        if (map.containsKey(hash)) {
            String s = (String) map.get(hash);
            if (!s.equals(str)) {
                dup++;
                System.out.println(s + ":" + str);
            }
        } else {
            map.put(hash, str);
            // System.out.println(str);
        }
    }

    private static void hs() {
        String s = "1";
        System.out.println(s.hashCode());

        String ss = new String("1");
        System.out.println(ss.hashCode());


    }

    private static void ll() {

        long l = 12345678901234L;
        System.out.println(l);
        System.out.println(Long.toBinaryString(l));
        int i = (int) l;
        System.out.println(Integer.toBinaryString(i));
        System.out.println(i);
//        10110011101001110011110011100010111111110010
//        00000000000001110011110011100010111111110010
    }


    private static void upload() {
        String fileName = "E:\\abc.txt";
        File f = new File(fileName);
        // 换行符
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        long length = f.length();

        System.out.println("flength:" + length);

        String http = "http://58.253.238.132:8080/upload";
//        String http = "http://58.253.238.132:8080/group1/default/M00/00/00/abc.txt";
        try {
            URL url = new URL(http);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(length));
            conn.setConnectTimeout(10 * 1000);
            String BOUNDARY = "mk5X9g84K7B6lwW0fXX9iIHM-sXth1sEK";
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Range", "bytes 1-12/" + String.valueOf(length));
            conn.setDoOutput(true);


            // 上传文件
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + "abc.txt"
                    + "\"" + newLine);
//            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            bis.skip(1);

            int rl = 11;
            byte[] buf = new byte[rl];
            int read = -1;
            OutputStream bufferOutStream = null;

            bufferOutStream = new BufferedOutputStream(conn.getOutputStream());

            // 将参数头的数据写入到输出流中
            bufferOutStream.write(sb.toString().getBytes());

            int total = 0;

            while ((read = bis.read(buf, 0, rl)) != -1) {
                System.out.println("read:" + read);
                bufferOutStream.write(buf, 0, read);
                total += read;
                System.out.println("total:" + total);
                if (total >= rl) {
                    System.out.println("total:" + total + " >= rl:" + rl);
                    break;
                }
            }
//            bufferOutStream.write(msg.getBytes());


            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
                    .getBytes();
            // 写上结尾标识
            bufferOutStream.write(end_data);
            bufferOutStream.flush();
            bufferOutStream.close();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode:" + responseCode);

            if (responseCode == 200) {
                // 定义BufferedReader输入流来读取URL的响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            bufferOutStream.flush();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile() {
        String fileName = "E:\\hello.txt";
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
//            String http = "http://58.253.238.132:8080/group1/default/20190425/16/29/5/o.json";
//            String http = "http://58.253.238.132:8080/upload/group1/default/20190425/18/30/6/hello.json";
            String http = "http://58.253.238.132:8080/upload";


//            String BOUNDARY = "========7d4a6d158c9";
//            String http = "www.myhost.com";
            // 服务器的域名
            URL url = new URL(http);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            String BOUNDARY = "mk5X9g84K7B6lwW0fXX9iIHM-sXth1sEK";
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // 上传文件
            File file = new File(fileName);
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + fileName
                    + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());

            // 数据输入流,用于读取文件数据
            DataInputStream in = new DataInputStream(new FileInputStream(
                    file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            // 最后添加换行
            out.write(newLine.getBytes());
            in.close();

            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
                    .getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode:" + responseCode);

            if (responseCode == 200) {
                // 定义BufferedReader输入流来读取URL的响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
    }


    private static void time() {
        int year = 2019;
        int month = 2;
        int day = 26;
        Date d = new Date(year, month, day);
        System.out.println(d.getTime());

        Date dd = new Date(System.currentTimeMillis());
        System.out.println(dd.getTime());

        Calendar instance = Calendar.getInstance();
//        instance.set((dd.getYear() + 1900 - 2000),dd.getMonth()+1,dd.getDate());
//        instance.set(dd.getYear(),dd.getMonth(),dd.getDate());
        instance.setTimeInMillis(System.currentTimeMillis());
        System.out.println(instance.getTimeInMillis());

        Calendar instance2 = Calendar.getInstance();
        instance2.set(2019, 1, 26);
        long timeInMillis = instance2.getTimeInMillis();
        System.out.println(timeInMillis);

        long startime = DateUtils.fastFormatTsToDayTs(timeInMillis);
        System.out.println(startime);

        Calendar instance3 = Calendar.getInstance();
        instance3.setTimeInMillis(startime);
        long timeInMillis1 = instance.getTimeInMillis();
        System.out.println(timeInMillis1);

        Date time = instance.getTime();
        System.out.println(time.getYear());
        System.out.println(time.getMonth());
        System.out.println(time.getDate());
    }


    private static void scan() {
        Scanner s = new Scanner(System.in);
        int i = s.nextInt();
        System.out.println(i);
    }


    private static void splitUrl() {
        String url = "http://thirdwx.qlogo.cn/mmopen/vi_32/7rmnEumJia3WnaNAqxhKkU5HqeibPGJ1jIQ3coWTib59cysdy0mqbu7libZqUPjUVgPwcEUYicnbZNsejKugkkGvIwQ/132";
        int i = url.lastIndexOf("/");
        String substring = url.substring(i + 1);

        int i1 = substring.indexOf(".");
        if (i1 < 0) {
            substring += ".jpg";
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

        System.out.println(" getDisplayName(false, TimeZone.SHORT): " + displayName);

        String displayName1 = aDefault.getDisplayName();
        System.out.println(" getDisplayName: " + displayName1);

        String displayName2 = aDefault.getDisplayName(Locale.getDefault());
        System.out.println(" getDisplayName(Locale.getDefault()): " + displayName2);

        String s = aDefault.getID();
        System.out.println(" getID: " + s);

        int dstSavings = aDefault.getDSTSavings();
        System.out.println(" dstSavings: " + dstSavings);

        String timezone = "-08:00";
//        timezone.substring()


        int i = timezone.indexOf("+");
        System.out.println(" indexOf+ " + i);

        boolean isA = i >= 0;

        if (!isA) {
            i = timezone.indexOf("-");
            System.out.println(" indexOf- " + i);
        }

        int i1 = timezone.indexOf(":");
        System.out.println(" " + i1);

        String substring = timezone.substring(i + 1, i1);
        System.out.println(" " + substring);

        int i2 = Integer.parseInt(substring);
        System.out.println(" " + i2);

        int timezoneInt = isA ? i2 : -i2;
        System.out.println(" timezoneInt: " + timezoneInt);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long curMillis = 1542902400000L;
        long startTimestampFromStr = 1542816000000L;

        System.out.println(" cur: " + dateFormat.format(new Date(curMillis)));
        System.out.println(" startTimestampFromStr: " + dateFormat.format(new Date(startTimestampFromStr)));
    }


}
