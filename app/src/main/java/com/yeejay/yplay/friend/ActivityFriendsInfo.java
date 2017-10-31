package com.yeejay.yplay.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityFriendsInfo extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.afi_tv_school_name)
    TextView afiTvSchoolName;
    @BindView(R.id.afi_item_header_img)
    ImageView afiItemHeaderImg;
    @BindView(R.id.afi_tv_name)
    TextView afiTvName;
    @BindView(R.id.afi_tv_name2)
    TextView afiTvName2;
    @BindView(R.id.afi_small_img)
    ImageView afiSmallImg;
    @BindView(R.id.afi_tv_is_graduate)
    TextView afiTvIsGraduate;
    @BindView(R.id.afi_btn_friends_state)
    Button afiBtnFriendsState;
    @BindView(R.id.afi_tv_friends_number)
    TextView afiTvFriendsNumber;
    @BindView(R.id.afi_tv_diamond_number)
    TextView afiTvDiamondNumber;
    @BindView(R.id.afi_list_view)
    ListView afiListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_info);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String headerImg =  bundle.getString("yplay_friend_header_img");
        String schoolName =  bundle.getString("yplay_friend_school_name");
        String friendName =  bundle.getString("yplay_friend_name");
        String friendSchoolGrade=  bundle.getString("yplay_friend_school_grade");

        Picasso.with(this).load(headerImg).into(afiItemHeaderImg);
        afiTvSchoolName.setText(schoolName);
        afiTvName.setText(friendName);
        afiTvIsGraduate.setText(friendSchoolGrade);
        initTitle();
    }

    private void initView(){

    }

    private void initTitle(){
        layoutTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutTitle.setText("好友");

        afiListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 3;
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
                    convertView = View.inflate(ActivityFriendsInfo.this,R.layout.item_afi,null);
                    holder = new ViewHolder();
                    holder.itemAfiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAfiText = (TextView) convertView.findViewById(R.id.afi_item_text);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position == 0){
                    holder.itemAfiText.setText("我可以和他说所有的话");
                }else if (position == 1){
                    holder.itemAfiText.setText("美妆达人");
                }else if (position == 2){
                    holder.itemAfiText.setText("垂直剪刀布经常赢");
                }
                return convertView;
            }
        });
        afiBtnFriendsState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });
    }


    private void showNormalDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ActivityFriendsInfo.this);
        normalDialog.setMessage("解除好友关系后，你也会在对方的好友列表中消失");
        normalDialog.setPositiveButton("解除关系",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private static class ViewHolder{
        ImageView itemAfiImg;
        TextView itemAfiText;
    }
}



