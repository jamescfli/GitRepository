package cn.jamesli.example.cpt06lentitemmemo.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract between clients and the lentitems content provider.
 *
 * @author Wolfram Rittmeyer
 */
public final class LentItemsContract {

    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY = "cn.jamesli.example.cpt06lentitemmemo.lentitems";
    /**
     * The content URI for the top-level lentitems authority.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    /**
     * A selection clause for ID based queries.
     */
    public static final String SELECTION_ID_BASED = BaseColumns._ID + " = ? ";

    /**
     * Constants for the Items table of the lentitems provider.
     */
    public static final class Items implements CommonColumns {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =  Uri.withAppendedPath(LentItemsContract.CONTENT_URI, "items");
        /**
         * The mime type of a directory of items. TODO to verify the names
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.openminds.lentitems_items";
        /**
         * The mime type of a single item. TODO to verify the names
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.openminds.lentitems_items";
        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = {_ID, NAME, BORROWER};
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
    }

    /**
     * Constants for the Photos table of the lentitems provider.
     * For each item there is exactly one photo. You can
     * safely call insert with the an already existing ITEMS_ID. You
     * won't get constraint violations. The content provider
     * takes care of this. Take care: The _ID of the new record in this
     * case differs from the _ID of the old record.
     */
    public static final class Photos implements BaseColumns {
        /**
         * The Content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(LentItemsContract.CONTENT_URI, "photos");
        /**
         * The mime type of a directory of photos.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/lentitems_photos";
        /**
         * The mime type of a single photo.
         */
        public static final String CONTENT_PHOTO_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/lentitems_photos";
        /**
         * The data column of this photo.
         */
        public static final String _DATA = "_data";
        /**
         * The id of the item this photo belongs to.
         */
        public static final String ITEMS_ID = "items_id";
        /**
         * A projection of all columns in the photos table.
         */
        public static final String[] PROJECTION_ALL = {_ID, _DATA, ITEMS_ID};
    }

    /**
     * Constants for a joined view of Items and Photos. The _id of this
     * joined view is the _id of the Items table.
     */
    public static final class ItemEntities implements CommonColumns {
        /**
         * The Content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(LentItemsContract.CONTENT_URI, "entities");
        /**
         * The mime type of a directory of joined entities.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/lentitems_entities";
        /**
         * The mime type of a single joined entity.
         */
        public static final String CONTENT_ENTITY_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/lentitems_entities";
        /**
         * The data column of this joined entity.
         */
        public static final String _DATA = "_data";
        /**
         * A projection of all columns in the photos table.
         */
        public static final String[] PROJECTION_ALL = {DbSchema.TBL_ITEMS + "." +_ID, NAME, BORROWER, _DATA};
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
    }

    /**
     * This interface defines common columns found in multiple tables.
     */
    public static interface CommonColumns extends BaseColumns {
        /**
         * The name of the item.
         */
        public static final String NAME = "item_name";
        /**
         * The borrower of the item.
         */
        public static final String BORROWER = "borrower";
    }
}
