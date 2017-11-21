package com.yeejay.yplay.login;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassList extends AppCompatActivity {

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

    ArrayList<String> primaryList;

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.class_color));

        getLonLat();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isActivitySetting = bundle.getInt("activity_setting_school");
            System.out.println("activity_setting_school" + isActivitySetting);
            if (isActivitySetting == 10){
                clBack.setVisibility(View.VISIBLE);
            }
        }

//        if (mLatitude == 0 || mLongitude == 0) {
//            Toast.makeText(ClassList.this,"请打开位置权限",Toast.LENGTH_LONG).show();
//        }

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

    // 5.0版本以上
    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    String mProvider;//位置提供器
    LocationManager mLocationManager;//位置服务
    Location mLocation;


    //获取当前经纬度
    private void getLonLat(){

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mProvider = judgeProvider(mLocationManager);
        if (Build.VERSION.SDK_INT >= 23 &&
                ClassList.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ClassList.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null){
            System.out.println("当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
        }
    }

    //判断是否有可用的内容提供者
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }else{
            System.out.println("没有可用的位置提供器");
        }
        return null;
    }
}
