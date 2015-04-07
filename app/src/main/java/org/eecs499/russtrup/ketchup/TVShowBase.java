package org.eecs499.russtrup.ketchup;

import android.util.Log;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TVShowBase implements Serializable, Comparable{

    private String TAG = TVShowBase.class.getSimpleName();

    String _id;
    String _title;
    int _year;
    String _background_url;

    public TVShowBase(String _id, String _title, int _year, String _background_url) {
        this._id = _id;
        this._title = _title;
        this._year = _year;
        this._background_url = _background_url;
    }

    public TVShowBase(String _id, String _title) {
        this._id = _id;
        this._title = _title;
    }

    public String get_id() {
        return _id;
    }

    public String get_title() {
        return _title;
    }

    public String get_background_url() {
        return _background_url;
    }

    public int get_year() {

        return _year;
    }

    protected String formatTime(String time) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat outputDf = new SimpleDateFormat("hh:mm aa");

        Date date;
        String output = null;

        try {
            date = df.parse(time);
            output = outputDf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public int compareTo(Object another) {
        if (another.getClass() == TVShowBase.class) {
            return compareTo((TVShowBase)another);
        } else {
            Log.e(TAG, "Cannot compare " + TVShowBase.class.getSimpleName() + " with " + another.getClass().getSimpleName());
            return 0;
        }
    }

    public int compareTo(TVShowBase tvshowbase) {
        // TODO: For now sort by name, later sort by next airing episode
        return this.get_title().compareTo(tvshowbase.get_title());
    }
}
