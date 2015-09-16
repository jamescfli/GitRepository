package cn.jamesli.example.bt10ibeacontxrx.filestorage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jamesli on 15/9/1.
 */
public class LogToFile {
    private final static String TAG = "LogtoFile";

    private File mDataFile;
    private PrintWriter mPrintWriter;
    private String mFileName;

    public LogToFile(Context context,String fileName) {
        mFileName = fileName;
        // save to external cache directory
        mDataFile = new File(context.getExternalCacheDir(), mFileName);

        try {
            mPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.mDataFile)));
        } catch (IOException e) {
            Log.e(TAG, "Could not open CSV file(s)", e);
            e.printStackTrace();
        }
    }

    public boolean close() {
        if (mPrintWriter != null) {
            mPrintWriter.close();
        }

        if (mPrintWriter.checkError()) {
            Log.e(TAG, "Error when closing writer");
            return false;
        } else {
            return true;
        }
    }

    public void write(String data) {
        mPrintWriter.println(data);

        if (mPrintWriter.checkError()) {
            Log.e(TAG, "Error when writing data");
        }
    }
}
