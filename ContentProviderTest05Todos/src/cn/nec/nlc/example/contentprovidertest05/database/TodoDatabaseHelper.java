package cn.nec.nlc.example.contentprovidertest05.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


// database for TodoTable class, it may contain more than one table
public class TodoDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "todotable.db";
	private static final int DATABASE_VERSION = 1;

	public TodoDatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		TodoTable.onCreate(database);
		// more tables can be created here
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		TodoTable.onUpgrade(database, oldVersion, newVersion);
		// more tables can be upgraded here
	}

}
