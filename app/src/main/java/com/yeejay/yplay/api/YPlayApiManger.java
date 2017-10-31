package com.yeejay.yplay.api;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * api管理
 * Created by Administrator on 2017/10/29.
 */

public class YPlayApiManger {

    public static final String BASE_URL = "http://yplay.vivacampus.com";

    //URL u = YplayApplication.getWnsInstance().getWnsHttpUrl(BASE_URL);


    //缓存策略  有网时获取网络数据   没网时获取缓存
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(YplayApplication.getInstance())){
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            }else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            //打印retrofit日志
            Log.i("RetrofitLog","retrofitBack = "+message);
        }
    });


    public static YPlayApiManger mYplayManger;

    //缓存相关
    private static File httpCacheDirectory = new File(YplayApplication.getInstance().getCacheDir(),"zivCachesss");
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory,cacheSize);
    private OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .build();

    private YPlayApi yplayApi;
    private  YPlayApi yPlayParametersApi;
    private Object yplayMonitor = new Object();

    public static YPlayApiManger getInstance(){
        if (mYplayManger == null){
            synchronized (YPlayApiManger.class){
                if (mYplayManger == null){
                    mYplayManger = new YPlayApiManger();
                }
            }
        }
        return mYplayManger;
    }

    public YPlayApi getZivApiService(){
        System.out.println("无参服务");

        if (yplayApi == null){
            synchronized (yplayMonitor){
                if (yplayApi == null){
                    yplayApi = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(YPlayApi.class);
                }
            }
        }
        return yplayApi;
    }

    public YPlayApi getZivApiServiceParameters(String baseUrl){
        System.out.println("有参服务");
        System.out.println("baeUrl---" + baseUrl);
        if (yPlayParametersApi == null){
            synchronized (yplayMonitor){
                if (yPlayParametersApi == null){
                    yPlayParametersApi = new Retrofit.Builder()
                            .baseUrl("http://sh.file.myqcloud.com")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(YPlayApi.class);
                }
            }
        }
        return yPlayParametersApi;
    }
}
