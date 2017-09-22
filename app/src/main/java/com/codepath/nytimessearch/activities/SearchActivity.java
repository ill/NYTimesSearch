package com.codepath.nytimessearch.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.adapters.EndlessScrollListener;
import com.codepath.nytimessearch.fragments.SearchFilterDialogGragment;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.nytimessearch.models.SearchFilters.SortOrder.NEWEST;

public class SearchActivity extends AppCompatActivity {

    static final String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    static final String API_KEY = "8267db2803dc4322b918ee66bd57f778";

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String currentQuery;
    //int currentPage;

    SearchFilters searchFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
    }

    public void setupViews() {
        searchFilters = new SearchFilters();

        gvResults = (GridView) findViewById(R.id.gvResults);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);

                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (page < 100) {
                    performArticleSearchQueryDelayed(page);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
                else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                performNewArticleSearchQuery(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            showFilterDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchFilterDialogGragment editNameDialogFragment = SearchFilterDialogGragment.newInstance(searchFilters, new SearchFilterDialogGragment.DidSaveListener() {
            @Override
            public void didSave() {
                performNewArticleSearchQuery(currentQuery);
            }
        });
        editNameDialogFragment.show(fm, "fragment_search_filters");
    }

    void performNewArticleSearchQuery(String query) {
        //currentPage = 0;
        currentQuery = query;
        adapter.clear();

        performArticleSearchQuery(0);
    }

    void performArticleSearchQueryDelayed(final int page) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performArticleSearchQuery(page);
            }
        }, 100);
    }

    //Suppressing the warning because I really want that date in that specific format for the NYTimes API query
    @SuppressLint("SimpleDateFormat")
    void performArticleSearchQuery(final int page) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page/*currentPage*/);
        params.put("q", currentQuery);

        params.put("sort", searchFilters.sortOrder == NEWEST ? "newest" : "oldest");

        StringBuilder ndvStringBuilder = new StringBuilder();

        if (searchFilters.ndvArts) {
            ndvStringBuilder.append("\"Arts\" ");
        }

        if (searchFilters.ndvFashionStyle) {
            ndvStringBuilder.append("\"Fashion & Style\" ");
        }

        if (searchFilters.ndvSports) {
            ndvStringBuilder.append("\"Sports\" ");
        }

        if (ndvStringBuilder.length() > 0) {
            params.put("fq", "news_desk:(" + ndvStringBuilder.toString() + ")");
        }

        params.put("begin_date", new SimpleDateFormat("yyyyMMdd").format(searchFilters.beginDate));

        client.get(API_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));

                    //++currentPage;
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                retryIfRateLimited(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                retryIfRateLimited(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                retryIfRateLimited(statusCode);
            }

            void retryIfRateLimited(int statusCode) {
                if (statusCode == 429) {
                    performArticleSearchQueryDelayed(page);
                }
            }
        });
    }
}
