package com.codepath.nytimessearch;

import android.app.Application;
import android.content.Context;

/**
 * Created by ilyaseletsky on 9/21/17.
 */

public class NYTimesSearchApplication extends Application {
    /**
     * Doing this so my enum can return a localized toString.
     * https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
     */
    private static Context staticContextRef;

    @Override
    public void onCreate() {
        super.onCreate();

        staticContextRef = this;
    }

    public static Context getContext(){
        return staticContextRef;
    }
}
