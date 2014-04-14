package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subcategory extends DBObject {
	private long   categoryId;
	private String name;
	private String description;
	
	public Subcategory() {
	    
	}
	
	public long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(categoryId);
        dest.writeString(name);
        if (description == null) {
            dest.writeString("");
        }
        else {
            dest.writeString(description);
        }
    }
    
    private Subcategory(Parcel in) {
        super(in);
        categoryId = in.readLong();
        name = in.readString();
        description = in.readString();
        if (description.length() == 0) {
            description = null;
        }
    }
	
    public static final Parcelable.Creator<Subcategory> CREATOR = new Creator<Subcategory>() {
        
        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
        
        @Override
        public Subcategory createFromParcel(Parcel source) {
            return new Subcategory(source);
        }
    };
	
}
