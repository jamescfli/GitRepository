package cn.jamesli.example.bt03easycursortest;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import uk.co.alt236.easycursor.EasyCursor;
import uk.co.alt236.easycursor.sqlcursor.EasySqlCursor;

/**
 * Created by jamesli on 15-8-12.
 */
public class CommonTestCases extends AndroidTestCase {
    protected void testBooleanFieldParsing(EasyCursor cursor) {
        // Real data
        final String NON_EXISTANT_COL = "THIS_COLUMN_DOES_NOT_EXIST";
        final String EXISTANT_COL = "hascomposer";

        Assert.assertTrue(cursor.getBoolean(EXISTANT_COL));
        Assert.assertTrue(cursor.optBooleanAsWrapperType(EXISTANT_COL));
        Assert.assertEquals(cursor.optBoolean(NON_EXISTANT_COL), EasySqlCursor.DEFAULT_BOOLEAN);
        Assert.assertTrue(cursor.optBoolean(NON_EXISTANT_COL, true));
        Assert.assertNull(cursor.optBooleanAsWrapperType(NON_EXISTANT_COL));
    }
}
