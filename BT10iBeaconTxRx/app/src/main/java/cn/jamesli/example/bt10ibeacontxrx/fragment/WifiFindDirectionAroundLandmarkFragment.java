package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.jamesli.example.bt10ibeacontxrx.R;


public class WifiFindDirectionAroundLandmarkFragment extends Fragment {
    private static final String TAG = "WifiFindDirectionAroundLandmarkFragment";

    private static String mFragmentTitle;

    public static WifiFindDirectionAroundLandmarkFragment newInstance(String fragmentTitle) {
        mFragmentTitle = fragmentTitle;
        return new WifiFindDirectionAroundLandmarkFragment();
    }

    public WifiFindDirectionAroundLandmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(mFragmentTitle);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_find_direction_around_landmark, container, false);
    }

}
