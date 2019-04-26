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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

        upload();

//        uploadFile();

//        uploadTcp();

        System.out.println("end");
    }

    private static void uploadTcp() {
        //1.创建客户端Socket对象，并指定ip跟端口
        Socket s = null;
        try {
            s = new Socket("58.253.238.132", 8080);

            //2.将Socket中封装好的字节输出流包装成缓冲字节输出流
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());

            //3.从指定的路径中读取要上传的文件上传到服务端
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("E:\\abc.txt"));
            //IO流经典4行代码
            byte[] b = new byte[1024];
            int len;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            //将文件内容从缓存器刷新到服务端
            bos.flush();
            //告诉服务端，客户端已经上传完毕
            s.shutdownOutput();

            //4.获取服务端的返回结果
            //将Socket中封装好的字节输入流包装成字节缓冲输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //读取
            String string = br.readLine();
            System.out.println(string);

            //关闭资源
            bis.close();//自己创建的字节缓冲输出流
            s.close();//关闭Socket，其自己封装好的输入输出流也将关闭

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void upload() {

        File f = new File("E:\\abc.txt");

        System.out.println("flength:" + f.length());

        String http = "http://58.253.238.132:8080/upload";
//        String http = "http://58.253.238.132:8080/group1/default/M00/00/00/abc.txt";
        try {
            URL url = new URL(http);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(f.length()));
            conn.setConnectTimeout(10 * 1000);
            String BOUNDARY = "mk5X9g84K7B6lwW0fXX9iIHM-sXth1sEK";
//            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setDoOutput(true);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            byte[] buf = new byte[1024];
            int read = -1;
            OutputStream bufferOutStream = null;

            bufferOutStream = new BufferedOutputStream(conn.getOutputStream());

            while ((read = bis.read(buf, 0, 1024)) != -1) {
                System.out.println("read:" + read);
                bufferOutStream.write(buf, 0, read);
            }
//            bufferOutStream.write(msg.getBytes());
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
