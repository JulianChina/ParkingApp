package com.run.sg.pickup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.run.sg.amap3d.R;
import com.run.sg.pay.NormalActivity;

/**
 * Created by yq on 2017/6/11.
 */
public class SGPickUpActivity extends Activity {

    private Button mPayOnlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_pick_up_activity_content);
        findViewById(R.id.pick_up_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPayOnlineButton = (Button) findViewById(R.id.pick_up_pay_btn);
        mPayOnlineButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pick_up_pay_btn :
                    startActivity(new Intent(SGPickUpActivity.this, NormalActivity.class));
                    break;
                default:
                    break;
            }
        }
    };
}
