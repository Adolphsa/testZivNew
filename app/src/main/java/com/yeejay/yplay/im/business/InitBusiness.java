package com.yeejay.yplay.im.business;

import android.content.Context;
import android.util.Log;

import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusiness {

    private static final int IM_SDK_APP_ID = 1400046572;

    private static final String TAG = InitBusiness.class.getSimpleName();

    private InitBusiness(){}

    public static void start(Context context){
        initImsdk(context, 0);
    }

    public static void start(Context context, int logLevel){
        initImsdk(context, logLevel);
    }


    /**
     * 初始化imsdk
     */
    private static void initImsdk(Context context, int logLevel){
        TIMSdkConfig config = new TIMSdkConfig(IM_SDK_APP_ID);
        config.enableLogPrint(true)
                .setLogLevel(TIMLogLevel.values()[logLevel]);
        //初始化imsdk
        TIMManager.getInstance().init(context, config);
        //禁止服务器自动代替上报已读
        Log.d(TAG, "initIMsdk");

    }





}
