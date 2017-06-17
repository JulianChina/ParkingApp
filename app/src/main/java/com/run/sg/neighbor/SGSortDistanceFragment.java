package com.run.sg.neighbor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.run.sg.amap3d.R;
import com.run.sg.model.SGParkingLotItemData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortDistanceFragment extends Fragment {

    private ListView mSortDistanceListView;
    private SGSortListAdapter mSortDistanceListAdapter;
    private List<SGParkingLotItemData> mDataByDistance = new ArrayList<SGParkingLotItemData>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.la_sg_sort_distance_fragment_content,container,false);
        mSortDistanceListView = (ListView) view.findViewById(R.id.sort_distance_listview);
        mDataByDistance.addAll(((SGNeighborActivity)getActivity()).getData());
        Collections.sort(mDataByDistance, new Comparator<SGParkingLotItemData>() {
            @Override
            public int compare(SGParkingLotItemData o1, SGParkingLotItemData o2) {
                return o1.getDistance() < o2.getDistance() ? -1 : 1 ;
            }
        });
        mSortDistanceListAdapter = new SGSortListAdapter(getActivity(), mDataByDistance);
        mSortDistanceListView.setAdapter(mSortDistanceListAdapter);

        return view;
    }
}
