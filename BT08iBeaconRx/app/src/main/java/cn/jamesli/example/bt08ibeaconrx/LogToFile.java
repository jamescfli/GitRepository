package cn.jamesli.example.bt08ibeaconrx;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jamesli on 15/8/26.
 */
public class LogToFile {

    private File mDataFile;
    private PrintWriter mPrintWriter;
    private String mFileName;

    private final static String TAG = "LogtoFile";


    public LogToFile(Context context,String fileName) {
        mFileName = fileName;
        mDataFile = new File(context.getExternalCacheDir(), mFileName);

        try {
            mPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.mDataFile)));
        } catch (IOException e) {
            Log.e(TAG, "Could not open CSV file(s)", e);
            e.printStackTrace();
        }
    }
    public void close()
    {
        if (mPrintWriter != null)
        {
            mPrintWriter.close();
        }

        if (mPrintWriter.checkError())
        {
            Log.e(TAG, "Error closing writer");
        }
    }

    public void write(String data)
    {
        mPrintWriter.println(data);

        if (mPrintWriter.checkError()) {
            Log.e(TAG, "Error writing data");
        }
    }

}
