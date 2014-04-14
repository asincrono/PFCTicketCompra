package es.dexusta.ticketcompra.dataaccess;

import android.database.Cursor;
import android.os.AsyncTask;

import java.util.List;

import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.DBObject;

public abstract class AsyncQuery<T extends DBObject> extends AsyncTask<Void, Void, List<T>> {
    
        private DBHelper mHelper;
        private String mRawQuery;
        private String[] mArgs;        
        
        private DataAccessCallbacks<T> mListener;    
        
        public AsyncQuery(DBHelper helper, String rawQuery, String[] args, DataAccessCallbacks<T> listener) {
            mHelper = helper; 
            mRawQuery = rawQuery;
            mArgs = args;
            mListener = listener;
        }
        
        /*
         * Abstract methods.
         */
        
        public abstract T cursorToData(Cursor c);
        
        public abstract List<T> cursorToDataList(Cursor c);
        
        @Override
        protected List<T> doInBackground(Void... params) {
            List<T> list = null;
            
            if (mListener != null) {
                Cursor c = null;

                try {
                    c = mHelper.getReadableDatabase().rawQuery(mRawQuery, mArgs);
                    if (c.moveToFirst()) {
                        list = cursorToDataList(c);
                    }
                }
                finally {
                    if (c != null) c.close();
                }
            }
            
            return list;
        }

        @Override
        protected void onPostExecute(List<T> results) {
            if (mListener != null) {                
                mListener.onDataReceived(results);
            }
        }
}