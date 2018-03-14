package com.avantir.wpos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.dialog.KeyPadDialog;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.listeners.CommsListener;
import com.avantir.wpos.listeners.EMVPINPadListener;
import com.avantir.wpos.listeners.PINPadListener;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.printer.PrintTransactionThread;
import com.avantir.wpos.services.EMVManager;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.*;
import com.solab.iso8583.IsoMessage;
import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.emv.ICallbackListener;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.Printer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lekanomotayo on 24/01/2018.
 */
public class BalanceActivity extends BaseActivity {

    private String TAG = "BalanceActivity";

    private TextView tranStatusText;
    private TextView infoText;

    private Bundle bundle;

    private boolean isOffLine = false;
    private String accountType;
    //Transaction type flag
    private int tranTypeFlag = ConstantUtils.BALANCE;
    private int paymentInstrumentFlag = ConstantUtils.BANK_CARD;

    GlobalData globalData;
    private Context context;
    private Core mCore;
    private EmvCore emvCore;


    TransInfo transInfo;
    private Printer mPrinter;

    TransInfoDao transInfoDao;
    boolean transactionInProgress = true;
    boolean printingInProgress = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_display);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);

        WPOSApplication.activityList.add(this);
        tranStatusText = (TextView) findViewById(R.id.transactionStatusText);
        infoText = (TextView) findViewById(R.id.infoText);
    }

    @Override
    protected void initData() {

        context = this;
        bundle = getIntent().getExtras();
        if(bundle == null)
        {
            bundle = new Bundle();
        }
        tranTypeFlag = bundle.getInt(ConstantUtils.TRAN_TYPE, ConstantUtils.BALANCE);
        paymentInstrumentFlag = bundle.getInt(ConstantUtils.PAYMENT_INSTRUMENT, ConstantUtils.BANK_CARD);
        ((TextView) findViewById(R.id.titleNameText)).setText("Balance");
        accountType = bundle.getString(ConstantUtils.ACCT_TYPE, "00");

        transInfoDao = new TransInfoDao(WPOSApplication.app);
        transInfo = new TransInfo();
        globalData = GlobalData.getInstance();
        KeyPadDialog.getInstance().clearPinRetries();



        new Thread(new Runnable() {
            @Override
            public void run() {
                emvCore = new EmvCore(getApplicationContext());
                mCore = new Core(getApplicationContext());
                //mBankCard = new BankCard(getApplicationContext());
                mPrinter = new Printer(getApplicationContext());

                try{
                    transInfo.setDeviceSerialNo(Build.SERIAL);
                    transInfo.setTradeType(ConstantUtils.Type_Sale);
                    transInfo.setMerchantId(globalData.getMerchantId());
                    transInfo.setMerchantName(globalData.getMerchantName());
                    transInfo.setTerminalId(globalData.getTerminalId());
                    int stan = (globalData.getStan() + 1) % 999999;
                    if(stan == 0)
                        stan = 1;
                    globalData.setStan(stan);
                    transInfo.setStan(StringUtil.leftPadding('0', 6, String.valueOf(stan)));
                    long retRef = (globalData.getRetrievalRef() + 1) % 999999999999L;
                    if(retRef == 0)
                        retRef = 1;
                    globalData.setRetrievalRef(retRef);
                    transInfo.setRetRefNo(StringUtil.leftPad(String.valueOf(retRef), 12, '0'));
                    transInfo.setAmt("0");
                    transInfo.setAccountType(accountType);
                    transInfo.setAuthenticationMethod("NONE");
                    transInfo.setOnLine(true);// Default is online transaction
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }

                String cardType = bundle.getString(ConstantUtils.CARD_TYPE, ConstantUtils.ICC_CARD_TYPE);
                readCardInfo(cardType);

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                back();
                break;
            default:
                break;

        }
    }


    @Override
    protected void handleMessage(Message msg) {
        Log.i(BalanceActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_BACK:
                transactionInProgress = false;
                finishAndReturnMainActivity();
            case ConstantUtils.MSG_ERROR:
                //myHandler.sendMessage(myHandler.obtainMessage(WhetherRetryVisiableTrue, msg.obj));
                setTransactionStatusActionTip(msg.obj + "");
                break;
            case ConstantUtils.MSG_PROGRESS:
                //myHandler.sendMessage(myHandler.obtainMessage(WhetherRetryVisiableFlase, msg.obj));
                setTransactionStatusActionTip(msg.obj + "");
                break;
            case ConstantUtils.Hide_Progress:
               // Hide the progress
                hideTransactionStatusActionTip();
                break;
            case ConstantUtils.MSG_START_TRANS:
                // start processing card things
                break;
            case ConstantUtils.MSG_START_COMMS:
                // start processing transaction online
                doBalance(msg.obj == null ? null : String.valueOf(msg.obj));
                break;
            case ConstantUtils.MSG_FINISH_COMMS:
                // end comms
                byte[] receiveData = (byte[])msg.obj;
                doBalanceResponse(receiveData);
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please remove card").sendToTarget();
                break;
            case  ConstantUtils.MSG_FINISH_ERROR_COMMS:
                // end comms with error
                processFailedResponse(Integer.parseInt(msg.obj.toString()), msg.getData());
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Network connection error, please try again later.").sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please remove card").sendToTarget();
                break;
            case ConstantUtils.MSG_INFO:
                // Hide the progress
                setInfoActionTip(msg.obj.toString());
                break;
            case ConstantUtils.MSG_START_PRINT:
                // Print Receipt
                printingInProgress = true;
                print(true);
                break;
            case ConstantUtils.MSG_FINISH_PRINT:
                // go to main page
                finishAndReturnMainActivity();
                transactionInProgress = false;
                break;
        }
    }


    private void readCardInfo(String s) {
        isOffLine = false;
        //baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "read card success ..." + s).sendToTarget();
        transInfo.setPosInputType(s);
        EMVManager.setEMVManager(context,baseHandler,emvCore);
        switch (s) {
            case ConstantUtils.ICC_CARD_TYPE:
                readEMVCardInfo();
                break;
            case ConstantUtils.PICC_CARD_TYPE:
                readContactlessCardInfo();
                break;
            case ConstantUtils.MAG_CARD_TYPE:
                readMagStripeCardInfo();
                break;
        }
    }


    private void readEMVCardInfo(){
        try {
            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, ConstantUtils.WAITING_MSG).sendToTarget();

            int result = EMVManager.PBOC_Simple(transInfo, iCallBackListener);

            if(!StringUtil.isEmpty(transInfo.getExpDate()) && TimeUtil.hasExpired(transInfo.getExpDate())){
                baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.INVALID_CARD).sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                transactionInProgress = false;
            }
            else if (result != ConstantUtils.EMV_OPERATION_SUCCESS) {
                if (result == -20){
                    baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                    baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.CARD_REMOVED).sendToTarget();
                    transactionInProgress = false;
                }
                else
                {
                    baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED + result).sendToTarget();
                    baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                    transactionInProgress = false;
                }
            }
            else {
                int transType = transInfo.getTradeType();

                //Log.v(TAG, "PBOC_Simple。transType==" + transType);
                if (transType == ConstantUtils.Type_QueryBalance || transType == ConstantUtils.Type_Sale
                        || transType == ConstantUtils.Type_Auth || transType == ConstantUtils.Type_CoilingSale) {

                    int transResult = EMVManager.EMV_TransProcess(transInfo, iCallBackListener);
                    //Log.d(TAG,"checkResult=="+transResult);
                    if (transResult != -8) {
                        if(transResult == -4){
                            // User canceled
                            onBack();
                        }
                        /*else if(transResult == -4){
                            // WRONG PIN, CARD RESTRICTED/BLOCKED (3/3)
                            int totalPinRetry = KeyPadDialog.getInstance().getTotalPinRetry();
                            KeyPadDialog.getInstance().dismissDialog();
                            baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please Remove Card").sendToTarget();
                        }
                        */
                        else if(transResult == 5){ // timeout
                            // Timeout, go to main menu or display error with decline or timeout msg?
                            KeyPadDialog.getInstance().dismissDialog();
                            baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please Remove Card").sendToTarget();
                        }
                        else{
                            KeyPadDialog.getInstance().dismissDialog();
                            baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                            baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "Please Remove Card").sendToTarget();
                        }
                        transactionInProgress = false;
                    }
                } else {
                    displayPinPad(ConstantUtils.PICC_CARD_TYPE);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void readContactlessCardInfo(){
        try {
            baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, ConstantUtils.WAITING_MSG).sendToTarget();
            int result = EMVManager.QPBOC_PreProcess(transInfo, iCallBackListener);
            if (result != ConstantUtils.EMV_OPERATION_SUCCESS) {
                //Log.d(TAG, "QPBOC_PreProcess fail，result==" + result);
                baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                transactionInProgress = false;
            } else {
                result = EMVManager.PBOC_Simple(transInfo, iCallBackListener);
                if (result == ConstantUtils.EMV_OPERATION_SUCCESS) {
                    displayPinPad(ConstantUtils.PICC_CARD_TYPE);
                } else {
                    baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                    baseHandler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
                    transactionInProgress = false;
                    //Log.d(TAG, "PBOC_Simple fail，result==" + result);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    private void readMagStripeCardInfo(){

        String pan = bundle.getString(ConstantUtils.MAG_STRIPE_PAN);
        transInfo.setCardNo(pan);
        transInfo.setMaskedPan(StringUtil.maskPan(pan));
        String expDate = bundle.getString(ConstantUtils.MAG_STRIPE_EXP_DATE);
        transInfo.setExpDate(expDate);
        String serviceCode = bundle.getString(ConstantUtils.MAG_STRIPE_SERVICE_CODE);
        transInfo.setServiceRestrictionCode(StringUtil.leftPad(serviceCode, 3, '0'));
        String track2Data = bundle.getString(ConstantUtils.MAG_STRIPE_TRACK2_DATA);
        transInfo.setTrack2(track2Data);
        String cardHolderName = bundle.getString(ConstantUtils.MAG_STRIPE_CARDHOLDER_NAME);
        transInfo.setCardHolderName(cardHolderName);
        //String track3Data = bundle.getString(ConstantUtils.MAG_STRIPE_TRACK3_DATA);

        displayPinPad(ConstantUtils.MAG_CARD_TYPE);
    }


    private void displayPinPad(final String tradeType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cardNo = transInfo.getCardNo();
                KeyPadDialog.getInstance().showDialog((Activity) context, cardNo, new PINPadListener(baseHandler, iCallBackListener, transInfo, tradeType));
                transInfo.setAuthenticationMethod("PIN");
            }
        });
    }


    private CountDownLatch countDownLatch = null;
    private ICallbackListener iCallBackListener = new ICallbackListener.Stub() {
        @Override
        public int emvCoreCallback(final int command, final byte[] data, final byte[] result, final int[] resultlen) throws RemoteException {
            countDownLatch = new CountDownLatch(1);
            final KeyPadDialog keyPadDialog = KeyPadDialog.getInstance();
            //Log.d(TAG, "emvCoreCallback。command==" + command);
            switch (command) {
                case 2818: //Core.CALLBACK_PIN
                    Log.i("iCallbackListener", "Core.CALLBACK_PIN");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            keyPadDialog.showDialog((Activity) context, command, data, result, resultlen, transInfo.getCardNo(), new EMVPINPadListener(baseHandler, countDownLatch));
                            transInfo.setAuthenticationMethod("PIN");
                        }
                    });
                    break;
                case 2821://Core.CALLBACK_ONLINE
                    Log.i("iCallbackListener", "Core.CALLBACK_ONLINE");
                    int pinRetry = keyPadDialog.getCurrentPinRetry();
                    if(pinRetry >= keyPadDialog.getMaxPinRetry()){
                        KeyPadDialog.getInstance().dismissDialog();
                        baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.WRONG_PIN).sendToTarget();
                        baseHandler.obtainMessage(ConstantUtils.MSG_INFO, ConstantUtils.REMOVE_CARD).sendToTarget();
                        transactionInProgress = false;
                    }
                    else{
                        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Sending transaction online").sendToTarget();
                        int ret = EMVManager.EMV_OnlineProc(result, resultlen,countDownLatch,baseHandler, transInfo);
                        Log.i("iCallbackListener", "Core.CALLBACK_ONLINE, ret = " + ret);
                    }
                    break;
                case 2823:
                    //strTxt = "OffLine pin check success";
                    //baseHandler.sendEmptyMessage(0);
                    baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Correct PIN").sendToTarget();
                    countDownLatch.countDown();
                    break;
                case 2817://Core.CALLBACK_NOTIFY
                    Log.i("iCallbackListener", "Core.CALLBACK_NOTIFY");
                    //app select
                    break;
                case 2819://Core.CALLBACK_AMOUNT //Set amount
                    long amount = 0L;
                    String amt = MoneyUtil.kobo2Naira(amount);
                    //Log.d(TAG, "amount==" + amount);
                    result[0] = 0;
                    //Log.d(TAG, "int2Bytes==" + (int) MoneyUtil.naira2Kobo(Double.parseDouble(a)));
                    byte[] tmp = ByteUtil.int2Bytes((int) (int) MoneyUtil.naira2Kobo(Double.parseDouble(amt)));
                    System.arraycopy(tmp, 0, result, 1, 4);
                    resultlen[0] = 9;
                    countDownLatch.countDown();
                    break;
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };


    private void doBalance(String pinblock){
        byte[] data = null;
        try{
            transInfo.setMsgType(ConstantUtils._0100);
            transInfo.setProcCode(ConstantUtils.BALANCE_PROC_CODE + accountType +  "00");
            Date now = new Date(System.currentTimeMillis());
            String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
            String localTime = TimeUtil.getTimehhmmss(now);
            String localDate = TimeUtil.getDateMMdd(now);
            transInfo.setTransmissionDateTime(transmissionDatetime);
            transInfo.setLocalTime(localTime);
            transInfo.setLocalDate(localDate);
            transInfo.setMerchType(globalData.getMerchantCategoryCode());
            String cardSeqNo = transInfo.getCardSequenceNo();
            transInfo.setCardSequenceNo(StringUtil.isEmpty(cardSeqNo) ? null : StringUtil.leftPadding("0", 3, cardSeqNo));
            transInfo.setPosConditionCode(ConstantUtils.NORMAL_PRESENTMENT_POS_CONDITION_CODE);
            transInfo.setPosPinCaptureCode(StringUtil.leftPadding("0", 2, String.valueOf(ConstantUtils.MAX_PIN_LENGTH)));
            transInfo.setPosEntryMode(transInfo.getPosInputType() + ConstantUtils.ACCEPT_PIN_MODE_CAPABILITY);
            transInfo.setSurcharge("C" + StringUtil.leftPadding("0", 8, globalData.getPurchaseSurcharge()));
            transInfo.setAcqInstId(globalData.getAcquirerId());
            transInfo.setMerchantLoc(IsoMessageUtil.getIso8583MerchantLoc(globalData.getMerchantLoc()));
            transInfo.setCurrencyCode(globalData.getCurrencyCode().substring(1));
            if(!StringUtil.isEmpty(pinblock)){
                transInfo.setPinData(pinblock.substring(2));
            }
            transInfo.setPosDataCode(globalData.getPOSDataCode());
            transInfo.setCreatedOn(TimeUtil.getTimeInEpoch(new Date(System.currentTimeMillis())));
            transInfoDao.create(transInfo);
            //transInfo = transInfoDao.findByRetRefNo(transInfo.getRetRefNo()); // get database ID

            data = IsoMessageUtil.createRequest(transInfo);
            GlobalData globalData = GlobalData.getInstance();
            TcpComms comms = new TcpComms(globalData.getCTMSIP(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
            ICommsListener commsListener = new CommsListener(baseHandler, ConstantUtils.NETWORK_BAL_REQ_TYPE);
            comms.dataCommu(this, data, commsListener);
        }
        catch(Exception ex){
            ex.printStackTrace();
            baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, "Error while trying to ").sendToTarget();
            transactionInProgress = false;
            return;
        }
    }


    private void  processFailedResponse(int commsErrorCode, Bundle bundle){
        transactionInProgress = false;
        baseHandler.sendEmptyMessage(ConstantUtils.MSG_START_PRINT);
    }


    private void doBalanceResponse(byte[] respData){
        try{
            IsoMessage isoMsgResponse = IsoMessageUtil.getInstance().decode(respData);
            System.out.println(isoMsgResponse.debugString());
            String responseCode = isoMsgResponse.getObjectValue(39);
            boolean approved = "00".equalsIgnoreCase(responseCode);
            String responseMsg = approved ? ConstantUtils.TRANSACTION_APPROVED : ConstantUtils.RSP_CODE_MSG_MAP.get(responseCode);
            if(approved){
                String additionalAmt = isoMsgResponse.getObjectValue(54);
                String amt = getAvailableBalance(transInfo.getAccountType(), additionalAmt);
                if(!StringUtil.isEmpty(amt)){
                    baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, amt).sendToTarget();
                }else{
                    baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Balance unavailable please contact your bank").sendToTarget();
                }
            }
            else{
                responseMsg = StringUtil.isEmpty(responseMsg) ? "Error, please try again later." : responseMsg;
                baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, responseMsg).sendToTarget();
            }
            transInfo.setResponseCode(StringUtil.isEmpty(responseCode) ? "96" : responseCode);
            String authNum = isoMsgResponse.getObjectValue(38);
            transInfo.setAuthNum(StringUtil.isEmpty(authNum) ? "" : authNum);
            transInfo.setCompleted(1);
            transInfoDao.updateResponseCodeAuthNumCompletedByRetRefNo(transInfo.getRetRefNo(), transInfo.getResponseCode(), transInfo.getAuthNum(), transInfo.getCompleted());
            baseHandler.sendEmptyMessage(ConstantUtils.MSG_START_PRINT);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String getAvailableBalance(String transAcctType, String additionalAmt){
        try{
            List<String> amtList = new ArrayList<>();
            while(StringUtil.isEmpty(additionalAmt) || additionalAmt.length() > 0){
                String balDetails = additionalAmt.substring(0, 20);
                String acctType = balDetails.substring(0, 2);
                String balType = balDetails.substring(2, 4);

                if("02".equalsIgnoreCase(balType) && transAcctType.equalsIgnoreCase(acctType)){
                    String currency = ConstantUtils.ISO_CURRENCY_MAP.get(balDetails.substring(4, 7));
                    String sign = "C".equalsIgnoreCase(balDetails.substring(7, 8)) ? "" : "-";
                    String amt = MoneyUtil.kobo2Naira(Long.parseLong(balDetails.substring(8, 20)));
                    return sign + currency + amt;
                }

                amtList.add(balDetails);
                additionalAmt = additionalAmt.substring(20, additionalAmt.length());
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }


    private void print(boolean customerCopy){
        new PrintTransactionThread(mPrinter, baseHandler, transInfo, customerCopy, false).start();
        //baseHandler.obtainMessage(ConstantUtils.MSG_FINISH_PRINT, customerCopy).sendToTarget();
    }


    //The plug-in to display the status of transaction
    private void setTransactionStatusActionTip(String info) {
        tranStatusText.setText(info);
    }

    private void setInfoActionTip(String tip) {
        infoText.setText(tip);
    }

    private void hideTransactionStatusActionTip() {
        tranStatusText.setText("");
    }

    //@Override
    /*protected void onBack() {
        finishAndReturnMainActivity();
    }
    */


    private void back(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }


    @Override
    public void onBackPressed()
    {
        if(!transactionInProgress) {
            super.onBackPressed();
            back();
        }
    }

    private void finishAndReturnMainActivity() {
        finishAppActivity();
        finish();
    }


    //Exit the App call method
    public void finishAppActivity() {
        for (int i = 0; i < WPOSApplication.activityList.size(); i++) {
            Activity ac = WPOSApplication.activityList.get(i);
            if (ac != null) {
                ac.finish();
            }
            ac = null;
        }
        WPOSApplication.activityList.clear();
        //android.os.Process.killProcess(android.os.Process.myPid());
        //System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (InsertCardActivity.mBankCard != null) {
            try {
                InsertCardActivity.mBankCard.breakOffCommand();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    // do reversal
    //  ensure call home works
    // ensure periodic key download tsk decryption works

}
