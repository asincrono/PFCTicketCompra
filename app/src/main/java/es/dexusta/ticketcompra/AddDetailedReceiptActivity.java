package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.AddDetailFragment.AddDetailCallback;
import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.ReceiptDetailAdapter;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.tests.ListDetailsFragment;
import es.dexusta.ticketcompra.tests.ListDetailsFragment.ListDetailsCallback;

public class AddDetailedReceiptActivity extends CloudBackendActivity implements
        AddDetailCallback, ListDetailsCallback {
    private static final String TAG                       = "AddDetailedReceiptActivity";
    private static final String TAG_STATE_FRAGMENT        = "state_fragment";
    private static final String TAG_LIST_DETAILS_FRAGMENT = "list_details_fragment";
    private static final String TAG_ADD_DETAIL_FRAGMENT   = "add_detail_fragment";

    // Won't show current activity until second activity returns.
    private static final int    REQUEST_PRODUCT_SELECTION = 0;

    private boolean      mShowingClassicAB;
    private Shop         mSelectedShop;
    private Product      mSelectedProduct;
    private Receipt      mReceipt;
    private List<Detail> mDetails;

    private ReceiptDetailAdapter mDetailAdapter;

    private StateFragment mStateFragment;

    private DataSource mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedShop = getIntent().getExtras().getParcelable(Keys.KEY_SELECTED_SHOP);

        FragmentManager manager = getFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            mStateFragment = new StateFragment();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT);
            transaction.add(android.R.id.content, new ListDetailsFragment(), TAG_LIST_DETAILS_FRAGMENT);

            transaction.commit();
        } else {
            mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

            if (BuildConfig.DEBUG && (mStateFragment == null))
                throw new AssertionError("StateFragment shouldn't be null");

            mSelectedProduct = (Product) mStateFragment.get(Keys.KEY_PRODUCT);
            mDetails = (List<Detail>) mStateFragment.get(Keys.KEY_DETAIL_LIST);
        }

        mDetailAdapter = new ReceiptDetailAdapter(this, mDetails);

        mDS = DataSource.getInstance(getApplicationContext());

        // Set listeners:
        mDS.setReceiptCallback(new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                                        boolean result) {
                if (result) {
                    mReceipt = dataList.get(0);

                    for (Detail detail : mDetails) {
                        detail.setReceiptId(mReceipt.getId());
                    }
                    mDS.insertDetails(mDetails);
                }
            }
        });

        mDS.setDetailCallback(new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                                        boolean result) {
                // SHOW DIALOG INDICATING DETAILS INSERTED / OR ERROR.
                // Then finish().
                Toast.makeText(AddDetailedReceiptActivity.this,
                        " Details inserted: " + dataList.size() + ".", Toast.LENGTH_SHORT).show();

                if (BackendDataAccessV2.hasConnectivity(getApplicationContext())) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "about to upload receipt & details.");

                    //BackendDataAccess.uploadReceiptDetails(mReceipt, dataList,
                    // getApplicationContext(), getCloudBackend());
                    BackendDataAccessV2.uploadReceiptAndDetails(mReceipt, dataList,
                            getApplicationContext(), getCloudBackend());
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStateFragment.put(Keys.KEY_PRODUCT, mSelectedProduct);
        mStateFragment.put(Keys.KEY_DETAIL_LIST, mDetails);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PRODUCT_SELECTION:
                if (resultCode == RESULT_OK) {
                    mSelectedProduct = data.getParcelableExtra(Keys.KEY_SELECTED_PRODUCT);
                    showAddDetailFragment();
                }
                break;
        }
    }

    private void showAddDetailFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        applyTransactionAnimator(transaction);

        AddDetailFragment fragment = (AddDetailFragment) manager
                .findFragmentByTag(TAG_ADD_DETAIL_FRAGMENT);

        if (fragment == null) {
            fragment = new AddDetailFragment();
            transaction.replace(android.R.id.content, fragment, TAG_ADD_DETAIL_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void applyTransactionAnimator(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right);
    }

    private void showAcceptCancelActionBar() {
        final ActionBar actionBar = getActionBar();

        LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());

        final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null);

        // actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
        // actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        // Previous line is equivalent to:
        // actionBar.setDisplayShowTitleEnabled(false);
        // actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setDisplayUseLogoEnabled(false);
        // actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    // @Override
    // public void onBackPressed() {
    // int currentFragment = mViewPager.getCurrentItem();
    // if (currentFragment > 0) {
    // mViewPager.setCurrentItem(--currentFragment);
    // } else {
    // super.onBackPressed();
    // }
    // }

    @Override
    public void onAddDetail() {
        // Select product.
        Intent intent = new Intent(this, ProductSelectionActivityV2.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onAddDetailCanceled() {
        Intent intent = new Intent(this, TicketCompraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public ReceiptDetailAdapter getReceiptDetailListAdapter() {
        return mDetailAdapter;
    }

    @Override
    public void onListDetailsAccepted() {
        // 0.- Check if there is a null or empty list.
        // 1.- Create and insert the new receipt in the BD
        // 2.- Then, when receipt inserted, insert the details.
        // 3.- Then, after details inserted in the BD upload receipt & details
        // to the backend.

        // (0) Checking if there is a null or empty list:
        if ((mDetails == null) || (mDetails.size() == 0)) {

            Toast.makeText(this, getString(R.string.msg_empty_detail_list),
                    Toast.LENGTH_SHORT).show();
        } else {
            // (1) Creating and inserting a new receipt.
            mReceipt = new Receipt();
            mReceipt.setShopId(mSelectedShop.getId());

            // Get the total.
            int total = 0;
            for (Detail detail : mDetails) {
                total += detail.getPrice();
            }
            mReceipt.setTotal(total);

            mReceipt.setTimestamp(System.currentTimeMillis());
            List<Receipt> listReceipt = new ArrayList<Receipt>();
            listReceipt.add(mReceipt);
            mDS.insertReceipts(listReceipt);

            // TODO: Mostrar una lista con los receipts insertados donde figure
            // el
            // presente?
            Intent intent = new Intent(this, TicketCompraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onListDetailsCanceled() {
        Intent intent = new Intent(this, TicketCompraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDetailAdded(Detail detail) {
        if (mDetails == null) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Details list was null.");
            mDetails = new ArrayList<Detail>();
            mDetailAdapter.swapList(mDetails);
        }
        mDetails.add(detail);
        onBackPressed();
    }

    @Override
    public Product getSelectedProduct() {
        return mSelectedProduct;
    }

    @Override
    public boolean isInsertionActive() {
        return true;
    }


    @Override
    public void showAcceptCancelActionBar(View.OnClickListener onClickAccept,
                                          View.OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            mShowingClassicAB = false;
            ActionBarController.setAcceptCancel(actionBar, onClickAccept, onClickCancel);
        }
    }

    @Override
    public void hideAcceptCancelActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null && !mShowingClassicAB) {
            mShowingClassicAB = true;
            ActionBarController.setDisplayDefault(actionBar);
        }
    }

    @Override
    public boolean isABAvaliable() {
        return (getActionBar() != null);
    }

    @Override
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
}
