package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendFragmentActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

public class SelectShopV2Activity extends CloudBackendFragmentActivity implements
        ChainSelectionCallback, ShopSelectionCallback, AddShopCallbacks {
    private static final String    TAG                   = "SelectShopV2Activity";
    private static final boolean   DEBUG                 = true;

    private static final int       SELECT_CHAIN_FRAGMENT = 0;
    private static final int       SELECT_SHOP_FRAGMENT  = 1;
    private static final int       ADD_SHOP_FRAGMENT     = 2;

    private DataSource             mDS;
    private ArrayList<Chain>       mChains;
    private ArrayList<Shop>        mShops;

    private ChainAdapter           mChainAdapter;
    private ShopAdapter            mShopAdapter;

    private Chain                  mSelectedChain;

    private ViewPager              mViewPager;

    private int                    mCurrentFragment;

    private Class                  mDestinationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG)
            Log.d(TAG, "onCreate");
        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt(Keys.KEY_CURRENT_FRAGMENT,
                    SELECT_CHAIN_FRAGMENT);
            mChains = savedInstanceState.getParcelableArrayList(Keys.KEY_CHAIN_LIST);
            mShops = savedInstanceState.getParcelableArrayList(Keys.KEY_SHOP_LIST);
        }

        mChainAdapter = new ChainAdapter(this, mChains);
        mShopAdapter  = new ShopAdapter(this, mShops);

        mDestinationActivity = (Class) getIntent().getSerializableExtra(
                Keys.KEY_DESTINATION_ACTIVITY);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.pager_activity);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mViewPager.setAdapter(new SelectShopPagerAdapter(getSupportFragmentManager()));

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setChainCallback(new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                setProgressBarIndeterminateVisibility(false);

                // As we call mPagerAdapter.instantiateItem(...), we will create
                // the fragment IF it doesn't already exists.
                mChains = results != null ? new ArrayList<Chain>(results) : null;

                mChainAdapter.swapList(mChains);

                onShowingFragment(SELECT_CHAIN_FRAGMENT);
                mViewPager.setCurrentItem(SELECT_CHAIN_FRAGMENT);
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

                onShowingFragment(SELECT_SHOP_FRAGMENT);
                mViewPager.setCurrentItem(SELECT_SHOP_FRAGMENT);
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                                        boolean result) {
                if (result) {
                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                        BackendDataAccess.uploadShop(dataList.get(0), getApplicationContext(),
                                getCloudBackend());

                        if (DEBUG) Log.d(TAG, "Shop inserted: " + dataList.get(0));
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
        } else {
            onShowingFragment(mCurrentFragment);
            mViewPager.setCurrentItem(mCurrentFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Keys.KEY_CURRENT_FRAGMENT, mViewPager.getCurrentItem());
        outState.putParcelableArrayList(Keys.KEY_CHAIN_LIST, mChains);
        outState.putParcelableArrayList(Keys.KEY_SHOP_LIST, mShops);
    }

    @Override
    public void onBackPressed() {
        int currentFragment = mViewPager.getCurrentItem();
        if (currentFragment > 0) {
            onShowingFragment(currentFragment - 1);
            mViewPager.setCurrentItem(currentFragment - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onChainSelected(Chain chain) {
        mSelectedChain = chain;
        setProgressBarIndeterminateVisibility(true);
        mDS.getShopsBy(chain);
        // Toast.makeText(this, " Chain id " + chain.getId() + " selected",
        // Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelChainSelection() {
        finish();
    }

    @Override
    public ChainAdapter getChainAdapter() {
        return mChainAdapter;
    }

    // @Override
    // public void onShopSelected(Shop shop) {
    // // Return the shop (on activity result).
    // Toast.makeText(this, " Shop id " + shop.getId() + " selected",
    // Toast.LENGTH_SHORT).show();
    // Intent intent = new Intent(this, mDestinationActivity);
    // intent.putExtra(Keys.KEY_SHOP, shop);
    // startActivity(intent);
    // }

    // @Override
    // public void onCancelShopSelection() {
    // mViewPager.setCurrentItem(SELECT_CHAIN_FRAGMENT);
    // }

    @Override
    public void onAcceptAddShop(Shop shop) {
        List<Shop> shops = new ArrayList<Shop>();
        shops.add(shop);
        mDS.insertShops(shops);
    }

    @Override
    public void onCancelAddShop() {
        onShowingFragment(SELECT_SHOP_FRAGMENT);
        mViewPager.setCurrentItem(SELECT_SHOP_FRAGMENT);
    }

    @Override
    public Chain getSelectedChain() {
        return mSelectedChain;
    }

    @Override
    public void onShopSelected(Shop shop) {
        // Return the shop (on activity result).
        // Toast.makeText(this, " Shop id " + shop.getId() + " selected",
        // Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, mDestinationActivity);
        intent.putExtra(Keys.KEY_SHOP, shop);
        startActivity(intent);

    }

    @Override
    public void onCancelShopSelection() {
        onShowingFragment(SELECT_CHAIN_FRAGMENT);
        mViewPager.setCurrentItem(SELECT_CHAIN_FRAGMENT);
    }

    @Override
    public void onClickAddShop() {
        onShowingFragment(ADD_SHOP_FRAGMENT);
        mViewPager.setCurrentItem(ADD_SHOP_FRAGMENT);
    }

    @Override
    public ShopAdapter getShopAdapter() {
        if (DEBUG)
            Log.d(TAG, "returning ShopAdapter: " + mShopAdapter.getCount());
        return mShopAdapter;
    }

    // @Override
    // public void onShopSelected(Shop shop) {
    // // Return the shop (on activity result).
    // Toast.makeText(this, " Shop id " + shop.getId() + " selected",
    // Toast.LENGTH_SHORT).show();
    // Intent intent = new Intent(this, mDestinationActivity);
    // intent.putExtra(Keys.KEY_SHOP, shop);
    // startActivity(intent);
    // }

    // @Override
    // public void onCancelShopSelection() {
    // mViewPager.setCurrentItem(SELECT_CHAIN_FRAGMENT);
    // }

    private void onShowingFragment(int fragment_code) {
        ActionBar ab = getActionBar();
        if (ab != null) {
            switch (fragment_code) {
                case SELECT_CHAIN_FRAGMENT:
                    ab.setTitle(R.string.select_chain_fragment_title);
                    showClassicAB(ab);
                    break;
                case SELECT_SHOP_FRAGMENT:
                    ab.setTitle(R.string.select_shop_fragment_title);
                    showClassicAB(ab);
                    break;
                case ADD_SHOP_FRAGMENT:
                    ab.setTitle(R.string.add_shop_fragment_title);
                    showCustomAB(ab);
                    break;
                default:
                    showClassicAB(ab);
                    break;
            }
        }
    }

    private void showClassicAB(ActionBar ab) {
        if (ab != null)
            ab.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE);
    }

    private void showCustomAB(ActionBar ab) {
        if (ab != null)
            ab.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE);
    }

    private class SelectShopPagerAdapter extends FragmentPagerAdapter {
        private static final int                     FRAGMENTS            = 3;


        private SparseArray<WeakReference<Fragment>> mRegisteredFragments = new SparseArray<WeakReference<Fragment>>();

        public SelectShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(position);

            Fragment fragment = (fw != null) ? fw.get() : null;

            if (fragment == null) {
                switch (position) {
                    case SELECT_CHAIN_FRAGMENT:
                        fragment = ChainSelectionFragment.newInstance();
                        break;
                    case SELECT_SHOP_FRAGMENT:
                        fragment = ShopSelectionFragment.newInstance();
                        break;

                    // TESTING
                    case ADD_SHOP_FRAGMENT:
                        fragment = AddShopFragment.newInstance(mSelectedChain);
                        break;
                }
            }

            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(position);
            Fragment fragment = (fw != null) ? fw.get() : null;

            if (fragment == null) {
                fragment = (Fragment) super.instantiateItem(container, position);
                mRegisteredFragments.put(position, new WeakReference<Fragment>(fragment));
            }

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return FRAGMENTS;
        }
    }
}
