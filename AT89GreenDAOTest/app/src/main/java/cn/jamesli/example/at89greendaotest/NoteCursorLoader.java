package cn.jamesli.example.at89greendaotest;

import android.content.Context;
import android.content.CursorLoader;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import cn.jamesli.example.at89greendaotest.src_gen.DaoMaster;
import cn.jamesli.example.at89greendaotest.src_gen.DaoSession;
import cn.jamesli.example.at89greendaotest.src_gen.Note;
import cn.jamesli.example.at89greendaotest.src_gen.NoteDao;

// TODO unfinished
public class NoteCursorLoader extends CursorLoader {
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static NoteDao mNoteDao;

    public NoteCursorLoader(Context context) {
        super(context);
        helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mNoteDao = mDaoSession.getNoteDao();
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
