package org.eecs499.russtrup.ketchup;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity
        implements SearchResultFragment.OnFragmentInteractionListener,
        FailedSearchFragment.OnFragmentInteractionListener {

    SearchView searchView;
    int numResults;
    String lastQuery;
    LinearLayout resultsContainer;
    AnimationDrawable loadingAnimation;
    ImageView loadingSpinner;

    ArrayList<TVShowBase> searchResults;
    ArrayList<Fragment> resultFragments;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String query = handleIntent(getIntent());
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView) findViewById(R.id.searchBox);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setQueryHint("ex. Game of Thrones");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        numResults = 0;
        searchResults = new ArrayList<>();
        resultFragments = new ArrayList<>();
        resultsContainer = (LinearLayout) findViewById(R.id.searchResultsList);

        loadingSpinner = (ImageView) findViewById(R.id.loading_spinner);
        loadingSpinner.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loadingSpinner.getBackground();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("SEARCH", s);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                clearResults();

                try {
                    runQuery(UriUtils.encodeQuery(s, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("SearchActivity", e.getMessage());
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            // Don't allow the searchView to collapse (it looks bad)
            @Override
            public boolean onClose() {
                return true;
            }
        });

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search");

            toolbar.bringToFront();
        }

        if (savedInstanceState == null) {

            if (query != null && !query.isEmpty()) {
                runQuery(query);
            } else {
                searchView.requestFocus();
            }
        }
    }

    private void clearResults() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        assert (searchResults.size() == numResults);

        for (int i = 0; i < numResults; i++) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(resultFragments.remove(0));
            fragmentTransaction.commit();
            searchResults.remove(0);
        }

        numResults = 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.action_settings):
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runQuery(String query) {

        loadingAnimation.start();
        loadingSpinner.setVisibility(View.VISIBLE);

        try {
            searchView.setQuery(UriUtils.decode(query, "UTF-8"), false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("SearchActivity", e.getMessage());
        }
        lastQuery = query;
        KetchupAPI.searchShows(query, new SearchCallback(findViewById(R.id.search_content)));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SearchCallback implements KetchupAPI.HTTPCallback {

        View redirectView;

        public SearchCallback(View v) {
            redirectView = v;
        }

        @Override
        public void invokeCallback(JSONObject response) {

            loadingSpinner.setVisibility(View.INVISIBLE);
            loadingAnimation.stop();

            FragmentManager fragmentManager = getSupportFragmentManager();

            try {
                JSONArray results = response.getJSONArray("shows");

                for (int i = 0; i < results.length(); i++) {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SearchResultFragment result = new SearchResultFragment();

                    resultFragments.add(result);

                    JSONObject resultJson = (JSONObject) results.get(i);
                    String resultId = (String) resultJson.getJSONArray("showid").get(0);
                    String resultName = (String) resultJson.getJSONArray("name").get(0);
                    String resultTime = (String) resultJson.getJSONArray("airtime").get(0);
                    String resultDay = (String) resultJson.getJSONArray("airday").get(0);
                    String resultNetwork = resultJson.getString("network");

                    TVShowBase show = new TVShowBase(resultId, resultName, resultNetwork, resultDay, resultTime);
                    result.set_tvshowbase(show);
                    searchResults.add(show);

                    fragmentTransaction.add(R.id.searchResultsList, result);
                    fragmentTransaction.commit();

                    numResults++;
                }

            } catch (JSONException | IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail() {

            try {
                loadingSpinner.setVisibility(View.INVISIBLE);
                loadingAnimation.stop();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                FailedSearchFragment failedSearchFragment = new FailedSearchFragment();

                resultFragments.add(failedSearchFragment);
                numResults = 1;

                fragmentTransaction.add(R.id.searchResultsList, failedSearchFragment);
                fragmentTransaction.commit();

                // Force transaction to assign retry to button
                fragmentManager.executePendingTransactions();

                Button retryBtn = failedSearchFragment.getRetryButton();

                retryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearResults();
                        KetchupAPI.searchShows(lastQuery, new SearchCallback(findViewById(R.id.search_content)));
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private String handleIntent(Intent intent) {
        String query;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            try {
                query = UriUtils.encodeQuery(intent.getStringExtra(SearchManager.QUERY), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (intent.getExtras() != null) {
                query = intent.getExtras().getString("QUERY");
            } else {
                query = null;
            }
        }

        return query;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
