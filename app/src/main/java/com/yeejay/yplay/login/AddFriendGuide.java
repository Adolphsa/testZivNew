package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ContactsAdapter;
import com.yeejay.yplay.adapter.GuideContactsAdapter;
import com.yeejay.yplay.adapter.GuideSchoolmateAdapter;
import com.yeejay.yplay.adapter.SchoolmateAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.customview.LazyScrollView;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.friend.AddFriends;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
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


    @OnClick(R.id.aafd_back)
    public void back(){
        finish();
    }

    @OnClick(R.id.aafg_enter)
    public void aafgEnter() {
        startActivity(new Intent(AddFriendGuide.this, MainActivity.class));
    }

    private ContactsInfoDao contactsInfoDao;
    private List<ContactsInfo> contactsList;
    private GuideContactsAdapter contactsAdapter;

    private GuideSchoolmateAdapter schoolmateAdapter;//全部同学适配器
    List<GetRecommendsRespond.PayloadBean.FriendsBean> allSchoolMateList;   //全部
    List<Integer> contactsPositionList;
    List<Integer> sameSchoolPositionList;

    int mPageNum = 1;
    int mType = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_guide);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_text_color2));
        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();

        contactsList = new ArrayList<>();
        contactsPositionList = new ArrayList();
        sameSchoolPositionList = new ArrayList<>();
        allSchoolMateList = new ArrayList<>();

        initContactAdapter();
        initSameSchoolAdapter();
        getRecommends(mType,mPageNum);
    }

    //通讯录好友
    private void initContactAdapter(){

        contactsList = contactsInfoDao.queryBuilder()
                .where(ContactsInfoDao.Properties.Uin.gt(1000))
                .list();
        if (contactsList != null && contactsList.size() > 0){
            Log.i(TAG, "initContactAdapter: contactsListSize---" + contactsList.size());
            aafgContacts.setVisibility(View.VISIBLE);

        }else {
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
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);

                            int position = (int) button.getTag();
                            contactsPositionList.add(position);
                            addFriend(contactsList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriendGuide.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                contactsList, contactsPositionList);
        aafgContactsList.setAdapter(contactsAdapter);
    }

    private void initSameSchoolAdapter(){

        //同校好友
        schoolmateAdapter = new GuideSchoolmateAdapter(AddFriendGuide.this,
                null,
                new GuideSchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriendGuide.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();

                            sameSchoolPositionList.add(position);
                            addFriend(allSchoolMateList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriendGuide.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                allSchoolMateList, sameSchoolPositionList);

        aafgSameSchoolList.setAdapter(schoolmateAdapter);
    }

    //拉取同校/通讯录好友
    private void getRecommends(int type, int pageNum) {

        System.out.println("type---" + type);
        Map<String, Object> recommendsMap = new HashMap<>();
        recommendsMap.put("type", 3);
        recommendsMap.put("pageNum", pageNum);
        recommendsMap.put("pageSize", 50);
        recommendsMap.put("uin", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_UIN, 0));
        recommendsMap.put("token", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        recommendsMap.put("ver", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getSchoolmates(recommendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
                        if (getRecommendsRespond.getCode() == 0) {
                            System.out.println("好友列表---" + getRecommendsRespond.toString());
                            List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList =
                                    getRecommendsRespond.getPayload().getFriends();
                            if (friendsBeanList != null && friendsBeanList.size() > 0) {

                                allSchoolMateList.addAll(friendsBeanList);
                                handleSchoolMate(allSchoolMateList);
                                schoolmateAdapter.notifyDataSetChanged();

                            } else {
                                aafgSameSchool.setVisibility(View.GONE);
                            }

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("拉取好友异常---" + e.getMessage());
                        Toast.makeText(AddFriendGuide.this, "网络异常", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //处理同校同学
    private void handleSchoolMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleSchoolMate(), allSchoolMateList.size() = " + allSchoolMateList.size()
                + allSchoolMateList.toString());


//        allSchoolmateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int uin = allSchoolMateList.get(position).getUin();
//                if (NetWorkUtil.isNetWorkAvailable(AddFriendGuide.this)) {
//                    getFriendInfo(uin, view);
//                } else {
//                    Toast.makeText(AddFriendGuide.this, "网络异常", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//        });


    }

    //发送加好友的请求
    private void addFriend(int toUin, int srcType) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("srcType", srcType);
        addFreindMap.put("uin", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(AddFriendGuide.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .addFriend(addFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddFriendRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AddFriendRespond addFriendRespond) {
                        System.out.println("发送加好友请求---" + addFriendRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("发送加好友请求异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
