package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.listeners.CommsListener;
import com.avantir.wpos.services.HttpComms;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.IsoMessageUtil;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.KeyUtils;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.util.HexCodec;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libkeymanagerbinder.Key;

import java.util.HashMap;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DownloadKeysTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DownloadKeysTask";
    private final Context mApplicationContext;
    //protected BaseHandler baseHandler = new BaseHandler();
    private final int MSG_FINISH_COMMS = 1, MSG_FINISH_ERROR_COMMS = 2;
    GlobalData globalData;
    private TcpComms tcpComms;
    private HttpComms httpComms;
    private Key mKey;
    private Core mCore;


    public DownloadKeysTask(Context context) {
        mApplicationContext = context.getApplicationContext();
        initData();
    }

    protected void initData() {
        new Thread() {
            @Override
            public void run() {
                mKey = new Key(mApplicationContext);
            }
        }.start();
        globalData = GlobalData.getInstance();
        tcpComms = new TcpComms(globalData.getCTMSHost(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        // Download TMK, Session Key, Pin Key, IPEK Track 2, IPEK EMV
        // Save all.
        try{
            //int keyDownloadPeriodInMill = globalData.getKeyDownloadPeriodInMin() * 60 * 1000;
            long lastKeyDownload = globalData.getLastKeyDownloadDate();
            long now = System.currentTimeMillis();
            //if((now - lastKeyDownload) >= keyDownloadPeriodInMill){

                IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
                byte[] tmkReqBytes = isoMessageUtil.createTMKDownloadRequest();
                byte[] receiveData = tcpComms.dataCommuBlocking(WPOSApplication.app, tmkReqBytes);
                IsoMessage tmkIsoMsg = isoMessageUtil.decode(receiveData);
                System.out.println(tmkIsoMsg.debugString());
                byte[] tmkDataBytes = tmkIsoMsg == null ? null : (byte[]) tmkIsoMsg.getField(53).getValue();
                String tmkData = tmkDataBytes == null ? null : HexCodec.hexEncode(tmkDataBytes, 0, tmkDataBytes.length);

                byte[] tskReqBytes = isoMessageUtil.createTSKDownloadRequest();
                receiveData = tcpComms.dataCommuBlocking(WPOSApplication.app, tskReqBytes);
                IsoMessage tskIsoMsg = isoMessageUtil.decode(receiveData);
                System.out.println(tskIsoMsg.debugString());
                byte[] tskDataBytes = tskIsoMsg == null ? null : (byte[]) tskIsoMsg.getField(53).getValue();
                String tskData = tskDataBytes == null ? null : HexCodec.hexEncode(tskDataBytes, 0, tskDataBytes.length);

                byte[] tpkReqBytes = isoMessageUtil.createTPKDownloadRequest();
                receiveData = tcpComms.dataCommuBlocking(WPOSApplication.app, tpkReqBytes);
                IsoMessage tpkIsoMsg = isoMessageUtil.decode(receiveData);
                System.out.println(tpkIsoMsg.debugString());
                byte[] tpkDataBytes = tpkIsoMsg == null ? null : (byte[]) tpkIsoMsg.getField(53).getValue();
                String tpkData = tpkDataBytes == null ? null : HexCodec.hexEncode(tpkDataBytes, 0, tpkDataBytes.length);

                if(tmkData == null || tskData == null || tpkData == null)
                    throw new Exception();

                String tmk = tmkData.substring(0, 32);
                String tmkKcv = tmkData.substring(32, 38);
                String tsk = tskData.substring(0, 32);
                String tskKcv = tskData.substring(32, 38);
                String tpk = tpkData.substring(0, 32);
                String tpkKcv = tpkData.substring(32, 38);

                KeyUtils.saveTMK(mKey, tmk, tmkKcv);
                globalData.setTMKKeyFlag(true);
                KeyUtils.saveTSK(mKey, tsk, tskKcv);
                globalData.setMacKeyFlag(true);
                KeyUtils.saveTPK(mKey, tpk, tpkKcv);
                globalData.setPinKeyFlag(true);
                if(globalData.getLocalMasterKeyLoadedFlag())
                    globalData.setKeysLoadedFlag(true);
                globalData.setLastKeyDownloadDate(System.currentTimeMillis());
            //}
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {

    }

}
