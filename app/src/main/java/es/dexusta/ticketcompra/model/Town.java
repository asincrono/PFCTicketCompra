package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Town extends DBObject {
    public static final Parcelable.Creator<Town> CREATOR = new Parcelable.Creator<Town>() {

        @Override
        public Town createFromParcel(Parcel source) {
            return new Town(source);
        }

        @Override
        public Town[] newArray(int size) {
            return new Town[size];
        }
    };
    private long subregionId;
    private String name;
    
    public Town() {

    }
    
    private Town(Parcel in) {
        super(in);
        subregionId = in.readLong();
        name = in.readString();
    }

    public long getSubregionId() {
        return subregionId;
    }

    public void setSubregionId(long subregionId) {
        this.subregionId = subregionId;
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
        dest.writeLong(subregionId);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
