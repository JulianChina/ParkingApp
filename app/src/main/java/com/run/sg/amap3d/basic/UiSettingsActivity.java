package com.run.sg.amap3d.basic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.services.core.LatLonPoint;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.run.sg.amap3d.R;
import com.run.sg.amap3d.route.DriveRouteActivity;
import com.run.sg.amap3d.util.SensorEventHelper;
import com.run.sg.neighbor.SGNeighborActivity;
import com.run.sg.parking.SGParkingActivity;
import com.run.sg.pickup.SGPickUpActivity;
import com.run.sg.search.SGMainSearchActivity;
import com.run.sg.settings.SGFranchiseShopActivity;
import com.run.sg.settings.SGMoreSettingsActivity;
import com.run.sg.settings.SGRecommendActivity;
import com.run.sg.settings.SGServiceActivity;
import com.run.sg.util.CurrentPosition;
import com.run.sg.view.SGCustomDialog;


/**
 * UI settings一些选项设置响应事件
 */
public class UiSettingsActivity extends Activity implements
        LocationSource, AMapLocationListener, AMap.OnInfoWindowClickListener {

    private Context mContext;
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper mSensorHelper;
    private Marker mLocMarker;
    private Marker mLocalGeoMarker;
    private Circle mCircle;
    private boolean mFirstFix = false;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    private String mCurrentCityName = "深圳";

    public String getCurrentCityName() {
        return mCurrentCityName;
    }

    public void setCurrentCityName(String mCurrentCityName) {
        this.mCurrentCityName = mCurrentCityName;
    }

    private double mEndPointLat = 0.0;
    private double mEndPointLon = 0.0;

    private LinearLayout mBottomBarLayout;

    private SlidingMenu mSlidingMenu;
    private ImageView mEmptyMap;
    private FrameLayout mSearchContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ui_settings_activity);
        mContext = this.getApplicationContext();
        initSlidingMenu();

        mBottomBarLayout = (LinearLayout) findViewById(R.id.bottom_bar_layout);
        mEmptyMap = (ImageView)findViewById(R.id.empty_map);
        mSearchContent = (FrameLayout)findViewById(R.id.search_view_container);

        mapView = (MapView) findViewById(com.run.sg.amap3d.R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        registerListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (0 == requestCode && 0 == requestCode) {
            aMap.clear();
            aMap.addMarker(mLocMarker.getOptions());
            mEndPointLat = data.getDoubleExtra("searchPointLat", 0.0);
            mEndPointLon = data.getDoubleExtra("searchPointLon", 0.0);
            if (mEndPointLat > 0.0 && mEndPointLon > 0.0) {
                LatLng localLatLng = new LatLng(mEndPointLat, mEndPointLon);
                mLocalGeoMarker = aMap.addMarker(new MarkerOptions().anchor(1.0f, 1.0f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(localLatLng)
                        .title("点击这里去目的地"));
                mLocalGeoMarker.showInfoWindow();
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(localLatLng));
                if (CurrentPosition.mCurrentPointLat > 0.0 && CurrentPosition.mCurrentPointLon > 0.0) {
                    // 设置所有maker显示在当前可视区域地图中
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(new LatLng(CurrentPosition.mCurrentPointLat, CurrentPosition.mCurrentPointLon))
                            .include(localLatLng).build();
                    aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                    aMap.moveCamera(CameraUpdateFactory.zoomOut());
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
        mUiSettings.setMyLocationButtonEnabled(!flag);  // 显示默认的定位按钮
        aMap.setMyLocationEnabled(flag);  // 可触发定位并显示定位层
    }

    private void registerListener() {
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    routePlan(marker);
                } else {
                    Toast.makeText(UiSettingsActivity.this, "error marker click", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        routePlan(marker);
    }

    private void routePlan(Marker marker) {
        try {
            LatLng endPoint = marker.getPosition();
            mEndPointLat = endPoint.latitude;
            mEndPointLon = endPoint.longitude;
            Intent intent = new Intent(UiSettingsActivity.this, DriveRouteActivity.class);
            intent.putExtra("driveRouteCurrentLat", CurrentPosition.mCurrentPointLat);
            intent.putExtra("driveRouteCurrentLon", CurrentPosition.mCurrentPointLon);
            intent.putExtra("driveRouteEndLat", mEndPointLat);
            intent.putExtra("driveRouteEndLon", mEndPointLon);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "doRoutePlan error", Toast.LENGTH_SHORT).show();
        }
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
                    Intent intent = new Intent(UiSettingsActivity.this, DriveRouteActivity.class);
                    intent.putExtra("driveRouteCurrentLat", CurrentPosition.mCurrentPointLat);
                    intent.putExtra("driveRouteCurrentLon", CurrentPosition.mCurrentPointLon);
                    intent.putExtra("driveRouteEndLat", mEndPointLat);
                    intent.putExtra("driveRouteEndLon", mEndPointLon);
                    startActivity(intent);
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
                ((TextView)findViewById(R.id.current_position_textview)).setText("当前位置：" + amapLocation.getAddress());
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

    private void initSlidingMenu(){
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT); //设置滑动菜单在左边还是右边
        // 设置触摸屏幕的模式
        //TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        //TOUCHMODE_MARGIN 边缘模式，在content页面中，在屏幕边缘滑动才可以打开slding menu
        //TOUCHMODE_NONE 不能通过手势打开啦
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.sg_slidingmenu_shadow_width);
        // 设置滑动菜单视图的宽度
        mSlidingMenu.setBehindOffsetRes(R.dimen.sg_slidingmenu_offset);
        // 设置渐入渐出效果的值
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setFadeEnabled(true);
        mSlidingMenu.setBehindScrollScale(0);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.la_sg_slidingmenu_content);
        mSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                Log.d("yq","onOpen");
                mapView.setVisibility(View.GONE);
                if (mEmptyMap != null && mEmptyMap.getVisibility() == View.GONE){
                    mEmptyMap.setVisibility(View.VISIBLE);
                }
            }
        });

        mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                Log.d("yq","onOpened");
                mapView.setVisibility(View.VISIBLE);
                if (mEmptyMap != null && mEmptyMap.getVisibility() == View.VISIBLE){
                    mEmptyMap.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mSlidingMenu !=null && mSlidingMenu.isMenuShowing()){
            mSlidingMenu.toggle();
            return;
        }
        if (mSearchContent != null && mSearchContent.getVisibility() == View.VISIBLE){
            mSearchContent.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    private void showSearchContentWithAnim(){
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mSearchContent,"alpha",0.3f,1.0f);
        //TranslateAnimation translateAnimation = new TranslateAnimation();
        ObjectAnimator transYAnim = ObjectAnimator.ofFloat(mSearchContent,"y",
                -1920,0);
        int startY = mSearchContent.getBottom() - mSearchContent.getTop();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnim,transYAnim);
        animatorSet.setDuration(1000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSearchContent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSearchContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public void menuClickListener(View view){
        int id = view.getId();
        switch (id){
            case R.id.menu_logon_layout:
                Log.d("yq","logon click");
                break;
            case R.id.menu_wallet_layout:
                Log.d("yq","menu_wallet_layout click");
                break;
            case R.id.menu_licence_plate_layout:
                Log.d("yq","menu_licence_plate_layout click");
                break;
            case R.id.menu_order_layout:
                Log.d("yq","menu_order_layout click");
                break;
            case R.id.menu_recommend_layout:
                Log.d("yq","menu_recommend_layout click");
                startActivity(new Intent(this, SGRecommendActivity.class));
                break;
            case R.id.menu_franchise_shop_layout:
                Log.d("yq","menu_franchise_shop_layout click");
                startActivity(new Intent(this, SGFranchiseShopActivity.class));
                break;
            case R.id.menu_service_layout:
                Log.d("yq","menu_service_layout click");
                startActivity(new Intent(this, SGServiceActivity.class));
                break;
            case R.id.menu_more_layout:
                Log.d("yq","menu_more_layout click");
                startActivity(new Intent(this, SGMoreSettingsActivity.class));
                break;
            default:
                break;
        }
    }

    public void bottomBarClickListener(View v){
        int id = v.getId();
        switch (id){
            case R.id.neighbor_layout:
                startActivity(new Intent(this, SGNeighborActivity.class));
                break;
            case R.id.parking_layout:
                startActivity(new Intent(this, SGParkingActivity.class));
                break;
            case R.id.pick_up_layout:
                startActivity(new Intent(this, SGPickUpActivity.class));
                break;
            case R.id.search_layout:
                Intent intent = new Intent(this, SGMainSearchActivity.class);
                intent.putExtra("city_name", getCurrentCityName());
                UiSettingsActivity.this.startActivityForResult(intent, 0);
                    /*if (mSearchContent != null && mSearchContent.getVisibility() == View.GONE){
                        showSearchContentWithAnim();
                    }*/
                break;
            case R.id.setting_img:
                if (mSlidingMenu != null){
                    mSlidingMenu.toggle();
                }
                break;
            default:
                break;
        }
    }
}
