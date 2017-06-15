package com.run.sg.amap3d.basic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.run.sg.amap3d.R;
import com.run.sg.amap3d.route.DriveRouteActivity;
import com.run.sg.amap3d.route.WalkRouteDetailActivity;
import com.run.sg.amap3d.util.AMapUtil;
import com.run.sg.amap3d.util.SensorEventHelper;
import com.run.sg.amap3d.util.ToastUtil;
import com.run.sg.navigation.WalkRouteCalculateActivity;
import com.run.sg.neighbor.SGNeighborActivity;
import com.run.sg.parking.SGParkingActivity;
import com.run.sg.pickup.SGPickUpActivity;
import com.run.sg.search.SGMainSearchActivity;
import com.run.sg.util.CurrentPosition;
import com.run.sg.view.SGCustomDialog;

import java.util.ArrayList;
import java.util.List;

import overlay.WalkRouteOverlay;

/**
 * UI settings一些选项设置响应事件
 */
public class UiSettingsActivity extends Activity implements
        LocationSource, AMapLocationListener,
        RouteSearch.OnRouteSearchListener,
        GeocodeSearch.OnGeocodeSearchListener {

    private Context mContext;
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper mSensorHelper;
    private Marker mLocMarker;
    private Circle mCircle;
    private boolean mFirstFix = false;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private LatLonPoint mStartPoint;

    public String getCurrentCityName() {
        return mCurrentCityName;
    }

    public void setCurrentCityName(String mCurrentCityName) {
        this.mCurrentCityName = mCurrentCityName;
    }

    private String mCurrentCityName = "深圳";
    private double mEndPointLat = 0.0;
    private double mEndPointLon = 0.0;
    private LatLonPoint mEndPoint;
    private RouteSearch mRouteSearch;
    private final int ROUTE_TYPE_WALK = 3;
    private ProgressDialog progressDialog = null;// 搜索时进度条
    private WalkRouteResult mWalkRouteResult;
    private TextView mRouteTimeDes, mRouteDetailDes;
    private Button mNavigateBtn;
    private List<LatLonPoint> mDonatePointLatLan = new ArrayList<LatLonPoint>();

    private boolean mQueryFlag = true;

    private LinearLayout mBottomBarLayout;
    private LinearLayout mNeighborLayout;
    private LinearLayout mParkingLayout;
    private LinearLayout mPickUpLayout;
    private LinearLayout mSearchLayout;
    private RelativeLayout mBottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ui_settings_activity);
        mContext = this.getApplicationContext();

        mBottomBarLayout = (LinearLayout) findViewById(R.id.bottom_bar_layout);
        mNeighborLayout = (LinearLayout) findViewById(R.id.neighbor_layout);
        mNeighborLayout.setOnClickListener(mClickListener);
        mParkingLayout = (LinearLayout) findViewById(R.id.parking_layout);
        mParkingLayout.setOnClickListener(mClickListener);
        mPickUpLayout = (LinearLayout) findViewById(R.id.pick_up_layout);
        mPickUpLayout.setOnClickListener(mClickListener);
        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mSearchLayout.setOnClickListener(mClickListener);

        mapView = (MapView) findViewById(com.run.sg.amap3d.R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        registerListener();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.neighbor_layout:
                    startActivity(new Intent(UiSettingsActivity.this, SGNeighborActivity.class));
                    break;
                case R.id.parking_layout:
                    startActivity(new Intent(UiSettingsActivity.this, SGParkingActivity.class));
                    break;
                case R.id.pick_up_layout:
                    startActivity(new Intent(UiSettingsActivity.this, SGPickUpActivity.class));
                    break;
                case R.id.search_layout:
                    Intent intent = new Intent(UiSettingsActivity.this, SGMainSearchActivity.class);
                    intent.putExtra("city_name", getCurrentCityName());
                    UiSettingsActivity.this.startActivityForResult(intent, 0);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && 0 == requestCode) {
            aMap.clear();
            aMap.addMarker(mLocMarker.getOptions());
            mEndPointLat = data.getDoubleExtra("searchPointLat", 0.0);
            mEndPointLon = data.getDoubleExtra("searchPointLon", 0.0);
            Log.d("julian", "" + mEndPointLat + ":" + mEndPointLon);
            if (mEndPointLat > 0.0 && mEndPointLon > 0.0) {
                Marker localGeoMarker = aMap.addMarker(new MarkerOptions().anchor(1.0f, 1.0f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng(mEndPointLat, mEndPointLon))
                        .title("目的地"));
                if (CurrentPosition.mCurrentPointLat > 0.0 && CurrentPosition.mCurrentPointLon > 0.0) {
                    // 设置所有maker显示在当前可视区域地图中
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(new LatLng(CurrentPosition.mCurrentPointLat, CurrentPosition.mCurrentPointLon))
                            .include(new LatLng(mEndPointLat, mEndPointLon)).build();
                    aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));

                }
            }
        }
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
            openAllSettings(true);
        }
        mSensorHelper = new SensorEventHelper(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        setupLocationStyle();
    }

    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
//		//定位一次，且将视角移动到地图中心点。
//		myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW) ;
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void openAllSettings(boolean flag) {
        mUiSettings.setScaleControlsEnabled(flag);  //设置地图默认的比例尺显示
        mUiSettings.setZoomControlsEnabled(flag);  //设置地图默认的缩放按钮显示
        mUiSettings.setCompassEnabled(flag);  //设置地图默认的指南针显示
        mUiSettings.setMyLocationButtonEnabled(flag);  // 显示默认的定位按钮
        aMap.setMyLocationEnabled(flag);  // 可触发定位并显示定位层
    }

    private void registerListener() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    showDialog(marker);
                } else {
                    Toast.makeText(UiSettingsActivity.this, "error marker click", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    /**
     * 点击marker显示弹框
     */
    void showDialog(final Marker marker) {
        String title = "This is title";
        String message = "This is content";
        final SGCustomDialog mCustomDialog = new SGCustomDialog(UiSettingsActivity.this, title, message);
        mCustomDialog.setCanceledOnTouchOutside(false);
        mCustomDialog.setListener(new SGCustomDialog.ButtonClickListener() {
            @Override
            public void doNavigate() {
                try {
                    LatLng endPoint = marker.getPosition();
                    mEndPointLat = endPoint.latitude;
                    mEndPointLon = endPoint.longitude;
                    mEndPoint = new LatLonPoint(mEndPointLat, mEndPointLon);
                    mStartPoint = new LatLonPoint(CurrentPosition.mCurrentPointLat, CurrentPosition.mCurrentPointLon);
                    Intent intent = new Intent(UiSettingsActivity.this, DriveRouteActivity.class);
                    intent.putExtra("driveRouteCurrentLat", CurrentPosition.mCurrentPointLat);
                    intent.putExtra("driveRouteCurrentLon", CurrentPosition.mCurrentPointLon);
                    intent.putExtra("driveRouteEndLat", mEndPointLat);
                    intent.putExtra("driveRouteEndLon", mEndPointLon);
                    startActivity(intent);
//                    routePlan(mStartPoint, mEndPoint);
                } catch (Exception e) {
                    Toast.makeText(mContext, "doRoutePlan error", Toast.LENGTH_SHORT).show();
                }
                mCustomDialog.dismiss();
            }

            @Override
            public void doReward() {
                Toast.makeText(mContext, "doReward", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doCancel() {
                mCustomDialog.dismiss();
            }
        });

        mCustomDialog.show();
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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        if (mBottomBarLayout != null) {
            mBottomBarLayout.setVisibility(View.VISIBLE);
        }
        if (mBottomLayout != null) {
            mBottomLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        mFirstFix = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    CurrentPosition.mCurrentPointLat = amapLocation.getLatitude();
                    CurrentPosition.mCurrentPointLon = amapLocation.getLongitude();
                    mCurrentCityName = amapLocation.getCity();
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                } else {
                    mLocMarker.setPosition(location);
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }



    private void routePlan(LatLonPoint startPoint, LatLonPoint endPoint) {
        initRoutePlan();
        setFromAndToMarker(startPoint, endPoint);
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault, startPoint, endPoint);
    }

    private void initRoutePlan() {
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(com.run.sg.amap3d.R.id.bottom_layout);
        mRouteTimeDes = (TextView) findViewById(com.run.sg.amap3d.R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(com.run.sg.amap3d.R.id.secondline);
        mNavigateBtn = (Button) findViewById(R.id.navigate_btn);
    }

    private void setFromAndToMarker(LatLonPoint startPoint, LatLonPoint searchPoint) {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(startPoint))
                .icon(BitmapDescriptorFactory.fromResource(com.run.sg.amap3d.R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(searchPoint))
                .icon(BitmapDescriptorFactory.fromResource(com.run.sg.amap3d.R.drawable.end)));
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode, LatLonPoint startPoint, LatLonPoint endPoint) {
        if (startPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (endPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在搜索");
        progressDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dismissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();

                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRouteTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomBarLayout.setVisibility(View.GONE);
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mBottomLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result", mWalkRouteResult);
                            startActivity(intent);
                        }
                    });
                    mNavigateBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent naviIntent = new Intent(UiSettingsActivity.this, WalkRouteCalculateActivity.class);
                            if (CurrentPosition.mCurrentPointLat > 0.0 && CurrentPosition.mCurrentPointLon > 0.0 && mEndPointLat > 0.0 && mEndPointLon > 0.0) {
                                naviIntent.putExtra("startPointLat", CurrentPosition.mCurrentPointLat);
                                naviIntent.putExtra("startPointLon", CurrentPosition.mCurrentPointLon);
                                naviIntent.putExtra("endPointLat", mEndPointLat);
                                naviIntent.putExtra("endPointLon", mEndPointLon);
                                startActivity(naviIntent);
                            } else {
                                Toast.makeText(UiSettingsActivity.this, "error navigation", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, com.run.sg.amap3d.R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, com.run.sg.amap3d.R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }


    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                mDonatePointLatLan.add(address.getLatLonPoint());
                mQueryFlag = true;
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

    }


}
