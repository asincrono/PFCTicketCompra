package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Receipt;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

public class ReceiptDataAccess extends DataAccess<Receipt> {
    private static final String TAG = "ReceiptDataAccess";
    private static final boolean DEBUG = true;

    private static final String TABLE_NAME = DBHelper.TBL_RECEIPT;

    private static final String ID = DBHelper.T_RECPT_ID;
    private static final String UNIV_ID = DBHelper.T_RECPT_UNIVERSAL_ID;
    private static final String USER_ID = DBHelper.T_RECPT_USER_ID;
    private static final String SHOP_ID = DBHelper.T_RECPT_SHOP_ID;
    private static final String SHOP_UNIV_ID = DBHelper.T_RECPT_SHOP_UNIV_ID;
    private static final String TOTAL = DBHelper.T_RECPT_TOTAL;
    private static final String TIMESTAMP = DBHelper.T_RECPT_TIMESTAMP;
    private static final String UPDATED = DBHelper.T_RECPT_UPDATED;

    
    private DBHelper mHelper;

    public ReceiptDataAccess(DBHelper helper) {        
        mHelper = helper;
    }

    public static ContentValues getValues(Receipt data) {
        ContentValues cv = null;

        if (data != null) {
            cv = new ContentValues();
            long id = data.getId();
            if (id > 0) {
                cv.put(ID, id);
            }

            String univ_id = data.getUniversalId();
            if (univ_id != null) {
                cv.put(UNIV_ID, univ_id);
            }
            cv.put(SHOP_ID, data.getShopId());
            cv.put(SHOP_UNIV_ID, data.getShopUnivId());
            cv.put(TOTAL, data.getTotal());
            cv.put(TIMESTAMP, data.getTimestampRfc3339());
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }

        return cv;
    }

    public static Receipt cursorToData(Cursor c) {
        Receipt receipt = null;

        if (c != null && c.getCount() > 0) {
            receipt = new Receipt();
            receipt.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            receipt.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            receipt.setShopId(c.getLong(c.getColumnIndexOrThrow(SHOP_ID)));
            receipt.setShopUnivId(c.getString(c.getColumnIndexOrThrow(SHOP_UNIV_ID)));
            receipt.setTotal(c.getInt(c.getColumnIndexOrThrow(TOTAL)));
            receipt.setTimestamp(c.getString(c.getColumnIndexOrThrow(TIMESTAMP)));
            receipt.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);
        }

        return receipt;
    }

    public static List<Receipt> cursorToDataList(Cursor c) {
        List<Receipt> list = null;
        if (c != null) {
            int savedPosition = c.getPosition();

            int idIndex = c.getColumnIndexOrThrow(ID);
            int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
            int shopIdIndex = c.getColumnIndexOrThrow(SHOP_ID);
            int shopUnivIdIndex = c.getColumnIndexOrThrow(SHOP_UNIV_ID);
            int totalIndex = c.getColumnIndexOrThrow(TOTAL);
            int timestampIndex = c.getColumnIndexOrThrow(TIMESTAMP);
            int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

            if (c.moveToFirst()) {
                list = new ArrayList<Receipt>();
                Receipt receipt = null;
                do {
                    receipt = new Receipt();

                    receipt.setId(c.getLong(idIndex));
                    receipt.setUniversalId(c.getString(univIdIndex));
                    receipt.setShopId(c.getLong(shopIdIndex));
                    receipt.setShopUnivId(c.getString(shopUnivIdIndex));
                    receipt.setTotal(c.getInt(totalIndex));
                    receipt.setTimestamp(c.getString(timestampIndex));
                    receipt.setUpdated(c.getInt(updatedIndex) > 0);

                    list.add(receipt);

                } while (c.moveToNext());

            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list() {
        DataAccessCallbacks<Receipt> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + TIMESTAMP;
            new ReceiptAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Receipt> listener = getCallback();
        if (listener != null) {
            new ReceiptAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void insert(List<Receipt> dataList) {
        new ReceiptAsyncInput(mHelper, dataList, INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Receipt> dataList) {
        new ReceiptAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    @Override
    public void delete(List<Receipt> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new ReceiptAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new ReceiptAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    @Override
    public void getCount() {
        DataAccessCallbacks<Receipt> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new ReceiptAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }

    }

    class ReceiptAsyncQuery extends AsyncQuery<Receipt> {

        public ReceiptAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Receipt> listener) {
            super(helper, rawQuery, args, listener);        }

        @Override
        public Receipt cursorToData(Cursor c) {           
            return ReceiptDataAccess.cursorToData(c);
        }

        @Override
        public List<Receipt> cursorToDataList(Cursor c) {
            return ReceiptDataAccess.cursorToDataList(c);
        }

    }

    class ReceiptAsyncInput extends AsyncInput<Receipt> {

        public ReceiptAsyncInput(DBHelper helper, List<Receipt> dataList, Operation operation,
                DataAccessCallbacks<Receipt> listener) {
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
        public ContentValues getValues(Receipt data) {
            return ReceiptDataAccess.getValues(data);
        }        
    }

    class ReceiptAsyncStatement extends AsyncStatement<Receipt> {

        public ReceiptAsyncStatement(DBHelper helper, String sqlStatement,
                Option option,
                DataAccessCallbacks<Receipt> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
