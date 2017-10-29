package com.yeejay.yplay;

import android.app.Application;

import com.tencent.wns.client.inte.WnsAppInfo;
import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;

/**
 *
 * Created by Administrator on 2017/10/29.
 */

public class YplayApplication extends Application {

    private static YplayApplication instance;

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
}
