package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.*;
import com.avantir.wpos.services.HttpComms;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.printer.PrintEoDThread;
import com.avantir.wpos.utils.*;
import com.solab.iso8583.IsoMessage;
import wangpos.sdk4.libbasebinder.Printer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lekanomotayo on 24/01/2018.
 */
public class EoDActivity extends BaseActivity {

    private String TAG = "EndOfDayActivity";

    public static final int MSG_DOWNLOAD_EOD = 1001;

    private TextView eodHeaderText;

    private Button printButton;
    private Button retryButton;

    GlobalData globalData;
    private TcpComms tcpComms;

    private Printer mPrinter;

    HashMap<String, TransInfo> matched;
    HashMap<String, TransInfo> notMatched;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_eod_download);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Print EoD");

        printButton = ((Button) findViewById(R.id.print_btn));
        printButton.setEnabled(false);
        retryButton = ((Button) findViewById(R.id.retry_btn));
        retryButton.setEnabled(false);

        printButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);

        //eodStatusText = (TextView) findViewById(R.id.downloadEoDText);
        eodHeaderText = (TextView) findViewById(R.id.EoDHeader);

    }

    @Override
    protected void initData() {

        matched = new HashMap<>();
        notMatched = new HashMap<>();
        globalData = GlobalData.getInstance();
        baseHandler.sendEmptyMessage(MSG_DOWNLOAD_EOD);
        eodHeaderText.setText("Download EoD Report");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.retry_btn:
                baseHandler.sendEmptyMessage(MSG_DOWNLOAD_EOD);
                printButton.setEnabled(false);
                retryButton.setEnabled(false);
                break;
            case R.id.print_btn:
                baseHandler.sendEmptyMessage(ConstantUtils.MSG_START_PRINT);
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }


    private void goBack(){
        finish();
        skipActivityAnim(-1);
    }


    @Override
    protected void handleMessage(Message msg) {
        Log.i(PayActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_INFO:
                //setTransactionStatusActionTip();
                break;
            case MSG_DOWNLOAD_EOD:
                //getEodFromNIBSS();
                getEodFromDatabase();
                break;
            case ConstantUtils.MSG_FINISH_COMMS:
                // end tcpComms
                byte[] receiveData = (byte[])msg.obj;
                Bundle bundleSuccess = msg.getData();
                processResponse(bundleSuccess, receiveData);
                break;
            case  ConstantUtils.MSG_FINISH_ERROR_COMMS:
                // end tcpComms with error
                showToast("Comms error!");
                retryButton.setEnabled(true);
                break;
            case ConstantUtils.MSG_START_PRINT:
                // Print Receipt
                new PrintEoDThread(mPrinter, baseHandler, matched, notMatched).start();
                //transactionInProgress = false;
                break;
            case ConstantUtils.MSG_FINISH_PRINT:
                // go to main page
                printButton.setEnabled(false);
                retryButton.setEnabled(false);
                break;
        }
    }


    private void getEodFromNIBSS(){
        tcpComms = new TcpComms(globalData.getCTMSIP(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
        tcpComms.setHost(globalData.getCTMSIP());
        tcpComms.setPort(globalData.getCTMSPort());
        tcpComms.setTimeout(globalData.getCTMSTimeout());
        tcpComms.setIfSSL(globalData.getIfCTMSSSL());

        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadEoD(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download EoD!").sendToTarget();
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    private void processResponse(Bundle bundle, byte[] receiveData){
        try{
            int reqType = bundle.getInt(ConstantUtils.NETWORK_REQ_TYPE);
            IsoMessage isoMessage = IsoMessageUtil.getInstance().decode(receiveData);
            System.out.println(isoMessage.debugString());
            String responseCode = isoMessage == null ? null : (String) isoMessage.getObjectValue(39);

            if(reqType == ConstantUtils.NETWORK_EOD_DOWNLOAD_REQ_TYPE){
                if(ConstantUtils.ISO8583_APPROVED.equalsIgnoreCase(responseCode)) {

                    String  eodData = isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                    System.out.println("EoD: " + eodData);
                    List<List<NibssTLV>> eodTlvListList = NibssTLVUtil.parseTLVData(eodData);
                    HashMap<String, EndOfDay> endOfDayList = NibssTLVUtil.getEndOfDayList(eodTlvListList);

                    TransInfoDao transInfoDao = new TransInfoDao(WPOSApplication.app);
                    List<TransInfo> transInfoList = transInfoDao.findByToday();
                    if(transInfoList != null){
                        for(TransInfo transInfo: transInfoList){
                            String transDateTime = transInfo.getTransmissionDateTime();
                            if(endOfDayList.containsKey(transDateTime)){
                                EndOfDay endOfDay = endOfDayList.get(transDateTime);
                                String amt = transInfo.getAmt();
                                boolean amtMatch = amt.equalsIgnoreCase(endOfDay.getAmt());
                                boolean respCodeMatch = transInfo.getResponseCode().equalsIgnoreCase(endOfDay.getRespCode());
                                String tranType = transInfo.getProcCode().substring(0, 2);
                                boolean tranTypeMatch = tranType.equalsIgnoreCase(endOfDay.getTransType());
                                if(amtMatch && respCodeMatch && tranTypeMatch)
                                    matched.put(transDateTime, transInfo);
                                else
                                    notMatched.put(transDateTime, transInfo);
                            }
                            else{
                                notMatched.put(transInfo.getTransmissionDateTime(), transInfo);
                            }
                        }
                        printButton.setEnabled(true);
                    }
                    else{
                        showToast("Nothing to print!");
                    }
                }
                else{
                    showToast("Failed to download EoD!");
                }
            }
            else{
                showToast("Unknown request type!");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            showToast("Error!");
        }
        retryButton.setEnabled(true);
    }


    private void getEodFromDatabase(){
        TransInfoDao transInfoDao = new TransInfoDao(WPOSApplication.app);
        List<TransInfo> transInfoList = transInfoDao.findByToday();
        if(transInfoList != null){
            for(TransInfo transInfo: transInfoList){
                String transDateTime = transInfo.getTransmissionDateTime();
                matched.put(transDateTime, transInfo);
            }
            printButton.setEnabled(true);
        }
        else{
            showToast("Nothing to print!");
        }
        retryButton.setEnabled(true);
    }

    private void back(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(EoDActivity.this, MainActivity.class));
        finish();
    }
}

