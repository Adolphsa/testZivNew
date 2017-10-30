package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

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

    ListView mSchoolListView;

    List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mSchoolInfoBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        Bundle bundle = getIntent().getExtras();
        mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
        mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
        schoolType = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_TYPE);
        grade = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_GRADE);

        Button btnBack = (Button) findViewById(R.id.layout_title_back);
        TextView title = (TextView) findViewById(R.id.layout_title);
        mSchoolListView = (ListView) findViewById(R.id.sl_school_list);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("选择你的学校");

        if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)){
            getSchoolList(1,10);
        }else {
            Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }


        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)){
                    NearestSchoolsRespond.PayloadBean.SchoolsBean  schoolInfo = mSchoolInfoBeanList.get(position);
                    System.out.println("shcoolID---" + schoolInfo.getSchoolId() + ",年级---" + grade);
                    choiceSchool(schoolInfo.getSchoolId(),grade);
                }else {
                    Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class SchoolAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mSchoolInfoBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSchoolInfoBeanList.get(position);
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
                schoolInfoBean = mSchoolInfoBeanList.get(position);
                holder.slItemName.setText(schoolInfoBean.getSchool());
                holder.slItemNumber.setText(String.valueOf(schoolInfoBean.getMemberCnt()));
                holder.slItemAddress.setText(schoolInfoBean.getCity());
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

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取学校异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //显示学校数据
    private void showSchoolData(NearestSchoolsRespond nearestSchoolsRespond){
        mSchoolInfoBeanList = nearestSchoolsRespond.getPayload().getSchools();
        if (mSchoolInfoBeanList.size() > 0){
            SchoolAdapter schoolAdapter = new SchoolAdapter();
            mSchoolListView.setAdapter(schoolAdapter);
        }
    }

    //选择学校
    private void choiceSchool(int schoolId,int grade){

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
                            startActivity(new Intent(SchoolList.this,ChoiceSex.class));
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
}
