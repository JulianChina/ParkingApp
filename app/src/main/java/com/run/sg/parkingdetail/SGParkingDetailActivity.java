package com.run.sg.parkingdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.run.sg.amap3d.R;
import com.run.sg.util.SGParkingContants;

/**
 * Created by yq on 2017/6/20.
 */
public class SGParkingDetailActivity extends Activity {

    private Button mNaviBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_parking_detail_content);
        Intent intent = getIntent();
        if (intent != null){
            initView(intent);
        } else {
            finish();
        }
    }

    private void initView(Intent intent){
        ((TextView)findViewById(R.id.parking_detail_name))
                .setText(intent.getStringExtra(SGParkingContants.EXTRA_NAME));
        ((TextView)findViewById(R.id.parking_detail_address))
                .setText(intent.getStringExtra(SGParkingContants.EXTRA_ADDRESS));
        String distance = Double.toString(intent.getDoubleExtra(SGParkingContants.EXTRA_DISTANCE,0.0d)) + "Km";
        ((TextView)findViewById(R.id.parking_detail_distance))
                .setText(distance);
        String left = Integer.toString(intent.getIntExtra(SGParkingContants.EXTRA_LEFT,0));
        ((TextView)findViewById(R.id.parking_detail_left))
                .setText(left);
        String total = Integer.toString(intent.getIntExtra(SGParkingContants.EXTRA_TOTAL,0));
        ((TextView)findViewById(R.id.parking_detail_total))
                .setText(total);
        findViewById(R.id.parking_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
