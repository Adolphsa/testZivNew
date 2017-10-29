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

import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.NearestSchoolsRespond;
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

        getSchoolList();

        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(SchoolList.this,ChoiceSex.class));
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
    private void getSchoolList(){
        Map<String,Object> schoolMap = new HashMap<>();

        schoolMap.put("schoolType",schoolType);
        schoolMap.put("latitude",mLatitude);
        schoolMap.put("longitude",mLongitude);
        schoolMap.put("pageNum",1);
        schoolMap.put("pageSize",10);
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
}
