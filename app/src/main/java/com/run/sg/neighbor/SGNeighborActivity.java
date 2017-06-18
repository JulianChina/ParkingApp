package com.run.sg.neighbor;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.Window;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.run.sg.amap3d.R;
import com.run.sg.db.ParkInfoTable;
import com.run.sg.model.SGParkingLotItemData;
import com.run.sg.util.CurrentPosition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yq on 2017/6/11.
 */
public class SGNeighborActivity extends FragmentActivity
        implements ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private ArrayList<SGSortTab> mSortTabList = new ArrayList<>();
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private ProgressDialog mProgressDialog;
    private LatLng mCurrentPoint;
    private List<SGParkingLotItemData> mData = new ArrayList<SGParkingLotItemData>();
    private LoadDataAsyncTask mLoadDataAsyncTask;

    public List<SGParkingLotItemData> getData() {
        return mData;
    }

    public void setData(List<SGParkingLotItemData> mData) {
        this.mData = mData;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_neighbor_activity_content);

        initSortTabList();
        initActionBar();
        mCurrentPoint = new LatLng(CurrentPosition.mCurrentPointLat, CurrentPosition.mCurrentPointLon);
    }

    private void initSortTabList(){
        mSortTabList.add(new SGSortTab(
                this.getResources().getString(R.string.sort_by_price_text),SGSortPriceFragment.class));
        mSortTabList.add(new SGSortTab(
                this.getResources().getString(R.string.sort_by_distance_text),SGSortDistanceFragment.class));
    }

    private void initActionBar(){
        mActionBar = getActionBar();
        String str = getResources().getString(R.string.neighbor_title_text);
        SpannableString spannableString = new SpannableString(str);
        StyleSpan styleSpan_B  = new StyleSpan(Typeface.BOLD);
        AbsoluteSizeSpan sizeSpan01 = new AbsoluteSizeSpan(18,true);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.f850_default_blue)),
                0,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(styleSpan_B,0,spannableString.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan01,0,spannableString.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mActionBar.setTitle(spannableString);
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
    protected void onResume() {
        super.onResume();
        if (mLoadDataAsyncTask != null && mLoadDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadDataAsyncTask.cancel(true);
        }
        mLoadDataAsyncTask = new LoadDataAsyncTask();
        mLoadDataAsyncTask.execute();
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

    class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {
        DecimalFormat  df = new DecimalFormat("0.00");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String[] temp : ParkInfoTable.defaultParkInfo) {
                double latitude = Double.valueOf(temp[2]);
                double longitude = Double.valueOf(temp[3]);
                double distance = AMapUtils.calculateLineDistance(mCurrentPoint,
                        new LatLng(latitude, longitude));
                distance /= 1000.0;
                distance = Double.valueOf(df.format(distance));
                mData.add(new SGParkingLotItemData(temp[0], temp[1], latitude, longitude,
                        Integer.valueOf(temp[4]), Integer.valueOf(temp[5]),
                        Double.valueOf(temp[6]), distance));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgressDialog();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("正在搜索");
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
