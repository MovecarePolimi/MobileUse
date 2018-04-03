package com.polimi.movecare_r01.applicationLogic.schedule;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;

public class MyJobScheduler {
    private static final String TAG = MyJobScheduler.class.getSimpleName();

    private static final String MILLIS_DATE = "millis_date";

    public void scheduleSendReportJob(Context context, boolean onlyInternetIssue) {
        Log.v(TAG, "Method scheduleSendReportJob: start");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int job_id = 1221;
            ComponentName mServiceComponent = new ComponentName(context, SendReportAgainJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(job_id, mServiceComponent);

            if(!onlyInternetIssue){
                SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
                builder.setMinimumLatency(1000*60*60*sharedPreferencesMgr.getReportInterval()); // try again after 1 hour
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);

            // Extras, work duration.
            /*PersistableBundle extras = new PersistableBundle();
            extras.putLong(MILLIS_DATE, millis);

            builder.setExtras(extras);*/

            JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            tm.schedule(builder.build());
        } else{
            Log.e(TAG, "Minimum android versione required is Lollipop");
            return;
        }

        Log.v(TAG, "Method scheduleSendReportJob: end");
    }

}
