package com.pinterset.clienttask.pinterestclient.fragmenet;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinterset.clienttask.pinterestclient.R;

/**
 * Created by Nourhan on 1/6/2017.
 */

public class RawResponseDialogFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "raw";
    private String raw;

    public static RawResponseDialogFragment newInstance(String raw) {
        RawResponseDialogFragment fragment = new RawResponseDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, raw);
        fragment.setArguments(args);
        return fragment;
    }

    public RawResponseDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            raw = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raw_response_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        TextView tv = (TextView) getView().findViewById(R.id.tv);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(raw);
    }

    public void show(FragmentManager supportFragmentManager, String rawResponseDialogFragment) {
    }
}
