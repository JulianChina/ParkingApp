package com.run.sg.mainui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.run.sg.amap3d.R;
import com.run.sg.neighbor.SGNeighborActivity;
import com.run.sg.parking.SGParkingActivity;
import com.run.sg.pickup.SGPickUpActivity;
import com.run.sg.util.SGMapConstants;

/**
 * Created by yq on 2017/5/28.
 */
public class SGMainUI {

    private Context mContext;
    private RelativeLayout mRootView;

    public SGMainUI(Context context,RelativeLayout relativeLayout){
        mContext = context;
        mRootView = relativeLayout;
    }

    public void initMainUI(){
        initBottomClickListener();
    }

    private void initBottomClickListener(){
        mRootView.findViewById(R.id.neighbor_layout).setOnClickListener(mButtomBarClickListener);
        mRootView.findViewById(R.id.parking_layout).setOnClickListener(mButtomBarClickListener);
        mRootView.findViewById(R.id.pick_up_layout).setOnClickListener(mButtomBarClickListener);
        mRootView.findViewById(R.id.search_layout_in_main_ui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SGMapConstants.SEARCH_ACTION);
                mContext.startActivity(intent);
            }
        });
    }

    private View.OnClickListener mButtomBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.neighbor_layout:
                    mContext.startActivity(new Intent(mContext, SGNeighborActivity.class));
                    break;
                case R.id.parking_layout:
                    mContext.startActivity(new Intent(mContext, SGParkingActivity.class));
                    break;
                case R.id.pick_up_layout:
                    mContext.startActivity(new Intent(mContext, SGPickUpActivity.class));
                    break;
                default:
                    break;
            }

        }
    };

    public interface SGMainUICallback {
        void showDonatePoint();
    }
}
