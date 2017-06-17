package com.run.sg.neighbor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.run.sg.amap3d.R;
import com.run.sg.model.SGParkingLotItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yq on 2017/6/11.
 */
public class SGSortListAdapter extends BaseAdapter {
    private List<SGParkingLotItemData> mAllParkingLotItems = new ArrayList<>();
    private Context mContext;

    public SGSortListAdapter(Context context, List<SGParkingLotItemData> data){
        mContext = context;
        mAllParkingLotItems = data;
    }

    @Override
    public Object getItem(int position) {
        return mAllParkingLotItems.get(position);
    }

    @Override
    public int getCount() {
        return mAllParkingLotItems.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.la_sg_sort_list_item_view,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        setValue(holder, position);
        return convertView;
    }

    private void setValue(ViewHolder holder, int position) {
        holder.mName.setText(mAllParkingLotItems.get(position).getName());
        holder.mAddress.setText(mAllParkingLotItems.get(position).getAddress());
        holder.mVacancy.setText("" + mAllParkingLotItems.get(position).getLeft()
                + "/" + mAllParkingLotItems.get(position).getVacancy());
        holder.mPrice.setText("￥" + mAllParkingLotItems.get(position).getPrice());
        holder.mDistance.setText("" + mAllParkingLotItems.get(position).getDistance() + "Km");
    }

    private class ViewHolder{
        TextView mName,mAddress,mPrice,mVacancy,mDistance;

        ViewHolder(View convertView){
            mName = (TextView)convertView.findViewById(R.id.name_textview);
            mAddress = (TextView)convertView.findViewById(R.id.address_textview);
            mVacancy = (TextView)convertView.findViewById(R.id.vacancy_textview);
            mPrice = (TextView)convertView.findViewById(R.id.price_textview);
            mDistance = (TextView)convertView.findViewById(R.id.distance_textview);
        }
    }
}
