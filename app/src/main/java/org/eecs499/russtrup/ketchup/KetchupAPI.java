package org.eecs499.russtrup.ketchup;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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

    private static SessionManager session;

    public static String baseUrl = "http://ketchup-api.ngrok.com/api/v1";
//    public static String baseUrl = "http://www.ketchuptv.me/api/v1";

    public interface HTTPCallback {
        void invokeCallback(JSONObject response) throws JSONException;
        void onFail();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;

        session = new SessionManager(getApplicationContext());
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

    public static void loginUser(String email, String password, final HTTPCallback callback ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/login", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.getString("authToken"));
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            session.createLoginSession(response.getString("email"), response.getString("authToken"));
                            callback.invokeCallback(response);
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                            callback.onFail();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP ERROR", error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFail();
            }
        });

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void signupUser(String email, String password, final HTTPCallback callback) {
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
                            session.createLoginSession(response.getString("email"), response.getString("authToken"));
                            callback.invokeCallback(response);
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                            callback.onFail();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP ERROR", error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFail();
            }
        });

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static boolean checkLogin() {
        Log.i("SESSION", "checking login");
        return session.checkLogin();
    }

    public static HashMap<String, String> getUserDetails() {
        return session.getUserDetails();
    }

    public static void logoutUser() {
        HashMap<String, String> params = new HashMap<>();

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/logout", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            session.logoutUser();
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

    public static void searchShows(String query, final HTTPCallback callback ) {

        Log.i("HTTP REQ", KetchupAPI.baseUrl + "/search?query=" + query);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/search?query=" + query, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUCCESS", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            callback.invokeCallback(response);
                        } catch (JSONException e) {
                            Log.i("HTTP EXCEPTION", e.getMessage());
                            e.printStackTrace();
                            callback.onFail();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFail();
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(45000, 2, 1.0f));

        KetchupAPI.getInstance().addToRequestQueue(req);
    }
}
