package com.avantir.wpos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.math.BigInteger;

/**
 * @author liudy
 */
public class GlobalData {

    private static final String TAG = "GlobalData";
    private static final String PREFERENCES = "global_data";
    public Context mContext;
    private SharedPreferences mPrefs;
    private Editor mEditor;
    private static GlobalData instance = null;

    /**
     * keys define
     */



    public static boolean ifEntransActivityExist = false;
    private GlobalData() {

    }

    public void init(Context context) {
        if (instance == null) {
            instance = new GlobalData();
        }
        mContext = context;
        mPrefs = mContext.getSharedPreferences(PREFERENCES, Context.MODE_WORLD_READABLE);
        mEditor = mPrefs.edit();
    }

    public static GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }


    public void setKeysLoadedFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.KEYSLOADED, ifset);
        mEditor.commit();
    }
    public boolean getKeysLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.KEYSLOADED, false);
    }

    public void setLocalMasterKeyLoadedFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.LOCALMASTERKEYLOADED, ifset);
        mEditor.commit();
    }
    public boolean getLocalMasterKeyLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.LOCALMASTERKEYLOADED, false);
    }

    public void setTMKKeyFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.SETTMKKEY, ifset);
        mEditor.commit();
    }
    public boolean getTMKKeyFlag() {
        return mPrefs.getBoolean(ConstantUtils.SETTMKKEY, false);
    }

    public void setMacKeyFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.SETMACKEY, ifset);
        mEditor.commit();
    }
    public boolean getMacKeyFlag() {
        return mPrefs.getBoolean(ConstantUtils.SETMACKEY, false);
    }

    public void setPinKeyFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.SETPINKEY, ifset);
        mEditor.commit();
    }
    public boolean getPinKeyFlag() {
        return mPrefs.getBoolean(ConstantUtils.SETPINKEY, false);
    }

    public void setAIDLoadedFlag(boolean ifAIDLoaded) {
        mEditor.putBoolean(ConstantUtils.AID_LOADED, ifAIDLoaded);
        mEditor.commit();
    }
    public boolean getAIDLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.AID_LOADED, false);
    }

    public void setCAPKLoadedFlag(boolean ifCAPKLoaded) {
        mEditor.putBoolean(ConstantUtils.CAPK_LOADED, ifCAPKLoaded);
        mEditor.commit();
    }
    public boolean getCAPKLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.CAPK_LOADED, false);
    }

    public void setEMVParamsLoadedFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.EMVPARAMSLOADED, ifset);
        mEditor.commit();
    }
    public boolean getEMVParamsLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.EMVPARAMSLOADED, false);
    }

    public void setTerminalParamsLoadedFlag(boolean ifset) {
        mEditor.putBoolean(ConstantUtils.TERMPARAMSLOADED, ifset);
        mEditor.commit();
    }
    public boolean getTerminalParamsLoadedFlag() {
        return mPrefs.getBoolean(ConstantUtils.TERMPARAMSLOADED, false);
    }


    public void setLogin(boolean ifLogin) {
        mEditor.putBoolean(ConstantUtils.LOGIN, ifLogin);
        mEditor.commit();
    }
    public boolean getLogin() {
        return mPrefs.getBoolean(ConstantUtils.LOGIN, false);
    }


    public void setLastKeyDownloadDate(long date) {
        mEditor.putLong(ConstantUtils.KEYDOWNLOADDATE, date);
        mEditor.commit();
    }
    public long getLastKeyDownloadDate() {
        return mPrefs.getLong(ConstantUtils.KEYDOWNLOADDATE, ConstantUtils.DEFAULTKEYDOWNLOADDATE);
    }





    public void setStan(int stan) {
        mEditor.putInt(ConstantUtils.STAN, stan);
        mEditor.commit();
    }
    public int getStan() {
        return mPrefs.getInt(ConstantUtils.STAN, 0);
    }

    public void setRetrievalRef(long retrievalRef) {
        mEditor.putLong(ConstantUtils.RETRIEVAL_REF, retrievalRef);
        mEditor.commit();
    }
    public long getRetrievalRef() {
        return mPrefs.getLong(ConstantUtils.RETRIEVAL_REF, 0L);
    }

    public void setMerchantLoc(String merchantLoc) {
        mEditor.putString(ConstantUtils.MERCHANT_LOC, merchantLoc);
        mEditor.commit();
    }
    public String getMerchantLoc() {
        return mPrefs.getString(ConstantUtils.MERCHANT_LOC, "");
    }


    public void setTerminalId(String terminalId) {
        mEditor.putString(ConstantUtils.TERMINAL_ID, terminalId);
        mEditor.commit();
    }
    public String getTerminalId() {
        return mPrefs.getString(ConstantUtils.TERMINAL_ID, "");
    }

    public void setMerchantId(String merchantId) {
        mEditor.putString(ConstantUtils.MERCHANT_ID, merchantId);
        mEditor.commit();
    }
    public String getMerchantId() {
        return mPrefs.getString(ConstantUtils.MERCHANT_ID, "");
    }

    public void setMerchantName(String merchantName) {
        mEditor.putString(ConstantUtils.MERCHANT_NAME, merchantName);
        mEditor.commit();
    }
    public String getMerchantName() {
        return mPrefs.getString(ConstantUtils.MERCHANT_NAME, "");
    }



    public void setMerchantCategoryCode(String merchantCategoryCode) {
        mEditor.putString(ConstantUtils.MERCHANT_CATEGORY_CODE, merchantCategoryCode);
        mEditor.commit();
    }
    public String getMerchantCategoryCode() {
        return mPrefs.getString(ConstantUtils.MERCHANT_CATEGORY_CODE, "5399");
    }


    public void setPurchaseSurcharge(String purchaseSurcharge) {
        mEditor.putString(ConstantUtils.PURCHASE_SURCHARGE, purchaseSurcharge);
        mEditor.commit();
    }
    public String getPurchaseSurcharge() {
        return mPrefs.getString(ConstantUtils.PURCHASE_SURCHARGE, "0");
    }


    public void setAcquirerId(String acquirerId) {
        mEditor.putString(ConstantUtils.ACQUIRER_ID, acquirerId);
        mEditor.commit();
    }
    public String getAcquirerId() {
        return mPrefs.getString(ConstantUtils.ACQUIRER_ID, "000000");
    }

    public void setPTSP(String ptsp) {
        mEditor.putString(ConstantUtils.PTSP, ptsp);
        mEditor.commit();
    }
    public String getPTSP() {
        return mPrefs.getString(ConstantUtils.PTSP, "Arca Networks");
    }

    public void setCurrencyCode(String currencyCode) {
        mEditor.putString(ConstantUtils.CURRENCY_CODE, currencyCode);
        mEditor.commit();
    }
    public String getCurrencyCode() {
        return mPrefs.getString(ConstantUtils.CURRENCY_CODE, "566");
    }

    public void setCountryCode(String countryCode) {
        mEditor.putString(ConstantUtils.COUNTRY_CODE, countryCode);
        mEditor.commit();
    }
    public String getCountryCode() {
        return mPrefs.getString(ConstantUtils.COUNTRY_CODE, "566");
    }

    public void setPOSDataCode(String posDataCode) {
        mEditor.putString(ConstantUtils.POS_DATA_CODE, posDataCode);
        mEditor.commit();
    }
    public String getPOSDataCode() {
        return mPrefs.getString(ConstantUtils.POS_DATA_CODE, "511201213344002");
    }

    public void setCTMSHost(String ctmsHost) {
        mEditor.putString(ConstantUtils.CTMS_HOST, ctmsHost);
        mEditor.commit();
    }
    public String getCTMSHost() {
        return mPrefs.getString(ConstantUtils.CTMS_HOST, "ctms.nibss-plc.com");
    }

    public void setCTMSIP(String ctmsIp) {
        mEditor.putString(ConstantUtils.CTMS_IP, ctmsIp);
        mEditor.commit();
    }
    public String getCTMSIP() {
        return mPrefs.getString(ConstantUtils.CTMS_IP, "");
    }

    public void setCTMSPort(int ctmsPort) {
        mEditor.putInt(ConstantUtils.CTMS_PORT, ctmsPort);
        mEditor.commit();
    }
    public int getCTMSPort() {
        return mPrefs.getInt(ConstantUtils.CTMS_PORT, 0);
    }

    public void setCTMSTimeout(int ctmsTimeout) {
        mEditor.putInt(ConstantUtils.CTMS_TIMEOUT, ctmsTimeout);
        mEditor.commit();
    }
    public int getCTMSTimeout() {
        return mPrefs.getInt(ConstantUtils.CTMS_TIMEOUT, 60);
    }

    public void setIfCTMSSSL(boolean ifCTMSSSL) {
        mEditor.putBoolean(ConstantUtils.CTMS_SSL, ifCTMSSSL);
        mEditor.commit();
    }
    public boolean getIfCTMSSSL() {
        return mPrefs.getBoolean(ConstantUtils.CTMS_SSL, false);
    }



    public void setTMSHost(String tmsHost) {
        mEditor.putString(ConstantUtils.TMS_HOST, tmsHost);
        mEditor.commit();
    }
    public String getTMSHost() {
        return mPrefs.getString(ConstantUtils.TMS_HOST, "52.56.159.171");
    }

    public void setTMSPort(int tmsPort) {
        mEditor.putInt(ConstantUtils.TMS_PORT, tmsPort);
        mEditor.commit();
    }
    public int getTMSPort() {
        return mPrefs.getInt(ConstantUtils.TMS_PORT, 80);
    }

    public void setTMSTimeout(int tmsTimeout) {
        mEditor.putInt(ConstantUtils.TMS_TIMEOUT, tmsTimeout);
        mEditor.commit();
    }
    public int getTMSTimeout() {
        return mPrefs.getInt(ConstantUtils.TMS_TIMEOUT, 60);
    }

    public void setIfTMSSSL(boolean ifTMSSSL) {
        mEditor.putBoolean(ConstantUtils.CTMS_SSL, ifTMSSSL);
        mEditor.commit();
    }
    public boolean getIfTMSSSL() {
        return mPrefs.getBoolean(ConstantUtils.TMS_SSL, false);
    }



    public void setICCData(String iccData) {
        mEditor.putString(ConstantUtils.ICC_DATA, iccData);
        mEditor.commit();
    }
    public String getICCData() {
        return mPrefs.getString(ConstantUtils.ICC_DATA, "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F34,9F33,9F35,9F03");
    }

    public void setKeyDownloadPeriodInMin(int timeInMin) {
        mEditor.putInt(ConstantUtils.KEY_DOWNLOAD_TIME_IN_MINS, timeInMin);
        mEditor.commit();
    }
    public int getKeyDownloadPeriodInMin() {
        return mPrefs.getInt(ConstantUtils.KEY_DOWNLOAD_TIME_IN_MINS, 1440);
    }


    public void setCheckKeyDownloadIntervalInMin(int timeInMin) {
        mEditor.putInt(ConstantUtils.CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS, timeInMin);
        mEditor.commit();
    }
    public int getCheckKeyDownloadIntervalInMin() {
        return mPrefs.getInt(ConstantUtils.CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS, 120);
    }

    public void setCallHomePeriodInMin(int timeInMin) {
        mEditor.putInt(ConstantUtils.CALL_HOME_TIME_IN_MINS, timeInMin);
        mEditor.commit();
    }
    public int getCallHomePeriodInMin() {
        return mPrefs.getInt(ConstantUtils.CALL_HOME_TIME_IN_MINS, 60);
    }

    public void setResendReversalPeriodInMin(int resendReversal) {
        mEditor.putInt(ConstantUtils.RESEND_REVERSAL_TIME_IN_MINS, resendReversal);
        mEditor.commit();
    }
    public int getResendReversalPeriodInMin() {
        return mPrefs.getInt(ConstantUtils.RESEND_REVERSAL_TIME_IN_MINS, 60);
    }


    public void setPageTimerInSec(int pageTimerInSec) {
        mEditor.putInt(ConstantUtils.PAGE_TIMER_IN_SEC, pageTimerInSec);
        mEditor.commit();
    }
    public int getPageTimerInSec() {
        return mPrefs.getInt(ConstantUtils.PAGE_TIMER_IN_SEC, 60);
    }



    public void setCtmk(String ctmk) {
        mEditor.putString(ConstantUtils.CTMK, ctmk);
        mEditor.commit();
    }
    public String getCtmk() {
        return mPrefs.getString(ConstantUtils.CTMK, "");
    }

    public void setTmk(String tmk) {
        mEditor.putString(ConstantUtils.TMK, tmk);
        mEditor.commit();
    }
    public String getTmk() {
        return mPrefs.getString(ConstantUtils.TMK, "");
    }

    public void setTsk(String tsk) {
        mEditor.putString(ConstantUtils.TSK, tsk);
        mEditor.commit();
    }
    public String getTsk() {
        return mPrefs.getString(ConstantUtils.TSK, "");
    }

    public void setSupervisorPIN(String supervisorPIN) {
        mEditor.putString(ConstantUtils.SUPERVISOR_PIN, supervisorPIN);
        mEditor.commit();
    }
    public String getSupervisorPIN() {
        return mPrefs.getString(ConstantUtils.SUPERVISOR_PIN, "1234");
    }

    public void setAdminPassword(String adminPassword) {
        mEditor.putString(ConstantUtils.ADMIN_PWD, adminPassword);
        mEditor.commit();
    }
    public String getAdminPassword() {
        return mPrefs.getString(ConstantUtils.ADMIN_PWD, "Password1$");
    }



    public void setIfFirstLaunch(boolean ifFirstLaunch) {
        mEditor.putBoolean(ConstantUtils.FIRST_LAUNCH, ifFirstLaunch);
        mEditor.commit();
    }
    public boolean getIfFirstLaunch() {
        return mPrefs.getBoolean(ConstantUtils.FIRST_LAUNCH, true);
    }

    /*
    public void setIfUseRemoteNetworkConfig(boolean ifUseRemoteNetworkConfig) {
        mEditor.putBoolean(ConstantUtils.USE_REMOTE_NETWORK_CONFIG, ifUseRemoteNetworkConfig);
        mEditor.commit();
    }
    public boolean getIfUseRemoteNetworkConfig() {
        return mPrefs.getBoolean(ConstantUtils.USE_REMOTE_NETWORK_CONFIG, false);
    }
    */


}
