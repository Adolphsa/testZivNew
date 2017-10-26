package com.yeejay.yplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.yeejay.yplay.adapter.FragmentAdapter;
import com.yeejay.yplay.answer.FragmentAnswer;
import com.yeejay.yplay.friend.FragmentFriend;
import com.yeejay.yplay.message.FragmentMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_view_pager)
    ViewPager viewPager;

    @BindView(R.id.main_nav_bar)
    SpaceNavigationView spaceNavigationView;

    FragmentAdapter frgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initSpaceNavigationView(savedInstanceState);
        addFragment();
        viewPager.setAdapter(frgAdapter);
        viewPager.setCurrentItem(2);
    }

    private void addFragment() {
        //构造适配器
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new FragmentFriend());
        fragments.add(new FragmentMessage());
        fragments.add(new FragmentAnswer());
        frgAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
    }

    private void initSpaceNavigationView(Bundle savedInstanceState) {
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.userinfo_small));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.userinfo_small));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                System.out.println("中间按钮被点击了");
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                System.out.println("itemIndex---" + itemIndex + ",itemName" + itemName);
                viewPager.setCurrentItem(itemIndex);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }
}
