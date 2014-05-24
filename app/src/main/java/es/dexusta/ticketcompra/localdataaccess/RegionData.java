package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Region;

/**
 * Created by asincrono on 22/05/14.
 */
public class RegionData extends DataAccess<Region> {
    private static final String TAG = "RegionData";

    private static final String TABLE_NAME = DBHelper.TBL_REGION;

    private static final String ID   = DBHelper.T_REGION_ID;
    private static final String NAME = DBHelper.T_REGION_NAME;


    private DBHelper mHelper;

    public RegionData(DBHelper helper) {
        mHelper = helper;
    }

    public static Region cursorToRegion(Cursor c) {
        Region region = null;
        if (c != null && c.getCount() > 0) {
            region = new Region();
            region.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            region.setName(c.getColumnName(c.getColumnIndexOrThrow(NAME)));
        }
        return region;
    }

    public static List<Region> cursorToRegionList(Cursor c) {
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

    public static ContentValues getValues(Region data) {
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
    public void list(DataAccessCallback<Region> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new RegionAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Region> callback) {
        new RegionAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Region> dataList, DataAccessCallback<Region> callback) {
        new RegionAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Region> dataList, DataAccessCallback<Region> callback) {
        new RegionAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Region> dataList, DataAccessCallback<Region> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new RegionAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Region> callback) {
        new RegionAsyncDelete(mHelper, null, callback).execute();
    }

    class RegionAsyncRead extends AsyncRead<Region> {
        RegionAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Region> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Region cursorToData(Cursor c) {
            return RegionData.cursorToRegion(c);
        }

        @Override
        public List<Region> cursorToDataList(Cursor c) {
            return RegionData.cursorToRegionList(c);
        }
    }

    class RegionAsyncInsert extends AsyncInsert<Region> {
        RegionAsyncInsert(DBHelper helper, List<Region> data, DataAccessCallback<Region> callback) {
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
        public ContentValues getValues(Region data) {
            return RegionData.getValues(data);
        }
    }

    class RegionAsyncUpdate extends AsyncUpdate<Region> {
        RegionAsyncUpdate(DBHelper helper, List<Region> data, DataAccessCallback<Region> callback) {
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
        public ContentValues getValues(Region data) {
            return RegionData.getValues(data);
        }
    }

    class RegionAsyncDelete extends AsyncDelete<Region> {
        RegionAsyncDelete(DBHelper helper, List<Region> dataList, DataAccessCallback<Region> callback) {
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
