package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Receipt;

/**
 * Created by asincrono on 22/05/14.
 */
public class ReceiptData extends DataAccess<Receipt> {
    private static final String TAG = "ReceiptData";

    private static final String TABLE_NAME = DBHelper.TBL_RECEIPT;

    private static final String ID           = DBHelper.T_RECPT_ID;
    private static final String UNIV_ID      = DBHelper.T_RECPT_UNIVERSAL_ID;
    private static final String SHOP_ID      = DBHelper.T_RECPT_SHOP_ID;
    private static final String SHOP_UNIV_ID = DBHelper.T_RECPT_SHOP_UNIV_ID;
    private static final String TOTAL        = DBHelper.T_RECPT_TOTAL;
    private static final String TIMESTAMP    = DBHelper.T_RECPT_TIMESTAMP;
    private static final String UPDATED      = DBHelper.T_RECPT_UPDATED;

    private DBHelper mHelper;

    public ReceiptData(DBHelper helper) {
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

    public static Receipt cursorToReceipt(Cursor c) {
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

    public static List<Receipt> cursorToReceiptList(Cursor c) {
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
                Receipt receipt;
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
    public void list(DataAccessCallback<Receipt> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + TIMESTAMP;
        new ReceiptAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Receipt> callback) {
        new ReceiptAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        new ReceiptAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        new ReceiptAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new ReceiptAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Receipt> callback) {
        new ReceiptAsyncDelete(mHelper, null, callback).execute();
    }

    class ReceiptAsyncRead extends AsyncRead<Receipt> {
        ReceiptAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Receipt> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Receipt cursorToData(Cursor c) {
            return ReceiptData.cursorToReceipt(c);
        }

        @Override
        public List<Receipt> cursorToDataList(Cursor c) {
            return ReceiptData.cursorToReceiptList(c);
        }
    }

    class ReceiptAsyncInsert extends AsyncInsert<Receipt> {
        ReceiptAsyncInsert(DBHelper helper, List<Receipt> data, DataAccessCallback<Receipt> callback) {
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
        public ContentValues getValues(Receipt data) {
            return ReceiptData.getValues(data);
        }
    }

    class ReceiptAsyncUpdate extends AsyncUpdate<Receipt> {
        ReceiptAsyncUpdate(DBHelper helper, List<Receipt> data, DataAccessCallback<Receipt> callback) {
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
        public ContentValues getValues(Receipt data) {
            return ReceiptData.getValues(data);
        }
    }

    class ReceiptAsyncDelete extends AsyncDelete<Receipt> {
        ReceiptAsyncDelete(DBHelper helper, List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
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
