package com.shi.androidstudy.tablayout;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivityEx extends AppCompatActivity {

    private ViewPager viewPager;
    private List<String> listTitle = new ArrayList<String>();
    private List<Fragment> listFragment = new ArrayList<Fragment>();
    private MyAdapter adapter;
    private TabIndicatorView tabIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ex);
        tabIndicatorView = (TabIndicatorView) findViewById(R.id.tabIndicatorView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        for (int i = 0; i < 8; i++) {
            listTitle.add("标题" + i);
            TestViewFragment testFragment = new TestViewFragment();
            testFragment.initView(listTitle.get(i));
            listFragment.add(testFragment);
        }
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabIndicatorView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                viewPager.setCurrentItem(checkedId);
            }
        });

        tabIndicatorView.initIndicatorBottom(dip2px(70));
        tabIndicatorView.refreshRadioGroup(listTitle);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(final int position) {
                tabIndicatorView.setCurrentSelectItem(position);
            }

            @Override
            public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(final int position) {
            }
        });
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                tabIndicatorView.setCurrentSelectItem(0);
            }
        });
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitle.get(position);
        }
    }

    /**
     * dip转换成px
     */
    public int dip2px(float dpValue) {
        //获取当前设备像素密度
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
