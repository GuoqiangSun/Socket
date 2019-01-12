package cn.com.startai.socket.global;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import cn.com.swain.baselib.file.FileTemplate;
import cn.com.swain.baselib.file.FileUtil;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/15 0015
 * desc :
 */
public class FileManager extends FileTemplate {

    private FileManager() {
    }

    private static final class ClassHolder {
        private static final FileManager FM = new FileManager();
    }

    public static FileManager getInstance() {
        return ClassHolder.FM;
    }

    @Override
    public void init(Application app) {
        super.init(app);

        String absolutePath = getProjectPath().getAbsolutePath();

        FileUtil.notifySystemToScan(app, absolutePath);

//        MediaScannerConnection.scanFile(app, new String[]{absolutePath}, null, null);

        Tlog.i(" FileManager init finish ; success:" + exit);
    }

    public void recreate(Application app) {
        super.init(app);
        Tlog.i(" FileManager recreate finish ; success:" + exit);
    }


    /**
     * 获取app缓存数据的目录
     *
     * @return
     */
    protected File initMyProjectPath() {
        if (CustomManager.getInstance().isTriggerBle()) {
            return new File(getAppRootPath(), "TriggerHomeBle");
        } else if (CustomManager.getInstance().isTriggerWiFi()) {
            return new File(getAppRootPath(), "TriggerHomeWiFi");
        } else if (CustomManager.getInstance().isGrowroomate()) {
            return new File(getAppRootPath(), "Growrootmate");
        } else if (CustomManager.getInstance().isMUSIK()) {
            return new File(getAppRootPath(), "MUSIK");
        } else if (CustomManager.getInstance().isAirtempNBProject()) {
            return new File(getAppRootPath(), "AirTempNB");
        }
        return new File(getAppRootPath(), "socket");
    }

    @Override
    protected String initMyAppRootPath() {
        return "startai";
    }
}
