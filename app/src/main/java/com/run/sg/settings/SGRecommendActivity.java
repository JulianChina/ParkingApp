package com.run.sg.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.run.sg.amap3d.R;

public class SGRecommendActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_recommend_activity_content);
        findViewById(R.id.recommend_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
