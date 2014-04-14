package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Subcategory;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

public class SubcategoryDataAccess extends DataAccess<Subcategory> {
    private static final String TAG   = "SubcategoryDataAccess";
    private static final String                    TABLE_NAME  = DBHelper.TBL_SUBCATEGORY;
    private static final String                    ID          = DBHelper.T_SUBCAT_ID;
    private static final String                    CATEGORY_ID = DBHelper.T_SUBCAT_CAT_ID;
    private static final String                    NAME        = DBHelper.T_SUBCAT_NAME;
    private static final String                    DESCRIPTION = DBHelper.T_SUBCAT_DESCR;
    private static boolean      DEBUG = false;
private DBHelper                               mHelper;

        public SubcategoryDataAccess(DBHelper helper) {
        mHelper = helper;
    };

    public static void setDebug(boolean debug) {
        DEBUG = debug;
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

                Subcategory subcat = null;
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
    public void list() {
        DataAccessCallbacks<Subcategory> listener = getCallback();
        if (listener != null) {
            String rawQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME;
            new SubcategoryAsyncQuery(mHelper, rawQuery, null, listener).execute();
        }
    }

    @Override
    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Subcategory> listener = getCallback();
        if (listener != null) {
            new SubcategoryAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    @Override
    public void insert(List<Subcategory> dataList) {
        new SubcategoryAsyncInput(mHelper, dataList, INSERT, getCallback()).execute();
    }

    @Override
    public void update(List<Subcategory> dataList) {
        new SubcategoryAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    @Override
    public void delete(List<Subcategory> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new SubcategoryAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new SubcategoryAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    @Override
    public void getCount() {
        DataAccessCallbacks<Subcategory> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new SubcategoryAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
    }

    class SubcategoryAsyncQuery extends AsyncQuery<Subcategory> {

        public SubcategoryAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Subcategory> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Subcategory cursorToData(Cursor c) {
            return cursorToSubcategory(c);
        }

        @Override
        public List<Subcategory> cursorToDataList(Cursor c) {
            return cursorToSubcategoryList(c);
        }
    }

    class SubcategoryAsyncInput extends AsyncInput<Subcategory> {

        public SubcategoryAsyncInput(DBHelper helper, List<Subcategory> dataList,
                Operation operation, DataAccessCallbacks<Subcategory> listener) {
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
        public ContentValues getValues(Subcategory data) {
            return SubcategoryDataAccess.getValues(data);
        }

    }

    class SubcategoryAsyncStatement extends AsyncStatement<Subcategory> {

        public SubcategoryAsyncStatement(DBHelper helper, String sqlStatement, Option option,
                DataAccessCallbacks<Subcategory> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
