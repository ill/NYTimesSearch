package com.codepath.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.SearchFilters;

/**
 * Created by ilyaseletsky on 9/21/17.
 */

public class SearchFilterDialogGragment extends DialogFragment {
    Spinner spSortOrder;

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

        spSortOrder = (Spinner)view.findViewById(R.id.spSortOrder);

        //set up the spinner values
        ArrayAdapter<SearchFilters.SortOrder> priorityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                SearchFilters.SortOrder.values());

        spSortOrder.setAdapter(priorityAdapter);
    }
}
