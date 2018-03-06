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
import android.widget.ImageView;
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
public class DownloadTermParamsActivity extends BaseActivity {

    private String TAG = "DownloadTermParamsActivity";

    public static final int MSG_DOWNLOAD_TMK = 1000;
    public static final int MSG_DOWNLOAD_TPK = 1001;
    public static final int MSG_DOWNLOAD_TSK = 1002;
    public static final int MSG_DOWNLOAD_AID = 1003;
    public static final int MSG_DOWNLOAD_CAPK = 1004;
    public static final int MSG_DOWNLOAD_NIBSS_TERM_PARAM = 1005;
    public static final int MSG_DOWNLOAD_FROM_MGT_SERVER = 1006;

    private TextView ctmkText;
    private TextView cbdkText;
    private TextView termParamText;
    private TextView tmkText;
    private TextView tpkText;
    private TextView tskText;
    private TextView aidText;
    private TextView capkText;
    private TextView emvParamText;
    private TextView downloadStatusText;
    //private TextView status;

    private String ctmkTextMsg;
    private String cbdkTextMsg;
    private String termParamTextMsg;
    private String tmkTextMsg;
    private String tpkTextMsg;
    private String tskTextMsg;
    private String aidTextMsg;
    private String capkTextMsg;
    private String emvParamTextMsg;
    private String downloadStatusTextMsg;
    private Button doneButton;
    private Button retryButton;

    //String ctmk;
    private String tmkData;
    private String tskData;
    private String tpkData;

    GlobalData globalData;
    private TcpComms tcpComms;
    private HttpComms httpComms;

    private Key mKey;
    private EmvCore emvCore;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_term_params_download);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
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
        titleNameText.setText("Download Term Params");

        doneButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);

        ctmkText = (TextView) findViewById(R.id.ctmkText);
        cbdkText = (TextView) findViewById(R.id.cbdkText);
        termParamText = (TextView) findViewById(R.id.termParamText);
        tmkText = (TextView) findViewById(R.id.tmkText);
        tpkText = (TextView) findViewById(R.id.tpkText);
        tskText = (TextView) findViewById(R.id.tskText);
        aidText = (TextView) findViewById(R.id.aidText);
        capkText = (TextView) findViewById(R.id.capkText);
        emvParamText = (TextView) findViewById(R.id.emvParamText);
        downloadStatusText = (TextView) findViewById(R.id.downloadStatusText);

        //status = (TextView) findViewById(R.id.status);
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

        ctmkTextMsg = ctmkText.getText().toString();
        cbdkTextMsg = cbdkText.getText().toString();
        emvParamTextMsg = emvParamText.getText().toString();
        tmkTextMsg = tmkText.getText().toString();
        tpkTextMsg = tpkText.getText().toString();
        tskTextMsg = tskText.getText().toString();
        aidTextMsg = aidText.getText().toString();
        capkTextMsg = capkText.getText().toString();
        termParamTextMsg = termParamText.getText().toString();
        //downloadStatusTextMsg = downloadStatusText.getText().toString();


        TextView devSerialNoText = ((TextView) findViewById(R.id.deviceSerialNo));
        String msg = devSerialNoText.getText().toString() + Build.SERIAL;
        devSerialNoText.setText(msg);
        setAllDownloadStatus("pending");
        /*
        ctmkText.setText(ctmkTextMsg + "pending");
        cbdkText.setText(cbdkTextMsg + "pending");
        emvParamText.setText(emvParamTextMsg + "pending");
        tmkText.setText(tmkTextMsg + "pending");
        tpkText.setText(tpkTextMsg + "pending");
        tskText.setText(tskTextMsg + "pending");
        aidText.setText(aidTextMsg + "pending");
        capkText.setText(capkTextMsg + "pending");
        termParamText.setText(termParamTextMsg + "pending");
        */


        globalData = GlobalData.getInstance();
        httpComms = HttpComms.getInstance(globalData.getTMSHost(), globalData.getTMSPort(), globalData.getTMSTimeout(), globalData.getIfTMSSSL(), null);

        baseHandler.sendEmptyMessage(MSG_DOWNLOAD_FROM_MGT_SERVER);
        downloadStatusText.setText("Downloading params from mgt server...");
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
                baseHandler.sendEmptyMessage(MSG_DOWNLOAD_FROM_MGT_SERVER);
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
        if(globalData.getIfFirstLaunch()){
            globalData.setIfFirstLaunch(false);
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
        else{
            finish();
            skipActivityAnim(-1);
        }
    }


    @Override
    protected void handleMessage(Message msg) {
        Log.i(PayActivity.class.getSimpleName(), "handleMessage: "+msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_INFO:
                //setTransactionStatusActionTip();
                break;
            case MSG_DOWNLOAD_FROM_MGT_SERVER:
                downloadParamsFromTMS();
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
            case MSG_DOWNLOAD_AID:
                downloadAIDs();
                break;
            case MSG_DOWNLOAD_CAPK:
                downloadCAPKs();
                break;
            case MSG_DOWNLOAD_NIBSS_TERM_PARAM:
                downloadTermParamFromNIBSS();
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


    private void downloadParamsFromTMS(){
        httpComms.setHost(globalData.getTMSHost());
        httpComms.setPort(globalData.getTMSPort());
        httpComms.setTimeout(globalData.getTMSTimeout());
        httpComms.setIfSSL(globalData.getIfTMSSSL());

        Runnable r = new Runnable(){
            public void run() {
                try{
                    TMSDownload.downloadTerminalParams(baseHandler, httpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    showToast("Failed to download Terminal Parameter from TMS!");
                    doneButton.setEnabled(true);
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
                    else if(reqType == ConstantUtils.NETWORK_AID_DOWNLOAD_REQ_TYPE) {
                        processAIDResponse(isoMessage);
                    }
                    else if(reqType == ConstantUtils.NETWORK_CAPK_DOWNLOAD_REQ_TYPE) {
                        processCAPKResponse(isoMessage);
                    }
                    else if(reqType == ConstantUtils.NETWORK_NIBSS_TERM_PARAM_DOWNLOAD_REQ_TYPE) {
                        processTermParamResponseFromNIBSS(isoMessage);
                    }
                }
            }
            else if(reqType == ConstantUtils.NETWORK_TMS_TERM_PARAM_DOWNLOAD_REQ_TYPE) {
                processTMSTermParamResponse(new String(receiveData));
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





    private void processTMSTermParamResponse(String response){
        try{
            downloadStatusText.setText("Downloaded params from mgt server...");
            Gson gson = new Gson();
            Parameter parameter = gson.fromJson(response, Parameter.class);
            if(parameter != null && parameter.getCtmk() != null && !parameter.getCtmk().isEmpty()) {

                String encryptedBase64Ctmk = parameter.getCtmk();
                String ctmk = KeyUtils.decryptBase64StringWithRSA(encryptedBase64Ctmk);
                if (ctmk == null){
                    //ctmkText.setText(ctmkTextMsg + "failed");
                    setCtmkStatus("failed");
                    throw new Exception("Unable able to decrypt CTMK");
                }
                String ctmkKcv = parameter.getCtmkChkDigit();
                int ret = KeyUtils.saveCTMK(mKey, ctmk, ctmkKcv, encryptedBase64Ctmk);
                if(ret != 0)
                    throw new Exception("Unable able to save CTMK");

                globalData.setLocalMasterKeyLoadedFlag(true);
                //ctmkText.setText(ctmkTextMsg + "done");
                setCtmkStatus("done");

                //cbdkText.setText(cbdkTextMsg + "skipped");
                setBdkStatus("skipped");


                String merchantName = parameter.getMerchantName();
                String terminalId = parameter.getTerminalId();
                int termType = parameter.getTermType(); //Attended, Online with offline capability
                int termCapabilities = Integer.parseInt(parameter.getTermCap(), 16);; //  0xE0F9C8, E090C8, IC with contacts, Enciphered PIN for online verification, Enciphered PIN for offline verification, DDA
                String extraTermCapabilities = parameter.getTermExCap(); // "7F80C0F0FF", E000F0A001, Goods,Services,  Cashback, Inquiry, Transfer, Payment, Administrative, Cash Deposit, Numeric keys,Alphabetic and special character key attendant Print, cardholder Print, attendant Display, cardholder Display, Code table 1-8
                int forcedOnline = parameter.isForceOnline() ? 0x01 : 0x00;
                String posDataCode = parameter.getPosDataCode(); //510101511344101 , 511201213344002
                String iccData = parameter.getIccData(); //"9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F34,9F33,9F35,9F03";
                String referenceCurrencyCode = parameter.getTransRefCurr();
                String transCurrencyExponent = String.valueOf(parameter.getTransCurrExp()); // https://en.wikipedia.org/wiki/ISO_4217#Treatment_of_minor_currency_units_(the_"exponent")
                String referenceCurrencyExponent = String.valueOf(parameter.getRefCurrExp());
                int referenceCurrencyConversion = parameter.getRefCurrConv();
                int defaultTDOL = parameter.isSupportDefaultTDOL() ? 0x01 : 0x00;
                int defaultDDOL = parameter.isSupportDefaultDDOL() ? 0x01 : 0x00;
                int supportPSESelection = parameter.isSupportPSESelection() ? 0x01 : 0x00;
                int tranType = ConstantUtils.GOODS_TRAN_TYPE;
                int getDataPin = ConstantUtils.GET_PIN_DATA; //Whether to get the PIN retry times TAG9F17 0x01 Yes;  0x00 No
                String termTransQuali = ConstantUtils.TERM_TRANSACTION_QUALITY; // "26800080"

                globalData.setCTMSHost("ctms.nibss-plc.com");
                globalData.setCTMSIP(parameter.getTmsIp()); // 41.58.130.139
                globalData.setCTMSPort(parameter.getTmsPort());
                globalData.setCTMSTimeout(parameter.getTmsTimeout());
                globalData.setIfCTMSSSL(parameter.isTmsSsl());
                globalData.setMerchantName(merchantName);
                globalData.setTerminalId(terminalId);
                globalData.setAcquirerId(parameter.getAcquirer());
                globalData.setKeyDownloadPeriodInMin(parameter.getKeyDownlTimeInMin());
                globalData.setCheckKeyDownloadIntervalInMin(parameter.getKeyDownlIntervalInMin());
                globalData.setCurrencyCode(parameter.getTransCurr());
                globalData.setPOSDataCode(posDataCode);
                globalData.setICCData(iccData);
                globalData.setResendReversalPeriodInMin(60); // get from remote
                globalData.setPageTimerInSec(60);
                globalData.setPTSP("Arca Networks");

                byte[] outData = new byte[1024];
                int[] outStatus = new int[1];
                emvCore.getParam(outData, outStatus);
                EmvParam emvParam = new EmvParam(getApplicationContext());
                emvParam.parseByteArray(outData);
                emvParam.setMerchName(merchantName);
                emvParam.setTermId(terminalId);
                emvParam.setForceOnline(forcedOnline); // go online
                emvParam.setTermCapab(termCapabilities);
                emvParam.setExTermCapab(extraTermCapabilities);
                emvParam.setTerminalType(termType);
                emvParam.setReferCurrCode(referenceCurrencyCode);
                emvParam.setReferCurrCon(referenceCurrencyConversion);
                emvParam.setReferCurrExp(referenceCurrencyExponent);
                emvParam.setTransCurrExp(transCurrencyExponent);
                emvParam.setTransType(tranType);
                emvParam.setSupportDefaultDDOL(defaultDDOL);
                emvParam.setSupportDefaultTDOL(defaultTDOL);
                emvParam.setSupportPSESel(supportPSESelection);
                emvParam.setTermTransQuali(termTransQuali);
                emvParam.setGetDataPIN(getDataPin);
                emvCore.setParam(emvParam.toByteArray());

                //emvParamText.setText(emvParamTextMsg + "in progress");
                setEmvParamStatus("in progress");

                downloadKeys();
            }
            else{
                showToast("Failed to download Terminal Parameters!");
                setAllDownloadStatus("failed");
                Errors errors = gson.fromJson(response, Errors.class);
                if(errors != null && errors.getErrors() != null && errors.getErrors()[0].getCode() != null){
                    Error error = errors.getErrors()[0];
                    downloadStatusText.setText("Failed: " + error.getMessage());
                }
                else{
                    downloadStatusText.setText("Failed to download params from mgt server...");
                }
                doneButton.setEnabled(true);
                retryButton.setEnabled(true);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            showToast("Failed to download Terminal Parameters from mgt server!");
            downloadStatusText.setText("Failed to download params from mgt server...");
            /// set all status thats not done to fail
            setCtmkStatus("Failed");
            setBdkStatus("Failed");
            setEmvParamStatus("Failed");
            setTmkStatus("Stopped");
            setTpkStatus("Stopped");
            setTskStatus("Stopped");
            setTermParamStatus("Stopped");
            setCapkStatus("Stopped");
            setAidStatus("Stopped");
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
            setTermParamStatus("Stopped");
            setCapkStatus("Stopped");
            setAidStatus("Stopped");
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
            setTermParamStatus("Stopped");
            setCapkStatus("Stopped");
            setAidStatus("Stopped");
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

            /*
            tmkText.setText(tmkTextMsg + "");
            tpkText.setText(tpkTextMsg + "done (loaded)");
            tskText.setText(tskTextMsg + "done (loaded)");
            */
            setTmkStatus("done (loaded)");
            setTpkStatus("done (loaded)");
            setTskStatus("done (loaded)");
            //showToast("Keys (TMK, TSK, TPK, IPEK Track 2, IPEK EMV) loaded");

            baseHandler.sendEmptyMessage(MSG_DOWNLOAD_NIBSS_TERM_PARAM);
        }
        catch(Exception ex){
            ex.printStackTrace();
            //showToast("Failed to download TSK!");
            baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download TSK!").sendToTarget();
            downloadStatusText.setText("TSK Key download failed ...");
            setTmkStatus("downloaded (not loaded)");
            setTpkStatus("downloaded (not loaded)");
            setTskStatus("Failed");
            setTermParamStatus("Stopped");
            setCapkStatus("Stopped");
            setAidStatus("Stopped");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }


    private void downloadTermParamFromNIBSS(){
        downloadStatusText.setText("Downloading Terminal Params from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadTerminalParam(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //showToast("Failed to download Terminal Parameter from NIBSS CTMS!");
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download Term Param form NIBSS!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void processTermParamResponseFromNIBSS(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded Terminal Param from NIBSS ...");
            termParamText.setText(termParamTextMsg + "downloaded");
            String  responseCodeTermParam = isoMessage == null ? null : (String) isoMessage.getField(39).getValue();
            if("00".equalsIgnoreCase(responseCodeTermParam)){
                //String mgtData1 = isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                String  termParamData= isoMessage == null ? null : (String) isoMessage.getField(62).getValue();
                System.out.println("Terminal Parameter: " + termParamData);
                 /*
                    02 CTMS Date and Time 014
                    03 Card Acceptor Identification Code 015
                    04 Timeout (maximum time interval to wait for response – in seconds) 002
                    05 Currency Code 003
                    06 Country Code 003
                    07 Call home time (maximum time interval idleness for which a call – home must be done – in hours) 002
                    52 Merchant Name and Location 040
                    08 Merchant Category Code 004
                 */
                List<List<NibssTLV>> termParamTlvListList = NibssTLVUtil.parseTLVData(termParamData);
                List<NibssTLV> termParamTlvList = termParamTlvListList.get(0);
                String merchantId = ""; // MTNNIGERIA0000001
                String merchantLocation = "";
                String merchantCategoryCode = ""; // 5999, "5399";//5311 - Dept Stores, 5331 - Variety Stores, 5399 - Misc. General Merchandise, 5411 - Grocery Stores & Supermarkets
                String countryCodeStr = "0566"; //
                String transCurrencyCode = "0566"; //0566
                int callHomeTimeInMin = 0; // 60
                int serverTimeoutInSec = 0; // 60

                for(NibssTLV tlv: termParamTlvList){
                    if("03".equalsIgnoreCase(tlv.getTag()))
                        merchantId = tlv.getValue();
                    else if("04".equalsIgnoreCase(tlv.getTag()))
                        serverTimeoutInSec = Integer.parseInt(tlv.getValue());
                    else if("05".equalsIgnoreCase(tlv.getTag()))
                        transCurrencyCode = "0" + tlv.getValue();
                    else if("06".equalsIgnoreCase(tlv.getTag()))
                        countryCodeStr = "0" + tlv.getValue();
                    else if("07".equalsIgnoreCase(tlv.getTag()))
                        callHomeTimeInMin = Integer.parseInt(tlv.getValue()) * 60;
                    else if("08".equalsIgnoreCase(tlv.getTag()))
                        merchantCategoryCode = tlv.getValue();
                    else if("52".equalsIgnoreCase(tlv.getTag()))
                        merchantLocation = tlv.getValue();
                }


                globalData.setMerchantId(merchantId);
                globalData.setMerchantCategoryCode(merchantCategoryCode);
                globalData.setMerchantLoc(merchantLocation);
                globalData.setCallHomePeriodInMin(callHomeTimeInMin);
                globalData.setCTMSTimeout(serverTimeoutInSec);

                    /*
                    String terminalId = emvParam.getTermId();
                    int forcedOnline = emvParam.getForceOnline();
                    int termCapabilities = emvParam.getTermCapab();
                    String extraTermCapabilities = emvParam.getExTermCapab();
                    int termType = emvParam.getTerminalType();
                    */

                byte[] outData = new byte[1024];
                int[] outStatus = new int[1];
                emvCore.getParam(outData, outStatus);
                EmvParam emvParam = new EmvParam(getApplicationContext());
                emvParam.parseByteArray(outData);
                emvParam.setMerchId(merchantId);
                emvParam.setMerchCateCode(merchantCategoryCode);
                emvParam.setTransCurrCode(transCurrencyCode);
                emvParam.setCountryCode(countryCodeStr);
                emvCore.setParam(emvParam.toByteArray());
                globalData.setTerminalParamsLoadedFlag(true);
                globalData.setEMVParamsLoadedFlag(true);

                emvParamText.setText(emvParamTextMsg + "done (loaded)");
                termParamText.setText(termParamTextMsg + "done (loaded)");

                baseHandler.sendEmptyMessage(MSG_DOWNLOAD_CAPK);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            showToast("Failed to download Terminal Param!");
            downloadStatusText.setText("Terminal Param download failed ...");
            setTermParamStatus("Failed");
            setCapkStatus("Stopped");
            setAidStatus("Stopped");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }



    private void downloadCAPKs(){
        downloadStatusText.setText("Downloading CAPK from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadCAPK(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //showToast("Failed to download CAPK!");
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download CAPK!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    private void processCAPKResponse(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded CAPK from NIBSS ...");
            capkText.setText(capkTextMsg + "downloaded");
            String  responseCodeCapk = isoMessage == null ? null : (String) isoMessage.getField(39).getValue();
            if("00".equalsIgnoreCase(responseCodeCapk)){
                String  capkData= isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                //String mgtData1 = isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                System.out.println("CAPK: " + capkData);
                 /*
                    32 Certificate Authority (CA) Key Index
                    33 CA Key Internal Reference Number
                    34 CA Key Name
                    35 EMV RID
                    36 Hash Algorithm
                    37 EMV CA PK Modulus
                    38 EMV CA PK Exponent
                    39 EMV CA PK Hash
                */
                List<List<NibssTLV>> capkTlvListList = NibssTLVUtil.parseTLVData(capkData);
                String capksFromNibss[] = NibssTLVUtil.getCAPKAsStringArray(capkTlvListList);
                String[] capks = new String[capksFromNibss.length + ConstantUtils.CAPK_DATA.length];
                System.arraycopy(ConstantUtils.CAPK_DATA, 0, capks, 0, ConstantUtils.CAPK_DATA.length);
                System.arraycopy(capksFromNibss, 0, capks, ConstantUtils.CAPK_DATA.length, capksFromNibss.length);

                try {
                    emvCore.delAllCAPK();//delete capk
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
                boolean bRet;
                for (int i = 0; i < capks.length; i++) {
                    CAPK capk = TLVUtil.getCAPK(this, capks[i]);
                    byte[] capkByte = capk.toByteArray();
                    int result = emvCore.addCAPK(capkByte);
                    Log.e("addCapk_Result", result + "");
                }
                globalData.setCAPKLoadedFlag(true);
                capkText.setText(capkTextMsg + "done (loaded)");

                baseHandler.sendEmptyMessage(MSG_DOWNLOAD_AID);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            //showToast("Failed to download CAPK!");
            baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download CAPK!").sendToTarget();
            downloadStatusText.setText("CAPK download failed ...");
            setCapkStatus("Failed");
            setAidStatus("Stopped");
            doneButton.setEnabled(true);
            retryButton.setEnabled(true);
        }
    }



    private void downloadAIDs(){
        downloadStatusText.setText("Downloading AID from NIBSS ...");
        Runnable r = new Runnable(){
            public void run() {
                try{
                    NIBSSRequests.downloadAID(baseHandler, tcpComms);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //showToast("Failed to download AID!");
                    baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download AID!").sendToTarget();
                    doneButton.setEnabled(true);
                    retryButton.setEnabled(true);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void processAIDResponse(IsoMessage isoMessage){
        try{
            downloadStatusText.setText("Downloaded AID from NIBSS ...");
            aidText.setText(aidTextMsg + "downloaded");
            String  responseCodeAid = isoMessage == null ? null : (String) isoMessage.getField(39).getValue();
            if("00".equalsIgnoreCase(responseCodeAid)){
                String  aidData= isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                //String mgtData1 = isoMessage == null ? null : (String) isoMessage.getField(63).getValue();
                System.out.println("AID: " + aidData);
                /*
                    13 AID Index
                    14 Application Internal Reference Number
                    15 Application Identification Number (EMV AID)
                    16 Match 001
                    17 EMV Application Name
                    18 EMV Application Version
                    19 EMV Application Selection Priority
                    20 EMV DDOL
                    21 EMV TDOL
                    22 EMV TFL Domestic
                    23 EMV TFL International
                    24 EMV Offline Threshold Domestic
                    25 EMV Max Target Domestic
                    26 EMV Max Target International
                    27 EMV Target Percentage Domestic
                    28 EMV Target Percentage International
                    29 Default EMV TAC Value
                    30 EMV TAC Denial
                    31 EMV TAC Online
                */
                List<List<NibssTLV>> aidDataTlvListList = NibssTLVUtil.parseTLVData(aidData);
                String aidsFromNibss[] = NibssTLVUtil.getAIDAsStringArray(aidDataTlvListList);
                String[] aids = new String[aidsFromNibss.length + ConstantUtils.AID_DATA.length];
                System.arraycopy(ConstantUtils.AID_DATA, 0, aids, 0, ConstantUtils.AID_DATA.length);
                System.arraycopy(aidsFromNibss, 0, aids, ConstantUtils.AID_DATA.length, aidsFromNibss.length);
                downloadStatusText.setText("Processing AID response from NIBSS ...");

                try {
                    emvCore.delAllAID();//delete Aid
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
                for (int j = 0; j < aids.length; j++) {
                    EmvAppList emvAppList = TLVUtil.getAID(this, aids[j]);
                    emvCore.addAID(emvAppList.toByteArray());
                }
                globalData.setAIDLoadedFlag(true);
                aidText.setText(aidTextMsg + "done (loaded)");
                downloadStatusText.setText("Terminal Parameter downloaded ...");
                globalData.setLogin(true);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            //showToast("Failed to download AID!");
            baseHandler.obtainMessage(ConstantUtils.ShowToastFlag, "Failed to download AID!").sendToTarget();
            downloadStatusText.setText("AID download failed ...");
            setAidStatus("Failed");
        }
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
        ctmkText.setText(ctmkTextMsg + status);
        cbdkText.setText(cbdkTextMsg + status);
        emvParamText.setText(emvParamTextMsg + status);
        tmkText.setText(tmkTextMsg + status);
        tpkText.setText(tpkTextMsg + status);
        tskText.setText(tskTextMsg + status);
        aidText.setText(aidTextMsg + status);
        capkText.setText(capkTextMsg + status);
        termParamText.setText(termParamTextMsg + status);
    }

    private void setCtmkStatus(String status){
        ctmkText.setText(ctmkTextMsg + status);
    }

    private void setBdkStatus(String status){
        cbdkText.setText(cbdkTextMsg + status);
    }

    private void setEmvParamStatus(String status){
        emvParamText.setText(emvParamTextMsg + status);
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

    private void setAidStatus(String status){
        aidText.setText(aidTextMsg + status);
    }

    private void setCapkStatus(String status){
        capkText.setText(capkTextMsg + status);
    }

    private void setTermParamStatus(String status){
        termParamText.setText(termParamTextMsg + status);
    }


}

