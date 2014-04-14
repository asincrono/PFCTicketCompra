package es.dexusta.ticketcompra.model;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.util.Installation;

public class Detail extends ReplicatedDBObject implements Persistent {
    private static final String  TAG                      = "Detail";
    private static final boolean DEBUG                    = true;

    public static final String   KIND_NAME                = "detail";

    private static final String  PROPERTY_RECEIPT_ID      = "receipt_id";
    private static final String  PROPERTY_RECEIPT_UNIV_ID = "receipt_universal_id";
    private static final String  PROPERTY_PRODUCT_ID      = "product_id";
    private static final String  PROPERTY_PRODUCT_UNIV_ID = "product_universal_id";
    private static final String  PROPERTY_PRODUCT_NAME    = "product_name";
    private static final String  PROPERTY_UNITS           = "units";
    private static final String  PROPERTY_WEIGHT          = "weight";
    private static final String  PROPERTY_VOLUME          = "volume";
    private static final String  PROPERTY_PRICE           = "price";

    private long                 receiptId;
    private String               receiptUnivId;
    private long                 productId;
    private String               productUnivId;
    private String               productName;
    private int                  units                    = 1;
    private int                  weight;
    // volume will be in ml.
    private int                  volume;
    private int                  price;
    private float                pricePerUnit;
    private float                pricePerKilo;
    private float                pricePerLiter;

    public Detail() {

    }

    public Detail(Context context) {
        super(context);
    }

    public Detail(String installation) {
        super(installation);
    }

    // public static Detail getDetail(DBOCloudEntity entity) {
    // Detail detail = null;
    // if (entity.getKindName().equals(KIND_NAME)) {
    // detail = new Detail();
    //
    // detail.setId(entity.getLocalId());
    // detail.setRemoteId(entity.getId());
    //
    // // I don't check if they return null as this are required.
    // detail.setReceiptId(Long.parseLong((String)
    // entity.get(PROPERTY_RECEIPT_ID)));
    // detail.setProductId(Long.parseLong((String)
    // entity.get(PROPERTY_PRODUCT_ID)));
    // detail.setProductName((String) entity.get(PROPERTY_PRODUCT_NAME));
    // detail.setPrice(((BigDecimal) entity.get(PROPERTY_PRICE)).intValue());
    // detail.setUnits(((BigDecimal) entity.get(PROPERTY_UNITS)).intValue());
    //
    // // I need to check if this two return null as they are optional.
    // BigDecimal returned = (BigDecimal) entity.get(PROPERTY_VOLUME);
    // if (returned != null)
    // detail.setVolume(returned.intValue());
    //
    // returned = (BigDecimal) entity.get(PROPERTY_WEIGHT);
    // if (returned != null)
    // detail.setWeight(returned.intValue());
    // }
    // return detail;
    // }

    @Override
    public ContentValues getValues() {
        ContentValues cv = null;

        cv = new ContentValues();
        long id = getId();
        if (id > 0) {
            cv.put(DBHelper.T_DETAIL_ID, id);
        }

        String univId = getUniversalId();
        if (univId != null) {
            cv.put(DBHelper.T_DETAIL_UNIVERSAL_ID, univId);
        }

        cv.put(DBHelper.T_DETAIL_PROD_ID, getProductId());
        cv.put(DBHelper.T_DETAIL_PROD_UNIV_ID, getProductUnivId());
        cv.put(DBHelper.T_DETAIL_RECPT_ID, getReceiptId());
        cv.put(DBHelper.T_DETAIL_RECPT_UNIV_ID, getReceiptUnivId());
        cv.put(DBHelper.T_DETAIL_PRICE, getPrice());
        cv.put(DBHelper.T_DETAIL_UNITS, getUnits());
        cv.put(DBHelper.T_DETAIL_WEIGHT, getWeight());
        cv.put(DBHelper.T_DETAIL_UPDATED, isUpdated() ? 1 : 0);

        return cv;
    }

    @Override
    public String getKindName() {
        return KIND_NAME;
    }
    
    @Override
    public String getTableName() {
        return DBHelper.TBL_DETAIL;
    }

    @Override
    public DBOCloudEntity getEntity(Context context) {

        String installation = Installation.id(context);
        long id = getId();
        String univ_id = getUniversalId();

        if (univ_id == null && id > 0) {
            setUniversalId(installation + id);
        }

        DBOCloudEntity entity = super.getEntity(context);

        // entity.put(PROPERTY_RECEIPT_ID, Long.toString(receiptId));

        if (receiptUnivId == null) {
            receiptUnivId = installation + receiptId;
        }
        entity.put(PROPERTY_RECEIPT_UNIV_ID, receiptUnivId);

        // entity.put(PROPERTY_PRODUCT_ID, Long.toString(productId));

        if (productUnivId == null) {
            productUnivId = installation + productId;
        }
        entity.put(PROPERTY_PRODUCT_UNIV_ID, productUnivId);

        entity.put(PROPERTY_PRODUCT_NAME, productName);
        entity.put(PROPERTY_PRICE, Integer.valueOf(price));
        entity.put(PROPERTY_UNITS, Integer.valueOf(units));
        entity.put(PROPERTY_VOLUME, Integer.valueOf(volume));
        entity.put(PROPERTY_WEIGHT, Integer.valueOf(weight));
        return entity;
    }

    private int translate(Object object) {
        int value = 0;
        if (object instanceof BigDecimal) {
            value = ((BigDecimal) object).intValue();
        } else if (object instanceof String) {
            value = Integer.parseInt((String) object);
        } else {
            Log.wtf(TAG, "This entity is a mess: retrieving Integers as "
                    + object.getClass().getName());
        }
        return value;
    }

    public Detail(CloudEntity entity) {
        super(entity);
        // receiptId = Long.parseLong((String) entity.get(PROPERTY_RECEIPT_ID));
        receiptUnivId = (String) entity.get(PROPERTY_RECEIPT_UNIV_ID);
        // productId = Long.parseLong((String) entity.get(PROPERTY_PRODUCT_ID));
        productUnivId = (String) entity.get(PROPERTY_PRODUCT_UNIV_ID);
        productName = (String) entity.get(PROPERTY_PRODUCT_NAME);

        int price = translate(entity.get(PROPERTY_PRICE));
        setPrice(price);

        int units = translate(entity.get(PROPERTY_UNITS));
        setUnits(units);

        // I need to check if this two return null as they are optional.
        Object returned = entity.get(PROPERTY_VOLUME);

        if (returned != null) {
            int volume = translate(returned);
            setVolume(volume);
            // Log.d(TAG, "Volume returned: " + volume);
        }

        returned = entity.get(PROPERTY_WEIGHT);
        if (returned != null) {
            int weight = translate(returned);
            setWeight(weight);
            // Log.d(TAG, "Weight returned: " + weight);
        }
    }

    public long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(long receiptId) {
        this.receiptId = receiptId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProduct(Product product) {
        productId = product.getId();
        productUnivId = product.getUniversalId();
        productName = product.getName();
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public CharSequence getProductName() {
        return productName;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getReceiptUnivId() {
        return receiptUnivId;
    }

    public void setReceiptUnivId(String receiptUnivId) {
        this.receiptUnivId = receiptUnivId;
    }

    public String getProductUnivId() {
        return productUnivId;
    }

    public void setProductUnivId(String productUnivId) {
        this.productUnivId = productUnivId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        if (units > 0) {
            if (price > 0) {
                pricePerUnit = price / units;
            }
            this.units = units;
        } else {
            throw new InvalidParameterException("The units value must be positive");
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight >= 0) {
            if (weight > 0 && price > 0) {
                pricePerKilo = price * 1000 / weight;
            }
            this.weight = weight;
        } else {
            throw new InvalidParameterException("The weight value must be positive");
        }

    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if (volume >= 0) {
            if (volume > 0 && price > 0) {
                pricePerLiter = price * 1000 / volume;
            }
            this.volume = volume;
        } else {
            throw new InvalidParameterException("The volume value must be positive");
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        float decimalPrice = price / 100;
        if (price > 0) {
            if (units > 0) {
                pricePerUnit = decimalPrice / units;
            }
            if (weight > 0) {
                pricePerKilo = decimalPrice * 1000 / weight;
            }
            if (volume > 0) {
                pricePerLiter = decimalPrice * 1000 / volume;
            }

            this.price = price;
        } else {
            throw new InvalidParameterException("The price must be positive");
        }
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public float getPricePerKilo() {
        return pricePerKilo;
    }

    public float getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerKilo(float pricePerKilo) {
        this.pricePerKilo = pricePerKilo;
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
        text.append("\n").append("receiptId : ").append(receiptId);
        text.append("\n").append("receiptUnivId : ").append(receiptUnivId);
        text.append("\n").append("productId : ").append(productId);
        text.append("\n").append("productUnivId : ").append(productUnivId).append("\n");

        return text.toString();
    }

    private Detail(Parcel in) {
        super(in);
        receiptId = in.readLong();
        productId = in.readLong();
        productName = in.readString();
        units = in.readInt();
        weight = in.readInt();
        volume = in.readInt();
        price = in.readInt();
        pricePerUnit = in.readFloat();
        pricePerKilo = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(receiptId);
        dest.writeLong(productId);
        dest.writeString(productName);
        dest.writeInt(units);
        dest.writeInt(weight);
        dest.writeInt(volume);
        dest.writeInt(price);
        dest.writeFloat(pricePerUnit);
        dest.writeFloat(pricePerKilo);
    }

    public static final Parcelable.Creator<Detail> CREATOR = new Parcelable.Creator<Detail>() {

                                                               @Override
                                                               public Detail createFromParcel(
                                                                       Parcel source) {
                                                                   return new Detail(source);
                                                               }

                                                               @Override
                                                               public Detail[] newArray(int size) {
                                                                   return new Detail[size];
                                                               }
                                                           };

    // public static Detail getDetail(DBOCloudEntity entity) {
    // Detail detail = null;
    // if (entity.getKindName().equals(KIND_NAME)) {
    // detail = new Detail();
    //
    // detail.setId(entity.getLocalId());
    // detail.setRemoteId(entity.getId());
    //
    // // I don't check if they return null as this are required.
    // detail.setReceiptId(Long.parseLong((String)
    // entity.get(PROPERTY_RECEIPT_ID)));
    // detail.setProductId(Long.parseLong((String)
    // entity.get(PROPERTY_PRODUCT_ID)));
    // detail.setProductName((String) entity.get(PROPERTY_PRODUCT_NAME));
    // detail.setPrice(((BigDecimal) entity.get(PROPERTY_PRICE)).intValue());
    // detail.setUnits(((BigDecimal) entity.get(PROPERTY_UNITS)).intValue());
    //
    // // I need to check if this two return null as they are optional.
    // BigDecimal returned = (BigDecimal) entity.get(PROPERTY_VOLUME);
    // if (returned != null)
    // detail.setVolume(returned.intValue());
    //
    // returned = (BigDecimal) entity.get(PROPERTY_WEIGHT);
    // if (returned != null)
    // detail.setWeight(returned.intValue());
    // }
    // return detail;
    // }

}
