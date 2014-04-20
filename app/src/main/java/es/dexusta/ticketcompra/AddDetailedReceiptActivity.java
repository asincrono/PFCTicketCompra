package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.AddDetailFragment.AddDetailCallback;
import es.dexusta.ticketcompra.ListDetailsFragment.ListDetailsCallback;
import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
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

public class AddDetailedReceiptActivity extends CloudBackendActivity implements
        AddDetailCallback, ListDetailsCallback {
    private static final String  TAG   = "DetailedReceiptAcitivity";
    private static final boolean DEBUG = true;

    private static final String TAG_LIST_DETAILS_FRAGMENT   = "list_details_fragment";

    private static final String TAG_ADD_DETAIL_FRAGMENT     = "add_detail_fragment";
    private static final String TAG_STATE_FRAGMENT          = "state_fragment";

    // Won't show current activity until second activity returns.
    private static final int REQUEST_PRODUCT_SELECTION = 0;

    private Shop         mShop;
    private Product      mProduct;
    private Receipt      mReceipt;
    private List<Detail> mDetails;

    private ReceiptDetailAdapter mDetailAdapter;

    private StateFragment mStateFragment;

    private int mCurrentFragment;

    // private NoSwipeViewPager mViewPager;

    private DataSource mDS;

    // private OnClickListener mOnClickAccept;
    // private OnClickListener mOnClickCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShop = getIntent().getExtras().getParcelable(Keys.KEY_SHOP);

        FragmentManager manager = getFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(new StateFragment(), TAG_STATE_FRAGMENT);
            transaction.commit();

            transaction = manager.beginTransaction();
            transaction.add(android.R.id.content, new ListDetailsFragment(), TAG_LIST_DETAILS_FRAGMENT);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

            if (BuildConfig.DEBUG && (mStateFragment == null))
                throw new AssertionError("StateFragment shouldn't be null");

            mProduct = (Product) mStateFragment.get(Keys.KEY_PRODUCT);
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
                    long receiptId = dataList.get(0).getId();
                    List<CloudEntity> ceList = new ArrayList<CloudEntity>();

                    for (Detail detail : mDetails) {
                        detail.setReceiptId(receiptId);
                        ceList.add(detail.getEntity(getApplicationContext()));
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

                if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                    BackendDataAccess.uploadDetails(dataList, getApplicationContext(),
                            getCloudBackend());
                }

                Intent intent = new Intent(AddDetailedReceiptActivity.this,
                        TicketCompraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();
            }
        });

        showAcceptCancelActionBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStateFragment.put(Keys.KEY_PRODUCT, mProduct);
        mStateFragment.put(Keys.KEY_DETAIL_LIST, mDetails);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TESTING HIDE/SHOW ACTION BAR.
        ActionBar actionBar = getActionBar();
        actionBar.show();

        switch (requestCode) {
            case REQUEST_PRODUCT_SELECTION:
                if (resultCode == RESULT_OK) {
                    mProduct = data.getParcelableExtra(Keys.KEY_PRODUCT);
                    showAddDetailFragment(mProduct);
                }
                break;
        }
    }

    private void showAddDetailFragment(Product product) {

        FragmentManager manager = getFragmentManager();

        AddDetailFragment fragment = (AddDetailFragment) manager
                .findFragmentByTag(TAG_ADD_DETAIL_FRAGMENT);

        FragmentTransaction transaction = manager.beginTransaction();

        if (fragment == null) {
            fragment = new AddDetailFragment();
            transaction.replace(android.R.id.content, fragment, TAG_ADD_DETAIL_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }

        fragment.setProduct(product);

        // Es el segundo fragmento y podrá volverse al primero?
        transaction.addToBackStack(null);

        transaction.commit();
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
        Intent intent = new Intent(this, ProductSelectionActivity.class);
        startActivityForResult(intent, 0);
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

        if ((mDetails == null) || (mDetails.size() == 0)) {
            String text = getString(R.string.msg_empty_detail_list);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            DataAccessCallbacks<Receipt> receiptCallbacks = new DataAccessCallbacks<Receipt>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataReceived(List<Receipt> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList,
                                            Operation operation, boolean result) {
                    long receiptId = mReceipt.getId();
                    for (Detail detail : mDetails) {
                        detail.setReceiptId(receiptId);
                    }
                    mDS.insertDetails(mDetails);
                }
            };

            DataAccessCallbacks<Detail> detailCallbacks = new DataAccessCallbacks<Detail>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataReceived(List<Detail> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataProcessed(int processed, List<Detail> dataList,
                                            Operation operation, boolean result) {
                    // Iniciar la cadena de inserción del receipt y de los
                    // detalles
                    // en el backend.
                    // Además (si se completa la tarea) se marcarán como
                    // actualizados en la BD.
                    BackendDataAccess.uploadReceiptDetails(mReceipt, mDetails,
                            getApplicationContext(), getCloudBackend());
                }
            };

            mDS.setReceiptCallback(receiptCallbacks);
            mDS.setDetailCallback(detailCallbacks);

            mReceipt = new Receipt();
            mReceipt.setShopId(mShop.getId());

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
            mDetails = new ArrayList<Detail>();
        }
        mDetails.add(detail);
        onBackPressed();
    }

    @Override
    public void onAddDetailCanceled() {
        Intent intent = new Intent(this, TicketCompraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean isInsertionActive() {
        return true;
    }

    public class InsertDetailsHandler extends CloudCallbackHandler<List<CloudEntity>> {
        private List<Detail> mDetails;
        private Context      mContext;

        public InsertDetailsHandler(List<Detail> details, Context context) {
            mDetails = details;
            mContext = context;
        }

        @Override
        public void onComplete(List<CloudEntity> results) {
            List<Detail> details = new ArrayList<Detail>();
            for (CloudEntity entity : results) {
                details.add(new Detail(entity));
            }
            mDS.insertDetails(details);
            Toast.makeText(mContext, results.size() + " details inserted in the cloud",
                    Toast.LENGTH_SHORT).show();
            mContext = null;
        }

        @Override
        public void onError(IOException exception) {
            mDS.insertDetails(mDetails);
            Toast.makeText(mContext, "Details NOT inserted in the cloud", Toast.LENGTH_SHORT)
                    .show();
            mContext = null;
        }
    }
}
