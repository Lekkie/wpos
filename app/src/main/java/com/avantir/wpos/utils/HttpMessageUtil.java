package com.avantir.wpos.utils;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.listeners.CommsListener;
import com.avantir.wpos.model.Message;
import com.avantir.wpos.model.RestResponse;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.GPSTracker;
import com.avantir.wpos.services.HttpComms;
import com.google.gson.Gson;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import com.solab.iso8583.util.HexCodec;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;


/**
 * Created by lekanomotayo on 10/02/2018.
 */
public class HttpMessageUtil {

    private static HttpMessageUtil instance;

    private HttpMessageUtil(){

    }

    public static HttpMessageUtil getInstance(){
        if(instance == null){
            instance = new HttpMessageUtil();
        }
        return instance;
    }


    public String createTerminalParamDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();

        return null;
    }

    public Message copyTransInfoToMessage(TransInfo transInfo){
        Message message = new Message();
        message.setF0(transInfo.getMsgType());
        message.setF2(transInfo.getCardNo());
        message.setF3(transInfo.getProcCode());
        message.setF4(transInfo.getAmt());
        message.setF7(transInfo.getTransmissionDateTime());
        message.setF11(transInfo.getStan());
        message.setF12(transInfo.getLocalTime());
        message.setF13(transInfo.getLocalDate());
        message.setF14(transInfo.getExpDate());
        message.setF18(transInfo.getMerchType());
        message.setF22(transInfo.getPosEntryMode());
        message.setF23(transInfo.getCardSequenceNo());
        message.setF25(transInfo.getPosConditionCode());
        message.setF26(transInfo.getPosPinCaptureCode());
        //message.setF28(transInfo.getse); // settlement fee
        message.setF32(transInfo.getAcqInstId());
        message.setF33(transInfo.getFwdInstId());
        message.setF35(transInfo.getTrack2());
        message.setF37(transInfo.getRetRefNo());
        message.setF38(transInfo.getAuthNum());
        message.setF39(transInfo.getResponseCode());
        message.setF40(transInfo.getServiceRestrictionCode());
        message.setF41(transInfo.getTerminalId());
        message.setF42(transInfo.getMerchantId());
        message.setF43(transInfo.getMerchantLoc());
        message.setF49(transInfo.getCurrencyCode());
        message.setF56(transInfo.getMsgReasonCode());
        //message.setF59(transInfo.get);  //Echo data
        //message.setF102(transInfo.getMsgReasonCode()); // from acct
        //message.setF103(transInfo.getMsgReasonCode()); / /to acct
        message.setF123(transInfo.getPosDataCode());
        message.setLatency(transInfo.getLatency());

        return message;
    }


    public Message copyTransInfoToMessage(ReversalInfo reversalInfo){
        Message message = new Message();
        message.setF0(reversalInfo.getMsgType());
        message.setF2(reversalInfo.getCardNo());
        message.setF3(reversalInfo.getProcCode());
        message.setF4(reversalInfo.getAmt());
        message.setF7(reversalInfo.getTransmissionDateTime());
        message.setF11(reversalInfo.getStan());
        message.setF12(reversalInfo.getLocalTime());
        message.setF13(reversalInfo.getLocalDate());
        message.setF14(reversalInfo.getExpDate());
        message.setF18(reversalInfo.getMerchType());
        message.setF22(reversalInfo.getPosEntryMode());
        message.setF23(reversalInfo.getCardSequenceNo());
        message.setF25(reversalInfo.getPosConditionCode());
        message.setF26(reversalInfo.getPosPinCaptureCode());
        //message.setF28(reversalInfo.getse); // settlement fee
        message.setF32(reversalInfo.getAcqInstId());
        message.setF33(reversalInfo.getFwdInstId());
        message.setF35(reversalInfo.getTrack2());
        message.setF37(reversalInfo.getRetRefNo());
        message.setF39(reversalInfo.getResponseCode());
        message.setF40(reversalInfo.getServiceRestrictionCode());
        message.setF41(reversalInfo.getTerminalId());
        message.setF42(reversalInfo.getMerchantId());
        message.setF43(reversalInfo.getMerchantLoc());
        message.setF49(reversalInfo.getCurrencyCode());
        message.setF56(reversalInfo.getMsgReasonCode());
        //message.setF59(reversalInfo.get);  //Echo data
        //message.setF102(reversalInfo.getMsgReasonCode()); // from acct
        //message.setF103(reversalInfo.getMsgReasonCode()); / /to acct
        message.setF123(reversalInfo.getPosDataCode());
        message.setLatency(reversalInfo.getLatency());

        return message;
    }


    public void sendNotificationAsync(GlobalData globalData, Handler handler, TransInfo transInfo){
        Message message = copyTransInfoToMessage(transInfo);
        Gson gson = new Gson();
        String request = gson.toJson(message);
        HttpComms httpComms = HttpComms.getInstance(globalData.getTMSHost(), globalData.getTMSPort(), globalData.getTMSTimeout(), globalData.getIfTMSSSL(), null);
        HashMap headers = new HashMap();
        headers.put(ConstantUtils.DEVICE_SERIAL_NO, Build.SERIAL);
        headers.put(ConstantUtils.DEVICE_LOCATION, getLastBestLocation());
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.TRAN_NOTIFICATION_REQ_TYPE);
        httpComms.dataCommu(WPOSApplication.app, ConstantUtils.NOTIFY_TRANSACTION_URI, 1, headers, request, commsListener);
    }


    public void sendNotificationReversalAsync(GlobalData globalData, Handler handler, ReversalInfo reversalInfo){
        Message message = copyTransInfoToMessage(reversalInfo);
        Gson gson = new Gson();
        String request = gson.toJson(message);
        HttpComms httpComms = HttpComms.getInstance(globalData.getTMSHost(), globalData.getTMSPort(), globalData.getTMSTimeout(), globalData.getIfTMSSSL(), null);
        HashMap headers = new HashMap();
        headers.put(ConstantUtils.DEVICE_SERIAL_NO, Build.SERIAL);
        headers.put(ConstantUtils.DEVICE_LOCATION, getLastBestLocation());
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.TRAN_NOTIFICATION_REVERSAL_REQ_TYPE);
        httpComms.dataCommu(WPOSApplication.app, ConstantUtils.NOTIFY_TRANSACTION_URI, 1, headers, request, commsListener);
    }

    public byte[] sendNotificationSync(GlobalData globalData, TransInfo transInfo) throws Exception{
        Message message = copyTransInfoToMessage(transInfo);
        Gson gson = new Gson();
        String request = gson.toJson(message);
        HttpComms httpComms = HttpComms.getInstance(globalData.getTMSHost(), globalData.getTMSPort(), globalData.getTMSTimeout(), globalData.getIfTMSSSL(), null);
        HashMap headers = new HashMap();
        headers.put(ConstantUtils.DEVICE_SERIAL_NO, Build.SERIAL);
        headers.put(ConstantUtils.DEVICE_LOCATION, getLastBestLocation());
        return httpComms.dataCommuBlocking(WPOSApplication.app, ConstantUtils.NOTIFY_TRANSACTION_URI, 1, headers, request);
    }

    public byte[] sendNotificationReversalSync(GlobalData globalData, ReversalInfo reversalInfo) throws Exception{
        Message message = copyTransInfoToMessage(reversalInfo);
        Gson gson = new Gson();
        String request = gson.toJson(message);
        HttpComms httpComms = HttpComms.getInstance(globalData.getTMSHost(), globalData.getTMSPort(), globalData.getTMSTimeout(), globalData.getIfTMSSSL(), null);
        HashMap headers = new HashMap();
        headers.put(ConstantUtils.DEVICE_SERIAL_NO, Build.SERIAL);
        headers.put(ConstantUtils.DEVICE_LOCATION, getLastBestLocation());
        return httpComms.dataCommuBlocking(WPOSApplication.app, ConstantUtils.NOTIFY_TRANSACTION_URI, 1, headers, request);
    }

    public void receiveNotificationResponse(byte[] receiveData, String refNo, TransInfoDao transInfoDao){
        String response = new String(receiveData);
        Gson gson = new Gson();
        RestResponse restResponse = gson.fromJson(response, RestResponse.class);
        String code = restResponse.getCode();
        if("00".equalsIgnoreCase(code))
            transInfoDao.updateNotificationByRetRefNo(refNo, 1);
    }

    public void receiveNotificationReversalResponse(byte[] receiveData, String refNo, ReversalInfoDao reversalInfoDao){
        String response = new String(receiveData);
        Gson gson = new Gson();
        RestResponse restResponse = gson.fromJson(response, RestResponse.class);
        String code = restResponse.getCode();
        if("00".equalsIgnoreCase(code))
            reversalInfoDao.updateNotificationByRetRefNo(refNo, 1);
    }


    private String getLastBestLocation() {
        GPSTracker gpsTracker = GPSTracker.getInstance(WPOSApplication.app);
        gpsTracker.getLocation();
        double latitude = gpsTracker.getLatitude(); // latitude
        double longitude = gpsTracker.getLongitude();
        String loc = latitude + "," + longitude;
        return loc;
    }

}
