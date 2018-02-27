package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.IsoMessageUtil;
import com.avantir.wpos.utils.NIBSSRequests;
import com.solab.iso8583.IsoMessage;
import wangpos.sdk4.libkeymanagerbinder.Key;

import java.util.List;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class ReversalTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "ProcessReversalTask";
    private final Context mApplicationContext;
    GlobalData globalData;
    private TcpComms comms;
    ReversalInfoDao reversalInfoDao;
    TransInfoDao transInfoDao;


    public ReversalTask(Context context) {
        mApplicationContext = context.getApplicationContext();
        initData();
    }

    protected void initData() {
        globalData = GlobalData.getInstance();
        comms = new TcpComms(globalData.getCTMSHost(), globalData.getCTMSPort(), globalData.getCTMSTimeout(), globalData.getIfCTMSSSL(), null);
        reversalInfoDao  = new ReversalInfoDao(WPOSApplication.app);
        transInfoDao = new TransInfoDao(WPOSApplication.app);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        // Fetch all open reversal
        try{
            List<ReversalInfo> reversalInfoList = reversalInfoDao.findAllOpenTransaction();
            if(reversalInfoList != null){
                for(ReversalInfo reversalInfo: reversalInfoList){
                    try{
                        int retry = reversalInfo.getRetryNo();
                        boolean isRepeat = false;
                        if(retry > 0)
                            isRepeat = true;
                        retry++;
                        String retRefNo = reversalInfo.getRetRefNo();
                        reversalInfoDao.updateRetryByRetRefNo(retRefNo, retry);
                        String responseCode = NIBSSRequests.doPurchaseReversal(reversalInfoDao, reversalInfo, isRepeat);
                        if("00".equalsIgnoreCase(responseCode))
                            transInfoDao.updateReversalStatusByRetRefNo(retRefNo, true);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
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
