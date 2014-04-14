package es.dexusta.ticketcompra;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import es.dexusta.ticketcompra.control.ShopAdapter;
import es.dexusta.ticketcompra.control.ShopSelectionCallback;
import es.dexusta.ticketcompra.model.Shop;

/**
 * Created by asincrono on 16/05/13.
 */
public class ShopSelectionFragment extends ListFragment {
    private static final String   TAG   = "ShopSelectionFragment";
    private static boolean        DEBUG = true;

    private ShopSelectionCallback mCallback;
    
    private Button mBttNewShop; 

    public static ShopSelectionFragment newInstance() {
        return new ShopSelectionFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ShopSelectionCallback) {
            mCallback = (ShopSelectionCallback) activity;
            setListAdapter(mCallback.getShopAdapter());
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ShopSelectionCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.shop_selec_fragment, container, false);
        mBttNewShop = (Button) v.findViewById(R.id.btt_add_shop);
        mBttNewShop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onClickAddShop();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreateView.");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            Log.d(TAG, "onStart.");
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume.");
        
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause.");
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Shop shop = ((ShopAdapter) getListAdapter()).getItem(position);
        mCallback.onShopSelected(shop);
    }
}