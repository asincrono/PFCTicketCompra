package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Subregion;

public class SubregionDataAccess extends DataAccess<Subregion> {
    private static final String  TAG   = "SubregionDataAccess";
    private static final boolean DEBUG = true;

    private static final String TABLE_NAME = DBHelper.TBL_SUBREGION;
    private static final String ID         = DBHelper.T_SUBREGION_ID;
    private static final String REGION_ID  = DBHelper.T_SUBREGION_REGION_ID;
    private static final String NAME       = DBHelper.T_SUBREGION_NAME;

    private DBHelper                       mHelper;
    private DataAccessCallbacks<Subregion> mListener;

    public SubregionDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    public static Subregion cursorToSubregion(Cursor c) {
        Subregion subregion = null;

        if (c != null && c.getCount() > 0) {
            subregion = new Subregion();
            subregion.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            subregion.setRegionId(c.getLong(c.getColumnIndexOrThrow(REGION_ID)));
            subregion.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
        }

        return subregion;
    }

    public static List<Subregion> cursorToSubregionList(Cursor c) {
        List<Subregion> list = null;

        if (c != null && c.getCount() > 0) {
            int currentPosition = c.getPosition();

            int idIndex = c.getColumnIndexOrThrow(ID);
            int regionIdIndex = c.getColumnIndexOrThrow(REGION_ID);
            int nameIndex = c.getColumnIndexOrThrow(NAME);

            Subregion subregion;
            list = new ArrayList<Subregion>();

            c.moveToFirst();
            do {
                subregion = new Subregion();
                subregion.setId(c.getLong(idIndex));
                subregion.setRegionId(c.getLong(regionIdIndex));
                subregion.setName(c.getString(nameIndex));
                list.add(subregion);
            } while (c.moveToNext());

            c.moveToPosition(currentPosition);
        }

        return list;
    }

    public static ContentValues getValues(Subregion data) {
        ContentValues cv = new ContentValues();

        long id = data.getId();
        if (id > 0) {
            cv.put(ID, data.getId());
        }

        cv.put(REGION_ID, data.getRegionId());
        cv.put(NAME, data.getName());

        return cv;
    }

    public void setCallback(DataAccessCallbacks<Subregion> listener) {
        mListener = listener;
    }

    public boolean hasCallback() {
        return mListener != null;
    }

    @Override
    public void list() {
        if (mListener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDERY BY " + NAME;
            new SubregionAsyncQuery(mHelper, rawQuery, null, mListener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        if (mListener != null) {
            new SubregionAsyncQuery(mHelper, rawQuery, args, mListener).execute();
        }
    }

    @Override
    public void insert(List<Subregion> dataList) {
        new SubregionAsyncInput(mHelper, dataList, Operation.INSERT, mListener).execute();
    }

    @Override
    public void update(List<Subregion> dataList) {
        new SubregionAsyncInput(mHelper, dataList, Operation.UPDATE, mListener).execute();

    }

    @Override
    public void delete(List<Subregion> dataList) {
        if (dataList == null) {// would trigger a deleteAll()
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new SubregionAsyncInput(mHelper, dataList, Operation.DELETE, mListener).execute();

    }

    @Override
    public void deleteAll() {
        new SubregionAsyncInput(mHelper, null, Operation.DELETE, mListener).execute();
    }

    @Override
    public void getCount() {
        if (mListener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new SubregionAsyncStatment(mHelper, sqlStatement, Option.LONG, mListener).execute();
        }
    }

    class SubregionAsyncQuery extends AsyncQuery<Subregion> {

        public SubregionAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                                   DataAccessCallbacks<Subregion> listener) {
            super(helper, rawQuery, args, listener);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Subregion cursorToData(Cursor c) {
            return SubregionDataAccess.cursorToSubregion(c);
        }

        @Override
        public List<Subregion> cursorToDataList(Cursor c) {
            return SubregionDataAccess.cursorToSubregionList(c);
        }

    }

    class SubregionAsyncInput extends AsyncInput<Subregion> {

        public SubregionAsyncInput(DBHelper helper, List<Subregion> dataList, Operation operation,
                                   DataAccessCallbacks<Subregion> listener) {
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
        public ContentValues getValues(Subregion data) {
            return SubregionDataAccess.getValues(data);
        }

    }

    class SubregionAsyncStatment extends AsyncStatement<Subregion> {

        public SubregionAsyncStatment(DBHelper helper, String sqlStatement,
                                      es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option option,
                                      DataAccessCallbacks<Subregion> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }

}
