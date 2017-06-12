package com.run.sg.neighbor;

import android.support.v4.app.Fragment;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortTab {

    private String mTitle;
    private Class mFragment;

    SGSortTab(String title, Class fragment){
        this.mTitle = title;
        this.mFragment = fragment;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public Class getFragment(){
        return mFragment;
    }

    public void setFragment(Class fragment){
        this.mFragment = fragment;
    }
}
