package com.avantir.wpos;

import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import com.avantir.wpos.services.CallHomeJobService;
import com.avantir.wpos.services.DownloadKeysJobService;
import com.avantir.wpos.services.ReversalJobService;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.IsoMessageUtil;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.KeyUtils;
import sdk4.wangpos.libemvbinder.EmvCore;

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
        int reversalTimeInMill = globalData.getResendReversalPeriodInMin() * 60 * 1000;

        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName downloadKeysComponent = new ComponentName(getPackageName(), DownloadKeysJobService.class.getName());
        //ComponentName downloadKeysComponent = new ComponentName(getApplicationContext(), DownloadKeysJobService.class);
        JobInfo.Builder downloadKeysBuilder = new JobInfo.Builder(ConstantUtils.DOWNLOAD_KEYS_JOB_ID, downloadKeysComponent);
        downloadKeysBuilder = downloadKeysBuilder.setRequiresDeviceIdle(true);
        JobInfo downloadKeysJobInfo =  downloadKeysBuilder.setPeriodic(keydownloadTimeInMill) //every 12 hours (43200000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setPersisted(true)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(downloadKeysJobInfo);

        ComponentName callHomeComponent = new ComponentName(getPackageName(), CallHomeJobService.class.getName());
        JobInfo.Builder callHomeBuilder = new JobInfo.Builder(ConstantUtils.CALL_HOME_JOB_ID, callHomeComponent);
        callHomeBuilder = callHomeBuilder.setRequiresDeviceIdle(false);
        JobInfo callHomeJobInfo =  callHomeBuilder.setPeriodic(callHomeTimeInMill)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setPersisted(true)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(callHomeJobInfo);


        ComponentName reversalComponent = new ComponentName(getPackageName(), ReversalJobService.class.getName());
        JobInfo.Builder reversalBuilder = new JobInfo.Builder(ConstantUtils.REVERSAL_JOB_ID, reversalComponent);
        reversalBuilder = reversalBuilder.setRequiresDeviceIdle(false); // set to true
        JobInfo reversalJobInfo =  reversalBuilder.setPeriodic(reversalTimeInMill)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setPersisted(true)
                //.setMinimumLatency(1000)
                //.setRequiresCharging(true)
                .build();
        jobScheduler.schedule(reversalJobInfo);
    }

}
