package org.eecs499.russtrup.ketchup;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

public class SearchActivity extends ActionBarActivity implements SearchResultFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String query = handleIntent(getIntent());

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search");

            toolbar.bringToFront();
        }

        if (query != null) {
            runQuery(query);
        }
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
        switch(item.getItemId()) {
            case(android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case(R.id.action_settings):
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runQuery(String query) {
        EditText searchBar = (EditText) findViewById(R.id.search_searchbar);
        try {
            searchBar.setText(UriUtils.decode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

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

            FragmentManager fragmentManager = getSupportFragmentManager();

            try {
                JSONArray results = response.getJSONArray("shows");

                for (int i = 0; i < results.length(); i++) {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SearchResultFragment result = new SearchResultFragment();
                    JSONObject resultJson = (JSONObject)results.get(i);
                    String resultId = (String) resultJson.getJSONArray("showid").get(0);
                    String resultName = (String) resultJson.getJSONArray("name").get(0);
                    String resultImage = "";
                    String resultTime = (String) resultJson.getJSONArray("airtime").get(0);
                    String resultNetwork = resultJson.getString("network");


                    fragmentTransaction.add(R.id.searchResultsList, result);
                    fragmentTransaction.commit();
                    result.fillData(resultId, resultName, resultImage, resultTime, resultNetwork);
                }



            } catch (JSONException e) {
                e.printStackTrace();
                onFail();
                return;
            }
        }

        @Override
        public void onFail() {
            Log.i("CALLBACK", "Search Failed");
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
            query = intent.getExtras().getString("QUERY");
        }

        return query;
    }
}
