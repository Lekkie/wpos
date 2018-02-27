package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.IsoMessageUtil;
import com.solab.iso8583.IsoMessage;
import wangpos.sdk4.libkeymanagerbinder.Key;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class CallHomeTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "CallHomeTask";
    private final Context mApplicationContext;
    //protected BaseHandler baseHandler = new BaseHandler();
    private final int MSG_FINISH_COMMS = 1, MSG_FINISH_ERROR_COMMS = 2;
    GlobalData globalData;
    private TcpComms comms;
    Key mKey;


    public CallHomeTask(Context context) {
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
        comms = new TcpComms(globalData.getCTMSHost(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        // Call NIBSS
        try{
            IsoMessageUtil isoMessageUtil = IsoMessageUtil.getInstance();
            byte[] callHomeReqBytes = isoMessageUtil.createCallHomeRequest();
            byte[] receiveData = comms.dataCommuBlocking(WPOSApplication.app, callHomeReqBytes);
            IsoMessage callHomeIsoMsg = isoMessageUtil.decode(receiveData);
            System.out.println(callHomeIsoMsg.debugString());
            String respCode  = callHomeIsoMsg.getObjectValue(39);
            //if("00".equalsIgnoreCase(respCode))
            // GlobalData.getInstance().setlastCallHomeTime(time);
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
