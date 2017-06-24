package com.run.sg.parkingdetail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.run.sg.amap3d.R;
import com.run.sg.amap3d.route.DriveRouteActivity;
import com.run.sg.amap3d.route.DriveRouteDetailActivity;
import com.run.sg.amap3d.util.AMapUtil;
import com.run.sg.amap3d.util.ToastUtil;
import com.run.sg.navigation.SingleRouteCalculateActivity;
import com.run.sg.util.SGParkingContants;

import overlay.DrivingRouteOverlay;

/**
 * Created by yq on 2017/6/20.
 */
public class SGParkingDetailActivity extends Activity
        implements RouteSearch.OnRouteSearchListener {

    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private Button mNaviBtn;
    private TextView mRouteTime;
    private TextView mRouteDistance;
    private double mStartPointLat = 0.0;
    private double mStartPointLon = 0.0;
    private double mEndPointLat = 0.0;
    private double mEndPointLon = 0.0;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_DRIVE = 2;
    private ProgressDialog progDialog = null;// 搜索时进度条


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_parking_detail_content);
        mContext = this.getApplicationContext();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        initView(intent);
        mapView = (MapView) findViewById(R.id.parking_detail_mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        mStartPointLat = intent.getDoubleExtra("driveRouteCurrentLat", 0.0);
        mStartPointLon = intent.getDoubleExtra("driveRouteCurrentLon", 0.0);
        mEndPointLat = intent.getDoubleExtra("driveRouteEndLat", 0.0);
        mEndPointLon = intent.getDoubleExtra("driveRouteEndLon", 0.0);
        mStartPoint = new LatLonPoint(mStartPointLat, mStartPointLon);
        mEndPoint = new LatLonPoint(mEndPointLat, mEndPointLon);

        init();
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
    }

    private void initView(Intent intent){
        mNaviBtn = (Button) findViewById(R.id.parking_detail_navigation_btn);
        mRouteTime = (TextView) findViewById(R.id.route_time);
        mRouteDistance = (TextView) findViewById(R.id.parking_detail_distance);
//        String distance = Double.toString(intent.getDoubleExtra(SGParkingContants.EXTRA_DISTANCE, 0.0d)) + "Km";
//        mRouteDistance.setText(distance);
        ((TextView)findViewById(R.id.parking_detail_name))
                .setText(intent.getStringExtra(SGParkingContants.EXTRA_NAME));
        ((TextView)findViewById(R.id.parking_detail_address))
                .setText(intent.getStringExtra(SGParkingContants.EXTRA_ADDRESS));
        String left = Integer.toString(intent.getIntExtra(SGParkingContants.EXTRA_LEFT,0));
        ((TextView)findViewById(R.id.parking_detail_left))
                .setText(left);
        String total = Integer.toString(intent.getIntExtra(SGParkingContants.EXTRA_TOTAL,0));
        ((TextView)findViewById(R.id.parking_detail_total))
                .setText(total);
        findViewById(R.id.parking_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            openAllSettings(false);
        }
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        LatLng location = new LatLng(mEndPointLat, mEndPointLon);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

    }

    private void openAllSettings(boolean flag) {
        mUiSettings.setCompassEnabled(flag);  //设置地图默认的指南针显示
        mUiSettings.setRotateGesturesEnabled(flag);
        mUiSettings.setScaleControlsEnabled(flag);  //设置地图默认的比例尺显示
        mUiSettings.setScrollGesturesEnabled(!flag);
        mUiSettings.setTiltGesturesEnabled(flag);
        mUiSettings.setZoomControlsEnabled(flag);  //设置地图默认的缩放按钮显示
        mUiSettings.setZoomGesturesEnabled(flag);
        mUiSettings.setMyLocationButtonEnabled(!flag);  // 显示默认的定位按钮
        aMap.setMyLocationEnabled(flag);  // 可触发定位并显示定位层
    }

    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null || mEndPoint == null) {
            ToastUtil.show(mContext, "没有起点或终点数据，无法搜索目的地，请返回，重新进入");
            return;
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索...");
        progDialog.show();
    }

    private void dismissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dismissProgressDialog();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    int dur = (int) drivePath.getDuration();
                    int dis = (int) drivePath.getDistance();
                    String duration = AMapUtil.getFriendlyTime(dur);
                    String distance = AMapUtil.getFriendlyLength(dis);
                    mRouteTime.setText(duration);
                    mRouteDistance.setText(distance);
                    mNaviBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent naviIntent = new Intent(SGParkingDetailActivity.this, SingleRouteCalculateActivity.class);
                            if (mStartPointLat > 0.0 && mStartPointLon > 0.0 && mEndPointLat > 0.0 && mEndPointLon > 0.0) {
                                naviIntent.putExtra("startPointLat", mStartPointLat);
                                naviIntent.putExtra("startPointLon", mStartPointLon);
                                naviIntent.putExtra("endPointLat", mEndPointLat);
                                naviIntent.putExtra("endPointLon", mEndPointLon);
                                startActivity(naviIntent);
                            } else {
                                Toast.makeText(SGParkingDetailActivity.this, "error navigation", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
