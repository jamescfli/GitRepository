package cn.jamesli.example.cp01logsensortosqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;

/**
 * Created by jamesli on 15/12/5.
 */
public class SensorMeasureSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "SensorMeasureOpenHelper";

    public static final String  MEASURES_TABLE = "measurements";
//    // _id column
//    public static final String MEASURE_ID = "_id";
    // .. You can create and define tables as you wish, but if you will have to use the table via
    // a CursorAdapter that won't work without a tweak.
    // @see <a href="http://developer.android.com/reference/android/widget/CursorAdapter.html>CursorAdapter</a>:
    // The Cursor must include a column named "_id" or this class will not work.
    // Additionally, using MergeCursor with this class will not work if the merged Cursors have
    // overlapping values in their "_id" columns.

    // content column
    public interface SensorMeasureColumns extends BaseColumns {
        // default _ID column : the unique ID for a row
        public static final String COLUMN_ACC_TIMESTAMP = "acctimestamp";
        public static final String COLUMN_ACC_X = "accX";
        public static final String COLUMN_ACC_Y = "accY";
        public static final String COLUMN_ACC_Z = "accZ";
        public static final String COLUMN_GYRO_TIMESTAMP = "gyrotimestamp";
        public static final String COLUMN_GYRO_X = "gyroX";
        public static final String COLUMN_GYRO_Y = "gyroY";
        public static final String COLUMN_GYRO_Z = "gyroZ";
    }

    private static final int DATABASE_VERSION = 1;

    private static final String MEASURES_TABLE_CREATE = "create table "
            + MEASURES_TABLE
            + " (" +
            SensorMeasureColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SensorMeasureColumns.COLUMN_ACC_TIMESTAMP + " INTEGER not null, " +
            SensorMeasureColumns.COLUMN_ACC_X + " REAL not null, " +
            SensorMeasureColumns.COLUMN_ACC_Y + " REAL not null, " +
            SensorMeasureColumns.COLUMN_ACC_Z + " REAL not null, " +
            SensorMeasureColumns.COLUMN_GYRO_TIMESTAMP + " INTEGER not null, " +
            SensorMeasureColumns.COLUMN_GYRO_X + " REAL not null, " +
            SensorMeasureColumns.COLUMN_GYRO_Y + " REAL not null, " +
            SensorMeasureColumns.COLUMN_GYRO_Z + " REAL not null);";

    private SQLiteDatabase database;

    public SensorMeasureSQLiteOpenHelper(Context context, String name) {
        // name: of the database file, or null for an in-memory database
        super(context, context.getExternalCacheDir() + File.separator + name, null, DATABASE_VERSION);
        // .. put the database in external cache storage for further adb pull
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create SQL : " + MEASURES_TABLE_CREATE);
        db.execSQL(MEASURES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SensorMeasureSQLiteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // delete old database, DROP TABLE database_name.table_name
        // where database_name is already given by db
        db.execSQL("DROP TABLE IF EXISTS " + MEASURES_TABLE);
        // replace with the new one by calling onCreate() again
        onCreate(db);
    }
}
