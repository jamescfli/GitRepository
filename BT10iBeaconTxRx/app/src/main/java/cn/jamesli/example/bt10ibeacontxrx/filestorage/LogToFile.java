package cn.jamesli.example.bt10ibeacontxrx.filestorage;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jamesli on 15/9/1.
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
            try {
                // write the file once without BufferedWriter()
                PrintWriter pw = new PrintWriter(new FileWriter(dataFile));
                pw.println(params[0]);
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
                resultMessage = e.toString();
            }
            return resultMessage;
        }

        @Override
        protected void onPostExecute(String resultMessage) {
            Toast.makeText(mContext, "'" + mLogFileName + "' was " + resultMessage,
                    Toast.LENGTH_LONG).show();
//            // android.view.WindowLeaked: leaked window com.android.internal.policy.impl.PhoneWindow$DecorView
//            new AlertDialog.Builder(mContext).setMessage("'" + mLogFileName + "' was " + resultMessage).
//                    setPositiveButton("OK",null).show();
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


//    private File mDataFile;
//    private PrintWriter mPrintWriter;
//    private String mFileName;
//
//    public LogToFile(Context context,String fileName) {
//        mFileName = fileName;
//        // save to external cache directory
//        mDataFile = new File(context.getExternalCacheDir(), mFileName);
//
//        try {
//            mPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.mDataFile)));
//        } catch (IOException e) {
//            Log.e(TAG, "Could not open CSV file(s)", e);
//            e.printStackTrace();
//        }
//    }
//
//    public boolean close() {
//        if (mPrintWriter != null) {
//            mPrintWriter.close();
//        }
//
//        if (mPrintWriter.checkError()) {
//            Log.e(TAG, "Error when closing writer");
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    public void write(String data) {
//        mPrintWriter.println(data);
//
//        if (mPrintWriter.checkError()) {
//            Log.e(TAG, "Error when writing data");
//        }
//    }
}
