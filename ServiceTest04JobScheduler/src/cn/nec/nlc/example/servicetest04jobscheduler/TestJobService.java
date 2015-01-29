package cn.nec.nlc.example.servicetest04jobscheduler;

import java.util.LinkedList;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobScheduler;
//import android.app.job.JobService;

import android.util.Log;

public class TestJobService extends JobService {
	private static final String TAG = "SyncService";
	
	@Override
	public boolean onStartJob(JobParameters params) {
		// nothing's been done. Just track which jobs have landed on our service
		// and update UI accordingly.
		Log.i(TAG, "on start job: " + params.getJobId());
		return true;
	}
	
	@Override
	public boolean onStopJob(JobParameters params) {
		Log.i(TAG, "on stop job" + params.getJobId());
		return true;
	}
	
	MainActivity mActivity;
	private final LinkedList<JobParameters> jobParamsMap = new LinkedList<JobParameters>();
	
	public void setUiCallback(MainActivity activity) {
		mActivity = activity;
	}
}
