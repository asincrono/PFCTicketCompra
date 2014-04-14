package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Region;

public class RegionDataAccess extends DataAccess<Region> {
    private static final String TAG = "RegionDataAccess";
    private static final boolean DEBUG = true;

    private static final String TABLE_NAME = DBHelper.TBL_REGION;

    private static final String ID = DBHelper.T_REGION_ID;
    private static final String NAME = DBHelper.T_REGION_NAME;

    private DBHelper mHelper;

    public RegionDataAccess(DBHelper helper) {
        mHelper = helper;        
    }
    
    public static final Region cursorToData(Cursor c) {
        Region region = null;
        if (c != null && c.getCount() > 0) {
            region = new Region();
            region.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            region.setName(c.getColumnName(c.getColumnIndexOrThrow(NAME)));
        }
        return region;
    }

    public static final List<Region> cursorToDataList(Cursor c) {
        List<Region> list = null;

        if (c != null && c.getCount() > 0) {
            int currentPosition = c.getPosition();

            // We obtain all the indexes at once.
            int idIndex = c.getColumnIndexOrThrow(ID);
            int nameIndex = c.getColumnIndexOrThrow(NAME);

            Region region;
            list = new ArrayList<Region>();

            c.moveToFirst();
            do {
                region = new Region();
                region.setId(c.getLong(idIndex));
                region.setName(c.getString(nameIndex));

                list.add(region);
            } while (c.moveToNext());

            c.moveToPosition(currentPosition);

        }

        return list;
    }
    
    public static final ContentValues getValues(Region data) {
        ContentValues values = null;

        if (data != null) {
            values = new ContentValues();

            long id = data.getId();
            if (id > 0) {
                values.put(ID, data.getId());
            }

            values.put(NAME, data.getName());
        }

        return values;
    }
   
    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Region> listener = getCallback();
        if (listener != null) { // no point on doing stuff if no one sees it.
            new RegionAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }
        

    // TODO: PROBAR BORRADO REGIONES ESPECÍFICAS.
    
    @Override
    public void list() {
        DataAccessCallbacks<Region> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
            new RegionAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void insert(List<Region> list) {
        new RegionAsyncInput(mHelper, list, Operation.INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Region> list) {
        new RegionAsyncInput(mHelper, list, Operation.UPDATE, getCallback()).execute();
    }

    public void delete(List<Region> list) {
        // we need to check that data != null as we internally use data = null to
        // erase ALL THE TABLE. See deleteAll();
        if (list == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new RegionAsyncInput(mHelper, list, Operation.DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new RegionAsyncInput(mHelper, null, Operation.DELETE, getCallback()).execute();
    }
    
    @Override
    public void getCount() {
        DataAccessCallbacks<Region> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new RegionAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
    }

    class RegionAsyncQuery extends AsyncQuery<Region> {

        public RegionAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Region> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Region cursorToData(Cursor c) {
            return RegionDataAccess.cursorToData(c);
        }

        @Override
        public List<Region> cursorToDataList(Cursor c) {
            return RegionDataAccess.cursorToDataList(c);
        }
    }

    class RegionAsyncInput extends AsyncInput<Region> {

        public RegionAsyncInput(DBHelper helper, List<Region> data, Operation operation,
                DataAccessCallbacks<Region> listener) {
            super(helper, data, operation, listener);
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
        public ContentValues getValues(Region data) {
            return RegionDataAccess.getValues(data);
        }

    }

    class RegionAsyncStatement extends AsyncStatement<Region> {

        public RegionAsyncStatement(DBHelper helper, String sqlStatement,
                es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option option,
                DataAccessCallbacks<Region> listener) {
            super(helper, sqlStatement, option, listener);
        }        

    }
}
