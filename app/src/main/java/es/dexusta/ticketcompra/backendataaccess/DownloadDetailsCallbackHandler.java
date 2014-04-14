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
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.util.Installation;

public class DownloadDetailsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String TAG = "DownloadDetailsCallbackHandler";
    
    private Context               mContext;
    private DateTime              mThisUpdate;
    private CloudBackendMessaging mBackend;

    private String                mInstallation;
    private DataSource            mDS;
    private SharedPreferences     mSP;

    public DownloadDetailsCallbackHandler(Context context, DateTime thisUpdate,
            CloudBackendMessaging backend) {
        mContext = context;
        mThisUpdate = thisUpdate;
        mBackend = backend;

        mInstallation = Installation.id(context);
        mDS = DataSource.getInstance(context);
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() > 0) {
            Toast.makeText(mContext, "Downloaded " + results.size() + " details", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, results.size() + " details downloaded");
            
            Detail detail;
            List<Detail> details = new ArrayList<Detail>();
            String detailUnivId;
            long receiptId;
            long productId;

            for (CloudEntity entity : results) {
                detail = new Detail(entity);
                detail.setUpdated(true);
                detailUnivId = detail.getUniversalId();
                if (!detailUnivId.startsWith(mInstallation)) {
                    receiptId = mDS.getReceiptLocIdFromUnivId(detail.getReceiptUnivId());
                    productId = mDS.getProductLocIdFromUnivId(detail.getProductUnivId());
                    detail.setReceiptId(receiptId);
                    detail.setProductId(productId);
                    details.add(detail);
                }
            }
            if (details.size() > 0) mDS.insertDetails(details);

        }

        mSP.edit().putString(Consts.PREF_DETAILS_LAST_UPDATE, mThisUpdate.toStringRfc3339())
                .commit();
    }

}
