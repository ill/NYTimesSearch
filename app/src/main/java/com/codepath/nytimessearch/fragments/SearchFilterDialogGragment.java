package com.codepath.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.SearchFilters;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ilyaseletsky on 9/21/17.
 */

public class SearchFilterDialogGragment extends DialogFragment {
    static final String SEARCH_FILTERS_KEY = "searchFilters";

    SearchFilters searchFilters;

    DatePicker dpDatePicker;
    Spinner spSortOrder;
    CheckBox cbSports;
    CheckBox cbFashionStyle;
    CheckBox cbArts;

    public SearchFilterDialogGragment() {}

    public static SearchFilterDialogGragment newInstance(SearchFilters searchFilters) {
        SearchFilterDialogGragment frag = new SearchFilterDialogGragment();

        Bundle args = new Bundle();
        args.putSerializable(SEARCH_FILTERS_KEY, searchFilters);
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

        searchFilters = (SearchFilters) getArguments().getSerializable(SEARCH_FILTERS_KEY);

        dpDatePicker = (DatePicker) view.findViewById(R.id.dpDatePicker);
        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);

        cbSports = (CheckBox) view.findViewById(R.id.cbSports);
        cbFashionStyle = (CheckBox) view.findViewById(R.id.cbFashionStyle);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);

        //set up the spinner values
        ArrayAdapter<SearchFilters.SortOrder> priorityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                SearchFilters.SortOrder.values());
        spSortOrder.setAdapter(priorityAdapter);

        setDate(searchFilters.beginDate);

        setSortOrder(searchFilters.sortOrder);

        cbSports.setChecked(searchFilters.ndvSports);
        cbFashionStyle.setChecked(searchFilters.ndvFashionStyle);
        cbArts.setChecked(searchFilters.ndvArts);

        Button btSave = (Button)view.findViewById(R.id.btSave);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
    }

    public void onSave() {

        searchFilters.beginDate = getDate();
        searchFilters.sortOrder = getSortOrder();

        searchFilters.ndvSports = cbSports.isChecked();
        searchFilters.ndvFashionStyle = cbFashionStyle.isChecked();
        searchFilters.ndvArts = cbArts.isChecked();

        dismiss();
    }

    public Date getDate() {
        int day = dpDatePicker.getDayOfMonth();
        int month = dpDatePicker.getMonth() + 1;
        int year = dpDatePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(dpDatePicker.getYear(),
                dpDatePicker.getMonth(),
                dpDatePicker.getDayOfMonth());

        return calendar.getTime();
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        dpDatePicker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public SearchFilters.SortOrder getSortOrder() {
        return (SearchFilters.SortOrder) spSortOrder.getSelectedItem();
    }

    public void setSortOrder(SearchFilters.SortOrder sortOrder) {
        //assumes value is always in the order of the enums
        spSortOrder.setSelection(sortOrder.ordinal());
    }
}
