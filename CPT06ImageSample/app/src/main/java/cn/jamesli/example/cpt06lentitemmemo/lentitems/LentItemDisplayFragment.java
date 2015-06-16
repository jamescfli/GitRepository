package cn.jamesli.example.cpt06lentitemmemo.lentitems;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.jamesli.example.cpt06lentitemmemo.R;
import cn.jamesli.example.cpt06lentitemmemo.provider.LentItemsContract;

public class LentItemDisplayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    interface DisplayCallback extends DetailCallback {
        void addItem();
        void switchToForm(long itemId);
    }

    /**
     * The unique identifier of the loader for entities.
     */
    private static final int ENTITY_LOADER = 40;
    /**
     * The key for storing the two pane mode
     */
    private static final String KEY_ITEM_ID = "keyItemid";
    /**
     * The id of the currently active lent item. -1 if none is active.
     */
    private long mItemId = -1;
    /**
     * The TextView field for the name.
     */
    private TextView mTxtName;
    /**
     * The TextView field for the borrower.
     */
    private TextView mTxtBorrower;
    /**
     * The Uri of the attached photo.
     */
    private Uri mPhotoUri;
    /**
     * The (initially hidden) label for the photo.
     */
    private TextView mTxtPhotoLabel;
    /**
     * The (initially hidden) Imageview for the photo.
     */
    private ImageView mImgPhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItemId = getArguments().getLong(KEY_ITEM_ID, -1);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mItemId != -1) {
            getLoaderManager().initLoader(ENTITY_LOADER, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cpsample_detail_view, container, false);
        mTxtBorrower = (TextView) rootView.findViewById(R.id.txtBorrower);
        mTxtName = (TextView) rootView.findViewById(R.id.txtItemName);
        mTxtPhotoLabel = (TextView) rootView.findViewById(R.id.labelPhoto);
        mImgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_display_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (mItemId != -1) {
                    ((DetailCallback) getActivity()).deleteItem(mItemId);
                }
                break;
            case R.id.menu_edit:
                ((DisplayCallback) getActivity()).switchToForm(mItemId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Uri loaderUri = ContentUris.withAppendedId(LentItemsContract.ItemEntities.CONTENT_URI, mItemId);
        return new CursorLoader(getActivity(),
                loaderUri,
                LentItemsContract.ItemEntities.PROJECTION_ALL,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mTxtName.setText(data.getString(1));
            mTxtBorrower.setText(data.getString(2));
            String uriString = data.getString(3);
            if (!TextUtils.isEmpty(uriString)) {
                mPhotoUri = Uri.parse(uriString);
                ImageHelper.loadImage(mPhotoUri, mImgPhoto, mTxtPhotoLabel);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // n.a.
    }

    public static LentItemDisplayFragment newInstance(long itemId) {
        LentItemDisplayFragment f = new LentItemDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ITEM_ID, itemId);
        f.setArguments(bundle);
        return f;
    }

    /**
     * The dummyApplyBatch method is only used to show a sample batch.
     * It's not used within this sample app.
     */
    @SuppressWarnings("unused")
    private void dummyApplyBatch() {
        String borrower = "John Deo";
        String item = "Anhalter";
        boolean hasPic = true;
        Context ctx  = getActivity();
        String somePath = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(LentItemsContract.Items.CONTENT_URI)
                .withValue(LentItemsContract.Items.NAME, item)
                .withValue(LentItemsContract.Items.BORROWER, borrower)
                .build());
        if (hasPic) {
            ops.add(ContentProviderOperation.newInsert(LentItemsContract.Photos.CONTENT_URI)
                    .withValue(LentItemsContract.Photos._DATA, somePath)
                    .withValueBackReference(LentItemsContract.Photos.ITEMS_ID, 0).
                    build());
        }
        try {
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.applyBatch(LentItemsContract.AUTHORITY, ops);
        } catch (OperationApplicationException e) {
            Log.e("wemonit", "cannot apply batch: " + e.getLocalizedMessage(), e);
        } catch (RemoteException e) {
            Log.e("wemonit", "cannot apply batch: " + e.getLocalizedMessage(), e);
        }

        // EventBus.getDefault().post(whatever);
    }

    /**
     * The dummyUpdate method is only used to show a sample update.
     * It's not used within this sample app.
     */
    @SuppressWarnings("unused")
    private long dummyUpdate() {

        String itemId = "10";
        String selection = LentItemsContract.SELECTION_ID_BASED; // BaseColumns._ID
        // + " = ? "
        String[] selectionArgs = {itemId};
        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(LentItemsContract.Items.BORROWER, "Jane Doe");
        long updateCount =
                resolver.update(LentItemsContract.Items.CONTENT_URI, values, selection, selectionArgs);


        return updateCount;
    }

    /**
     * The dummyQuery method is only used to show a sample query.
     * It's not used within this sample app.
     */
    @SuppressWarnings("unused")
    private void dummyQuery() {

        String itemId = "10";
        String selection = LentItemsContract.SELECTION_ID_BASED; // BaseColumns._ID
        // + " = ? "
        String[] selectionArgs = {itemId};
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor c = resolver.query(
                LentItemsContract.Items.CONTENT_URI,          // die URI
                LentItemsContract.Items.PROJECTION_ALL,       // optionale Angabe der gewünschten Spalten
                selection,                  // optionale WHERE Klausel (ohne Keyword)
                selectionArgs,              // optionale Wildcard Ersetzungen
                LentItemsContract.Items.SORT_ORDER_DEFAULT);  // optionale ORDER BY Klausel (ohne Keyword)

        if (c != null && c.moveToFirst()) {
            // int idx = c.getColumnIndex(Items.NAME);
            String name = c.getString(1);
            String borrower = c.getString(2);
        }

    }
}
