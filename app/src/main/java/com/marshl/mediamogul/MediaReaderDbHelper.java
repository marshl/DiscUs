package com.marshl.mediamogul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class MediaReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MediaReader.db";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MediaReaderContract.MediaEntry.TABLE_NAME;

    public MediaReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MediaReaderContract.MediaEntry.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertMediaRecord(Media media) throws SQLException {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(MediaReaderContract.MediaEntry.TABLE_NAME, MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID + " = ?", new String[]{media.getImdbId()});

        ContentValues values = MediaReaderContract.getContentValuesForMedia(media);
        db.insertOrThrow(MediaReaderContract.MediaEntry.TABLE_NAME, null, values);
    }

    public Media.OwnershipType getMediaOwnershipStatus(String imdbId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MediaReaderContract.MediaEntry.COLUMN_NAME_OWNERSHIP_STATUS
        };

        String selection = MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID + " = ?";
        String[] selectionArgs = {imdbId};

        Cursor cur = db.query(
                MediaReaderContract.MediaEntry.TABLE_NAME,  // The table to query
                projection,    // The columns to return
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                null           // The sort order
        );

        if (cur.getCount() == 0) {
            return Media.OwnershipType.NOT_OWNED;
        }

        cur.moveToNext();
        Media.OwnershipType ownershipStatus = Media.OwnershipType.values()[cur.getInt(0)];
        cur.close();

        return ownershipStatus;
    }

    public ArrayList<Media> runMediaSearch(SearchParameters params) {
        if (params.getSearchType() != SearchParameters.SearchType.USER_OWNED) {
            throw new IllegalArgumentException("Search parameters must have search type of USER_OWNED");
        }

        ArrayList<Media> results = new ArrayList<Media>();

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_POSTER_URL,
        };

        String selection = "LOWER(" + MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE + ") REGEXP ?";
        String[] selectionArgs = {".*\\b" + params.getSearchText().toLowerCase() + "\\b.*"};

        String sortOrder = MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_VOTES + " DESC";

        Cursor cur = db.query(
                MediaReaderContract.MediaEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (cur.moveToNext()) {
            Media media = new Media();
            media.setImdbId(cur.getString(0));
            media.setTitle(cur.getString(1));
            media.setYear(cur.getString(2));
            media.setType(cur.getString(3));
            media.setPosterUrl(cur.getString(4));
            results.add(media);
        }
        cur.close();

        return results;
    }

    public Media getMediaDetails(String imdbId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR,
                MediaReaderContract.MediaEntry.COLUMN_NAME_CONTENT_RATING,
                MediaReaderContract.MediaEntry.COLUMN_NAME_RELEASE_DATE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_DURATION_MINUTES,
                MediaReaderContract.MediaEntry.COLUMN_NAME_GENRES,
                MediaReaderContract.MediaEntry.COLUMN_NAME_DIRECTOR,
                MediaReaderContract.MediaEntry.COLUMN_NAME_WRITER,
                MediaReaderContract.MediaEntry.COLUMN_NAME_ACTORS,
                MediaReaderContract.MediaEntry.COLUMN_NAME_PLOT,
                MediaReaderContract.MediaEntry.COLUMN_NAME_LANGUAGES,
                MediaReaderContract.MediaEntry.COLUMN_NAME_COUNTRY,
                MediaReaderContract.MediaEntry.COLUMN_NAME_AWARDS,
                MediaReaderContract.MediaEntry.COLUMN_NAME_POSTER_URL,
                MediaReaderContract.MediaEntry.COLUMN_NAME_METASCORE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_RATING,
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_VOTES,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_OWNERSHIP_STATUS,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TOTAL_SEASONS,
        };

        String selection = MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID + "= ?";
        String[] selectionArgs = {imdbId};

        Cursor cur = db.query(
                MediaReaderContract.MediaEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (cur.getCount() == 0) {
            cur.close();
            return null;
        }

        cur.moveToNext();
        Media media = new Media();
        media.setImdbId(cur.getString(0));
        media.setTitle(cur.getString(1));
        media.setYear(cur.getString(2));
        media.setContentRating(cur.getString(3));
        media.setReleaseDate(new Date(cur.getLong(4)));
        media.setDurationMinutes(cur.getInt(5));
        media.setGenres(cur.getString(6));
        media.setDirector(cur.getString(7));
        media.setWriter(cur.getString(8));
        media.setActors(cur.getString(9));
        media.setPlot(cur.getString(10));
        media.setLanguages(cur.getString(11));
        media.setCountry(cur.getString(12));
        media.setAwards(cur.getString(13));
        media.setPosterUrl(cur.getString(14));
        media.setMetascore(cur.getInt(15));
        media.setImdbRating(cur.getFloat(16));
        media.setImdbVotes(cur.getInt(17));
        media.setType(cur.getString(18));
        media.setOwnershipStatus(Media.OwnershipType.values()[cur.getInt(19)]);
        media.setTotalSeasons(cur.getInt(20));

        cur.close();
        return media;
    }

}
