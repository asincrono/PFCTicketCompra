package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category extends DBObject {
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
	private String name;
	private String description;
	
	public Category() {

	}

    private Category(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
        if (description.length() == 0) {
            description = null;
        }
    }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
        if (description == null) {
            dest.writeString("");
        }
        else {
            dest.writeString(description);
        }
    }
	
}
