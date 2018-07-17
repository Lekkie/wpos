package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.HttpMessageUtil;
import com.avantir.wpos.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DeleteTransactionTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DeleteTransactionTask";
    private GlobalData globalData;
    private TransInfoDao transInfoDao;


    public DeleteTransactionTask() {
        initData();
    }

    protected void initData() {
        globalData = GlobalData.getInstance();
        transInfoDao = new TransInfoDao(WPOSApplication.app);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try{
            int retentionDays = globalData.getTranNotifyRetentionInDays();
            long startOfDay = TimeUtil.getStartOfDay(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(startOfDay));
            calendar.add(Calendar.DAY_OF_YEAR, -retentionDays);

            List<TransInfo> transInfoList = transInfoDao.findOlderThanDate(calendar.getTime().getTime());
            if(transInfoList != null){
                transInfoDao.deleteList(transInfoList);
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
