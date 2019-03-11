package cn.com.startai.socket.sign.hardware.ble.util;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.socket.db.gen.DisplayBleDeviceDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.global.DeveloperBuilder;
import cn.com.startai.socket.mutual.js.bean.DisplayBleDevice;
import cn.com.startai.socket.sign.hardware.ble.impl.BleManager;
import cn.com.startai.socket.sign.hardware.ble.xml.ConBleSp;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/17 0017
 * desc :
 */
public class DeviceEnablePost extends AsyncTask<Void, Void, String> {

    private static String TAG = BleManager.TAG;

    private final String mac;
    private Application app;

    public DeviceEnablePost(Application app, String mac) {

        this.mac = mac;
        this.app = app;
    }


    @Override
    protected String doInBackground(Void... voids) {

        if (mac == null) {
            Tlog.e(TAG, " DeviceEnablePost() doInBackground mac=null ");
            return null;
        }
        String enableDevice = ConBleSp.getConBleSp(app).getEnableDevice("");

        if (enableDevice.equalsIgnoreCase(mac)) {
            Tlog.e(TAG, " DeviceEnablePost()  sp mac " + mac + " already enable");
            return null;
        }

        DisplayBleDeviceDao displayBleDeviceDao = DBManager.getInstance().getDaoSession().getDisplayBleDeviceDao();
        QueryBuilder<DisplayBleDevice> where = displayBleDeviceDao.queryBuilder().where(DisplayBleDeviceDao.Properties.Address.eq(mac),
                DisplayBleDeviceDao.Properties.HasRemoteActivation.eq(false));
        List<DisplayBleDevice> list = where.list();

        if (list.size() <= 0) {
            Tlog.e(TAG, " DeviceEnablePost()  db mac " + mac + " already enable");
            return null;
        }

        String sn = StartAI.getInstance().getDeviceInfoManager().getSn(mac);
        DeveloperBuilder.BleSocketScmDeveloper mBleDeveloper = new DeveloperBuilder.BleSocketScmDeveloper();

        String msg = null;

        try {
            JSONArray rootArray = new JSONArray();

            JSONObject devObj = new JSONObject();
            devObj.put("m_ver", mBleDeveloper.m_ver);
            devObj.put("clientid", sn);
            devObj.put("sn", sn);
            devObj.put("appid", mBleDeveloper.appid);
            devObj.put("apptype", mBleDeveloper.apptype);
            devObj.put("domain", mBleDeveloper.domain);

            JSONObject hardwareObj = new JSONObject();
            hardwareObj.put("sysVersion", "");
            hardwareObj.put("bluetoothMac", mac);
            devObj.put("firmwareParam", hardwareObj);

            rootArray.put(devObj);
            msg = rootArray.toString();

        } catch (JSONException e) {
            Tlog.e(TAG, " DeviceEnablePost() JSONException ", e);
            e.printStackTrace();
        }

        Tlog.e(TAG, " DeviceEnablePost() msg :" + msg);
        if (msg == null) {
            return null;
        }

        return sendMsg(msg);
    }

    /**
     * [{
     * "m_ver":"",
     * "clientid":"",
     * "sn":"",
     * "appid":"",
     * "apptype":"",
     * "domain":"",
     * "activateType":2,  //选填字段
     * "firmwareParam":{
     * "sysVersion":"",
     * "iNetMac":""     //选填字段
     * }}]
     */


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Tlog.e(TAG, " DeviceEnablePost() onPostExecute " + s);

        if (s == null || s.length() <= 0) {

            return;
        }
//        激活成功时返回:
//        {"errcode":"200","errmsg":"OK"}
//        激活失败时返回:
//        {"errcode":"0x800101","errmsg":"devices active fail,param loss"}

        String errcode = "";

        try {
            JSONObject jsonObject = new JSONObject(s);
            errcode = jsonObject.optString("errcode");
            Tlog.v(TAG, "errcode " + errcode);

        } catch (JSONException e) {
            e.printStackTrace();
            Tlog.v(TAG, " parseJson ", e);
        }

        if (errcode.equalsIgnoreCase("200")) {
            ConBleSp.getConBleSp(app).setEnableDevice(mac);

            DisplayBleDeviceDao displayBleDeviceDao = DBManager.getInstance().getDaoSession().getDisplayBleDeviceDao();
            QueryBuilder<DisplayBleDevice> where = displayBleDeviceDao.queryBuilder().where(DisplayBleDeviceDao.Properties.Address.eq(mac),
                    DisplayBleDeviceDao.Properties.HasRemoteActivation.eq(false));
            List<DisplayBleDevice> list = where.list();

            if (list.size() > 0) {
                for (DisplayBleDevice mDisplayBleDevice : list) {
                    mDisplayBleDevice.setHasRemoteActivation(true);
                    displayBleDeviceDao.update(mDisplayBleDevice);
                    Tlog.v(TAG, " displayBleDeviceDao update " + mDisplayBleDevice.getId());
                }
            }

        }

    }

    private static String sendMsg(String msg) {

        if (msg == null || msg.length() <= 0) {
            return null;
        }

        String responseMsg = null;


        try {
            URL url = new URL("https://bs.startai.cn/device/v1.0/simble_activate");
//            URL url = new URL("http://192.168.1.148:8080/service/device/simpleactivate");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn instanceof HttpsURLConnection) {

                SSLContext sc;

                if (Build.VERSION.SDK_INT < 22) {
                    sc = SSLContext.getInstance("TLSv1.2");
                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) conn).setSSLSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));
                } else {
                    sc = SSLContext.getInstance("SSL");

                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());

                }
                ((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());

            }


//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(msg.length()));
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream bufferOutStream = null;

            bufferOutStream = new BufferedOutputStream(conn.getOutputStream());
            bufferOutStream.write(msg.getBytes());
            bufferOutStream.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Tlog.v(TAG, " conn.getResponseCode() " + conn.getResponseCode());
            BufferedInputStream bis = null;
            if (200 == conn.getResponseCode()) {
                InputStream inputStream = conn.getInputStream();
                bis = new BufferedInputStream(inputStream);

                int read = -1;
                byte[] data = new byte[512];

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                while ((read = bis.read(data)) != -1) {

                    bos.write(data, 0, read);

                }

                if (bos.size() > 0) {
                    byte[] bytes = bos.toByteArray();
                    responseMsg = new String(bytes);

                }

            }

            if (bufferOutStream != null) {
                bufferOutStream.close();
            }

            if (bis != null) {
                bis.close();
            }

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            Tlog.e(TAG, "DeviceEnable MalformedURLException ", e);
        } catch (IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, "DeviceEnable IOException ", e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Tlog.e(TAG, "DeviceEnable NoSuchAlgorithmException ", e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Tlog.e(TAG, "DeviceEnable KeyManagementException ", e);
        }


        return responseMsg;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {


        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {

        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    public static class Tls12SocketFactory extends SSLSocketFactory {
        private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

        final SSLSocketFactory delegate;

        public Tls12SocketFactory(SSLSocketFactory base) {
            this.delegate = base;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return patch(delegate.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return patch(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return patch(delegate.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return patch(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return patch(delegate.createSocket(address, port, localAddress, localPort));
        }

        private Socket patch(Socket s) {
            if (s instanceof SSLSocket) {
                ((SSLSocket) s).setEnabledProtocols(TLS_V12_ONLY);
            }
            return s;
        }
    }

    public static void main(String[] args) {
        sendMsg("123");
    }

}
