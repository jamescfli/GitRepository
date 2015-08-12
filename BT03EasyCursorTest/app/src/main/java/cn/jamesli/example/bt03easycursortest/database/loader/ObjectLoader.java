package cn.jamesli.example.bt03easycursortest.database.loader;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import com.commonsware.cwac.loaderex.acl.AbstractCursorLoader;
// import com.commonsware.cwac.loaderex.AbstractCursorLoader;   // fails
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.bt03easycursortest.container.JsonDataGsonModel;
import uk.co.alt236.easycursor.EasyCursor;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

/**
 * Created by jamesli on 15-8-12.
 */
public class ObjectLoader extends AbstractCursorLoader {
    private static final String UTF_8 = "UTF-8";
    private static final String DATA_SAMPLE_JSON_JSON = "data/sample_json.json";

    public ObjectLoader(final Context context) {
        super(context);
    }

    @Override
    protected Cursor buildCursor() {
        EasyCursor cursor;
        try {
            final Gson gson = new Gson();
            final InputStream is = getContext().getAssets().open(DATA_SAMPLE_JSON_JSON);    // read json file
            // Json -- Gson --> Object List
            final List<JsonDataGsonModel> data = gson.fromJson(new InputStreamReader(is),
                    new TypeToken<ArrayList<JsonDataGsonModel>>() {}.getType());
            // List --> Cursor
            cursor = new EasyObjectCursor<>(JsonDataGsonModel.class, data, "id");
            is.close();
        } catch (final IOException e) {
            e.printStackTrace();
            cursor = null;
        }
        return cursor;
    }

    public String loadAssetsFileAsString(final String path) {
        final String json;
        try {
            final InputStream is = getContext().getAssets().open(path);
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
