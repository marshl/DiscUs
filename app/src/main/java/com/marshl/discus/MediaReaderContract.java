package com.marshl.discus;

import android.provider.BaseColumns;

public final class MediaReaderContract {
    private MediaReaderContract() {
    }

    public static class MediaEntry implements BaseColumns {
        public static final String TABLE_NAME = "media";

        public static final String COLUMN_NAME_REFERENCE = "reference";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EPISODE_TITLE = "episode_title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DIRECTOR = "director";
    }


}
