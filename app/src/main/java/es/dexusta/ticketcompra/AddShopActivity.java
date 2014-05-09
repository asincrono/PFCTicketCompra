package es.dexusta.ticketcompra;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public class AddShopActivity extends CloudBackendActivity implements
        ChainSelectionCallback, AddShopCallbacks {
    private static final String  TAG   = "AddShopV2Activity";
    private static final boolean DEBUG = true;

    private static final String TAG_SELECT_CHAIN_FRAGMENT = "select_chain";
    private static final String TAG_ADD_SHOP_FRAGMENT     = "add_shop";
    private static final String TAG_STATE_FRAGMENT        = "state_fragment";

    private Chain         mSelectedChain;
    private ChainAdapter  mChainAdapter;
    private StateFragment mStateFragment;
    private List<Chain>   mChains;

    private DataSource                 mDS;
    private DataAccessCallbacks<Chain> mChainListener;
    private DataAccessCallbacks<Shop>  mShopListener;

    private boolean mShowMenu = true;

    private boolean mPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Default fragment initialization on first run.
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Add the state fragment for the first time.
            mStateFragment = new StateFragment();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT);

            // Add the initial fragment.
            //transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
            transaction.replace(android.R.id.content, ChainSelectionFragment.newInstance(), TAG_SELECT_CHAIN_FRAGMENT);
            transaction.commit();
        } else {
            mStateFragment = (StateFragment) getFragmentManager().findFragmentByTag(TAG_STATE_FRAGMENT);
            mChains = (List<Chain>) mStateFragment.get(Keys.KEY_CHAIN_LIST);
            mSelectedChain = (Chain) mStateFragment.get(Keys.KEY_SELECTED_CHAIN);
        }

        // New chain adapter cause it has a reference to the activity who was
        // destroyed.
        mChainAdapter = new ChainAdapter(this, mChains);

        mDS = DataSource.getInstance(getApplicationContext());

        mChainListener = new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                mChains = results;
                mStateFragment.put(Keys.KEY_CHAIN_LIST, mChains);
                mChainAdapter.swapList(results);
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
                if (DEBUG)
                    Log.d(TAG, "Shop processed, result = " + result);

                if (result) {
                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                        BackendDataAccess.uploadShop(dataList.get(0), getApplicationContext(),
                                getCloudBackend());

                        Toast.makeText(getApplicationContext(), "Shop inserted.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mDS.setChainCallback(mChainListener);
        mDS.setShopCallback(mShopListener);
        if (mChains == null)
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

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ActionBarController.showMenu(menu, mShowMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onChainSelected(Chain chain) {
        mSelectedChain = chain;
        mStateFragment.put(Keys.KEY_SELECTED_CHAIN, chain);
        showAddShopFragment();
    }

    @Override
    public void onCancelChainSelection() {
        //finish();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (DEBUG)
            Log.d(TAG, "onBackPressed, items on backstack: " + getFragmentManager().getBackStackEntryCount());
    }

    @Override
    public ChainAdapter getChainAdapter() {
        return mChainAdapter;
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

    @Override
    public Chain getChain() {
        return mSelectedChain;
    }

    private void showAddShopFragment() {

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right);

        Fragment newFragment = manager.findFragmentByTag(TAG_ADD_SHOP_FRAGMENT);
        if (newFragment == null) {
            // First transaction with this fragment.
            newFragment = AddShopFragment.newInstance();
            transaction.replace(android.R.id.content, newFragment, TAG_ADD_SHOP_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, newFragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showAcceptCancelActionBar(View.OnClickListener onClickAccept,
                                          View.OnClickListener onClickCancel) {
        mShowMenu = false;
        invalidateOptionsMenu();
        ActionBarController.setAcceptCancel(getActionBar(), onClickAccept, onClickCancel);
    }

    @Override
    public void hideAcceptCancelActionBar() {
        mShowMenu = true;
        invalidateOptionsMenu();
        ActionBarController.setDisplayDefault(getActionBar());
    }

    @Override
    public boolean isABAvaliable() {
        return getActionBar() != null;
    }

    @Override
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
    }
}
