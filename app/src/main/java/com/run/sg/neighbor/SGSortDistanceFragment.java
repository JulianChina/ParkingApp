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
public class SGSortDistanceFragment extends Fragment {

    private FragmentActivity mFragmentActivity;
    private ListView mSortDistanceListView;
    private SGSortListAdapter mSortDistanceListAdapter;
    private List<SGParkingLotItemData> mDataByDistance = new ArrayList<SGParkingLotItemData>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.la_sg_sort_distance_fragment_content,container,false);
        mSortDistanceListView = (ListView) view.findViewById(R.id.sort_distance_listview);
        mFragmentActivity = getActivity();
        mDataByDistance.addAll(((SGNeighborActivity)mFragmentActivity).getData());
        Collections.sort(mDataByDistance, new Comparator<SGParkingLotItemData>() {
            @Override
            public int compare(SGParkingLotItemData o1, SGParkingLotItemData o2) {
                return o1.getDistance() < o2.getDistance() ? -1 : 1 ;
            }
        });
        mSortDistanceListAdapter = new SGSortListAdapter(mFragmentActivity, mDataByDistance);
        mSortDistanceListView.setAdapter(mSortDistanceListAdapter);
        mSortDistanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mFragmentActivity, DriveRouteActivity.class);
                intent.putExtra("driveRouteCurrentLat", CurrentPosition.mCurrentPointLat);
                intent.putExtra("driveRouteCurrentLon", CurrentPosition.mCurrentPointLon);
                intent.putExtra("driveRouteEndLat", mDataByDistance.get(position).getLatitude());
                intent.putExtra("driveRouteEndLon", mDataByDistance.get(position).getLongitude());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        if (mDataByDistance != null) {
            mDataByDistance.clear();
            mDataByDistance = null;
        }
        super.onDestroy();
    }
}
