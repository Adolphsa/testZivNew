package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.userinfo.ActivitySetting;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SchoolList extends AppCompatActivity {

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;
    int mPageNum = 1;


    ListView mSchoolListView;
    PullToRefreshLayout mptfRefresh;

    //
    List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            schoolType = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_TYPE);
            grade = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_GRADE);
            isActivitySetting = bundle.getInt("activity_setting_class_school");
        }

        Button btnBack = (Button) findViewById(R.id.layout_title_back);
        TextView title = (TextView) findViewById(R.id.layout_title);
        mSchoolListView = (ListView) findViewById(R.id.sl_school_list);
        mptfRefresh = (PullToRefreshLayout) findViewById(R.id.sl_ptf_refresh);

        mDataList = new ArrayList<>();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("选择你的学校");

        if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)){
            getSchoolList(mPageNum,10);
        }else {
            Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }


        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)){
                    NearestSchoolsRespond.PayloadBean.SchoolsBean  schoolInfo = mDataList.get(position);
                    System.out.println("shcoolID---" + schoolInfo.getSchoolId() + ",年级---" + grade);
                    choiceSchool(schoolInfo.getSchoolId(),grade,schoolInfo.getSchool());
                }else {
                    Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mptfRefresh.setCanRefresh(false);
        mptfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                mPageNum++;
                getSchoolList(mPageNum,10);
            }
        });
    }


    class SchoolAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            NearestSchoolsRespond.PayloadBean.SchoolsBean schoolInfoBean;
            if (convertView == null){
                convertView = View.inflate(SchoolList.this,R.layout.sl_item,null);
                holder = new ViewHolder();
                holder.slItemName = (TextView) convertView.findViewById(R.id.sl_school_name);
                holder.slItemNumber = (TextView) convertView.findViewById(R.id.sl_school_number);
                holder.slItemAddress = (TextView) convertView.findViewById(R.id.sl_school_address);
                holder.slItemIsJoin = (TextView) convertView.findViewById(R.id.sl_is_join);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
                schoolInfoBean = mDataList.get(position);
                holder.slItemName.setText(schoolInfoBean.getSchool());
                holder.slItemNumber.setText(String.valueOf(schoolInfoBean.getMemberCnt()));
                StringBuilder str1 = new StringBuilder(schoolInfoBean.getProvince());
                str1.append(schoolInfoBean.getCity());
                holder.slItemAddress.setText(str1);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView slItemName;
        TextView slItemNumber;
        TextView slItemAddress;
        TextView slItemIsJoin;
    }

    //获取学校列表
    private void getSchoolList(int pageNum,int pageSize){
        Map<String,Object> schoolMap = new HashMap<>();

        schoolMap.put("schoolType",schoolType);
        schoolMap.put("latitude",mLatitude);
        schoolMap.put("longitude",mLongitude);
        schoolMap.put("pageNum",pageNum);
        schoolMap.put("pageSize",pageSize);
        schoolMap.put("uin", SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_UIN,0));
        schoolMap.put("token",SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        schoolMap.put("ver",SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_VER,0));

        System.out.println("school---" + schoolType +
                            ",latitude---" + mLatitude +
                ",latitude---" + mLongitude +
                ",uin---" + SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_UIN,0) +
                ",token---" + SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_TOKEN,"yplay") +
                ",ver---" + SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_VER,0)

        );

        YPlayApiManger.getInstance().getZivApiService()
                .getNearestScools(schoolMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NearestSchoolsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull NearestSchoolsRespond nearestSchoolsRespond) {
                        System.out.println("学校列表---" + nearestSchoolsRespond.toString());
                        if (nearestSchoolsRespond.getCode() == 0){
                            showSchoolData(nearestSchoolsRespond);
                        }else {
                            System.out.println("获取学校失败");
                        }
                        mptfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取学校异常---" + e.getMessage());
                        mptfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //显示学校数据
    private void showSchoolData(NearestSchoolsRespond nearestSchoolsRespond){
        List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mSchoolInfoBeanList = nearestSchoolsRespond.getPayload().getSchools();
        mDataList.addAll(mSchoolInfoBeanList);
        if (mDataList != null && mSchoolInfoBeanList.size() > 0){
            SchoolAdapter schoolAdapter = new SchoolAdapter();
            mSchoolListView.setAdapter(schoolAdapter);
        }
    }

    //选择学校
    private void choiceSchool(int schoolId, final int grade, final String schooolName){

        Map<String,Object> schoolMap = new HashMap<>();
        schoolMap.put("schoolId",schoolId);
        schoolMap.put("grade",grade);
        schoolMap.put("uin", SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_UIN,0));
        schoolMap.put("token",SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        schoolMap.put("ver",SharePreferenceUtil.get(SchoolList.this,YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .choiceSchool(schoolMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("选择学校返回---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0){
                            if (isActivitySetting == 10){
                                System.out.println("啦啦啦啦啦啦---");
                                Intent intent = new Intent(SchoolList.this, ActivitySetting.class);
                                intent.putExtra("as_school_type",schoolType);
                                intent.putExtra("as_grade",grade);
                                intent.putExtra("as_school_name",schooolName);
                                SchoolList.this.setResult(202,intent);
                                SchoolList.this.startActivity(intent);
                            }else {
                                //startActivity(new Intent(SchoolList.this,ChoiceSex.class));
                                jumpToWhere();
                            }

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("选择学校返回错误---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void jumpToWhere(){

        //判断性别
        int gender = (int)SharePreferenceUtil.get(SchoolList.this,YPlayConstant.TEMP_GENDER,0);
        if (gender == 0){
            startActivity(new Intent(SchoolList.this,ChoiceSex.class));
        }
        //判断基本信息
        String name = (String) SharePreferenceUtil.get(SchoolList.this,YPlayConstant.TEMP_NICK_NAME,"yplay");
        if (TextUtils.isEmpty(name) || name.equals("yplay")){
            startActivity(new Intent(SchoolList.this,UserInfo.class));
        }

        startActivity(new Intent(SchoolList.this, MainActivity.class));
    }
}
