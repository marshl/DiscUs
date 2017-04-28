package com.marshl.mediamogul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class MediaReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MediaReader.db";

    public static final String MEDIA_TABLE_NAME = "media";
    public static final String MEDIA_TABLE_IMDB_ID_COLUMN_NAME = "imdb_id";
    public static final String MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME = "ownership_status";

    public static final String MEDIA_DETAILS_TABLE_NAME = "media_details";
    public static final String MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME = "imdb_id";
    public static final String MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME = "key_field";
    public static final String MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME = "value_field";

    public MediaReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MEDIA_TABLE_NAME + "( " +
                MEDIA_TABLE_IMDB_ID_COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL, " +
                MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME + " INTEGER NOT NULL, " +
                " UNIQUE (" + MEDIA_TABLE_IMDB_ID_COLUMN_NAME + ") ON CONFLICT REPLACE" +
                " )"
        );

        db.execSQL("CREATE TABLE " + MEDIA_DETAILS_TABLE_NAME + "( " +
                MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " TEXT NOT NULL, " +
                MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " TEXT NOT NULL, " +
                MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + ", " + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + ") ON CONFLICT REPLACE, " +
                " FOREIGN KEY(" + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + ") REFERENCES " + MEDIA_TABLE_NAME + "(" + MEDIA_TABLE_IMDB_ID_COLUMN_NAME + ") ON DELETE CASCADE" +
                " )"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MEDIA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEDIA_DETAILS_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertMediaRecord(Media media) throws SQLException {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(MEDIA_TABLE_NAME, MEDIA_TABLE_IMDB_ID_COLUMN_NAME + " = ?", new String[]{media.getImdbId()});

        ContentValues values = new ContentValues();
        values.put(MEDIA_TABLE_IMDB_ID_COLUMN_NAME, media.getImdbId());
        values.put(MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME, media.getOwnershipStatus());
        db.insertOrThrow(MEDIA_TABLE_NAME, null, values);

        for (Map.Entry<String, String> entry : media.getElementMap().entrySet()) {
            ContentValues detailValues = new ContentValues();
            detailValues.put(MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME, media.getImdbId());
            detailValues.put(MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME, entry.getKey());
            detailValues.put(MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME, entry.getValue());
            db.insertOrThrow(MEDIA_DETAILS_TABLE_NAME, null, detailValues);
        }

        db.close();
    }

    public int getMediaOwnershipStatus(String imdbId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME
        };

        String selection = MEDIA_TABLE_IMDB_ID_COLUMN_NAME + " = ?";
        String[] selectionArgs = {imdbId};

        Cursor cur = db.query(
                MEDIA_TABLE_NAME,  // The table to query
                projection,    // The columns to return
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                null           // The sort order
        );

        if (cur.getCount() == 0) {
            cur.close();
            db.close();
            return Media.OWNERSHIP_NOT_OWNED;
        }

        cur.moveToNext();
        int ownershipStatus = cur.getInt(0);
        cur.close();
        db.close();

        return ownershipStatus;
    }

    public ArrayList<Media> runMediaSearch(SearchParameters params) {
        if (params.getSearchType() != SearchParameters.SearchType.USER_OWNED && params.getSearchType() != SearchParameters.SearchType.ON_WISHLIST) {
            throw new IllegalArgumentException("Search parameters must have search type of USER_OWNED");
        }

        ArrayList<Media> results = new ArrayList<Media>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME + ", " +
                        " media." + MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME + ", " +
                        " title." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + ", " +
                        " year." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + ", " +
                        " type." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + ", " +
                        " poster." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME +
                        " FROM " + MEDIA_TABLE_NAME + " media " +
                        " JOIN " + MEDIA_DETAILS_TABLE_NAME + " title ON title." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME +
                        " AND title." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " = '" + Media.TITLE_KEY + "'" +

                        " LEFT JOIN " + MEDIA_DETAILS_TABLE_NAME + " year ON year." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME +
                        " AND year." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " = '" + Media.YEAR_KEY + "'" +

                        " LEFT JOIN " + MEDIA_DETAILS_TABLE_NAME + " type ON type." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME +
                        " AND type." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " = '" + Media.TYPE_KEY + "'" +

                        " LEFT JOIN " + MEDIA_DETAILS_TABLE_NAME + " poster ON poster." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME +
                        " AND poster." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " = '" + Media.POSTER_URL_KEY + "'" +

                        " LEFT JOIN " + MEDIA_DETAILS_TABLE_NAME + " votes ON votes." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME +
                        " AND votes." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " = '" + Media.IMDB_VOTES_KEY + "'" +

                        " WHERE LOWER(title." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + ") REGEXP ? " +
                        " AND media." + MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME + " = ?" +
                        " ORDER BY CAST(votes. " + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + " AS NUMBER) ASC, title." + MEDIA_DETAIL_TABLE_VALUE_COLUMN_NAME + " ASC",
                new String[]{
                        ".*\\b" + params.getSearchText().toLowerCase() + "\\b.*",
                        "" + (params.getSearchType() == SearchParameters.SearchType.USER_OWNED ? Media.OWNERSHIP_OWNED : Media.OWNERSHIP_ON_WISHLIST)
                }
        );

        while (cur.moveToNext()) {

            String imdbId = cur.getString(0);
            int ownershipStatus = cur.getInt(1);
            Media media = new Media(imdbId, ownershipStatus);
            media.setValue(Media.TITLE_KEY, cur.getString(2));
            media.setValue(Media.YEAR_KEY, cur.getString(3));
            media.setValue(Media.TYPE_KEY, cur.getString(4));
            media.setValue(Media.POSTER_URL_KEY, cur.getString(5));
            results.add(media);
        }
        cur.close();
        db.close();

        return results;
    }

    public Media getMediaDetails(String imdbId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery(
                "SELECT media." + MEDIA_TABLE_OWNERSHIP_STATUS_COLUMN_NAME +
                        " FROM " + MEDIA_TABLE_NAME + " media " +
                        " WHERE media." + MEDIA_TABLE_IMDB_ID_COLUMN_NAME + " = ?",
                new String[]{imdbId}
        );

        if (cur.getCount() == 0) {
            cur.close();
            return null;
        }

        cur.moveToNext();
        int ownershipStatus = cur.getInt(0);
        Media media = new Media(imdbId, ownershipStatus);
        cur.close();

        // Now get the key value pairs
        cur = db.rawQuery(
                "SELECT det." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " key, " +
                        " det." + MEDIA_DETAIL_TABLE_KEY_COLUMN_NAME + " val " +
                        " FROM " + MEDIA_DETAILS_TABLE_NAME + " det " +
                        " WHERE det." + MEDIA_DETAIL_TABLE_IMDB_ID_COLUMN_NAME + " = ?",
                new String[]{imdbId}
        );

        while (cur.moveToNext()) {
            String key = cur.getString(0);
            String value = cur.getString(1);

            media.setValue(key, value);
        }

        return media;
    }

}
