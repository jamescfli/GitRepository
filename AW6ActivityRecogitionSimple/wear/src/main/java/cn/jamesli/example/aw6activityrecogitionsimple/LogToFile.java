package cn.jamesli.example.aw6activityrecogitionsimple;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jamesli on 15/11/30.
 */
public class LogToFile {
    private final static String TAG = "LogToFileAsync";

    // params, progress and result
    class LogToFileAsyncTask extends AsyncTask<String, Void, String> {
        // retrieve context from outside class

        @Override
        protected String doInBackground(String... params) {
            String resultMessage = "successfully saved.";
            File dataFile = new File(mContext.getExternalCacheDir(), mLogFileName);
//            File dataFile = new File(mContext.getExternalFilesDir(null), mLogFileName);
            // write the file once without BufferedWriter()
            PrintWriter pw = null;
            try {
                // write the file once without BufferedWriter()
                pw = new PrintWriter(new FileWriter(dataFile));
                pw.println(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                resultMessage = e.toString();
            } finally {
                pw.close();
            }
            return resultMessage;
        }

        @Override
        protected void onPostExecute(String resultMessage) {
            Toast.makeText(mContext, "'" + mLogFileName + "' was " + resultMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    private Context mContext;
    private String mLogFileName;

    public LogToFile(Context context, String fileNamePrefix, String fileNameAppendix) {
        mContext = context;
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        mLogFileName = fileNamePrefix + dateFormat.format(new Date().getTime()) + fileNameAppendix;
    }

    // the function is expected to appear in onPause() of fragment only once
    public void saveToExternalCacheDir(String content) {
        LogToFileAsyncTask task = new LogToFileAsyncTask();
        task.execute(content);
    }
}
