package org.eecs499.russtrup.ketchup;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {

    SharedPreferences prefs;

    // Editor for SharedPreferences
    Editor editor;

    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref filename
    private static final String PREF_NAME = "KetchupPref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "authToken";
    private static final String COOKIE = "sessionCookie";

    public SessionManager(Context context) {
        this._context = context;
        prefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    // Setup a login session
    public void createLoginSession(String email, String authToken) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing auth token in pref
        editor.putString(KEY_TOKEN, authToken);
        User.get_instance().set_id(authToken);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        User.get_instance().set_email(email);

        // commit changes
        editor.commit();
    }

    // Get session data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_TOKEN, prefs.getString(KEY_TOKEN, null));

        // user email id
        user.put(KEY_EMAIL, prefs.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    // Get Login State
    public boolean isLoggedIn(){
        return prefs.getBoolean(IS_LOGIN, false);
    }

    // Check login method wil check user login status
    // If false it will redirect user to login page
    // Else won't do anything
    public boolean checkLogin(){

        // Check login status
        if(!this.isLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

        return this.isLoggedIn();
    }

    // Clear session details
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void setCookie(String cookie) {
        editor.putString(COOKIE, cookie);
        editor.commit();
    }

    public String getCookie() {
        return prefs.getString(COOKIE, null);
    }
}
