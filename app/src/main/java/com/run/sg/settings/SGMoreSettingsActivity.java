package com.run.sg.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.run.sg.amap3d.R;

/**
 * Created by yq on 2017/7/1.
 */
public class SGMoreSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_more_activity_content);
        findViewById(R.id.more_activity_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void preferenceEntryClickListener(View v){
        int id = v.getId();
        switch (id){
            case R.id.about_us_layout:
                startActivity(new Intent(this, SGAboutUsPreferenceActivity.class));
                break;
            case R.id.feedback_layout:
                startActivity(new Intent(this, SGFeedbackPreferenceActivity.class));
                break;
            case R.id.help_layout:
                startActivity(new Intent(this, SGHelpPreferenceActivity.class));
                break;
            case R.id.upgrade_layout:
                startActivity(new Intent(this, SGUpgradeActivity.class));
                break;
            default:
                break;
        }
    }
}
