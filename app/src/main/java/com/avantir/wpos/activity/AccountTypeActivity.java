package com.avantir.wpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class AccountTypeActivity extends BaseActivity {

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_type);
        this.findViewById(R.id.savings_btn).setOnClickListener(this);
        this.findViewById(R.id.current_btn).setOnClickListener(this);
        this.findViewById(R.id.credit_btn).setOnClickListener(this);
        this.findViewById(R.id.titleBackImage).setOnClickListener(this);
        super.onCreate(savedInstanceState);
        WPOSApplication.activityList.add(this);
    }


    protected void initView() {
        //Top title bar
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Account Type");
    }

    @Override
    protected void initData() {

        bundle = getIntent().getExtras();
        if(bundle == null)
        {
            bundle = new Bundle();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                startActivity(new Intent(AccountTypeActivity.this, InputMoneyActivity.class));
                finish();
                //skipActivityAnim(-1);
                break;
            case R.id.savings_btn:
                Intent payActivity = new Intent(this, PayActivity.class);
                bundle.putString(ConstantUtils.ACCT_TYPE, ConstantUtils.SAVINGS_ACCT_TYPE);
                payActivity.putExtras(bundle);
                startActivity(payActivity);
                finish();
                break;
            case R.id.current_btn:
                payActivity = new Intent(this, PayActivity.class);
                bundle.putString(ConstantUtils.ACCT_TYPE, ConstantUtils.CURRENT_ACCT_TYPE);
                payActivity.putExtras(bundle);
                startActivity(payActivity);
                finish();
                break;
            case R.id.credit_btn:
                payActivity = new Intent(this, PayActivity.class);
                bundle.putString(ConstantUtils.ACCT_TYPE, ConstantUtils.CREDIT_ACCT_TYPE);
                payActivity.putExtras(bundle);
                startActivity(payActivity);
                finish();
                break;
        }
    }
}
