package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.util.Installation;

public class DownloadShopsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final String   TAG    = "DownloadShopsCallbacksHandler";
    private static boolean        DEBUG  = true;
    private static String         mInstallation;
    private SharedPreferences     mSP;
    private DataSource            mDS;
    private DateTime              mThisUpdate;
    private Context               mContext;
    private boolean               mChain = false;
    private CloudBackendMessaging mBackend;

    public DownloadShopsCallbackHandler(Context context, DateTime thisUpdate,
            CloudBackendMessaging backend, boolean chain) {
        mContext = context;
        mThisUpdate = thisUpdate;
        mBackend = backend;
        mChain = chain;

        if (mInstallation == null) mInstallation = Installation.id(context);
        
        mDS = DataSource.getInstance(context);
        
        mSP = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public void onComplete(List<CloudEntity> results) {

        if (results.size() > 0) {
            Toast.makeText(mContext, "Uploaded " + results.size() + " shops", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, results.size() + " shops downloaded.");

            List<Shop> shops = new ArrayList<Shop>();
            Shop shop;
            String shopUnivId;
            for (CloudEntity entity : results) {
                shop = new Shop(entity);
                shop.setUpdated(true);
                shopUnivId = shop.getUniversalId();
                if (!shopUnivId.startsWith(mInstallation)) {
                    shops.add(shop);
                }
            }

            if (shops.size() > 0) {
                mDS.setShopCallback(new ShopDACallbacks());
                mDS.insertShops(shops);
            }
            
        } else {
            if (DEBUG) Log.d(TAG, "No new shops downloaded.");
        }
        
        mSP.edit().putString(Consts.PREF_SHOPS_LAST_UPDATE, mThisUpdate.toStringRfc3339()).commit();
        
        if (mChain) {
            BackendDataAccess.downloadNewProducts(mContext, mBackend, mChain);
        }
    }

    class ShopDACallbacks implements DataAccessCallbacks<Shop> {

        @Override
        public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Shop shop : dataList) {
                    mDS.addToUnivIdLocIdMap(shop);
                }
            }
        }

        @Override
        public void onDataReceived(List<Shop> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }
    }

}
