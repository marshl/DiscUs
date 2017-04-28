package com.marshl.mediamogul;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

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
        this.setImdbId(in.readString());
        this.setOwnershipStatus(in.readInt());

        int tupleCount = in.readInt();
        for (int i = 0; i < tupleCount; ++i) {
            String key = in.readString();
            String value = in.readString();
            this.setValue(key, value);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getImdbId());
        parcel.writeInt(this.getOwnershipStatus());

        parcel.writeInt(this.getElementMap().size());
        for (Map.Entry<String, String> entry : this.getElementMap().entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }
}
