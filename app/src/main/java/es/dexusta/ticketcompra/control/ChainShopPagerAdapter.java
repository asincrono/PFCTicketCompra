package es.dexusta.ticketcompra.control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.dexusta.ticketcompra.ChainSelectionFragment;
import es.dexusta.ticketcompra.ShopSelectionFragment;

public class ChainShopPagerAdapter extends FragmentPagerAdapter {
    public static final int CHAIN_FRAGMENT = 0;
    public static final int SHOP_FRAGMENT = 1;

    public ChainShopPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
        case CHAIN_FRAGMENT:
            return ChainSelectionFragment.newInstance();
        case SHOP_FRAGMENT:
            return ShopSelectionFragment.newInstance();
        }
        return null;        
    }

    @Override
    public int getCount() {
        return 2;
    }

}
