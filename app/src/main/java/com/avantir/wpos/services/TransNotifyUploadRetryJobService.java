package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.TransNotifyUploadRetryTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class TransNotifyUploadRetryJobService extends JobService {

    TransNotifyUploadRetryTask transactionNotificationTask = null;

    public boolean onStartJob(final JobParameters params){
        transactionNotificationTask = new TransNotifyUploadRetryTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        transactionNotificationTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (transactionNotificationTask != null) {
            transactionNotificationTask.cancel(true);
        }

        return true;
    }
}
