package com.codepath.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nytimessearch.R;

/**
 * Created by ilyaseletsky on 9/21/17.
 */

public class SearchFilterDialogGragment extends DialogFragment {
    public SearchFilterDialogGragment() {}

    public static SearchFilterDialogGragment newInstance() {
        SearchFilterDialogGragment frag = new SearchFilterDialogGragment();

        Bundle args = new Bundle();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_filters, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
