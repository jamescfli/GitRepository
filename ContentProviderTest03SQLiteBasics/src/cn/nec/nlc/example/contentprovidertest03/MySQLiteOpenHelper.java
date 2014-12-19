package cn.nec.nlc.example.contentprovidertest03;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	// table name
	public static final String TABLE_COMMENTS = "comments";
	// _id column
	public static final String COLUMN_ID = "_id";
	// content column
	public static final String COLUMN_COMMENT = "comment";

	private static final String DATABASE_NAME = "commments.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_COMMENTS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_COMMENT
			+ " text not null);";
	// .. full command:
	//	CREATE TABLE table_name (
	//		column1 datatype,
	//		column2 datatype,
	//		column3 datatype,
	//		...
	//		columnN datatype,
	//		PRIMARY KEY ( one or more columns )
	//	);
	
	
	// constructor
	public MySQLiteOpenHelper(Context context) {
		// ( context, db_name, cursorFactory, version )
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
		        + newVersion + ", which will destroy all old data");
		// delete old database, DROP TABLE database_name.table_name
		// where database_name is already given by db
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		// replace with the new one by calling onCreate() again
		onCreate(db);
	}

}
