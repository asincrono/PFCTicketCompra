package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Detail;

public class UploadPendingDetailsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
    private static final String   TAG = "UploadPendingDetailCallbackHandler";

    private List<Detail>          mDetails;
    private Context               mContext;
    
    private DataSource            mDS;

    public UploadPendingDetailsCallbackHandler(List<Detail> details, Context context) {
        mDetails = details;
        mContext = context;
    
        mDS = DataSource.getInstance(context);
    }

    @Override
    public void onComplete(List<CloudEntity> results) {
        if (results.size() == mDetails.size()) {
            Toast.makeText(mContext, "Uploaded " + mDetails.size() + " details", Toast.LENGTH_SHORT).show();
            if (BuildConfig.DEBUG)
                Log.d(TAG, mDetails.size() + " details uploaded");

            // Marcar los detail como actualizados en la BD.
            for (Detail detail : mDetails) {
                detail.setUpdated(true);
            }            

            mDS.setDetailCallback(new DataAccessCallbacks<Detail>() {
                @Override
                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
                    if (operation == Types.Operation.UPDATE) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "updated " + dataList.size() + " details.");
                    }
                }

                @Override
                public void onDataReceived(List<Detail> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });
            mDS.updateDetails(mDetails);
            // Nothing to chain to.
            
        } else {
            Log.wtf(TAG, "number of uploaded details don't match!!!");
        }

    }

}
