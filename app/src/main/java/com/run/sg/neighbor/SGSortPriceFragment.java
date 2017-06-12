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
public class SGSortPriceFragment extends Fragment {

    private ListView mSortPriceListView;
    private SGSortListAdapter mSortPriceListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.la_sg_sort_price_fragment_content,container,false);
        mSortPriceListView = (ListView) view.findViewById(R.id.sort_price_listview);
        mSortPriceListAdapter = new SGSortListAdapter(getActivity());
        mSortPriceListAdapter.setData(null);
        mSortPriceListView.setAdapter(mSortPriceListAdapter);
        return view;
    }
}
