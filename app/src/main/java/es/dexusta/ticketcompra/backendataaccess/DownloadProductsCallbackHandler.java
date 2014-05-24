package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.util.Installation;

public class DownloadProductsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final String   TAG    = "DownloadProductsCallbacksHandler";
    private static boolean        DEBUG  = true;
    private static String         mInstallation;
    private SharedPreferences     mSP;
    private DataSource            mDS;
    private DateTime              mThisUpdate;
    private Context               mContext;
    private boolean               mChain = false;
    private CloudBackendMessaging mBackend;

    public DownloadProductsCallbackHandler(Context context, DateTime thisUpdate,
            CloudBackendMessaging backend, boolean chain) {
        mContext = context;
        mBackend = backend;
        mChain = chain;

        if (mInstallation == null) mInstallation = Installation.id(context);
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
        mDS = DataSource.getInstance(context);
        mThisUpdate = thisUpdate;

    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() > 0) {
            if (DEBUG) Log.d(TAG, results.size() + " products downloaded.");

            List<Product> products = new ArrayList<Product>();
            Product product;
            String productUnivId;
            for (CloudEntity entity : results) {
                product = new Product(entity);
                product.setUpdated(true);
                productUnivId = product.getUniversalId();
                if (!productUnivId.startsWith(mInstallation)) {
                    products.add(product);
                }
            }
            if (products.size() > 0) {
                mDS.setProductCallback(new ProductDACallbacks());
                mDS.insertProducts(products);
            }
        } else {
            if (DEBUG) Log.d(TAG, "No new shops downloaded.");
        }

        mSP.edit().putString(Consts.PREF_PRODUCTS_LAST_UPDATE, mThisUpdate.toStringRfc3339()).commit();
    }

    class ProductDACallbacks implements DataAccessCallbacks<Product> {

        @Override
        public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Product product : dataList) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "product univId: " + product.getUniversalId() + ", locId: " + product.getId());
                    mDS.addToUnivIdLocIdMap(product);
                }
            }
            if (mChain) {
                //BackendDataAccess.downloadReceipts(mContext, mBackend, true);
            }
        }

        @Override
        public void onDataReceived(List<Product> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

}
