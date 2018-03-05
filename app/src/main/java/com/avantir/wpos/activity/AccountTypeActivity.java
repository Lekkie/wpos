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
        this.findViewById(R.id.default_btn).setOnClickListener(this);
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
        // check if card is still inserted, if yes continue otherwise, go back to main menu or insert card
        // Check if card is inserted
        // int res = mBankCard.iccDetect();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                startActivity(new Intent(this, MainMenuActivity.class));
                finish();
                //skipActivityAnim(-1);
                break;
            case R.id.default_btn:
                doNext(ConstantUtils.DEFAULT_ACCT_TYPE);
                break;
            case R.id.savings_btn:
                doNext(ConstantUtils.SAVINGS_ACCT_TYPE);
                break;
            case R.id.current_btn:
                doNext(ConstantUtils.CURRENT_ACCT_TYPE);
                break;
            case R.id.credit_btn:
                doNext(ConstantUtils.CREDIT_ACCT_TYPE);
                break;
        }
    }

    private void  doNext(String acctType){
        // check if card is still inserted, if yes continue otherwise, go back to main menu or insert card
        // Check if card is inserted
        // int res = mBankCard.iccDetect();

        int tranTypeFlag = bundle.getInt(ConstantUtils.TRAN_TYPE, ConstantUtils.PURCHASE);
        if(ConstantUtils.PURCHASE == tranTypeFlag)
            doPurchase(acctType);
        else if(ConstantUtils.BALANCE == tranTypeFlag)
            doBal(acctType);
        else if(ConstantUtils.REFUND == tranTypeFlag)
            doRefund(acctType);
    }

    private void doPurchase(String acctType){
        Intent intent = new Intent(this, InputMoneyActivity.class);
        bundle.putString(ConstantUtils.ACCT_TYPE, acctType);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void doRefund(String acctType){
        Intent intent = new Intent(this, InputMoneyActivity.class);
        bundle.putString(ConstantUtils.ACCT_TYPE, acctType);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void doBal(String acctType){
        Intent intent = new Intent(this, BalanceActivity.class);
        bundle.putString(ConstantUtils.ACCT_TYPE, acctType);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
