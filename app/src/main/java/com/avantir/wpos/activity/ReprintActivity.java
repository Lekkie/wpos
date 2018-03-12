package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.printer.PrintTransactionThread;
import com.avantir.wpos.utils.*;
import wangpos.sdk4.libbasebinder.Printer;

/**
 * Created by lekanomotayo on 24/01/2018.
 */
public class ReprintActivity extends BaseActivity {

    private String TAG = "ReprintActivity";

    LinearLayout reprintLayout;
    private TextView reprintHeaderText;
    private TextView reprintStatusText;

    private Printer mPrinter;

    boolean printingInProgress = false;
    boolean customerReceiptPrinted = false;
    boolean merchantReceiptPrinted = false;

    TransInfo transInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reprint);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Reprint");

        //doneButton.setOnClickListener(this);
        //reprintButton.setOnClickListener(this);


        reprintLayout = (LinearLayout) findViewById(R.id.reprintLayout);
        reprintLayout.setOnClickListener(this);
        reprintStatusText = (TextView) findViewById(R.id.reprintStatusText);
        reprintHeaderText = (TextView) findViewById(R.id.reprintHeader);
    }

    @Override
    protected void initData() {
        reprintHeaderText.setText("Reprint Last Transaction");
        reprintStatusText.setText("Printing...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }).start();

        TransInfoDao transInfoDao = new TransInfoDao(WPOSApplication.app);
        transInfo = transInfoDao.findLastTransaction();
        baseHandler.sendEmptyMessage(ConstantUtils.MSG_START_PRINT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.reprintLayout:
                if(customerReceiptPrinted && !printingInProgress && !merchantReceiptPrinted) {
                    printingInProgress = true;
                    print(false);
                }
                break;               /*
            case R.id.done_btn:
                goBack();
                break;
            case R.id.reprint_btn:
                customerCopy = !customerCopy;
                baseHandler.sendEmptyMessage(MSG_START_PRINT);
                break;
                */
            default:
                break;

        }
    }


    @Override
    protected void handleMessage(Message msg) {
        Log.i(PayActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_INFO:
                setInfoActionTip(msg.obj.toString());
                break;
            case ConstantUtils.MSG_START_PRINT:
                // Print Receipt
                printingInProgress = true;
                print(true);
                //transactionInProgress = false;
                break;
            case ConstantUtils.MSG_FINISH_PRINT:
                // go to main page
                boolean customerCopy = Boolean.parseBoolean(msg.obj.toString());
                if(customerCopy) {
                    // wait for key press to print merchant copy
                    customerReceiptPrinted = true;
                    printingInProgress = false;
                    // press back key or any key,to initiate merchant print
                    baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.PRESS_PRINT_MERCHANT_COPY).sendToTarget();
                }
                else {
                    merchantReceiptPrinted = true;
                    printingInProgress = false;
                    onBackPressed();
                }
                break;
        }
    }

    private void print(boolean customerCopy){
        if(transInfo != null){
            new PrintTransactionThread(mPrinter, baseHandler, transInfo, customerCopy, true).start();
        }
        //baseHandler.obtainMessage(ConstantUtils.MSG_FINISH_PRINT, customerCopy).sendToTarget();
    }

    private void setInfoActionTip(String tip) {
        reprintStatusText.setText(tip);
    }


    @Override
    public void onBackPressed()
    {
        if(customerReceiptPrinted && !printingInProgress && !merchantReceiptPrinted) {
            printingInProgress = true;
            print(false);
        }
        else {
            super.onBackPressed();
            back();
        }
    }

    private void back(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(ReprintActivity.this, MainActivity.class));
        finish();
    }

}

