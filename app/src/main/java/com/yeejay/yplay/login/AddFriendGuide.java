package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.GuideContactsAdapter;
import com.yeejay.yplay.adapter.GuideSchoolmateAdapter;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFriendGuide extends AppCompatActivity {

    private static final String TAG = "AddFriendGuide";

    @BindView(R.id.aafg_enter)
    Button aafgEnter;
    @BindView(R.id.aafd_back)
    ImageButton aafdBack;
    @BindView(R.id.aafg_contacts_list)
    MesureListView aafgContactsList;
    @BindView(R.id.aafg_contacts)
    LinearLayout aafgContacts;
    @BindView(R.id.aafg_same_school_list)
    MesureListView aafgSameSchoolList;
    @BindView(R.id.aafg_same_school)
    LinearLayout aafgSameSchool;
    @BindView(R.id.aafg_list_is_null)
    TextView listIsNUll;
    @BindView(R.id.aafd_friend_number)
    TextView aafdFriendNumber;
    @BindView(R.id.aafd_finish_view)
    LinearLayout aafdFinishView;


    @OnClick(R.id.aafd_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.aafg_enter)
    public void aafgEnter() {
        SharePreferenceUtil.put(AddFriendGuide.this,YPlayConstant.YPLAY_LOGIN_MODE,false);
        startActivity(new Intent(AddFriendGuide.this, MainActivity.class));
    }

    private ContactsInfoDao contactsInfoDao;
    private List<ContactsInfo> contactsList;
    private GuideContactsAdapter contactsAdapter;

    private GuideSchoolmateAdapter schoolmateAdapter;//全部同学适配器
    List<GetRecommendsRespond.PayloadBean.FriendsBean> allSchoolMateList;   //全部

    int mPageNum = 1;
    int mType = 3;
    int sumFriendNumber = 0;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_guide);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_text_color2));
        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();
        phoneNumber = (String) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_PHONE_NUMBER, "");

        contactsList = new ArrayList<>();
        allSchoolMateList = new ArrayList<>();

        initContactAdapter();
        initSameSchoolAdapter();
        getRecommends(mPageNum);
    }

    //通讯录好友
    private void initContactAdapter() {

        contactsList = contactsInfoDao.queryBuilder()
                .where(ContactsInfoDao.Properties.Uin.gt(1000))
                .where(ContactsInfoDao.Properties.Phone.notEq(phoneNumber))
                .list();
        if (contactsList != null && contactsList.size() > 0) {
            Log.i(TAG, "initContactAdapter: contactsListSize---" + contactsList.size());
            aafgContacts.setVisibility(View.VISIBLE);

        } else {
            Log.i(TAG, "initContactAdapter: contactsListSize null");
            aafgContacts.setVisibility(View.GONE);
        }

        //已开通联系人
        contactsAdapter = new GuideContactsAdapter(AddFriendGuide.this,
                null,
                new GuideContactsAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriendGuide.this)) {
                            Button button = (Button) v;
//                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);

                            sumFriendNumber++;

                            int position = (int) button.getTag();
                            addFriend(contactsList.get(position).getUin(), mType);

                            contactsList.remove(position);
                            contactsAdapter.notifyDataSetChanged();

                            bothNull();

                        } else {
                            Toast.makeText(AddFriendGuide.this, R.string.base_no_internet, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                contactsList);
        aafgContactsList.setAdapter(contactsAdapter);
    }

    private void initSameSchoolAdapter() {

        //同校好友
        schoolmateAdapter = new GuideSchoolmateAdapter(AddFriendGuide.this,
                null,
                new GuideSchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriendGuide.this)) {
                            Button button = (Button) v;
//                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);

                            sumFriendNumber++;

                            int position = (int) button.getTag();
                            addFriend(allSchoolMateList.get(position).getUin(), mType);

                            allSchoolMateList.remove(position);
                            schoolmateAdapter.notifyDataSetChanged();

                            bothNull();
                        } else {
                            Toast.makeText(AddFriendGuide.this, R.string.base_no_internet, Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                allSchoolMateList);

        aafgSameSchoolList.setAdapter(schoolmateAdapter);
    }

    //判断同校好友和通讯录好友是否为空
    private void bothNull() {

        if (contactsList.size() == 0){
            aafgContacts.setVisibility(View.GONE);
        }

        if (allSchoolMateList.size() == 0){
            aafgSameSchool.setVisibility(View.GONE);
        }

        if (contactsList.size() == 0 && allSchoolMateList.size() == 0){
            aafdFinishView.setVisibility(View.VISIBLE);
            aafdFriendNumber.setText("已添加了" + sumFriendNumber + "个好友！");
        }else {
            aafdFinishView.setVisibility(View.GONE);
        }
    }

    //拉取同校
    private void getRecommends(int pageNum) {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_GET_SCHOOL_FRIEND_URL;
        Map<String, Object> recommendsMap = new HashMap<>();
        recommendsMap.put("type", mType);
        recommendsMap.put("pageNum", pageNum);
        recommendsMap.put("pageSize", 50);
        recommendsMap.put("uin", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_UIN, 0));
        recommendsMap.put("token", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        recommendsMap.put("ver", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, recommendsMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: school friend--- " + result);
                GetRecommendsRespond getRecommendsRespond = GsonUtil.GsonToBean(result,GetRecommendsRespond.class);
                if (getRecommendsRespond.getCode() == 0) {
                    List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList =
                            getRecommendsRespond.getPayload().getFriends();
                    if (friendsBeanList != null && friendsBeanList.size() > 0) {

                        allSchoolMateList.addAll(friendsBeanList);
                        schoolmateAdapter.notifyDataSetChanged();

                    } else {
                        aafgSameSchool.setVisibility(View.GONE);

                        if (contactsList.size() == 0 && allSchoolMateList.size() == 0) {
                            listIsNUll.setVisibility(View.VISIBLE);
                        } else {
                            listIsNUll.setVisibility(View.GONE);
                        }
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

    //发送加好友的请求
    private void addFriend(int toUin, int srcType) {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_ADD_FRIEND_URL;
        Map<String, Object> addFriendMap = new HashMap<>();
        addFriendMap.put("toUin", toUin);
        addFriendMap.put("srcType", srcType);
        addFriendMap.put("uin", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_UIN, 0));
        addFriendMap.put("token", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFriendMap.put("ver", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, addFriendMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: add friend--- " + result);
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
