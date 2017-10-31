package com.yeejay.yplay;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.tencent.wns.client.inte.WnsAppInfo;
import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;
import com.yeejay.yplay.greendao.DaoMaster;
import com.yeejay.yplay.greendao.DaoSession;

/**
 *
 * Created by Administrator on 2017/10/29.
 */

public class YplayApplication extends Application {

    private static YplayApplication instance;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    private static WnsService wns = WnsClientFactory.getThirdPartyWnsService ();

    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null){
            instance = this;
        }

        WnsAppInfo info = new WnsAppInfo()
                .setAppId(203682)
                .setAppVersion("1.0.0")
                .setChannelId("yyb");
        wns.initAndStartWns(this, info);

        setDatabase();
    }

    public static YplayApplication getInstance() {
        return instance;
    }

    public static WnsService getWnsInstance(){
        if (wns == null){
            wns = WnsClientFactory.getThirdPartyWnsService ();
        }
        return wns;
    }

    private void setDatabase() {

        mHelper = new DaoMaster.DevOpenHelper(this,"yplay-db", null);
        db =mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
