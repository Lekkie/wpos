package com.avantir.wpos.utils;

import android.os.RemoteException;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import com.solab.iso8583.util.HexCodec;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lekanomotayo on 10/02/2018.
 */
public class IsoMessageUtil {

    private static String clearTsk;
    static MessageFactory<IsoMessage> messageFactory;
    private static IsoMessageUtil instance;
    private static boolean messageFactoryInit = false;
    private static boolean sessionKeyInit = false;
    public static Map<String, String> responseMessageMap = new HashMap<>();
    private static String COUNTRY_CODE = "NG";
    private static String DEFAULT_ACCOUNT = "DEFAULT";
    private static String SAVINGS_ACCOUNT = "SAVINGS";
    private static String CURRENT_ACCOUNT = "CURRENT";
    private static String CREDIT_ACCOUNT = "CREDIT";

    private static String PURCHASE = "Purchase";
    private static String BALANCE = "Balance";
    private static String REFUND = "Refund";
    private static String TRANSACTION = "Transaction";


    static {
        responseMessageMap.put("00", "Transaction Approved");
        responseMessageMap.put("01", "Refer to card issuer");
        responseMessageMap.put("02", "Refer to card issuer, special condition");
        responseMessageMap.put("03", "Invalid merchant");
        responseMessageMap.put("04", "Pick-up card");
        responseMessageMap.put("05", "Do not honor");
        responseMessageMap.put("06", "Error");
        responseMessageMap.put("07", "Pick-up card, special condition");
        responseMessageMap.put("08", "Honor with identification");
        responseMessageMap.put("09", "Request in progress");
        responseMessageMap.put("10", "Approved, partial");
        responseMessageMap.put("11", "Approved, VIP");
        responseMessageMap.put("12", "Invalid transaction");
        responseMessageMap.put("13", "Invalid amount");
        responseMessageMap.put("14", "Invalid card number");
        responseMessageMap.put("15", "No such issuer");
        responseMessageMap.put("16", "Approved, update track 3");
        responseMessageMap.put("17", "Customer cancellation");
        responseMessageMap.put("18", "Customer dispute");
        responseMessageMap.put("19", "Re-enter transaction");
        responseMessageMap.put("20", "Invalid response");
        responseMessageMap.put("21", "No action taken");
        responseMessageMap.put("22", "Suspected malfunction");
        responseMessageMap.put("23", "Unacceptable transaction fee");
        responseMessageMap.put("24", "File update not supported");
        responseMessageMap.put("25", "Unable to locate record");
        responseMessageMap.put("26", "Duplicate record");
        responseMessageMap.put("27", "File update field edit error");
        responseMessageMap.put("28", "File update file locked");
        responseMessageMap.put("29", "File update failed");
        responseMessageMap.put("30", "Format error");
        responseMessageMap.put("31", "Bank not supported");
        responseMessageMap.put("32", "Completed partially");
        responseMessageMap.put("33", "Expired card, pick-up");
        responseMessageMap.put("34", "Suspected fraud, pick-up");
        responseMessageMap.put("35", "Contact acquirer, pick-up");
        responseMessageMap.put("36", "Restricted card, pick-up");
        responseMessageMap.put("37", "Call acquirer security, pick-up");
        responseMessageMap.put("38", "PIN tries exceeded, pick-up");
        responseMessageMap.put("39", "No credit account");
        responseMessageMap.put("40", "Function not supported");
        responseMessageMap.put("41", "Lost card, pick-up");
        responseMessageMap.put("42", "No universal account");
        responseMessageMap.put("43", "Stolen card, pick-up");
        responseMessageMap.put("44", "No investment account");
        responseMessageMap.put("45", "Account closed");
        responseMessageMap.put("46", "Identification required");
        responseMessageMap.put("47", "Identification cross-check required");
        responseMessageMap.put("51", "Not sufficient funds");
        responseMessageMap.put("52", "No check account");
        responseMessageMap.put("53", "No savings account");
        responseMessageMap.put("54", "Expired card");
        responseMessageMap.put("55", "Incorrect PIN");
        responseMessageMap.put("56", "No card record");
        responseMessageMap.put("57", "Transaction not permitted to cardholder");
        responseMessageMap.put("58", "Transaction not permitted on terminal");
        responseMessageMap.put("59", "Suspected fraud");
        responseMessageMap.put("60", "Contact acquirer");
        responseMessageMap.put("61", "Exceeds withdrawal limit");
        responseMessageMap.put("62", "Restricted card");
        responseMessageMap.put("63", "Security violation");
        responseMessageMap.put("64", "Original amount incorrect");
        responseMessageMap.put("65", "Exceeds withdrawal frequency");
        responseMessageMap.put("66", "Call acquirer security");
        responseMessageMap.put("67", "Hard capture");
        responseMessageMap.put("68", "Response received too late");
        responseMessageMap.put("69", "Advice received too late");
        responseMessageMap.put("90", "Cut-off in progress");
        responseMessageMap.put("91", "Issuer or switch inoperative");
        responseMessageMap.put("92", "Routing error");
        responseMessageMap.put("93", "Violation of law");
        responseMessageMap.put("94", "Duplicate transaction");
        responseMessageMap.put("95", "Reconcile error");
        responseMessageMap.put("96", "System malfunction");
        responseMessageMap.put("98", "Exceeds cash limit");
        responseMessageMap.put("A1", "ATC not incremented");
        responseMessageMap.put("A2", "ATC limit exceeded");
        responseMessageMap.put("A3", "ATC configuration error");
        responseMessageMap.put("A4", "CVR check failure");
        responseMessageMap.put("A5", "CVR configuration error");
        responseMessageMap.put("A6", "TVR check failure");
        responseMessageMap.put("A7", "TVR configuration error");
        responseMessageMap.put("C0", "Unacceptable PIN");
        responseMessageMap.put("C1", "PIN Change failed");
        responseMessageMap.put("C2", "PIN Unblock failed");
        responseMessageMap.put("D1", "MAC Error");
        responseMessageMap.put("E1", "Prepay error");

    }

    private IsoMessageUtil(){

    }

    public static IsoMessageUtil getInstance(){
        if(instance == null){
            instance = new IsoMessageUtil();
        }
        if(!(messageFactoryInit && sessionKeyInit)){
            instance.init();
        }
        return instance;
    }

    public void init() {

        try{
            if(!messageFactoryInit){
                String xml = "NIBSS_PACKAGER.xml";
                String[] filesArray = AssetUtils.getFilesArrayFromAssets(WPOSApplication.app, "PACKAGER");
                for(String file: filesArray){
                    if(file.contains(xml)){
                        byte[] data = AssetUtils.getFromAssets(WPOSApplication.app, file);
                        String string = new String(data);
                        StringReader stringReader = new StringReader(string);
                        messageFactory = ConfigParser.createFromReader(stringReader);
                        messageFactory.setUseBinaryBitmap(false); //NIBSS usebinarybitmap = false
                        messageFactory.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        messageFactoryInit = true;
                    }
                }
            }

            if(!sessionKeyInit){
                String base64Ctmk = GlobalData.getInstance().getCtmk();
                String tmk = GlobalData.getInstance().getTmk();
                String tsk = GlobalData.getInstance().getTsk();
                if(!StringUtil.isEmpty(base64Ctmk) && !StringUtil.isEmpty(tmk) && !StringUtil.isEmpty(tsk)){
                    String clearCtmk = KeyUtils.decryptBase64StringWithRSA(base64Ctmk);
                    String clearTmk = KeyUtils.decryptWithDES(clearCtmk, tmk);
                    clearTsk = KeyUtils.decryptWithDES(clearTmk, tsk);
                    sessionKeyInit = true;
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }



    public byte[] createTMKDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.TMK_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, globalData.getTerminalId(), templ.getField(41).getType(), templ.getField(41).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public byte[] createTPKDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.TPK_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength()); // LLVAR
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, globalData.getTerminalId(), templ.getField(41).getType(), templ.getField(41).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public byte[] createIPEKTrack2DownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.IPEK_TRACK2_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength()); // LLVAR
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public static byte[] createIPEKEMVDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.IPEK_EMV_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength()); // LLVAR
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public byte[] createTSKDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.TSK_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, globalData.getTerminalId(), templ.getField(41).getType(), templ.getField(41).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public byte[] createTermParamDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();
        String tidLen = StringUtil.leftPad(String.valueOf(tid.length()), 3, '0');
        String mgtData1 = "01" + tidLen + tid; // 0100820390059

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.TERM_PARAM_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(62, mgtData1, templ.getField(62).getType(), mgtData1.length());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public byte[] createCallHomeRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();
        String tidLen = StringUtil.leftPad(String.valueOf(tid.length()), 3, '0');
        String appVersion = "001";
        String appVersionLen = StringUtil.leftPad(String.valueOf(appVersion.length()), 3, '0');
        String paymentChannelMode = "00000000000000000000";
        String paymentChannelModeLen = StringUtil.leftPad(String.valueOf(paymentChannelMode.length()), 3, '0');
        String callHomeComments = "N/A"; // Call – Home Merchant Information/Complaint/Comments
        String callHomeCommentsLen = StringUtil.leftPad(String.valueOf(callHomeComments.length()), 3, '0');
        String commsServiceProvider = "WIFI/MTN/AIRTEL/GLO"; // Communications Service Provider
        String commsServiceProviderLen = StringUtil.leftPad(String.valueOf(commsServiceProvider.length()), 3, '0');
        String mgtData1 = "01" + tidLen + tid
                + "09" + appVersionLen + appVersion
                + "10" + paymentChannelModeLen + paymentChannelMode
                + "11" + callHomeCommentsLen + callHomeComments
                + "12" + commsServiceProviderLen + commsServiceProvider;

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.CALL_HOME_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(62, mgtData1, templ.getField(62).getType(), mgtData1.length());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public byte[] createCAPKDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();
        String tidLen = StringUtil.leftPad(String.valueOf(tid.length()), 3, '0');
        String mgtData2 = "01" + tidLen + tid; // 0100820390059

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.CAPK_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(63, mgtData2, templ.getField(63).getType(), mgtData2.length());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public byte[] createAIDDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();
        String tidLen = StringUtil.leftPad(String.valueOf(tid.length()), 3, '0');
        String mgtData2 = "01" + tidLen + tid; // 0100820390059

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.AID_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength()); // LLVAR
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(63, mgtData2, templ.getField(63).getType(), mgtData2.length());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public byte[] createDailyTransactionReportDownloadRequest() throws Exception{
        GlobalData globalData = GlobalData.getInstance();
        Date now = new Date(System.currentTimeMillis());
        String transmissionDatetime = TimeUtil.getDateTimeMMddhhmmss(now);
        int stan = globalData.getStan() + 1;
        globalData.setStan(stan);
        String formattedStan = StringUtil.leftPadding('0', 6, String.valueOf(stan));
        String localTime = TimeUtil.getTimehhmmss(now);
        String localDate = TimeUtil.getDateMMdd(now);
        String tid  = globalData.getTerminalId();
        String tidLen = StringUtil.leftPad(String.valueOf(tid.length()), 3, '0');
        String mgtData2 = "01" + tidLen + tid; // 0100820390059

        int type = Integer.parseInt(ConstantUtils._0800, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(3, ConstantUtils.DAILY_REPORT_DOWNLOAD_PROC_CODE, templ.getField(3).getType(), templ.getField(3).getLength()); // LLVAR
        message.setValue(7, transmissionDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, formattedStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, localTime, templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, localDate, templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(41, tid, templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(63, mgtData2, templ.getField(63).getType(), mgtData2.length());
        message.setValue(64, new String(new byte[] { 0x0 }), templ.getField(64).getType(), templ.getField(64).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(64, hashValue, templ.getField(64).getType(), templ.getField(64).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public static byte[] createRequest(TransInfo transInfo) throws Exception{

        String pan = transInfo.getCardNo();
        String amt = StringUtil.leftPad(transInfo.getAmt(), 12, '0');
        String cardSeqNo = transInfo.getCardSequenceNo();
        String acqInstId = transInfo.getAcqInstId();
        String track2 = transInfo.getTrack2();
        String serviceRestrictionCode = transInfo.getServiceRestrictionCode();
        String pinData = transInfo.getPinData();
        String iccData  = transInfo.getIccData();

        int type = Integer.parseInt(transInfo.getMsgType(), 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(2, pan, templ.getField(2).getType(), pan.length());
        message.setValue(3, transInfo.getProcCode(), templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(4, amt, templ.getField(4).getType(), templ.getField(4).getLength());
        message.setValue(7, transInfo.getTransmissionDateTime(), templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, transInfo.getStan(), templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, transInfo.getLocalTime(), templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, transInfo.getLocalDate(), templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(14, transInfo.getExpDate(), templ.getField(14).getType(), templ.getField(14).getLength());
        message.setValue(18, transInfo.getMerchType(), templ.getField(18).getType(), templ.getField(18).getLength());
        message.setValue(22, transInfo.getPosEntryMode(), templ.getField(22).getType(), templ.getField(22).getLength());

        if(!StringUtil.isEmpty(cardSeqNo)){
            message.setValue(23, cardSeqNo, templ.getField(23).getType(), templ.getField(23).getLength());
        }

        message.setValue(25, transInfo.getPosConditionCode(), templ.getField(25).getType(), templ.getField(25).getLength());
        message.setValue(26, transInfo.getPosPinCaptureCode(), templ.getField(26).getType(), templ.getField(26).getLength());
        message.setValue(28, transInfo.getSurcharge(), templ.getField(28).getType(), templ.getField(28).getLength());
        message.setValue(32, acqInstId, templ.getField(32).getType(), acqInstId.length());
        //message.setValue(33, fwdInstId, templ.getField(33).getType(), fwdInstId.getLength());
        if (track2 != null) {
            message.setValue(35, track2, templ.getField(35).getType(), track2.length());
        }
        message.setValue(37, transInfo.getRetRefNo(), templ.getField(37).getType(), templ.getField(37).getLength());
        if (!StringUtil.isEmpty(serviceRestrictionCode) && serviceRestrictionCode.length() == 3) {
            message.setValue(40, serviceRestrictionCode, templ.getField(40).getType(), templ.getField(40).getLength());
        }
        message.setValue(41, transInfo.getTerminalId(), templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(42, transInfo.getMerchantId(), templ.getField(42).getType(), templ.getField(42).getLength());
        message.setValue(43, transInfo.getMerchantLoc(), templ.getField(43).getType(), templ.getField(43).getLength());
        message.setValue(49, transInfo.getCurrencyCode(), templ.getField(49).getType(), templ.getField(49).getLength());
        if (!StringUtil.isEmpty(pinData)) {
            byte[] pinBytes = HexCodec.hexDecode(pinData);
            message.setValue(52, pinBytes, templ.getField(52).getType(), templ.getField(52).getLength());
        }
        if (!StringUtil.isEmpty(iccData)) {
            message.setValue(55, iccData, templ.getField(55).getType(), iccData.length());
        }
        message.setValue(123, transInfo.getPosDataCode(), templ.getField(123).getType(), templ.getField(123).getLength());
        message.setValue(128, new String(new byte[] { 0x0 }), templ.getField(128).getType(), templ.getField(128).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(128, hashValue, templ.getField(128).getType(), templ.getField(128).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }


    public static byte[] createRequestReversal(ReversalInfo reversalInfo, boolean isRepeat) throws Exception{
        String pan = reversalInfo.getCardNo();
        String amt = reversalInfo.getAmt();
        String cardSeqNo = reversalInfo.getCardSequenceNo();
        String acqInstId = reversalInfo.getAcqInstId();
        String track2 = reversalInfo.getTrack2();
        String serviceRestrictionCode = reversalInfo.getServiceRestrictionCode();
        GlobalData globalData = GlobalData.getInstance();
        long retRef = (globalData.getRetrievalRef() + 1) % 999999999999L;
        if(retRef == 0)
            retRef = 1;
        globalData.setRetrievalRef(retRef);
        String retRefNo = StringUtil.leftPad(String.valueOf(retRef), 12, '0');
        //String msgReasonCode= reversalInfo.getMsgReasonCode();
        String origMsgType = reversalInfo.getMsgType();
        String origStan = reversalInfo.getStan();
        String origTransDatetime = reversalInfo.getTransmissionDateTime();
        String origAcqId = StringUtil.rightPad(acqInstId, 11, '0');
        String origForwId = reversalInfo.getFwdInstId() == null ? "" : reversalInfo.getFwdInstId();
        origForwId = StringUtil.rightPad(origForwId, 11, '0');
        String origDataElem = origMsgType + origStan + origTransDatetime + origAcqId + origForwId;

        int type = Integer.parseInt(ConstantUtils._0420, 16);
        if(isRepeat)
            type = Integer.parseInt(ConstantUtils._0421, 16);
        IsoMessage message = messageFactory.newMessage(type);
        IsoMessage templ = messageFactory.getMessageTemplate(type);
        message.setValue(2, pan, templ.getField(2).getType(), pan.length()); // LLVAR
        message.setValue(3, reversalInfo.getProcCode(), templ.getField(3).getType(), templ.getField(3).getLength());
        message.setValue(4, amt, templ.getField(4).getType(), templ.getField(4).getLength());
        message.setValue(7, origTransDatetime, templ.getField(7).getType(), templ.getField(7).getLength());
        message.setValue(11, origStan, templ.getField(11).getType(), templ.getField(11).getLength());
        message.setValue(12, reversalInfo.getLocalTime(), templ.getField(12).getType(), templ.getField(12).getLength());
        message.setValue(13, reversalInfo.getLocalDate(), templ.getField(13).getType(), templ.getField(13).getLength());
        message.setValue(14, reversalInfo.getExpDate(), templ.getField(14).getType(), templ.getField(14).getLength());
        message.setValue(18, reversalInfo.getMerchType(), templ.getField(18).getType(), templ.getField(18).getLength());
        message.setValue(22, reversalInfo.getPosEntryMode(), templ.getField(22).getType(), templ.getField(22).getLength());
        if(cardSeqNo != null)
            message.setValue(23, cardSeqNo, templ.getField(23).getType(), templ.getField(23).getLength());
        message.setValue(25, reversalInfo.getPosConditionCode(), templ.getField(25).getType(), templ.getField(25).getLength());
        message.setValue(26, reversalInfo.getPosPinCaptureCode(), templ.getField(26).getType(), templ.getField(26).getLength());
        message.setValue(28, reversalInfo.getSurcharge(), templ.getField(28).getType(), templ.getField(28).getLength());
        message.setValue(32, acqInstId, templ.getField(32).getType(), acqInstId.length());
        //message.setValue(33, fwdInstId, templ.getField(33).getType(), fwdInstId.length());
        if (track2 != null) {
            message.setValue(35, track2, templ.getField(35).getType(), track2.length());
        }
        message.setValue(37, retRefNo, templ.getField(37).getType(), templ.getField(37).getLength());
        if (!StringUtil.isEmpty(serviceRestrictionCode) && serviceRestrictionCode.length() == 3) {
            message.setValue(40, serviceRestrictionCode, templ.getField(40).getType(), templ.getField(40).getLength());
        }
        message.setValue(41, reversalInfo.getTerminalId(), templ.getField(41).getType(), templ.getField(41).getLength());
        message.setValue(42, reversalInfo.getMerchantId(), templ.getField(42).getType(), templ.getField(42).getLength());
        message.setValue(43, reversalInfo.getMerchantLoc(), templ.getField(43).getType(), templ.getField(43).getLength());
        message.setValue(49, reversalInfo.getCurrencyCode(), templ.getField(49).getType(), templ.getField(49).getLength());
        //message.setValue(56, msgReasonCode, templ.getField(56).getType(), msgReasonCode.length());
        message.setValue(90, origDataElem, templ.getField(90).getType(), templ.getField(90).getLength());
        message.setValue(123, reversalInfo.getPosDataCode(), templ.getField(123).getType(), templ.getField(123).getLength());
        message.setValue(128, new String(new byte[] { 0x0 }), templ.getField(128).getType(), templ.getField(128).getLength());
        byte[] bytes = message.writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }
        String hashValue = KeyUtils.getMac(clearTsk, temp); //SHA256
        message.setValue(128, hashValue, templ.getField(128).getType(), templ.getField(128).getLength());

        System.out.println(message.debugString());
        return message.writeData();
    }

    public static byte[] createPinChange(TransInfo transInfo) throws Exception{
        return null;
    }


    public static ReversalInfo createReversalInfo(TransInfo transInfo, String msgReasonCode){
        ReversalInfo reversalInfo = new ReversalInfo();
        reversalInfo.setMsgType(transInfo.getMsgType());
        reversalInfo.setCardNo(transInfo.getCardNo());
        reversalInfo.setProcCode(transInfo.getProcCode());
        reversalInfo.setAmt(transInfo.getAmt());
        reversalInfo.setTransmissionDateTime(transInfo.getTransmissionDateTime());
        reversalInfo.setStan(transInfo.getStan());
        reversalInfo.setLocalTime(transInfo.getLocalTime());
        reversalInfo.setLocalDate(transInfo.getLocalDate());
        reversalInfo.setExpDate(transInfo.getExpDate());
        reversalInfo.setMerchType(transInfo.getMerchType());
        reversalInfo.setPosEntryMode(transInfo.getPosEntryMode());
        if(transInfo.getCardSequenceNo() != null && !transInfo.getCardSequenceNo().isEmpty())
            reversalInfo.setCardSequenceNo(transInfo.getCardSequenceNo());
        reversalInfo.setPosConditionCode(transInfo.getPosConditionCode());
        reversalInfo.setPosPinCaptureCode(transInfo.getPosPinCaptureCode());
        reversalInfo.setSurcharge(transInfo.getSurcharge());
        reversalInfo.setAcqInstId(transInfo.getAcqInstId());
        //reversalInfo.setFwdInstId(message.getFwdInstId);
        if(transInfo.getTrack2() != null && !transInfo.getTrack2().isEmpty())
            reversalInfo.setTrack2(transInfo.getTrack2());
        reversalInfo.setRetRefNo(transInfo.getRetRefNo());
        if(transInfo.getServiceRestrictionCode() != null && !transInfo.getServiceRestrictionCode().isEmpty())
            reversalInfo.setServiceRestrictionCode(transInfo.getServiceRestrictionCode());
        reversalInfo.setTerminalId(transInfo.getTerminalId());
        reversalInfo.setMerchantId(transInfo.getMerchantId());
        reversalInfo.setMerchantLoc(transInfo.getMerchantLoc());
        reversalInfo.setCurrencyCode(transInfo.getCurrencyCode());
        reversalInfo.setMsgReasonCode(msgReasonCode);
        reversalInfo.setPosDataCode(transInfo.getPosDataCode());

        return reversalInfo;
    }

    public static String getIso8583MerchantLoc(String merchantLoc){
        if(merchantLoc == null)
            return (StringUtil.leftPadding(" ", 38, "") + COUNTRY_CODE);
        if(merchantLoc.length() > 38)
            return merchantLoc.substring(0, 38) + COUNTRY_CODE;
        return StringUtil.rightPadding(" ", 38, merchantLoc) + COUNTRY_CODE;
    }


    public IsoMessage decode(byte[] data) {
        try{
            IsoMessage isoMessageResponse = messageFactory.parseMessage(data, 0);
            return isoMessageResponse;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }


    private String getMac(IsoMessage isoMessage, Core mCore) throws Exception{
        final int len = 24;
        byte[] pbOutdata = new byte[100];
        int[] pbOutdataLen = new int[50];
        int macMode = 0x02;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        for (int i = 0; i < vectordata.length; i++)
            vectordata[i] = 0;
        int dataLen = len;
        byte[] data = new byte[len];
        int ret = -1;
        try {
            ret = mCore.getMacWithAlgorithm(ConstantUtils.APP_NAME, Core.ALGORITHM_3DES, vectorLen, vectordata, dataLen, data, macMode, pbOutdata, pbOutdataLen);
            //mCore.
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (ret == 0) {
            return HEX.bytesToHex(pbOutdata);
        }

        throw new Exception("Faield to create MAC");
    }



    public static boolean isSessionKeyInit() {
        return sessionKeyInit;
    }

    public static void setSessionKeyInit(boolean sessionKeyInit) {
        IsoMessageUtil.sessionKeyInit = sessionKeyInit;
    }


    public static String getAccountTypeName(String acctType){
        if("00".equalsIgnoreCase(acctType))
            return DEFAULT_ACCOUNT;
        if("10".equalsIgnoreCase(acctType))
            return SAVINGS_ACCOUNT;
        else if("20".equalsIgnoreCase(acctType))
            return CURRENT_ACCOUNT;
        else if("30".equalsIgnoreCase(acctType))
            return CREDIT_ACCOUNT;
        return DEFAULT_ACCOUNT;
    }

    public static String getTranTypeName(String procCode){
        String tranType = procCode.substring(0, 2);
        if(ConstantUtils.PURCHASE_PROC_CODE.equalsIgnoreCase(tranType))
            return PURCHASE;
        if(ConstantUtils.BALANCE_PROC_CODE.equalsIgnoreCase(tranType))
            return BALANCE;
        else if(ConstantUtils.REFUND_PROC_CODE.equalsIgnoreCase(tranType))
            return REFUND;
        return TRANSACTION;
    }
}
