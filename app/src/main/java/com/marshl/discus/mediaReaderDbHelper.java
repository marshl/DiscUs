package com.marshl.discus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MediaReaderDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MediaReaderContract.MediaEntry.TABLE_NAME + " (" +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_REFERENCE + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_EPISODE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    MediaReaderContract.MediaEntry.COLUMN_NAME_DIRECTOR + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MediaReaderContract.MediaEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MediaReader.db";

    public MediaReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
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

    public void insertMediaRecord(Media media) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_REFERENCE, media.getReference());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_NAME, media.getName());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_TITLE, media.getTitle());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_NAME, media.getName());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_EPISODE_TITLE, media.getEpisodeTitle());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_DESCRIPTION, media.getDescription());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_YEAR, media.getYear());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_TYPE, media.getType());
        values.put(MediaReaderContract.MediaEntry.COLUMN_NAME_DIRECTOR, media.getDirector());

        db.insert(MediaReaderContract.MediaEntry.TABLE_NAME, null, values);
    }
}
