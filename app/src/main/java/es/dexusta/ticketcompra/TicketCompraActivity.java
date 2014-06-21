package es.dexusta.ticketcompra;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.InitializeDBTask.InitializerCallback;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.tests.ListProductsActivity;
import es.dexusta.ticketcompra.tests.ListReceiptsActivity;
import es.dexusta.ticketcompra.tests.ListShopsActivity;
import es.dexusta.ticketcompra.tests.TestActivity;

public class TicketCompraActivity extends CloudBackendActivity {
    private static final String  TAG   = "TicketCompraActivity";
    private static final boolean DEBUG = true;

    // Drawer menu constants.
    // Title: NEW DATA = 0
    private static final int DETAILED_RECEIPT       = 1;
    private static final int ADD_SHOP               = 2;
    private static final int ADD_PRODUCT            = 3;
    // Title: CHECK SPENDING = 5
    private static final int CUMULATIVE_SPENDING    = 5;
    private static final int SPENDING_IN_TIME       = 6;
    private static final int SPENDING_BY_CATEGORY   = 7;

    // Request codes for startAcitivityForResult.
    // Need to take in account that:
    // private static final int REQUEST_ACCOUNT_PICKER = 2;
    // defined in parent, SO I CAN NOT USE 2.
    private static final int REQUEST_SHOP_FOR_DETAILED_RECEIPT = 3;
    private static final int REQUEST_SHOP_FOR_TOTAL_RECEIPT    = 4;
    private static final int REQUEST_ADD_SHOP                  = 5;
    private static final int REQUEST_ADD_PRODUCT               = 6;

    // private static final int REQUEST_ = ;

    private DataSource mDS;

    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout           mFLContentFrame;
    private DrawerLayout          mDrawerLayout;

    private ListView mDrawerList;

    private CharSequence mTitle;

    private String[] mDrawerMenuTitles;
    private String[] mDrawerFirstMenu;
    private String[] mDrawerSecondMenu;
    //private String[] mDrawerThirdMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.ticket_compra_activity);

        mDrawerMenuTitles = getResources().getStringArray(R.array.drawer_menu_titles);
        mDrawerFirstMenu = getResources().getStringArray(R.array.drawer_first_menu);
        mDrawerSecondMenu = getResources().getStringArray(R.array.drawer_second_menu);
        //mDrawerThirdMenu = getResources().getStringArray(R.array.drawer_third_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerList = (ListView) findViewById(R.id.lv_drawer_list);
        mDrawerList.setAdapter(new DawerListAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Gestión del tique de la compra");

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                //getActionBar().setTitle("");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle("");
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setProgressBarVisibility(true);
        // App initialization
        boolean isInitalized = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getBoolean(Consts.PREF_DB_POPULATED, false);
        if (!isInitalized) {
            initData();
        }
    }

    @Override
    protected void onPostCreate() {
        mDS = DataSource.getInstance(getApplicationContext());
        // Puede que se entretejan download y upload.
        // En cualquier caso no debería de representar un problema a no ser
        // que "sobreescriban". Para evitarlo: encadenarlas.

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onPostCreate");
        if (BuildConfig.DEBUG)
            Log.d(TAG, "About to update data.");
        BackendDataAccessV2.updateData(getApplicationContext(), getCloudBackend());
    }

    private void initData() {
        DataSource ds = DataSource.getInstance(getApplicationContext());
        ds.initDatabase(new InitializerCallback() {

            @Override
            public void onInitialized() {
                setProgressBarVisibility(false);
            }
        });
    }

    private void showCumulativeSpending() {
        Intent intent = new Intent(this, CumulativeSpendingGraphActivity.class);
        startActivity(intent);
    }

    private void showSpendingInTime() {
        Intent intent = new Intent(this, SpendingInTimeGraphActivity.class);
        startActivity(intent);
    }

    private void showSpendingByCategory() {
        Intent intent = new Intent(this, SpendingByCategoryGraphActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // This makes the ic_drawer image to show
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        // Hide ActionBar items that shoudn't show when drawer open
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        Intent intent;
        switch (item.getItemId()) {
            case R.id.tests:
                intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Menu option \"Tests\" selected.");
                break;
            case R.id.list_shops:
                intent = new Intent(this, ListShopsActivity.class);
                startActivity(intent);
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Menu option \"List shops\" selected");
                break;
            case R.id.list_products:
                // TODO: Test this.
                intent = new Intent(this, ListProductsActivity.class);
                startActivity(intent);
                break;
            case R.id.list_receipts:
                // TODO: Test this.
                intent = new Intent(this, ListReceiptsActivity.class);
                startActivity(intent);
                break;
            case R.id.delete:
                mDS.setShopCallback(new DataAccessCallbacks<Shop>() {
                    @Override
                    public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                        if (operation == Types.Operation.DELETE) {
                            mDS.deleteProducts();
                        }

                        if (BuildConfig.DEBUG)
                            Log.d(TAG, processed + " shops deleted.");
                    }

                    @Override
                    public void onDataReceived(List<Shop> results) {

                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                mDS.setProductCallback(new DataAccessCallbacks<Product>() {
                    @Override
                    public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                        if (operation == Types.Operation.DELETE) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, processed + " products deleted.");
                            mDS.deleteReceipts();
                        }
                    }

                    @Override
                    public void onDataReceived(List<Product> results) {

                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                mDS.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                    @Override
                    public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                        if (operation == Types.Operation.DELETE) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, processed + " receipts deleted.");
                        }
                    }

                    @Override
                    public void onDataReceived(List<Receipt> results) {

                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                mDS.deleteShops();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onActivityResult.");

        if (resultCode == RESULT_OK) {

//            if (BuildConfig.DEBUG && data.getParcelableExtra(Keys.KEY_SELECTED_SHOP) == null)
//                throw new AssertionError("If RESULT_OK, returned show shouldn't be null");

            switch (requestCode) {
                case REQUEST_SHOP_FOR_DETAILED_RECEIPT:
                    data.setComponent(new ComponentName(this, AddDetailedReceiptActivity.class));
                    startActivity(data);
                    break;
                case REQUEST_SHOP_FOR_TOTAL_RECEIPT:
                    data.setComponent(new ComponentName(this, AddTotalActivity.class));
                    startActivity(data);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Shop selection cancelled.");
        } else {
            throw new AssertionError("We shouldn't reach this point");
        }
    }

    private class DrawerItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent;
            switch (position) { // item id -> position.
                case DETAILED_RECEIPT:
                    // call detailed ticket sequence.
                    intent = new Intent(TicketCompraActivity.this, SelectShopV2Activity.class);
                    intent.putExtra(Keys.KEY_DESTINATION_ACTIVITY, AddDetailedReceiptActivity.class);
                    startActivity(intent);
                    break;
                case ADD_SHOP:
                    intent = new Intent(TicketCompraActivity.this, AddShopActivity.class);
                    startActivity(intent);
                    break;
                case ADD_PRODUCT:
                    //intent = new Intent(TicketCompraActivity.this, AddProductActivity.class);
                    intent = new Intent(TicketCompraActivity.this, ProductSelectionActivityV2.class);
                    startActivity(intent);
                    break;
                case CUMULATIVE_SPENDING:
                    showCumulativeSpending();
                    break;
                case SPENDING_IN_TIME:
                    showSpendingInTime();
                    break;
                case SPENDING_BY_CATEGORY:
                    showSpendingByCategory();
                    break;
                default:
                    break;
            }

            Toast.makeText(TicketCompraActivity.this, "Selected: " + position, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class DawerListAdapter extends BaseAdapter {
        private final int mFirstMenuCount  = mDrawerFirstMenu.length;
        private final int mSecondMenuCount = mDrawerSecondMenu.length;
        //private final int mThirdMenuCount  = mDrawerThirdMenu.length;

        private final int mCount = mDrawerMenuTitles.length
                + mDrawerFirstMenu.length
                + mDrawerSecondMenu.length;
                //+ mDrawerThirdMenu.length;

        private final LayoutInflater mInflater = LayoutInflater
                .from(TicketCompraActivity.this);

        @Override
        public int getCount() {
            // Number of menu titles + number of menu elements:
            return mCount;
        }

        @Override
        public String getItem(int position) {

            ArrayList<String> totalArray = new ArrayList<String>();
            totalArray.add(mDrawerMenuTitles[0]);
            totalArray.addAll(Arrays.asList(mDrawerFirstMenu));
            totalArray.add(mDrawerMenuTitles[1]);
            totalArray.addAll(Arrays.asList(mDrawerSecondMenu));

            if (position >= 0 && position < mCount) {
                return totalArray.get(position);
            } else {
                throw new IllegalArgumentException("Invalid position: " + Integer.toString(position));
            }
        }

        //        @Override
//        public String getItem(int position) {
//            // Position 0 = title1
//            // Position 0 + mFirstMenuCount = title2
//            // Position 0 + mFirstMenuCount = title2
//            int offset;
//            if (position < mFirstMenuCount + 1) { // + 1 = one header.
//                offset = 0;
//                if (position == 0) { // position - offset == 0
//                    return mDrawerMenuTitles[0];
//                } else {
//                    return mDrawerFirstMenu[position - 1]; // position - offset
//                    // - 1 (header before
//                    // (first one))
//                }
//            } else if (position < mFirstMenuCount + mSecondMenuCount + 2) { // +
//                // 2
//                // =
//                // two
//                // headers.
//                offset = mFirstMenuCount + 1;
//                if (position == offset) { // = position - offset == 0
//                    return mDrawerMenuTitles[1];
//                } else {
//                    return mDrawerSecondMenu[position - offset - 1]; // 1
//                    // (headers
//                    // before
//                    // in this
//                    // chunk)
//                }
//            } else if (position < mFirstMenuCount + mSecondMenuCount + mThirdMenuCount + 3) { // +
//                // 3
//                // =
//                // three
//                // headers.
//                offset = mFirstMenuCount + mSecondMenuCount + 2;
//                if (position == offset) {
//                    return mDrawerMenuTitles[2];
//                } else {
//                    return mDrawerThirdMenu[position - offset - 1]; // one
//                    // header
//                    // before in
//                    // this
//                    // chunk
//                }
//            }
//
//            throw new IllegalArgumentException("Invalid position: " + Integer.toString(position));
//        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if ((position == 0) || (position == mFirstMenuCount + 1)
                    || (position == mFirstMenuCount + mSecondMenuCount + 2)) {
                return 1; // Title.
            }
            return 0; // Menu element.
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if (getItemViewType(position) == 0) return true;
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 1) {
                return getHeaderView(position, convertView, parent);
            }

            View row = convertView;

            if (row == null) {
                row = mInflater.inflate(R.layout.drawer_menu_item, parent, false);
            }

            MenuItemHolder holder = (MenuItemHolder) row.getTag();

            if (holder == null) {
                holder = new MenuItemHolder(row);
            }

            holder.tvText.setText(getItem(position));

            return row;
        }

        private View getHeaderView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                row = mInflater.inflate(R.layout.drawer_header, parent, false);
            }

            HeaderHolder holder = (HeaderHolder) row.getTag();

            if (holder == null) {
                holder = new HeaderHolder(row);
            }

            holder.tvText.setText(getItem(position));

            return row;
        }

        class HeaderHolder {
            TextView tvText;

            HeaderHolder(View v) {
                tvText = (TextView) v.findViewById(R.id.tv_header);
                v.setTag(this);
            }
        }

        class MenuItemHolder {
            TextView tvText;

            MenuItemHolder(View v) {
                tvText = (TextView) v.findViewById(R.id.tv_menu_item);
                v.setTag(this);
            }
        }
    }
}
