package cn.jamesli.example.at89greendaotest;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import cn.jamesli.example.at89greendaotest.src_gen.DaoMaster;
import cn.jamesli.example.at89greendaotest.src_gen.DaoSession;
import cn.jamesli.example.at89greendaotest.src_gen.Note;
import cn.jamesli.example.at89greendaotest.src_gen.NoteDao;

// A CursorLoader runs an asynchronous query in the background against a ContentProvider
// This allows the Activity or FragmentActivity to continue to interact with the user while the query is ongoing.
public class NoteCursorLoader extends CursorLoader {

    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static NoteDao mNoteDao;

    // Hold a reference to the loader's data
    private Cursor mNoteCursor;

    public NoteCursorLoader(Context context) {
        super(context);
        helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mNoteDao = mDaoSession.getNoteDao();
    }

    @Override
    public Cursor loadInBackground() {
        mNoteCursor = loadCursor();
        if (mNoteCursor != null) {
            // ensure that the content window is filled
            mNoteCursor.getCount();
        }
        return mNoteCursor;
    }

    private Cursor loadCursor() {
        String orderBy = NoteDao.Properties.Text.columnName + " COLLATE LOCALIZED ASC";
        return db.query(mNoteDao.getTablename(), mNoteDao.getAllColumns(), null, null,
                null, null, orderBy);
    }

    public void insert(String noteText) {
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());
        Note note = new Note(null, noteText, comment, new Date());
        mNoteDao.insert(note);
        Log.d("DaoExample", "Inserted new note, ID: " + note.getId());
    }

    public void deleteByKey(long id) {
        mNoteDao.deleteByKey(id);
        Log.d("DaoExample", "Deleted note, ID: " + id);
    }
}
