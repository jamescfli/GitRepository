package cn.jamesli.example.at94customizedalertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends Activity implements NoticeDialogFragment.NoticeDialogListener {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
        case R.id.action_show_simple_dialog:
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setMessage("This is a smaple alert dialog!");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        case R.id.action_show_customized_dialog:
            final Dialog dialog = new Dialog(this);
            // Hide to default title for Dialog
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Inflate dialog_layout.xml
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_layout, null, false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(view);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            // Retrieve views from the inflated dialog layout
            TextView textTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
            textTitle.setText("Custom Dialog");

            TextView textMessage = (TextView) dialog.findViewById(R.id.txt_dialog_message);
            textMessage.setText("Do you want to visit the website : https://www.baidu.com ?");

            Button btnOpenBrowser = (Button) dialog.findViewById(R.id.btn_open_browser);
            btnOpenBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Open the browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
                    startActivity(browserIntent);
                    // Dismiss the dialog
                    // If not applied, the activity will come back to dialog page, instead of the
                    // activity, if you come back from the browser.
                    dialog.dismiss();
                }
            });

            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the dialog
                    dialog.dismiss();
                }
            });

            // Display the dialog
            dialog.show();
            return true;
        case R.id.action_show_datepicker_dialog:
            final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    DatePickerDialog.THEME_HOLO_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            textView.setText("Date: " + year + "/" + monthOfYear + "/" + dayOfMonth);
                        }
                    },
                    2015,
                    7,
                    26);
            datePickerDialog.show();
            return true;
        case R.id.action_show_notice_dialog:
            NoticeDialogFragment noticeDialogFragment = new NoticeDialogFragment();
            noticeDialogFragment.show(getFragmentManager(), "NoticeDialogFragment");
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        textView.setText("Feedback from NoticeDialog: positive");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        textView.setText("Feedback from NoticeDialog: negative");
    }
}
