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
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.util.Installation;

public class DownloadReceiptsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String TAG = "DownloadReceiptsCallbackHandler";
    
    private Context               mContext;
    private DateTime              mThisUpdate;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;

    private String                mInstallation;
    private SharedPreferences     mSP;
    private DataSource            mDS;

    public DownloadReceiptsCallbackHandler(Context context, DateTime thisUpdate,
            CloudBackendMessaging backend, boolean chain) {
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mThisUpdate = thisUpdate;

        mInstallation = Installation.id(context);
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
        mDS = DataSource.getInstance(context);

    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() > 0) {
            Toast.makeText(mContext, "Downloaded " + results.size() + " receipts", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, results.size() + " receipts downloaded");
            
            List<Receipt> receipts = new ArrayList<Receipt>();
            Receipt receipt;
            String receiptUnivId;
            
            long shopId;
            for (CloudEntity entity : results) {
                receipt = new Receipt(entity);
                receipt.setUpdated(true);
                receiptUnivId = receipt.getUniversalId();
                if (!receiptUnivId.startsWith(mInstallation)) {
                    String shopUnivId = receipt.getShopUnivId();                    
                    shopId = mDS.getShopLocIdFromUnivId(shopUnivId);
//                    shopId = mDS.getShopLocIdFromUnivId(receipt.getShopUnivId());
                    receipt.setShopId(shopId);
                    receipts.add(receipt);
                }
            }
            if (receipts.size() > 0) {
                mDS.setReceiptCallback(new ReceiptDACallbacks());
                mDS.insertReceipts(receipts);
            }
        }

        mSP.edit().putString(Consts.PREF_RECEIPTS_LAST_UPDATE, mThisUpdate.toStringRfc3339())
                .commit();

        if (mChain) {
            BackendDataAccess.downloadTotals(mContext, mBackend, mChain);
        }
    }

    class ReceiptDACallbacks implements DataAccessCallbacks<Receipt> {

        @Override
        public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Receipt receipt : dataList) {
                    mDS.addToUnivIdLocIdMap(receipt);
                }
            }
        }

        @Override
        public void onDataReceived(List<Receipt> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }
}
