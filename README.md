>  本文接之前写的文章 [TabLayout控件的使用（一）](http://blog.csdn.net/abc6368765/article/details/51755118)

我们在使用TabLayout控件的时候，有时候需要我们去修改下划线的长度，如何修改TabLayout控件的下划线长度呢？查阅官方文档API许久，没有找到合适的API接口，好吧，还是查阅TabLayout控件源码，用反射处理修改一下他的属性吧。

主要用到的一个属性是一个名为mTabStrip的字段属性。

![mTabStrip字段](/screen/1.png)

大概讲述一下思路，我们通过反射获取到TabLayout的class，然后获取其中名字为mTabStrip的属性字段，接着设置该属性可以被访问setAccessible(true)，并获取到该属性对象，然后获取每个TabItem底部的下划线，依次修改其长度以及和TabItem左右两侧的距离，下面具体看代码吧。

```java
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

        setIndicator(this,tabLayout,40,40);
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
            //看源码可知是一个继承自linearLayout叫作SlidingTabStrip的类
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //进行dp到px的数值转换
        int left  = dip2px(leftDip);
        int right = dip2px(rightDip);

        //一次获取每个tabItem的下划线对象并设置该下划线和左右两侧的距离
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
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
```

虽然修改下划线长度的目的实现了，但是有同学反馈修改之后滑动过程有抖动问题，说不定这也是为什么官方没有提供公开API修改下划线长度的原因吧！如果实在无法忍受抖动问题，建议自定义个tablayout或者看看其他第三方tabLayout，网上蛮多的。
如果懒得用其他第三方的控件也懒得自定义，那博主就好人做到底，可以参考 [自定义控件实战<六> TabLayout控件的实现](http://blog.csdn.net/abc6368765/article/details/68490112) 这篇文章。