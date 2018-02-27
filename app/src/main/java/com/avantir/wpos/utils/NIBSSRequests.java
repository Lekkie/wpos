package com.avantir.wpos.utils;

import android.os.Handler;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
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
    public static void downloadTMK(Handler handler, TcpComms comms) throws Exception{
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

    public static void downloadAID(Handler handler, TcpComms comms) throws Exception{
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] aidReqBytes = isoMessageUtil.createAIDDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_AID_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, aidReqBytes, commsListener);
    }

    public static void downloadCAPK(Handler handler, TcpComms comms) throws Exception{
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] capkReqBytes = isoMessageUtil.createCAPKDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_CAPK_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, capkReqBytes, commsListener);
    }

    public static void downloadTerminalParam(Handler handler, TcpComms comms) throws Exception{
        IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
        byte[] tmkReqBytes = isoMessageUtil.createTermParamDownloadRequest();
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_NIBSS_TERM_PARAM_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, tmkReqBytes, commsListener);
    }

    public static String doPurchaseReversal(ReversalInfo reversalInfo, boolean isRepeat) throws Exception{
        byte[] data = IsoMessageUtil.createPurchaseReversal(reversalInfo, isRepeat);
        GlobalData globalData = GlobalData.getInstance();
        TcpComms comms = new TcpComms(globalData.getCTMSHost(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
        byte[] respData = comms.dataCommuBlocking(WPOSApplication.app, data);
        IsoMessage isoMsgResponse = IsoMessageUtil.getInstance().decode(respData);
        System.out.println(isoMsgResponse.debugString());
        String responseCode = isoMsgResponse.getObjectValue(39);
        return  responseCode;
    }

}
