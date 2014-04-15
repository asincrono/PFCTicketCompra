package es.dexusta.ticketcompra.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.cloud.backend.android.CloudEntity;

import java.math.BigDecimal;

import es.dexusta.ticketcompra.util.Installation;

public class Total extends ReplicatedDBObject {
    public static final String   KIND_NAME                = "total";
    public static final String   PROPERTY_RECEIPT_ID      = "receipt_id";
    public static final String   PROPERTY_RECEIPT_UNIV_ID = "receipt_universal_id";
    public static final String   PROPERTY_VALUE           = "value";
    public static final Parcelable.Creator<Total> CREATOR = new Creator<Total>() {

                                                              @Override
                                                              public Total[] newArray(int size) {
                                                                  return new Total[size];
                                                              }

                                                              @Override
                                                              public Total createFromParcel(
                                                                      Parcel source) {
                                                                  return new Total(source);
                                                              }
                                                          };
    private static final boolean DEBUG                    = true;
    private static final String  TAG                      = "Total";
    private long                 receiptId;
    private String               receiptUnivId;
    // En céntimos de euro.
    private int                  value;

    public Total() {

    }

    public Total(Context context) {
        super(context);
    }

    public Total(String installation) {
        super(installation);
    }

    public Total(CloudEntity entity) {
        super(entity);
        receiptUnivId = (String) entity.get(PROPERTY_RECEIPT_UNIV_ID);
        Object auxValue = entity.get(PROPERTY_VALUE);
        if (auxValue instanceof String) {
            value = Integer.parseInt((String) auxValue);
        } else if (auxValue instanceof BigDecimal) {
            value = ((BigDecimal) auxValue).intValue();
        } else {
            Log.wtf(TAG, "Returned value type isn't BigDecimal nor String!!!");
        }
    }

    private Total(Parcel in) {
        super(in);
        receiptId = in.readLong();
        receiptUnivId = in.readString();
        value = in.readInt();
    }

    @Override
    public String getKindName() {
        return KIND_NAME;
    }

    @Override
    public String getTableName() {
        return DBHelper.TBL_TOTAL;
    }

    @Override
    public DBOCloudEntity getEntity(Context context) {
        long id = getId();
        String installation = Installation.id(context);
        String univ_id = getUniversalId();

        if (univ_id == null && id > 0) {
            setUniversalId(installation + id);
        }

        DBOCloudEntity entity = super.getEntity(context);

        if (receiptUnivId == null) {
            receiptUnivId = installation + id;
        }

        entity.put(PROPERTY_RECEIPT_UNIV_ID, receiptUnivId);
        entity.put(PROPERTY_VALUE, Integer.valueOf(value));

        return entity;
    }

    @Override
    public ContentValues getValues() {
        ContentValues cv = null;

        cv = new ContentValues();
        long id = getId();
        if (id > 0) {
            cv.put(DBHelper.T_TOTAL_ID, id);
        }

        String univId = getUniversalId();
        if (univId != null) {
            cv.put(DBHelper.T_TOTAL_UNIVERSAL_ID, univId);
        }

        cv.put(DBHelper.T_TOTAL_RECPT_ID, getReceiptId());
        cv.put(DBHelper.T_TOTAL_RECPT_UNIV_ID, getReceiptUnivId());
        cv.put(DBHelper.T_TOTAL_VALUE, getValue());
        cv.put(DBHelper.T_TOTAL_UPDATED, isUpdated() ? 1 : 0);

        return cv;
    }

    public long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(long receiptId) {
        this.receiptId = receiptId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = (int) (100 * value);
    }

    public void setValue(double value) {
        this.value = (int) (100 * value);
    }

    public void setValue(String valueStr) {
        int dotPos = valueStr.indexOf('.');
        if (dotPos > 0) {
            // if valueStr 12.34
            String euros = valueStr.substring(0, dotPos); // we get [12].34
            String cents = valueStr.substring(dotPos + 1);// we get 12.[34]
            value = Integer.parseInt(euros + cents);
        } else {
            value = Integer.parseInt(valueStr);
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getReceiptUnivId() {
        return receiptUnivId;
    }

    public void setReceiptUnivId(String receiptUnivId) {
        this.receiptUnivId = receiptUnivId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(receiptId);
        dest.writeString(receiptUnivId);
        dest.writeInt(value);
    }
}
