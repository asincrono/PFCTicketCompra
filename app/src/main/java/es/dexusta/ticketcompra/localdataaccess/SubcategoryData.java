package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Subcategory;

/**
 * Created by asincrono on 22/05/14.
 */
public class SubcategoryData extends DataAccess<Subcategory> {
    private static final String TAG = "SubcategoryData";


    private static final String TABLE_NAME  = DBHelper.TBL_SUBCATEGORY;
    private static final String ID          = DBHelper.T_SUBCAT_ID;
    private static final String CATEGORY_ID = DBHelper.T_SUBCAT_CAT_ID;
    private static final String NAME        = DBHelper.T_SUBCAT_NAME;
    private static final String DESCRIPTION = DBHelper.T_SUBCAT_DESCR;

    private DBHelper mHelper;

    public SubcategoryData(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Subcategory subcat) {
        ContentValues cv = null;

        if (subcat != null) {
            cv = new ContentValues();
            if (subcat.getId() > 0) {
                cv.put(ID, subcat.getId());
            }
            cv.put(CATEGORY_ID, subcat.getCategoryId());
            cv.put(NAME, subcat.getName());
            cv.put(DESCRIPTION, subcat.getDescription());
        }

        return cv;
    }

    public static Subcategory cursorToSubcategory(Cursor c) {
        Subcategory subcat = null;

        if (c != null && c.getCount() > 0) {
            subcat = new Subcategory();
            subcat.setId(c.getLong(c.getColumnIndexOrThrow(ID)));
            subcat.setCategoryId(c.getLong(c.getColumnIndexOrThrow(CATEGORY_ID)));
            subcat.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
            subcat.setDescription(c.getString(c.getColumnIndexOrThrow(DESCRIPTION)));
        }
        return subcat;
    }

    public static List<Subcategory> cursorToSubcategoryList(Cursor c) {
        List<Subcategory> list = null;
        if (c != null) {
            int savedPosition = c.getPosition();

            if (c.moveToFirst()) {
                list = new ArrayList<Subcategory>();

                int idIndex = c.getColumnIndexOrThrow(ID);
                int catgoryIdIndex = c.getColumnIndexOrThrow(CATEGORY_ID);
                int nameIndex = c.getColumnIndexOrThrow(NAME);
                int descriptionIndex = c.getColumnIndexOrThrow(DESCRIPTION);

                Subcategory subcat;
                do {
                    subcat = new Subcategory();

                    subcat.setId(c.getLong(idIndex));
                    subcat.setCategoryId(c.getLong(catgoryIdIndex));
                    subcat.setName(c.getString(nameIndex));
                    subcat.setDescription(c.getString(descriptionIndex));

                    list.add(subcat);
                } while (c.moveToNext());
            }
            c.moveToPosition(savedPosition);
        }
        return list;
    }

    @Override
    public void list(DataAccessCallback<Subcategory> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new SubcategoryAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Subcategory> callback) {
        new SubcategoryAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        new SubcategoryAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        new SubcategoryAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new SubcategoryAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Subcategory> callback) {
        new SubcategoryAsyncDelete(mHelper, null, callback).execute();
    }

    class SubcategoryAsyncRead extends AsyncRead<Subcategory> {
        SubcategoryAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Subcategory> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Subcategory cursorToData(Cursor c) {
            return SubcategoryData.cursorToSubcategory(c);
        }

        @Override
        public List<Subcategory> cursorToDataList(Cursor c) {
            return SubcategoryData.cursorToSubcategoryList(c);
        }
    }

    class SubcategoryAsyncInsert extends AsyncInsert<Subcategory> {
        SubcategoryAsyncInsert(DBHelper helper, List<Subcategory> data, DataAccessCallback<Subcategory> callback) {
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
        public ContentValues getValues(Subcategory data) {
            return SubcategoryData.getValues(data);
        }
    }

    class SubcategoryAsyncUpdate extends AsyncUpdate<Subcategory> {
        SubcategoryAsyncUpdate(DBHelper helper, List<Subcategory> data, DataAccessCallback<Subcategory> callback) {
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
        public ContentValues getValues(Subcategory data) {
            return SubcategoryData.getValues(data);
        }
    }

    class SubcategoryAsyncDelete extends AsyncDelete<Subcategory> {
        SubcategoryAsyncDelete(DBHelper helper, List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
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
