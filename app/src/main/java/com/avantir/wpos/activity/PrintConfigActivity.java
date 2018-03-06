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
import com.avantir.wpos.printer.PrintConfigThread;
import com.avantir.wpos.printer.PrintTransactionThread;
import com.avantir.wpos.utils.ConstantUtils;
import wangpos.sdk4.libbasebinder.Printer;

/**
 * Created by lekanomotayo on 24/01/2018.
 */
public class PrintConfigActivity extends BaseActivity {

    private String TAG = "PrintConfigActivity";

    private TextView headerText;
    private TextView statusText;

    //private Button doneButton;
    //private Button printConfigButton;

    private Printer mPrinter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_print_config);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Print Config");

        statusText = (TextView) findViewById(R.id.printConfigStatusText);
        headerText = (TextView) findViewById(R.id.printConfigHeader);
    }

    @Override
    protected void initData() {
        //reprintHeaderText.setText("Reprint Last Transaction");
        statusText.setText("Printing...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }).start();

        baseHandler.sendEmptyMessage(ConstantUtils.MSG_START_PRINT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;             /*
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
                print();
                break;
            case ConstantUtils.MSG_FINISH_PRINT:
                back();
                break;
        }
    }

    private void print(){
        new PrintConfigThread(mPrinter, baseHandler).start();
        //baseHandler.obtainMessage(ConstantUtils.MSG_FINISH_PRINT).sendToTarget();
    }

    private void setInfoActionTip(String tip) {
        statusText.setText(tip);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        back();
    }

    private void back(){
        finish();
        skipActivityAnim(-1);
    }

}

