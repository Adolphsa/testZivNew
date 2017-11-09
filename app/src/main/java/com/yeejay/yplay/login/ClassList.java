package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;

public class ClassList extends AppCompatActivity {

    ListView mPrimaryListView;
    ListView mHighListView;
    ListView mCollegeListView;

    ArrayList<String> primaryList;
    ArrayList<String> highList;
    ArrayList<String> collegeList;

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            isActivitySetting = bundle.getInt("activity_setting_school");
            System.out.println("activity_setting_school" + isActivitySetting);
        }
        if (mLatitude == 0 || mLongitude == 0){

            String tempLat = (String) SharePreferenceUtil.get(ClassList.this,YPlayConstant.YPLAY_LATITUDE,"");
            String tempLon = (String) SharePreferenceUtil.get(ClassList.this,YPlayConstant.YPLAY_LONGITUDE,"");
            System.out.println("未读---" + tempLat + ",---" + tempLon);
            if (!TextUtils.isEmpty(tempLat)){
                mLatitude = Double.valueOf(tempLat);
            }
            if (!TextUtils.isEmpty(tempLon)){
                mLongitude  = Double.valueOf(tempLon);
            }
        }
        mPrimaryListView = (ListView) findViewById(R.id.cl_primary_list);
        mHighListView = (ListView) findViewById(R.id.cl_high_list);
        mCollegeListView = (ListView) findViewById(R.id.cl_college_list);

        primaryList = new ArrayList<>();
        primaryList.add("初一");
        primaryList.add("初二");
        primaryList.add("初三");
        primaryList.add("毕业");

        highList = new ArrayList<>();
        highList.add("高一");
        highList.add("高二");
        highList.add("高三");
        highList.add("毕业");

        collegeList = new ArrayList<>();
        collegeList.add("大一");
        collegeList.add("大二");
        collegeList.add("大三");
        collegeList.add("大四");
        collegeList.add("大五");
        collegeList.add("毕业");

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
                if (convertView == null){
                    convertView = View.inflate(ClassList.this,R.layout.cl_item,null);
                    holder = new ViewHolder();
                    holder.itemText = (TextView) convertView.findViewById(R.id.cl_item_title);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.itemText.setText(primaryList.get(position));
                return convertView;
            }
        });
        mHighListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return highList.size();
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
                if (convertView == null){
                    convertView = View.inflate(ClassList.this,R.layout.cl_item,null);
                    holder = new ViewHolder();
                    holder.itemText = (TextView) convertView.findViewById(R.id.cl_item_title);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.itemText.setText(highList.get(position));
                return convertView;
            }
        });
        mCollegeListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return collegeList.size();
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
                if (convertView == null){
                    convertView = View.inflate(ClassList.this,R.layout.cl_item,null);
                    holder = new ViewHolder();
                    holder.itemText = (TextView) convertView.findViewById(R.id.cl_item_title);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.itemText.setText(collegeList.get(position));
                return convertView;
            }
        });


        mPrimaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schoolType = 1;
                if (position == 3){
                    grade = 100;
                }else {
                    grade = position + 1;
                }
                jumpToSchoolList();
                System.out.println("初中---第" + grade + "schoolType---" + schoolType);
            }
        });

        mHighListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schoolType = 2;
                if (position == 3){
                    grade = 100;
                }else {
                    grade = position + 1;
                }
                jumpToSchoolList();
                System.out.println("高中---第" + grade + "schoolType---" + schoolType);
            }
        });

        mCollegeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schoolType = 3;
                if (position == 5){
                    grade = 100;
                }else {
                    grade = position + 1;
                }
                System.out.println("大学年级---第" + grade + ",schoolType---" + schoolType);
                jumpToSchoolList();
            }
        });
    }



    private void jumpToSchoolList(){
        Intent intent = new Intent(ClassList.this,SchoolList.class);
        intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE,mLatitude);
        intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE,mLongitude);
        intent.putExtra(YPlayConstant.YPLAY_SCHOOL_TYPE,schoolType);
        intent.putExtra(YPlayConstant.YPLAY_SCHOOL_GRADE,grade);
        intent.putExtra("activity_setting_class_school",isActivitySetting);
        startActivity(intent);
    }

    private static class ViewHolder {
        TextView itemText;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (isActivitySetting == 10)
            return super.onKeyDown(keyCode, event);
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
