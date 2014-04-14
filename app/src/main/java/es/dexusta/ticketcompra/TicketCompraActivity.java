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

import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.InitializeDBTask.InitializerCallback;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.model.Shop;

public class TicketCompraActivity extends CloudBackendActivity {
    private static final String   TAG                               = "TicketCompraActivity";
    private static final boolean  DEBUG                             = true;

    // Drawer menu constants.
    // Title: NEW DATA = 0
    private static final int      DETAILED_RECEIPT                  = 1;
    private static final int      TOTAL_TICKET                      = 2;
    private static final int      ADD_SHOP                          = 3;
    private static final int      ADD_PRODUCT                       = 4;
    // Title: CHECK SPENDING = 5
    private static final int      CUMULATIVE_SPENDING               = 6;
    private static final int      SPENDING_IN_TIME                  = 7;
    private static final int      SPENDING_BY_CATEGORY              = 8;
    // Title: REPORTS & SUGGESTIONS = 9
    private static final int      PURCHASING_SUGGESTIONS            = 10;
    private static final int      BETTER_PRICES                     = 11;

    // Request codes for startAcitivityForResult.
    // Need to take in account that:
    // private static final int REQUEST_ACCOUNT_PICKER = 2;
    // defined in parent, SO I CAN NOT USE 2.
    private static final int      REQUEST_SHOP_FOR_DETAILED_RECEIPT = 3;
    private static final int      REQUEST_SHOP_FOR_TOTAL_RECEIPT    = 4;
    private static final int      REQUEST_ADD_SHOP                  = 5;
    private static final int      REQUEST_ADD_PRODUCT               = 6;

    // private static final int REQUEST_ = ;

    private DataSource            mDS;

    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout           mFLContentFrame;
    private DrawerLayout          mDrawerLayout;

    private ListView              mDrawerList;

    private CharSequence          mTitle;

    private String[]              mDrawerMenuTitles;
    private String[]              mDrawerFirstMenu;
    private String[]              mDrawerSecondMenu;
    private String[]              mDrawerThirdMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.ticket_compra_activity);

        mDrawerMenuTitles = getResources().getStringArray(R.array.drawer_menu_titles);
        mDrawerFirstMenu = getResources().getStringArray(R.array.drawer_first_menu);
        mDrawerSecondMenu = getResources().getStringArray(R.array.drawer_second_menu);
        mDrawerThirdMenu = getResources().getStringArray(R.array.drawer_third_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerList = (ListView) findViewById(R.id.lv_drawer_list);
        mDrawerList.setAdapter(new DawerListAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle("Hola");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Hola drawer");
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
        mDS.downloadData(getCloudBackend());
        mDS.uploadData(getCloudBackend());
    }

    private void initData() {
        DataSource ds = DataSource.getInstance(getApplicationContext());
        ds.initDatabase(new InitializerCallback() {

            @Override
            public void onInitialized() {
                setProgressBarVisibility(false);
            }
        });
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        // Hide ActionBar items that shoudn't show when drawer open
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // This makes the ic_drawer image to show
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
            case TOTAL_TICKET:
                // call total ticket sequence.
                intent = new Intent(TicketCompraActivity.this, SelectShopV2Activity.class);
                intent.putExtra(Keys.KEY_DESTINATION_ACTIVITY, AddTotalActivity.class);
                startActivity(intent);
                break;
            case ADD_SHOP:
                intent = new Intent(TicketCompraActivity.this, AddShopV2Activity.class);
                startActivity(intent);
                break;
            case ADD_PRODUCT:
                intent = new Intent(TicketCompraActivity.this, AddProductActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Shop shop;
        // Intent intent;
        switch (requestCode) {
        case REQUEST_SHOP_FOR_DETAILED_RECEIPT:
            if (resultCode == RESULT_OK) {
                shop = data.getParcelableExtra(Keys.KEY_SHOP);
                data.setComponent(new ComponentName(this, AddDetailedReceiptActivity.class));
                // intent = new Intent(this, AddDetailedReceiptActivity.class);
                // intent.putExtra(Keys.KEY_SHOP, shop);
                startActivity(data);
            }
            break;

        case REQUEST_SHOP_FOR_TOTAL_RECEIPT:
            if (resultCode == RESULT_OK) {
                shop = data.getParcelableExtra(Keys.KEY_SHOP);
                data.setComponent(new ComponentName(this, AddTotalActivity.class));
                startActivity(data);
            } else if (resultCode == RESULT_CANCELED) {
                if (DEBUG) Log.d(TAG, "Add total recipe canceled.");
            }
            break;
        }
    }

    private class DawerListAdapter extends BaseAdapter {
        private final int            mFirstMenuCount  = mDrawerFirstMenu.length;
        private final int            mSecondMenuCount = mDrawerSecondMenu.length;
        private final int            mThirdMenuCount  = mDrawerThirdMenu.length;

        private final int            mCount           = mDrawerMenuTitles.length
                                                              + mDrawerFirstMenu.length
                                                              + mDrawerSecondMenu.length
                                                              + mDrawerThirdMenu.length;

        private final LayoutInflater mInflater        = LayoutInflater
                                                              .from(TicketCompraActivity.this);

        @Override
        public int getCount() {
            // Number of menu titles + number of menu elements:
            return mCount;
        }

        @Override
        public String getItem(int position) {
            // Position 0 = title1
            // Position 0 + mFirstMenuCount = title2
            // Position 0 + mFirstMenuCount = title2
            int offset;
            if (position < mFirstMenuCount + 1) { // + 1 = one header.
                offset = 0;
                if (position == 0) { // position - offset == 0
                    return mDrawerMenuTitles[0];
                } else {
                    return mDrawerFirstMenu[position - 1]; // position - offset
                    // - 1 (header before
                    // (first one))
                }
            } else if (position < mFirstMenuCount + mSecondMenuCount + 2) { // +
                // 2
                // =
                // two
                // headers.
                offset = mFirstMenuCount + 1;
                if (position == offset) { // = position - offset == 0
                    return mDrawerMenuTitles[1];
                } else {
                    return mDrawerSecondMenu[position - offset - 1]; // 1
                    // (headers
                    // before
                    // in this
                    // chunk)
                }
            } else if (position < mFirstMenuCount + mSecondMenuCount + mThirdMenuCount + 3) { // +
                // 3
                // =
                // three
                // headers.
                offset = mFirstMenuCount + mSecondMenuCount + 2;
                if (position == offset) {
                    return mDrawerMenuTitles[2];
                } else {
                    return mDrawerThirdMenu[position - offset - 1]; // one
                    // header
                    // before in
                    // this
                    // chunk
                }
            }

            throw new IllegalArgumentException("Invalid position: " + Integer.toString(position));
        }

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
