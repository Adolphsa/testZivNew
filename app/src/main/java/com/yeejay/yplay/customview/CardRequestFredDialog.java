package com.yeejay.yplay.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名片dialog
 * Created by Administrator on 2017/11/17.
 */

public class CardRequestFredDialog extends Dialog {
    private static final String TAG = "CardRequestFredDialog";

    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int STATUS_FRIEND = 1;

    ImageView backView;
    TextView nameView;
    ImageView genderView;
    TextView diamondNumView;
    TextView addFriendView;
    TextView schoolView;
//    TextView gradeView;
    ListView aadListView;
    PullToRefreshLayout aadPtfRefresh;

    private int mPageNum = 1;
    private int mPageSize = 3;

    private View.OnClickListener addFriendListener;

    private Context context;
    private UserInfoResponde.PayloadBean mPayloadBean;

    public CardRequestFredDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CardRequestFredDialog(@NonNull Context context, int themeResId,
                         UserInfoResponde.PayloadBean payloadBean) {
        super(context, themeResId);
        this.context = context;
        this.mPayloadBean = payloadBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_layout_peer_friends_details);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initCardDialog();
        getUserDiamondInfo(mPayloadBean.getInfo().getUin(), mPageNum, mPageSize);
    }

    private void initCardDialog(){
        backView = (ImageView) findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        nameView = (TextView) findViewById(R.id.lui_name);
        genderView = (ImageView) findViewById(R.id.lui_gender);
        diamondNumView = (TextView) findViewById(R.id.diamond_num);
        addFriendView = (TextView) findViewById(R.id.add_friend);
        schoolView = (TextView)findViewById(R.id.school);
//        gradeView = (TextView) findViewById(R.id.grade);
        aadListView = (ListView) findViewById(R.id.aad_list_view);
        aadPtfRefresh = (PullToRefreshLayout) findViewById(R.id.aad_ptf_refresh);

        final UserInfoResponde.PayloadBean.InfoBean infoBean = mPayloadBean.getInfo();
        //状态
        int status = mPayloadBean.getStatus();//1表示已经加为好友, 否则为接受请求状态；
        if (status == STATUS_FRIEND) {
            addFriendView.setEnabled(false);
        } else {
            addFriendView.setEnabled(true);
        }

        nameView.setText(infoBean.getNickName());

        if (infoBean.getGender() == GENDER_MALE) {
            genderView.setImageResource(R.drawable.feeds_boy);
        } else if (infoBean.getGender() == GENDER_FEMALE) {
            genderView.setImageResource(R.drawable.feeds_girl);
        }
        diamondNumView.setText(String.valueOf(infoBean.getGemCnt()));
        schoolView.setText(infoBean.getSchoolName() + "·" + FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade()));

        addFriendView.setOnClickListener(addFriendListener);
    }

    public void setAddFriendListener(View.OnClickListener addFriendListener) {
        this.addFriendListener = addFriendListener;
    }

    private void initDiamondList(final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList){

        aadListView.setAdapter(new BaseAdapter() {
            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public int getCount() {
                return tempList.size();
            }

            @Override
            public Object getItem(int position) {
                return tempList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CardRequestFredDialog.ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_diamond_details, null);
                    holder = new CardRequestFredDialog.ViewHolder();
                    holder.itemAmiIndex = (TextView) convertView.findViewById(R.id.item_diamond_sort);
                    holder.itemAmiMsg = (TextView) convertView.findViewById(R.id.item_diamond_msg);
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.item_diamond_lable);

                    convertView.setTag(holder);
                } else {
                    holder = (CardRequestFredDialog.ViewHolder) convertView.getTag();
                }
                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean = tempList.get(position);

                holder.itemAmiIndex.setText(String.valueOf(position+1));

                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(context).load(url).into(holder.itemAmiImg);
                }else {
                    holder.itemAmiImg.setImageResource(R.drawable.diamond_null);
                }
                holder.itemAmiMsg.setText(statsBean.getQtext());

                if(position == 0) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.gold_medal);
                } else if (position == 1) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.silver_medal);
                } else if (position == 2) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.bronze_medal);
                }

                return convertView;
            }
        });
    }

    //获取钻石信息
    private void getUserDiamondInfo(int userUin,int pageNum, int pageSize){

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_GET_DIAMOND_URL;
        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin",userUin);
        diamondInfoMap.put("pageNum",pageNum);
        diamondInfoMap.put("pageSize",pageSize);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, diamondInfoMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 用户钻石信息---" + result);
                UsersDiamondInfoRespond usersDiamondInfoRespond = GsonUtil.GsonToBean(result, UsersDiamondInfoRespond.class);
                if (usersDiamondInfoRespond.getCode() == 0){
                    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = usersDiamondInfoRespond.getPayload().getStats();
                    initDiamondList(tempList);
                }
            }

            @Override
            public void onTimeOut() {}

            @Override
            public void onError() {

            }
        });
    }

    private static class ViewHolder {
        TextView itemAmiIndex;
        TextView itemAmiMsg;
        ImageView itemAmiImg;
    }

}