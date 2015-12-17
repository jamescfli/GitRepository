package cn.jamesli.example.aw6activityrecogitionsimple.database;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.aw6activityrecogitionsimple.WatchMainActivity;
import cn.jamesli.example.aw6activityrecogitionsimple.measure.SensorValueItem;

/**
 * Created by jamesli on 15/12/12.
 */
public class LogToSqlFileAsync {
    private final static String TAG = "LogToSqlFileAsync";

    // It is better to put the file access in another thread to avoid blocking UI thread
    class LogToSqlFileAsyncTask extends AsyncTask<List<SensorValueItem>, Void, String> {
        // retrieve context from outside class

        @Override
        protected String doInBackground(List<SensorValueItem>... params) {
            String resultMessage = "successfully saved.";
            SensorMeasureSavor sensorMeasureSavor = new SensorMeasureSavor(
                    mContext,
                    mNameOfActivity
            );
            ArrayList<SensorValueItem> listAccData = new ArrayList<>(params[0]);
            ArrayList<SensorValueItem> listGyroData = new ArrayList<>(params[1]);
            sensorMeasureSavor.open();
//            // low speed due to:
//            //      begin_transaction();
//            //      insert();
//            //      commit_transaction();
//            // for each record
//            for (int i = 0, SIZE = listAccData.size(); i < SIZE; i++) {
//                sensorMeasureSavor.createMeasure(listAccData.get(i));
//            }
            boolean returnFlag = sensorMeasureSavor.createBatchMeasure(listAccData, listGyroData);
            if (!returnFlag) {  // false => failed to save data
                resultMessage = "failed to be saved";
            }
            sensorMeasureSavor.close();
            return resultMessage;
        }

        @Override
        protected void onPostExecute(String resultMessage) {
            Toast.makeText(mContext, "'" + mNameOfActivity + "' data was " + resultMessage,
                    Toast.LENGTH_LONG).show();
            // change status textView say file saved
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((WatchMainActivity) mContext).getTextViewStatus().setText("'"
                            + mNameOfActivity + "' data was saved.");
                    // resume LogToFile the button after data has been saved
                    ((WatchMainActivity) mContext).getButtonLogToFile().setEnabled(true);
                }
            });

        }
    }

    private Context mContext;
    private String mNameOfActivity;

    public LogToSqlFileAsync(Context context, String nameOfActivity) {
        mContext = context;
        mNameOfActivity = nameOfActivity;
    }

    public void saveToExternalCacheDir(List<SensorValueItem> listAccData,
                                       List<SensorValueItem> listGyroData) {
        LogToSqlFileAsyncTask task = new LogToSqlFileAsyncTask();
        task.execute(listAccData, listGyroData);
    }

}