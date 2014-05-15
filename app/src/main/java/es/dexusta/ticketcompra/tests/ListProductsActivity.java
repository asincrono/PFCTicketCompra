package es.dexusta.ticketcompra.tests;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.StateFragment;
import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Product;

public class ListProductsActivity extends ListActivity {
    private static final String TAG = "ListProductsActivity";

    private static final String TAG_STATE_FRAGMENT = "state_fragment";
    private static final String KEY_PRODUCT_ADAPTER = "product_adapter";

    private DataSource mDS;
    private ProductAdapter mAdapter;

    private StateFragment mStateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);

        mDS = DataSource.getInstance(getApplicationContext());

        FragmentManager manager = getFragmentManager();

        mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

        if (mStateFragment == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            mStateFragment = new StateFragment();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT).commit();
        }

        mAdapter = (ProductAdapter) mStateFragment.get(KEY_PRODUCT_ADAPTER);

        if (mAdapter == null) {
            mAdapter = new ProductAdapter(this);
            mStateFragment.put(KEY_PRODUCT_ADAPTER, mAdapter);
        }

        setListAdapter(mAdapter);

        mDS.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Product> results) {
                mAdapter.swapList(results);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        mDS.listProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
