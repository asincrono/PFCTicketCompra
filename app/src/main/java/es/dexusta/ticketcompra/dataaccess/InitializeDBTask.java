package es.dexusta.ticketcompra.dataaccess;

import es.dexusta.ticketcompra.model.DBHelper;
import android.os.AsyncTask;

public class InitializeDBTask extends AsyncTask<Void, Void, Void> {
    private DBHelper mHelper;
    private InitializerCallback mCallback;
    
    public InitializeDBTask(DBHelper helper, InitializerCallback callback) {
        mHelper = helper;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mHelper.getReadableDatabase();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCallback.onInitialized();
    }
    
    public interface InitializerCallback {
        public void onInitialized();
    }

}
