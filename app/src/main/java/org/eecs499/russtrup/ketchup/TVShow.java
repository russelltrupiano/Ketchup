package org.eecs499.russtrup.ketchup;

import java.util.ArrayList;

public class TVShow extends TVShowBase{

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

    public String get_imageUrl() {
        return _imageUrl;
    }

    public ArrayList<Episode> get_episodes() {
        return _episodes;
    }
}
