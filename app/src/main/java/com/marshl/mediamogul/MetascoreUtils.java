package com.marshl.mediamogul;

import android.content.res.Resources;

public class MetascoreUtils {

    private static final int UNFAVORABLE_COLOR = 0xFFFF0000;
    private static final int MIXED_COLOR = 0xFFFFCC33;
    private static final int FAVORABLE_COLOR = 0xFF66CC33;


    private static final int UNFAVORABLE_TEXT_COLOR = 0xFFFFFFFF;
    private static final int MIXED_TEXT_COLOR = 0xFFFFFFFF;
    private static final int FAVORABLE_TEXT_COLOR = 0xFFFFFFFF;

    private MetascoreUtils() {
    }

    public static int getMetascoreColor(int metascore, boolean isGame) {

        if (isGame) {
            if (metascore < 50) {
                return UNFAVORABLE_COLOR;
            } else if (metascore < 75) {
                return MIXED_COLOR;
            } else {
                return FAVORABLE_COLOR;
            }
        } else {
            if (metascore < 40) {
                return UNFAVORABLE_COLOR;
            } else if (metascore < 61) {
                return MIXED_COLOR;
            } else {
                return FAVORABLE_COLOR;
            }
        }
    }

    public static int getMetascoreTextColor(int metascore, boolean isGame) {

        if (isGame) {
            if (metascore < 50) {
                return UNFAVORABLE_TEXT_COLOR;
            } else if (metascore < 75) {
                return MIXED_TEXT_COLOR;
            } else {
                return FAVORABLE_TEXT_COLOR;
            }
        } else {
            if (metascore < 40) {
                return UNFAVORABLE_TEXT_COLOR;
            } else if (metascore < 61) {
                return MIXED_TEXT_COLOR;
            } else {
                return FAVORABLE_TEXT_COLOR;
            }
        }
    }

    public String getMetaScoreMeaning(int metascore, boolean isGame, Resources resources) {
        if (isGame) {
            if (metascore < 20) {
                return resources.getString(R.string.metascore_overwhelming_dislike);
            } else if (metascore < 50) {
                return resources.getString(R.string.metascore_generally_unfavorable_reviews);
            } else if (metascore < 75) {
                return resources.getString(R.string.metascore_mixed_reviews);
            } else if (metascore < 90) {
                return resources.getString(R.string.metascore_generally_favorable_reviews);
            } else {
                return resources.getString(R.string.metascore_universal_acclaim);
            }
        } else {
            if (metascore < 20) {
                return resources.getString(R.string.metascore_overwhelming_dislike);
            } else if (metascore < 40) {
                return resources.getString(R.string.metascore_generally_unfavorable_reviews);
            } else if (metascore < 61) {
                return resources.getString(R.string.metascore_mixed_reviews);
            } else if (metascore < 81) {
                return resources.getString(R.string.metascore_generally_favorable_reviews);
            } else {
                return resources.getString(R.string.metascore_universal_acclaim);
            }
        }
    }

}
