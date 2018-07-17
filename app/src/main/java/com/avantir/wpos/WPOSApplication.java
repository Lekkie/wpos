package com.avantir.wpos;

import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import com.avantir.wpos.services.*;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.IsoMessageUtil;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.KeyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lekanomotayo on 23/01/2018.
 */
public class WPOSApplication extends Application { //LitePalApplication

    public static List<Activity> activityList = new ArrayList<Activity>();
    public static WPOSApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        if(!KeyUtils.privateKeyExists()){
            KeyUtils.generateRSAKey();
        }

        GlobalData globalData = GlobalData.getInstance();
        globalData.init(this);
        IsoMessageUtil.getInstance();

        int keydownloadTimeInMill = globalData.getCheckKeyDownloadIntervalInMin() * 60 * 1000;
        int callHomeTimeInMill = globalData.getCallHomePeriodInMin() * 60 * 1000;
        int reversalRetryTimeInMill = globalData.getReversalRetryTimeInMin() * 60 * 1000;
        int transNotifyRetryTimeInMill = globalData.getTransactionNotificationRetryTimeInMin() * 60 * 1000;

        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName downloadKeysComponent = new ComponentName(getPackageName(), DownloadKeysJobService.class.getName());
        JobInfo.Builder downloadKeysBuilder = new JobInfo.Builder(ConstantUtils.DOWNLOAD_KEYS_JOB_ID, downloadKeysComponent);
        //downloadKeysBuilder = downloadKeysBuilder.setRequiresDeviceIdle(true);
        JobInfo downloadKeysJobInfo =  downloadKeysBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                .setPeriodic(keydownloadTimeInMill) //every 12 hours (43200000)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(downloadKeysJobInfo);

        ComponentName callHomeComponent = new ComponentName(getPackageName(), CallHomeJobService.class.getName());
        JobInfo.Builder callHomeBuilder = new JobInfo.Builder(ConstantUtils.CALL_HOME_JOB_ID, callHomeComponent);
        //callHomeBuilder = callHomeBuilder.setRequiresDeviceIdle(true);
        JobInfo callHomeJobInfo =  callHomeBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                .setPeriodic(callHomeTimeInMill)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(callHomeJobInfo);


        ComponentName reversalRetryComponent = new ComponentName(getPackageName(), ReversalJobService.class.getName());
        JobInfo.Builder reversalRetryBuilder = new JobInfo.Builder(ConstantUtils.REVERSAL_JOB_ID, reversalRetryComponent);
        //reversalRetryBuilder = reversalRetryBuilder.setRequiresDeviceIdle(true); // set to true
        JobInfo reversalRetryJobInfo =  reversalRetryBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                .setPeriodic(reversalRetryTimeInMill)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(reversalRetryJobInfo);


        ComponentName transNotifyUploadRetryComponent = new ComponentName(getPackageName(), TransNotifyUploadRetryJobService.class.getName());
        JobInfo.Builder transNotifyUploadRetryBuilder = new JobInfo.Builder(ConstantUtils.TRANSACTION_NOTIFICATION_JOB_ID, transNotifyUploadRetryComponent);
        //transNotifyUploadRetryBuilder = transNotifyUploadRetryBuilder.setRequiresDeviceIdle(true); // set to true
        JobInfo transNotifyUploadRetryJobInfo =  transNotifyUploadRetryBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                .setPeriodic(transNotifyRetryTimeInMill)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(transNotifyUploadRetryJobInfo);


        ComponentName deleteReversalComponent = new ComponentName(getPackageName(), DeleteReversalJobService.class.getName());
        JobInfo.Builder deleteReversalBuilder = new JobInfo.Builder(ConstantUtils.DELETE_REVERSAL_JOB_ID, deleteReversalComponent);
        //deleteReversalBuilder = deleteReversalBuilder.setRequiresDeviceIdle(true); // set to true
        JobInfo deleteReversalJobInfo =  deleteReversalBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                //.setPeriodic(callHomeTimeInMill)
                //.setMinimumLatency(callHomeTimeInMill)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(deleteReversalJobInfo);


        ComponentName deleteTransactionComponent = new ComponentName(getPackageName(), DeleteTransactionJobService.class.getName());
        JobInfo.Builder deleteTransactionBuilder = new JobInfo.Builder(ConstantUtils.DELETE_TRANSACTION_JOB_ID, deleteTransactionComponent);
        //deleteTransactionBuilder = deleteTransactionBuilder.setRequiresDeviceIdle(true); // set to true
        JobInfo deleteTransactionJobInfo =  deleteTransactionBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(true)
                //.setPersisted(true)
                //.setMinimumLatency(callHomeTimeInMill)
                .setPeriodic(callHomeTimeInMill)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(deleteTransactionJobInfo);

    }

}
