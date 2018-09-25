package cn.com.startai.socket.global;

import android.app.Application;

import java.io.File;

import cn.com.swain.baselib.file.FileTemplate;
import cn.com.swain169.log.Tlog;

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
        Tlog.i(" FileManager init finish ; success:" + exit);
    }

    public void recreate(Application app) {
        super.init(app);
        Tlog.i(" FileManager recreate finish ; success:" + exit);
    }

    private static final String MY_PROJECT_PATH_NAME = "socket";

    /**
     * 获取app缓存数据的目录
     *
     * @return
     */
    protected File initMyProjectPath() {
        return new File(getAppRootPath(), MY_PROJECT_PATH_NAME);
    }


}
