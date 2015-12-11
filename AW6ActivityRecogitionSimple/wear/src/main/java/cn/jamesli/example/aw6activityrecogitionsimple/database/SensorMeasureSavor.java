package cn.jamesli.example.aw6activityrecogitionsimple.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jamesli.example.aw6activityrecogitionsimple.measure.AccDataItem;

/**
 * Created by jamesli on 15/12/11.
 */
public class SensorMeasureSavor {
    private static final String TAG = "SensorMeasureSavor";

    // Database fields
    private SQLiteDatabase mDatabase;
    private static final String DATABASE_FILENAME_SUFFIX = "measure_";
    private static final String DATABASE_FILENAME_APPENDIX = ".db";
    private SensorMeasureSQLiteOpenHelper dbHelper;	// wrapper
    private static final String[] allColumns = {
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z
    };

    public SensorMeasureSavor(Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        String databaseFileName = DATABASE_FILENAME_SUFFIX
                + dateFormat.format(new Date().getTime()) + DATABASE_FILENAME_APPENDIX;
        dbHelper = new SensorMeasureSQLiteOpenHelper(context, databaseFileName);
    }

    // add the type of the activity to the database name
    public SensorMeasureSavor(Context context, String activityType) {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        String databaseFileName = DATABASE_FILENAME_SUFFIX + activityType + "_"
                + dateFormat.format(new Date().getTime()) + DATABASE_FILENAME_APPENDIX;
        dbHelper = new SensorMeasureSQLiteOpenHelper(context, databaseFileName);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createMeasure(int measureIndex, AccDataItem accDataItem) {
        ContentValues values = new ContentValues();
        // input values, no need for _ID
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, accDataItem.getTimeStamp());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, accDataItem.getXAxisAcc());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, accDataItem.getYAxisAcc());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, accDataItem.getZAxisAcc());

        long insertId = mDatabase.insert(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
                null,
                values);
        return (insertId >= 0);     // TRUE if succeeded, FALSE if failed
    }

    public void deleteMeasure(int idMeasure) {
        System.out.println("Measure deleted with id: " + idMeasure);
        // ( table, whereClause, whereArgs )
        mDatabase.delete(
                SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
                SensorMeasureSQLiteOpenHelper.SensorMeasureColumns._ID + " = " + idMeasure,
                null);  // WHERE args
    }

    public List<AccDataItem> getAllMeasures() {
        List<AccDataItem> listAccDataItems = new ArrayList<>();

        // ( table, columns, ... )
        Cursor cursor = mDatabase.query(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP));
            float x = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X));
            float y = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y));
            float z = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z));
            listAccDataItems.add(new AccDataItem(timestamp, x, y, z));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listAccDataItems;
    }
}
