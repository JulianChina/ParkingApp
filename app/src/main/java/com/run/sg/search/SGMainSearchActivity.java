package com.run.sg.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.run.sg.amap3d.R;

/**
 * Created by yq on 2017/5/29.
 */
public class SGMainSearchActivity extends Activity {
    LinearLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_search_activity_content);
        mRootView = (LinearLayout)findViewById(R.id.search_activity_content_root);
        if (savedInstanceState == null){
            mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startRootAnimation();
                    return true;
                }
            });
        }
        findViewById(R.id.right_arrow_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startRootAnimation() {
        mRootView.setScaleY(0.1f);
        mRootView.setPivotY(mRootView.getY() + mRootView.getHeight() / 3);

        mRootView.animate()
                .scaleY(1)
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }


}
