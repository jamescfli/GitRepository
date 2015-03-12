package cn.nec.nlc.example.jamesli.activitytest54prefheader;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference
        implements SeekBar.OnSeekBarChangeListener {
    // Namespaces to read attributes
    private static final String PREFERENCE_NS = "cn.nec.nlc.example.jamesli.activitytest54prefheader";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Attribute names
    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";

    private static final int DEFAULT_MIN_VALUE = 10;
    private static final int DEFAULT_MAX_VALUE = 20;
    public static final int DEFAULT_CURRENT_VALUE = 15;

    private int mMinValue;
    private int mMaxValue;
    private int mDefaultValue;
    private int mCurrentValue;
    private SeekBar mSeekBar;
    private TextView mValueText;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // read parameters from attributes
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {
        // get current value from preference
        mCurrentValue = getPersistedInt(mDefaultValue);
        // inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_slider, null);
        // set up min and max text label
        ((TextView) view.findViewById(R.id.min_value)).setText(Integer.toString(mMinValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(Integer.toString(mMaxValue));
        // set up seek bar
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        // set up text label for current value
        mValueText = (TextView) view.findViewById(R.id.current_value);
        mValueText.setText(Integer.toString(mCurrentValue));
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        // return if change was cancelled
        if (!positiveResult) {
            return;
        }
        // persist current value if needed
        if (shouldPersist()) {
            persistInt(mCurrentValue);
        }
        // notify activities about changes, e.g. to update preference summary line
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        // format summary string with current value
        String summary = super.getSummary().toString();
        int value = getPersistedInt(mDefaultValue);
        return String.format(summary, value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean fromTouch) {
        // update current value
        mCurrentValue = mMinValue + value;
        // update lable with current value
        mValueText.setText(Integer.toString(mCurrentValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // n.a.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // n.a.
    }
}
