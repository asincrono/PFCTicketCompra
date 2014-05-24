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
public abstract class AsyncInsert<T extends DBObject> extends AsyncTask<Void, Void, List<T>> {
    private DataAccessCallback<T> mCallback;

    private DBHelper        mHelper;
    private List<T>         mData;
    private Types.Operation mOperation;

    private boolean mResult;

    public AsyncInsert(DBHelper helper, List<T> data, DataAccessCallback<T> callback) {
        mHelper = helper;
        mData = data;
        mCallback = callback;
    }

    public abstract String getTableName();

    public abstract String getIdName();

    public abstract ContentValues getValues(T data);

    @Override
    protected List<T> doInBackground(Void... params) {
        int inserted = 0;

        if (mHelper != null) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            if (db != null) {
                try {
                    db.beginTransaction();

                    long id;

                    for (T data : mData) {
                        id = db.insertOrThrow(getTableName(), null, getValues(data));
                        if (id > 0) {
                            data.setId(id);
                            inserted += 1;
                        }
                    }

                    mResult = (inserted == mData.size());

                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                    db.close();
                }
            }

        } else {
            mResult = false;
        }

        return mData;
    }

    @Override
    protected void onPostExecute(List<T> ts) {
        if (mCallback != null) {
            mCallback.onComplete(mData, mResult);
        }
    }
}
