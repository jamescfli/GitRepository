package cn.jamesli.example.at89greendaotest;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.jamesli.example.at89greendaotest.src_gen.DaoMaster;
import cn.jamesli.example.at89greendaotest.src_gen.DaoSession;
import cn.jamesli.example.at89greendaotest.src_gen.NoteDao;

public class NoteCursorLoader extends AsyncTaskLoader<Cursor> {

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;

    // Hold a reference to the loader's data
    private Cursor mNoteCursor;

    private String orderBy = NoteDao.Properties.Text.columnName + " COLLATE LOCALIZED ASC";

    public NoteCursorLoader(Context context, SQLiteDatabase db, DaoMaster.DevOpenHelper helper,
                            DaoMaster daoMaster, DaoSession daoSession, NoteDao noteDao) {
        super(context);
        this.db = db;
        this.helper = helper;
        this.daoMaster = daoMaster;
        this.daoSession = daoSession;
        this.noteDao = noteDao;
    }

    @Override
    public Cursor loadInBackground() {  // from 1.a
        mNoteCursor = loadCursor();
        if (mNoteCursor != null) {
            // ensure that the content window is filled
            mNoteCursor.getCount();
        }
        return mNoteCursor;
    }

    private Cursor loadCursor() {
        return db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null,
                null, null, orderBy);
    }

    @Override
    public void deliverResult(Cursor data) {    // 2.
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        // Hold reference to the old data so that it doesn't get garbage collected
        // We must protect it until the new data has been delivered
        Cursor oldCursor = mNoteCursor;
        mNoteCursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        // Invalidate old data since we don't need it anymore
        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            releaseResources(oldCursor);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mNoteCursor != null) {
            // Delivering previously loaded data to the client
            deliverResult(mNoteCursor);
        }

        // takeContentChanged() indicating whether the loader's content had changed while it was stopped
        if (takeContentChanged() || mNoteCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader has been put in a stopped state, so we should attempt to
        // cancel the current load (if there is one).
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure loader is stopped
        onStopLoading();

        // Release resources associated
        if (mNoteCursor != null && !mNoteCursor.isClosed()) {
            releaseResources(mNoteCursor);
        }
        mNoteCursor = null;

        // stop observers, monitoring for changes, if there is any
    }

    @Override
    public void onCanceled(Cursor data) {
        super.onCanceled(mNoteCursor);
        if (mNoteCursor != null && !mNoteCursor.isClosed()) {
            releaseResources(mNoteCursor);
        }
    }

    private void releaseResources(Cursor cursor) {
        // For a simple List, nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
        cursor.close();
    }
}
