package com.marshl.mediamogul;

import android.os.Parcel;
import android.os.Parcelable;

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
    private SearchType searchType;
    public SearchParameters() {
        super();
    }

    public SearchParameters(Parcel in) {
        super();
        this.searchText = in.readString();
        this.searchType = SearchType.values()[in.readInt()];
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public SearchType getSearchType() {
        return this.searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.searchText);
        parcel.writeInt(this.searchType.ordinal());
    }

    public enum SearchType {
        USER_OWNED,
        NOT_USER_OWNED,
        BOTH
    }
}
