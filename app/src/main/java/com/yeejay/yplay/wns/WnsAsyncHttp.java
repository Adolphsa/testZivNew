package com.yeejay.yplay.wns;

import android.util.Log;

import com.tencent.wns.client.inte.WnsService;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Wns请求类
 * Created by Administrator on 2017/12/18.
 */

public class WnsAsyncHttp {

    private static final String TAG = "WnsAsyncHttp";
    private static WnsService wnsService = YplayApplication.getWnsInstance();
    private static final int THREAD_COUNT = 10;

    public static void wnsRequest(String url, Map<String, Object> stringParams,
                           WnsRequestListener wnsRequestListener) {

        int readTimeout = 10*1000; //如果不设置，则默认30秒
        int connTimeout = 10*1000; //如果不设置，这默认30秒
        new WnsAsyncTask(url, stringParams, wnsRequestListener, readTimeout, connTimeout).executeOnExecutor(Executors.newFixedThreadPool(THREAD_COUNT));
    }

    public static void wnsRequest(String url, Map<String, Object> stringParams,
                                  WnsRequestListener wnsRequestListener, int connTimeout, int readTimeout) {
        new WnsAsyncTask(url, stringParams, wnsRequestListener, connTimeout, readTimeout).executeOnExecutor(Executors.newFixedThreadPool(THREAD_COUNT));
    }


    public static String sendHttpUrlConnReq(String url, Map<String, Object> params, int connTimeout, int readTimeout) {

        String result = "";
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {

            //1.构造URL，底层网络走wns通道,使用起来和URL是一样的。
            URL u = wnsService.getWnsHttpUrl(url);                   //需要在控制台上配置url对应域名的路由
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            //conn.addRequestProperty("User-Agent", "");
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            //[两个超时总和的请求，建议60秒
            conn.setConnectTimeout(connTimeout);
            conn.setReadTimeout(readTimeout);

            //如果是post方法，需要设置一下post参数,
            conn.setDoOutput(true);
            String outStr = getRequestData(params).toString();

            Log.i(TAG, "sendHttpUrlConnReq: outStr---" + outStr);

            conn.getOutputStream().write(outStr.getBytes());

            //获取页面内容
            int rspcode = conn.getResponseCode();

            Log.d(TAG, "rspcode = " + rspcode);

            if (HttpURLConnection.HTTP_OK == rspcode) {
                in = conn.getInputStream();
                if (in == null) {
                    return null;
                }

                byte[] buff = new byte[4096];
                out = new ByteArrayOutputStream();
                int len = -1;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }

                String content = out.toString().trim();

//              Log.i(TAG, "sendHttpUrlConnReq: content---" + content);
                return content;
            }else{
                // todo 加日志  url rspcode
                LogUtils.getInstance().error("request url %s, http rsp code %d", url, rspcode);

            }

        }catch (SocketTimeoutException timeoutException){
            result = WnsAsyncTask.TIME_OUT;
            LogUtils.getInstance().error("time out url---" + url);
        } catch (Exception e) {
            Log.i(TAG, "sendHttpUrlConnReq: 异常e---" + e.getMessage());
            LogUtils.getInstance().error("Exception---" + e.getMessage());

        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 封装请求体信息
     *
     * @param params 请求体 参数
     * @return 返回封装好的StringBuffer
     */
    private static StringBuffer getRequestData(Map<String, Object> params) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }


}
