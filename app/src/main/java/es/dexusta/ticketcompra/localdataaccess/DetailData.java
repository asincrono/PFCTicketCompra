package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Detail;

/**
 * Created by asincrono on 22/05/14.
 */
public class DetailData extends DataAccess<Detail> {
    private static final String TAG = "DetailData";

    private static final String  TABLE_NAME      = DBHelper.TBL_DETAIL;
    private static final String  BASE_QUERY      = "SELECT * FROM " + TABLE_NAME + " WHERE ? = ?";
    private static final String  ID              = DBHelper.T_DETAIL_ID;
    private static final String  UNIV_ID         = DBHelper.T_DETAIL_UNIVERSAL_ID;
    private static final String  PRODUCT_ID      = DBHelper.T_DETAIL_PROD_ID;
    private static final String  PRODUCT_UNIV_ID = DBHelper.T_DETAIL_PROD_UNIV_ID;
    private static final String  PRODUCT_NAME    = DBHelper.T_DETAIL_PROD_NAME;
    private static final String  RECEIPT_ID      = DBHelper.T_DETAIL_RECPT_ID;
    private static final String  RECEIPT_UNIV_ID = DBHelper.T_DETAIL_RECPT_UNIV_ID;
    private static final String  PRICE           = DBHelper.T_DETAIL_PRICE;
    private static final String  UNITS           = DBHelper.T_DETAIL_UNITS;
    private static final String  WEIGHT          = DBHelper.T_DETAIL_WEIGHT;
    private static final String  UPDATED         = DBHelper.T_DETAIL_UPDATED;


    private DBHelper mHelper;

    public DetailData(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Detail data) {
        ContentValues cv = null;
        if (data != null) {
            cv = new ContentValues();
            long id = data.getId();
            if (id > 0) {
                cv.put(ID, id);
            }

            String univId = data.getUniversalId();
            if (univId != null) {
                cv.put(UNIV_ID, univId);
            }

            cv.put(PRODUCT_ID, data.getProductId());
            cv.put(PRODUCT_UNIV_ID, data.getProductUnivId());
            cv.put(PRODUCT_NAME, data.getProductName());

            cv.put(RECEIPT_ID, data.getReceiptId());
            cv.put(RECEIPT_UNIV_ID, data.getReceiptUnivId());
            cv.put(PRICE, data.getPrice());
            cv.put(UNITS, data.getUnits());
            cv.put(WEIGHT, data.getWeight());
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }
        return cv;
    }

    public static Detail cursorToData(Cursor c) {
        Detail detail = null;
        if (c != null && c.getCount() > 0) {
            detail = new Detail();
            detail.setId(c.getColumnIndexOrThrow(ID));
            detail.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            detail.setProductId(c.getLong(c.getColumnIndexOrThrow(PRODUCT_ID)));
            detail.setProductUnivId(c.getString(c.getColumnIndexOrThrow(PRODUCT_UNIV_ID)));
            detail.setProductName(c.getString(c.getColumnIndexOrThrow(PRODUCT_NAME)));

            detail.setReceiptId(c.getLong(c.getColumnIndexOrThrow(RECEIPT_ID)));
            detail.setReceiptUnivId(c.getString(c.getColumnIndexOrThrow(RECEIPT_UNIV_ID)));
            detail.setPrice(c.getInt(c.getColumnIndexOrThrow(PRICE)));
            detail.setUnits(c.getInt(c.getColumnIndexOrThrow(UNITS)));
            detail.setWeight(c.getInt(c.getColumnIndexOrThrow(WEIGHT)));
            detail.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);
        }
        return detail;
    }

    public static List<Detail> cursorToDataList(Cursor c) {
        List<Detail> list = null;
        if (c != null) {

            int savedPosition = c.getPosition();

            if (c.moveToFirst()) {
                list = new ArrayList<Detail>();

                int idIndex = c.getColumnIndexOrThrow(ID);
                int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
                int receiptIdIndex = c.getColumnIndexOrThrow(RECEIPT_ID);
                int receiptUnivIdIndex = c.getColumnIndexOrThrow(RECEIPT_UNIV_ID);
                int productIdIndex = c.getColumnIndexOrThrow(PRODUCT_ID);
                int productUnivIdIndex = c.getColumnIndexOrThrow(PRODUCT_UNIV_ID);
                int productNameIndex = c.getColumnIndexOrThrow(PRODUCT_NAME);

                int priceIndex = c.getColumnIndexOrThrow(PRICE);
                int unitsIndex = c.getColumnIndexOrThrow(UNITS);
                int weightIndex = c.getColumnIndexOrThrow(WEIGHT);
                int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

                Detail detail;
                do {
                    detail = new Detail();

                    detail.setId(c.getLong(idIndex));
                    detail.setUniversalId(c.getString(univIdIndex));
                    detail.setProductId(c.getLong(productIdIndex));
                    detail.setProductUnivId(c.getString(productUnivIdIndex));
                    detail.setProductName(c.getString(productNameIndex));

                    detail.setReceiptId(c.getLong(receiptIdIndex));
                    detail.setReceiptUnivId(c.getString(receiptUnivIdIndex));
                    detail.setPrice(c.getInt(priceIndex));
                    detail.setUnits(c.getInt(unitsIndex));
                    detail.setWeight(c.getInt(weightIndex));
                    detail.setUpdated(c.getInt(updatedIndex) > 0);

                    list.add(detail);
                } while (c.moveToNext());
            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list(DataAccessCallback<Detail> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + RECEIPT_ID;
        new DetailAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sqlQuery, String[] args, DataAccessCallback<Detail> callback) {
        new DetailAsyncRead(mHelper, sqlQuery, args, callback).execute();
    }

    @Override
    public void insert(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        new DetailAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        new DetailAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new DetailAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Detail> callback) {
        new DetailAsyncDelete(mHelper, null, callback).execute();
    }

    class DetailAsyncRead extends AsyncRead<Detail> {
        DetailAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Detail> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Detail cursorToData(Cursor c) {
            return DetailData.cursorToData(c);
        }

        @Override
        public List<Detail> cursorToDataList(Cursor c) {
            return DetailData.cursorToDataList(c);
        }
    }

    class DetailAsyncInsert extends AsyncInsert<Detail> {
        DetailAsyncInsert(DBHelper helper, List<Detail> data, DataAccessCallback<Detail> callback) {
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
        public ContentValues getValues(Detail data) {
            return DetailData.getValues(data);
        }
    }

    class DetailAsyncUpdate extends AsyncUpdate<Detail> {
        DetailAsyncUpdate(DBHelper helper, List<Detail> data, DataAccessCallback<Detail> callback) {
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
        public ContentValues getValues(Detail data) {
            return DetailData.getValues(data);
        }
    }

    class DetailAsyncDelete extends AsyncDelete<Detail> {
        DetailAsyncDelete(DBHelper helper, List<Detail> dataList, DataAccessCallback<Detail> callback) {
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
