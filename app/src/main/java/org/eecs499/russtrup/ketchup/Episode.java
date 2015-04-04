package org.eecs499.russtrup.ketchup;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

public class Episode implements Comparable{

    private String TAG = Episode.class.getSimpleName();

    String _title;
    int _season;
    int _episodeNumber;
    Date _airdate;
    Boolean _watched;

    public Episode(String title, int season, int episodeNumber, Date airdate) {
        this(title, season, episodeNumber, airdate, false);
    }

    public Episode(String title, int season, int episodeNumber, Date airdate, Boolean watched) {
        _title = title;
        _season = season;
        _episodeNumber = episodeNumber;
        _airdate = airdate;
        _watched = watched;
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

    public Boolean get_watched() {
        return _watched;
    }

    @Override
    public int compareTo(@NonNull Object another) {
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
