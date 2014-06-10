package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Subregion;

/**
 * Created by asincrono on 22/05/14.
 */
public class SubregionData extends DataAccess<Subregion> {
    private static final String TAG = "SubregionData";

    private static final String TABLE_NAME = DBHelper.TBL_SUBREGION;
    private static final String ID         = DBHelper.T_SUBREGION_ID;
    private static final String REGION_ID  = DBHelper.T_SUBREGION_REGION_ID;
    private static final String NAME       = DBHelper.T_SUBREGION_NAME;

    private DBHelper mHelper;

    public SubregionData(DBHelper helper) {
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

    @Override
    public void list(DataAccessCallback<Subregion> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new SubregionAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sqlQuery, String[] args, DataAccessCallback<Subregion> callback) {
        new SubregionAsyncRead(mHelper, sqlQuery, args, callback).execute();
    }

    @Override
    public void insert(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        new SubregionAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        new SubregionAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new SubregionAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Subregion> callback) {
        new SubregionAsyncDelete(mHelper, null, callback).execute();
    }

    class SubregionAsyncRead extends AsyncRead<Subregion> {
        SubregionAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Subregion> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Subregion cursorToData(Cursor c) {
            return SubregionData.cursorToSubregion(c);
        }

        @Override
        public List<Subregion> cursorToDataList(Cursor c) {
            return SubregionData.cursorToSubregionList(c);
        }
    }

    class SubregionAsyncInsert extends AsyncInsert<Subregion> {
        SubregionAsyncInsert(DBHelper helper, List<Subregion> data, DataAccessCallback<Subregion> callback) {
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
        public ContentValues getValues(Subregion data) {
            return SubregionData.getValues(data);
        }
    }

    class SubregionAsyncUpdate extends AsyncUpdate<Subregion> {
        SubregionAsyncUpdate(DBHelper helper, List<Subregion> data, DataAccessCallback<Subregion> callback) {
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
        public ContentValues getValues(Subregion data) {
            return SubregionData.getValues(data);
        }
    }

    class SubregionAsyncDelete extends AsyncDelete<Subregion> {
        SubregionAsyncDelete(DBHelper helper, List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
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
