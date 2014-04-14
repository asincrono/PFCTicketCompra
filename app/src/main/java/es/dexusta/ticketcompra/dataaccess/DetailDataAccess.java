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
import es.dexusta.ticketcompra.model.Detail;

public class DetailDataAccess extends DataAccess<Detail> {
    private static final String  TAG             = "DetailDataAccess";
    private static final boolean DEBUG           = true;

    private static final String  TABLE_NAME      = DBHelper.TBL_DETAIL;

    private static final String  ID              = DBHelper.T_DETAIL_ID;
    private static final String  UNIV_ID         = DBHelper.T_DETAIL_UNIVERSAL_ID;
    private static final String  PRODUCT_ID      = DBHelper.T_DETAIL_PROD_ID;
    private static final String  PRODUCT_UNIV_ID = DBHelper.T_DETAIL_PROD_UNIV_ID;
    private static final String  RECEIPT_ID      = DBHelper.T_DETAIL_RECPT_ID;
    private static final String  RECEIPT_UNIV_ID = DBHelper.T_DETAIL_RECPT_UNIV_ID;
    private static final String  PRICE           = DBHelper.T_DETAIL_PRICE;
    private static final String  UNITS           = DBHelper.T_DETAIL_UNITS;
    private static final String  WEIGHT          = DBHelper.T_DETAIL_WEIGHT;
    private static final String  UPDATED         = DBHelper.T_DETAIL_UPDATED;

    private static final String  BASE_QUERY      = "SELECT * FROM " + TABLE_NAME + " WHERE ? = ?";

    private DBHelper             mHelper;

    public DetailDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    // Métodos de acceso específicos de Detail.
    // public void read(Receipt receipt) {
    // String[] args = {RECEIPT_ID, Long.toString(receipt.getId())};
    // new AsyncDetailQuery(mHelper, BASE_QUERY, args, listener).execute();
    // }

    @Override
    public void list() {
        DataAccessCallbacks<Detail> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + RECEIPT_ID;
            new DetailAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Detail> listener = getCallback();
        if (listener != null) {
            new DetailAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    @Override
    public void insert(List<Detail> dataList) {
        new DetailAsyncInput(mHelper, dataList, INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Detail> dataList) {
        new DetailAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    @Override
    public void delete(List<Detail> dataList) {
        new DetailAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new DetailAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    public void getCount() {
        DataAccessCallbacks<Detail> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new DetailAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
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
                Detail detail = null;

                int idIndex = c.getColumnIndexOrThrow(ID);
                int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
                int receiptIdIndex = c.getColumnIndexOrThrow(RECEIPT_ID);
                int receiptUnivIdIndex = c.getColumnIndexOrThrow(RECEIPT_UNIV_ID);
                int productIdIndex = c.getColumnIndexOrThrow(PRODUCT_ID);
                int productUnivIdIndex = c.getColumnIndexOrThrow(PRODUCT_UNIV_ID);
                int priceIndex = c.getColumnIndexOrThrow(PRICE);
                int unitsIndex = c.getColumnIndexOrThrow(UNITS);
                int weightIndex = c.getColumnIndexOrThrow(WEIGHT);
                int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

                do {
                    detail = new Detail();

                    detail.setId(c.getLong(idIndex));
                    detail.setUniversalId(c.getString(univIdIndex));
                    detail.setProductId(c.getLong(productIdIndex));
                    detail.setProductUnivId(c.getString(productUnivIdIndex));
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

    class DetailAsyncQuery extends AsyncQuery<Detail> {

        public DetailAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Detail> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Detail cursorToData(Cursor c) {
            return DetailDataAccess.cursorToData(c);
        }

        @Override
        public List<Detail> cursorToDataList(Cursor c) {

            return DetailDataAccess.cursorToDataList(c);
        }

    }

    class DetailAsyncInput extends AsyncInput<Detail> {

        public DetailAsyncInput(DBHelper helper, List<Detail> dataList, Operation operation,
                DataAccessCallbacks<Detail> listener) {
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
        public ContentValues getValues(Detail data) {
            return DetailDataAccess.getValues(data);
        }

    }

    class DetailAsyncStatement extends AsyncStatement<Detail> {

        public DetailAsyncStatement(DBHelper helper, String sqlStatement, Option option,
                DataAccessCallbacks<Detail> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }

}
