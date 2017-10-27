package com.yeejay.yplay.userinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMyInfo extends AppCompatActivity {

    @BindView(R.id.my_tv_back)
    Button myTvBack;
    @BindView(R.id.my_tv_title)
    TextView myTvTitle;
    @BindView(R.id.my_ib_logo)
    ImageButton myIbLogo;
    @BindView(R.id.ami_tv_school_name)
    TextView amiTvSchoolName;
    @BindView(R.id.ami_item_header_img)
    ImageView amiItemHeaderImg;
    @BindView(R.id.ami_tv_name)
    TextView amiTvName;
    @BindView(R.id.ami_tv_name2)
    TextView amiTvName2;
    @BindView(R.id.ami_small_img)
    ImageView amiSmallImg;
    @BindView(R.id.ami_tv_is_graduate)
    TextView amiTvIsGraduate;
    @BindView(R.id.ami_btn_setting)
    ImageButton amiBtnSetting;
    @BindView(R.id.afi_tv_friends_number)
    TextView afiTvFriendsNumber;
    @BindView(R.id.afi_tv_diamond_number)
    TextView afiTvDiamondNumber;
    @BindView(R.id.ami_diamond_list_view)
    ListView amiDiamondListView;
    @BindView(R.id.ami_friends_list_view)
    ListView amiFriendsListView;

    @OnClick(R.id.my_tv_back)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.my_ib_logo)
    public void logo(View view){
        startActivity(new Intent(ActivityMyInfo.this,ActivityAboutOur.class));
    }

    @OnClick(R.id.ami_btn_setting)
    public void setting(View view){
        startActivity(new Intent(ActivityMyInfo.this,ActivitySetting.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);

        initDiamondList();
        initFriendsList();
    }

    private void initDiamondList(){

        View footView = View.inflate(ActivityMyInfo.this,R.layout.item_af_listview_foot,null);
        amiDiamondListView.addFooterView(footView);
        amiDiamondListView.setAdapter(new BaseAdapter() {
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
                    convertView = View.inflate(ActivityMyInfo.this,R.layout.item_afi,null);
                    holder = new ViewHolder();
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position == 0){
                    holder.itemAmiText.setText("我可以和他说所有的话");
                }else if (position == 1){
                    holder.itemAmiText.setText("美妆达人");
                }else if (position == 2){
                    holder.itemAmiText.setText("垂直剪刀布经常赢");
                }
                return convertView;
            }
        });
        amiDiamondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    startActivity(new Intent(ActivityMyInfo.this,ActivityAllDiamond.class));
                }
            }
        });
    }

    private void initFriendsList(){

        amiFriendsListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 6;
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
                viewHolderFriends holder;
                if (convertView == null){
                    convertView = View.inflate(ActivityMyInfo.this,R.layout.item_all_diamond,null);
                    holder = new viewHolderFriends();
                    holder.itemAadImg = (ImageView) convertView.findViewById(R.id.item_aad_img);
                    holder.itemAadText1 = (TextView) convertView.findViewById(R.id.item_aad_text1);
                    holder.itemAadText2 = (TextView) convertView.findViewById(R.id.item_aad_text2);
                    convertView.setTag(holder);
                }else {
                    holder = (viewHolderFriends) convertView.getTag();
                }
                return convertView;
            }
        });
        amiFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showNormalDialog();
            }
        });
    }

    //显示对话框
    private void showNormalDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ActivityMyInfo.this);
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
        ImageView itemAmiImg;
        TextView itemAmiText;
    }

    private static class viewHolderFriends{
        ImageView itemAadImg;
        TextView itemAadText1;
        TextView itemAadText2;
    }
}
