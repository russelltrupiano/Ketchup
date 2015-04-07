package org.eecs499.russtrup.ketchup;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie2;
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

    // http client instance
    private static DefaultHttpClient mHttpClient;

    private static String sessionCookie;

    public static String baseUrl;

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

        if (Config.MODE_ACTIVE.equals(Config.MODE_DEBUG)) {
            baseUrl = "http://ketchup-api.ngrok.com/api/v1";
        } else {
            baseUrl = "http://www.ketchuptv.me/api/v1";
        }
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
        // Create an instance of the Http client.
        // We need this in order to access the cookie store
        mHttpClient = new DefaultHttpClient();
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HttpClientStack(mHttpClient));
        }

        resetCookie();

        return mRequestQueue;
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

    private static void resetCookie() {

        String cookie = session.getCookie();

        if (cookie != null) {
            Log.i("COOKIE", "Got cookie: " + cookie);
            sessionCookie = cookie;
            CookieStore cs = mHttpClient.getCookieStore();
            cs.addCookie(new BasicClientCookie2("session", sessionCookie));
        } else {
            Log.i("COOKIE", "no cookie found :(");
        }
    }

    private static void setCookie(String cookie) {
        if (cookie != null && cookie.startsWith("session=")) {
            cookie = cookie.substring(8);
            Log.i("COOKIE IN HEADER", cookie);
            sessionCookie = cookie;
            CookieStore cs = mHttpClient.getCookieStore();
            cs.addCookie(new BasicClientCookie2("session", sessionCookie));
            session.setCookie(sessionCookie);
            Log.i("COOKIE", "Set cookie: " + sessionCookie);
        }
    }

    private static class JsonObjectRequestHeaders extends JsonObjectRequest {

        public JsonObjectRequestHeaders(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public JsonObjectRequestHeaders(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            setCookie(response.headers.get("Set-Cookie"));
            return super.parseNetworkResponse(response);
        }
    }

    public static void loginUser(String email, String password, final HTTPCallback callback ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequestHeaders req = new JsonObjectRequestHeaders(KetchupAPI.baseUrl + "/login", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP LOGIN SUCCESS", response.getString("authToken"));
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

        JsonObjectRequestHeaders req = new JsonObjectRequestHeaders(KetchupAPI.baseUrl + "/signup", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.i("HTTP SIGNUP SUCCESS", response.toString());
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
                            Log.i("HTTP LOGOUT SUCCESS", response.toString());
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
                            Log.i("HTTP SEARCH SUCCESS", response.toString());
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

    public static void getMyShows(final HTTPCallback callback) {
        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/" + KetchupAPI.getUserDetails().get("authToken") + "/shows", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP MY SHOWS SUCCESS", response.toString());
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

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void subscribeToShow(String showid, final HTTPCallback callback) {

        Log.i("SUBSCRIBING", "Trying to subscribe to show with id " + showid);

        HashMap<String, String> params = new HashMap<>();
        params.put("show_id", showid);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/" + KetchupAPI.getUserDetails().get("authToken") + "/subscribe", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUB SUCCESS", response.toString());
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

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void unsubscribeToShow(String showid, final HTTPCallback callback) {

        HashMap<String, String> params = new HashMap<>();
        params.put("show_id", showid);

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/" + KetchupAPI.getUserDetails().get("authToken") + "/unsubscribe", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUB SUCCESS", response.toString());
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

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void updateEpisode(String showId, int seasonNumber, int episodeNumber,
                                     boolean watched, final HTTPCallback callback) {
        Log.i("UPDATING", "Marking " + seasonNumber + "x" + episodeNumber + " - " +
                showId + " to " + (watched ? "watched" : "unwatched"));

        HashMap<String, String> params = new HashMap<>();
        StringBuilder showData = new StringBuilder();

        // Build POST params
        showData.append("{\"shows\":[{" +
                "\"id\":\"" + showId + "\"," +
                "\"episodes\":[{" +
                    "\"season\":" + seasonNumber + "," +
                    "\"number\":" + episodeNumber + "," +
                    "\"watched\":" + watched + "}]" +
                "}]}");
        params.put("shows", showData.toString());

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/" +
                KetchupAPI.getUserDetails().get("authToken") + "/episodes", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUB SUCCESS", response.toString());
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

        KetchupAPI.getInstance().addToRequestQueue(req);
    }

    public static void batchUpdateEpisode(String showId, int seasonNumber, int numEpisodes,
                                     boolean watched, final HTTPCallback callback) {
        Log.i("UPDATING", "Marking " + seasonNumber + "x1-" + numEpisodes + " - " +
                showId + " to " + (watched ? "watched" : "unwatched"));

        HashMap<String, String> params = new HashMap<>();
        StringBuilder showData = new StringBuilder();

        // Build POST params
        showData.append("{\"shows\":[{\"id\":\"" + showId + "\",\"episodes\":[");

        for (int i = 1; i <= numEpisodes; i++) {
            showData.append("{" +
                    "\"season\":" + seasonNumber + "," +
                    "\"number\":" + i + "," +
                    "\"watched\":" + watched + "}");
            if (i != numEpisodes) {
                showData.append(",");
            }
        }

        showData.append("]}]}");
        params.put("shows", showData.toString());

        JsonObjectRequest req = new JsonObjectRequest(KetchupAPI.baseUrl + "/" +
                KetchupAPI.getUserDetails().get("authToken") + "/episodes", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("HTTP SUB SUCCESS", response.toString());
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

        KetchupAPI.getInstance().addToRequestQueue(req);
    }
}
