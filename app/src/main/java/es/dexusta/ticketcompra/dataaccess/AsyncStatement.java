package es.dexusta.ticketcompra.dataaccess;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;



public abstract class AsyncStatement<T extends DBObject> extends AsyncTask<Void, Void, Object> {
    private static final String TAG = "AsyncStatement";
    private static final boolean DEBUG = true;
    
    public enum Option {
        STRING, LONG, BLOB
    };
    
    private DBHelper mHelper;
    private String mSqlStatement;
    private Option mOption;
    private DataAccessCallbacks<T> mListener;
    
    public AsyncStatement(DBHelper helper, String sqlStatement, Option option, DataAccessCallbacks<T> listener) {
        mHelper = helper;
        mSqlStatement = sqlStatement;
        mListener = listener;
        mOption = option;
    }    
    
    @Override
    protected Object doInBackground(Void... params) {
        if (mListener != null) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            SQLiteStatement sqlStatement = db.compileStatement(mSqlStatement);   
            switch (mOption) {
            case STRING:
                return sqlStatement.simpleQueryForString();            
            case LONG:
                return sqlStatement.simpleQueryForLong();
            default:
                return sqlStatement.simpleQueryForBlobFileDescriptor();            
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (mListener != null) {
            mListener.onInfoReceived(result, mOption);
        }
    }


    
    

}
