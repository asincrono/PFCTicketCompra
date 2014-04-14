package es.dexusta.ticketcompra.backendataaccess;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Shop;

public class UploadShopsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String TAG = "UploadShopsCallbackHandler";
    
    private List<Shop> mShops;
    private Context mContext;
    private CloudBackendMessaging mBackend;
    private boolean mChain;
    private DataSource mDS;
    

    public UploadShopsCallbackHandler(List<Shop> shops, Context context, CloudBackendMessaging backend, boolean chain) {
        mShops = shops;        
        mContext = context;
        mBackend = backend;
        mChain = chain;
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mShops.size()) {
            Toast.makeText(mContext, "Uploaded " + results.size() + " shops", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, results.size() + " shops uploaded");
            for (Shop shop : mShops) {
                shop.setUpdated(true);
            }
            
            //TODO: CHECK IF THIS IS SAFE.
            mDS.setShopCallback(null);
            mDS.updateShops(mShops);  
            if (mChain) {
                BackendDataAccess.uploadPendingProducts(mContext, mBackend, mChain);
            }
        }
        else {
            Log.wtf(TAG, "Some shops weren't uploaded!");
        }
    }

}
