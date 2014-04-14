package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Total;

public class UploadTotalsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean  DEBUG = true;
    private static final String   TAG   = "UploadTotalsCallbackHandler";

    private List<Total>           mTotals;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;
    private DataSource            mDS;

    public UploadTotalsCallbackHandler(List<Total> totals, Context context,
            CloudBackendMessaging backend, boolean chain) {
        mTotals = totals;
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mTotals.size()) {
            Toast.makeText(mContext, "Uploaded " + mTotals.size() + " totals", Toast.LENGTH_SHORT)
                    .show();
            if (DEBUG) Log.d(TAG, mTotals.size() + " totals updated");
            for (Total total : mTotals) {
                total.setUpdated(true);
            }

            mDS.setTotalCallback(null);
            mDS.updateTotals(mTotals);
            if (mChain) {
                BackendDataAccess.uploadPendingDetails(mContext, mBackend);
            }
        } else {
            Log.wtf(TAG, "number of uploaded receipts don't match!!!");
        }
    }
}
