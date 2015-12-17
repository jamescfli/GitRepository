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

import cn.jamesli.example.aw6activityrecogitionsimple.measure.SensorValueItem;

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
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z
    };

//    public SensorMeasureSavor(Context context) {
//        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
//        String databaseFileName = DATABASE_FILENAME_SUFFIX
//                + dateFormat.format(new Date().getTime()) + DATABASE_FILENAME_APPENDIX;
//        dbHelper = new SensorMeasureSQLiteOpenHelper(context, databaseFileName);
//    }

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

//    public boolean createMeasure(SensorValueItem accDataItem) {
//        ContentValues values = new ContentValues();
//        // input values, no need for _ID
//        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, accDataItem.getTimeStamp());
//        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, accDataItem.getXAxisValue());
//        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, accDataItem.getYAxisValue());
//        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, accDataItem.getZAxisValue());
//
//        long insertId = mDatabase.insert(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
//                null,
//                values);
//        return (insertId >= 0);     // TRUE if succeeded, FALSE if failed
//    }

    // note: low access efficiency, not recommended to use
    public boolean createMeasure(SensorValueItem accDataItem, SensorValueItem gyroDataItem) {
        ContentValues values = new ContentValues();
        // input values, no need for _ID
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, accDataItem.getTimeStamp());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, accDataItem.getXAxisValue());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, accDataItem.getYAxisValue());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, accDataItem.getZAxisValue());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP, gyroDataItem.getTimeStamp());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X, gyroDataItem.getXAxisValue());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y, gyroDataItem.getYAxisValue());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z, gyroDataItem.getZAxisValue());

        long insertId = mDatabase.insert(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
                null,
                values);
        return (insertId >= 0);     // TRUE if succeeded, FALSE if failed
    }

//    public boolean createBatchMeasure(List<SensorValueItem> listAccDataItem) {
//        // speed up the SQLite write by putting all writings in one database transaction
//        // as a result, time consumed by database writing has been dramatically reduced
//        mDatabase.beginTransaction();
//        boolean isSavingSuccessful;
//        try {
//            ContentValues values = new ContentValues();
//            for (SensorValueItem accDataItem : listAccDataItem) {
//                values.clear();
//                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, accDataItem.getTimeStamp());
//                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, accDataItem.getXAxisValue());
//                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, accDataItem.getYAxisValue());
//                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, accDataItem.getZAxisValue());
//                mDatabase.insert(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
//                        null,
//                        values);
//            }
//            mDatabase.setTransactionSuccessful();
//            isSavingSuccessful = true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            isSavingSuccessful = false;
//        } finally {
//            mDatabase.endTransaction();
//        }
//        return isSavingSuccessful;
//    }

    public boolean createBatchMeasure(List<SensorValueItem> listAccDataItem,
                                      List<SensorValueItem> listGyroDataItem) {
        mDatabase.beginTransaction();
        boolean isSavingSuccessful;
        try {
            ContentValues values = new ContentValues();
            int SIZE = Math.min(listAccDataItem.size(), listGyroDataItem.size());
            for (int i = 0; i < SIZE; i++) {
                values.clear();
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, listAccDataItem.get(i).getTimeStamp());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, listAccDataItem.get(i).getXAxisValue());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, listAccDataItem.get(i).getYAxisValue());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, listAccDataItem.get(i).getZAxisValue());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP, listGyroDataItem.get(i).getTimeStamp());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X, listGyroDataItem.get(i).getXAxisValue());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y, listGyroDataItem.get(i).getYAxisValue());
                values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z, listGyroDataItem.get(i).getZAxisValue());
                mDatabase.insert(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
                        null,
                        values);
            }
            mDatabase.setTransactionSuccessful();
            isSavingSuccessful = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            isSavingSuccessful = false;
        } finally {
            mDatabase.endTransaction();
        }
        return isSavingSuccessful;
    }

//    public void deleteMeasure(int idMeasure) {
//        System.out.println("Measure deleted with id: " + idMeasure);
//        // ( table, whereClause, whereArgs )
//        mDatabase.delete(
//                SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
//                SensorMeasureSQLiteOpenHelper.SensorMeasureColumns._ID + " = " + idMeasure,
//                null);  // WHERE args
//    }

//    public List<SensorValueItem> getAllMeasures() {
//        List<SensorValueItem> listAccDataItems = new ArrayList<>();
//
//        // ( table, columns, ... )
//        Cursor cursor = mDatabase.query(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE_NAME,
//                allColumns, null, null, null, null, null);
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            long timestamp = cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP));
//            float x = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X));
//            float y = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y));
//            float z = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z));
//            listAccDataItems.add(new SensorValueItem(timestamp, x, y, z));
//            cursor.moveToNext();
//        }
//        // make sure to close the cursor
//        cursor.close();
//        return listAccDataItems;
//    }
}
