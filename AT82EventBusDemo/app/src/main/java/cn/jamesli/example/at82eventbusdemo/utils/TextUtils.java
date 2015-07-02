package cn.jamesli.example.at82eventbusdemo.utils;

import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jamesli on 15-7-2.
 */
public class TextUtils {
    private TextUtils() { /* cannot be instantiated */ }

    public static CharSequence getHintIfTextIsNull(EditText editText) {
        CharSequence text;
        if (null == editText) {
            return null;
        }
        return (StringUtils.isEmpty(text = editText.getEditableText().toString())) ? editText.getHint() : text;
    }
}
