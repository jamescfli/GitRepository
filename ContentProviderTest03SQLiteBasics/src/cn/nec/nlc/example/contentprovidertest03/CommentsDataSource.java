package cn.nec.nlc.example.contentprovidertest03;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// This class is our DAO - Data Access Object
// It maintains the database connection and supports adding new comments
// and fetching all comments.
public class CommentsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteOpenHelper dbHelper;	// wrapper
	private String[] allColumns = { MySQLiteOpenHelper.COLUMN_ID,
			MySQLiteOpenHelper.COLUMN_COMMENT };

	public CommentsDataSource(Context context) {
		dbHelper = new MySQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// public for database operation
	public Comment createComment(String comment) {
		// Class is used to store a set of values that ContentResolver can process
		ContentValues values = new ContentValues();
		// key + String value from { "Love it", "Cool", "Very nice", "Good", "Fair", "Hate it" }
		values.put(MySQLiteOpenHelper.COLUMN_COMMENT, comment); // comment (key) = content (value) 
		// insert ( table, nullColumnHack, contentValues )
		long insertId = database.insert(
				MySQLiteOpenHelper.TABLE_COMMENTS, 	// table_name
				null,								// nullColumnHack, SQL does not allow completely empty row, assign by NULL value
				values);							// map contains the initial column values for the row
		// then read it back from database
		Cursor cursor = database.query(				// return a Cursor object positioned before the first entry
				MySQLiteOpenHelper.TABLE_COMMENTS,	// table_name
				allColumns, 						// a list of columns to return
				MySQLiteOpenHelper.COLUMN_ID + " = " + insertId, 	// a filter declaring which rows to return, SQL WHERE format
				null,								// selectionArgs
				null, 								// groupBy, how to group rows
				null, 								// having, which row groups to include in the cursor
				null);								// orderBy, how to order the rows
		cursor.moveToFirst();	// get the first + latest one, cursor.mCount = 1, mCurrentRowID = insertId
		Comment newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;	// pass to the adapter 
	}

	// public for database operation
	public void deleteComment(Comment comment) {
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		// ( table, whereClause, whereArgs )
		database.delete(							// return int, the number of rows affected
				MySQLiteOpenHelper.TABLE_COMMENTS, 	// table_name
				MySQLiteOpenHelper.COLUMN_ID + " = " + id, 	// WHERE clause to apply, null will delete all rows
				null);								// whereArgs
	}

	public List<Comment> getAllComments() {
		List<Comment> comments = new ArrayList<Comment>();

		// ( table, columns, ... )
		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_COMMENTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Comment comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Comment cursorToComment(Cursor cursor) {
		Comment comment = new Comment();
		// getLong(column number): returns value of requested column as long
		comment.setId(cursor.getLong(0));
		comment.setComment(cursor.getString(1));
		return comment;
	}
}
