package com.run.sg.amap3d.district;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.run.sg.amap3d.R;
import com.run.sg.amap3d.basic.UiSettingsActivity;
import com.run.sg.amap3d.util.ToastUtil;
import com.run.sg.search.SGMainSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 政区划查询
 */
public class DistrictActivity extends Activity implements
		OnDistrictSearchListener, OnItemSelectedListener {

	public static final String COUNTRY = "country"; // 行政区划，国家级
	public static final String PROVINCE = "province"; // 行政区划，省级
	public static final String CITY = "city"; // 行政区划，市级

	//当前选中的级别
	private String selectedLevel = COUNTRY;
	
	// 当前行政区划
	private DistrictItem currentDistrictItem = null;

	// 下级行政区划集合
	private Map<String, List<DistrictItem>> subDistrictMap = new HashMap<String, List<DistrictItem>>();

	// 省级列表
	private List<DistrictItem> provinceList = new ArrayList<DistrictItem>();

	// 市级列表
	private List<DistrictItem> cityList = new ArrayList<DistrictItem>();

	// 区县级列表
	private List<DistrictItem> districtList = new ArrayList<DistrictItem>();

	// 是否已经初始化
	private boolean isInit = false;

	private Spinner spinnerProvince;
	private Spinner spinnerCity;
	private Button mButtonConfirm;
	private TextView mCurrentCityText;
	private String mCurrentProvinceName = "广东省";
	private String mCurrentCityName = "深圳";
	private String mQueryCityName = "深圳";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.run.sg.amap3d.R.layout.district_activity);

		spinnerProvince = (Spinner) findViewById(com.run.sg.amap3d.R.id.spinner_province);
		spinnerCity = (Spinner) findViewById(com.run.sg.amap3d.R.id.spinner_city);
		mButtonConfirm = (Button) findViewById(com.run.sg.amap3d.R.id.btn_confirm);
		mCurrentCityText = (TextView)findViewById(R.id.current_city_text);
		mCurrentCityText.setText(mCurrentCityName);

		spinnerProvince.setOnItemSelectedListener(this);
		spinnerCity.setOnItemSelectedListener(this);
		mButtonConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DistrictActivity.this, SGMainSearchActivity.class);
				intent.putExtra("select_city_name", mQueryCityName);
				setResult(0, intent);
				finish();
			}
		});

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置行政区划查询监听
		DistrictSearch districtSearch = new DistrictSearch(this);
		districtSearch.setOnDistrictSearchListener(this);
		// 查询中国的区划
		DistrictSearchQuery query = new DistrictSearchQuery();
		query.setKeywords("中国");
		districtSearch.setQuery(query);
		// 异步查询行政区
		districtSearch.searchDistrictAsyn();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 返回District（行政区划）异步处理的结果
	 */
	@Override
	public void onDistrictSearched(DistrictResult result) {
		List<DistrictItem> subDistrictList  = null;
		if (result != null) {
			if (result.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {

				List<DistrictItem> district = result.getDistrict();

				if (!isInit) {
					currentDistrictItem = district.get(0);
					isInit = true;
				}

				// 将查询得到的区划的下级区划写入缓存
				for (int i = 0; i < district.size(); i++) {
					DistrictItem districtItem = district.get(i);
					Log.d("julian", districtItem.getName());
					subDistrictMap.put(districtItem.getAdcode(), districtItem.getSubDistrict());
				}
				// 获取当前区划的下级区划列表
				subDistrictList = subDistrictMap.get(currentDistrictItem.getAdcode());
			} else {
				ToastUtil.showerror(this, result.getAMapException().getErrorCode());
			}
		}
		setSpinnerView(subDistrictList);
	}

	/**
	 * 获取行政区划的信息字符串
	 * @param districtItem
	 * @return
	 */
	private String getDistrictInfoStr(DistrictItem districtItem){
		StringBuffer sb = new StringBuffer();
		String name = districtItem.getName();
		String adcode = districtItem.getAdcode();
		String level = districtItem.getLevel();
		String citycode = districtItem.getCitycode();
		LatLonPoint center = districtItem.getCenter();
		sb.append("区划名称:" + name + "\n");
		sb.append("区域编码:" + adcode + "\n");
		sb.append("城市编码:" + citycode + "\n");
		sb.append("区划级别:" + level + "\n");
		if (null != center) {
			sb.append("经纬度:(" + center.getLongitude() + ", "
					+ center.getLatitude() + ")\n");
		}
		mQueryCityName = name;
		mCurrentCityText.setText(mQueryCityName);
		return sb.toString();
	}
	
	// 设置spinner视图
	private void setSpinnerView(List<DistrictItem> subDistrictList) {
		List<String> nameList = new ArrayList<String>();
		if(subDistrictList != null && subDistrictList.size() > 0){
			for (int i = 0; i < subDistrictList.size(); i++) {
				nameList.add(subDistrictList.get(i).getName());
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, nameList);
			
			if (selectedLevel.equalsIgnoreCase(COUNTRY)) {
				provinceList = subDistrictList;
				spinnerProvince.setAdapter(adapter);
				for (int i = 0; i < provinceList.size(); ++i) {
					if (provinceList.get(i).getName().equals(mCurrentProvinceName)) {
						spinnerProvince.setSelection(i);
						break;
					}
				}
			}

			if (selectedLevel.equalsIgnoreCase(PROVINCE)) {
				cityList = subDistrictList;
				spinnerCity.setAdapter(adapter);
				for (int i = 0; i < cityList.size(); ++i) {
					if (cityList.get(i).getName().equals(mCurrentCityName)) {
						spinnerCity.setSelection(i);
						break;
					}
				}
			}

			if (selectedLevel.equalsIgnoreCase(CITY)) {
				districtList = subDistrictList;
//				//如果没有区县，将区县说明置空
//				if(null == nameList || nameList.size() <= 0){
//					tv_districtInfo.setText("");
//				}
//				spinnerDistrict.setAdapter(adapter);
			}

		} else {
			List<String> emptyNameList = new ArrayList<String>();
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, emptyNameList);
			if (selectedLevel.equalsIgnoreCase(COUNTRY)) {
				
				spinnerProvince.setAdapter(emptyAdapter);
				spinnerCity.setAdapter(emptyAdapter);
//				spinnerDistrict.setAdapter(emptyAdapter);
//				tv_provinceInfo.setText("");
//				tv_cityInfo.setText("");
//				tv_districtInfo.setText("");
			}

			if (selectedLevel.equalsIgnoreCase(PROVINCE)) {
			 
				spinnerCity.setAdapter(emptyAdapter);
//				spinnerDistrict.setAdapter(emptyAdapter);
//				tv_cityInfo.setText("");
//				tv_districtInfo.setText("");
			}
			
			if (selectedLevel.equalsIgnoreCase(CITY)) {
//				spinnerDistrict.setAdapter(emptyAdapter);
//				tv_districtInfo.setText("");
			}
		}
	}

	/**
	 * 查询下级区划
	 * 
	 * @param districtItem
	 *            要查询的区划对象
	 */
	private void querySubDistrict(DistrictItem districtItem) {
		DistrictSearch districtSearch = new DistrictSearch(DistrictActivity.this);
		districtSearch.setOnDistrictSearchListener(DistrictActivity.this);
		// 异步查询行政区
		DistrictSearchQuery query = new DistrictSearchQuery();
		query.setKeywords(districtItem.getName());
		districtSearch.setQuery(query);
		districtSearch.searchDistrictAsyn();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DistrictItem districtItem = null;
		switch (parent.getId()) {
		case com.run.sg.amap3d.R.id.spinner_province:
			districtItem = provinceList.get(position);
			selectedLevel = PROVINCE;
			getDistrictInfoStr(districtItem);
			Log.d("julian", "province");
			break;
		case com.run.sg.amap3d.R.id.spinner_city:
			selectedLevel = CITY;
			districtItem = cityList.get(position);
			getDistrictInfoStr(districtItem);
			Log.d("julian", "city");
			break;
//		case com.run.sg.amap3d.R.id.spinner_district:
//			selectedLevel = DISTRICT;
//			districtItem = districtList.get(position);
//			tv_districtInfo.setText(getDistrictInfoStr(districtItem));
//			break;
		default:
			break;
		}

		if (districtItem != null) {
			currentDistrictItem = districtItem;
			// 先查缓存如果缓存存在则直接从缓存中查找，无需再执行查询请求
			List<DistrictItem> cache = subDistrictMap.get(districtItem.getAdcode());
			if (null != cache) {
				setSpinnerView(cache);
			} else {
				querySubDistrict(districtItem);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
