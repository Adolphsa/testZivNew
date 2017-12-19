package com.yeejay.yplay.wns;

/**
 * 该接口中的所有方法都是在主线程中调用
 * Created by Administrator on 2017/12/18.
 */

public interface WnsRequestListener<T> {

    /**
     * 当前无网络时回调该方法，回调后，该接口的其他方法都不会被调用
     */
    void onNoInternet();

    /**
     * 开始加载时调用，一般在此处放入加载进度条
     */
    void onStartLoad();

    /**
     * 只有加载成功时，才会调用该方法
     * @param result： 返回该次请求结果
     */
    void onComplete(T result);

    /**
     * 超时时调用该方法
     */
    void onTimeOut();

    /**
     * 加载出错时调用该方法
     */
    void onError();
}
