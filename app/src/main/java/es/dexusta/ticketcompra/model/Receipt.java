package es.dexusta.ticketcompra.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudEntity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import es.dexusta.ticketcompra.util.Installation;

public class Receipt extends ReplicatedDBObject {
    public static final String KIND_NAME             = "receipt";

    public static final String PROPERTY_SHOP_ID      = "shop_id";
    public static final String PROPERTY_SHOP_UNIV_ID = "shop_universal_id";
    public static final String PROPERTY_TOTAL        = "total";
    public static final String PROPERTY_TIMESTAMP    = "timestamp";
    public static final Parcelable.Creator<Receipt> CREATOR = new Creator<Receipt>() {

                                                                @Override
                                                                public Receipt[] newArray(int size) {
                                                                    return new Receipt[size];
                                                                }

                                                                @Override
                                                                public Receipt createFromParcel(
                                                                        Parcel source) {
                                                                    return new Receipt(source);
                                                                }
                                                            };
    private long               shopId;
    private String             shopUnivId;
    private int                total;
    private DateTime           timestamp;

    public Receipt() {
        timestamp = new DateTime(false, System.currentTimeMillis(), 0);
    }

    public Receipt(Context context) {
        super(context);
        timestamp = new DateTime(false, System.currentTimeMillis(), 0);
    }

    public Receipt(String installation) {
        super(installation);
        timestamp = new DateTime(false, System.currentTimeMillis(), 0);
    }

    public Receipt(CloudEntity entity) {
        super(entity);
        shopUnivId = (String) entity.get(PROPERTY_SHOP_UNIV_ID);

        Object aux_total = entity.get(PROPERTY_TOTAL);
        if (aux_total instanceof String) {
            total = Integer.parseInt((String) aux_total);
        } else if (aux_total instanceof BigDecimal) {
            total = ((BigDecimal) aux_total).intValue();
        }

        timestamp = DateTime.parseRfc3339((String) entity.get(PROPERTY_TIMESTAMP));
    }

    private Receipt(Parcel in) {
        super(in);
        shopId = in.readLong();
        shopUnivId = in.readString();
        total = in.readInt();
        timestamp = new DateTime(in.readLong());
    }

    @Override
    public String getKindName() {
        return KIND_NAME;
    }

    @Override
    public String getTableName() {
        return DBHelper.TBL_RECEIPT;
    }

    @Override
    public DBOCloudEntity getEntity(Context context) {
        long id = getId();
        String installation = Installation.id(context);

        String univ_id = getUniversalId();
        if (univ_id == null && id > 0) {
            setUniversalId(Installation.id(context) + id);
        }

        DBOCloudEntity entity = super.getEntity(context);

        entity.put(PROPERTY_SHOP_ID, Long.toString(shopId));

        if (shopUnivId == null) {
            shopUnivId = installation + shopId;
        }
        entity.put(PROPERTY_SHOP_UNIV_ID, shopUnivId);
        entity.put(PROPERTY_TOTAL, Integer.valueOf(total));
        entity.put(PROPERTY_TIMESTAMP, timestamp); // Se guardará la cadena con
                                                   // la fecha.

        return entity;
    }

    @Override
    public ContentValues getValues() {
        ContentValues cv = null;

        cv = new ContentValues();
        long id = getId();
        if (id > 0) {
            cv.put(DBHelper.T_RECPT_ID, id);
        }

        String univ_id = getUniversalId();
        if (univ_id != null) {
            cv.put(DBHelper.T_RECPT_UNIVERSAL_ID, univ_id);
        }
        cv.put(DBHelper.T_RECPT_SHOP_ID, shopId);
        cv.put(DBHelper.T_RECPT_SHOP_UNIV_ID, shopUnivId);
        cv.put(DBHelper.T_RECPT_TOTAL, total);
        cv.put(DBHelper.T_RECPT_TIMESTAMP, getTimestampRfc3339());
        cv.put(DBHelper.T_RECPT_UPDATED, isUpdated() ? 1 : 0);

        return cv;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }
    
    public String getShopUnivId() {
        return shopUnivId;
    }
    
    public void setShopUnivId(String univId) {
        shopUnivId = univId;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }

    public String getTimestampRfc3339() {
        return timestamp.toStringRfc3339();
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = new DateTime(false, timestamp.getTimeInMillis(), 0);
    }

    public void setTimestamp(String strTimestamp) {
        timestamp = DateTime.parseRfc3339(strTimestamp);
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = new DateTime(false, timestamp, 0);
    }

    public Date getDateTimestamp() {
        return new Date(timestamp.getValue());
    }

    // Devuelve el timestamp en milisegundos (tiempo local).
    public long getMillisTimestamp() {
        return timestamp.getValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(KIND_NAME);
        text.append("\n").append("id: ").append(getId());
        text.append("\n").append("univId: ").append(getUniversalId());
        text.append("\n").append("shopId: ").append(shopId);
        text.append("\n").append("shopUnivId: ").append(shopUnivId);
        text.append("\n").append("total: ").append(total);
        text.append("\n").append("timestamp: ").append(timestamp.toString()).append("\n");

        return text.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(shopId);
        dest.writeString(shopUnivId);
        dest.writeInt(total);
        dest.writeLong(timestamp.getValue());
    }
}
