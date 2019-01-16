package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.sign.hardware.WiFi.impl.UserManager;
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
            formBitmap();
        }

        return null;
    }

    private void formBitmap() {

        try {

            URL downURL = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) downURL.openConnection();

            int responseCode = urlConnection.getResponseCode();

            Tlog.v(TAG, "DownloadTask formBitmap responseCode " + responseCode);

            if (responseCode == 200) {

                long contentLength = urlConnection.getContentLength();
                Tlog.v(TAG, "DownloadTask formBitmap contentLength " + contentLength);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);

                Bitmap bitmap = BitmapFactory.decodeStream(bis);

                FileOutputStream fos = new FileOutputStream(outputFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bis.close();

                bos.flush();

                bos.close();

            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, " DownloadTask IOException ", e);
        }


    }


    private void fromHttp() {


        try {

            URL downURL = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) downURL.openConnection();

            int responseCode = urlConnection.getResponseCode();

            Tlog.v(TAG, "DownloadTask fromHttp responseCode " + responseCode);

            if (responseCode == 200) {

                long contentLength = urlConnection.getContentLength();
                Tlog.v(TAG, "DownloadTask fromHttp contentLength " + contentLength);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);

                String randomName = UUID.randomUUID().toString();
                File file = new File(getSaveDownRootPath(), randomName);

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int read;
                byte[] buffer = new byte[1024 * 8];
                long allLength = 0;
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

                Tlog.v(TAG, "DownloadTask success syncFile ; allLength:" + allLength);
                FileUtil.syncFile(fos.getFD());
                FileUtil.copyFileUsingFileChannels(file, outputFile);

                boolean delete = file.delete();
                Tlog.v(TAG, "DownloadTask copyFile success delete cache " + delete);

                bis.close();

                bos.flush();

                bos.close();

            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, " DownloadTask IOException ", e);
        }

    }

}
