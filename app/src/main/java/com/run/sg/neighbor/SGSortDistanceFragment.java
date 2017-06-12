package com.run.sg.neighbor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.run.sg.amap3d.R;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortDistanceFragment extends Fragment {

    private ListView mSortDistanceListView;
    private SGSortListAdapter mSortDistanceListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.la_sg_sort_distance_fragment_content,container,false);
        mSortDistanceListView = (ListView) view.findViewById(R.id.sort_distance_listview);
        mSortDistanceListAdapter = new SGSortListAdapter(getActivity());
        mSortDistanceListAdapter.setData(null);
        mSortDistanceListView.setAdapter(mSortDistanceListAdapter);

        return view;
    }
}
