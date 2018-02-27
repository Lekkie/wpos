package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.*;
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

        // Fetch all open transaction
        try{
            //List<TransInfo> transInfoList1 = transInfoDao.findAll();
            List<TransInfo> transInfoList = transInfoDao.findAllOpenTransaction();
            if(transInfoList != null){
                for(TransInfo transInfo: transInfoList){
                    try{
                        long now = System.currentTimeMillis();
                        long diff = now - transInfo.getCreatedOn();
                        if(diff > (60 * 60 * 1000)){ // set this to about 1hr
                            ReversalInfo reversalInfo = reversalInfoDao.findByRetRefNo(transInfo.getRetRefNo());
                            if(reversalInfo == null){
                                reversalInfo = IsoMessageUtil.createReversalInfo(transInfo, ConstantUtils.MSG_REASON_CODE_TIMEOUT_WAITING_FOR_RESPONSE);
                                reversalInfo.setCreatedOn(System.currentTimeMillis());
                                reversalInfoDao.create(reversalInfo);
                            }
                            transInfoDao.updateCompletionStatusByRetRefNo(transInfo.getRetRefNo(), 1);
                        }
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }


            // Fetch all open reversal
            //List<ReversalInfo> reversalInfoList1 = reversalInfoDao.findAll();
            List<ReversalInfo> reversalInfoList = reversalInfoDao.findAllOpenTransaction();
            //System.out.println("Is terminal idle: " );
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
                        String responseCode = NIBSSRequests.doPurchaseReversal(reversalInfo, isRepeat);
                        reversalInfo.setStatus(StringUtil.isEmpty(responseCode) ? "" : responseCode);
                        reversalInfo.setCompleted(1);
                        reversalInfoDao.updateStatusCompletionByRetRefNo(reversalInfo.getRetRefNo(), reversalInfo.getStatus(), reversalInfo.getCompleted());
                        transInfoDao.updateReversalCompletionStatusByRetRefNo(retRefNo, 1, 1);
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
