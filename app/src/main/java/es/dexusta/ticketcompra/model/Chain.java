package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chain extends DBObject {
	private String name;
	private String code;

	public Chain() {
	    
	}
	
	private Chain(Parcel in) {
        super(in);
        name = in.readString();
        code = in.readString();
        if (code.length() == 0) {
            code = null;
        }
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
	    return name;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        if (code == null) {
            dest.writeString("");
        }
        else {
            dest.writeString(code);
        }   
    }
    
    public static final Parcelable.Creator<Chain> CREATOR = new Parcelable.Creator<Chain>() {

        @Override
        public Chain createFromParcel(Parcel source) {            
            return new Chain(source);
        }

        @Override
        public Chain[] newArray(int size) {            
            return new Chain[size];
        }
    };
}
