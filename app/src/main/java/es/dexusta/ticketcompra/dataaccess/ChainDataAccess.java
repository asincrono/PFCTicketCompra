package es.dexusta.ticketcompra.dataaccess;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.DBHelper;

public class ChainDataAccess extends DataAccess<Chain> {
    private static final String TAG = "ChainDataAccess";
    private static final boolean DEBUG = true;

    private static final String TABLE_NAME = DBHelper.TBL_CHAIN;
    private static final String ID = DBHelper.T_CHAIN_ID;
    private static final String NAME = DBHelper.T_CHAIN_NAME;
    private static final String CODE = DBHelper.T_CHAIN_CODE;

    private DBHelper mHelper;    

    public ChainDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    @Override
    public void list() {
        DataAccessCallbacks<Chain> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
            new ChainAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Chain> listener = getCallback();
        if (listener != null) {
            new ChainAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    @Override
    public void insert(List<Chain> dataList) {
        new ChainAsyncInput(mHelper, dataList, Operation.INSERT, getCallback()).execute();

    }

    @Override
    public void update(List<Chain> dataList) {
        new ChainAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    @Override
    public void delete(List<Chain> dataList) {
        if (dataList == null) {            
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }                
        new ChainAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {        
        new ChainAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    @Override
    public void getCount() {
       
        DataAccessCallbacks<Chain> listener = getCallback();
        if (listener != null) {
            String sqlStatementStr = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new ChainAsyncStatement(mHelper, sqlStatementStr, Option.LONG, listener).execute();
        }
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

    class ChainAsyncInput extends AsyncInput<Chain> {

        public ChainAsyncInput(DBHelper helper, List<Chain> dataList, Operation operation, DataAccessCallbacks<Chain> listener) {
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
        public ContentValues getValues(Chain data) {
            return ChainDataAccess.getValues(data);
        }

    }

    class ChainAsyncQuery extends AsyncQuery<Chain> {
        public ChainAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Chain> listener) {
            super(helper, rawQuery, args, listener);         
        }

        @Override
        public Chain cursorToData(Cursor c) {
            return cursorToChain(c);
        }

        @Override
        public List<Chain> cursorToDataList(Cursor c) {           
            return cursorToChainList(c);
        }

    }

    class ChainAsyncStatement extends AsyncStatement<Chain> {

        public ChainAsyncStatement(DBHelper helper, String sqlStatement,
                es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option option,
                DataAccessCallbacks<Chain> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
