package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.avantir.wpos.R;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        this.findViewById(R.id.setup_mgt_host_btn).setOnClickListener(this);
        this.findViewById(R.id.download_terminal_params_btn).setOnClickListener(this);
        this.findViewById(R.id.titleBackImage).setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                //finishAppActivity();
                skipActivityAnim(-1);
                break;
            case R.id.download_terminal_params_btn:
                Intent downloadTermParamsIntent = new Intent(this, DownloadTermParamsActivity.class);
                startActivity(downloadTermParamsIntent);
                break;
            case R.id.setup_mgt_host_btn:
                Intent hostConfigIntent = new Intent(this, HostConfigActivity.class);
                startActivity(hostConfigIntent);
                break;
        }
    }


    private void setTerminalParam() throws Exception{

        /*
        // From NIBSS
        String merchantId = "MTNNIGERIA0000001";
        String merchantLocation = "Golden Plaza Building Awolowo Road, Falomo, Ikoyi, Lagos, Nigeria";
        String merchantName = "MTN Nigeria";
        String merchantCategoryCode = "5399"; // 5999, "5399";//5311 - Dept Stores, 5331 - Variety Stores, 5399 - Misc. General Merchandise, 5411 - Grocery Stores & Supermarkets
        String referenceCurrencyCode = "0566";
        String countryCodeStr = "0566";
        String transCurrencyCode = "0566";
        int callHomeTimeInMin = 60;

        // Supplied (Get from server)
        String ctmsHost = "196.6.103.72"; //NIBSS test host
        int ctmsPort = 5042; //NIBSS test port
        int ctmsTimeout = 60;
        boolean ifCTMSSSL = false;
        String tmsHost = "192.168.1.66"; //TMS (Mgt test host)
        int tmsPort = 8080; //NIBSS test port
        int tmsTimeout = 60;
        boolean ifTMSSSL = false;


        String terminalId = "20390059";
        String acquirerId = "628051";
        int keyDownloadTimeInMin = 1440;
        int checkKeyDownloadIntervalInMin = 30;
        int referenceCurrencyConversion = 0;


        //  Standard
        // https://cert.api2.heartlandportico.com/Gateway/PorticoDevGuide/build/PorticoDeveloperGuide/PDL%20Response%20Table%2030%20-%20Terminal%20Data.html
        // https://cert.api2.heartlandportico.com/Gateway/PorticoDevGuide/build/PorticoDeveloperGuide/PDL%20Response%20Table%2040%20-%20Contact%20Card%20Data.html
        int termType = 22; //Attended, Online with offline capability
        int termCapabilities = 0xE0F9C8; //  E090C8, IC with contacts, Enciphered PIN for online verification, Enciphered PIN for offline verification, DDA
        String extraTermCapabilities = "7F80C0F0FF"; // E000F0A001, Goods,Services,  Cashback, Inquiry, Transfer, Payment, Administrative, Cash Deposit, Numeric keys,Alphabetic and special character key attendant Print, cardholder Print, attendant Display, cardholder Display, Code table 1-8
        String transCurrencyExponent = "2"; // https://en.wikipedia.org/wiki/ISO_4217#Treatment_of_minor_currency_units_(the_"exponent")
        String referenceCurrencyExponent = "2";
        int forcedOnline = 0x01;
        int tranType = 0x02;
        int defaultTDOL = 0x01; // 9F3704
        int defaultDDOL = 0x01;
        int supportPSESelection = 0x01; // 0 - false, 1 - true
        int getDataPin = 0x01; //Whether to get the PIN retry times TAG9F17 0x01 Yes;  0x00 No
        String termTransQuali = "26800080"; // "26800080"
        String posDataCode = "510101511344101"; //510101511344101 , 511201213344002
        String iccData = "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F34,9F33,9F35,9F03";

        //globalData.setRetrievalRef();
        //globalData.setStan(stan);
        //globalData.setPurchaseSurcharge();
        globalData.setCTMSHost(ctmsHost);
        globalData.setCTMSPort(ctmsPort);
        globalData.setCTMSTimeout(ctmsTimeout);
        globalData.setIfCTMSSSL(ifCTMSSSL);

        globalData.setTMSHost(tmsHost);
        globalData.setTMSPort(tmsPort);
        globalData.setTMSTimeout(tmsTimeout);
        globalData.setIfTMSSSL(ifTMSSSL);

        // NIBSS
        globalData.setMerchantId(merchantId);
        globalData.setMerchantName(merchantName);
        globalData.setMerchantCategoryCode(merchantCategoryCode);
        globalData.setMerchantLoc(merchantLocation);
        globalData.setCallHomePeriodInMin(callHomeTimeInMin); // ideally shld be set after terminal params download

        globalData.setTerminalId(terminalId);
        globalData.setAcquirerId(acquirerId);
        globalData.setKeyDownloadPeriodInMin(keyDownloadTimeInMin);
        globalData.setCheckKeyDownloadIntervalInMin(checkKeyDownloadIntervalInMin);
        globalData.setCurrencyCode(transCurrencyCode);
        globalData.setPOSDataCode(posDataCode);
        globalData.setICCData(iccData);



        EmvParam emvParam = new EmvParam(getApplicationContext());
        byte[] outData = new byte[1024];
        int[] outStaus = {1024};
        emvCore.getParam(outData, outStaus);
        emvParam.parseByteArray(outData);
        emvParam.setMerchId(merchantId);
        emvParam.setMerchName(merchantName);
        emvParam.setMerchCateCode(merchantCategoryCode);
        emvParam.setTransCurrCode(transCurrencyCode);
        emvParam.setReferCurrCode(referenceCurrencyCode);
        emvParam.setCountryCode(countryCodeStr);


        emvParam.setTermId(terminalId);
        emvParam.setForceOnline(forcedOnline); // go online
        emvParam.setTermCapab(termCapabilities);
        emvParam.setExTermCapab(extraTermCapabilities);
        emvParam.setTerminalType(termType);
        emvParam.setReferCurrCon(referenceCurrencyConversion);
        emvParam.setReferCurrExp(referenceCurrencyExponent);
        emvParam.setTransCurrExp(transCurrencyExponent);
        emvParam.setTransType(tranType);
        emvParam.setSupportDefaultDDOL(defaultDDOL);
        emvParam.setSupportDefaultTDOL(defaultTDOL);
        emvParam.setSupportPSESel(supportPSESelection);
        emvParam.setTermTransQuali(termTransQuali);
        emvParam.setGetDataPIN(getDataPin);
        //emvParam.setFixedCashBackAmount(0x00);
        //emvParam.setECTSI(0x01);
        //emvParam.setUcDataCaptureFlag(0x01);

        emvCore.setParam(emvParam.toByteArray());
        globalData.setEMVParamsLoadedFlag(true);
        globalData.setTerminalParamsLoadedFlag(true);
        LOGD("posEmvParam 1:"+emvParam.toString());
        */
    }


}
