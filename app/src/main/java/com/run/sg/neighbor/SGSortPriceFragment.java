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
public class SGSortPriceFragment extends Fragment {

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
        mDataByPrice.addAll(((SGNeighborActivity)getActivity()).getData());
        Collections.sort(mDataByPrice, new Comparator<SGParkingLotItemData>() {
            @Override
            public int compare(SGParkingLotItemData o1, SGParkingLotItemData o2) {
                return o1.getPrice() < o2.getPrice() ? -1 : 1 ;
            }
        });
        mSortPriceListAdapter = new SGSortListAdapter(getActivity(), mDataByPrice);
        mSortPriceListView.setAdapter(mSortPriceListAdapter);
        return view;
    }
}
