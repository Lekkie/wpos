package com.avantir.wpos.activity.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.activity.transactions.PayActivity;
import com.avantir.wpos.activity.transactions.RefundActivity;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.MoneyUtil;

/**
 * Created by lekanomotayo on 23/01/2018.
 */
public class InputMoneyActivity extends BaseActivity {

    //Enter the amount of Naira and Kobo
    private TextView inputMoneyMajorText, inputMoneyMinorText;
    private long inputMoney = 0;
    private Bundle bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_money);
        WPOSApplication.activityList.add(this);

        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);
        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        //Top Title bar
        //ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        //titleBackImage.setOnClickListener(this);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Enter Amount (Naira)");

        inputMoneyMajorText = (TextView) findViewById(R.id.inputMoneyMajorText);
        inputMoneyMinorText = (TextView) findViewById(R.id.inputMoneyMinorText);

        Button btn_num00 = (Button) findViewById(R.id.btn_num00);
        btn_num00.setOnClickListener(this);
        Button btn_num0 = (Button) findViewById(R.id.btn_num0);
        btn_num0.setOnClickListener(this);
        Button btn_num1 = (Button) findViewById(R.id.btn_num1);
        btn_num1.setOnClickListener(this);
        Button btn_num2 = (Button) findViewById(R.id.btn_num2);
        btn_num2.setOnClickListener(this);
        Button btn_num3 = (Button) findViewById(R.id.btn_num3);
        btn_num3.setOnClickListener(this);
        Button btn_num4 = (Button) findViewById(R.id.btn_num4);
        btn_num4.setOnClickListener(this);
        Button btn_num5 = (Button) findViewById(R.id.btn_num5);
        btn_num5.setOnClickListener(this);
        Button btn_num6 = (Button) findViewById(R.id.btn_num6);
        btn_num6.setOnClickListener(this);
        Button btn_num7 = (Button) findViewById(R.id.btn_num7);
        btn_num7.setOnClickListener(this);
        Button btn_num8 = (Button) findViewById(R.id.btn_num8);
        btn_num8.setOnClickListener(this);
        Button btn_num9 = (Button) findViewById(R.id.btn_num9);
        btn_num9.setOnClickListener(this);

        //Clear
        ImageView btn_num_clear = (ImageView) findViewById(R.id.btn_num_clear);
        btn_num_clear.setOnClickListener(this);

        //Swipe
        ImageView btn_brush_cashier = (ImageView) findViewById(R.id.btn_brush_cashier);
        btn_brush_cashier.setOnClickListener(this);
    }

    protected void initData() {
        bundle = getIntent().getExtras();
        if (bundle == null) {
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
                back();
                break;
            case R.id.btn_num0:
            case R.id.btn_num1:
            case R.id.btn_num2:
            case R.id.btn_num3:
            case R.id.btn_num4:
            case R.id.btn_num5:
            case R.id.btn_num6:
            case R.id.btn_num7:
            case R.id.btn_num8:
            case R.id.btn_num9:
                restartTimer();
                if ((inputMoney + "").length() < 11) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());
                    inputMoneySetText();
                }
                break;

            case R.id.btn_num00:
                restartTimer();
                if ((inputMoney + "").length() < 10) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());
                    inputMoneySetText();
                }
                break;

            case R.id.btn_num_clear:
                restartTimer();
                if (inputMoney > 0) {
                    inputMoney = inputMoney / 10;
                    inputMoneySetText();
                }
                break;
            case R.id.btn_brush_cashier:
                int tranTypeFlag = bundle.getInt(ConstantUtils.TRAN_TYPE, ConstantUtils.PURCHASE);
                invokeNextActivity(tranTypeFlag);
                break;
            default:
                break;
        }
    }


    /**
     * Jump to the next page
     *
     * @param tranTypeFlag 0 for bar code payment
     */
    private void invokeNextActivity(int tranTypeFlag) {
        // check if card is still inserted, if yes continue otherwise, go back to main menu or insert card
        // Check if card is inserted
        // int res = mBankCard.iccDetect();

        if (inputMoney > 0) {
            Intent intent = new Intent();
            bundle.putLong(ConstantUtils.TRAN_AMT, inputMoney);
            String accountType = bundle.getString(ConstantUtils.ACCT_TYPE, "00");

            if (tranTypeFlag == ConstantUtils.PURCHASE) {
                bundle.putInt(ConstantUtils.TRAN_TYPE, ConstantUtils.PURCHASE);
                intent.setClass(this, PayActivity.class);
                countDownTimer.cancel();
            }
            else if (tranTypeFlag == ConstantUtils.REFUND) {
                bundle.putInt(ConstantUtils.TRAN_TYPE, ConstantUtils.REFUND);
                intent.setClass(this, RefundActivity.class);
                countDownTimer.cancel();
            }

            intent.putExtras(bundle);
            startActivity(intent);
            //finish();
            skipActivityAnim(1);
        } else {
            displayDialog("Amount cannot be Zero");
        }
    }


    private void inputMoneySetText() {
        String inputMoneyString = MoneyUtil.kobo2Naira(inputMoney);
        inputMoneyMajorText.setText(inputMoneyString.substring(0, inputMoneyString.length() - 2));
        inputMoneyMinorText.setText(inputMoneyString.substring(inputMoneyString.length() - 2));
    }


    @Override
    public void onBackPressed()
    {
        back();
        //if (inputMoney > 0){

        //}
        /*
        else {
            try{
                int cardpresent = InsertCardActivity.mBankCard.iccDetect();
                displayDialog("Please remove card");
            }
            catch(Exception ex){

            }
        }
        */
    }


    private void back(){
        finish();
        skipActivityAnim(-1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        if(SmartPeakApplication.consumePaySuccess) {
            inputMoney = 0;
            inputMoneySetText();
            SmartPeakApplication.consumePaySuccess = false;
        }
        */
    }

    GlobalData globalData = GlobalData.getInstance();

    CountDownTimer countDownTimer = new CountDownTimer(globalData.getPageTimerInSec() * 1000, globalData.getPageTimerInSec() * 1000) {

        public void onTick(long millisUntilFinished) {
            //System.out.println("Tick Tock...");
        }

        public void onFinish() {
            back();
        }
    }.start();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            restartTimer();
        }
        return super.onTouchEvent(event);
    }


    private void restartTimer(){
        countDownTimer.cancel();
        countDownTimer.start();
        System.out.println("Starting timer again");
    }



}
