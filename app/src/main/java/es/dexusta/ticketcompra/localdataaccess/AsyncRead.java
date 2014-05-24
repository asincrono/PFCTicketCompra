package es.dexusta.ticketcompra.localdataaccess;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;

/**
 * Created by asincrono on 22/05/14.
 */
public abstract class AsyncRead<T extends DBObject> extends AsyncTask<String, Void, List<T>> {
    private DataAccessCallback<T> mCallback;

    private DBHelper mHelper;
    private String   mSQL;
    private String[] mArgs;
    private boolean  mResult;

    public AsyncRead(DBHelper helper, String sql, String[] args, DataAccessCallback<T> callback) {
        mHelper = helper;
        mSQL = sql;
        mArgs = args;

        mCallback = callback;
    }

    public abstract T cursorToData(Cursor c);

    public abstract List<T> cursorToDataList(Cursor c);

    @Override
    protected List<T> doInBackground(String... params) {
        List<T> list = null;

        if (mCallback != null && mHelper != null) {
            Cursor c = null;

            SQLiteDatabase db = mHelper.getReadableDatabase();

            if (db != null) {
                try {
                    c = db.rawQuery(mSQL, mArgs);

                    if (c.moveToFirst()) {
                        list = cursorToDataList(c);
                        mResult = true;
                    } else {
                        mResult = false;
                    }

                } finally {
                    if (c != null) c.close();
                }
            } else {
                mResult = false;
            }
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<T> list) {
        if (mCallback != null) {
            mCallback.onComplete(list, mResult);
        }
    }
}
