package com.marshl.discus;

import android.os.Parcel;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang3.Range.is;
import static org.junit.Assert.*;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ParcelableMediaAndroidUnitTest {

    public static final int TEST_METASCORE = 56;

    @Test
    public void logHistory_ParcelableWriteRead() {
        Media media = new Media();
        media.setMetascore(TEST_METASCORE);

        ParcelableMedia test = new ParcelableMedia(media);

        Parcel parcel = Parcel.obtain();
        test.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ParcelableMedia createdFromParcel = (ParcelableMedia) ParcelableMedia.CREATOR.createFromParcel(parcel);
        assertEquals(test.getMetascore(), createdFromParcel.getMetascore());
    }
}
