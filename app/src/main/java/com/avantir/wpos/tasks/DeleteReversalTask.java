package com.avantir.wpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.ReversalInfoDao;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DeleteReversalTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DeleteReversalTask";
    private GlobalData globalData;
    private ReversalInfoDao reversalInfoDao;


    public DeleteReversalTask() {
        initData();
    }

    protected void initData() {
        globalData = GlobalData.getInstance();
        reversalInfoDao  = new ReversalInfoDao(WPOSApplication.app);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try{
            int retentionDays = globalData.getReversalRetentionInDays();
            long startOfDay = TimeUtil.getStartOfDay(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(startOfDay));
            calendar.add(Calendar.DAY_OF_YEAR, -retentionDays);

            List<ReversalInfo> reversalInfoList = reversalInfoDao.findOlderThanDate(calendar.getTime().getTime());
            if(reversalInfoList != null){
                reversalInfoDao.deleteList(reversalInfoList);
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
