package com.run.sg.navigation;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.run.sg.amap3d.R;

import java.util.ArrayList;
import java.util.List;

public class SingleRouteCalculateActivity extends BaseActivity {

    private Intent mNaviIntent;
    private double mStartPointLat;
    private double mStartPointLon;
    private double mEndPointLat;
    private double mEndPointLon;
    private NaviLatLng mStartLatlng;
    private NaviLatLng mEndLatlng;
    private final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    private final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        mNaviIntent = getIntent();
        if (mNaviIntent != null) {
            mStartPointLat = mNaviIntent.getDoubleExtra("startPointLat", 0.0);
            mStartPointLon = mNaviIntent.getDoubleExtra("startPointLon", 0.0);
            mEndPointLat = mNaviIntent.getDoubleExtra("endPointLat", 0.0);
            mEndPointLon = mNaviIntent.getDoubleExtra("endPointLon", 0.0);
            mStartLatlng = new NaviLatLng(mStartPointLat, mStartPointLon);
            mEndLatlng = new NaviLatLng(mEndPointLat, mEndPointLon);
            sList.add(mStartLatlng);
            eList.add(mEndLatlng);
        }
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.GPS);
    }
}
