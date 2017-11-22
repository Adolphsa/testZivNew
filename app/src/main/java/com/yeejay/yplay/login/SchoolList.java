package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SchoolList extends BaseActivity {

    @BindView(R.id.sl_back)
    ImageButton slBack;
    @BindView(R.id.sl_title_ll)
    LinearLayout slTitleLl;
    @BindView(R.id.sl_school_list)
    MesureListView mSchoolListView;
    @BindView(R.id.sl_ptf_refresh)
    PullToRefreshLayout mptfRefresh;
    @BindView(R.id.sl_scroll_view)
    LazyScrollView slScrollView;

    @OnClick(R.id.sl_back)
    public void back() {
        finish();
    }

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;
    int mPageNum = 1;

    List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mDataList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_text_color2));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            schoolType = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_TYPE);
            grade = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_GRADE);
            isActivitySetting = bundle.getInt("activity_setting_class_school");
        }

//        if (mLatitude == 0 || mLongitude == 0) {
//            mLatitude = (double) SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_LATITUDE, 0.0);
//            mLongitude = (double) SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_LONGITUDE, 0.0);
//        }



        mDataList = new ArrayList<>();

        if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)) {
            getSchoolList(mPageNum, 10);
        } else {
            Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }

        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)) {
                    NearestSchoolsRespond.PayloadBean.SchoolsBean schoolInfo = mDataList.get(position);
                    System.out.println("shcoolID---" + schoolInfo.getSchoolId() + ",年级---" + grade);
                    choiceSchool(schoolInfo.getSchoolId(), grade, schoolInfo.getSchool());
                } else {
                    Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mSchoolListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    System.out.println("拦截");
//                    slScrollView.requestDisallowInterceptTouchEvent(false);
//                } else {
//                    // 请求父类不要拦截这个事件或者直接让ScrollView不拦截这个事件，下面的两行代码一样
//                    // lv.getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                    System.out.println("不拦截");
//                    slScrollView.requestDisallowInterceptTouchEvent(true);
//                }
//                return false;
//            }
//        });

        mptfRefresh.setCanRefresh(false);
        mptfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {}

            @Override
            public void loadMore() {
                mPageNum++;
                getSchoolList(mPageNum, 10);
            }
        });
    }


    class SchoolAdapter extends BaseAdapter {

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
            if (convertView == null) {
                convertView = View.inflate(SchoolList.this, R.layout.sl_item, null);
                holder = new ViewHolder();
                holder.slItemName = (TextView) convertView.findViewById(R.id.sl_school_name);
                holder.slItemNumber = (TextView) convertView.findViewById(R.id.sl_school_number);
                holder.slItemAddress = (TextView) convertView.findViewById(R.id.sl_school_address);
                holder.slItemIsJoin = (TextView) convertView.findViewById(R.id.sl_is_join);
                convertView.setTag(holder);
            } else {
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
    private void getSchoolList(int pageNum, int pageSize) {

        getLonLat();

        Map<String, Object> schoolMap = new HashMap<>();

        schoolMap.put("schoolType", schoolType);
        schoolMap.put("latitude", mLatitude);
        schoolMap.put("longitude", mLongitude);
        schoolMap.put("pageNum", pageNum);
        schoolMap.put("pageSize", pageSize);
        schoolMap.put("uin", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0));
        schoolMap.put("token", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        schoolMap.put("ver", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0));

        System.out.println("school---" + schoolType +
                ",latitude---" + mLatitude +
                ",latitude---" + mLongitude +
                ",uin---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0) +
                ",token---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay") +
                ",ver---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0)

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
                        if (nearestSchoolsRespond.getCode() == 0) {
                            showSchoolData(nearestSchoolsRespond);
                        } else {
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
    private void showSchoolData(NearestSchoolsRespond nearestSchoolsRespond) {
        List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mSchoolInfoBeanList = nearestSchoolsRespond.getPayload().getSchools();
        mDataList.addAll(mSchoolInfoBeanList);
        if (mDataList != null && mSchoolInfoBeanList.size() > 0) {
            SchoolAdapter schoolAdapter = new SchoolAdapter();
            mSchoolListView.setAdapter(schoolAdapter);
        }
    }

    //选择学校
    private void choiceSchool(int schoolId, final int grade, final String schooolName) {

        Map<String, Object> schoolMap = new HashMap<>();
        schoolMap.put("schoolId", schoolId);
        schoolMap.put("grade", grade);
        schoolMap.put("uin", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0));
        schoolMap.put("token", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        schoolMap.put("ver", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0));

        if (isActivitySetting == 10){
            schoolMap.put("flag",1);
        }

        YPlayApiManger.getInstance().getZivApiService()
                .choiceSchool(schoolMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("选择学校返回---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0) {
                            if (isActivitySetting == 10) {
                                System.out.println("啦啦啦啦啦啦---");
                                Intent intent = new Intent(SchoolList.this, ActivitySetting.class);
                                intent.putExtra("as_school_type", schoolType);
                                intent.putExtra("as_grade", grade);
                                intent.putExtra("as_school_name", schooolName);
                                SchoolList.this.setResult(202, intent);
                                SchoolList.this.startActivity(intent);
                            } else {
                                startActivity(new Intent(SchoolList.this, ChoiceSex.class));
                                //jumpToWhere();
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


    private void jumpToWhere() {

        //判断性别
        int gender = (int) SharePreferenceUtil.get(SchoolList.this, YPlayConstant.TEMP_GENDER, 0);
        if (gender == 0) {
            startActivity(new Intent(SchoolList.this, ChoiceSex.class));
        }
        //判断基本信息
        String name = (String) SharePreferenceUtil.get(SchoolList.this, YPlayConstant.TEMP_NICK_NAME, "yplay");
        if (TextUtils.isEmpty(name) || name.equals("yplay")) {
            startActivity(new Intent(SchoolList.this, UserInfo.class));
        }

        startActivity(new Intent(SchoolList.this, MainActivity.class));
    }

    String mProvider;//位置提供器
    LocationManager mLocationManager;//位置服务
    Location mLocation;


    //获取当前经纬度
    private void getLonLat(){

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mProvider = judgeProvider(mLocationManager);
        if (Build.VERSION.SDK_INT >= 23 &&
                SchoolList.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                SchoolList.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null){
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
            System.out.println("学校页面当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
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
            Toast.makeText(SchoolList.this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
