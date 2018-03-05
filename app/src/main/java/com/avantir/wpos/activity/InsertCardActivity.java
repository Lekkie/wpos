package com.avantir.wpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.utils.ByteUtil;
import com.avantir.wpos.utils.ConstantUtils;
import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class InsertCardActivity extends BaseActivity {


    private String TAG = "InsertCardActivity";

    private TextView insertCardStatusText;
    private TextView insertCardInfoText;


    private BankCard mBankCard;
    private Core mCore;
    private EmvCore emvCore;


    //GlobalData globalData;
    private Bundle bundle;
    private Context context;
    //private TransInfo transInfo;

    //private ICallbackListener iCallBackListener;


    /*
    No clear button
    No enter button
    Cancel = back button
    Select Purchase Operation when no printing paper is inside the POS : POS should display "Printer not ready" or "Check Printer" or "Insert Paper"

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_insert_card_display);
        this.findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        this.findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);
        //this.findViewById(R.id.titleBackImage).setOnClickListener(this);
        super.onCreate(savedInstanceState);

    }


    protected void initView() {
        context =  this;
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Card");

        WPOSApplication.activityList.add(this);
        insertCardStatusText = (TextView) findViewById(R.id.insertCardStatusText);
        insertCardInfoText = (TextView) findViewById(R.id.insertCardInfoText);
    }


    @Override
    protected void initData() {

        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        //transInfo = new TransInfo();
        //globalData = GlobalData.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
                mBankCard = new BankCard(getApplicationContext());
                startReadingCard();
            }
        }).start();
    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                //finishAppActivity();
                skipActivityAnim(-1);
                break;
        }
    }



    @Override
    protected void handleMessage(Message msg) {
        Log.i(PayActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_PROGRESS:
                //myHandler.sendMessage(myHandler.obtainMessage(WhetherRetryVisiableFlase, msg.obj));
                setStatusActionTip(msg.obj + "");
                break;
            case ConstantUtils.Hide_Progress:
                // Hide the progress
                hideStatusActionTip();
            case ConstantUtils.READ_CARD:
                // Hide the progress
                startReadingCard();
                break;
            case ConstantUtils.MSG_INFO:
                // Hide the progress
                setInfoActionTip(msg.obj.toString());
                break;
            case ConstantUtils.ICC_NEXT:
                Intent intent = new Intent(this, AccountTypeActivity.class);
                bundle.putString(ConstantUtils.CARD_TYPE, msg.obj.toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;

        }
    }



    private void startReadingCard(){

        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "waiting read card...").sendToTarget();
        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.INSERT_CARD).sendToTarget();
        try{
            // read card
            mBankCard.breakOffCommand();//结束上一笔交易的命令(Order to close the previous transaction)
            byte[] outData = new byte[512];
            int[] outDataLen = new int[1];
            int result = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC|BankCard.CARD_MODE_ICC|BankCard.CARD_MODE_MAG,0x3600,outData,outDataLen,ConstantUtils.APP_NAME);

            if (result == 0) {
                Log.d("outData", ByteUtil.bytes2HexString(outData));
                switch (outData[0]) {
                    case 0x01:
                        //Read card failed
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Failed to read card").sendToTarget();
                        //displayDialog("Failed to read card");
                        //onBack();
                        break;
                    case 0x02:
                        //Read card success,but encryption processing failed
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Read card, but encryption processing failed").sendToTarget();
                        //displayDialog("Read card, but encryption processing failed");
                        //onBack();
                        break;
                    case 0x03:
                        //Read card timeout
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Timeout reading card").sendToTarget();
                        //displayDialog("Timeout reading card");
                        //onBack();
                        break;
                    case 0x04:
                        //Cancel read card
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Cancelled while reading card").sendToTarget();
                        //displayDialog("Cancelled while reading card");
                        //onBack();
                        break;
                    case 0x05:
                        //Read card success,type ICC
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.DO_NOT_REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Chip (IC) card detected").sendToTarget();
                        mCore.buzzer();
                        baseHandler.obtainMessage(ConstantUtils.ICC_NEXT, ConstantUtils.ICC_CARD_TYPE).sendToTarget();
                        //readCardInfo(ConstantUtils.ICC_CARD_TYPE);
                        break;
                    case 0x07:
                        //Read card success,type PICC
                        // baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please do not remove card").sendToTarget();
                        // baseHandler.obtainMessage(MSG_PROGRESS, "Contactless card detected").sendToTarget();
                        //   mCore.buzzer();
                        //   readCardInfo(ConstantUtils.PICC_CARD_TYPE);
                        break;
                    case 0x00:
                        //Card not inserted properly
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Insert Card Properly").sendToTarget();
                        //baseHandler.obtainMessage(ConstantUtils.READ_CARD).sendToTarget();
                        break;
                    default:
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Failed to read card, please ensure card is inserted").sendToTarget();
                        break;
                }
            }
            else if (result == 5){
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Timeout reading card").sendToTarget();
            }
            else {
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Failed to read card").sendToTarget();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Error while trying to read card").sendToTarget();
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //displayDialog("Error while trying to read card");
                    setStatusActionTip("Error while trying to read card");
                    setInfoActionTip("");
                }
            });
            */
        }
    }

    //The plug-in to display the status of transaction
    private void setStatusActionTip(String info) {
        insertCardStatusText.setText(info);
    }

    private void setInfoActionTip(String tip) {
        insertCardInfoText.setText(tip);
    }

    private void hideStatusActionTip() {
        insertCardStatusText.setText("");
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        skipActivityAnim(-1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBankCard != null) {
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }




}
