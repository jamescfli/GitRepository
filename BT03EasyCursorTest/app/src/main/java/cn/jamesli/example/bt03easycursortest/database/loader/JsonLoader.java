package cn.jamesli.example.bt03easycursortest.database.loader;

import android.content.Context;
import android.database.Cursor;

// requires cwac-loaderex in libs
import com.commonsware.cwac.loaderex.acl.AbstractCursorLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import uk.co.alt236.easycursor.EasyCursor;
import uk.co.alt236.easycursor.jsoncursor.EasyJsonCursor;

/**
 * Created by jamesli on 15-8-12.
 */
public class JsonLoader extends AbstractCursorLoader {
    private static final String UTF_8 = "UTF-8";
    private static final String DATA_SAMPLE_JSON_JSON = "data/sample_json.json";

    @Override
    protected Cursor buildCursor() {
        EasyCursor cursor;
        try {
            // Wrap a JSONArray into a cursor
            // Json data in sample_json.json do not have an "_id" field, so we will alias "_id" as "id"
            cursor = new EasyJsonCursor(new JSONArray(loadAssetsFileAsString(DATA_SAMPLE_JSON_JSON)), "id");
            // .. _idAlias = "id"
        } catch (final JSONException e) {
            e.printStackTrace();
            cursor = null;
        }
        return cursor;
    }

    public JsonLoader(final Context context) {
        super(context);
    }

    private String loadAssetsFileAsString(final String path) {
        final String json;
        try {
            final InputStream is = getContext().getAssets().open(path);
            // Returns an estimated no. of bytes can be read or skipped without blocking for more input
            final int size = is.available();
            final byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, UTF_8);
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
