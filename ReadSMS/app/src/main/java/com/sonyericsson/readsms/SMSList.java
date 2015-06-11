package com.sonyericsson.readsms;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
//import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
//import android.widget.TextView;

//import java.util.Date;

/**
 * A very simple ListActivity that just prints the messages in the SMS inbox.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SMSList extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "SMSList";

    /**
     * Copied from Telephony.java in frameworks/base
     * comment template: content://authority/optionalPath/optionalId
     */
    public static final Uri SMS_INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");

//    /**
//     * Copied from Telephony.java in frameworks/base
//     */
//    public class TextSmsColumns {
//        public static final String ID = "_id";
//        public static final String ADDRESS = "address";
//        public static final String DATE = "date";
//        public static final String BODY = "body";
//    }

    private static final String[] PROJECTION = new String[] { "_id", "address", "date", "body" };

    // The loader's unique id. Loader ids are specific to the Activity or
    // Fragment in which they reside.
    private static final int LOADER_ID = 1;

    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    // The adapter that binds our data to the ListView
    private SimpleCursorAdapter mAdapter;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(SMSList.this, SMS_INBOX_CONTENT_URI, PROJECTION, null, null,
                "date DESC");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(data);
                break;
        }
        // The listview now displays the queried data
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // For whatever reason, the Loader's data is now unavailable.
        // Remove any references to the old data by replacing it with
        // a null Cursor.
        mAdapter.swapCursor(null);
    }

    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // method deprecated in API level 11, use CursorLoader instead
//        // managedQuery (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
//        Cursor cursor = managedQuery(SMS_INBOX_CONTENT_URI, new String[]{TextSmsColumns.ID, TextSmsColumns.ADDRESS,
//                TextSmsColumns.DATE, TextSmsColumns.BODY}, null, null, TextSmsColumns.DATE + " DESC"); // descending order
        String[] smsColumns = { "address", "date", "body" };
        int[] textViewIDs = {R.id.sms_origin, R.id.sms_date, R.id.sms_body };

        mAdapter = new SimpleCursorAdapter(this, R.layout.text_sms_item, null,
                smsColumns, textViewIDs, 0);

//        SmsCursorAdapter adapter = new SmsCursorAdapter(this, cursor, true);
        setListAdapter(mAdapter);

        mCallbacks = this;

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

//    private class SmsCursorAdapter extends CursorAdapter {
//
//        public SmsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
//            super(context, c, autoRequery);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//            return View.inflate(context, R.layout.text_sms_item, null);
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            ((TextView) view.findViewById(R.id.sms_origin)).setText(cursor.getString(cursor.getColumnIndexOrThrow(TextSmsColumns.ADDRESS)));
//            ((TextView) view.findViewById(R.id.sms_body)).setText(cursor.getString(cursor.getColumnIndexOrThrow(TextSmsColumns.BODY)));
//            ((TextView) view.findViewById(R.id.sms_date)).setText(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TextSmsColumns.DATE))).toLocaleString());
//        }
//    }
}
