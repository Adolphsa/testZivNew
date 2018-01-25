package com.yeejay.yplay.wns;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.utils.NetWorkUtil;

import java.util.Map;

/**
 * wns task
 * Created by Administrator on 2017/12/20.
 */

public class WnsAsyncTask extends AsyncTask<String, Integer, String>{

    private static final String TAG = "WnsAsyncTask";
    private static final String NO_INTENT = "no_intent";
    public static final String TIME_OUT = "time_out";

    private String url;
    private Map<String,Object> params;
    private WnsRequestListener wnsRequestListener;
    private int connTimeout; //毫秒!!
    private int readTimeout; //毫秒!!


    public WnsAsyncTask(String url, Map<String,Object> params, WnsRequestListener wnsRequestListener, int connTimeout, int readTimeout){
        this.url = url;

        this.params = params;
        this.wnsRequestListener = wnsRequestListener;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (NetWorkUtil.isNetWorkAvailable(YplayApplication.getInstance())){
            return WnsAsyncHttp.sendHttpUrlConnReq(url,params, connTimeout, readTimeout);
        }else {
            return NO_INTENT;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (TextUtils.isEmpty(s)){
            wnsRequestListener.onError();
        }else if (s.equals(NO_INTENT)){
            Log.i(TAG, "onPostExecute: 无网络");
            Toast.makeText(YplayApplication.getInstance(), "网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
            wnsRequestListener.onNoInternet();
        }else if (s.equals(TIME_OUT)){
            Log.i(TAG, "onPostExecute: 连接超时");
            wnsRequestListener.onTimeOut();
        }else {
            wnsRequestListener.onComplete(s);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        wnsRequestListener.onStartLoad();
    }
}
