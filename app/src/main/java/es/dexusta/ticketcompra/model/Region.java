package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Region extends DBObject {
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() {

        @Override
        public Region createFromParcel(Parcel source) {
            return new Region(source);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
    private String name;

    public Region() {

    }



    private Region(Parcel in) {
        super(in);
        name = in.readString();
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
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
    }
}
