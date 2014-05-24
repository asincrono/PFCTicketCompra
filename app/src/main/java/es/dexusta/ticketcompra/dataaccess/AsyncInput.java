package es.dexusta.ticketcompra.dataaccess;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;

public abstract class AsyncInput<T extends DBObject> extends AsyncTask<Void, Void, List<T>> {
    private static final String    TAG   = "AsyncInputNew";
    private static final boolean   DEBUG = true;

    private DataAccessCallbacks<T> mListener;
    private DBHelper               mHelper;
    private Operation              mOperation;
    private List<T>                mDataList;
    private boolean                mResult;
    private int                    mProcessed;

    public AsyncInput(DBHelper helper, List<T> dataList, Operation operation,
            DataAccessCallbacks<T> listener) {
        mHelper = helper;
        mDataList = dataList;
        mOperation = operation;
        mListener = listener;
    }

    /*
     * Abstract methods.
     */

    public abstract String getTableName();

    public abstract String getIdName();

    public abstract ContentValues getValues(T data);

    @Override
    protected List<T> doInBackground(Void... nothing) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            mResult = true;
            long id;

            // Just for debugging:
            ContentValues cv;
            
            switch (mOperation) {
            case INSERT:
                for (T data : mDataList) {
                    cv = getValues(data);
                    id = db.insert(getTableName(), null, cv);
                    if (id > 0) {                        
                        data.setId(id);
                        ++mProcessed;
                    } else {
                        mResult = false;
                    }
                }
                break;
            case UPDATE:
                for (T data : mDataList) {
                    if (db.update(getTableName(), getValues(data), getIdName() + " = " + data.getId(),
                            null) > 0) {
                        ++mProcessed;                    
                    }
                    else {
                        mResult = false;
                    }
                }
                break;
            case DELETE:
                if (mDataList == null) {
                    db.delete(getTableName(), null, null);
                }
                else {
                    for (T data: mDataList) {                
                        if (db.delete(getTableName(), getIdName() + " = " + data.getId(), null) > 0) {
                            ++mProcessed;
                        }
                        else {
                            mResult = false;
                        }
                    }
                }
                break;            
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            // TODO: CHECKING IF CLOSING DE DB IS "A PROBLEM". 
            db.close();
        }

        return mDataList;
    }

    @Override
    protected void onPostExecute(List<T> result) {
        if (mListener != null) {
            mListener.onDataProcessed(mProcessed, mDataList, mOperation, mResult);
        }
    }

}