package es.dexusta.ticketcompra.localdataaccess;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;

/**
 * Created by asincrono on 22/05/14.
 */
public abstract class AsyncUpdate<T extends DBObject> extends AsyncTask<Void, Void, Void> {
    private DataAccessCallback<T> mCallback;

    private DBHelper mHelper;
    private List<T> mData;

    private boolean mResult = false;

    public AsyncUpdate(DBHelper helper, List<T> data, DataAccessCallback<T> callback) {
        mHelper = helper;
        mData = data;
        mCallback = callback;
    }

    public abstract String getTableName();

    public abstract String getIdName();

    public abstract ContentValues getValues(T data);

    @Override
    protected Void doInBackground(Void... params) {

        if (mHelper != null) {
            SQLiteDatabase db = mHelper.getWritableDatabase();

            if (db != null) {
                db.beginTransaction();

                try {
                    int updated = 0;

                    String tableName = getTableName();
                    String idName = getIdName();

                    for (T data : mData) {
                        updated += db.update(tableName, getValues(data), idName + " = " + data.getId(), null);
                    }

                    mResult = (updated == mData.size());

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
    protected void onPostExecute(Void aVoid) {
        if (mCallback != null) {
            mCallback.onComplete(mData, mResult);
        }
    }
}
