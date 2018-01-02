package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
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
    private static final String TAG = "SchoolList";

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
    @BindView(R.id.sl_search)
    ImageView btnSearch;
    @BindView(R.id.sl_rl)
    RelativeLayout rlLayout;
    @BindView(R.id.sl_search_cancel)
    Button btnSearchCancel;
    @BindView(R.id.sl_search_result)
    TextView resultTip;
    @BindView(R.id.emptyview)
    View emptyView;

    @OnClick(R.id.sl_search)
    public void search() {
        btnSearch.setVisibility(View.GONE);
        rlLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.sl_search_cancel)
    public void cancelSearch() {
        Log.d(TAG, "cancel search, isInputMethodShow() = " + isInputMethodShow());
        if(isInputMethodShow()) {
            closeInputMethod(mActivity);
        } else {
            mSearchFlag = 0;
            mHandler.sendEmptyMessage(MSG_CODE_HIDE_RESULT_TIP);

            //清空搜索结果后，重新拉一次学校数据，从第一页开始拉;
            mDataList.clear();
            mPageNum  = 1;
            getSchoolList(mPageNum, 10);
        }
    }

    @OnClick(R.id.sl_back)
    public void back() {
        finish();
    }

    private static final int MSG_CODE_LEFT_COUNT = 0;
    private static final int MSG_CODE_HIDE_RESULT_TIP = 1;
    private static final int MSG_CODE_SHOW_RESULT_TIP = 2;

    double mLatitude;
    double mLongitude;
    int schoolType;
    int grade;
    int isActivitySetting;
    int mPageNum = 1;
    private int mSearchPageNum = 1;
    private int mLeftCnt;
    private int mSearchFlag = 0;
    private TextView mTips;
    private EditText searchEdit;
    private Activity mActivity;

    List<NearestSchoolsRespond.PayloadBean.SchoolsBean> mDataList;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "msg.what = " + msg.what);
            switch (msg.what) {
                case MSG_CODE_LEFT_COUNT :
                    if(isLoginMode()) {
                        mTips.setText(R.string.look_for_classmates);
                    } else {
                        mTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                                Integer.toString(mLeftCnt)));
                    }

                    break;

                case MSG_CODE_HIDE_RESULT_TIP :
                    resultTip.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.VISIBLE);
                    rlLayout.setVisibility(View.GONE);

                    break;

                case MSG_CODE_SHOW_RESULT_TIP :
                    resultTip.setVisibility(View.VISIBLE);
                    btnSearchCancel.setEnabled(true);

                    break;
                default:
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        ButterKnife.bind(this);
        mActivity = this;

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_text_color2));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLatitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LATITUDE);
            mLongitude = bundle.getDouble(YPlayConstant.YPLAY_FIRST_LONGITUDE);
            schoolType = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_TYPE);
            grade = bundle.getInt(YPlayConstant.YPLAY_SCHOOL_GRADE);
            isActivitySetting = bundle.getInt("activity_setting_class_school");
        }

        mTips = (TextView) findViewById(R.id.tips);
        if(isLoginMode()) {
            mTips.setText(R.string.look_for_classmates);
        }
        searchEdit = (EditText) findViewById(R.id.sl_search_edit);
        queryUserUpdateLeftCount(3);

        mDataList = new ArrayList<>();

        if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)) {
            getSchoolList(mPageNum, 10);
        } else {
            Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }

        mSchoolListView.setEmptyView(emptyView);
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
                mSearchPageNum++;
                if(mSearchFlag == 1) {
                    searchSchoolList(mSearchPageNum, 10);
                } else {
                    getSchoolList(mPageNum, 10);
                }
            }
        });

       searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "actionId = " + actionId);
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    mSearchFlag = 1;
                    mHandler.sendEmptyMessage(MSG_CODE_SHOW_RESULT_TIP);

                    if (NetWorkUtil.isNetWorkAvailable(SchoolList.this)) {
                        //先清空之前根据定位拉取的学校数据；
                        mDataList.clear();
                        searchSchoolList(mSearchPageNum, 10);
                    } else {
                        Toast.makeText(SchoolList.this, "网络不可用", Toast.LENGTH_SHORT).show();
                    }

                    closeInputMethod(mActivity);
                    return false;
                }
                return false;
            }
        });
    }

    boolean isLoginMode() {
        SharedPreferences pref = YplayApplication.getContext().getSharedPreferences("loginMode",MODE_PRIVATE);
        return pref.getBoolean("isLogin", false);
    }

    private void closeInputMethod(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "close inputmethod exception!");
        }
    }

    private boolean isInputMethodShow() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
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

    //获取学校搜索结果列表
    private void searchSchoolList(int pageNum, int pageSize) {
        Map<String, Object> schoolMap = new HashMap<>();

        Log.d(TAG, "searchEdit content = " + searchEdit.getText().toString().trim() +
                " , schoolType = " + schoolType);
        schoolMap.put("schoolType", schoolType);
        schoolMap.put("schoolName", searchEdit.getText().toString().trim());
        schoolMap.put("pageNum", pageNum);
        schoolMap.put("pageSize", pageSize);
        schoolMap.put("uin", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0));
        schoolMap.put("token", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        schoolMap.put("ver", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0));

        Log.d(TAG,"school---" + schoolType +
                ",latitude---" + mLatitude +
                ",latitude---" + mLongitude +
                ",uin---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0) +
                ",token---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay") +
                ",ver---" + SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0)

        );

        YPlayApiManger.getInstance().getZivApiService()
                .getSearchScools(schoolMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NearestSchoolsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull NearestSchoolsRespond nearestSchoolsRespond) {
                        Log.d(TAG, "搜索得到的学校列表信息---" + nearestSchoolsRespond.toString());
                        if (nearestSchoolsRespond.getCode() == 0) {
                            showSchoolData(nearestSchoolsRespond);
                        } else {
                            mSchoolListView.setAdapter(null);
                            Log.d(TAG,"搜索学校失败");
                        }
                        mptfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "搜索学校异常---" + e.getMessage());
                        mptfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取学校列表
    private void getSchoolList(int pageNum, int pageSize) {

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

    //查询用户的修改配额
    private void queryUserUpdateLeftCount(int field) {

        Map<String, Object> leftCountMap = new HashMap<>();
        leftCountMap.put("field", field);
        leftCountMap.put("uin", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_UIN, 0));
        leftCountMap.put("token", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        leftCountMap.put("ver", SharePreferenceUtil.get(SchoolList.this, YPlayConstant.YPLAY_VER, 0));
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
                            mHandler.sendEmptyMessage(MSG_CODE_LEFT_COUNT);
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
