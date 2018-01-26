package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassList extends BaseActivity {

    @BindView(R.id.cl_back)
    ImageButton clBack;
    @BindView(R.id.cl_title_ll)
    LinearLayout clTitleLl;
    @BindView(R.id.cl_grade_list)
    MesureListView mPrimaryListView;
    @BindView(R.id.cl_scroll_view)
    LazyScrollView clScrollView;
    @BindView(R.id.tips)
    TextView mTips;

    @OnClick(R.id.cl_back)
    public void back(View view) {
        finish();
    }

    private static final String TAG = "ClassList";

    ArrayList<String> primaryList;

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;
    private int mLeftCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.class_color));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isActivitySetting = bundle.getInt("activity_setting_school");
            System.out.println("activity_setting_school" + isActivitySetting);
            if (isActivitySetting == 10){
                clBack.setVisibility(View.VISIBLE);
                queryUserUpdateLeftCount(3);
            }
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            Log.i(TAG, "onCreate: lat---" + mLatitude + ",lon---" + mLongitude);
        }

        if(isLoginMode()) {
            mTips.setText(R.string.look_for_sameclass);
        }


        mPrimaryListView = (MesureListView) findViewById(R.id.cl_grade_list);

        primaryList = new ArrayList<>();
        primaryList.add("初中一年级");
        primaryList.add("初中二年级");
        primaryList.add("初中三年级");
        primaryList.add("初中毕业");
        primaryList.add("高中一年级");
        primaryList.add("高中二年级");
        primaryList.add("高中三年级");
        primaryList.add("高中毕业");
        primaryList.add("大学一年级");
        primaryList.add("大学二年级");
        primaryList.add("大学三年级");
        primaryList.add("大学四年级");
        primaryList.add("大学五年级");
        primaryList.add("大学毕业");

        mPrimaryListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return primaryList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(ClassList.this, R.layout.cl_item, null);
                    holder = new ViewHolder();
                    holder.itemText = (TextView) convertView.findViewById(R.id.cl_item_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.itemText.setText(primaryList.get(position));
                return convertView;
            }
        });

        mPrimaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSchoolAndGrade(position);
                jumpToSchoolList();
                Log.i(TAG, "onItemClick: 初中---第" + grade + "schoolType---" + schoolType);
                LogUtils.getInstance().error("grade {}, schoolType {}",grade,schoolType);
            }
        });

    }

    boolean isLoginMode() {
        return  (boolean)SharePreferenceUtil.get(ClassList.this,YPlayConstant.YPLAY_LOGIN_MODE,false);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(isLoginMode()) {
            mTips.setText(R.string.look_for_sameclass);
        } else {
            mTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                    Integer.toString(mLeftCnt)));
        }
    }

    private void jumpToSchoolList() {

        Intent intent = new Intent(ClassList.this, SchoolList.class);
        intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE, mLatitude);
        intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE, mLongitude);
        intent.putExtra(YPlayConstant.YPLAY_SCHOOL_TYPE, schoolType);
        intent.putExtra(YPlayConstant.YPLAY_SCHOOL_GRADE, grade);
        intent.putExtra("activity_setting_class_school", isActivitySetting);
        startActivity(intent);
    }

    private void getSchoolAndGrade(int position) {
        switch (position) {
            case 0:
                schoolType = 1;
                grade = 1;
                break;
            case 1:
                schoolType = 1;
                grade = 2;
                break;
            case 2:
                schoolType = 1;
                grade = 3;
                break;
            case 3:
                schoolType = 1;
                grade = 100;
                break;
            case 4:
                schoolType = 2;
                grade = 1;
                break;
            case 5:
                schoolType = 2;
                grade = 2;
                break;
            case 6:
                schoolType = 2;
                grade = 3;
                break;
            case 7:
                schoolType = 2;
                grade = 100;
                break;
            case 8:
                schoolType = 3;
                grade = 1;
                break;
            case 9:
                schoolType = 3;
                grade = 2;
                break;
            case 10:
                schoolType = 3;
                grade = 3;
                break;
            case 11:
                schoolType = 3;
                grade = 4;
                break;
            case 12:
                schoolType = 3;
                grade = 5;
                break;
            case 13:
                schoolType = 3;
                grade = 100;
                break;
        }

    }

    private static class ViewHolder {
        TextView itemText;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isActivitySetting == 10)
            return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    //查询用户的修改配额
    private void queryUserUpdateLeftCount(int field) {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_QUERY_LEFT_COUNT_URL;
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("field", field);
        nameMap.put("uin", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_UIN, 0));
        nameMap.put("token", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        nameMap.put("ver", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, nameMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 剩余修改次数--- "+ result);
                UserUpdateLeftCountRespond userUpdateLeftCountRespond = GsonUtil.GsonToBean(result,UserUpdateLeftCountRespond.class);
                if (userUpdateLeftCountRespond.getCode() == 0) {
                    mLeftCnt = userUpdateLeftCountRespond.getPayload().getInfo().getLeftCnt();
                    if(isLoginMode()) {
                        mTips.setText(R.string.look_for_sameclass);
                    } else {
                        mTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                                Integer.toString(mLeftCnt)));
                    }
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });
    }

}
