package cn.jamesli.example.cp01logsensortosqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jamesli.example.cp01logsensortosqlite.sensor.AccSensorDataItem;
import cn.jamesli.example.cp01logsensortosqlite.sensor.GyroSensorDataItem;

/**
 * Created by jamesli on 15/12/6.
 */
public class SensorMeasureSavor {

    private static final String TAG = "SensorMeasureSavor";

    // Database fields
    private SQLiteDatabase database;
    private static final String DATABASE_FILENAME_SUFFIX = "measure_db_";
    private static final String DATABASE_FILENAME_APPENDIX = ".db";
    private SensorMeasureSQLiteOpenHelper dbHelper;	// wrapper
    private static final String[] accColumns = {
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z
    };
    private static final String[] gyroColumns = {
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y,
            SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z
    };


    public SensorMeasureSavor(Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        String databaseFileName = DATABASE_FILENAME_SUFFIX
                + dateFormat.format(new Date().getTime()) + DATABASE_FILENAME_APPENDIX;
        dbHelper = new SensorMeasureSQLiteOpenHelper(context, databaseFileName);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // public for database operation
    public boolean createMeasure(int measureIndex, AccSensorDataItem accDataItem, GyroSensorDataItem gyroDataItem) {
        // Class is used to store a set of values that ContentResolver can process
        ContentValues values = new ContentValues();
        // note: do not put index _id into values, content only
//        values.put(SensorMeasureSQLiteOpenHelper.MEASURE_ID, measureIndex);
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP, accDataItem.getAccTimeStamp());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X, accDataItem.getAccXAxisMeasure());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y, accDataItem.getAccYAxisMeasure());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z, accDataItem.getAccZAxisMeasure());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP, gyroDataItem.getGyroTimeStamp());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X, gyroDataItem.getGyroXAxisMeasure());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y, gyroDataItem.getGyroYAxisMeasure());
        values.put(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z, gyroDataItem.getGyroZAxisMeasure());
        // insert ( table, nullColumnHack, contentValues )
//        try {
//            long insertId = database.insertOrThrow(
//                    SensorMeasureSQLiteOpenHelper.MEASURES_TABLE,    // table_name
//                    null,                                // nullColumnHack, SQL does not allow completely empty row, assign by NULL value
//                    values);                            // map contains the initial column values for the row
//            // .. row ID of the newly inserted row, or -1 if an error occurred
//            // .. use insertOrThrow and catch SQLException for debug
//            return (insertId >= 0);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }

        long insertId = database.insert(
                SensorMeasureSQLiteOpenHelper.MEASURES_TABLE,    // table_name
                null,
                values);
        return (insertId >= 0);
    }

    // public for database operation
    public void deleteMeasure(int idMeasure) {
        System.out.println("Measure deleted with id: " + idMeasure);
        // ( table, whereClause, whereArgs )
        database.delete(
                SensorMeasureSQLiteOpenHelper.MEASURES_TABLE,
                SensorMeasureSQLiteOpenHelper.SensorMeasureColumns._ID + " = " + idMeasure,
                null);  // WHERE args
    }

    public List<AccSensorDataItem> getAllAccMeasures() {
        List<AccSensorDataItem> listAccDataItems = new ArrayList<>();

        // ( table, columns, ... )
        Cursor cursor = database.query(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE,
                accColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP));
            float x = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X));
            float y = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y));
            float z = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z));
            listAccDataItems.add(new AccSensorDataItem(timestamp, x, y, z));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listAccDataItems;
    }

    public List<GyroSensorDataItem> getAllGyroMeasures() {
        List<GyroSensorDataItem> listGyroDataItems = new ArrayList<>();

        // ( table, columns, ... )
        Cursor cursor = database.query(SensorMeasureSQLiteOpenHelper.MEASURES_TABLE,
                gyroColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP));
            float x = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X));
            float y = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y));
            float z = cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z));
            listGyroDataItems.add(new GyroSensorDataItem(timestamp, x, y, z));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listGyroDataItems;
    }

    private boolean isCursorHasValue(Cursor cursor) {
        if (cursor == null) {
            return false;
        } else {
            Log.i(TAG, "Saved measure is "
                    + cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_TIMESTAMP)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_X)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Y)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_ACC_Z)) + " "
                    + cursor.getLong(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_X)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Y)) + " "
                    + cursor.getFloat(cursor.getColumnIndex(SensorMeasureSQLiteOpenHelper.SensorMeasureColumns.COLUMN_GYRO_Z))
            );
            return true;
        }
    }
}
