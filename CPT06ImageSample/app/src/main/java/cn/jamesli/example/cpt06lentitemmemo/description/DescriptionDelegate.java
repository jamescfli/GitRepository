package cn.jamesli.example.cpt06lentitemmemo.description;

import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.jamesli.example.cpt06lentitemmemo.R;

public class DescriptionDelegate {
    private ViewGroup mLinkContainer;
    
    public void addDescriptionAndLinks(LayoutInflater inflater, ViewGroup rootView,
                                       Resources resources, int descriptionId,
                                       int linkTextsId, int linkTargetsId) {
        TextView description = (TextView) rootView.findViewById(R.id.demoapp_fragment_description);
        Spanned descSpannable = Html.fromHtml(resources.getString(descriptionId));
        description.setText(descSpannable);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        mLinkContainer = (ViewGroup) rootView.findViewById(R.id.container_demo_blog_links);
        String[] linkTexts = resources.getStringArray(linkTextsId);
        String[] linkTargets = resources.getStringArray(linkTargetsId);
        showLinks(mLinkContainer, inflater, linkTexts, linkTargets);
    }

    // was no private previously
    private void showLinks(ViewGroup container, LayoutInflater inflater,
                           String[] linkTexts, String[] linkTargets) {
        StringBuilder link = null;
        for (int i = 0; i < linkTexts.length; i++) {
            final TextView tv = (TextView) inflater.inflate(R.layout.single_blog_link, container, false);
            link = new StringBuilder(100);
            link.append("<a href=\"");
            link.append(linkTargets[i]);
            link.append("\">");
            link.append(linkTexts[i]);
            link.append("</a>");
            Spanned spannedLink = Html.fromHtml(link.toString());
            tv.setText(spannedLink);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            container.addView(tv);
        }
    }

    public void clearFocusOnLinks() {
        if (mLinkContainer != null) {
            int count = mLinkContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                mLinkContainer.getChildAt(i).clearFocus();
            }
        }
    }
}
