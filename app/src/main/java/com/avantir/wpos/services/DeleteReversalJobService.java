package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.DeleteReversalTask;
import com.avantir.wpos.tasks.TransNotifyUploadRetryTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DeleteReversalJobService extends JobService {

    DeleteReversalTask deleteReversalTask = null;

    public boolean onStartJob(final JobParameters params){
        deleteReversalTask = new DeleteReversalTask() {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        deleteReversalTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (deleteReversalTask != null) {
            deleteReversalTask.cancel(true);
        }

        return true;
    }
}
