package com.yeejay.yplay;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.request.target.ViewTarget;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.tencent.wns.client.inte.WnsAppInfo;
import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;
import com.yeejay.yplay.greendao.DaoMaster;
import com.yeejay.yplay.greendao.DaoSession;
import com.yeejay.yplay.greendao.DataBaseHelper;
import com.yeejay.yplay.im.ImConfig;
import com.yeejay.yplay.utils.Foreground;

import java.io.File;

/**
 *
 * Created by Administrator on 2017/10/29.
 */

public class YplayApplication extends Application {

    private static YplayApplication instance;

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private static Context context;

    private static WnsService wns = WnsClientFactory.getThirdPartyWnsService ();

    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null){
            instance = this;
        }

        Foreground.init(this);
        context = getApplicationContext();

        ViewTarget.setTagId(R.id.glide_tag);

        WnsAppInfo info = new WnsAppInfo()
                .setAppId(203682)
                .setAppVersion("1.0.0")
                .setChannelId("yyb");
        wns.initAndStartWns(this, info);

        setDatabase();

        ImConfig.getImInstance().imConfig();
        ImConfig.getImInstance().userConfig();

        if(MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher_yplay);

                    }
                }
            });
        }

        //创建记录日志的文件夹，路径为/storage/emulated/0/yplay/logs
        String dirStr = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "yplay" + File.separator + "logs";
        File file = new File(dirStr);
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
    }

    public static YplayApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    public static WnsService getWnsInstance(){
        if (wns == null){
            wns = WnsClientFactory.getThirdPartyWnsService ();
        }
        return wns;
    }

    private void setDatabase() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,"yplay-db");
        mDaoMaster = DataBaseHelper.getDaoMaster(this);
        mDaoSession = DataBaseHelper.getDaoSession(this);
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
