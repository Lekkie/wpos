package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.ReversalTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class ReversalJobService extends JobService {

    ReversalTask reversalTask = null;

    public boolean onStartJob(final JobParameters params){
        reversalTask = new ReversalTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        reversalTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (reversalTask != null) {
            reversalTask.cancel(true);
        }

        return true;
    }
}
