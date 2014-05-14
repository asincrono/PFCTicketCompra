package es.dexusta.ticketcompra.tests;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.StateFragment;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public class ListShopsActivity extends ListActivity {
    private static final String TAG = "ListShopsActivity";

    private static final String TAG_STATE_FRAGMENT = "state_fragment";
    private static final String KEY_LIST_ADAPTER   = "list_adapter";

    private StateFragment   mStateFragment;
    private DataSource      mDS;
    private ShopListAdapter mListAdapter;
    private TestData        mTestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_shops);

        mDS = DataSource.getInstance(getApplicationContext());

        mTestData = TestData.getInstance(getApplicationContext(), mDS);
        mTestData.buildDataInfo(new TestData.ChainedCallback() {
            @Override
            public void after() {
                HashMap<Long, Chain> chainHashMap = mTestData.getChainMap();
                FragmentManager manager = getFragmentManager();

                mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

                if (mStateFragment == null) {
                    mStateFragment = new StateFragment();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(mStateFragment, TAG_STATE_FRAGMENT).commit();
                }

                mListAdapter = (ShopListAdapter) mStateFragment.get(KEY_LIST_ADAPTER);
                if (mListAdapter == null) {
                    mListAdapter = new ShopListAdapter(ListShopsActivity.this, null, chainHashMap);
                    mStateFragment.put(KEY_LIST_ADAPTER, mListAdapter);
                }

                setListAdapter(mListAdapter);

                setContentView(R.layout.activity_list_shops);

                mDS.setShopCallback(new DataAccessCallbacks<Shop>() {
                    @Override
                    public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {

                    }

                    @Override
                    public void onDataReceived(List<Shop> results) {
                        mListAdapter.swapList(results);
                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                mDS.listShops();
            }
        }, null, false);
    }
}
