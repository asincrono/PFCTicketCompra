package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Shop;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

public class ShopDataAccess extends DataAccess<Shop> {
    private static final String  TAG        = "ShopDataAccess";
    private static final boolean DEBUG      = true;

    private static final String  TABLE_NAME = DBHelper.TBL_SHOP;
    private static final String  ID         = DBHelper.T_SHOP_ID;
    private static final String  UNIV_ID    = DBHelper.T_SHOP_UNIVERSAL_ID;
    private static final String  CHAIN_ID   = DBHelper.T_SHOP_CHAIN_ID;
    private static final String  TOWN_ID    = DBHelper.T_SHOP_TOWN_ID;
    private static final String  TOWN_NAME  = DBHelper.T_SHOP_TOWN_NAME;
    // private static final String NAME = DBHelper.T_SHOP_NAME;
    private static final String  LATITUDE   = DBHelper.T_SHOP_LATIT;
    private static final String  LONGITUDE  = DBHelper.T_SHOP_LONGT;
    private static final String  ADDRESS    = DBHelper.T_SHOP_ADDR;
    private static final String  UPDATED    = DBHelper.T_SHOP_UPDATED;

    private DBHelper             mHelper;

    public ShopDataAccess(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Shop data) {
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
            cv.put(CHAIN_ID, data.getChainId());
            cv.put(TOWN_ID, data.getTownId());
            cv.put(TOWN_NAME, data.getTownName());
            // cv.put(NAME, data.getName());
            cv.put(LATITUDE, data.getLatitude());
            cv.put(LONGITUDE, data.getLongitude());
            cv.put(ADDRESS, data.getAddress());
            // Less confusing than use Boolean as there is no getBoolean from
            // cursor.
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }
        return cv;
    }

    public static final Shop cursorToData(Cursor c) {
        Shop shop = null;
        if (c != null && c.getCount() > 0) {
            shop = new Shop();
            shop.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            shop.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            shop.setChainId(c.getLong(c.getColumnIndexOrThrow(CHAIN_ID)));
            shop.setTownId(c.getLong(c.getColumnIndexOrThrow(TOWN_ID)));
            shop.setTownName(c.getString(c.getColumnIndexOrThrow(TOWN_NAME)));
            // shop.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
            shop.setLatitude(c.getDouble(c.getColumnIndexOrThrow(LATITUDE)));
            shop.setLongitude(c.getDouble(c.getColumnIndexOrThrow(LONGITUDE)));
            shop.setAddress(c.getString(c.getColumnIndexOrThrow(ADDRESS)));
            shop.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);
        }
        return shop;
    }

    public static final List<Shop> cursorToDataList(Cursor c) {
        List<Shop> list = null;

        if (c != null) {
            int savedPosition = c.getPosition();

            int idIndex = c.getColumnIndexOrThrow(ID);
            int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
            int chainIdIndex = c.getColumnIndexOrThrow(CHAIN_ID);
            int townIdIndex = c.getColumnIndexOrThrow(TOWN_ID);
            int townNameIndex = c.getColumnIndexOrThrow(TOWN_NAME);
            // int nameIndex = c.getColumnIndexOrThrow(NAME);
            int latitudeIndex = c.getColumnIndexOrThrow(LATITUDE);
            int longitudeIndex = c.getColumnIndexOrThrow(LONGITUDE);
            int addressIndex = c.getColumnIndexOrThrow(ADDRESS);
            int updatedIndex = c.getColumnIndexOrThrow(UPDATED);

            if (c.moveToFirst()) {
                list = new ArrayList<Shop>();
                Shop shop = null;
                do {
                    shop = new Shop();

                    shop.setId(c.getLong(idIndex));
                    shop.setUniversalId(c.getString(univIdIndex));
                    shop.setChainId(c.getLong(chainIdIndex));
                    shop.setTownId(c.getLong(townIdIndex));
                    shop.setTownName(c.getString(townNameIndex));
                    // shop.setName(c.getString(nameIndex));
                    shop.setLatitude(c.getDouble(latitudeIndex));
                    shop.setLongitude(c.getDouble(longitudeIndex));
                    shop.setAddress(c.getString(addressIndex));
                    shop.setUpdated(c.getInt(updatedIndex) > 0);

                    list.add(shop);
                } while (c.moveToNext());
            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Shop> listener = getCallback();
        if (listener != null) {
            new ShopAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    @Override
    public void list() {
        DataAccessCallbacks<Shop> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME;
            new ShopAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void insert(List<Shop> dataList) {
        new ShopAsyncInput(mHelper, dataList, INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Shop> dataList) {
        new ShopAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    @Override
    public void delete(List<Shop> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new ShopAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new ShopAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    @Override
    public void getCount() {
        DataAccessCallbacks<Shop> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new ShopAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
    }

    class ShopAsyncQuery extends AsyncQuery<Shop> {

        public ShopAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Shop> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Shop cursorToData(Cursor c) {
            return ShopDataAccess.cursorToData(c);
        }

        @Override
        public List<Shop> cursorToDataList(Cursor c) {
            return ShopDataAccess.cursorToDataList(c);
        }

    }

    class ShopAsyncInput extends AsyncInput<Shop> {

        public ShopAsyncInput(DBHelper helper, List<Shop> dataList, Operation operation,
                DataAccessCallbacks<Shop> listener) {
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
        public ContentValues getValues(Shop data) {
            return ShopDataAccess.getValues(data);
        }
    }

    class ShopAsyncStatement extends AsyncStatement<Shop> {

        public ShopAsyncStatement(DBHelper helper, String sqlStatement, Option option,
                DataAccessCallbacks<Shop> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
