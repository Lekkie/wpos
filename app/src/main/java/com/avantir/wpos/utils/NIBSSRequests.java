package com.avantir.wpos.utils;

import android.os.Handler;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.listeners.CommsListener;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.TcpComms;
import com.solab.iso8583.IsoMessage;

/**
 * Created by lekanomotayo on 31/01/2018.
 */
public class NIBSSRequests {

    //TMK
    public static void downloadTMK(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] tmkReqBytes = isoMessageUtil.createTMKDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_TMK_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, tmkReqBytes, commsListener);
    }

    //MAK
    public static void downloadTSK(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] tskReqBytes = isoMessageUtil.createTSKDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_TSK_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, tskReqBytes, commsListener);
    }

    //PEK
    public static void downloadTPK(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] tpkReqBytes = isoMessageUtil.createTPKDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_TPK_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, tpkReqBytes, commsListener);
    }

    public static void downloadAID(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] aidReqBytes = isoMessageUtil.createAIDDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_AID_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, aidReqBytes, commsListener);
    }

    public static void downloadCAPK(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] capkReqBytes = isoMessageUtil.createCAPKDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_CAPK_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, capkReqBytes, commsListener);
    }

    public static void downloadTerminalParam(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] tmkReqBytes = isoMessageUtil.createTermParamDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_NIBSS_TERM_PARAM_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, tmkReqBytes, commsListener);
    }


    public static void doPurchase(TransInfo transInfo, TcpComms comms, Handler handler) throws Exception {
        byte[] dataBytes = IsoMessageUtil.createRequest(transInfo);
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_PURCHASE_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, dataBytes, commsListener);
    }

    public static void doCashBack(TransInfo transInfo, TcpComms comms, Handler handler) throws Exception {
        byte[] dataBytes = IsoMessageUtil.createRequest(transInfo);
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_CASHBACK_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, dataBytes, commsListener);
    }

    public static void doCashAdvance(TransInfo transInfo, TcpComms comms, Handler handler) throws Exception {
        byte[] dataBytes = IsoMessageUtil.createRequest(transInfo);
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_CASH_ADVANCE_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, dataBytes, commsListener);
    }

    public static String doReversal(ReversalInfo reversalInfo, boolean isRepeat, TcpComms comms) throws Exception {
        byte[] data = IsoMessageUtil.createRequestReversal(reversalInfo, isRepeat);
        byte[] respData = comms.dataCommuBlocking(WPOSApplication.app, data);
        IsoMessage isoMsgResponse = IsoMessageUtil.getInstance().decode(respData);
        System.out.println(isoMsgResponse.debugString());
        String responseCode = isoMsgResponse.getObjectValue(39);
        return responseCode;
    }

    //EoD
    public static void downloadEoD(Handler handler, TcpComms comms) throws Exception {
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] eodReqBytes = isoMessageUtil.createDailyTransactionReportDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_EOD_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, eodReqBytes, commsListener);
    }

}
