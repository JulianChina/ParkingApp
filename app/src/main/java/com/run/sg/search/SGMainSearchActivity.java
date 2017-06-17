package com.run.sg.search;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.run.sg.amap3d.R;
import com.run.sg.amap3d.basic.UiSettingsActivity;
import com.run.sg.amap3d.district.DistrictActivity;
import com.run.sg.amap3d.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yq on 2017/5/29.
 */
public class SGMainSearchActivity extends Activity
        implements TextWatcher, Inputtips.InputtipsListener {
    LinearLayout mRootView;
    private TextView mCurrentCity;
    private String mCurrentCityName = "深圳";
    private AutoCompleteTextView mKeywordText;
    private ListView mInputList;
    private List<LatLonPoint> mListLatLonPoint = new ArrayList<LatLonPoint>();
    private List<String> mListAddress = new ArrayList<String>();
    private LatLonPoint mSearchPoint = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_search_activity_content);
        final Intent intent = getIntent();
        if (null != intent) {
            mCurrentCityName = intent.getStringExtra("city_name");
        }
        mRootView = (LinearLayout) findViewById(R.id.search_activity_content_root);
        if (savedInstanceState == null) {
            mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startRootAnimation();
                    return true;
                }
            });
        }
        findViewById(R.id.right_arrow_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCurrentCity = (TextView) findViewById(R.id.current_city);
        if (!TextUtils.isEmpty(mCurrentCityName)) {
            mCurrentCity.setText(mCurrentCityName);
        }
        mCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SGMainSearchActivity.this, DistrictActivity.class), 0);
            }
        });
        mKeywordText = (AutoCompleteTextView) findViewById(R.id.search_edit_text);
        mInputList = (ListView) findViewById(R.id.input_list);
        mKeywordText.addTextChangedListener(this);
        mInputList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mKeywordText.setText(mListAddress.get(position));
                mSearchPoint = mListLatLonPoint.get(position);
                Log.d("julian", "(lat,lon):" + mListLatLonPoint.get(position).getLatitude()
                        + "," + mListLatLonPoint.get(position).getLongitude());
                Log.d("julian", "address:" + mListAddress.get(position));
                mListAddress.clear();
                mListLatLonPoint.clear();
                Intent routePlanIntent = new Intent(SGMainSearchActivity.this, UiSettingsActivity.class);
                routePlanIntent.putExtra("searchPointLat", mSearchPoint.getLatitude());
                routePlanIntent.putExtra("searchPointLon", mSearchPoint.getLongitude());
                setResult(0, routePlanIntent);
                finish();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && 0 == requestCode) {
            mCurrentCityName = data.getStringExtra("select_city_name");
            if (!TextUtils.isEmpty(mCurrentCityName)) {
                mCurrentCity.setText(mCurrentCityName);
            }
        }
    }

    private void startRootAnimation() {
        mRootView.setScaleY(0.1f);
        mRootView.setPivotY(mRootView.getY() + mRootView.getHeight() / 3);

        mRootView.animate()
                .scaleY(1)
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        InputtipsQuery inputQuery = new InputtipsQuery(newText, mCurrentCityName);
        inputQuery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(SGMainSearchActivity.this, inputQuery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void afterTextChanged(Editable s) {
        mKeywordText.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        mListLatLonPoint.clear();
        mListAddress.clear();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            List<HashMap<String, String>> listString = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", tipList.get(i).getName());
                map.put("address", tipList.get(i).getDistrict() + ":" + tipList.get(i).getAddress());
                listString.add(map);
                mListLatLonPoint.add(tipList.get(i).getPoint());
                mListAddress.add(tipList.get(i).getDistrict() + ":" + tipList.get(i).getAddress());
            }
            SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), listString, com.run.sg.amap3d.R.layout.item_layout,
                    new String[] {"name","address"}, new int[] {com.run.sg.amap3d.R.id.poi_field_id, com.run.sg.amap3d.R.id.poi_value_id});

            mInputList.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();

        } else {
            ToastUtil.showerror(this.getApplicationContext(), rCode);
        }
    }
}
