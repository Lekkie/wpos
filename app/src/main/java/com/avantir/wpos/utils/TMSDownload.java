package com.avantir.wpos.utils;

import android.os.Build;
import android.os.Handler;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.listeners.CommsListener;
import com.avantir.wpos.services.HttpComms;
import com.avantir.wpos.services.TcpComms;

import java.util.HashMap;

/**
 * Created by lekanomotayo on 31/01/2018.
 */
public class TMSDownload {

    public static void downloadTerminalParams(Handler handler, HttpComms comms) throws Exception{
        HashMap headers = new HashMap();
        headers.put(ConstantUtils.DEVICE_SERIAL_NO, Build.SERIAL);
        String devicePublicKey = KeyUtils.getBase64PublicKey();
        headers.put(ConstantUtils.TERMINAL_PUBLIC_KEY, devicePublicKey);
        ICommsListener commsListener = new CommsListener(handler, ConstantUtils.NETWORK_TMS_TERM_PARAM_DOWNLOAD_REQ_TYPE);
        comms.dataCommu(WPOSApplication.app, ConstantUtils.TERMINAL_DOWNLOAD_URI, 0, headers, null, commsListener);
    }


}
