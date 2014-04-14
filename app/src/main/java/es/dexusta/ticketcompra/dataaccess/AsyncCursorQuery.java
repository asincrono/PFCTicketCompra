package es.dexusta.ticketcompra.dataaccess;

import android.database.Cursor;
import android.os.AsyncTask;

import es.dexusta.ticketcompra.model.DBHelper;

public class AsyncCursorQuery extends AsyncTask<Void, Void, Cursor>{
    private DBHelper mHelper;
    private String mRawQuery;
    private String[] mQueryArgs;
    private DataCursorListener mListener;
    
    public AsyncCursorQuery(DBHelper helper, String rawQuery, String[] queryArgs, DataCursorListener listener) {
        mHelper = helper;
        mRawQuery = rawQuery;
        mQueryArgs = queryArgs;
        mListener = listener;
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        return mHelper.getReadableDatabase().rawQuery(mRawQuery, mQueryArgs);        
    }

    @Override
    protected void onPostExecute(Cursor c) {
        mListener.dataReceived(c);
    }
    
    

}
