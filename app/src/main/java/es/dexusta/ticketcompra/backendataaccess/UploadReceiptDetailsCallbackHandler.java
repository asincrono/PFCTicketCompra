package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;

public class UploadReceiptDetailsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean  DEBUG = true;
    private static final String   TAG   = "UploadReceiptDetailsCallbackHandler";

    private Receipt               mReceipt;
    private List<Detail>          mDetails;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private DataSource            mDS;

    public UploadReceiptDetailsCallbackHandler(Receipt receipt, List<Detail> details,
                                               Context context, CloudBackendMessaging backend) {
        mReceipt = receipt;
        mDetails = details;
        mContext = context;
        mBackend = backend;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        // 1.- Marcar el receipt como actualicado.
        if (DEBUG) Log.d(TAG, "Updating receipt and going to upload " + mDetails.size() + " details");
        mReceipt.setUpdated(true);
        final List<Receipt> receipts = new ArrayList<Receipt>();
        receipts.add(mReceipt);
        
        mDS.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                if (operation == Types.Operation.UPDATE) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, dataList.size() + " receipt(s) marked as \"updated.\".");
                }



                // 2.- Insertar los Details en el backend.
                //BackendDataAccess.uploadDetails(mDetails, mContext, mBackend);
                mDS.listPendingReceipts();
            }

            @Override
            public void onDataReceived(List<Receipt> results) {

                if (results != null) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "detected " + results.size() + " pending receipts.");
                } else {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "No pending receipts.");
                }
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });
        mDS.updateReceipts(receipts);
    }
}
