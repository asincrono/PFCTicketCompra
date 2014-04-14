package es.dexusta.ticketcompra;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendFragmentActivity;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.control.ShopSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.view.NoSwipeViewPager;

public class AddShopV2Activity extends CloudBackendFragmentActivity implements
        ChainSelectionCallback, AddShopCallbacks {
    private static final String        TAG                       = "AddShopV2Activity";
    private static final boolean       DEBUG                     = true;

    private static final String        KEY_CURRENT_PAGE          = "current_page";

    private static final int           CHAIN_SELECTION_FRAGMENT  = 0;
    private static final int           ADD_SHOP_FRAGMENT         = 1;

    private static final String        TAG_SELECT_CHAIN_FRAGMENT = "select_chain";
    private static final String        TAG_ADD_SHOP_FRAGMENT     = "add_shop";

    private NoSwipeViewPager           mNoSwipeViewPager;
    private AddShopPagerAdapter        mPagerAdapter;

    private DataSource                 mDS;
    private DataAccessCallbacks<Chain> mChainListener;
    private DataAccessCallbacks<Shop>  mShopListener;
    private boolean                    mPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentPage = CHAIN_SELECTION_FRAGMENT;

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        }

        setContentView(R.layout.add_shop_activity);

        mNoSwipeViewPager = (NoSwipeViewPager) findViewById(R.id.pager);
        mPagerAdapter = new AddShopPagerAdapter(getSupportFragmentManager());
        mNoSwipeViewPager.setAdapter(mPagerAdapter);
        mNoSwipeViewPager.setCurrentItem(currentPage);

        mDS = DataSource.getInstance(getApplicationContext());

        mChainListener = new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                ChainSelectionFragment fragment = (ChainSelectionFragment) mPagerAdapter
                        .instantiateItem(mNoSwipeViewPager, CHAIN_SELECTION_FRAGMENT);
                fragment.setList(results);
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
        outState.putInt(KEY_CURRENT_PAGE, mNoSwipeViewPager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        int currentPage = mNoSwipeViewPager.getCurrentItem();
        if (currentPage == 0) {
            super.onBackPressed();
        } else {
            mNoSwipeViewPager.setCurrentItem(currentPage - 1);
        }
    }

    private class AddShopPagerAdapter extends FragmentPagerAdapter {
        SparseArray<WeakReference<Fragment>> mRegisteredFragments = new SparseArray<WeakReference<Fragment>>();

        public AddShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mRegisteredFragments.put(position, new WeakReference<Fragment>(fragment));
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int nItem) {
            Log.d(TAG, "getItem: " + nItem);
            WeakReference<Fragment> fw = mRegisteredFragments.get(nItem);
            Fragment fragment = (fw != null) ? fw.get() : null;

            if (fragment == null) {
                switch (nItem) {
                case CHAIN_SELECTION_FRAGMENT:
                    // TODO: La referencia a "AddShopV2Activity.this" me tiene
                    // preocupado.
                    fragment = new ChainSelectionFragment();
                    break;
                case ADD_SHOP_FRAGMENT:
                    fragment = new AddShopFragment();
                    break;
                default:
                    fragment = new ChainSelectionFragment();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int index) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(index);
            return (fw != null) ? fw.get() : null;
        }
    }

    @Override
    public void onChainSelected(Chain chain) {
        // TODO: EXPERIMENTO, no tengo claro que funcione (debería).
        AddShopFragment fragment = (AddShopFragment) mNoSwipeViewPager.getAdapter()
                .instantiateItem(mNoSwipeViewPager, ADD_SHOP_FRAGMENT);
        fragment.setChain(chain);
        mNoSwipeViewPager.setCurrentItem(ADD_SHOP_FRAGMENT);
    }

    @Override
    public void onCancelChainSelection() {
        finish();
    }

//    @Override
//    public void onShopSelected(Shop shop) {
//        List<Shop> shops = new ArrayList<Shop>();
//        shops.add(shop);
//        mDS.insertShops(shops);
//
//        if (DEBUG) Log.d(TAG, "Shop selected.");
//        finish();
//    }
//
//    @Override
//    public void onCancelShopSelection() {
//
//        mNoSwipeViewPager.setCurrentItem(CHAIN_SELECTION_FRAGMENT);
//    }
//
//    @Override
//    public void onClickAddShop() {
//        // TODO Auto-generated method stub
//        
//    }

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
        mNoSwipeViewPager.setCurrentItem(CHAIN_SELECTION_FRAGMENT);        
    }

    // class InsertShopHandler extends CloudCallbackHandler<CloudEntity> {
    // private static final String SUBTAG = "InsertShopHandler";
    // private Shop mShop;
    // private Context mContext;
    //
    // public InsertShopHandler(Shop shop, Context context) {
    // mShop = shop;
    // mContext = context;
    // }
    //
    // @Override
    // public void onComplete(CloudEntity result) {
    // Shop shop = new Shop(result);
    // List<Shop> dataList = new ArrayList<Shop>();
    // dataList.add(shop);
    // mDS.insertShops(dataList);
    // if (DEBUG) Log.d(TAG + "." + SUBTAG, " onComplete.");
    // Toast.makeText(mContext, "Shop inserted in the cloud",
    // Toast.LENGTH_SHORT).show();
    // }
    //
    // @Override
    // public void onError(IOException exception) {
    // List<Shop> dataList = new ArrayList<Shop>();
    // dataList.add(mShop);
    // mDS.insertShops(dataList);
    // if (DEBUG) Log.d(TAG + "." + SUBTAG, " onError.");
    // Toast.makeText(mContext, "Shop NOT inserted in the cloud",
    // Toast.LENGTH_SHORT).show();
    // }
    // }
}
