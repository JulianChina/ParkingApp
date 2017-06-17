package com.run.sg.neighbor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.run.sg.amap3d.R;
import com.run.sg.amap3d.basic.UiSettingsActivity;
import com.run.sg.amap3d.route.DriveRouteActivity;
import com.run.sg.model.SGParkingLotItemData;
import com.run.sg.util.CurrentPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortPriceFragment extends Fragment {

    private FragmentActivity mFragmentActivity;
    private ListView mSortPriceListView;
    private SGSortListAdapter mSortPriceListAdapter;
    private List<SGParkingLotItemData> mDataByPrice = new ArrayList<SGParkingLotItemData>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.la_sg_sort_price_fragment_content,container,false);
        mSortPriceListView = (ListView) view.findViewById(R.id.sort_price_listview);
        mFragmentActivity = getActivity();
        mDataByPrice.addAll(((SGNeighborActivity)mFragmentActivity).getData());
        Collections.sort(mDataByPrice, new Comparator<SGParkingLotItemData>() {
            @Override
            public int compare(SGParkingLotItemData o1, SGParkingLotItemData o2) {
                return o1.getPrice() < o2.getPrice() ? -1 : 1 ;
            }
        });
        mSortPriceListAdapter = new SGSortListAdapter(mFragmentActivity, mDataByPrice);
        mSortPriceListView.setAdapter(mSortPriceListAdapter);
        mSortPriceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mFragmentActivity, DriveRouteActivity.class);
                intent.putExtra("driveRouteCurrentLat", CurrentPosition.mCurrentPointLat);
                intent.putExtra("driveRouteCurrentLon", CurrentPosition.mCurrentPointLon);
                intent.putExtra("driveRouteEndLat", mDataByPrice.get(position).getLatitude());
                intent.putExtra("driveRouteEndLon", mDataByPrice.get(position).getLongitude());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        if (mDataByPrice != null) {
            mDataByPrice.clear();
            mDataByPrice = null;
        }
        super.onDestroy();
    }
}
