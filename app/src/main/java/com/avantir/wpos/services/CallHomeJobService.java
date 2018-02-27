package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.CallHomeTask;
import com.avantir.wpos.tasks.DownloadKeysTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class CallHomeJobService extends JobService {

    CallHomeTask callHomeTask = null;

    public boolean onStartJob(final JobParameters params){
        callHomeTask = new CallHomeTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        callHomeTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (callHomeTask != null) {
            callHomeTask.cancel(true);
        }

        return true;
    }
}
