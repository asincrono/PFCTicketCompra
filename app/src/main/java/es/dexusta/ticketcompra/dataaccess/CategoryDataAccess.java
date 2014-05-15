package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.DBHelper;

import static es.dexusta.ticketcompra.dataaccess.Types.Operation.DELETE;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.INSERT;
import static es.dexusta.ticketcompra.dataaccess.Types.Operation.UPDATE;

public class CategoryDataAccess extends DataAccess<Category> {
    private static final String TABLE_NAME = DBHelper.TBL_CATEGORY;
    private static final String ID = DBHelper.T_CAT_ID;
    private static final String NAME = DBHelper.T_CAT_NAME;
    private static final String DESCRIPTION = DBHelper.T_CAT_DESCR;

    private DBHelper                                 mHelper;
    

    public CategoryDataAccess(DBHelper helper) {
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
                Category cat = null;
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

    public void list() {
        DataAccessCallbacks<Category> listener = getCallback();
        if (listener!= null) {
            String rawQueryString = "SELECT * FROM " + DBHelper.TBL_CATEGORY;
            new CategoryAsyncQuery(mHelper, rawQueryString, null, listener).execute();
        }
    }

    public void query(String rawQuery, String[] args) {
        DataAccessCallbacks<Category> listener = getCallback();
        if (listener != null) {
            new CategoryAsyncQuery(mHelper, rawQuery, args, listener).execute();
        }
    }

    public void insert(List<Category> dataList) {
        // La operación debe de hacerse haya o no listeners atentos a ella.
        new CategoryAsyncInput(mHelper, dataList, INSERT, getCallback()).execute();

    }

    public void update(List<Category> dataList) {
        new CategoryAsyncInput(mHelper, dataList, UPDATE, getCallback()).execute();
    }

    public void delete(List<Category> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Data suplied to delete can't be null.");
        }
        new CategoryAsyncInput(mHelper, dataList, DELETE, getCallback()).execute();
    }

    @Override
    public void deleteAll() {
        new CategoryAsyncInput(mHelper, null, DELETE, getCallback()).execute();
    }

    public void getCount() {
        DataAccessCallbacks<Category> listener = getCallback();
        if (listener != null) {
            String sqlStatement = "SELECT COUNT(*) FROM " + TABLE_NAME;
            new CategoryAsyncStatement(mHelper, sqlStatement, Option.LONG, listener).execute();
        }
    }

    class CategoryAsyncQuery extends AsyncQuery<Category> {
        public CategoryAsyncQuery(DBHelper helper, String rawQuery, String[] args,
                DataAccessCallbacks<Category> listener) {
            super(helper, rawQuery, args, listener);
        }

        @Override
        public Category cursorToData(Cursor c) {
            return cursorToCategory(c);
        }

        @Override
        public List<Category> cursorToDataList(Cursor c) {            
            return cursorToCategoryList(c);
        }        
    }

    class CategoryAsyncInput extends AsyncInput<Category> {

        public CategoryAsyncInput(
                DBHelper helper,
                List<Category> dataList,
                Operation operation,
                DataAccessCallbacks<Category> listener) {            
            super(helper, dataList, operation, listener);
            // TODO Auto-generated constructor stub
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
            return CategoryDataAccess.getValues(data);
        }
    }

    class CategoryAsyncStatement extends AsyncStatement<Category> {
        public CategoryAsyncStatement(DBHelper helper, String sqlStatement,
                Option option,
                DataAccessCallbacks<Category> listener) {
            super(helper, sqlStatement, option, listener);
        }

    }
}
