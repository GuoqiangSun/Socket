package cn.com.startai.socket.db.manager;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.lang.ref.WeakReference;

import cn.com.startai.socket.db.gen.DaoMaster;
import cn.com.startai.socket.db.gen.DaoSession;
import cn.com.swain.baselib.app.IApp.IApp;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/21 0021
 * desc :
 */

public class DBManager implements IApp {

    private static final class ClassHolder {
        private static final DBManager DBMANAGER = new DBManager();
    }

    public static DBManager getInstance() {
        return ClassHolder.DBMANAGER;
    }


    @Override
    public void init(Application app) {
        initDB(app);
    }

    private void initDB(Application app) {

        UpdateOpenHelper updateOpenHelper = new UpdateOpenHelper(app);
//                Database writableDb = updateOpenHelper.getEncryptedWritableDb("123");
        Database writableDb = updateOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(writableDb);
        daoSession = daoMaster.newSession();
        Tlog.i(" DBManager init success...");
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
