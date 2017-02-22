package com.marshl.discus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.marshl.discus.MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE;

public class MediaReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 23;
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

        ContentValues values = MediaReaderContract.getContentValuesForMedia(media);
        db.insertOrThrow(MediaReaderContract.MediaEntry.TABLE_NAME, null, values);
    }

    public boolean isMediaSavedToDatabase(String reference) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID,
                COLUMN_NAME_TITLE
        };

        String selection = MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID + " = ?";
        String[] selectionArgs = {reference};

        String sortOrder = COLUMN_NAME_TITLE + " DESC";

        Cursor c = db.query(
                MediaReaderContract.MediaEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int rowCount = c.getCount();
        c.close();
        return rowCount > 0;
    }

    public List<Media> runMediaSearch(SearchParameters params) {
        if (params.getSearchType() != SearchParameters.SearchType.USER_OWNED) {
            throw new IllegalArgumentException("Search parameters must have search type of USER_OWNED");
        }

        List<Media> results = new ArrayList<Media>();

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MediaReaderContract.MediaEntry.COLUMN_NAME_IMDB_ID,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR,
                MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE,
                MediaReaderContract.MediaEntry.COLUMN_NAME_POSTER_URL,
        };

        String selection = "LOWER(" + MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE + ") REGEXP LOWER(?)";
        String[] selectionArgs = {"\\b" + params.getSearchText() + "\\b"};

        String sortOrder = MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE;

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
}
