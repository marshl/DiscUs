package com.marshl.mediamogul;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ParcelableMedia extends Media implements Parcelable {

    public static final String MEDIA_PARCEL_NAME = "media";

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

        long longDate = in.readLong();
        this.setReleaseDate(longDate != -1 ? new Date(longDate) : null);

        int duration = in.readInt();
        this.setDurationMinutes(duration != -1 ? duration : null);
        this.setGenres(in.readString());
        this.setDirector(in.readString());
        this.setWriter(in.readString());
        this.setActors(in.readString());
        this.setPlot(in.readString());
        this.setLanguages(in.readString());
        this.setCountry(in.readString());
        this.setAwards(in.readString());
        this.setPosterUrl(in.readString());
        this.setMetascore(in.readInt());

        float imdbRating = in.readFloat();
        this.setImdbRating(imdbRating != -1 ? imdbRating : null);

        int votes = in.readInt();
        this.setImdbVotes(votes != -1 ? votes : null);
        this.setImdbId(in.readString());
        this.setType(in.readString());

        int ownershipStatus = in.readInt();
        this.setOwnershipStatus(Media.OwnershipType.values()[ownershipStatus]);

        int totalSeasons = in.readInt();
        this.setTotalSeasons(totalSeasons == -1 ? null : totalSeasons);
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
        parcel.writeLong(this.getReleaseDate() != null ? this.getReleaseDate().getTime() : -1);
        parcel.writeInt(this.getDurationMinutes() != null ? this.getDurationMinutes() : -1);
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
        parcel.writeFloat(this.getImdbRating() != null ? this.getImdbRating() : -1);
        parcel.writeInt(this.getImdbVotes() != null ? this.getImdbVotes() : -1);
        parcel.writeString(this.getImdbId());
        parcel.writeString(this.getType());
        parcel.writeInt(this.getOwnershipStatus().ordinal());
        parcel.writeInt(this.getTotalSeasons() != null ? this.getTotalSeasons() : -1);
    }
}
