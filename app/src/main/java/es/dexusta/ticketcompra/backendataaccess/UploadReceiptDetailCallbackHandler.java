package es.dexusta.ticketcompra.backendataaccess;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;

public class UploadReceiptDetailCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean  DEBUG = true;
    private static final String   TAG   = "UploadReceiptDetailsCallbackHandler";

    private Receipt               mReceipt;
    private List<Detail>          mDetails;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private DataSource            mDS;

    public UploadReceiptDetailCallbackHandler(Receipt receipt, List<Detail> details,
            Context context, CloudBackendMessaging backend) {
        mReceipt = receipt;
        mDetails = details;
        mContext = context;
        mBackend = backend;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        // 1.- Marcar los receipts como actualicados.
        if (DEBUG) Log.d(TAG, "Updating receipt and going to upload " + mDetails.size() + " details");
        mReceipt.setUpdated(true);
        List<Receipt> receipts = new ArrayList<Receipt>();
        receipts.add(mReceipt);
        
        mDS.setReceiptCallback(null);
        mDS.updateReceipts(receipts);

        // 2.- Insertar los Details en el backend.
        BackendDataAccess.uploadDetails(mDetails, mContext, mBackend);
    }
}
