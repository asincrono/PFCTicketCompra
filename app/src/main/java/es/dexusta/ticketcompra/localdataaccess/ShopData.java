package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Shop;

/**
 * Created by asincrono on 22/05/14.
 */
public class ShopData extends DataAccess<Shop> {
    private static final String TAG = "ShopData";

    private static final String  TABLE_NAME = DBHelper.TBL_SHOP;
    private static final String  ID         = DBHelper.T_SHOP_ID;
    private static final String  UNIV_ID    = DBHelper.T_SHOP_UNIVERSAL_ID;
    private static final String  CHAIN_ID   = DBHelper.T_SHOP_CHAIN_ID;
    private static final String  TOWN_ID    = DBHelper.T_SHOP_TOWN_ID;
    private static final String  TOWN_NAME  = DBHelper.T_SHOP_TOWN_NAME;
    private static final String  LATITUDE   = DBHelper.T_SHOP_LATIT;
    private static final String  LONGITUDE  = DBHelper.T_SHOP_LONGT;
    private static final String  ADDRESS    = DBHelper.T_SHOP_ADDR;
    private static final String  UPDATED    = DBHelper.T_SHOP_UPDATED;

    private DBHelper mHelper;

    public ShopData(DBHelper helper) {
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
            cv.put(LATITUDE, data.getLatitude());
            cv.put(LONGITUDE, data.getLongitude());
            cv.put(ADDRESS, data.getAddress());
            // Less confusing than use Boolean as there is no getBoolean from
            // cursor.
            cv.put(UPDATED, data.isUpdated() ? 1 : 0);
        }
        return cv;
    }

    public static Shop cursorToData(Cursor c) {
        Shop shop = null;
        if (c != null && c.getCount() > 0) {
            shop = new Shop();
            shop.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            shop.setUniversalId(c.getString(c.getColumnIndexOrThrow(UNIV_ID)));
            shop.setChainId(c.getLong(c.getColumnIndexOrThrow(CHAIN_ID)));
            shop.setTownId(c.getLong(c.getColumnIndexOrThrow(TOWN_ID)));
            shop.setTownName(c.getString(c.getColumnIndexOrThrow(TOWN_NAME)));
            shop.setLatitude(c.getDouble(c.getColumnIndexOrThrow(LATITUDE)));
            shop.setLongitude(c.getDouble(c.getColumnIndexOrThrow(LONGITUDE)));
            shop.setAddress(c.getString(c.getColumnIndexOrThrow(ADDRESS)));
            shop.setUpdated(c.getInt(c.getColumnIndexOrThrow(UPDATED)) > 0);
        }
        return shop;
    }

    public static List<Shop> cursorToDataList(Cursor c) {
        List<Shop> list = null;

        if (c != null) {
            int savedPosition = c.getPosition();

            int idIndex = c.getColumnIndexOrThrow(ID);
            int univIdIndex = c.getColumnIndexOrThrow(UNIV_ID);
            int chainIdIndex = c.getColumnIndexOrThrow(CHAIN_ID);
            int townIdIndex = c.getColumnIndexOrThrow(TOWN_ID);
            int townNameIndex = c.getColumnIndexOrThrow(TOWN_NAME);
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
    public void list(DataAccessCallback<Shop> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME;
        new ShopAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sqlQuery, String[] args, DataAccessCallback<Shop> callback) {
        new ShopAsyncRead(mHelper, sqlQuery, args, callback).execute();
    }

    @Override
    public void insert(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        new ShopAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        new ShopAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new ShopAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Shop> callback) {
        new ShopAsyncDelete(mHelper, null, callback).execute();
    }

    class ShopAsyncRead extends AsyncRead<Shop> {
        ShopAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Shop> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Shop cursorToData(Cursor c) {
            return ShopData.cursorToData(c);
        }

        @Override
        public List<Shop> cursorToDataList(Cursor c) {
            return ShopData.cursorToDataList(c);
        }
    }

    class ShopAsyncInsert extends AsyncInsert<Shop> {
        ShopAsyncInsert(DBHelper helper, List<Shop> data, DataAccessCallback<Shop> callback) {
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
        public ContentValues getValues(Shop data) {
            return ShopData.getValues(data);
        }
    }

    class ShopAsyncUpdate extends AsyncUpdate<Shop> {
        ShopAsyncUpdate(DBHelper helper, List<Shop> data, DataAccessCallback<Shop> callback) {
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
        public ContentValues getValues(Shop data) {
            return ShopData.getValues(data);
        }
    }

    class ShopAsyncDelete extends AsyncDelete<Shop> {
        ShopAsyncDelete(DBHelper helper, List<Shop> dataList, DataAccessCallback<Shop> callback) {
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
