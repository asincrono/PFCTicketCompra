package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.ListDetailsFragment.ListDetailsCallback;
import es.dexusta.ticketcompra.ListReceiptsFragment.ListReceiptsCallback;
import es.dexusta.ticketcompra.control.ReceiptAdapter;
import es.dexusta.ticketcompra.control.ReceiptDetailAdapter;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;


public class ListReceiptsActivity extends Activity implements ListDetailsCallback,
        ListReceiptsCallback {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "ListReceiptsActivity";

    private static final String TAG_STATE_FRAGMENT         = "state_fragment";
    private static final String TAG_LIST_RECEIPTS_FRAGMENT = "list_receipts_fragment";
    private static final String TAG_LIST_DETAILS_FRAGMENT  = "list_details_fragment";

    private Receipt mSelectedReceipt;
    private List<Receipt> mReceipts;
    private HashMap<Receipt, List<Detail>> mReceiptDetailMap;


    private ReceiptAdapter       mReceiptAdapter;
    private ReceiptDetailAdapter mReceiptDetailAdapter;

    private StateFragment mStateFragment;


    private DataSource mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (savedInstanceState == null) {
            // Cold start of this activity.

            // Creation of the Fragment to store data between
            // configuration changes.
            Fragment fragment = new StateFragment();
            transaction.add(new StateFragment(), TAG_STATE_FRAGMENT);


            // We show the first fragment.
            fragment = new ListReceiptsFragment();

            transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                    R.animator.enter_from_left, R.animator.exit_to_right);
            transaction.add(android.R.id.content, fragment, TAG_LIST_RECEIPTS_FRAGMENT);
            transaction.commit();
        } else {
            // We retrieve all the possible data stored.
            mSelectedReceipt = (Receipt) mStateFragment.get(Keys.KEY_CURRENT_RECEIPT);
            mReceipts = (List<Receipt>) mStateFragment.get(Keys.KEY_RECEIPT_LIST);
            mReceiptDetailMap = (HashMap<Receipt, List<Detail>>) mStateFragment.get(Keys.KEY_DETAIL_LIST);
            mSelectedReceipt = (Receipt) mStateFragment.get(Keys.KEY_RECEIPT);
            // We don't need to show any fragment, FragmentManager should take care of that.
        }

        // We create the adapters that will transfer (made available) the data to do the fragments.
        mReceiptAdapter = new ReceiptAdapter(this, mReceipts);
        mReceiptDetailAdapter = new ReceiptDetailAdapter(this, mReceiptDetailMap.get(mSelectedReceipt));

        mDS = DataSource.getInstance(getApplicationContext());

        // We set up the callbacks to get Receipts and Details from de DB.
        mDS.setReceiptCallback(new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                if (results != null) {
                    // We don't need to check if (or when) a fragment is created. The fragment
                    // itself will get the ListAdapter.
                    mReceipts = results;
                    mStateFragment.put(Keys.KEY_RECEIPT_LIST, mReceipts);
                    // As we update de list in the ListAdapter, the ListView in the fragment will
                    // be automatically updated.
                    mReceiptAdapter.swapList(mReceipts);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setDetailCallback(new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                mReceiptDetailMap.put(mSelectedReceipt, results);
                // To avoid as many access to de DB as possible, we save all the accesses to the
                // details in a HashMap using the Receipt as key.
                mStateFragment.put(Keys.KEY_RECEIPT_DETAIL_MAP, mReceiptDetailMap);
                mReceiptDetailAdapter.swapList(results);
            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean isInsertionActive() {
        return false;
    }

    @Override
    public void onAddDetail() {
        // TODO Auto-generated method stub

    }

    @Override
    public ReceiptDetailAdapter getReceiptDetailListAdapter() {
        return mReceiptDetailAdapter;
    }

    @Override
    public void onListDetailsAccepted() {

    }

    @Override
    public void onListDetailsCanceled() {
        // TODO Auto-generated method stub
        onBackPressed();
    }

    @Override
    public void onReceiptSelected(Receipt receipt) {
        mSelectedReceipt = receipt;

        List<Detail> details = mReceiptDetailMap.get(receipt);

        if (details != null) {
            mReceiptDetailAdapter.swapList(details);
        } else {
            mDS.getDetailsBy(receipt);
        }

        showReceiptDetails();
    }

    @Override
    public void onCancelReceiptSelection() {
        onBackPressed();
    }

    @Override
    public ReceiptAdapter getReceiptAdapter() {
        return mReceiptAdapter;
    }

    private void showReceiptDetails() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right);

        Fragment fragment = manager.findFragmentByTag(TAG_LIST_DETAILS_FRAGMENT);
        if (fragment == null) {
            fragment = new ListDetailsFragment();
            transaction.replace(android.R.id.content, fragment, TAG_LIST_DETAILS_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }


}
