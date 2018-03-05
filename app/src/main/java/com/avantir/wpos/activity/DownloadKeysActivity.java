package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.model.Error;
import com.avantir.wpos.model.Errors;
import com.avantir.wpos.model.NibssTLV;
import com.avantir.wpos.model.Parameter;
import com.avantir.wpos.services.HttpComms;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.*;
import com.google.gson.Gson;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.util.HexCodec;
import sdk4.wangpos.libemvbinder.CAPK;
import sdk4.wangpos.libemvbinder.EmvAppList;
import sdk4.wangpos.libemvbinder.EmvCore;
import sdk4.wangpos.libemvbinder.EmvParam;
import wangpos.sdk4.libkeymanagerbinder.Key;

import java.util.List;

/**
 * Created by lekanomotayo on 24/01/2018.
 */
public class DownloadKeysActivity extends BaseActivity {

    private String TAG = "DownloadKeysActivity";

    public static final int MSG_DOWNLOAD_TMK = 1000;
    public static final int MSG_DOWNLOAD_TPK = 1001;
    public static final int MSG_DOWNLOAD_TSK = 1002;

    private TextView tmkText;
    private TextView tpkText;
    private TextView tskText;
    private TextView downloadStatusText;

    private String tmkTextMsg;
    private String tpkTextMsg;
    private String tskTextMsg;
    private String downloadStatusTextMsg;
    private Button doneButton;
    private Button retryButton;

    //String ctmk;
    private String tmkData;
    private String tskData;
    private String tpkData;

    GlobalData globalData;
    private TcpComms tcpComms;

    private Key mKey;
    private EmvCore emvCore;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_keys_download);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);
        doneButton = ((Button) findViewById(R.id.done_btn));
        doneButton.setEnabled(false);
        retryButton = ((Button) findViewById(R.id.retry_btn));
        retryButton.setEnabled(false);
        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        /*
        if(GlobalData.getInstance().getIfFirstLaunch()) {
            findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        }
        else{
            ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
            titleBackImage.setOnClickListener(this);
        }
        */
        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Download Keys");

        doneButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);

        tmkText = (TextView) findViewById(R.id.tmkText);
        tpkText = (TextView) findViewById(R.id.tpkText);
        tskText = (TextView) findViewById(R.id.tskText);
        downloadStatusText = (TextView) findViewById(R.id.downloadStatusText);

    }

    @Override
    protected void initData() {

        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
                emvCore = new EmvCore(getApplicationContext());
            }
        }.start();

        tmkTextMsg = tmkText.getText().toString();
        tpkTextMsg = tpkText.getText().toString();
        tskTextMsg = tskText.getText().toString();

        setAllDownloadStatus("pending");


        globalData = GlobalData.getInstance();

        downloadKeys();
        downloadStatusText.setText("Downloading TMK from TMS...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.done_btn:
                goBack();
                break;
            case R.id.retry_btn:
                baseHandler.sendEmptyMessage(MSG_DOWNLOAD_TMK);
                doneButton.setEnabled(false);
                retryButton.setEnabled(false);
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
        //Log.i(PayActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_INFO:
                //setTransactionStatusActionTip();
                break;
            case MSG_DOWNLOAD_TMK:
                downloadTMK();
                break;
            case MSG_DOWNLOAD_TPK:
                downloadTPK();
                break;
            case MSG_DOWNLOAD_TSK:
                downloadTSK();
                break;
            case ConstantUtils.MSG_FINISH_COMMS:
                // end tcpComms
                byte[] receiveData = (byte[])msg.obj;
                Bundle bundleSuccess = msg.getData();
                processResponse(bundleSuccess, receiveData);
                break;
            case  ConstantUtils.MSG_FINISH_ERROR_COMMS:
                // end tcpComms with error
                //int commsErrorCode = Integer.parseInt(msg.obj.toString());
                //Bundle bundleFail = msg.getData(); //display which one is not successful
                //processFailedResponse(bundleFail);
                showToast("Comms error!");
                doneButton.setEnabled(true);
                retryButton.setEnabled(true);
                break;
        }
    }



    private void processResponse(Bundle bundle, byte[] receiveData){
        try{
            int reqType = bundle.getInt(ConstantUtils.NETWORK_REQ_TYPE);
            if(isIsoRequest(reqType)){
                IsoMessage isoMessage = IsoMessageUtil.getInstance().decode(receiveData);
                System.out.println(isoMessage.debugString());
                String responseCode = isoMessage == null ? null : (String) isoMessage.getField(39).getValue();

                if(ConstantUtils.ISO8583_APPROVED.equalsIgnoreCase(responseCode)){
                    if(reqType == ConstantUtils.NETWORK_TMK_DOWNLOAD_REQ_TYPE) {
                        processTMKResponse(isoMessage);
                    }
                    else if(reqType == ConstantUtils.NETWORK_TSK_DOWNLOAD_REQ_TYPE) {
                        processTSKResponse(isoMessage);
                    }
                    else if(reqType == ConstantUtils.NETWORK_TPK_DOWNLOAD_REQ_TYPE) {
                        processTPKResponse(isoMessage);
                    }
                }
            }
            else{
                showToast("Unknown request type!");
                doneButton.setEnabled(true);
                retryButton.setEnabled(true);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            showToast("Error!");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }



    private void downloadKeys(){
        tcpComms = new TcpComms(globalData.getCTMSIP(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
        tcpComms.setHost(globalData.getCTMSIP());
        tcpComms.setPort(globalData.getCTMSPort());
        tcpComms.setTimeout(globalData.getCTMSTimeout());
        tcpComms.setIfSSL(globalData.getIfCTMSSSL());
        baseHandler.sendEmptyMessage(MSG_DOWNLOAD_TMK);
    }

    private void downloadTMK(){
        downloadStatusText.setText("Downloading TMK from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadTMK(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TMK!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    private void processTMKResponse(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded TMK from NIBSS ...");
            //tmkText.setText(tmkTextMsg + "downloaded");
            setTmkStatus("downloaded");
            byte[] keyDataBytes = isoMessage == null ? null : (byte[]) isoMessage.getField(53).getValue();
            String keyData = keyDataBytes == null ? null : HexCodec.hexEncode(keyDataBytes, 0, keyDataBytes.length);
            tmkData = keyData;
            System.out.println("TMK: " + keyData);
            downloadStatusText.setText("Processing TMK response from NIBSS ...");
            baseHandler.sendEmptyMessage(MSG_DOWNLOAD_TPK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            downloadStatusText.setText("TMK Key download failed ...");
            baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TMK!").sendToTarget();
            setTmkStatus("Failed");
            setTpkStatus("Stopped");
            setTskStatus("Stopped");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }

    private void downloadTPK(){
        downloadStatusText.setText("Downloading TPK from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadTPK(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //showToast("Failed to download TPK!");
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TPK!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void processTPKResponse(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded TPK from NIBSS ...");
            //tpkText.setText(tpkTextMsg + "downloaded");
            setTpkStatus("downloaded");
            byte[] keyDataBytes = isoMessage == null ? null : (byte[]) isoMessage.getField(53).getValue();
            String keyData = keyDataBytes == null ? null : HexCodec.hexEncode(keyDataBytes, 0, keyDataBytes.length);
            tpkData = keyData;
            System.out.println("TPK: " + keyData);
            downloadStatusText.setText("Processing TPK response from NIBSS ...");
            baseHandler.sendEmptyMessage(MSG_DOWNLOAD_TSK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            showToast("Failed to download TPK!");
            downloadStatusText.setText("TPK Key download failed ...");
            setTpkStatus("Failed");
            setTskStatus("Stopped");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }


    private void downloadTSK(){
        downloadStatusText.setText("Downloading TSK from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadTSK(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //showToast("Failed to download TSK!");
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TSK!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void processTSKResponse(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded TSK from NIBSS ...");
            tskText.setText(tskTextMsg + "downloaded");
            byte[] keyDataBytes = isoMessage == null ? null : (byte[]) isoMessage.getField(53).getValue();
            String keyData = keyDataBytes == null ? null : HexCodec.hexEncode(keyDataBytes, 0, keyDataBytes.length);
            tskData = keyData;
            System.out.println("TSK: " + keyData);
            downloadStatusText.setText("Processing TSK response from NIBSS ...");

            if(tmkData == null || tskData == null || tpkData == null)
                throw new Exception();

            String tmk = tmkData.substring(0, 32);
            String tmkKcv = tmkData.substring(32, 38);
            String tsk = tskData.substring(0, 32);
            String tskKcv = tskData.substring(32, 38);
            String tpk = tpkData.substring(0, 32);
            String tpkKcv = tpkData.substring(32, 38);

            KeyUtils.saveTMK(mKey, tmk, tmkKcv);
            globalData.setTMKKeyFlag(true);
            KeyUtils.saveTSK(mKey, tsk, tskKcv);
            globalData.setMacKeyFlag(true);
            KeyUtils.saveTPK(mKey, tpk, tpkKcv);
            globalData.setPinKeyFlag(true);


            if(globalData.getLocalMasterKeyLoadedFlag())
                globalData.setKeysLoadedFlag(true);
            globalData.setLastKeyDownloadDate(System.currentTimeMillis());

            setTmkStatus("done (loaded)");
            setTpkStatus("done (loaded)");
            setTskStatus("done (loaded)");
            //showToast("Keys (TMK, TSK, TPK, IPEK Track 2, IPEK EMV) loaded");
        }
        catch(Exception ex){
            ex.printStackTrace();
            //showToast("Failed to download TSK!");
            baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TSK!").sendToTarget();
            downloadStatusText.setText("TSK Key download failed ...");
            setTmkStatus("downloaded (not loaded)");
            setTpkStatus("downloaded (not loaded)");
            setTskStatus("Failed");
        }

        downloadStatusText.setText("All keys downloaded ...");

        doneButton.setEnabled(true);
        retryButton.setEnabled(true);
    }

    private boolean isIsoRequest(int req){
        return req == ConstantUtils.NETWORK_TMK_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_TPK_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_TSK_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_AID_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_CAPK_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_NIBSS_TERM_PARAM_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_IPEK_TRACK2_DOWNLOAD_REQ_TYPE ||
                req == ConstantUtils.NETWORK_IPEK_EMV_DOWNLOAD_REQ_TYPE;
    }


    private void setAllDownloadStatus(String status){
        tmkText.setText(tmkTextMsg + status);
        tpkText.setText(tpkTextMsg + status);
        tskText.setText(tskTextMsg + status);
    }

    private void setTmkStatus(String status){
        tmkText.setText(tmkTextMsg + status);
    }

    private void setTpkStatus(String status){
        tpkText.setText(tpkTextMsg + status);
    }

    private void setTskStatus(String status){
        tskText.setText(tskTextMsg + status);
    }



}

