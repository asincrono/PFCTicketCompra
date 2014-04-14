package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import es.dexusta.ticketcompra.ChainSelectionFragment;
import es.dexusta.ticketcompra.ShopSelectionFragment;

public class ChainShopPagerAdapter extends FragmentPagerAdapter {
    public static final int CHAIN_FRAGMENT = 0;
    public static final int SHOP_FRAGMENT = 1;
    
    private ChainSelectionCallback mChainSelectionCallback;
    private ShopSelectionCallback mShopSelectionCallback;
    
    public ChainShopPagerAdapter(FragmentManager fm,
            ChainSelectionCallback chainSelectionCallback,
            ShopSelectionCallback shopSelectionCallback) {        
        super(fm);
        mChainSelectionCallback = chainSelectionCallback;
        mShopSelectionCallback = shopSelectionCallback;
    }

    private Context mContext;

    @Override
    public Fragment getItem(int position) {
        
        switch (position) {
        case CHAIN_FRAGMENT:           
            return new ChainSelectionFragment();
            
        case SHOP_FRAGMENT:
            return new ShopSelectionFragment();            
        }
        return null;        
    }

    @Override
    public int getCount() {
        return 2;
    }

}
