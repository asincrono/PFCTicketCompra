package es.dexusta.ticketcompra.dataaccess;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.ReplicatedDBObject;

public class SimpleAsyncInsert extends AsyncTask<Void, Void, ReplicatedDBObject> {
    private static final boolean                    DEBUG   = true;
    private static final String                     TAG     = "SimpleAsyncInsert";

    private ReplicatedDBObject                      mRDBO;
    private DBHelper                                mHelper;
    private DataAccessCallbacks<ReplicatedDBObject> mListener;
    private boolean                                 mResult = true;

    public SimpleAsyncInsert(DBHelper helper, ReplicatedDBObject rdbo,
            DataAccessCallbacks<ReplicatedDBObject> listener) {
        mHelper = helper;
        mRDBO = rdbo;
        mListener = listener;
    }

    @Override
    protected ReplicatedDBObject doInBackground(Void... params) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        mResult = db.insert(mRDBO.getTableName(), null, mRDBO.getValues()) > 0;
        return mRDBO;
    }

    @Override
    protected void onPostExecute(ReplicatedDBObject result) {
        if (mListener != null) {
            List<ReplicatedDBObject> dataList = new ArrayList<ReplicatedDBObject>();
            dataList.add(result);
            mListener.onDataProcessed(1, dataList, Operation.INSERT, mResult);
        }
    }

}
