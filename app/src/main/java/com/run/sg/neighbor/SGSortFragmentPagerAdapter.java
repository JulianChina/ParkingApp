package com.run.sg.neighbor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SGSortTab> mSortTabList;

    public SGSortFragmentPagerAdapter(FragmentManager fm, ArrayList<SGSortTab> tabList){
        super(fm);
        mSortTabList = tabList;
    }

    @Override
    public int getCount() {
        return mSortTabList.size();
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return (Fragment) mSortTabList.get(position).getFragment().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
