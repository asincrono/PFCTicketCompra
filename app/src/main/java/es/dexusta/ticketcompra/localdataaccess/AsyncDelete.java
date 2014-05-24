package es.dexusta.ticketcompra.localdataaccess;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;

/**
 * Created by asincrono on 22/05/14.
 */
public abstract class AsyncDelete<T extends DBObject> extends AsyncTask<Void, Void, Void> {
    private DataAccessCallback<T> mCallback;
    private DBHelper mHelper;
    private boolean mResult;
    private List<T> mData;

    public AsyncDelete(DBHelper helper, List<T> dataList, DataAccessCallback<T> callback) {
        mHelper = helper;
        mData = dataList;
        mCallback = callback;
    }

    public abstract String getTableName();

    public abstract String getIdName();

    @Override
    protected Void doInBackground(Void... params) {
        if (mHelper != null) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {

                    String tableName = getTableName();
                    String idName = getIdName();
                    int deleted = 0;

                    if (mData == null) {
                        deleted = db.delete(tableName, null, null);
                    } else {
                        for (T data : mData) {
                            deleted += db.delete(tableName, idName + " = " + data.getId(), null);
                        }
                    }

                    mResult = (mData.size() == deleted);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
            } else {
                mResult = false;
            }
        } else {
            mResult = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        if (mCallback != null) {
            mCallback.onComplete(mData, mResult);
        }
    }
}
