package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.DBHelper;

/**
 * Created by asincrono on 22/05/14.
 */

public class ChainData extends DataAccess<Chain> {
    private static final String TAG = "ChainData";

    private static final String TABLE_NAME = DBHelper.TBL_CHAIN;
    private static final String ID = DBHelper.T_CHAIN_ID;
    private static final String NAME = DBHelper.T_CHAIN_NAME;
    private static final String CODE = DBHelper.T_CHAIN_CODE;

    private DBHelper mHelper;

    public ChainData(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Chain data) {
        ContentValues cv = null;
        if (data != null) {
            cv =  new ContentValues();
            if (data.getId() > 0) {
                cv.put(ID, data.getId());
            }
            cv.put(NAME, data.getName());
            cv.put(CODE, data.getCode());
        }
        return cv;
    }

    public static Chain cursorToChain(Cursor c) {
        Chain chain = null;
        if (c != null && c.getCount() > 0) {
            chain = new Chain();
            chain.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            chain.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
            chain.setCode(c.getString(c.getColumnIndexOrThrow(CODE)));
        }
        return chain;
    }

    public static List<Chain> cursorToChainList(Cursor c) {
        List<Chain> list = null;
        if (c != null && c.getCount() > 0) {

            // Save the current cursor position.
            int savedPosition = c.getPosition();

            // We obtain all the column indexes at once.
            int idIndex = c.getColumnIndexOrThrow(ID);
            int nameIndex = c.getColumnIndexOrThrow(NAME);
            int codeIndex = c.getColumnIndexOrThrow(CODE);

            list = new ArrayList<Chain>();
            Chain chain;

            c.moveToFirst();

            do {
                chain = new Chain();
                chain.setId(c.getLong(idIndex));
                chain.setName(c.getString(nameIndex));
                chain.setCode(c.getString(codeIndex));

                list.add(chain);
            } while (c.moveToNext());


            // Restore original cursor position.
            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list(DataAccessCallback<Chain> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new ChainAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Chain> callback) {
        new ChainAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        new ChainAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        new ChainAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new ChainAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Chain> callback) {
        new ChainAsyncDelete(mHelper, null, callback).execute();
    }

    class ChainAsyncRead extends AsyncRead<Chain> {

        public ChainAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Chain> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Chain cursorToData(Cursor c) {
            return ChainData.cursorToChain(c);
        }

        @Override
        public List<Chain> cursorToDataList(Cursor c) {
            return ChainData.cursorToChainList(c);
        }
    }

    class ChainAsyncInsert extends AsyncInsert<Chain> {

        public ChainAsyncInsert(DBHelper helper, List<Chain> data, DataAccessCallback<Chain> callback) {
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
        public ContentValues getValues(Chain data) {
            return ChainData.getValues(data);
        }
    }

    class ChainAsyncUpdate extends AsyncUpdate<Chain> {

        ChainAsyncUpdate(DBHelper helper, List<Chain> data, DataAccessCallback<Chain> callback) {
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
        public ContentValues getValues(Chain data) {
            return ChainData.getValues(data);
        }
    }

    class ChainAsyncDelete extends AsyncDelete<Chain> {

        ChainAsyncDelete(DBHelper helper, List<Chain> dataList, DataAccessCallback<Chain> callback) {
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
