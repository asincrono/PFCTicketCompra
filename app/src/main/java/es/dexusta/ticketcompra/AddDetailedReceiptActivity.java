package es.dexusta.ticketcompra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendFragmentActivity;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import es.dexusta.ticketcompra.AddDetailFragment.AddDetailCallback;
import es.dexusta.ticketcompra.ListDetailsFragment.ListDetailsCallback;
import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;

public class AddDetailedReceiptActivity extends CloudBackendFragmentActivity implements
        AddDetailCallback, ListDetailsCallback {
    private static final String          TAG                       = "DetailedReceiptAcitivity";
    private static final boolean         DEBUG                     = true;

    private static final int             LIST_DETAILS_FRAGMENT     = 0;
    private static final int             ADD_DETAL_FRAGMENT        = 1;

    private static final String          TAG_LIST_DETAILS_FRAGMENT = "list_details_fragment";
    private static final String          TAG_ADD_DETAIL_FRAGMENT   = "add_detail_fragment";

    // Won't show current activity until second activity returns.
    private static final int             REQUEST_PRODUCT_SELECTION = 0;

    private Shop                         mShop;
    private Product                      mProduct;
    private Receipt                      mReceipt;
    private ArrayList<Detail>            mDetails;

    private int                          mCurrentFragment;

    // private NoSwipeViewPager mViewPager;

    private DataSource                   mDS;
    private DataAccessCallbacks<Receipt> mReceiptListener;
    private DataAccessCallbacks<Detail>  mDetailListener;

    // private OnClickListener mOnClickAccept;
    // private OnClickListener mOnClickCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShop = getIntent().getExtras().getParcelable(Keys.KEY_SHOP);
        mCurrentFragment = LIST_DETAILS_FRAGMENT;

        if (savedInstanceState != null) {
            mProduct = (Product) savedInstanceState.getParcelable(Keys.KEY_PRODUCT);
            mDetails = savedInstanceState.getParcelableArrayList(Keys.KEY_DETAIL_LIST);
            mCurrentFragment = savedInstanceState.getInt(Keys.KEY_CURRENT_FRAGMENT,
                    LIST_DETAILS_FRAGMENT);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (mCurrentFragment == LIST_DETAILS_FRAGMENT) {
            showListDetailsFragment(mDetails);
        } else {
            showAddDetailFragment(mProduct);
        }

        ft.commit();

        mDS = DataSource.getInstance(getApplicationContext());

        // Set listeners:
        mReceiptListener = new DataAccessCallbacks<Receipt>() {

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
        };

        mDetailListener = new DataAccessCallbacks<Detail>() {

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
        };

        mDS.setReceiptCallback(mReceiptListener);
        mDS.setDetailCallback(mDetailListener);

        // setContentView(R.layout.pager_activity);
        // mViewPager = (NoSwipeViewPager) findViewById(R.id.view_pager);
        // mViewPager.setAdapter(new
        // DetailedReceiptPagerAdapter(getSupportFragmentManager()));

        // showAcceptCancelActionBar(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // Save receipt & detail list if not empty.
        // if (mDetails != null && mDetails.size() > 0) {
        // // 1.- Save receipt.
        // Receipt receipt = new Receipt();
        // receipt.setShopId(mShop.getId());
        //
        // ArrayList<Receipt> list = new ArrayList<Receipt>();
        // list.add(receipt);
        // mDS.insertReceipts(list);
        // // 2.- Save details. (will be triggered by previous
        // // insertion.
        // }
        // }
        // }, new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // finish();
        // }
        // });

        // mOnClickAccept = new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // check wich fragment called this.
        // if (mCurrentFragment == LIST_DETAILS_FRAGMENT) {
        // Toast.makeText(AddDetailedReceiptActivity.this,
        // "Accept from ListDetailsFragment", Toast.LENGTH_LONG);
        // } else {
        // Toast.makeText(AddDetailedReceiptActivity.this,
        // "Accept from AddDetailFragment", Toast.LENGTH_LONG);
        // }
        // }
        // };
        //
        // mOnClickCancel = new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // check wich fragment called this.
        // if (mCurrentFragment == LIST_DETAILS_FRAGMENT) {
        // Toast.makeText(AddDetailedReceiptActivity.this,
        // "Cancel from ListDetailsFragment", Toast.LENGTH_LONG).show();;
        // } else {
        // Toast.makeText(AddDetailedReceiptActivity.this,
        // "Cancel from AddDetailFragment", Toast.LENGTH_LONG).show();
        // }
        // }
        // };

        showAcceptCancelActionBar();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Keys.KEY_PRODUCT, mProduct);
        outState.putParcelableArrayList(Keys.KEY_DETAIL_LIST, mDetails);
        outState.putInt(Keys.KEY_CURRENT_FRAGMENT, mCurrentFragment);
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

    private void showListDetailsFragment(List<Detail> details) {
        boolean created = false;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ListDetailsFragment fragment = (ListDetailsFragment) fm
                .findFragmentByTag(TAG_LIST_DETAILS_FRAGMENT);

        if (fragment == null) {
            fragment = new ListDetailsFragment();
            ft.replace(android.R.id.content, fragment, TAG_LIST_DETAILS_FRAGMENT);
        } else {
            ft.replace(android.R.id.content, fragment);
        }

        fragment.setList(details);

        // if (fragment == null) {
        // created = true;
        // fragment = new ListDetailsFragment();
        // }
        // fragment.setList(details);
        //
        // if (created) {
        // ft.replace(android.R.id.content, fragment,
        // TAG_LIST_DETAILS_FRAGMENT);
        // } else {
        // ft.replace(android.R.id.content, fragment);
        // }
        ft.commit();

        mCurrentFragment = LIST_DETAILS_FRAGMENT;
    }

    private void showAddDetailFragment(Product product) {
        boolean created = false;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        AddDetailFragment fragment = (AddDetailFragment) fm
                .findFragmentByTag(TAG_ADD_DETAIL_FRAGMENT);

        if (fragment == null) {
            fragment = new AddDetailFragment();
            ft.replace(android.R.id.content, fragment, TAG_ADD_DETAIL_FRAGMENT);
        } else {
            ft.replace(android.R.id.content, fragment);
        }
        
        fragment.setProduct(product);

        // if (fragment == null) {
        // created = true;
        // fragment = new AddDetailFragment();
        // }
        // fragment.setProduct(product);
        //
        // if (created) {
        // ft.replace(android.R.id.content, fragment, TAG_ADD_DETAIL_FRAGMENT);
        // } else {
        // ft.replace(android.R.id.content, fragment);
        // }

        // Es el segundo fragmento y podrá volverse al primero?
        ft.addToBackStack(null);

        ft.commit();

        mCurrentFragment = ADD_DETAL_FRAGMENT;
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
    public void onListDetailsAccepted(List<Detail> details) {
        // 0.- Check if there is a null or empty list.
        // 1.- Create and insert the new receipt in the BD
        // 2.- Then, when receipt inserted, insert the details.
        // 3.- Then, after details inserted in the BD upload receipt & details
        // to the backend.

        if ((details == null) || (details.size() == 0)) {
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

            // TODO: ¿Mostrar una lista con los receipts insertados donde figure
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
        showListDetailsFragment(mDetails);
    }

    @Override
    public void onAddDetailCanceled() {
        Intent intent = new Intent(this, TicketCompraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // private void showAcceptCancelActionBar(OnClickListener onClickAccept,
    // OnClickListener onClickCancel) {
    // final ActionBar actionBar = getActionBar();
    //
    // LayoutInflater inflater =
    // LayoutInflater.from(actionBar.getThemedContext());
    //
    // final View actionBarCustomView =
    // inflater.inflate(R.layout.actionbar_cancel_accept, null);
    //
    // actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
    // actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);
    //
    // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
    // ActionBar.DISPLAY_SHOW_CUSTOM
    // | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    // // Previous line is equivalent to:
    // // actionBar.setDisplayShowTitleEnabled(false);
    // // actionBar.setDisplayShowHomeEnabled(false);
    // // actionBar.setDisplayUseLogoEnabled(false);
    // // actionBar.setDisplayShowCustomEnabled(true);
    //
    // actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
    // ViewGroup.LayoutParams.MATCH_PARENT,
    // ViewGroup.LayoutParams.MATCH_PARENT));
    // }

    // private class DetailedReceiptPagerAdapter extends FragmentPagerAdapter {
    // private static final int FRAGMENTS = 2;
    //
    // private SparseArray<WeakReference<Fragment>> mRegisteredFragments = new
    // SparseArray<WeakReference<Fragment>>();
    // private FragmentManager mFragmentManager;
    //
    // public DetailedReceiptPagerAdapter(FragmentManager fm) {
    // super(fm);
    // mFragmentManager = fm;
    // }
    //
    // @Override
    // public Object instantiateItem(ViewGroup container, int position) {
    // Fragment fragment = (Fragment) super.instantiateItem(container,
    // position);
    // mRegisteredFragments.put(position, new
    // WeakReference<Fragment>(fragment));
    // return fragment;
    // }
    //
    // @Override
    // public void destroyItem(ViewGroup container, int position, Object object)
    // {
    // super.destroyItem(container, position, object);
    // mRegisteredFragments.remove(position);
    // }
    //
    // public Fragment getFragment(int position) {
    // WeakReference<Fragment> fw = mRegisteredFragments.get(position);
    // return (fw != null) ? fw.get() : null;
    // }
    //
    // @Override
    // public Fragment getItem(int position) {
    // WeakReference<Fragment> fw = mRegisteredFragments.get(position);
    // Fragment fragment = (fw != null) ? fw.get() : null;
    //
    // if (fragment == null) {
    // switch (position) {
    // case LIST_DETAILS_FRAGMENT:
    // fragment = new ListDetailsFragment();
    // break;
    // case ADD_DETAL_FRAGMENT:
    // fragment = new AddDetailFragment();
    // break;
    // default:
    // fragment = new ListDetailsFragment();
    // break;
    // }
    // }
    //
    // return fragment;
    // }
    //
    // @Override
    // public int getCount() {
    // return FRAGMENTS;
    // }
    //
    // }

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

    // class UploadReceiptCallbackHandler extends
    // CloudCallbackHandler<CloudEntity> {
    // private Receipt mReceipt;
    // private List<Detail> mDetails;
    //
    // public UploadReceiptCallbackHandler(Receipt receipt) {
    // mReceipt = receipt;
    // }
    //
    // @Override
    // public void onComplete(CloudEntity result) {
    // mReceipt.setUpdated(true);
    // List<Receipt> receips = new ArrayList<Receipt>();
    // receips.add(mReceipt);
    // mDS.updateReceipts(receips);
    //
    // BackendDataAccess.uploadDetails(mDetails, getApplicationContext(),
    // getCloudBackend());
    // }
    //
    // }
}
