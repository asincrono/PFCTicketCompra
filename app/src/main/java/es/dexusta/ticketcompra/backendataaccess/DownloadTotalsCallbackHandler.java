package es.dexusta.ticketcompra.backendataaccess;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Total;
import es.dexusta.ticketcompra.util.Installation;

public class DownloadTotalsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String TAG = "DownloadTotalsCallbackHandler";
    
    private Context               mContext;
    private DateTime              mThisUpdate;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;

    private String                mInstallation;
    private SharedPreferences     mSP;
    private DataSource            mDS;

    public DownloadTotalsCallbackHandler(Context context, DateTime thisUpdate,
            CloudBackendMessaging backend, boolean chain) {
        mContext = context;
        mThisUpdate = thisUpdate;
        mBackend = backend;
        mChain = chain;

        mInstallation = Installation.id(context);
        mDS = DataSource.getInstance(context);
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() > 0) {
            Toast.makeText(mContext, "Downloaded " + results.size() + " totlas", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, results.size() + " totals downloaded");
            
            Total total;
            List<Total> totals = new ArrayList<Total>();
            String totalUnivId;
            long receiptId;
            for (CloudEntity entity : results) {
                total = new Total(entity);
                total.setUpdated(true);                
                totalUnivId = total.getUniversalId();
                if ((totalUnivId != null) && !totalUnivId.startsWith(mInstallation)) {
                    receiptId = mDS.getReceiptLocIdFromUnivId(total.getReceiptUnivId());
                    total.setReceiptId(receiptId);
                    totals.add(total);                    
                }
                if (totals.size() > 0) mDS.insertTotals(totals);
            }
        }

        mSP.edit().putString(Consts.PREF_TOTALS_LAST_UPDATE, mThisUpdate.toStringRfc3339())
                .commit();

        if (mChain) {
            BackendDataAccess.downloadDetails(mContext, mBackend, mChain);
        }
    }

}
