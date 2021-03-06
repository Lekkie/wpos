package com.avantir.wpos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by lekanomotayo on 09/02/2018.
 */

@DatabaseTable(tableName="tbl_reversals_info")
public class ReversalInfo implements Serializable {


    @DatabaseField(generatedId = true, columnName = "id",canBeNull = false)
    private long id;
    @DatabaseField(unique=false, canBeNull = false)
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
    @DatabaseField(unique=false, canBeNull = false)
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
    private String merchantLoc;
    @DatabaseField(unique=false, canBeNull = false)
    private String currencyCode;
    @DatabaseField(unique=false, canBeNull = true)
    private String pinData;
    @DatabaseField(unique=false, canBeNull = true)
    private String msgReasonCode;
    @DatabaseField(unique=false, canBeNull = false)
    private String posDataCode;
    @DatabaseField(unique=false, canBeNull = true, columnName = "response_code")
    private String responseCode;
    @DatabaseField(unique=false, canBeNull = false, defaultValue = "0", columnName = "retry_no")
    private int retryNo = 0;
    @DatabaseField(unique=false, canBeNull = false, defaultValue = "0")
    private int completed = 0;
    @DatabaseField(unique=false, canBeNull = false, columnName = "created_on")
    private long createdOn = 0;
    @DatabaseField(unique=false, canBeNull = false)
    private int notified = 0;
    @DatabaseField(unique=false, canBeNull = true)
    private long latency = 0;
    private long startTime = 0;
    private long endTime = 0;

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

    public String getPosDataCode() {
        return posDataCode;
    }

    public void setPosDataCode(String posDataCode) {
        this.posDataCode = posDataCode;
    }

    public String getMsgReasonCode() {
        return msgReasonCode;
    }

    public void setMsgReasonCode(String msgReasonCode) {
        this.msgReasonCode = msgReasonCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getRetryNo() {
        return retryNo;
    }

    public void setRetryNo(int retryNo) {
        this.retryNo = retryNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
