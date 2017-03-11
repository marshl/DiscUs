package com.marshl.mediamogul;

import android.os.Parcel;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang3.Range.is;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ParcelableMediaAndroidUnitTest {

    public static final int TEST_METASCORE = 56;
    public static final int TEST_IMDB_VOTES = 23000;
    public static final String TEST_IMDB_ID = "oalakjsdas";

    @Test
    public void logHistory_ParcelableWriteRead() {
        Media media = new Media();
        media.setMetascore(TEST_METASCORE);
        media.setImdbVotes(TEST_IMDB_VOTES);
        media.setImdbId(TEST_IMDB_ID);

        ParcelableMedia test = new ParcelableMedia(media);

        Parcel parcel = Parcel.obtain();
        test.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ParcelableMedia createdFromParcel = (ParcelableMedia) ParcelableMedia.CREATOR.createFromParcel(parcel);
        assertEquals(test.getMetascore(), createdFromParcel.getMetascore());
        assertEquals(test.getImdbVotes(), createdFromParcel.getImdbVotes());
        assertEquals(test.getImdbId(), createdFromParcel.getImdbId());
    }
}
