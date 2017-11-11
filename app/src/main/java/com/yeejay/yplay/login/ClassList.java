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

        getWindow().setStatusBarColor(getResources().getColor(R.color.class_color));

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
        mPrimaryListView = (ListView) findViewById(R.id.cl_grade_list);

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

        mPrimaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSchoolAndGrade(position);
                jumpToSchoolList();
                System.out.println("初中---第" + grade + "schoolType---" + schoolType);
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

    private void getSchoolAndGrade(int position){
        switch (position){
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
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (isActivitySetting == 10)
            return super.onKeyDown(keyCode, event);
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
