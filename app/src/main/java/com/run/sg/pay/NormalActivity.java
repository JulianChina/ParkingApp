package com.run.sg.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fuqianla.paysdk.FuQianLa;
import com.fuqianla.paysdk.FuQianLaPay;
import com.fuqianla.paysdk.bean.FuQianLaResult;
import com.run.sg.amap3d.R;

/**
 * Created by siqi on 16/3/30.
 *
 * @author siqi
 */
public class NormalActivity extends Activity implements View.OnClickListener {
    private EditText etCommodity, etAmount, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);

        etCommodity = (EditText) findViewById(R.id.et_commodity);
        etAmount = (EditText) findViewById(R.id.et_amount);
        etPhone = (EditText) findViewById(R.id.et_phone);

        Button btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(this);

        etCommodity.setText("正常模式-商品名称");
        etPhone.setText("13681348271");
        etAmount.setText("0.01");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay:

                String subject = etCommodity.getText().toString();
                String amount = etAmount.getText().toString();
                String phone = etPhone.getText().toString();

                if (TextUtils.isEmpty(subject)
                        || TextUtils.isEmpty(amount)
                        || TextUtils.isEmpty(phone))
                    return;

                //支付核心代码
                FuQianLaPay pay = new FuQianLaPay.Builder(this)
                        .alipay(true)
                        .wxpay(true)
                        .baidupay(true)
                        .unionpay(true)
                        .jdpay(true)
                        .fqpay(true, 1)
                        .phone(phone)
                        .orderID(OrderUtils.getOutTradeNo())//订单号
                        .amount(Double.parseDouble(amount))//金额
                        .subject(subject)//商品名称
                        .notifyUrl(Merchant.MERCHANT_NOTIFY_URL)
                        .build();
                pay.startPay();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回结果
        if (requestCode == FuQianLa.REQUESTCODE
                && resultCode == FuQianLa.RESULTCODE
                && data != null) {
            FuQianLaResult result = data.getParcelableExtra(FuQianLa.PAYRESULT_KEY);
            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
