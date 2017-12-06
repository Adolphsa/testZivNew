package com.yeejay.yplay.data;

import android.content.Context;

import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.network.ApiHelper;
import com.yeejay.yplay.data.prefs.PreferencesHelper;

/**
 * DataManager实现类
 * Created by Administrator on 2017/12/5.
 */

public class ImpDataManager implements DataManager{

    private Context mContext;
    private DbHelper mDbHelper;
    private ApiHelper mApiHelper;
    private PreferencesHelper mPreferencesHelper;

    public ImpDataManager(Context context,
                          DbHelper dbHelper,
                          ApiHelper apiHelper,
                          PreferencesHelper preferencesHelper){
        mContext = context;
        mDbHelper = dbHelper;
        mApiHelper = apiHelper;
        mPreferencesHelper = preferencesHelper;
    }

}
