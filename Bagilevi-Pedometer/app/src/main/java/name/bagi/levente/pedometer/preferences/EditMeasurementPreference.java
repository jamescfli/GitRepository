package name.bagi.levente.pedometer.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * An {@link EditTextPreference} that is suitable for entering measurements.
 * It handles metric/imperial setting.
 * @author Levente Bagi
 */
abstract public class EditMeasurementPreference extends EditTextPreference {
	private boolean mIsMetric;  // unit in metric, friendly to private
	
	protected int mTitleResource;
	protected int mMetricUnitsResource;
	protected int mImperialUnitsResource;
	
	public EditMeasurementPreference(Context context) {
		super(context);
		initPreferenceDetails();
	}
	public EditMeasurementPreference(Context context, AttributeSet attr) {
		super(context, attr);
		initPreferenceDetails();
	}
	public EditMeasurementPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		initPreferenceDetails();
	}
	
	abstract protected void initPreferenceDetails();

    @Override
    protected void showDialog(Bundle state) {
        mIsMetric = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("units", "imperial").equals("metric");
        setDialogTitle(
                getContext().getString(mTitleResource) +
                        " (" +
                        getContext().getString(
                                mIsMetric
                                        ? mMetricUnitsResource
                                        : mImperialUnitsResource) +
                        ")"
        );

        try {
            Float.valueOf(getText());
        }
        catch (Exception e) {
            setText(PedometerSettings.STEPSIZE_DEFAULT);
        }
        super.showDialog(state);
    }

    // Adds the EditText widget of this preference to the dialog's view
    @Override
    protected void onAddEditTextToDialogView (View dialogView, EditText editText) {
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
          // InputType.TYPE_CLASS_NUMBER: class for numeric text
          // InputType.TYPE_NUMBER_FLAG_DECIMAL: flag of TYPE_CLASS_NUMBER: the number is decimal,
          // allowing a decimal point to provide fractional values.
        super.onAddEditTextToDialogView(dialogView, editText);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        // positiveResult	Whether the positive button was clicked (true),
        // or the negative button was clicked or the dialog was canceled (false).
        if (positiveResult) {
            try {
                Float.valueOf(((CharSequence)(getEditText().getText())).toString());
            }
            catch (NumberFormatException e) {
                // invalid value was input
                this.showDialog(null);
                return;
            }
        }
        super.onDialogClosed(positiveResult);
    }
}
