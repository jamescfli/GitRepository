package cn.nec.nlc.example.contentprovidertest05.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import cn.nec.nlc.example.contentprovidertest05.database.TodoDatabaseHelper;
import cn.nec.nlc.example.contentprovidertest05.database.TodoTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyTodoContentProvider extends ContentProvider {
	// database
	private TodoDatabaseHelper database;
	
	// used for the UriMacher, define uriType
	private static final int TODOS = 10;
	private static final int TODO_ID = 20;

	private static final String AUTHORITY = 
			"cn.nec.nlc.example.contentprovidertest05.contentprovider";

	private static final String BASE_PATH = "contentprovidertest05";
	
	// for public access, create a Uri which parses the given encoded URI string.
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	// ContentResolver.CURSOR_DIR_BASE_TYPE:
	// This is the Android platform's base MIME type for a content: URI 
	// containing a Cursor of zero or more items.
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/todos";
	// ContentResolver.CURSOR_ITEM_BASE_TYPE
	// This is the Android platform's base MIME type for a content: URI 
	// containing a Cursor of a single item.
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/todo";
	
	// Utility class to aid in matching URIs in Content Provider
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		// .addURI(String authority, String path, int code)
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
	}
	
	@Override
	public boolean onCreate() {
		database = new TodoDatabaseHelper(getContext());
		return false;	// true if provider was successfully loaded, false o.w.
	}
	
	// A request to delete one or more rows
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);	// TODO_ID = 20, return -1 if no match
		// database was initiated in onCreate()
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case TODOS:
	    	// .delete(String table, String whereClause, String[] whereArgs)
	    	rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO, selection,
	    			selectionArgs);
	    	break;
	    case TODO_ID:
	    	// return the decoded last segment or null if the path is empty
	    	String id = uri.getLastPathSegment(); // .../<id>
	    	// TextUtils.isEmpty(): return true if the string is null or 0-length
	    	if (TextUtils.isEmpty(selection)) {
	    		rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO,
	    				TodoTable.COLUMN_ID + "=" + id, 
	    				null);
	    	} else {
	    		rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO,
	    				TodoTable.COLUMN_ID + "=" + id 
	    				+ " and " + selection,
	    				selectionArgs);
	    	}
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    // Notify registered observers that a row was updated, e.g. listView
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case TODOS:
	    	id = sqlDB.insert(TodoTable.TABLE_TODO, null, values);
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
		      String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // check if the caller has requested a column which does not exists
	    checkColumns(projection);	
	    // .. throw exception if not existing, projection = [_id, summary]

	    // Set the table
	    queryBuilder.setTables(TodoTable.TABLE_TODO); 
	    // .. mTable = "todo" table_name
	    
	    // uri = content://cn.nec.nlc.example.contentprovidertest05
	    // .contentprovider/contentprovidertest05
	    int uriType = sURIMatcher.match(uri);  // uriType = 10
	    switch (uriType) {
	    case TODOS:	// = 10
	    	break;
	    case TODO_ID:
	    	// adding the ID to the original query
	    	queryBuilder.appendWhere(TodoTable.COLUMN_ID + "="
	    			+ uri.getLastPathSegment());
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    // database path: /data/data/cn.nec.nlc.example.contentprovidertest05
	    // /databases/todotable.db
	    SQLiteDatabase db = database.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    // .. update adapter with new inserted reminder
	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
		      String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case TODOS:
	    	rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, 	// table_name
	    			values, 									// content values
	    			selection,									// whereClause
	    			selectionArgs);								// selectionArgs
	    	break;
	    case TODO_ID:
	    	String id = uri.getLastPathSegment();
	    	if (TextUtils.isEmpty(selection)) {
	    		rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, 
	    				values,
	    				TodoTable.COLUMN_ID + "=" + id, 
	    				null);
	    	} else {
	    		rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, 
	    				values,
	    				TodoTable.COLUMN_ID + "=" + id 
	    				+ " and " 
	    				+ selection,
	    				selectionArgs);
	    	}
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
	    String[] available = { TodoTable.COLUMN_CATEGORY,
	    		TodoTable.COLUMN_SUMMARY, TodoTable.COLUMN_DESCRIPTION,
	    		TodoTable.COLUMN_ID };
	    if (projection != null) {
	    	HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	    	HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	    	// check if all columns which are requested are available
	    	if (!availableColumns.containsAll(requestedColumns)) {
	    		throw new IllegalArgumentException("Unknown columns in projection");
	      }
	    }
	}

}
