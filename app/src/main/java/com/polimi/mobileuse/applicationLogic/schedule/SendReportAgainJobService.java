package com.polimi.mobileuse.applicationLogic.schedule;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.polimi.mobileuse.applicationLogic.service.CreateDailyReportService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // Version: 5.0, API 21

public class SendReportAgainJobService extends JobService {
    private static final String TAG = SendReportAgainJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v(TAG, "JobService onStartJob: start");

        // start new Service here
        startService(new Intent(this, CreateDailyReportService.class));

        Log.v(TAG, "JobService onStartJob: end");

        // Return true as there's more work to be done with this job.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        Log.v(TAG, "JobService onStopJob: start and end");
        // Return false to drop the job.
        return false;
    }
}
