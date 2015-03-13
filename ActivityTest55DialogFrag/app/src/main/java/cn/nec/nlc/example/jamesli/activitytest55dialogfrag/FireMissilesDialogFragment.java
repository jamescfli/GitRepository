package cn.nec.nlc.example.jamesli.activitytest55dialogfrag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.ArrayList;

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

//    // List Dialog
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.pick_color)
//                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String[] colors = getResources().getStringArray(R.array.colors_array);
//                        ((MainActivity) getActivity()).getDisplayTextView().setText(colors[i] + " was selected.");
//                    }
//                });
//        return builder.create();
//    }

//    private ArrayList<Integer> mSelectedItems;
//    // Adding a persistent multiple-choice or single-choice list
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        mSelectedItems = new ArrayList<>();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // set title
//        builder.setTitle(R.string.pick_toppings)
//                // null means default with none selection
//                .setMultiChoiceItems(R.array.toppings, null, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
//                        if (isChecked) {
//                            mSelectedItems.add(which);
//                        } else if (mSelectedItems.contains(which)) {
//                            mSelectedItems.remove(Integer.valueOf(which));
//                        }
//                    }
//                })
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String[] toppings = getResources().getStringArray(R.array.toppings);
//                        StringBuilder selectedToppings = new StringBuilder();
//                        for (int j = 0, SIZE = mSelectedItems.size(); j < SIZE; j++) {
//                            selectedToppings.append(toppings[mSelectedItems.get(j)] + " ");
//                        }
//                        ((MainActivity) getActivity()).getDisplayTextView().setText(selectedToppings + "was(were) selected.");
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ((MainActivity) getActivity()).getDisplayTextView().setText("job cancelled");
//                    }
//                });
//        return builder.create();
//    }

//    // sign-in dialog with edittext views
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // get the layoutInflater
//        final LayoutInflater inflater = getActivity().getLayoutInflater();
//        // inflate and set the layout for the dialog
//        // pass null as the parent view because it's going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
//                // set action buttons
//                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // Retrieve any other views from the dialog by querying the dialogView
//                        EditText editTextUn = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.username);
//                        EditText editTextPw = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.password);
//                        ((MainActivity) getActivity()).getDisplayTextView()
//                                .setText("UN: " + editTextUn.getText()
//                                        + "\nPW: " + editTextPw.getText());
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        FireMissilesDialogFragment.this.getDialog().cancel();
//                          // You can also cancel a dialog. This is a special event that indicates
//                          // the user explicitly left the dialog without completing the task. This
//                          // occurs if the user presses the Back button, touches the screen outside
//                          // the dialog area, or if you explicitly call cancel() on the Dialog
//                          // (such as in response to a "Cancel" button in the dialog).
//                        ((MainActivity) getActivity()).getDisplayTextView()
//                                .setText("Sign in was cancelled.");
//                    }
//                });
//        return builder.create();
//    }

    // passing events back to the Dialog's Host
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FireMissilesDialogListener {
        public void onDialogPositiveClick(DialogFragment dialogFragment);
        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    // use this instance of the interface to deliver action events
    FireMissilesDialogListener mListener;

    // override the Fragment.onAttach() method to instantiate the FireMissilesDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // verify the host activity implements the callback interface
        try {
            // instantiate the FireMissilesDialogListener so we can send events to the host
            mListener = (FireMissilesDialogListener) activity;
        } catch (ClassCastException e) {
            // activity does not implement the interface
            throw new ClassCastException(activity.toString()
                    + " must implement FireMissilesDialogListener");
        }
    }

    // use Yes - No Dialog at the beginning without access to textView in MainActivity
    // public Dialog onCreateDialog(Bundle savedInstanceState)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // send the positive button event back to activity
                        mListener.onDialogPositiveClick(FireMissilesDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // send the negative button event back to activity
                        mListener.onDialogNegativeClick(FireMissilesDialogFragment.this);
                    }
                });
        // one could setNeutralButton as well, at most three buttons
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
