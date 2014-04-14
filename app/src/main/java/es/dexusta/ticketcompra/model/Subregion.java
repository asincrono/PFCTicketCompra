package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subregion extends DBObject {
    public static final Parcelable.Creator<Subregion> CREATOR = new Parcelable.Creator<Subregion>() {

        @Override
        public Subregion createFromParcel(Parcel source) {
            return new Subregion(source);
        }

        @Override
        public Subregion[] newArray(int size) {
            return new Subregion[size];
        }
    };
    private String name;
    private long regionId;

    public Subregion() {

    }

    private Subregion(Parcel in) {
        super(in);
        regionId = in.readLong();
        name = in.readString();
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(regionId);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
