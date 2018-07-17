package com.avantir.wpos.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.avantir.wpos.tasks.DeleteReversalTask;
import com.avantir.wpos.tasks.DeleteTransactionTask;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class DeleteTransactionJobService extends JobService {

    DeleteTransactionTask deleteTransactionTask = null;

    public boolean onStartJob(final JobParameters params){
        deleteTransactionTask = new DeleteTransactionTask() {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        deleteTransactionTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (deleteTransactionTask != null) {
            deleteTransactionTask.cancel(true);
        }

        return true;
    }
}
