package com.marshl.discus;

public class MetascoreUtils {

    private static final int UNFAVORABLE_COLOR = 0xFF0000;
    private static final int MIXED_COLOR = 0xFFCC33;
    private static final int FAVORABLE_COLOR = 0x66CC33;

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

    public static String getMetaScoreMeaning(int metascore, boolean isGame)
    {
        if (isGame) {
            if (metascore < 20) {
                return "Overwhelming Dislike";
            } else if (metascore < 50) {
                return "Generally Unfavorable Reviews";
            } else if (metascore < 75) {
                return "Mixed or Average Reviews";
            } else if (metascore < 90) {
                return "Generally Favorable Reviews";
            } else {
                return "Universal Acclaim";
            }
        } else {
            if (metascore < 20) {
                return "Overwhelming Dislike";
            } else if (metascore < 40) {
                return "Generally Unfavorable Reviews";
            } else if (metascore < 61) {
                return "Mixed or Average Reviews";
            } else if (metascore < 81) {
                return "Generally Favorable Reviews";
            } else {
                return "Universal Acclaim";
            }
        }
    }

}
