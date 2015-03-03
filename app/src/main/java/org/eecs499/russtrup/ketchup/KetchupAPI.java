package org.eecs499.russtrup.ketchup;

import android.app.Application;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KetchupAPI extends Application {

    /**
     * Log or request TAG
     */
    public static final String TAG = "Volley HTTP";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static KetchupAPI sInstance;

    public static final String baseUrl = "http://62e05ac9.ngrok.com/api/v1";

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized KetchupAPI getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static void loginUser(String email, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/login", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP ERROR", error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void signupUser(String email, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/signup", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP ERROR", error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void logoutUser(String email, String password) {

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/signup", new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP ERROR", error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        KetchupAPI.getInstance().addToRequestQueue(req);
    }
}
