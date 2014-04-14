package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Receipt;

public class UploadReceiptsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean  DEBUG = true;
    private static final String   TAG   = "UploadReceiptsCallbackHandler";

    private List<Receipt>         mReceipts;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;
    private DataSource            mDS;

    public UploadReceiptsCallbackHandler(List<Receipt> receipts, Context context,
            CloudBackendMessaging backend, boolean chain) {
        mReceipts = receipts;
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mReceipts.size()) {
            Toast.makeText(mContext, "Uploaded " + mReceipts.size() + " receipts",
                    Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, mReceipts.size() + " receipts updated");
            for (Receipt receipt : mReceipts) {
                receipt.setUpdated(true);
            }

            mDS.setReceiptCallback(null);
            mDS.updateReceipts(mReceipts);
            if (mChain) {
                BackendDataAccess.uploadTotals(mContext, mBackend, mChain);
            }
        } else {
            Log.wtf(TAG, "number of uploaded receipts don't match!!!");
        }
    }
}
