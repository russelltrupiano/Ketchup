package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

public class MainActivity extends BaseActivity
    implements  ContentFragment.OnFragmentInteractionListener,
                MyShowsListitemFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (KetchupAPI.checkLogin()) {
            loadCurrentFragment();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static void sendSearch(Context c, JSONObject response) {
        Intent i = new Intent(c, SearchActivity.class);
        jsonToContextVars(c, response);
        c.startActivity(i);
        ((Activity) c).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void jsonToContextVars(Context c, JSONObject response) {
        JSONArray showArray = null;
        try {
            showArray = response.getJSONArray("shows");

            for (int i = 0; i < showArray.length(); i++) {
                JSONObject jsonobject = showArray.getJSONObject(i);
                String name = jsonobject.getString("name");
                String airday = jsonobject.getString("airday");
                Log.i("ARRAY PARSER", name + " " + airday);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void redirectSearch(Context c, String query) {
        Intent i = new Intent(c, SearchActivity.class);
        try {
            i.putExtra("QUERY", UriUtils.encodeQuery(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        c.startActivity(i);
        ((Activity) c).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
