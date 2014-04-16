package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.control.ShopAdapter;
import es.dexusta.ticketcompra.control.ShopSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public class SelectShopV2Activity extends CloudBackendActivity implements
        ChainSelectionCallback, ShopSelectionCallback, AddShopCallbacks {
    private static final String    TAG                   = "SelectShopV2Activity";
    private static final boolean   DEBUG                 = true;

    private static final String TAG_STATE_FRAGMENT = "state_fragment";
    private static final String TAG_SELECT_CHAIN_FRAGMENT = "select_chain_fragment";
    private static final String TAG_SELECT_SHOP_FRAGMENT = "select_shop_fragment";
    private static final String TAG_ADD_SHOP_FRAGMENT = "add_shop_fragment";

    private static final String[] ALLOWED_TAGS = {TAG_SELECT_CHAIN_FRAGMENT, TAG_SELECT_SHOP_FRAGMENT, TAG_ADD_SHOP_FRAGMENT};

    private static final String KEY_CHAINS = "chains";
    private static final String KEY_SHOPS = "shops";


    private static final int       SELECT_CHAIN_FRAGMENT = 0;
    private static final int       SELECT_SHOP_FRAGMENT  = 1;
    private static final int       ADD_SHOP_FRAGMENT     = 2;

    private DataSource             mDS;
    private List<Chain>       mChains;
    private List<Shop>        mShops;

    private ChainAdapter mChainAdapter;
    private ShopAdapter mShopAdapter;

    private Chain                  mSelectedChain;

    private Class                  mDestinationActivity;

    private StateFragment mStateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT);
            transaction.add(android.R.id.content, ChainSelectionFragment.newInstance(), TAG_SELECT_CHAIN_FRAGMENT);
            // Add state fragment.
            mStateFragment = new StateFragment();

            //TODO add initial fragment.

            transaction.commit();
        } else {
            mStateFragment = (StateFragment) getFragmentManager().findFragmentByTag(TAG_STATE_FRAGMENT);
            mChains = (List<Chain>) mStateFragment.get(KEY_CHAINS);
            mShops = (List<Shop>) mStateFragment.get(KEY_SHOPS);
        }

        // We don't really care if mChains and mShops are null,
        // DBObjectAdapter can handle that.
        mChainAdapter = new ChainAdapter(this, mChains);
        mShopAdapter = new ShopAdapter(this, mShops);

        mDestinationActivity = (Class) getIntent().getSerializableExtra(
                Keys.KEY_DESTINATION_ACTIVITY);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.pager_activity);

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setChainCallback(new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                setProgressBarIndeterminateVisibility(false);

                mChains = results != null ? new ArrayList<Chain>(results) : null;
                mChainAdapter.swapList(mChains);

                mStateFragment.put(KEY_CHAINS, mChains);
            }

            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setShopCallback(new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                setProgressBarIndeterminateVisibility(false);
                mShops = results != null ? new ArrayList<Shop>(results) : null;
                mShopAdapter.swapList(mShops);

                mStateFragment.put(KEY_SHOPS, mShops);
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                if (result) {
                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                        BackendDataAccess.uploadShop(dataList.get(0), getApplicationContext(),
                                getCloudBackend());
                        
                        if (DEBUG)
                            Log.d(TAG, "Shop inserted: " + dataList.get(0));
                        Toast.makeText(getApplicationContext(), "Shop inserted.",
                                Toast.LENGTH_SHORT).show();
                        // TESTING
                        mDS.getShopsBy(mSelectedChain);
                    }
                }

            }
        });

        // setProgressBarIndeterminateVisibility(true);

        if (mChains == null) {
            mDS.listChains();
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
        mDS.getShopsBy(chain);
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
        intent.putExtra(Keys.KEY_SHOP, shop);
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
        mDS.insertShops(shops);
    }

    @Override
    public void onCancelAddShop() {
        onBackPressed();
    }

    private void showShopSelectionFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = manager.findFragmentByTag(TAG_SELECT_SHOP_FRAGMENT);
        if (fragment == null) {
            fragment = ShopSelectionFragment.newInstance();
            transaction.replace(android.R.id.content, fragment, TAG_SELECT_SHOP_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
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
    public void showAcceptCancelActionBar(View.OnClickListener onClickAccept, View.OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {

            LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());

            final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null, false);

            actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
            actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);

            showCustomAB(actionBar);
        }
    }

    @Override
    public void hideAcceptCancelActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            showClassicAB(actionBar);
        }
    }

    private void showClassicAB(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE
            );
        }
    }

    private void showCustomAB(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    private void onShowingFragment(int fragment_code) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            switch (fragment_code) {
                case SELECT_CHAIN_FRAGMENT:
                    actionBar.setTitle(R.string.select_chain_fragment_title);
                    showClassicAB(actionBar);
                    break;
                case SELECT_SHOP_FRAGMENT:
                    actionBar.setTitle(R.string.select_shop_fragment_title);
                    showClassicAB(actionBar);
                    break;
                case ADD_SHOP_FRAGMENT:
                    actionBar.setTitle(R.string.add_shop_fragment_title);
                    showCustomAB(actionBar);
                    break;
                default:
                    showClassicAB(actionBar);
                    break;
            }
        }
    }
}
