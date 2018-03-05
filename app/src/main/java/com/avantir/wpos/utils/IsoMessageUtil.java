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


/**
 * Created by lekanomotayo on 10/02/2018.
 */
public class IsoMessageUtil {

    /*
    private final static String FORMAT =
            "2:Primary Account Number (PAN),LLVAR-N19;"
                    + "3:Processing Code,N6;"
                    + "4:Amount Transaction,N12;"
                    + "7:Transmission Date and Time,N10;"
                    + "9:Settlement Conversion Rate,N8;"
                    + "11:Systems Trace Audit Number,N6;"
                    + "12:Local Transaction Time,N6;"
                    + "13:Local Transaction Date,N4;"
                    + "14:Expiration Date,N4;"//YYMM
                    + "15:Settlement Date,N4;"
                    + "18:Merchant Type,N4;"
                    + "22:POS Entry Mode,N3;"
                    + "23:Card Sequence Number,N3;"
                    + "25:POS Condition Code,N2;"
                    + "26:POS PIN Capture Code,N2;"
                    + "28:Transaction Fee Amount,AN9;"
                    + "29:Settlement Fee Amount,AN9;"
                    + "30:Transaction Processing Fee Amount,AN9;"
                    + "31:Settle Processing Fee Amount,AN9;"
                    + "32:Acquiring Institution ID Code,LLVAR-N11;"
                    + "33:Forwarding Institution ID Code,LLVAR-N11;"
                    + "35:Track 2 Data,LLVAR-N37;"
                    + "37:Retrieval Reference Number,AN12;"
                    + "38:Authorization ID Response,AN6;"
                    + "39:Response Code,AN9;"
                    + "40:Service Restriction Code,AN9;"
                    + "41:Card Acceptor Terminal ID,AN9;"
                    + "42:Card Acceptor ID Code,ANS15;"
                    + "43:Card Acceptor Name Location,ANS40;"
                    + "44:Additional Response Data,LLVAR-AN40;"
                    + "45:Track 1 Data,LLLVAR-ANS999;"
                    // + "46:自定义域,LLVAR-ANS4;"
                    //  + "47:自定义域,LLLVAR-ANS999;"
                    + "48:Additional Data,LLLVAR-N999;"
                    + "49:Transaction Currency Code,N3;"
                    + "50:Settlement Currency Code,N3;"
                    + "52:PIN Data,B8;"
                    + "53:Security Related Control Information,B48;"
                    + "54:Additional Amounts,LLLVAR-AN120;"
                    + "55:Integrated Circuit Card System Related Data,LLLVAR-AN510;"
                    + "56:Message Reason Code,LLLVAR-AN999;"
                    + "57:Authorization Life-cycle Code,LLLVAR-ANS999;"
                    + "58:Authorizing Agent Institution,LLVAR-ANS11;"
                    + "59:Echo Data,LLLVAR-ANS255;"
                    + "60:Payment Information,LLLVAR-N999;"
                    //  + "61:原始信息域,LLLVAR-N29;"
                    + "62:Private management data 1,LLLVAR-ANS999;"
                    + "63:Private management data 2,LLLVAR-ANS163;"
                    + "64:MAC,B24;"
                    + "67:Extended Payment Code,N2;"
                    + "90:Original Data Elements,N42;"
                    + "95:Replacement Amounts,N42;"
                    + "98:Payee,AN25;"
                    + "100:Receiving Institution ID Code,LLVAR-N11;"
                    + "102:Account Identification 1,LLVAR-N28;"
                    + "103:Account Identification 2,LLVAR-N28;"
                    + "123:POS Data Code,LLVAR-N15;"
                    + "124:Near Field Communication Data,LLLVAR-ANS999;"
                    + "128:MAC,B24;";

    public final static Field ITEMS[] = Field.makeItems(FORMAT, true);
    */

    private static String clearTsk;
    static MessageFactory<IsoMessage> messageFactory;
    private static IsoMessageUtil instance;
    private static boolean messageFactoryInit = false;
    private static boolean sessionKeyInit = false;

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
        if (!StringUtil.isEmpty(serviceRestrictionCode)) {
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


    public static byte[] createPurchaseReversal(ReversalInfo reversalInfo, boolean isRepeat) throws Exception{
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
        if (serviceRestrictionCode != null) {
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
            return (StringUtil.leftPadding(" ", 38, "") + "NG");
        if(merchantLoc.length() > 38)
            return merchantLoc.substring(0, 38) + "NG";
        return StringUtil.rightPadding(" ", 38, merchantLoc) + "NG";
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
            return "DEFAULT";
        if("10".equalsIgnoreCase(acctType))
            return "SAVINGS";
        else if("20".equalsIgnoreCase(acctType))
            return "CURRENT";
        else if("30".equalsIgnoreCase(acctType))
            return "CREDIT";
        return "DEFAULT";
    }
}
