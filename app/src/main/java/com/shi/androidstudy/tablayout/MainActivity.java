package com.shi.androidstudy.tablayout;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<String> list = new ArrayList<String>();
    private List<Fragment> listFragment = new ArrayList<Fragment>();
    private MyAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        for (int i=0; i<8; i++){
            list.add("标题"+i);
            TestViewFragment testFragment = new TestViewFragment();
            testFragment.initView(list.get(i));
            listFragment.add(testFragment);
        }

        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        setIndicator(this,tabLayout,10,10);
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
            return list.get(position );
        }
    }

    /**
     * @author SHI
     * @time 2017/3/29 16:25
     * @param context  设备上下文
     * @param tabs     需要设置下划线长度的TabLayout
     * @param leftDip  每个TabItem下划线和TabItem左侧的距离,单位dp
     * @param rightDip 每个TabItem下划线和TabItem右侧的距离,单位dp
     */
    public void setIndicator(Context context, TabLayout tabs, int leftDip, int rightDip) {
        //通过反射获取tabLayout的类对象
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            //获取tabLayout一个名叫mTabStrip的属性字段
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //设置tabStrip属性可以被调用，如果不设置为true，该属性为private时候，会报错
        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            //获取tabStrip这个字段对应的属性对象，
            //这里可以看出，tabayout底部的下划线应该是一个linearLayout或者linearLayout的子类
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //进行dp到px的数值转换
        float left  =  dip2px(leftDip);
        float right = dip2px(rightDip);

        //一次获取每个tabItem的下划线对象并设置该下划线和左右两侧的距离
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = (int) left;
            params.rightMargin = (int) right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
    /**
     * dip转换成px
     */
    public float dip2px(float dpValue) {
        //获取当前设备像素密度
        final float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale);
    }

}
