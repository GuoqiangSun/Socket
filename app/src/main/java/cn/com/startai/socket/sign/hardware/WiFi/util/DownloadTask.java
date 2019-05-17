package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.sign.hardware.WiFi.impl.UserManager;
import cn.com.startai.socket.sign.hardware.ble.util.DeviceEnablePost;
import cn.com.swain.baselib.file.FileUtil;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/6 0006
 * desc :
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    // 下载的缓存梗根路径
    public static File getSaveDownRootPath() {
        return FileManager.getInstance().getCachePath();
    }

    // 名称
    public static String getDownName(String url) {
        if (url == null) {
            return UUID.randomUUID().toString();
        }
        int i = url.lastIndexOf("/");
        if (i < 0) {
            return UUID.randomUUID().toString();
        }
        String substring = url.substring(i + 1);

        int i1 = substring.indexOf(".");
        if (i1 < 0) {
            substring += ".jpg";
        }

        return substring;
    }

    // 保存路径
    public static File getCacheDownPath(String url) {
        return new File(getSaveDownRootPath(), getDownName(url));
    }


    private String TAG = UserManager.TAG;

    private String url;
    private File outputFile;

    public DownloadTask(String url) {
        this.url = url;
        this.outputFile = getCacheDownPath(url);
    }

    public DownloadTask(String url, File outputFile) {
        this.url = url;
        this.outputFile = outputFile;
    }

    public DownloadTask(String url, String outputPath) {
        this.url = url;
        this.outputFile = new File(outputPath);
    }

    @Override
    protected Void doInBackground(Void... voids) {

//        formBitmap();

        boolean hasSuffix = true;

        int i = url.lastIndexOf("/");
        if (i > 0) {
            String substring = url.substring(i + 1);

            int i1 = substring.indexOf(".");
            if (i1 < 0) {
                hasSuffix = false;
            }
        }
        if (hasSuffix) {
            fromHttp();
        } else {
            formBitmap(); // wx用 fromHttp有问题。
        }

        return null;
    }

    private void formBitmap() {

        try {

            URL downURL = new URL(url);

            HttpURLConnection urlConnection = (HttpURLConnection) downURL.openConnection();

            if (urlConnection instanceof HttpsURLConnection) {

                SSLContext sc;

                if (Build.VERSION.SDK_INT < 22) {
                    sc = SSLContext.getInstance("TLSv1.2");
                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(new DeviceEnablePost.Tls12SocketFactory(sc.getSocketFactory()));
                } else {
                    sc = SSLContext.getInstance("SSL");

                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sc.getSocketFactory());

                }
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(new TrustAnyHostnameVerifier());

            }

            int responseCode = urlConnection.getResponseCode();
            Tlog.v(TAG, "DownloadTask formBitmap responseCode " + responseCode);

            if (responseCode == 200) {

                long contentLength = urlConnection.getContentLength();
                Tlog.v(TAG, "DownloadTask formBitmap contentLength " + contentLength);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);

                Bitmap bitmap = BitmapFactory.decodeStream(bis);

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

                Tlog.v(TAG, "DownloadTask formBitmap start compress length:" + outputFile.length());

//                bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp等。

                if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                } else if (url.endsWith(".png")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                } else if (url.endsWith(".webp")) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }

                Tlog.v(TAG, "DownloadTask formBitmap compress finish  length:" + outputFile.length());

                bis.close();

                bos.flush();
                bos.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Tlog.e(TAG, " DownloadTask IOException ", e);
        }


    }


    private void fromHttp() {

        File cacheFile = null;
        long allLength = 0;

        try {

            URL downURL = new URL(url);

            HttpURLConnection urlConnection = (HttpURLConnection) downURL.openConnection();

            if (urlConnection instanceof HttpsURLConnection) {

                SSLContext sc;

                if (Build.VERSION.SDK_INT < 22) {
                    sc = SSLContext.getInstance("TLSv1.2");
                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(new DeviceEnablePost.Tls12SocketFactory(sc.getSocketFactory()));
                } else {
                    sc = SSLContext.getInstance("SSL");

                    sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                            new java.security.SecureRandom());
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sc.getSocketFactory());

                }
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(new TrustAnyHostnameVerifier());

            }

            int responseCode = urlConnection.getResponseCode();

            Tlog.v(TAG, "DownloadTask fromHttp responseCode " + responseCode);

            if (responseCode == 200) {

                long contentLength = urlConnection.getContentLength();
                Tlog.v(TAG, "DownloadTask fromHttp contentLength " + contentLength);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);

                String randomName = UUID.randomUUID().toString();
                cacheFile = new File(getSaveDownRootPath(), randomName);

                FileOutputStream fos = new FileOutputStream(cacheFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int read;
                byte[] buffer = new byte[1024 * 8];

                int progress;
                int lastProgress = 0;
                while ((read = bis.read(buffer)) != -1) {
                    allLength += read;
                    bos.write(buffer, 0, read);

//                    Tlog.v(TAG, "DownloadTask down " + allLength);

                    progress = (int) (allLength * 100 / contentLength);
                    if (progress > lastProgress) {
                        lastProgress = progress;
                        Tlog.v(TAG, "DownloadTask fromHttp progress " + progress);
                    }

                }

                bos.flush();

                FileUtil.syncFile(fos.getFD());
                FileUtil.copyFileUsingFileChannels(cacheFile, outputFile);

                Tlog.v(TAG, "DownloadTask success syncFile ; allLength:" + allLength
                        + "  outputFile.length():" + outputFile.length());

                bis.close();

                bos.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Tlog.e(TAG, " DownloadTask IOException ", e);
        } finally {

            if (cacheFile != null && cacheFile.exists()) {
                boolean delete1 = cacheFile.delete();// delete cache
                Tlog.v(TAG, "DownloadTask finish ;  delete cache:" + delete1);
            }

            if (outputFile != null && outputFile.exists() && outputFile.length() < allLength) {
                boolean delete = outputFile.delete();
                Tlog.v(TAG, "DownloadTask finish ; but outputFile.length()< allLength: delete :" + delete);
            }

        }

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



}
