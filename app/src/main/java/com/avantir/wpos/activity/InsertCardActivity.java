package com.avantir.wpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.*;
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

import java.util.Arrays;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class InsertCardActivity extends BaseActivity {


    private String TAG = "InsertCardActivity";

    private TextView insertCardStatusText;
    private TextView insertCardInfoText;


    public static BankCard mBankCard;
    private Core mCore;
    private EmvCore emvCore;


    //GlobalData globalData;
    private Bundle bundle;
    private Context context;

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
        WPOSApplication.activityList.add(this);

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
                startReadingCard();
                break;
            case ConstantUtils.MSG_INFO:
                // Hide the progress
                setInfoActionTip(msg.obj.toString());
                break;
            case ConstantUtils.ICC_NEXT:
                Intent intent1 = new Intent(this, AccountTypeActivity.class);
                bundle.putString(ConstantUtils.CARD_TYPE, msg.obj.toString());
                intent1.putExtras(bundle);
                startActivity(intent1);
                finish();
                break;
            case ConstantUtils.PICC_NEXT:
                Intent intent2 = new Intent(this, AccountTypeActivity.class);
                bundle.putString(ConstantUtils.CARD_TYPE, msg.obj.toString());
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish();
                break;
            case ConstantUtils.MAG_STRIPE_NEXT:
                Intent intent3 = new Intent(this, AccountTypeActivity.class);
                bundle.putString(ConstantUtils.CARD_TYPE, msg.obj.toString());
                intent3.putExtras(bundle);
                startActivity(intent3);
                finish();
                break;

        }
    }



    private void startReadingCard(){

        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "waiting read card...").sendToTarget();
        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.INSERT_SWIPE_CARD).sendToTarget();
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
                        waitAndReadCardAgain(2000);
                        //displayDialog("Failed to read card");
                        //onBack();
                        break;
                    case 0x02:
                        //Read card success,but encryption processing failed
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Read card, but encryption processing failed").sendToTarget();
                        waitAndReadCardAgain(2000);
                        //displayDialog("Read card, but encryption processing failed");
                        //onBack();
                        break;
                    case 0x03:
                        //Read card timeout
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Timeout reading card").sendToTarget();
                        waitAndReadCardAgain(2000);
                        //displayDialog("Timeout reading card");
                        //onBack();
                        break;
                    case 0x04:
                        //Cancel read card
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Cancelled while reading card").sendToTarget();
                        waitAndReadCardAgain(2000);
                        //displayDialog("Cancelled while reading card");
                        //onBack();
                        break;
                    case 0x05:
                        //Read card success,type ICC
                        mCore.buzzer();
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.DO_NOT_REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Chip (IC) card detected").sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.ICC_NEXT, ConstantUtils.ICC_CARD_TYPE).sendToTarget();
                        break;
                    case 0x07:
                        //Read card success,type PICC
                        mCore.buzzer();
                        baseHandler.obtainMessage(ConstantUtils.PICC_NEXT, ConstantUtils.PICC_CARD_TYPE).sendToTarget();
                        break;
                    case 0x00:
                        //Card not inserted properly or magstripe
                        mCore.buzzer();
                        int len1 = outData[1];
                        int len2 = outData[2];
                        int len3 = outData[3];
                        byte[] data1 = Arrays.copyOfRange(outData, 4, len1 + 4);
                        byte[] data2 = Arrays.copyOfRange(outData, 4 + len1, len2 + len1 + 4);
                        byte[] data3 = Arrays.copyOfRange(outData, 4 + len1 + len2, len3 + len2 + 4 + len1);
                        Log.i(TAG, "run: read card len1==" + len1 + "len2==" + len2 + "len3==" + len3);

                        if (len2 > 0) {
                            getMagStripeData(data1, data2, data3, len1, len3);
                        }
                        else{
                            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, ConstantUtils.INSERT_CARD_PROPERLY).sendToTarget();
                            waitAndReadCardAgain(2000);
                        }
                        break;
                    default:
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Failed to read card, please ensure card is inserted").sendToTarget();
                        waitAndReadCardAgain(2000);
                        break;
                }
            }
            else if (result == 5){
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Timeout reading card").sendToTarget();
                waitAndReadCardAgain(2000);
            }
            else {
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Failed to read card").sendToTarget();
                waitAndReadCardAgain(2000);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Error while trying to read card").sendToTarget();
            waitAndReadCardAgain(2000);
        }
    }


    private void getMagStripeData(byte[] data1, byte[] data2, byte[] data3, int len1, int len3){
        Log.i(TAG, "run: card data1==" + ByteUtil.bytes2HexString(data1));
        Log.i(TAG, "run: card data2==" + ByteUtil.bytes2HexString(data2));
        Log.i(TAG, "run: card data3==" + ByteUtil.bytes2HexString(data3));

        String track2Data = new String(data2);
        String serviceCodeExpDatePart = ByteUtil.bytes2HexString(data2).split("D")[1];
        String pan = new String(data2).split("=")[0];
        String expDate = new String(Arrays.copyOfRange(ByteUtil.hexString2Bytes(serviceCodeExpDatePart), 0, 4));
        String serviceCode = new String(Arrays.copyOfRange(ByteUtil.hexString2Bytes(serviceCodeExpDatePart), 4, 6));

        Log.i(TAG, "run: track2Data =" + track2Data);
        Log.i(TAG, "run: serviceCode =" + serviceCode);
        Log.i(TAG, "run: expireDate =" + expDate.replace("=", ""));


        //https://en.wikipedia.org/wiki/Magnetic_stripe_card
        if (serviceCode.startsWith("2") || serviceCode.startsWith("6")) {
            //IC card does not support downgrade operation
            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.INSERT_CHIP_CARD).sendToTarget();
            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Swipe not allowed on this card").sendToTarget();
            waitAndReadCardAgain(2000);
            return;
        }

        bundle.putString(ConstantUtils.MAG_STRIPE_PAN, pan);
        bundle.putString(ConstantUtils.MAG_STRIPE_EXP_DATE, expDate);
        bundle.putString(ConstantUtils.MAG_STRIPE_SERVICE_CODE, serviceCode);
        bundle.putString(ConstantUtils.MAG_STRIPE_TRACK2_DATA, track2Data);

        if (len1 > 0){
            String track1Data = new String(data1);
            try{
                Log.i(TAG, "run: track1Data =" + track1Data);
                int firstIndex = track1Data.indexOf("^");
                if(firstIndex > 0)
                {
                    track1Data = track1Data.substring(firstIndex + 1, track1Data.length());
                    int lastIndex = track1Data.indexOf("^");
                    lastIndex = lastIndex > 0 ? lastIndex : track1Data.length();
                    String cardHolderName = track1Data.substring(0, lastIndex);
                    bundle.putString(ConstantUtils.MAG_STRIPE_CARDHOLDER_NAME, cardHolderName);
                }
            }
            catch(Exception ex){
             ex.printStackTrace();
            }
        }

        if (len3 > 0){
            String track3Data = new String(data3);
            bundle.putString(ConstantUtils.MAG_STRIPE_TRACK3_DATA, track3Data);
        }

        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Card Swiped").sendToTarget();
        baseHandler.obtainMessage(ConstantUtils.MAG_STRIPE_NEXT, ConstantUtils.MAG_CARD_TYPE).sendToTarget();
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
        if (mBankCard != null) {
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        finish();
        skipActivityAnim(-1);
    }


    private void waitAndReadCardAgain(long timeInMill){

        baseHandler.sendEmptyMessageDelayed(ConstantUtils.READ_CARD, timeInMill);
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
