package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.control.ShopAdapter;
import es.dexusta.ticketcompra.control.ShopSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.localdataaccess.DataAccessCallback;
import es.dexusta.ticketcompra.localdataaccess.LocalDataSource;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public class SelectShopV2Activity extends CloudBackendActivity implements
        ChainSelectionCallback, ShopSelectionCallback, AddShopCallbacks {
    private static final String  TAG   = "SelectShopV2Activity";
    private static final boolean DEBUG = true;

    private static final String TAG_STATE_FRAGMENT        = "state_fragment";
    private static final String TAG_SELECT_CHAIN_FRAGMENT = "select_chain_fragment";
    private static final String TAG_SELECT_SHOP_FRAGMENT  = "select_shop_fragment";
    private static final String TAG_ADD_SHOP_FRAGMENT     = "add_shop_fragment";

    private static final String[] ALLOWED_TAGS = {TAG_SELECT_CHAIN_FRAGMENT,
            TAG_SELECT_SHOP_FRAGMENT,
            TAG_ADD_SHOP_FRAGMENT};

    private static final String KEY_CHAINS = "chains";
    private static final String KEY_SHOPS  = "shops";

    private DataAccessCallback<Chain> mReadChainsCallback;
    private DataAccessCallback<Shop>  mReadShopsCallback;
    private DataAccessCallback<Shop>  mInsertShopsCallback;

    private LocalDataSource mLDS;
//    private DataSource      mDS;
    private List<Chain>     mChains;
    private List<Shop>      mShops;

    private ChainAdapter mChainAdapter;
    private ShopAdapter  mShopAdapter;

    private Chain mSelectedChain;

    private Class mDestinationActivity;

    private StateFragment mStateFragment;

    private boolean mShowingClassicAB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "savedInstanceState was null.");
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            // Add state fragment.
            mStateFragment = new StateFragment();

            transaction.add(mStateFragment, TAG_STATE_FRAGMENT);
            transaction.add(android.R.id.content,
                    ChainSelectionFragment.newInstance(), TAG_SELECT_CHAIN_FRAGMENT);

            transaction.commit();

            mReadChainsCallback = new DataAccessCallback<Chain>() {
                @Override
                public void onComplete(List<Chain> results, boolean result) {
                    setProgressBarIndeterminateVisibility(false);

                    mChains = results != null ? new ArrayList<Chain>(results) : null;
                    mChainAdapter.swapList(mChains);

                    mStateFragment.put(KEY_CHAINS, mChains);
                }
            };

            mReadShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    setProgressBarIndeterminateVisibility(false);

                    mShops = results != null ? new ArrayList<Shop>(results) : null;
                    mShopAdapter.swapList(mShops);

                    mStateFragment.put(KEY_SHOPS, mShops);
                }
            };

            mInsertShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    if (results != null) {
                        BackendDataAccessV2.uploadShops(results, getApplicationContext(), getCloudBackend());
                    }
                }
            };

            mStateFragment.put(Keys.KEY_READ_CHAINS_CALLBACK, mReadChainsCallback);
            mStateFragment.put(Keys.KEY_READ_SHOPS_CALLBACK, mReadShopsCallback);
            mStateFragment.put(Keys.KEY_INSERT_SHOPS_CALLBACK, mInsertShopsCallback);

        } else {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "savedInstanceState wasn't null.");
            mStateFragment = (StateFragment) getFragmentManager()
                    .findFragmentByTag(TAG_STATE_FRAGMENT);
            mChains = (List<Chain>) mStateFragment.get(KEY_CHAINS);
            mShops = (List<Shop>) mStateFragment.get(KEY_SHOPS);

            mReadChainsCallback = (DataAccessCallback<Chain>) mStateFragment.get(Keys.KEY_READ_CHAINS_CALLBACK);
            if (mReadChainsCallback == null) if (BuildConfig.DEBUG)
                Log.d(TAG, "WTF, we just saved it.");
            mReadShopsCallback = (DataAccessCallback<Shop>) mStateFragment.get(Keys.KEY_READ_SHOPS_CALLBACK);
            mInsertShopsCallback = (DataAccessCallback<Shop>) mStateFragment.get(Keys.KEY_INSERT_SHOPS_CALLBACK);
        }

        // We don't really care if mChains and mShops are null,
        // DBObjectAdapter can handle that.
        mChainAdapter = new ChainAdapter(this, mChains);
        mShopAdapter = new ShopAdapter(this, mShops);

        mDestinationActivity = (Class) getIntent().getSerializableExtra(
                Keys.KEY_DESTINATION_ACTIVITY);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.pager_activity);

        mLDS = LocalDataSource.getInstance(getApplicationContext());

        if (mReadChainsCallback == null || mReadShopsCallback == null || mInsertShopsCallback == null) {
            throw new AssertionError("DataAccessCallback for chain and shop shouldn't be null!");
        }

        if (mChains == null) {
            mLDS.listChains(mReadChainsCallback);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (DEBUG)
            Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onChainSelected(Chain chain) {
        mSelectedChain = chain;
        setProgressBarIndeterminateVisibility(true);
        mLDS.getShopsBy(chain, mReadShopsCallback);
        showFragment(TAG_SELECT_SHOP_FRAGMENT);
    }


    @Override
    public void onCancelChainSelection() {
        finish();
    }

    @Override
    public void onShopSelection(Shop shop) {
        // Return the shop (on activity result).
        // Toast.makeText(this, " Shop id " + shop.getId() + " selected",
        // Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, mDestinationActivity);
        intent.putExtra(Keys.KEY_SELECTED_SHOP, shop);
        startActivity(intent);
    }

    @Override
    public void onCancelShopSelection() {
        onBackPressed();
    }

    @Override
    public void onClickAddShop() {
        showFragment(TAG_ADD_SHOP_FRAGMENT);
    }

    @Override
    public void onAcceptAddShop(Shop shop) {
        List<Shop> shops = new ArrayList<Shop>();
        shops.add(shop);
        mLDS.insertShops(shops, mInsertShopsCallback);
    }

    @Override
    public void onCancelAddShop() {
        // We assume that if the user cancel add shop she wants to abandon this task. If not
        // she would just press back to select other chain.
        finish();
    }

    private void showFragment(String tag) {
        if (Arrays.asList(ALLOWED_TAGS).contains(tag)) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            Fragment fragment = manager.findFragmentByTag(tag);

            if (fragment == null) {
                if (tag.equals(TAG_SELECT_CHAIN_FRAGMENT)) {
                    fragment = ChainSelectionFragment.newInstance();
                } else if (tag.equals(TAG_SELECT_SHOP_FRAGMENT)) {

                    fragment = ShopSelectionFragment.newInstance();
                } else if (tag.equals(TAG_ADD_SHOP_FRAGMENT)) {
                    fragment = AddShopFragment.newInstance();
                }
                transaction.replace(android.R.id.content, fragment, tag);
            } else {
                transaction.replace(android.R.id.content, fragment);
            }

            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            throw new IllegalArgumentException("Fragment tag allowed is one in: " + ALLOWED_TAGS);
        }
    }

    @Override
    public ChainAdapter getChainAdapter() {
        return mChainAdapter;
    }

    @Override
    public ShopAdapter getShopAdapter() {
        return mShopAdapter;
    }

    @Override
    public Chain getChain() {
        return mSelectedChain;
    }

    @Override
    public void showAcceptCancelActionBar(View.OnClickListener onClickAccept,
                                          View.OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            mShowingClassicAB = false;
            ActionBarController.setAcceptCancel(actionBar, onClickAccept, onClickCancel);
        }
    }

    @Override
    public void hideAcceptCancelActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null && !mShowingClassicAB) {
            mShowingClassicAB = true;
            ActionBarController.setDisplayDefault(actionBar);
        }
    }

    @Override
    public boolean isABAvaliable() {
        return (getActionBar() != null);
    }

    @Override
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
}
