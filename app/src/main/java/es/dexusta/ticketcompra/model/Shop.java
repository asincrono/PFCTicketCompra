package es.dexusta.ticketcompra.model;

import java.math.BigDecimal;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.util.Installation;

public class Shop extends ReplicatedDBObject {
    public static final String KIND_NAME          = "shop";

    public static final String PROPERTY_CHAIN_ID  = "chain_id";
    public static final String PROPERTY_TOWN_ID   = "town_id";
    public static final String PROPERTY_TOWN_NAME = "town_name";
    // public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_LATITUDE  = "latitude";
    public static final String PROPERTY_LONGITUDE = "longitude";
    public static final String PROPERTY_ADDRESS   = "address";

    private long               chainId;
    private long               townId;
    private String             townName;
    // private String name;
    private double             latitude;
    private double             longitude;

    private String             address;                         // ???

    public Shop() {

    }

    public Shop(Context context) {
        super(context);
    }

    public Shop(String installation) {
        super(installation);
    }

    public Shop(DBOCloudEntity entity) {
        super(entity);
        chainId = Long.parseLong((String) entity.get(PROPERTY_CHAIN_ID));
        townId = Long.parseLong((String) entity.get(PROPERTY_TOWN_ID));
        townName = (String) entity.get(PROPERTY_TOWN_NAME);
        latitude = ((BigDecimal) entity.get(PROPERTY_LATITUDE)).doubleValue();
        longitude = ((BigDecimal) entity.get(PROPERTY_LONGITUDE)).doubleValue();
        address = (String) entity.get(PROPERTY_ADDRESS);
    }

    public Shop(CloudEntity entity) {
        super(entity);
        chainId = Long.parseLong((String) entity.get(PROPERTY_CHAIN_ID));
        townId = Long.parseLong((String) entity.get(PROPERTY_TOWN_ID));
        townName = (String) entity.get(PROPERTY_TOWN_NAME);
        latitude = ((BigDecimal) entity.get(PROPERTY_LATITUDE)).doubleValue();
        longitude = ((BigDecimal) entity.get(PROPERTY_LONGITUDE)).doubleValue();
        address = (String) entity.get(PROPERTY_ADDRESS);
    }

    @Override
    public String getKindName() {
        return KIND_NAME;
    }
    
    @Override
    public String getTableName() {
        return DBHelper.TBL_SHOP;
    }

    @Override
    public DBOCloudEntity getEntity(Context context) {
        long id = getId();
        String univ_id = getUniversalId();
        if (univ_id == null && id > 0) {
            setUniversalId(Installation.id(context) + id);
        }

        DBOCloudEntity entity = super.getEntity(context);

        entity.put(PROPERTY_CHAIN_ID, Long.toString(chainId));
        entity.put(PROPERTY_TOWN_ID, Long.toString(townId));
        entity.put(PROPERTY_TOWN_NAME, townName);
        entity.put(PROPERTY_LATITUDE, Double.valueOf(latitude));
        entity.put(PROPERTY_LONGITUDE, Double.valueOf(longitude));
        entity.put(PROPERTY_ADDRESS, address);

        return entity;
    }

    @Override
    public ContentValues getValues() {
        ContentValues cv = null;
    
        cv = new ContentValues();
        long id = getId();
        if (id > 0) {
            cv.put(DBHelper.T_SHOP_ID, id);
        }
    
        String univId = getUniversalId();
        if (univId != null) {
            cv.put(DBHelper.T_SHOP_UNIVERSAL_ID, univId);
        }
        cv.put(DBHelper.T_SHOP_CHAIN_ID, getChainId());
        cv.put(DBHelper.T_SHOP_TOWN_ID, getTownId());
        cv.put(DBHelper.T_SHOP_TOWN_NAME, getTownName());
        // cv.put(NAME, data.getName());
        cv.put(DBHelper.T_SHOP_LATIT, getLatitude());
        cv.put(DBHelper.T_SHOP_LONGT, getLongitude());
        cv.put(DBHelper.T_SHOP_ADDR, getAddress());
        // Less confusing than use Boolean as there is no getBoolean from
        // cursor.
        cv.put(DBHelper.T_SHOP_UPDATED, isUpdated() ? 1 : 0);
    
        return cv;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public long getTownId() {
        return townId;
    }

    public void setTownId(long townId) {
        this.townId = townId;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getTownName() {
        return townName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // public String getName() {
    // return name;
    // }
    //
    // public void setName(String name) {
    // this.name = name;
    // }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("\n").append(KIND_NAME);
        text.append("\n").append("id : ").append(getId());
        text.append("\n").append("univId : ").append(getUniversalId());
        text.append("\n").append("townId : ").append(getTownId());
        text.append("\n").append("address : ").append(getAddress()).append("\n");

        return text.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(chainId);
        dest.writeLong(townId);
        dest.writeString(townName == null ? "" : townName);
        // dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address == null ? "" : address);
        // if (address == null) {
        // dest.writeString("");
        // } else {
        // dest.writeString(address);
        // }
    }

    private Shop(Parcel in) {
        super(in);
        chainId = in.readLong();
        townId = in.readLong();
        townName = in.readString();
        // name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        if (address.length() == 0) {
            address = null;
        }
    }

    public static final Parcelable.Creator<Shop> CREATOR = new Creator<Shop>() {

                                                             @Override
                                                             public Shop[] newArray(int size) {
                                                                 return new Shop[size];
                                                             }

                                                             @Override
                                                             public Shop createFromParcel(
                                                                     Parcel source) {
                                                                 return new Shop(source);
                                                             }
                                                         };
}
