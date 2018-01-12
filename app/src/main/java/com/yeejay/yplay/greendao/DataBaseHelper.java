package com.yeejay.yplay.greendao;

import android.content.Context;
import android.util.Log;

import com.yeejay.yplay.utils.LogUtils;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库升级
 * Created by Administrator on 2017/11/23.
 */

public class DataBaseHelper extends DaoMaster.DevOpenHelper{

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    public static final String DBNAME = "yplay-db";


    public DataBaseHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
        LogUtils.getInstance().error(oldVersion + "---先前和更新之后的版本---" + newVersion);
        if (oldVersion < newVersion) {
            Log.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
            MigrationHelper.getInstance().migrate(db,
                    ContactsInfoDao.class,
                    DaoFriendFeedsDao.class,
                    FriendInfoDao.class,
                    ImMsgDao.class,
                    ImSessionDao.class,
                    MyInfoDao.class);
        }
    }

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    DBNAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
