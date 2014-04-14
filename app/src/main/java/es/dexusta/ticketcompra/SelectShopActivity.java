package es.dexusta.ticketcompra;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import java.util.List;

import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.control.ChainShopPagerAdapter;
import es.dexusta.ticketcompra.control.ShopAdapter;
import es.dexusta.ticketcompra.control.ShopSelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.view.NoSwipeViewPager;

public class SelectShopActivity extends FragmentActivity implements ChainSelectionCallback, ShopSelectionCallback{
    private static final String TAG = "SelectShopAcitivty";
    private static final boolean DEBUG = true;

    private ChainAdapter mChainAdapter;
    private ShopAdapter mShopAdapter;

    private DataAccessCallbacks<Chain> mChainListener;
    private DataAccessCallbacks<Shop> mShopListener;
    
    private DataSource mDS;
    
    private boolean mListening;
    
    //private NoSwipeViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);        
        setContentView(R.layout.shop_selection_activity);

        // Create the adapters for the two ListFragments
        mChainAdapter = new ChainAdapter(this);
        mShopAdapter = new ShopAdapter(this);
        
        mDS = DataSource.getInstance(getApplicationContext());
        
        // Create the listener for Chain and Shop.
        mChainListener = new DataAccessCallbacks<Chain>() {

            @Override
            public void onDataReceived(List<Chain> list) {
                mChainAdapter.updateList(list);
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub
                
            }

            
        };
        
        mShopListener = new DataAccessCallbacks<Shop>() {
            
            @Override
            public void onDataReceived(List<Shop> list) {
                mShopAdapter.updateList(list);
                setProgressBarIndeterminateVisibility(false);                
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub
                
            }
            
            
        };
        mDS.setChainCallback(mChainListener);
        mDS.setShopCallback(mShopListener);
        mListening = true;
        
        // Get and set pager and pagerAdapter.
        NoSwipeViewPager vp = (NoSwipeViewPager) findViewById(R.id.pager);
        vp.setAdapter(new ChainShopPagerAdapter(getSupportFragmentManager()));

        setProgressBarIndeterminateVisibility(true);
        mDS.listChains();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (!mListening) {
            mDS.setChainCallback(mChainListener);
            mDS.setShopCallback(mShopListener);
            mListening = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListening) {
            mDS.setChainCallback(null);
            mDS.setShopCallback(null);
            mListening = false;
        }
    }

    
    public void queryShops(Chain chain) {
        setProgressBarIndeterminateVisibility(true);
        mDS.getShopsBy(chain);
    }

    @Override
    public void onShopSelected(Shop shop) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onChainSelected(Chain chain) {
        queryShops(chain);
    }

    @Override
    public void onCancelShopSelection() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCancelChainSelection() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ChainAdapter getChainAdapter() {
        return mChainAdapter;
    }

    @Override
    public void onClickAddShop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ShopAdapter getShopAdapter() {
        return mShopAdapter;
    }

}
