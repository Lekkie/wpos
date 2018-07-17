package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.HttpComms;
import com.avantir.wpos.services.TcpComms;
import com.avantir.wpos.utils.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class TransNotifyUploadRetryTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "ProcessTransactionNotificationTask";
    private final Context mApplicationContext;
    private GlobalData globalData;
    private ReversalInfoDao reversalInfoDao;
    private TransInfoDao transInfoDao;


    public TransNotifyUploadRetryTask(Context context) {
        mApplicationContext = context.getApplicationContext();
        initData();
    }

    protected void initData() {
        globalData = GlobalData.getInstance();
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
            List<TransInfo> transInfoList = transInfoDao.findAllCompletedUnNotifiedTransaction();
            if(transInfoList != null){
                for(TransInfo transInfo: transInfoList){
                    try{
                        HttpMessageUtil httpMessageUtil = HttpMessageUtil.getInstance();
                        byte[] responseBytes = httpMessageUtil.sendNotificationSync(globalData, transInfo);
                        httpMessageUtil.receiveNotificationResponse(responseBytes, transInfo.getRetRefNo(), transInfoDao);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }


            // Fetch all open reversal
            //List<ReversalInfo> reversalInfoList1 = reversalInfoDao.findAll();
            List<ReversalInfo> reversalInfoList = reversalInfoDao.findAllCompletedUnNotifiedReversalTransaction();
            //System.out.println("Is terminal idle: " );
            if(reversalInfoList != null){
                for(ReversalInfo reversalInfo: reversalInfoList){
                    try{
                        HttpMessageUtil httpMessageUtil = HttpMessageUtil.getInstance();
                        byte[] responseBytes = httpMessageUtil.sendNotificationReversalSync(globalData, reversalInfo);
                        httpMessageUtil.receiveNotificationReversalResponse(responseBytes, reversalInfo.getRetRefNo(), reversalInfoDao);
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
