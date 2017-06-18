package com.run.sg.parking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.run.sg.amap3d.R;

/**
 * Created by yq on 2017/6/11.
 */
public class SGParkingActivity extends Activity {

    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.la_sg_parking_activity_content);
        mEditText = (EditText)findViewById(R.id.parking_id);
        findViewById(R.id.parking_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //显示虚拟键盘
    private void ShowKeyboard(View v,Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }
}
