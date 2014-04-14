package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Product;

public class UploadProductsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String   TAG = "UploadProductsCallbackHandler";

    private List<Product>         mProducts;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;
    private DataSource            mDS;

    public UploadProductsCallbackHandler(List<Product> products, Context context,
            CloudBackendMessaging backend, boolean chain) {
        mProducts = products;
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mProducts.size()) {
            Toast.makeText(mContext, "Uploaded " + mProducts.size()+ " products", Toast.LENGTH_SHORT).show();;
            if (DEBUG) Log.d(TAG, mProducts.size() + " products updated");
            for (Product product : mProducts) {
                product.setUpdated(true);
            }
            
            mDS.setProductCallback(null);
            mDS.updateProducts(mProducts);
            if (mChain) {
                BackendDataAccess.uploadPendingReceipts(mContext, mBackend, mChain);
            }
        } else {
            Log.wtf(TAG, "number of uploaded products don't mach!!!");
        }
    }
}
