package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Product;

/**
 * Created by asincrono on 22/05/14.
 */
public class ProductData extends DataAccess<Product> {
    private static final String TAG = "ProductDataAccess";

    private static final String TABLE_NAME = DBHelper.TBL_PRODUCT;

    private static final String ID             = DBHelper.T_PROD_ID;
    private static final String UNIV_ID        = DBHelper.T_PROD_UNIVERSAL_ID;
    private static final String SUBCATEGORY_ID = DBHelper.T_PROD_SUBCAT_ID;
    private static final String ARTNUMBER      = DBHelper.T_PROD_ARTNUMBER;
    private static final String NAME           = DBHelper.T_PROD_NAME;
    private static final String DESCRIPTION    = DBHelper.T_PROD_DESCR;
    private static final String IS_FAVOURITE   = DBHelper.T_PROD_IS_FAVOURITE;
    private static final String UPDATED        = DBHelper.T_PROD_UPDATED;

    private DBHelper mHelper;

    public ProductData(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Product data) {
        ContentValues cv = null;
        if (data != null) {
            cv = new ContentValues();

            long id = data.getId();
            if (id > 0) {
                cv.put(ID, id);
            }

            if (data.getUniversalId() != null) {
                cv.put(UNIV_ID, data.getUniversalId());
            }

            cv.put(SUBCATEGORY_ID, data.getSubcategoryId());
            cv.put(ARTNUMBER, data.getArticleNumber());
            cv.put(NAME, data.getName());
            cv.put(DESCRIPTION, data.getDescription());
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }
        return cv;
    }

    public static Product cursorToData(Cursor c) {
        Product product = null;
        if (c != null && c.getCount() > 0) {
            product = new Product();
            product.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            product.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            product.setSubcategoryId(c.getLong(c.getColumnIndexOrThrow(SUBCATEGORY_ID)));
            product.setArticleNumber(c.getString(c.getColumnIndexOrThrow(ARTNUMBER)));
            product.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
            product.setDescription(c.getString(c.getColumnIndexOrThrow(DESCRIPTION)));
            // product.setFavorite(c.getInt(c.getColumnIndexOrThrow(IS_FAVOURITE))
            // > 0);
            product.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);

        }
        return product;
    }

    public static List<Product> cursorToDataList(Cursor c) {
        List<Product> list = null;

        if (c != null) {
            int savedPosition = c.getPosition();

            if (c.moveToFirst()) {
                list = new ArrayList<Product>();

                int idIndex = c.getColumnIndexOrThrow(ID);
                int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
                int subcategoryIndex = c.getColumnIndexOrThrow(SUBCATEGORY_ID);
                int artNumberIndex = c.getColumnIndexOrThrow(ARTNUMBER);
                int nameIndex = c.getColumnIndexOrThrow(NAME);
                int descriptionIndex = c.getColumnIndexOrThrow(DESCRIPTION);
                // int isFavouriteIndex = c.getColumnIndexOrThrow(IS_FAVOURITE);
                int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

                Product product = null;
                do {
                    product = new Product();

                    product.setId(c.getLong(idIndex));
                    product.setUniversalId(c.getString(univIdIndex));
                    product.setSubcategoryId(c.getLong(subcategoryIndex));
                    product.setArticleNumber(c.getString(artNumberIndex));
                    product.setName(c.getString(nameIndex));
                    product.setDescription(c.getString(descriptionIndex));
                    // product.setFavorite(c.getInt(isFavouriteIndex) > 0);
                    product.setUpdated(c.getInt(updatedIndex) > 0);

                    list.add(product);
                } while (c.moveToNext());
            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list(DataAccessCallback<Product> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new ProductAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Product> callback) {
        new ProductAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Product> dataList, DataAccessCallback<Product> callback) {
        new ProductAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Product> dataList, DataAccessCallback<Product> callback) {
        new ProductAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Product> dataList, DataAccessCallback<Product> callback) {
        new ProductAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Product> callback) {
        new ProductAsyncDelete(mHelper, null, callback).execute();
    }

    class ProductAsyncRead extends AsyncRead<Product> {

        private ProductAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Product> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Product cursorToData(Cursor c) {
            return ProductData.cursorToData(c);
        }

        @Override
        public List<Product> cursorToDataList(Cursor c) {
            return ProductData.cursorToDataList(c);
        }
    }

    class ProductAsyncInsert extends AsyncInsert<Product> {


        public ProductAsyncInsert(DBHelper helper, List<Product> data, DataAccessCallback<Product> callback) {
            super(helper, data, callback);
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }

        @Override
        public String getIdName() {
            return ID;
        }

        @Override
        public ContentValues getValues(Product data) {
            return ProductData.getValues(data);
        }
    }

    class ProductAsyncUpdate extends AsyncUpdate<Product> {

        public ProductAsyncUpdate(DBHelper helper, List<Product> data, DataAccessCallback<Product> callback) {
            super(helper, data, callback);
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }

        @Override
        public String getIdName() {
            return ID;
        }

        @Override
        public ContentValues getValues(Product data) {
            return ProductData.getValues(data);
        }
    }

    class ProductAsyncDelete extends AsyncDelete<Product> {

        public ProductAsyncDelete(DBHelper helper, List<Product> dataList, DataAccessCallback<Product> callback) {
            super(helper, dataList, callback);
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }

        @Override
        public String getIdName() {
            return ID;
        }
    }
}
