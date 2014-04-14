package es.dexusta.ticketcompra.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.util.Installation;

public abstract class ReplicatedDBObject extends DBObject implements Persistent {
    public static final String PROPERTY_UNIVERSAL_ID = "universal_id";

    private boolean            mUpdated              = false;
    private String             mUniversalId;
    private String             mInstallation;

    public ReplicatedDBObject() {

    }

    public ReplicatedDBObject(Context context) {
        mInstallation = Installation.id(context);
    }

    public ReplicatedDBObject(String installation) {
        mInstallation = installation;
    }

    public ReplicatedDBObject(DBOCloudEntity entity) {
        if (entity.getKindName().equals(getKindName())) {
            mUniversalId = (String) entity.get(PROPERTY_UNIVERSAL_ID);
            mUpdated = (entity.getId() != null) ? true : false;
        }
        else {
            throw new IllegalArgumentException(
                    "The kind of the entity used in the constructor must be" + getKindName());
        }

    }

    public ReplicatedDBObject(CloudEntity entity) {
        if (entity.getKindName().equals(getKindName())) {
            mUniversalId = (String) entity.get(PROPERTY_UNIVERSAL_ID);
            mUpdated = (entity.getId() != null) ? true : false;
        }
        else {
            throw new IllegalArgumentException(
                    "The kind of the entity used in the constructor must be" + getKindName());
        }
    }

    public ReplicatedDBObject(Parcel in) {
        super(in);
        mUpdated = (in.readInt() > 0) ? true : false;
        mUniversalId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        // if mUpdated == true write 1, else write 0;
        dest.writeInt((mUpdated) ? 1 : 0);
        dest.writeString(mUniversalId);
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        if (mInstallation != null) {
            mUniversalId = mInstallation + id;
        }
    }

    public void setUniversalId(String universalId) {
        mUniversalId = universalId;
    }

    public String getUniversalId() {
        return mUniversalId;
    }
    
    public String getInstallation() {
        return mInstallation;
    }
    

    public abstract String getKindName();
    
    public abstract String getTableName();

    @Override
    public DBOCloudEntity getEntity(Context context) {
        DBOCloudEntity entity = new DBOCloudEntity(getKindName());
        entity.put(PROPERTY_UNIVERSAL_ID, mUniversalId);
        return entity;
    }
    
    public abstract ContentValues getValues();

    public void setUpdated(boolean updated) {
        mUpdated = updated;
    }

    public boolean isUpdated() {
        return mUpdated;
    }

}
