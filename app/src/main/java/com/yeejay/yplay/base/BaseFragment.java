package com.yeejay.yplay.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类
 * Created by Administrator on 2017/10/26.
 */

public abstract class BaseFragment extends Fragment{
    private String pageName;

    public abstract int getContentViewId();
    protected Context context;
    protected View mRootView;
    Unbinder unbinder;

    public BaseFragment() {
        pageName = getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView =inflater.inflate(getContentViewId(),container,false);
        unbinder = ButterKnife.bind(this,mRootView);//绑定framgent
        this.context = getActivity();
        initAllMembersView(savedInstanceState);
        return mRootView;
    }

    protected abstract void initAllMembersView(Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(false, false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onVisibilityChangedToUser(isVisibleToUser, true);
        }
    }

    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     * @param isVisibleToUser true：用户能看见当前Fragment；false：用户看不见当前Fragment
     * @param isHappenedInSetUserVisibleHintMethod true：本次回调发生在setUserVisibleHintMethod方法里；false：发生在onResume或onPause方法里
     */
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod){
        if(isVisibleToUser){
            if(pageName != null){
                Log.i("对用户可见", pageName + " - display - "+(isHappenedInSetUserVisibleHintMethod?"setUserVisibleHint":"onResume"));
            }
        }else{
            if(pageName != null){
                Log.w("对用户不可见", pageName + " - hidden - "+(isHappenedInSetUserVisibleHintMethod?"setUserVisibleHint":"onPause"));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
