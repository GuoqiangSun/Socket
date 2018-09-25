package cn.com.startai.socket.mutual.js.bean;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.startai.fssdk.db.entity.DownloadBean;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/5 0005
 * desc :
 */
public class UpdateProgress {


    /**
     * {
     * "url": "http://ip:port/xxx.apk",          // 下载链接
     * "status": 1,                              // 下载状态 0   0暂停 1下载中 2下载成功 3下载等待 4下载错误
     * "extName": "mp3/mp4/apk",                 // 后缀名
     * "localPath": "sdcard/startai/download/",  // 文件本地保存路径
     * "addedSize": "",                          // 已下载的文件大小
     * "totalSize": "",                          // 文件总大小
     * "progress": "86",                         // 进度（整数）
     * "updateTime": "",                         // 更新时间
     * "fileName": "",                           // 文件名
     * "fileId": "",                             // 文件fileId
     * "protocol": "tcp http"                    // 下载类型
     * }
     */

    public UpdateProgress(){

    }

    public UpdateProgress(DownloadBean downloadBean){
        this.addedSize = downloadBean.getAddedSize();
        this.extName = downloadBean.getExtName();
        this.fileId = downloadBean.getFileId();
        this.fileName = downloadBean.getFileName();
        this.localPath = downloadBean.getLocalPath();
        this.progress = downloadBean.getProgress();
        this.protocol = downloadBean.getProtocol();
        this.status = downloadBean.getStatus();
        this.totalSize = downloadBean.getTotalSize();
        this.updateTime = downloadBean.getUpdateTime();
        this.url = downloadBean.getUrl();
    }

    public String url;        // 下载链接
    public int status;                      // 下载状态 0   0暂停 1下载中 2下载成功 3下载等待 4下载错误
    public String extName;              // 后缀名
    public String localPath;  // 文件本地保存路径
    public long addedSize;                        // 已下载的文件大小
    public long totalSize;                        // 文件总大小
    public int progress;                         // 进度（整数）
    public long updateTime;                      // 更新时间
    public String fileName;                       // 文件名
    public String fileId;                           // 文件fileId
    public String protocol;                  // 下载类型

    public String toJsonStr() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("url",url);
            obj.put("status",status);
            obj.put("extName",extName);
            obj.put("localPath",localPath);
            obj.put("addedSize",addedSize);
            obj.put("totalSize",totalSize);
            obj.put("progress",progress);
            obj.put("updateTime",updateTime);
            obj.put("fileName",fileName);
            obj.put("fileId",fileId);
            obj.put("protocol",protocol);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
