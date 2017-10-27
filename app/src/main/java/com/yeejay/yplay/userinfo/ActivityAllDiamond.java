package com.yeejay.yplay.userinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAllDiamond extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.aad_list_view)
    ListView aadListView;

    @OnClick(R.id.layout_title_back)
    public void back(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diamond);
        ButterKnife.bind(this);

        layoutTitle.setText("所有钻石");

        aadListView.setAdapter(new BaseAdapter() {
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
                ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(ActivityAllDiamond.this, R.layout.item_afi, null);
                    holder = new ViewHolder();
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.itemAmiText.setText("我可以和他说所有的话");
                } else if (position == 1) {
                    holder.itemAmiText.setText("美妆达人");
                } else if (position == 2) {
                    holder.itemAmiText.setText("垂直剪刀布经常赢");
                } else if (position == 3) {
                    holder.itemAmiText.setText("1");
                } else if (position == 4) {
                    holder.itemAmiText.setText("2");
                } else if (position == 5) {
                    holder.itemAmiText.setText("3");
                }

                return convertView;
            }
        });
    }

    private static class ViewHolder {
        ImageView itemAmiImg;
        TextView itemAmiText;
    }
}
