package cn.nec.nlc.example.jamesli.activitytest55dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class FireMissilesDialogFragment extends DialogFragment {
    public FireMissilesDialogFragment() {
        // empty constructor
    }

//    // Yes - No Dialog
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.dialog_fire_missiles)
//                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                        ((MainActivity) getActivity()).getDisplayTextView().setText("FIRE ZE MISSILES!");
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        ((MainActivity) getActivity()).getDisplayTextView().setText("User cancelled the dialog");
//                    }
//                });
//            // one could setNeutralButton as well, at most three buttons
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }

    // List Dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_color)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] colors = getResources().getStringArray(R.array.colors_array);
                        ((MainActivity) getActivity()).getDisplayTextView().setText(colors[i] + " was selected.");
                    }
                });
        return builder.create();
    }
}
