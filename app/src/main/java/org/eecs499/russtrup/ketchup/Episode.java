package org.eecs499.russtrup.ketchup;

import android.util.Log;

import java.util.Date;

public class Episode implements Comparable{

    private String TAG = Episode.class.getSimpleName();

    String _id;
    String _title;
    int _season;
    int _episodeNumber;
    Date _airdate;

    public Episode(String id, String title, int season, int episodeNumber, Date airdate) {
        _id = id;
        _title = title;
        _season = season;
        _episodeNumber = episodeNumber;
        _airdate = airdate;
    }


    public String get_id() {
        return _id;
    }

    public String get_title() {
        return _title;
    }

    public int get_season() {
        return _season;
    }

    public int get_episodeNumber() {
        return _episodeNumber;
    }

    public Date get_airdate() {
        return _airdate;
    }


    @Override
    public int compareTo(Object another) {
        if (another.getClass() == Episode.class) {
            return compareTo((Episode)another);
        } else {
            Log.e(TAG, "Cannot compare " + TVShowBase.class.getSimpleName() + " with " + another.getClass().getSimpleName());
            return 0;
        }
    }

    public int compareTo(Episode another) {

        if (get_season() != another.get_season()) {
            return get_season() - another.get_season();
        }
        else {
            return get_episodeNumber() - another.get_episodeNumber();
        }
    }
}
