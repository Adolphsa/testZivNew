package com.yeejay.yplay.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.model.SchoolInfoBean;

import java.util.ArrayList;

public class SchoolList extends AppCompatActivity {

    ListView mSchoolListView;

    ArrayList<SchoolInfoBean> mSchoolInfoBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

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

        mSchoolInfoBeanArrayList = new ArrayList<>();
        SchoolInfoBean sib1 = new SchoolInfoBean("南山小学","1907","深圳","已加入");
        SchoolInfoBean sib2 = new SchoolInfoBean("五道口小学","23456","北京","已加入");
        mSchoolInfoBeanArrayList.add(sib1);
        mSchoolInfoBeanArrayList.add(sib2);
        SchoolAdapter schoolAdapter = new SchoolAdapter();
        mSchoolListView.setAdapter(schoolAdapter);

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
            return mSchoolInfoBeanArrayList.size();
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
            SchoolInfoBean schoolInfoBean;
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
                schoolInfoBean = mSchoolInfoBeanArrayList.get(position);
                holder.slItemName.setText(schoolInfoBean.getSchoolName());
                holder.slItemNumber.setText(schoolInfoBean.getSchoolNumber());
                holder.slItemAddress.setText(schoolInfoBean.getSchoolAddress());
                holder.slItemIsJoin.setText(schoolInfoBean.getIsJoin());
            return convertView;
        }
    }

    static class ViewHolder {
        TextView slItemName;
        TextView slItemNumber;
        TextView slItemAddress;
        TextView slItemIsJoin;
    }
}
