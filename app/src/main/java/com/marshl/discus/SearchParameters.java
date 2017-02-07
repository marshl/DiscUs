package com.marshl.discus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Liam on 14/01/2017.
 */

public class SearchParameters implements Parcelable {

    public static final String SEARCH_PARAM_PARCEL_NAME = "PARAMS";

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SearchParameters createFromParcel(Parcel in) {
            return new SearchParameters(in);
        }

        public SearchParameters[] newArray(int size) {
            return new SearchParameters[size];
        }
    };
    private String searchText;

    public SearchParameters() {
        super();
    }

    public SearchParameters(Parcel in) {
        super();
        this.searchText = in.readString();
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.searchText);
    }
}
