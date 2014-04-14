package es.dexusta.ticketcompra.dataaccess;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Town;

public class TownDataAccess extends DataAccess<Town> {
    private static final boolean DEBUG = true;
    private static final String TAG = "TownDataAccess";

    private static final String TABLE_NAME = DBHelper.TBL_TOWN;
    private static final String ID = DBHelper.T_TOWN_ID;
    private static final String SUBREGION_ID = DBHelper.T_TOWN_SUBREGION_ID;
    private static final String NAME = DBHelper.T_TOWN_NAME;

    private DBHelper mHelper;
    private DataAccessCallbacks<Town> mListener;

    public TownDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    public void setCallback(DataAccessCallbacks<Town> listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    public boolean hasCallback() {
        return mListener != null;
    }

    @Override
    public void list() {
        if (mListener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
            new TownAsyncQuery(mHelper, rawQuery, null, mListener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        if (mListener != null) {
            new TownAsyncQuery(mHelper, rawQuery, args, mListener).execute();
        }
    }

    @Override
    public void insert(List<Town> dataList) {
        new TownAsyncInput(mHelper, dataList, Operation.INSERT, mListener).execute();
    }

    @Override
    public void update(List<Town> dataList) {
        new TownAsyncInput(mHelper, dataList, Operation.UPDATE, mListener).execute();        
    }

    @Override
    public void delete(List<Town> dataList) {
        if (dataList == null) {// would trigger a deleteAll()
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new TownAsyncInput(mHelper, dataList, Operation.DELETE, mListener).execute();        
    }

    @Override
    public void deleteAll() {
        new TownAsyncInput(mHelper, null, Operation.DELETE, mListener).execute();                
    }

    @Override
    public void getCount() {
        if (mListener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new TownAsyncStatement(mHelper, sqlStatement, Option.LONG, mListener).execute();
        }
    }

    public static final Town cursorToData(Cursor c) {
        Town town = null;

        if (c != null && c.getCount() > 0) {
            town = new Town();
            town.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            town.setSubregionId(c.getLong(c.getColumnIndexOrThrow(SUBREGION_ID)));
            town.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
        }

        return town;
    }

    public static final List<Town> cursorToDataList(Cursor c) {
        List<Town> list = null;

        if (c != null && c.getCount() > 0) {
            int idIndex = c.getColumnIndexOrThrow(ID);
            int subregionIdIndex = c.getColumnIndexOrThrow(SUBREGION_ID);
            int nameIndex = c.getColumnIndexOrThrow(NAME);

            int currentPosition = c.getPosition();
            c.moveToFirst();


            Town town;
            list = new ArrayList<Town>();
            do {
                town = new Town();                
                town.setId(c.getLong(idIndex));
                town.setSubregionId(c.getLong(subregionIdIndex));
                town.setName(c.getString(nameIndex));

                list.add(town);
            } while (c.moveToNext());

            c.moveToPosition(currentPosition);            
        }

        return list;
    }

    public static final ContentValues getValues(Town data) {
        ContentValues cv = null;

        if (data != null) {
            cv = new ContentValues();

            long id = data.getId();
            if (id > 0) {
                cv.put(ID, data.getId());
            }

            cv.put(SUBREGION_ID, data.getSubregionId());
            cv.put(NAME, data.getName());
        }

        return cv;
    }

    class TownAsyncQuery extends AsyncQuery<Town> {

        public TownAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Town> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Town cursorToData(Cursor c) {
            return TownDataAccess.cursorToData(c);
        }

        @Override
        public List<Town> cursorToDataList(Cursor c) {
            return TownDataAccess.cursorToDataList(c);
        }

    }

    class TownAsyncInput extends AsyncInput<Town> {

        public TownAsyncInput(DBHelper helper, List<Town> dataList, Operation operation,
                DataAccessCallbacks<Town> listener) {
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
        public ContentValues getValues(Town data) {
            return TownDataAccess.getValues(data);
        }       
    }

    class TownAsyncStatement extends AsyncStatement<Town> {

        public TownAsyncStatement(DBHelper helper, String sqlStatement,
                es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option option,
                DataAccessCallbacks<Town> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }

}
