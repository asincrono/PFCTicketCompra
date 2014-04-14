package es.dexusta.ticketcompra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.cloud.backend.android.CloudBackendActivity;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.control.RegionAdapter;
import es.dexusta.ticketcompra.control.SubregionAdapter;
import es.dexusta.ticketcompra.control.TownAdapter;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

public class AddShopActivity extends CloudBackendActivity {
    private static final String  TAG   = "AddShopActivity";
    private static final boolean DEBUG = true;

    private Spinner              mSpnRegion;
    private Spinner              mSpnSubregion;
    private Spinner              mSpnTown;

    private RegionAdapter        mRegionAdapter;
    private SubregionAdapter     mSubregionAdapter;
    private TownAdapter          mTownAdapter;

    private EditText             mEdtAddress;

    private DataSource           mDS;
    private Shop                 mShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shop_activity);

        mDS = DataSource.getInstance(getApplicationContext());        

        if (savedInstanceState != null) {
            // restore data if any.
        }

        showAcceptCancelActionBar(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveData();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSpnRegion = (Spinner) findViewById(R.id.spn_region);

        mSpnSubregion = (Spinner) findViewById(R.id.spn_subregion);
        // String[] regions =
        // getResources().getStringArray(R.array.regions_spain);
        // mSpnRegion.setAdapter(new ArrayAdapter<String>(this,
        // android.R.layout.simple_spinner_item,
        // regions));

        mSpnTown = (Spinner) findViewById(R.id.spn_town);
        mEdtAddress = (EditText) findViewById(R.id.edt_address);

        // As I have to read all the data the spinners will show from
        // disk (xml or Sqlite db) I should set both spinners disabled.
        
//        mSpnRegion.setEnabled(false);
//        mSpnSubregion.setEnabled(false);
//        mSpnTown.setEnabled(false);

        mSpnRegion.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Reach for the correspondent subregions.
                mDS.getSubregionBy(mRegionAdapter.getItem(position));
                if (DEBUG) Log.d(TAG, "Reaching for subregions of: " + mRegionAdapter.getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        mSpnSubregion.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDS.getTownsBy(mSubregionAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        // Now I reach for regions and
        mDS.setRegionCallback(new DataAccessCallbacks<Region>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Region> results) {
                mRegionAdapter = new RegionAdapter(AddShopActivity.this, results);
                mSpnRegion.setAdapter(mRegionAdapter);
                mSpnRegion.setEnabled(true);
                
                // mSpnRegion.setOnItemSelectedListener(new
                // OnItemSelectedListener() {
                //
                // @Override
                // public void onItemSelected(AdapterView<?> parent, View view,
                // int position,
                // long id) {
                //
                //
                // }
                //
                // @Override
                // public void onNothingSelected(AdapterView<?> parent) {
                // // TODO Auto-generated method stub
                //
                // }
                // });
            }

            @Override
            public void onDataProcessed(int processed, List<Region> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setSubregionCallback(new DataAccessCallbacks<Subregion>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Subregion> results) {
                if (DEBUG) Log.d(TAG, "Subregions returned: " + results.size());
                mSubregionAdapter = new SubregionAdapter(AddShopActivity.this, results);
                mSpnSubregion.setAdapter(mSubregionAdapter);
                mSpnSubregion.setEnabled(true);
            }

            @Override
            public void onDataProcessed(int processed, List<Subregion> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setTownCallback(new DataAccessCallbacks<Town>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Town> results) {
                mTownAdapter = new TownAdapter(AddShopActivity.this, results);
                mSpnTown.setAdapter(mTownAdapter);
                mSpnTown.setEnabled(true);
            }

            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.listRegions();
//        mSpnRegion.requestFocus();
    }

    private void showAcceptCancelActionBar(OnClickListener onClickAccept,
            OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();

        LayoutInflater inflater = LayoutInflater.from(getActionBar().getThemedContext());

        final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null);

        actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
        actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        // Previous line is equivalent to:
        // actionBar.setDisplayShowTitleEnabled(false);
        // actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setDisplayUseLogoEnabled(false);
        // actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void saveData() {
        // TODO Guardar los datos de la tienda online.
        // 1.- Comprobar si hay conexión a internet.
        if (isNetworkAvailable()) {
            if (DEBUG)
                Log.d(TAG, "Network available.");
            mShop = new Shop();

            mShop.setChainId(1);
             
            mShop.setTownId(mSpnTown.getSelectedItemId());
            mShop.setTownName(mTownAdapter.getItem(mSpnTown.getSelectedItemPosition()).getName());
            mShop.setAddress(mEdtAddress.getText().toString());

            // 2.- Enviar los datos.
            getCloudBackend().insert(mShop.getEntity(this), new CloudCallbackHandler<CloudEntity>() {

                @Override
                public void onComplete(CloudEntity result) {
                    // 2.1.- Si no hubo error. Guardar en local marcando que ya están
                    // actualizados (actualizados significa que tienen remoteId).
//                    mShop.setRemoteId(result.getId());
                    
                    List<Shop> shops = new ArrayList<Shop>();
                    shops.add(mShop);
                    mDS.insertShops(shops);
                }

                @Override
                public void onError(IOException exception) {
                    // 2.2.- Si hubo error. Cuardar en local sin marcar actualizados.
                    List<Shop> shops = new ArrayList<Shop>();                    
                    mDS.insertShops(shops);
                }

            });
        }
        else {
            List<Shop> shops = new ArrayList<Shop>();
            shops.add(mShop);
            mDS.insertShops(shops);
        }
    }

    /**
     * Informs if there is an available network connection. Not if there is
     * internet access.
     * 
     * @return true if there is an available network, false in the contrary.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ActiveNI = cm.getActiveNetworkInfo();

        return ActiveNI != null && ActiveNI.isConnected();

        // return ActiveNI != null? ActiveNI.isConnected() : false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}
