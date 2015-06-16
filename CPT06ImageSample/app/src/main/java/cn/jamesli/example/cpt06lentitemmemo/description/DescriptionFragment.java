package cn.jamesli.example.cpt06lentitemmemo.description;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.jamesli.example.cpt06lentitemmemo.Constants;
import cn.jamesli.example.cpt06lentitemmemo.R;

public class DescriptionFragment extends Fragment {

    private static DescriptionDelegate mDescDelegate;

    public static DescriptionFragment newInstance(int descriptionId, int linkTextsId, int linkTargetsId) {
        DescriptionFragment f = new DescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_DESCRIPTION_ID, descriptionId);
        bundle.putInt(Constants.KEY_LINK_TEXTS_ID, linkTextsId);
        bundle.putInt(Constants.KEY_LINK_TARGETS_ID, linkTargetsId);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.fragment_demo_description,
                container, false);
        mDescDelegate = new DescriptionDelegate();
        mDescDelegate.addDescriptionAndLinks(inflater, rootView, getResources(),
                getArguments().getInt(Constants.KEY_DESCRIPTION_ID),
                getArguments().getInt(Constants.KEY_LINK_TEXTS_ID),
                getArguments().getInt(Constants.KEY_LINK_TARGETS_ID));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mDescDelegate) {
            mDescDelegate.clearFocusOnLinks();
        }
    }
}
