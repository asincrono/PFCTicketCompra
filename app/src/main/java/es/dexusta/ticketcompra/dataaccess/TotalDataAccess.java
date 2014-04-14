package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Total;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

public class TotalDataAccess extends DataAccess<Total> {
    private static final String  TAG             = "TotalDataAccess";
    private static final boolean DEBUG           = true;

    private static final String  TABLE_NAME      = DBHelper.TBL_TOTAL;

    private static final String  ID              = DBHelper.T_TOTAL_ID;
    private static final String  UNIV_ID         = DBHelper.T_TOTAL_UNIVERSAL_ID;
    private static final String  RECEIPT_ID      = DBHelper.T_TOTAL_RECPT_ID;
    private static final String  RECEIPT_UNIV_ID = DBHelper.T_TOTAL_RECPT_UNIV_ID;
    private static final String  VALUE           = DBHelper.T_TOTAL_VALUE;
    private static final String  UPDATED         = DBHelper.T_TOTAL_UPDATED;

    private DBHelper             mHelper;                                          ;

    public TotalDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Total data) {
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

            cv.put(RECEIPT_ID, data.getReceiptId());
            cv.put(RECEIPT_UNIV_ID, data.getReceiptUnivId());
            cv.put(VALUE, data.getValue());
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }

        return cv;
    }

    public static Total cursorToData(Cursor c) {
        Total total = null;

        if (c != null && c.getCount() > 0) {
            total = new Total();
            total.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            total.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            total.setReceiptId(c.getLong(c.getColumnIndexOrThrow(RECEIPT_ID)));
            total.setReceiptUnivId(c.getString(c.getColumnIndexOrThrow(RECEIPT_UNIV_ID)));
            total.setValue(c.getFloat(c.getColumnIndexOrThrow(VALUE)));
            total.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);
        }

        return total;
    }

    public static List<Total> cursorToDataList(Cursor c) {
        List<Total> list = null;

        if (c != null) {
            int savedPosition = c.getPosition();

            if (c.moveToFirst()) {
                list = new ArrayList<Total>();

                int idIndex = c.getColumnIndexOrThrow(ID);
                int univIdIndex = c.getColumnIndexOrThrow(RECEIPT_UNIV_ID);
                int receiptIdIndex = c.getColumnIndexOrThrow(RECEIPT_ID);
                int receiptUnivIdIndex = c.getColumnIndexOrThrow(RECEIPT_UNIV_ID);
                int valueIndex = c.getColumnIndexOrThrow(VALUE);
                int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

                Total total = null;
                do {
                    total = new Total();

                    total.setId(c.getLong(idIndex));
                    total.setUniversalId(c.getString(univIdIndex));
                    total.setReceiptId(c.getLong(receiptIdIndex));
                    total.setReceiptUnivId(c.getString(receiptUnivIdIndex));
                    total.setValue(c.getFloat(valueIndex));
                    total.setUpdated(c.getInt(updatedIndex) > 0);

                    list.add(total);
                } while (c.moveToNext());
            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list() {
        DataAccessCallbacks<Total> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME;
            new AsyncTotalQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Total> listener = getCallback();
        if (listener != null) {
            new AsyncTotalQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    @Override
    public void insert(List<Total> dataList) {
        new AsyncTotalInput(mHelper, dataList, INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Total> dataList) {
        new AsyncTotalInput(mHelper, dataList, UPDATE, getCallback()).execute();

    }

    @Override
    public void delete(List<Total> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new AsyncTotalInput(mHelper, dataList, DELETE, getCallback()).execute();

    }

    @Override
    public void deleteAll() {
        new AsyncTotalInput(mHelper, null, DELETE, getCallback()).execute();
    }

    @Override
    public void getCount() {
        DataAccessCallbacks<Total> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new TotalAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
    }

    class AsyncTotalQuery extends AsyncQuery<Total> {

        public AsyncTotalQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Total> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Total cursorToData(Cursor c) {
            return TotalDataAccess.cursorToData(c);
        }

        @Override
        public List<Total> cursorToDataList(Cursor c) {
            return TotalDataAccess.cursorToDataList(c);
        }

    }

    class AsyncTotalInput extends AsyncInput<Total> {

        public AsyncTotalInput(DBHelper helper, List<Total> dataList, Operation operation,
                DataAccessCallbacks<Total> listener) {
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
        public ContentValues getValues(Total data) {
            return TotalDataAccess.getValues(data);
        }

    }

    class TotalAsyncStatement extends AsyncStatement<Total> {

        public TotalAsyncStatement(DBHelper helper, String sqlStatement, Option option,
                DataAccessCallbacks<Total> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
