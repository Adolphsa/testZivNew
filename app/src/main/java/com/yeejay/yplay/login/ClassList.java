package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ClassList extends BaseActivity {

    @BindView(R.id.cl_back)
    ImageButton clBack;
    @BindView(R.id.cl_title_ll)
    LinearLayout clTitleLl;
    @BindView(R.id.cl_grade_list)
    MesureListView mPrimaryListView;
    @BindView(R.id.cl_scroll_view)
    LazyScrollView clScrollView;


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
    private TextView mTips;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                    Integer.toString(mLeftCnt)));
        }
    };

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
            }
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            Log.i(TAG, "onCreate: lat---" + mLatitude + ",lon---" + mLongitude);
        }
        mTips = (TextView) findViewById(R.id.tips);
        queryUserUpdateLeftCount(3);

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
                System.out.println("初中---第" + grade + "schoolType---" + schoolType);
            }
        });


//        clScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged(int top, int oldTop) {
//                System.out.println("开始滚动了top---" + top + ",oldTop---" + oldTop);
////                getWindow().setStatusBarColor(Color.TRANSPARENT);
////                clTitleLl.getBackground().setAlpha(1);
//            }
//        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                Integer.toString(mLeftCnt)));
    }

    private void jumpToSchoolList() {
        System.out.println("跳转到学校");
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

        Map<String, Object> leftCountMap = new HashMap<>();
        leftCountMap.put("field", field);
        leftCountMap.put("uin", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_UIN, 0));
        leftCountMap.put("token", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        leftCountMap.put("ver", SharePreferenceUtil.get(ClassList.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUserUpdateCount(leftCountMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserUpdateLeftCountRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserUpdateLeftCountRespond userUpdateLeftCountRespond) {
                        System.out.println("剩余修改次数---" + userUpdateLeftCountRespond.toString());
                        if (userUpdateLeftCountRespond.getCode() == 0) {
                            mLeftCnt = userUpdateLeftCountRespond.getPayload().getInfo().getLeftCnt();
                            mHandler.sendEmptyMessage(mLeftCnt);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
