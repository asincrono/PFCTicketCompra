package es.dexusta.ticketcompra.dataaccess;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Product;

public class ProductDataAccess extends DataAccess<Product> {
    private static final String  TAG            = "ProductDataAccess";
    private static final boolean DEBUG          = true;

    private static final String  TABLE_NAME     = DBHelper.TBL_PRODUCT;

    private static final String  ID             = DBHelper.T_PROD_ID;
    private static final String  UNIV_ID        = DBHelper.T_PROD_UNIVERSAL_ID;
    private static final String  SUBCATEGORY_ID = DBHelper.T_PROD_SUBCAT_ID;
    private static final String  ARTNUMBER      = DBHelper.T_PROD_ARTNUMBER;
    private static final String  NAME           = DBHelper.T_PROD_NAME;
    private static final String  DESCRIPTION    = DBHelper.T_PROD_DESCR;
    private static final String  IS_FAVOURITE   = DBHelper.T_PROD_IS_FAVOURITE;
    private static final String  UPDATED        = DBHelper.T_PROD_UPDATED;

    private DBHelper             mHelper;

    public ProductDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Product> listener = getCallback();
        if (listener != null) {
            new ProductAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void list() {
        DataAccessCallbacks<Product> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
            new ProductAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void insert(List<Product> dataList) {
        DataAccessCallbacks<Product> listener = getCallback();
        new ProductAsyncInput(mHelper, dataList, INSERT, listener).execute();
    }

    @Override
    public void update(List<Product> dataList) {
        DataAccessCallbacks<Product> listener = getCallback();
        new ProductAsyncInput(mHelper, dataList, UPDATE, listener).execute();
    }

    @Override
    public void delete(List<Product> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new ProductAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new ProductAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    // TODO MAKE THIS AN ASYNC TASK TOO!!!
    @Override
    public void getCount() {
        DataAccessCallbacks<Product> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new ProductAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
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

    class ProductAsyncQuery extends AsyncQuery<Product> {

        public ProductAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Product> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Product cursorToData(Cursor c) {
            return ProductDataAccess.cursorToData(c);
        }

        @Override
        public List<Product> cursorToDataList(Cursor c) {
            return ProductDataAccess.cursorToDataList(c);
        }
    }

    class ProductAsyncInput extends AsyncInput<Product> {

        public ProductAsyncInput(DBHelper helper, List<Product> dataList, Operation operation,
                DataAccessCallbacks<Product> listener) {
            super(helper, dataList, operation, listener);
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
            return ProductDataAccess.getValues(data);
        }
    }

    class ProductAsyncStatement extends AsyncStatement<Product> {

        public ProductAsyncStatement(DBHelper helper, String sqlStatement, Option option,
                DataAccessCallbacks<Product> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
