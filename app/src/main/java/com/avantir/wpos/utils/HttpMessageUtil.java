package com.avantir.wpos.utils;

import android.os.RemoteException;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.model.TransInfo;
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


}
