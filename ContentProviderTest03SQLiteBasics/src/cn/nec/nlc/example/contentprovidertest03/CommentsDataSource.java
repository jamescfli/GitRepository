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
		values.put(MySQLiteOpenHelper.COLUMN_COMMENT, comment);
		// insert ( table, nullColumnHack, contentValues )
		long insertId = database.insert(MySQLiteOpenHelper.TABLE_COMMENTS, null,
				values);
		// then read it back from database
		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_COMMENTS,
				allColumns, MySQLiteOpenHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Comment newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;	// pass to the adapter 
	}

	// public for database operation
	public void deleteComment(Comment comment) {
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		// ( table, whereClause, whereArgs )
		database.delete(MySQLiteOpenHelper.TABLE_COMMENTS, 
				MySQLiteOpenHelper.COLUMN_ID + " = " + id, null);
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
