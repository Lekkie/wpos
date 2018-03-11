package com.avantir.wpos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by lekanomotayo on 09/02/2018.
 */

@DatabaseTable(tableName="tbl_trans_info")
public class TransInfo implements Serializable {

    //private static TransInfo transInfo = null;

    @DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
    private long id;
    @DatabaseField(unique=false, canBeNull = false,  columnName = "msg_type")
    private String msgType;
    @DatabaseField(unique=false, canBeNull = false)
    private String cardNo;
    @DatabaseField(unique=false, canBeNull = false)
    private String procCode;
    @DatabaseField(unique=false, canBeNull = false)
    private String amt;
    @DatabaseField(unique=false, canBeNull = false)
    private String transmissionDateTime;
    @DatabaseField(unique=false, canBeNull = false)
    private String stan;
    @DatabaseField(unique=false, canBeNull = false)
    private String localTime;
    @DatabaseField(unique=false, canBeNull = false)
    private String localDate;
    @DatabaseField(unique=false, canBeNull = false)
    private String expDate;
    @DatabaseField(unique=false, canBeNull = false)
    private String merchType;
    @DatabaseField(unique=false, canBeNull = false)
    private String posEntryMode;
    @DatabaseField(unique=false, canBeNull = true)
    private String cardSequenceNo;
    @DatabaseField(unique=false, canBeNull = false)
    private String posConditionCode;
    @DatabaseField(unique=false, canBeNull = false)
    private String posPinCaptureCode;
    @DatabaseField(unique=false, canBeNull = true)
    private String surcharge;
    @DatabaseField(unique=false, canBeNull = false)
    private String acqInstId;
    @DatabaseField(unique=false, canBeNull = true)
    private String fwdInstId;
    @DatabaseField(unique=true, canBeNull = false, index = true, columnName = "ret_ref_no")
    private String retRefNo;
    @DatabaseField(unique=false, canBeNull = true)
    private String track2;
    @DatabaseField(unique=false, canBeNull = true)
    private String serviceRestrictionCode;
    @DatabaseField(unique=false, canBeNull = false)
    private String terminalId;
    @DatabaseField(unique=false, canBeNull = false)
    private String merchantId;
    @DatabaseField(unique=false, canBeNull = false)
    private String merchantName;
    @DatabaseField(unique=false, canBeNull = false)
    private String merchantLoc;
    @DatabaseField(unique=false, canBeNull = false)
    private String currencyCode;
    @DatabaseField(unique=false, canBeNull = true)
    private String pinData;
    private String iccData;
    private String msgReasonCode;
    @DatabaseField(unique=false, canBeNull = false)
    private String posDataCode;
    @DatabaseField(unique=false, canBeNull = true, columnName = "response_code")
    private String responseCode;
    @DatabaseField(unique=false, canBeNull = true, columnName = "auth_num")
    private String authNum;
    @DatabaseField(unique=false, canBeNull = false)
    private int reversed = 0;
    @DatabaseField(unique=false, canBeNull = false)
    private int completed = 0;
    @DatabaseField(unique=false, canBeNull = false, columnName = "created_on")
    private long createdOn = 0;
    @DatabaseField(unique=false, canBeNull = false, columnName = "masked_pan")
    private String maskedPan;
    @DatabaseField(unique=false, canBeNull = false, columnName = "card_holder_name")
    private String cardHolderName;
    @DatabaseField(unique=false, canBeNull = true, columnName = "card_type_name")
    private String cardTypeName;
    @DatabaseField(unique=false, canBeNull = false, columnName = "account_type")
    private String accountType;
    @DatabaseField(unique=false, canBeNull = false, columnName = "authentication_method")
    private String authenticationMethod;



    private String issuerCountry;
    private String track3;//磁道3数据


    private int cardType;//卡类型
    private String posInputType;//卡输入方式.
    int tradeType;

    private String exCardInfo;
    private boolean isOnLine;// 是否线上交易
    private int tranType;//交易类型
    private String deviceSerialNo;

    // IC 卡交易结果
    private int ICResult;

    byte[] aid;
    int aidLen;
    private byte[] appCrypt = new byte[8];//应用密文
    private byte[]    TVR = new byte[5];                                     //TAG95   终端验证结果TVR 5
    private byte[]    TSI = new byte[2];                                     //TAG9B   TSI  2
    private byte[]    ATC = new byte[2];                                     //TAG9F36 应用交易序号   ATC 2
    private byte[]    CVM = new byte[3];                                     //TAG9F34 持卡人验证方法(CVM)结果 CVM 3
    private byte[]    aucAppLabel =new byte[16];                             //TAG50   应用标签  16
    private int     aucAppLabelLen ;                                         //应用标签长度
    private byte[]    aucAppPreferName = new byte[16];                       //TAG9F12 应用首选名 16
    private int     aucAppPreferNameLen;                                     //首选名称长度
    private byte[]    aucUnPredNum = new byte[4];                            //TAG9F37 不可预知数字  4
    private byte[]    aucAIP = new byte[2];                                  //TAG82   应用交互特征   AIP 2
    private byte[]    aucCVR = new byte[7];                                  //TAG9F10 发卡行应用数据 IAD 7



    /*
    public static TransInfo getInstance(){
        if (transInfo == null) {
            transInfo = new TransInfo();
        }
        return transInfo;
    }
    */

    /*
    public void init(){
        this.msgType = null;
        this.isOnLine = false;
        this.tranType = 0;
        this.stan = null;
        this.cardType = -1;
        this.pinData = null;
        this.cardNo = null;
        this.maskedPan = null;
        this.cardHolderName = null;
        this.cardSequenceNo = null;
        this.track2 = null;
        this.track3 = null;
        this.iccData = null;
        this.posInputType = null;
        this.expDate = null;
        this.exCardInfo = null;
        this.amt = null;
        this.terminalId = null;
        this.merchantId = null;
        this.posConditionCode = null;
        this.serviceRestrictionCode = null;
        this.retRefNo = null;
        this.ICResult = 0;
        this.aid = null;
        this.aidLen = 0;
        this.appCrypt = new byte[8];
        this.TVR = new byte[5];
        this.TSI = new byte[2];
        this.ATC = new byte[2];
        this.CVM = new byte[3];
        this.aucAppLabel =new byte[16];
        this.aucAppLabelLen = 0;
        this.aucAppPreferName = new byte[16];
        this.aucAppPreferNameLen = 0;
        this.aucUnPredNum = new byte[4];
        this.aucAIP = new byte[2];
        this.aucCVR = new byte[7];
        this.accountType = null;
        this.cardTypeName = null;
        this.authNum = null;
        this.status = null;
        this.deviceSerialNo = null;
        this.completed = false;
        this.reversed = false;
    }
    */


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getTransmissionDateTime() {
        return transmissionDateTime;
    }

    public void setTransmissionDateTime(String transmissionDateTime) {
        this.transmissionDateTime = transmissionDateTime;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getMerchType() {
        return merchType;
    }

    public void setMerchType(String merchType) {
        this.merchType = merchType;
    }

    public String getPosEntryMode() {
        return posEntryMode;
    }

    public void setPosEntryMode(String posEntryMode) {
        this.posEntryMode = posEntryMode;
    }

    public String getCardSequenceNo() {
        return cardSequenceNo;
    }

    public void setCardSequenceNo(String cardSequenceNo) {
        this.cardSequenceNo = cardSequenceNo;
    }

    public String getPosConditionCode() {
        return posConditionCode;
    }

    public void setPosConditionCode(String posConditionCode) {
        this.posConditionCode = posConditionCode;
    }

    public String getPosPinCaptureCode() {
        return posPinCaptureCode;
    }

    public void setPosPinCaptureCode(String posPinCaptureCode) {
        this.posPinCaptureCode = posPinCaptureCode;
    }

    public String getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    public String getAcqInstId() {
        return acqInstId;
    }

    public void setAcqInstId(String acqInstId) {
        this.acqInstId = acqInstId;
    }

    public String getFwdInstId() {
        return fwdInstId;
    }

    public void setFwdInstId(String fwdInstId) {
        this.fwdInstId = fwdInstId;
    }

    public String getRetRefNo() {
        return retRefNo;
    }

    public void setRetRefNo(String retRefNo) {
        this.retRefNo = retRefNo;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getServiceRestrictionCode() {
        return serviceRestrictionCode;
    }

    public void setServiceRestrictionCode(String serviceRestrictionCode) {
        this.serviceRestrictionCode = serviceRestrictionCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantLoc() {
        return merchantLoc;
    }

    public void setMerchantLoc(String merchantLoc) {
        this.merchantLoc = merchantLoc;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPinData() {
        return pinData;
    }

    public void setPinData(String pinData) {
        this.pinData = pinData;
    }

    public String getIccData() {
        return iccData;
    }

    public void setIccData(String iccData) {
        this.iccData = iccData;
    }

    public String getMsgReasonCode() {
        return msgReasonCode;
    }

    public void setMsgReasonCode(String msgReasonCode) {
        this.msgReasonCode = msgReasonCode;
    }

    public String getPosDataCode() {
        return posDataCode;
    }

    public void setPosDataCode(String posDataCode) {
        this.posDataCode = posDataCode;
    }

    public boolean isOnLine() {
        return isOnLine;
    }
    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public byte[] getAid() {
        return aid;
    }

    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    public int getAidLen() {
        return aidLen;
    }

    public void setAidLen(int aidLen) {
        this.aidLen = aidLen;
    }

    public byte[] getTVR() {
        return TVR;
    }

    public byte[] getTSI() {
        return TSI;
    }

    public byte[] getATC() {
        return ATC;
    }

    public byte[] getCVM() {
        return CVM;
    }

    public int getICResult() {
        return ICResult;
    }

    public void setICResult(int ICResult) {
        this.ICResult = ICResult;
    }

    public byte[] getAucAppLabel() {
        return aucAppLabel;
    }

    public void setAucAppLabel(byte[] aucAppLabel) {
        this.aucAppLabel = aucAppLabel;
    }

    public int getAucAppLabelLen() {
        return aucAppLabelLen;
    }

    public void setAucAppLabelLen(int aucAppLabelLen) {
        this.aucAppLabelLen = aucAppLabelLen;
    }

    public byte[] getAucAppPreferName() {
        return aucAppPreferName;
    }

    public void setAucAppPreferName(byte[] aucAppPreferName) {
        this.aucAppPreferName = aucAppPreferName;
    }

    public int getAucAppPreferNameLen() {
        return aucAppPreferNameLen;
    }

    public void setAucAppPreferNameLen(int aucAppPreferNameLen) {
        this.aucAppPreferNameLen = aucAppPreferNameLen;
    }

    public byte[] getAucUnPredNum() {
        return aucUnPredNum;
    }

    public void setAucUnPredNum(byte[] aucUnPredNum) {
        this.aucUnPredNum = aucUnPredNum;
    }

    public byte[] getAucAIP() {
        return aucAIP;
    }

    public void setAucAIP(byte[] aucAIP) {
        this.aucAIP = aucAIP;
    }

    public byte[] getAucCVR() {
        return aucCVR;
    }

    public void setAucCVR(byte[] aucCVR) {
        this.aucCVR = aucCVR;
    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }

    public int getTranType() {
        return tranType;
    }

    public void setTranType(int tranType) {
        this.tranType = tranType;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public String getPosInputType() {
        return posInputType;
    }

    public void setPosInputType(String posInputType) {
        this.posInputType = posInputType;
    }

    public byte[] getAppCrypt() {
        return appCrypt;
    }

    public void setAppCrypt(byte[] appCrypt) {
        this.appCrypt = appCrypt;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getReversed() {
        return reversed;
    }

    public void setReversed(int reversed) {
        this.reversed = reversed;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }
}
