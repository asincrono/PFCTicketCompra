package es.dexusta.ticketcompra.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.util.Installation;

public class Product extends ReplicatedDBObject {
    public static final String KIND_NAME               = "product";

    public static final String PROPERTY_SUBCATEGORY_ID = "subcategory_id";
    public static final String PROPERTY_NAME           = "name";
    public static final String PROPERTY_DESCRIPTION    = "description";
    public static final String PROPERTY_ARTICLE_NUMBER = "article_number";
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {

                                                                @Override
                                                                public Product createFromParcel(
                                                                        Parcel source) {
                                                                    return new Product(source);
                                                                }

                                                                @Override
                                                                public Product[] newArray(int size) {
                                                                    return new Product[size];
                                                                }
                                                            };
    private long               subcategoryId;
    private String             name;
    private String             description;
    private String             articleNumber;                             // ESAN-13?
    private boolean            favorite;

    public Product() {

    }

    public Product(Context context) {
        super(context);
    }

    public Product(String installation) {
        super(installation);
    }

    public Product(DBOCloudEntity entity) {
        super(entity);

        subcategoryId = (Long.parseLong((String) entity.get(PROPERTY_SUBCATEGORY_ID)));
        name = (String) entity.get(PROPERTY_NAME);
        description = (String) entity.get(PROPERTY_DESCRIPTION);
        articleNumber = (String) entity.get(PROPERTY_ARTICLE_NUMBER);

        // TODO: ¿Qué hago con los favoritos?.
        // Cada usuario tendría una lista de favoritos.
        // En resumen, faborito no se guarda en la entity Product ya que es
        // información
        // única de cada usuario.
    }

    public Product(CloudEntity entity) {
        super(entity);

        subcategoryId = (Long.parseLong((String) entity.get(PROPERTY_SUBCATEGORY_ID)));
        name = (String) entity.get(PROPERTY_NAME);
        description = (String) entity.get(PROPERTY_DESCRIPTION);
        articleNumber = (String) entity.get(PROPERTY_ARTICLE_NUMBER);
    }

    private Product(Parcel in) {
        super(in);
        subcategoryId = in.readLong();
        name = in.readString();
        description = in.readString();
        if (description.length() == 0) {
            description = null;
        }

        articleNumber = in.readString();
        if (articleNumber.length() == 0) {
            articleNumber = null;
        }

        favorite = (in.readInt() == 1) ? true : false;
    }
    
    @Override
    public String getKindName() {
        return KIND_NAME;
    }

    @Override
    public String getTableName() {
        return DBHelper.TBL_PRODUCT;
    }

    @Override
    public DBOCloudEntity getEntity(Context context) {
        long id = getId();
        String univ_id = getUniversalId();
        if (univ_id == null && id > 0) {
            setUniversalId(Installation.id(context) + id);
        }

        DBOCloudEntity entity = super.getEntity(context);

        entity.put(PROPERTY_SUBCATEGORY_ID, Long.toString(subcategoryId));
        entity.put(PROPERTY_NAME, name);
        entity.put(PROPERTY_DESCRIPTION, description);
        entity.put(PROPERTY_ARTICLE_NUMBER, articleNumber);

        return entity;
    }

    @Override
    public ContentValues getValues() {
        ContentValues cv = null;

        cv = new ContentValues();

        long id = getId();
        if (id > 0) {
            cv.put(DBHelper.T_PROD_ID, id);
        }

        if (getUniversalId() != null) {
            cv.put(DBHelper.T_PROD_UNIVERSAL_ID, getUniversalId());
        }

        cv.put(DBHelper.T_PROD_SUBCAT_ID, getSubcategoryId());
        cv.put(DBHelper.T_PROD_ARTNUMBER, getArticleNumber());
        cv.put(DBHelper.T_PROD_NAME, getName());
        cv.put(DBHelper.T_PROD_DESCR, getDescription());
        cv.put(DBHelper.T_PROD_UPDATED, isUpdated() ? 1 : 0);

        return cv;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(long subcategoryId) {
        this.subcategoryId = subcategoryId;
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

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder("Product:");
        text.append("\n   id: ").append(getId());
        text.append("\n   univId: ").append(getUniversalId());
        text.append("\n   subcategoryId: ").append(subcategoryId);
        text.append("\n   name: ").append(name);
        text.append("\n   description: ").append(description + "\n");

        return text.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(subcategoryId);
        dest.writeString(name);
        if (description == null) {
            dest.writeString("");
        } else {
            dest.writeString(description);
        }

        if (articleNumber == null) {
            dest.writeString("");
        } else {
            dest.writeString(articleNumber);
        }

        if (favorite) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }
}
