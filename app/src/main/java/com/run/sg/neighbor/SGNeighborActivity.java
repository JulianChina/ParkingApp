package com.run.sg.neighbor;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.run.sg.amap3d.R;

import java.util.ArrayList;

/**
 * Created by yq on 2017/6/11.
 */
public class SGNeighborActivity extends FragmentActivity
        implements ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private ArrayList<SGSortTab> mSortTabList = new ArrayList<>();
    private ViewPager mViewPager;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_neighbor_activity_content);

        initSortTabList();
        initActionBar();
    }

    private void initSortTabList(){
        mSortTabList.add(new SGSortTab(
                this.getResources().getString(R.string.sort_by_price_text),SGSortPriceFragment.class));
        mSortTabList.add(new SGSortTab(
                this.getResources().getString(R.string.sort_by_distance_text),SGSortDistanceFragment.class));
    }

    private void initActionBar(){
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowTitleEnabled(true);//设置Ttile 可见
        mActionBar.setDisplayHomeAsUpEnabled(true);//设置返回键可见并能响应
        mActionBar.setDisplayShowHomeEnabled(false);//设置Home图标不显示
        mActionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));//设置返回键图标

        for (SGSortTab tab : mSortTabList){
            ActionBar.Tab t = mActionBar.newTab();
            t.setText(tab.getTitle());
            t.setTabListener(this);
            mActionBar.addTab(t);
        }

        mViewPager = (ViewPager) findViewById(R.id.sort_viewpager);
        mViewPager.setAdapter(new SGSortFragmentPagerAdapter(getSupportFragmentManager(),mSortTabList));
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**********implement TabListener**********/
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        if (mViewPager != null){
            mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    /**********implements OnPageChangeListener**********/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if (null != mActionBar){
            mActionBar.selectTab(mActionBar.getTabAt(position));
        }
    }
}
