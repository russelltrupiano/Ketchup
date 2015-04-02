package org.eecs499.russtrup.ketchup;

import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class User implements Serializable {

    private String TAG = User.class.getSimpleName();

    private static User _instance;
    private String _id;
    private String _email;
    private String _name; // TODO: not used yet
    // key: id value: TVShow
    private HashMap<String, TVShow> _subscriptions;

    // Use singleton design pattern
    private User(){
        _subscriptions = new HashMap<>();
    };

    public static User get_instance() {
        if (_instance == null) {
            _instance = new User();
        }
        return _instance;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public HashMap<String, TVShow> get_subscriptions() {
        return _subscriptions;
    }


    public void updateSubscriptions(Collection<TVShow> myShows) {

        Log.i(TAG, "Updating subscriptions");

        HashMap<String, TVShow> _tempShows = new HashMap<>();

        for (TVShow show : myShows) {
            _tempShows.put(show.get_id(), show);
        }

        _subscriptions = _tempShows;
        Log.i(TAG, "There are " + _subscriptions.size() + " subscriptions.");
    }

    public boolean subscribedTo(TVShowBase tvshowbase) {
        Log.i(TAG, "Checking if subscribed to " + tvshowbase.get_title() + " - " + tvshowbase.get_id() + " from " + _subscriptions.size() + " subscriptions.");

        StringBuilder sb = new StringBuilder();
        for (String key : _subscriptions.keySet()) {
            sb.append("\t");
            sb.append(key);
            sb.append("\n");
        }
        Log.i(TAG, sb.toString());
        return _subscriptions.keySet().contains(tvshowbase.get_id());
    }
}
