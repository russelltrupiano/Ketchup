package org.eecs499.russtrup.ketchup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TVShow extends TVShowBase {

    String _imageUrl;
    String _headerUrl;

    ArrayList<Episode> _episodes;

    public TVShow(String id, String title, String network, String airday, String airtime, String imageUrl, String headerUrl) {
        super(id, title, network, airday, airtime);
        _imageUrl = imageUrl;
        _headerUrl = headerUrl;
        _episodes = new ArrayList<>();
    }

    public Episode getNextEpisode() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void importEpisodes(ArrayList<Episode> episodes) {
        _episodes = new ArrayList<>(episodes);
    }

    public String get_imageUrl() {
        return _imageUrl;
    }
    public String get_headerUrl() {
        return _headerUrl;
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

    public int get_num_unwatched() {
        int count = 0;
        for (Episode e : _episodes) {
            if (!e.get_watched()) {
                count++;
            }
        }
        return count;
    }
}
