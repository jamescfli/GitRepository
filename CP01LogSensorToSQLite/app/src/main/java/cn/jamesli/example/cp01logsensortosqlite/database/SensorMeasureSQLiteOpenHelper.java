package cn.jamesli.example.cp01logsensortosqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jamesli on 15/12/5.
 */
public class SensorMeasureSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "SensorMeasureOpenHelper";

    public static final String  MEASURES_TABLE = "measurements";
    // _id column
    public static final String MEASURE_ID = "_id";
    // content column
    public static final String COLUMN_ACC_TIMESTAMP = "acctimestamp";
    public static final String COLUMN_ACC_X = "accX";
    public static final String COLUMN_ACC_Y = "accY";
    public static final String COLUMN_ACC_Z = "accZ";
    public static final String COLUMN_GYRO_TIMESTAMP = "gyrotimestamp";
    public static final String COLUMN_GYRO_X = "gyroX";
    public static final String COLUMN_GYRO_Y = "gyroY";
    public static final String COLUMN_GYRO_Z = "gyroZ";


    private static final int DATABASE_VERSION = 1;

    private static final String MEASURES_TABLE_CREATE = "create table "
            + MEASURES_TABLE
            + " (" +
            MEASURE_ID + "integer primary key autoincrement, " +
            COLUMN_ACC_TIMESTAMP + " INTEGER not null, " +
            COLUMN_ACC_X + " REAL not null, " +
            COLUMN_ACC_Y + " REAL not null, " +
            COLUMN_ACC_Z + " REAL not null, " +
            COLUMN_GYRO_TIMESTAMP + " INTEGER not null, " +
            COLUMN_GYRO_X + " REAL not null, " +
            COLUMN_GYRO_Y + " REAL not null, " +
            COLUMN_GYRO_Z + " REAL not null);";

    private SQLiteDatabase database;
    private File mDatabaseFilePath;
    private String mDatabaseFileName;

    public SensorMeasureSQLiteOpenHelper(Context context, String name,
                                         SQLiteDatabase.CursorFactory factory) {
        // name: of the database file, or null for an in-memory database
        super(context, name, factory, DATABASE_VERSION);
        mDatabaseFilePath = context.getExternalCacheDir();
        mDatabaseFileName = name;
        try {
//            database = SQLiteDatabase.openDatabase(mDatabaseFilePath
//                    + File.separator + mDatabaseFileName, null, SQLiteDatabase.OPEN_READWRITE);
            database = SQLiteDatabase.openDatabase(mDatabaseFilePath
                    + File.separator + mDatabaseFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        }
        catch (SQLiteException ex) {
            Log.e(TAG, "error -- " + ex.getMessage(), ex);
            // error means tables does not exits
            createTables();
        }
        finally {
            close();
        }
    }

    private void createTables() {
        database.execSQL(MEASURES_TABLE_CREATE);
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    public SQLiteDatabase getReadableDatabase()
    {
        database = SQLiteDatabase.openDatabase(mDatabaseFilePath
                        + File.separator + mDatabaseFileName, null,
                SQLiteDatabase.OPEN_READONLY);
        return database;
    }

    public SQLiteDatabase getWritableDatabase()
    {
        database = SQLiteDatabase.openDatabase(mDatabaseFilePath
                        + File.separator + mDatabaseFileName, null,
                SQLiteDatabase.OPEN_READWRITE);
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
