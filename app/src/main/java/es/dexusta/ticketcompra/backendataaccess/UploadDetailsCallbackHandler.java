package es.dexusta.ticketcompra.backendataaccess;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Detail;

public class UploadDetailsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final boolean DEBUG = true;
    private static final String   TAG = "UploadDetailCallbackHandler";

    private List<Detail>          mDetails;
    private Context               mContext;
    
    private DataSource            mDS;

    public UploadDetailsCallbackHandler(List<Detail> details, Context context) {
        mDetails = details;
        mContext = context;
    
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mDetails.size()) {
            Toast.makeText(mContext, "Uploaded " + mDetails.size() + " details", Toast.LENGTH_SHORT).show();
            if (DEBUG) Log.d(TAG, mDetails.size() + " details updated");
            
            // Marcar los detail como actualizados en la BD.
            for (Detail detail : mDetails) {
                detail.setUpdated(true);
            }            

            mDS.setDetailCallback(null);
            mDS.updateDetails(mDetails);
            // Nothing to chain to.
            
        } else {
            Log.wtf(TAG, "number of uploaded details don't match!!!");
        }

    }

}
