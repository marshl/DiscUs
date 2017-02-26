package com.marshl.discus;

import android.content.ContentValues;
import android.provider.BaseColumns;

import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_ACTORS;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_AWARDS;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_CONTENT_RATING;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_COUNTRY;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_DIRECTOR;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_DURATION_MINUTES;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_GENRES;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_RATING;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_VOTES;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_LANGUAGES;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_METASCORE;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_OWNERSHIP_STATUS;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_PLOT;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_POSTER_URL;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_RELEASE_DATE;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_WRITER;
import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR;

public final class MediaReaderContract {
    private MediaReaderContract() {
    }

    public static ContentValues getContentValuesForMedia(Media media) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, media.getTitle());
        values.put(COLUMN_NAME_YEAR, media.getYear());
        values.put(COLUMN_NAME_CONTENT_RATING, media.getContentRating());
        values.put(COLUMN_NAME_RELEASE_DATE, media.getReleaseDate().getTime());
        values.put(COLUMN_NAME_DURATION_MINUTES, media.getDurationMinutes());
        values.put(COLUMN_NAME_GENRES, media.getGenres());
        values.put(COLUMN_NAME_DIRECTOR, media.getDirector());
        values.put(COLUMN_NAME_WRITER, media.getWriter());
        values.put(COLUMN_NAME_ACTORS, media.getActors());
        values.put(COLUMN_NAME_PLOT, media.getPlot());
        values.put(COLUMN_NAME_LANGUAGES, media.getLanguages());
        values.put(COLUMN_NAME_COUNTRY, media.getCountry());
        values.put(COLUMN_NAME_AWARDS, media.getAwards());
        values.put(COLUMN_NAME_POSTER_URL, media.getPosterUrl());
        values.put(COLUMN_NAME_METASCORE, media.getMetascore());
        values.put(COLUMN_NAME_IMDB_RATING, media.getImdbRating());
        values.put(COLUMN_NAME_IMDB_VOTES, media.getImdbVotes());
        values.put(COLUMN_NAME_IMDB_ID, media.getImdbId());
        values.put(COLUMN_NAME_TYPE, media.getType());
        values.put(COLUMN_NAME_OWNERSHIP_STATUS, media.getOwnershipStatus().ordinal());

        return values;
    }

    public static class MediaEntry implements BaseColumns {
        public static final String TABLE_NAME = "media";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_CONTENT_RATING = "content_rating";
        public static final String COLUMN_NAME_RELEASE_DATE = "released_ate";
        public static final String COLUMN_NAME_DURATION_MINUTES = "duration_minutes";
        public static final String COLUMN_NAME_GENRES = "genres";
        public static final String COLUMN_NAME_DIRECTOR = "director";
        public static final String COLUMN_NAME_WRITER = "writer";
        public static final String COLUMN_NAME_ACTORS = "actors";
        public static final String COLUMN_NAME_PLOT = "plot";
        public static final String COLUMN_NAME_LANGUAGES = "languages";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_AWARDS = "awards";
        public static final String COLUMN_NAME_POSTER_URL = "poster_url";
        public static final String COLUMN_NAME_METASCORE = "metascore";
        public static final String COLUMN_NAME_IMDB_RATING = "imdb_rating";
        public static final String COLUMN_NAME_IMDB_VOTES = "imdb_votes";
        public static final String COLUMN_NAME_IMDB_ID = "imdb_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_OWNERSHIP_STATUS = "ownership";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_IMDB_ID + " TEXT PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                        COLUMN_NAME_YEAR + " TEXT, " +
                        COLUMN_NAME_CONTENT_RATING + " TEXT, " +
                        COLUMN_NAME_RELEASE_DATE + " INTEGER, " +
                        COLUMN_NAME_DURATION_MINUTES + " INTEGER, " +
                        COLUMN_NAME_GENRES + " TEXT, " +
                        COLUMN_NAME_DIRECTOR + " TEXT, " +
                        COLUMN_NAME_WRITER + " TEXT, " +
                        COLUMN_NAME_ACTORS + " TEXT, " +
                        COLUMN_NAME_PLOT + " TEXT, " +
                        COLUMN_NAME_LANGUAGES + " TEXT, " +
                        COLUMN_NAME_COUNTRY + " TEXT, " +
                        COLUMN_NAME_AWARDS + " TEXT, " +
                        COLUMN_NAME_POSTER_URL + " TEXT, " +
                        COLUMN_NAME_METASCORE + " INTEGER, " +
                        COLUMN_NAME_IMDB_RATING + " REAL, " +
                        COLUMN_NAME_IMDB_VOTES + " INTEGER, " +
                        COLUMN_NAME_TYPE + " TEXT, " +
                        COLUMN_NAME_OWNERSHIP_STATUS + " INTEGER, " +
                        "UNIQUE (" + COLUMN_NAME_IMDB_ID + ") ON CONFLICT REPLACE" +
                        " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MediaReaderContract.MediaEntry.TABLE_NAME;
    }
}
