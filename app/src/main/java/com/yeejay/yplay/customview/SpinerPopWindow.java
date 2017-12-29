package com.yeejay.yplay.customview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;

import java.util.List;

public class SpinerPopWindow<T> extends PopupWindow {
    private static final String TAG = "SpinerPopWindow";

    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private ListView mListView;
    private List<T> list;
    private MyAdapter  mAdapter;
    private int densityDpi;
    private float density;

    public SpinerPopWindow(Context context, Activity activity, List<T> list, AdapterView.OnItemClickListener clickListener) {
        super(context);
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.list = list;
        init(clickListener);

        DisplayMetrics metric = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Log.d(TAG, "popupwindow, density = " + density + " , densityDpi = " + densityDpi);
    }

    private void init(AdapterView.OnItemClickListener clickListener){
        View view = inflater.inflate(R.layout.spinner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mAdapter = new MyAdapter();
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(clickListener);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.spinner_dropdown_layout_filter_classmat_type, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.spinner_title);
                holder.tvIcon = (ImageView) convertView.findViewById(R.id.spinner_icon);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).toString());
            holder.tvIcon.setVisibility(View.INVISIBLE);
            SharedPreferences sharedFilter = context.getSharedPreferences("preferences_class_filter",
                    Context.MODE_PRIVATE);
            int selectOpn = sharedFilter.getInt("position", 0);
            if(position == selectOpn) {
                holder.tvIcon.setVisibility(View.VISIBLE);
            }


            ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                    (int)(30 * density));//设置宽度和高度
            convertView.setLayoutParams(params);
            return convertView;
        }
    }

    private class ViewHolder{
        private TextView tvName;
        private ImageView tvIcon;
    }
}