package es.dexusta.ticketcompra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public class AddShopV2Activity extends CloudBackendFragmentActivity implements
        ChainSelectionCallback, AddShopCallbacks {
    private static final String        TAG                       = "AddShopV2Activity";
    private static final boolean       DEBUG                     = true;

    private static final String KEY_CURRENT_FRAGMENT = "current_page";

    private static final int           CHAIN_SELECTION_FRAGMENT  = 0;
    private static final int           ADD_SHOP_FRAGMENT         = 1;

    private static final String        TAG_SELECT_CHAIN_FRAGMENT = "select_chain";
    private static final String        TAG_ADD_SHOP_FRAGMENT     = "add_shop";

    private String mActiveFragment;

    private ChainAdapter mChainAdapter;

    private DataSource                 mDS;
    private DataAccessCallbacks<Chain> mChainListener;
    private DataAccessCallbacks<Shop>  mShopListener;
    private boolean                    mPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mActiveFragment = savedInstanceState.getString(KEY_CURRENT_FRAGMENT, TAG_SELECT_CHAIN_FRAGMENT);
        } else {
            mActiveFragment = TAG_SELECT_CHAIN_FRAGMENT;
        }

        mChainAdapter = new ChainAdapter(this);

        mDS = DataSource.getInstance(getApplicationContext());

        mChainListener = new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {

            }

            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mShopListener = new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                if (result) {
                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                        BackendDataAccess.uploadShop(dataList.get(0), getApplicationContext(),
                                getCloudBackend());
                        if (DEBUG) Log.d(TAG, "Shop processed, result = " + result);
                        Toast.makeText(getApplicationContext(), "Shop inserted.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                // if (result) finish();

            }
        };

        mDS.setChainCallback(mChainListener);
        mDS.setShopCallback(mShopListener);
        mDS.listChains();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            mDS.setChainCallback(mChainListener);
            mDS.setShopCallback(mShopListener);
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        mDS.setChainCallback(null);
        mDS.setShopCallback(null);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_FRAGMENT, mActiveFragment);
    }

    @Override
    public void onChainSelected(Chain chain) {
       showAddShopFragment();
    }

    @Override
    public void onCancelChainSelection() {
        finish();
    }

    @Override
    public void onAcceptAddShop(Shop shop) {
        List<Shop> shops = new ArrayList<Shop>();
        shops.add(shop);
        mDS.insertShops(shops);

        if (DEBUG) Log.d(TAG, "Shop selected.");
        finish();
    }

    @Override
    public void onCancelAddShop() {
        onBackPressed();
    }

    private void showAddShopFragment() {
        if (!mActiveFragment.equals(TAG_ADD_SHOP_FRAGMENT)) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);

            Fragment newFragment = manager.findFragmentByTag(TAG_ADD_SHOP_FRAGMENT);
            if (newFragment == null) {
                transaction.replace(android.R.id.content, newFragment, TAG_ADD_SHOP_FRAGMENT);
            } else {
                transaction.replace(android.R.id.content, newFragment);
            }
            mActiveFragment = TAG_ADD_SHOP_FRAGMENT;
        }
    }

    private void showSelectChainFragment(ChainAdapter adapter) {
        if (!mActiveFragment.equals(TAG_SELECT_CHAIN_FRAGMENT)) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
            Fragment newFragment = manager.findFragmentByTag(TAG_SELECT_CHAIN_FRAGMENT);
            if (newFragment == null) {
                newFragment = ChainSelectionFragment.newInstance(adapter);
                transaction.add(android.R.id.content, newFragment, TAG_SELECT_CHAIN_FRAGMENT);
            } else {
                transaction.replace(android.R.id.content, newFragment);
            }
            mActiveFragment = TAG_SELECT_CHAIN_FRAGMENT;
        }
    }
}
