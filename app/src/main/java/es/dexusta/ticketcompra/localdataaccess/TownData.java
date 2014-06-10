package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 22/05/14.
 */
public class TownData extends DataAccess<Town> {
    private static final String TAG = "TownData";

    private static final String TABLE_NAME   = DBHelper.TBL_TOWN;
    private static final String ID           = DBHelper.T_TOWN_ID;
    private static final String SUBREGION_ID = DBHelper.T_TOWN_SUBREGION_ID;
    private static final String NAME         = DBHelper.T_TOWN_NAME;

    private DBHelper mHelper;

    public TownData(DBHelper helper) {
        mHelper = helper;
    }

    public static Town cursorToTown(Cursor c) {
        Town town = null;

        if (c != null && c.getCount() > 0) {
            town = new Town();
            town.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            town.setSubregionId(c.getLong(c.getColumnIndexOrThrow(SUBREGION_ID)));
            town.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
        }

        return town;
    }

    public static List<Town> cursorToTownList(Cursor c) {
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

    public static ContentValues getValues(Town data) {
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

    @Override
    public void list(DataAccessCallback<Town> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new TownAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sqlQuery, String[] args, DataAccessCallback<Town> callback) {
        new TownAsyncRead(mHelper, sqlQuery, null, callback).execute();
    }

    @Override
    public void insert(List<Town> dataList, DataAccessCallback<Town> callback) {
        new TownAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Town> dataList, DataAccessCallback<Town> callback) {
        new TownAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Town> dataList, DataAccessCallback<Town> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new TownAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Town> callback) {
        new TownAsyncDelete(mHelper, null, callback).execute();
    }

    class TownAsyncRead extends AsyncRead<Town> {
        TownAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Town> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Town cursorToData(Cursor c) {
            return TownData.cursorToTown(c);
        }

        @Override
        public List<Town> cursorToDataList(Cursor c) {
            return TownData.cursorToTownList(c);
        }
    }

    class TownAsyncInsert extends AsyncInsert<Town> {
        TownAsyncInsert(DBHelper helper, List<Town> data, DataAccessCallback<Town> callback) {
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
        public ContentValues getValues(Town data) {
            return TownData.getValues(data);
        }
    }

    class TownAsyncUpdate extends AsyncUpdate<Town> {
        TownAsyncUpdate(DBHelper helper, List<Town> data, DataAccessCallback<Town> callback) {
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
        public ContentValues getValues(Town data) {
            return TownData.getValues(data);
        }
    }

    class TownAsyncDelete extends AsyncDelete<Town> {
        TownAsyncDelete(DBHelper helper, List<Town> dataList, DataAccessCallback<Town> callback) {
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
