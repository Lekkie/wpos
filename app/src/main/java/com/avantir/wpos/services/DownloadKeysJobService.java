package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.DownloadKeysTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DownloadKeysJobService extends JobService {

    DownloadKeysTask downloadKeysTask = null;

    public boolean onStartJob(final JobParameters params){
        downloadKeysTask = new DownloadKeysTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        downloadKeysTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (downloadKeysTask != null) {
            downloadKeysTask.cancel(true);
        }

        return true;
    }
}
