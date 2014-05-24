package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.DBHelper;

/**
 * Created by asincrono on 22/05/14.
 */
public class CategoryData extends DataAccess<Category> {
    private static final String TAG = "CategoryData";

    private static final String TABLE_NAME  = DBHelper.TBL_CATEGORY;
    private static final String ID          = DBHelper.T_CAT_ID;
    private static final String NAME        = DBHelper.T_CAT_NAME;
    private static final String DESCRIPTION = DBHelper.T_CAT_DESCR;

    private DBHelper mHelper;

    public CategoryData(DBHelper helper) {
        mHelper = helper;
    }

    public static ContentValues getValues(Category c) {
        ContentValues cv = null;

        if (c != null) {
            cv = new ContentValues();

            long id = c.getId();
            if (id > 0) {
                cv.put(ID, id);
            }

            cv.put(NAME, c.getName());
            cv.put(DESCRIPTION, c.getDescription());
        }
        return cv;
    }

    public static Category cursorToCategory(Cursor c) {
        Category cat = null;

        if (c != null && c.getCount() > 0) {
            cat = new Category();
            cat.setId(c.getLong(c.getColumnIndex(ID)));
            cat.setName(c.getString(c.getColumnIndex(NAME)));
            cat.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
        }
        return cat;
    }

    public static  List<Category> cursorToCategoryList(Cursor c) {
        List<Category> list = null;

        if (c != null) {
            int savedPosition = c.getPosition();

            if (c.moveToFirst()) {

                int idIndex = c.getColumnIndexOrThrow(ID);
                int nameIndex = c.getColumnIndexOrThrow(NAME);
                int descriptionIndex = c.getColumnIndexOrThrow(DESCRIPTION);

                list = new ArrayList<Category>();
                Category cat;
                do {
                    cat = new Category();
                    cat.setId(c.getLong(idIndex));
                    cat.setName(c.getString(nameIndex));
                    cat.setDescription(c.getString(descriptionIndex));
                    list.add(cat);
                } while (c.moveToNext());
            }

            c.moveToPosition(savedPosition);
        }

        return list;
    }

    @Override
    public void list(DataAccessCallback<Category> callback) {
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
        new CategoryAsyncRead(mHelper, sql, null, callback).execute();
    }

    @Override
    public void read(String sql, String[] args, DataAccessCallback<Category> callback) {
        new CategoryAsyncRead(mHelper, sql, args, callback).execute();
    }

    @Override
    public void insert(List<Category> dataList, DataAccessCallback<Category> callback) {
        new CategoryAsyncInsert(mHelper, dataList, callback).execute();
    }

    @Override
    public void update(List<Category> dataList, DataAccessCallback<Category> callback) {
        new CategoryAsyncUpdate(mHelper, dataList, callback).execute();
    }

    @Override
    public void delete(List<Category> dataList, DataAccessCallback<Category> callback) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data supplied to delete can't be null.");
        }
        new CategoryAsyncDelete(mHelper, dataList, callback).execute();
    }

    @Override
    public void deleteAll(DataAccessCallback<Category> callback) {
        new CategoryAsyncDelete(mHelper, null, callback).execute();
    }

    class CategoryAsyncRead extends AsyncRead<Category> {
        CategoryAsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<Category> callback) {
            super(helper, sql, args, callback);
        }

        @Override
        public Category cursorToData(Cursor c) {
            return CategoryData.cursorToCategory(c);
        }

        @Override
        public List<Category> cursorToDataList(Cursor c) {
            return CategoryData.cursorToCategoryList(c);
        }
    }

    class CategoryAsyncInsert extends AsyncInsert<Category> {
        CategoryAsyncInsert(DBHelper helper, List<Category> data, DataAccessCallback<Category> callback) {
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
        public ContentValues getValues(Category data) {
            return CategoryData.getValues(data);
        }
    }

    class CategoryAsyncUpdate extends AsyncUpdate<Category> {
        CategoryAsyncUpdate(DBHelper helper, List<Category> data, DataAccessCallback<Category> callback) {
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
        public ContentValues getValues(Category data) {
            return CategoryData.getValues(data);
        }
    }

    class CategoryAsyncDelete extends AsyncDelete<Category> {
        CategoryAsyncDelete(DBHelper helper, List<Category> dataList, DataAccessCallback<Category> callback) {
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
