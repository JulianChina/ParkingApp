package com.run.sg.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.run.sg.amap3d.R;

/**
 * Created by yq on 2017/7/1.
 */
public class SGFeedbackPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_feedback_activity_content);
        findViewById(R.id.feedback_activity_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
