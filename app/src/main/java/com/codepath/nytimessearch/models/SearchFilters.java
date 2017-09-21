package com.codepath.nytimessearch.models;

import com.codepath.nytimessearch.NYTimesSearchApplication;
import com.codepath.nytimessearch.R;

import java.util.Date;

/**
 * Created by ilyaseletsky on 9/21/17.
 */

public class SearchFilters {
    public enum SortOrder {
        OLDEST(R.string.filter_sort_order_oldest),
        NEWEST(R.string.filter_sort_order_newest);

        private final int resourceId;

        SortOrder(int resourceId) {
            this.resourceId = resourceId;
        }

        @Override
        public String toString() {
            return NYTimesSearchApplication.getContext().getString(getResourceId());
        }

        public int getResourceId() {
            return resourceId;
        }
    }

    Date beginDate;

    SortOrder sortOrder;

}
