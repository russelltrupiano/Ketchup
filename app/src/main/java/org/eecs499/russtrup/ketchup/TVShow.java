package org.eecs499.russtrup.ketchup;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TVShow extends TVShowBase {

    String _imageUrl;

    ArrayList<Episode> _episodes;

    public TVShow(String id, String title, String network, String airday, String airtime, String imageUrl) {
        super(id, title, network, airday, airtime);
        _imageUrl = imageUrl;
        _episodes = new ArrayList<>();
    }

    public Episode getNextEpisode() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void importEpisodes(ArrayList<Episode> episodes) {
        _episodes = new ArrayList<>(episodes);
        for (int i = 0; i < _episodes.size(); i++) {
            Log.i("EPISODE", _episodes.get(i).get_title());
        }
    }

    public String get_imageUrl() {
        return _imageUrl;
    }

    public ArrayList<Episode> get_episodes() {
        return _episodes;
    }

    public Episode[] get_unwatched_episodes() {
        ArrayList<Episode> unwatched = new ArrayList<>();
        for (Episode e : _episodes) {
            if (!e.get_watched()) {
                unwatched.add(e);
            }
        }
        return Arrays.copyOf(unwatched.toArray(), unwatched.size(), Episode[].class);
    }

    public Episode[] make_demo_episode_list() {

        ArrayList<Episode> episodes = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            episodes.add(new Episode("Pico de Gallo - " + i, 1, i, new Date()));
        }

        return Arrays.copyOf(episodes.toArray(), episodes.size(), Episode[].class);
    }

    // Return ALL episodes for a season with data baout watched and unwatched
    public Episode[] get_episodes_for_season(int selectedSeason) {
        ArrayList<Episode> seasonEpisodes = new ArrayList<>();

        for (Episode e : _episodes) {
            if (e.get_season() == selectedSeason) {
                seasonEpisodes.add(e);
            }
        }

        return Arrays.copyOf(seasonEpisodes.toArray(), seasonEpisodes.size(), Episode[].class);
    }

    public String[] get_seasons_array() {
        int maxSeason = 1;
        for (Episode e : _episodes) {
            if (e.get_season() > maxSeason) {
                maxSeason = e.get_season();
            }
        }
        ArrayList<String> seasons = new ArrayList<>();
        for (int i = 1; i <= maxSeason; i++) {
            seasons.add("Season " + i);
        }

        return Arrays.copyOf(seasons.toArray(), seasons.size(), String[].class);
    }
}
