package cn.com.startai.socket.sign.hardware.WiFi.util;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.startai.socket.global.FileManager;
import cn.com.startai.socket.sign.hardware.WiFi.impl.UserManager;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/6 0006
 * desc :
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private String TAG = UserManager.TAG;

    private String url;
    private File outputFile;

    public DownloadTask(String url) {
        File cachePath = FileManager.getInstance().getCachePath();
        String name = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        File savePath = new File(cachePath, "update_" + name + ".apk");
        this.url = url;
        this.outputFile = savePath;
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


        try {

            URL downURL = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) downURL.openConnection();

            int responseCode = urlConnection.getResponseCode();

            Tlog.v(TAG, "DownloadTask responseCode " + responseCode);

            if (responseCode == 200) {

                long contentLength = urlConnection.getContentLength();
                Tlog.v(TAG, "DownloadTask contentLength " + contentLength);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);

                FileOutputStream fos = new FileOutputStream(outputFile);
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
                        Tlog.v(TAG, "DownloadTask progress " + progress);
                    }

                }

                Tlog.v(TAG, "DownloadTask readLength " + allLength);

                bis.close();

                bos.flush();

                bos.close();

            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, " DownloadTask IOException ", e);
        }


        return null;
    }
}
