package com.marshl.mediamogul;

import java.util.HashMap;
import java.util.Map;

public class Media {

    public static final String ACTOR_KEY = "actors";
    public static final String AWARDS_KEY = "awards";
    public static final String CONTENT_RATING_KEY = "content_rating";
    public static final String COUNTRY_KEY = "country";
    public static final String DIRECTOR_KEY = "director";
    public static final String GENRES_KEY = "genres";
    public static final String IMDB_RATING_KEY = "imdb_rating";
    public static final String LANGUAGE_KEY = "language";
    public static final String IMDB_VOTES_KEY = "imdb_votes";
    public static final String METASCORE_KEY = "metascore";
    public static final String PLOT_KEY = "plot";
    public static final String POSTER_URL_KEY = "poster_url";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String RUNTIME_KEY = "runtime";
    public static final String TITLE_KEY = "title";
    public static final String TOTAL_SEASONS_KEY = "total_seasons";
    public static final String TYPE_KEY = "type";
    public static final String WRITER_KEY = "writer";
    public static final String YEAR_KEY = "year";

    public static final int OWNERSHIP_OWNED = 1;
    public static final int OWNERSHIP_NOT_OWNED = 2;
    public static final int OWNERSHIP_ON_WISHLIST = 3;

    private String imdbId;
    private Map<String, String> elementMap;
    private int ownershipStatus;

    public Media() {
        this.elementMap = new HashMap<>();
    }


    public Media(String imdbId, int ownershipStatus) {
        this.imdbId = imdbId;
        this.ownershipStatus = ownershipStatus;
        this.elementMap = new HashMap<>();
    }

    public Media(Media other) {
        this.imdbId = other.imdbId;
        this.ownershipStatus = other.ownershipStatus;
        this.elementMap = new HashMap<>(other.elementMap);
    }

    public Map<String, String> getElementMap() {
        return elementMap;
    }

    public boolean hasKey(String key) {
        return this.elementMap.containsKey(key);
    }

    public String getString(String key) {
        return this.elementMap.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(this.getString(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(this.getString(key));
    }

    public void setValue(String key, String value) {
        if (this.elementMap.containsKey(key)) {
            this.elementMap.remove(key);
        }

        this.elementMap.put(key, value);
    }

    public boolean isGame() {
        return this.hasKey(TYPE_KEY) && this.getString(TYPE_KEY).equals("game");
    }

    public int getOwnershipStatus() {
        return ownershipStatus;
    }

    public void setOwnershipStatus(int ownershipStatus) {
        this.ownershipStatus = ownershipStatus;
    }

    public String getImdbId() {
        return this.imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
