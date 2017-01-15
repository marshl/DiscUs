package com.marshl.discus;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ParcelableMedia extends Media implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableMedia createFromParcel(Parcel in) {
            return new ParcelableMedia(in);
        }

        public ParcelableMedia[] newArray(int size) {
            return new ParcelableMedia[size];
        }
    };

    public ParcelableMedia(Media media) {
        super(media);
    }

    public ParcelableMedia(Parcel in) {
        super();

        this.setTitle(in.readString());
        this.setYear(in.readString());
        this.setContentRating(in.readString());
        this.setReleaseDate(new Date(in.readLong()));
        this.setDurationMinutes(in.readInt());
        this.setGenres(in.readString());
        this.setDirector(in.readString());
        this.setWriter(in.readString());
        this.setActors(in.readString());
        this.setPlot(in.readString());
        this.setLanguages(in.readString());
        this.setCountry(in.readString());
        this.setPosterUrl(in.readString());
        this.setMetascore(in.readInt());
        this.setImdbRating(in.readFloat());
        this.setImdbVotes(in.readInt());
        this.setType(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getTitle());
        parcel.writeString(this.getYear());
        parcel.writeString(this.getContentRating());
        parcel.writeLong(this.getReleaseDate() != null ? this.getReleaseDate().getTime() : 0);
        parcel.writeInt(this.getDurationMinutes());
        parcel.writeString(this.getGenres());
        parcel.writeString(this.getDirector());
        parcel.writeString(this.getWriter());
        parcel.writeString(this.getActors());
        parcel.writeString(this.getPlot());
        parcel.writeString(this.getLanguages());
        parcel.writeString(this.getCountry());
        parcel.writeString(this.getAwards());
        parcel.writeString(this.getPosterUrl());
        parcel.writeInt(this.getMetascore());
        parcel.writeFloat(this.getImdbRating());
        parcel.writeInt(this.getImdbVotes());
        parcel.writeString(this.getImdbId());
        parcel.writeString(this.getType());
    }
}
