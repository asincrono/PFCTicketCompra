package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Product;

public class UploadPendingProductsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String   TAG = "UploadPendingProductsCallbackHandler";

    private List<Product>         mProducts;
    private Context               mContext;
    private CloudBackendMessaging mBackend;
    private boolean               mChain;
    private DataSource            mDS;

    public UploadPendingProductsCallbackHandler(List<Product> products, Context context,
                                                CloudBackendMessaging backend, boolean chain) {
        mProducts = products;
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "mProducts.size = " + mProducts.size() + ", results.size = " + results.size());
        if (results.size() == mProducts.size()) {
            Toast.makeText(mContext, "Uploaded " + mProducts.size()+ " products", Toast.LENGTH_SHORT).show();;
            if (DEBUG) Log.d(TAG, mProducts.size() + " products updated");
            for (Product product : mProducts) {
                product.setUpdated(true);
            }
            
            mDS.setProductCallback(new DataAccessCallbacks<Product>() {
                @Override
                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                    //if (mChain) BackendDataAccess.uploadPendingReceipts(mContext, mBackend, true);

                }

                @Override
                public void onDataReceived(List<Product> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            mDS.updateProducts(mProducts);
        } else {
            Log.wtf(TAG, "number of uploaded products don't mach!!!");
        }
    }
}
