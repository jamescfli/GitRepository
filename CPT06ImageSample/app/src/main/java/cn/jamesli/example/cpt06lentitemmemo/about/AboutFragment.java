package cn.jamesli.example.cpt06lentitemmemo.about;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.jamesli.example.cpt06lentitemmemo.R;

public class AboutFragment extends DialogFragment {
    private static AboutFragment staticAboutFragment = null;

    public AboutFragment() {
        staticAboutFragment = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getResources().getString(R.string.cpsample_about));
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ViewGroup libParent = (ViewGroup) view.findViewById(R.id.about_container); // LinearLayout

        String[] libTitles = getResources().getStringArray(R.array.cpsample_about_titles);
        String[] libDescriptions = getResources().getStringArray(R.array.cpsample_about_contents);
        String libraryPlural = getResources().getQuantityString(R.plurals.cpsample_libraries_plural, libTitles.length);
        String aboutText = getResources().getString(R.string.cpsample_about_text, libraryPlural);
        // Html.fromHtml() Returns displayable styled text from the provided HTML string.
        Spanned spannedAboutText = Html.fromHtml(aboutText);
        TextView aboutTextView = (TextView) libParent.findViewById(R.id.about_text);
        aboutTextView.setText(spannedAboutText);
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());

        for (int i = 0; i < libTitles.length; i++) {
            View libContainer = inflater.inflate(R.layout.single_library_layout, libParent, false);
            TextView currlibTitle = (TextView) libContainer.findViewById(R.id.library_title);
            currlibTitle.setText(libTitles[i]);
            TextView currLibDesc = (TextView) libContainer.findViewById(R.id.library_text);
            Spanned spanned = Html.fromHtml(libDescriptions[i]);
            currLibDesc.setText(spanned);
            // LinkMovementMethod: movement method that traverses links in the text buffer and
            // scrolls if necessary. Supports clicking on links with DPad Center or Enter.
            currLibDesc.setMovementMethod(LinkMovementMethod.getInstance());
            libParent.addView(libContainer);
        }
        return view;
    }

    public static AboutFragment getInstance() {
        if (null == staticAboutFragment) {
            staticAboutFragment = new AboutFragment();
        }
        return staticAboutFragment;
    }
}
