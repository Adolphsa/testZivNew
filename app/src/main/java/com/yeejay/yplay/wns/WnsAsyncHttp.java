package com.yeejay.yplay.wns;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.tencent.wns.client.inte.WnsService;
import com.yeejay.yplay.YplayApplication;

/**
 * Wns请求类
 * Created by Administrator on 2017/12/18.
 */

public class WnsAsyncHttp {

    private static final String TAG = "WnsAsyncHttp";

    public static WnsAsyncHttp mWnsAsyncHttp = null;
    public WnsRequestListener mWnsRequestListener = null;
    private String mStringParams = null;
    public Activity mActivity = null;
    private String mUrl = null;

    WnsService wnsService = YplayApplication.getWnsInstance();

    public static WnsAsyncHttp getInstance() {
        if (mWnsAsyncHttp != null) {
            return mWnsAsyncHttp;
        }
        Log.d(TAG, "please new HttpUtil first!");
        return null;
    }

    public static void deleteWnsAsyncHttp() {
        if (mWnsAsyncHttp != null) {
            mWnsAsyncHttp = null;
        }
    }

    public static WnsAsyncHttp getWnsAsyncHttp(final String url, final String stringParams,
                                        Activity activity, WnsRequestListener wnsRequestListener) {

        mWnsAsyncHttp = new WnsAsyncHttp(url, stringParams, activity, wnsRequestListener);
        return mWnsAsyncHttp;
    }

    private WnsAsyncHttp(String url, String stringParams,
                         Activity activity, WnsRequestListener wnsRequestListener){

        mActivity = activity;
        mUrl = url;
        mWnsRequestListener = wnsRequestListener;
        mStringParams = stringParams;
    }

    class WnsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //执行异步任务

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //更新UI,显示结果
            Log.i(TAG, "onPostExecute: result---" + result);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //更新进度信息
        }
    }


}
