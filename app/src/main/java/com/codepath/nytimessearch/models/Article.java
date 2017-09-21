package com.codepath.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ilyaseletsky on 9/19/17.
 */

public class Article implements Serializable {
    static final String THUMBNAIL_URL_PREFIX = "http://www.nytimes.com/";

    String webUrl;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String headLine;
    String thumbNail;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = THUMBNAIL_URL_PREFIX + multimediaJson.getString("url");
            }
            else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<Article>();

        for (int ind = 0; ind < array.length(); ++ind) {
            try {
                results.add(new Article(array.getJSONObject(ind)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
