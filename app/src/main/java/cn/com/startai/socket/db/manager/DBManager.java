package cn.com.startai.socket.db.manager;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import cn.com.startai.socket.db.gen.DaoMaster;
import cn.com.startai.socket.db.gen.DaoSession;
import cn.com.startai.socket.global.LooperManager;
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
        LooperManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                initDB(app);
            }
        });
    }

    private final Object syncObj = new Object();

    private void initDB(Application app) {

        UpdateOpenHelper updateOpenHelper = new UpdateOpenHelper(app);
//                Database writableDb = updateOpenHelper.getEncryptedWritableDb("123");
        Database writableDb = updateOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(writableDb);
        daoSession = daoMaster.newSession();
        synchronized (syncObj) {
            syncObj.notifyAll();
        }
        Tlog.i(" DBManager init success...");
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        if (daoSession == null) {
            synchronized (syncObj) {
                if (daoSession == null) {
                    Tlog.e("DBManager daoSession == null wait() ");
                    try {
                        syncObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tlog.e("DBManager daoSession == null wait() finish");
                }
            }
        }
        return daoSession;
    }

}
