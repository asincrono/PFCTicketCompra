package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Receipt;

public class UploadPendingReceiptsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean  DEBUG = true;
    private static final String   TAG   = "UploadPendingReceiptsCallbackHandler";

    private List<Receipt>         mReceipts;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;
    private DataSource            mDS;

    public UploadPendingReceiptsCallbackHandler(List<Receipt> receipts, Context context,
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

            mDS.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                    if (mChain) BackendDataAccess.uploadPendingDetails(mContext, mBackend);
                }

                @Override
                public void onDataReceived(List<Receipt> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            mDS.updateReceipts(mReceipts);
        } else {
            Log.wtf(TAG, "number of uploaded receipts don't match!!!");
        }
    }
}
