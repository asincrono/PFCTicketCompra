package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import es.dexusta.ticketcompra.ListDetailsFragment.ListDetailsCallback;
import es.dexusta.ticketcompra.ListReceiptsFragment.ListReceiptsCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.view.NoSwipeViewPager;

public class ListReceiptsActivity extends FragmentActivity implements ListDetailsCallback,
        ListReceiptsCallback {
    private static final boolean      DEBUG                  = true;
    private static final String       TAG                    = "ListReceiptsActivity";

    private static final int          LIST_RECEIPTS_FRAGMENT = 0;
    private static final int          LIST_DETAILS_FRAGMENT  = 1;

    private static final String       STATE_FRAGMENT         = "state_fragment";

    private int                       mCurrentFragment;
    private Receipt                   mSelectedReceipt;

    private ViewPager                 mViewPager;
    private ListReceiptsPagerAdapter  mPagerAdapter;

    private ListReceiptsStateFragment mStateFragment;

    private DataSource                mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt(Keys.KEY_CURRENT_FRAGMENT,
                    LIST_RECEIPTS_FRAGMENT);
            mSelectedReceipt = (Receipt) savedInstanceState.get(Keys.KEY_CURRENT_RECEIPT);
        }

        setContentView(R.layout.pager_activity);

        mViewPager = (NoSwipeViewPager) findViewById(R.id.view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mPagerAdapter = new ListReceiptsPagerAdapter(fragmentManager);
        mViewPager.setAdapter(mPagerAdapter);

        DataAccessCallbacks<Receipt> receiptCallbacks = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                mStateFragment.setReceipts(results);
                mViewPager.setCurrentItem(LIST_RECEIPTS_FRAGMENT);
                ListReceiptsFragment fragment = (ListReceiptsFragment) mPagerAdapter
                        .getFragment(LIST_RECEIPTS_FRAGMENT);
                mCurrentFragment = LIST_RECEIPTS_FRAGMENT;
                fragment.setList(results);
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataAccessCallbacks<Detail> detailCallbacks = new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                mStateFragment.setDetails(mSelectedReceipt, results);
                ListDetailsFragment fragment = (ListDetailsFragment) mPagerAdapter
                        .getFragment(LIST_DETAILS_FRAGMENT);
                mViewPager.setCurrentItem(LIST_DETAILS_FRAGMENT);
                mCurrentFragment = LIST_DETAILS_FRAGMENT;
                fragment.setList(results);

            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());

        mDS.setReceiptCallback(receiptCallbacks);
        mDS.setDetailCallback(detailCallbacks);

        /*
         * If there isn't state fragment (a fragment without UI to hold the DB
         * data) we create one.
         */

        mStateFragment = (ListReceiptsStateFragment) fragmentManager
                .findFragmentByTag(STATE_FRAGMENT);
        if (mStateFragment == null) {
            mStateFragment = new ListReceiptsStateFragment();
            fragmentManager.beginTransaction().add(mStateFragment, STATE_FRAGMENT).commit();
        }

        List<Receipt> receipts = mStateFragment.getReceipts();

        if (receipts == null) {
            mDS.listReceipts();
        } else {
            if (mCurrentFragment == LIST_RECEIPTS_FRAGMENT) {
                ListReceiptsFragment fragment = (ListReceiptsFragment) mPagerAdapter
                        .instantiateItem(mViewPager, mCurrentFragment);
                mViewPager.setCurrentItem(LIST_RECEIPTS_FRAGMENT);
                fragment.setList(receipts);
            } else {
                List<Detail> details = mStateFragment.getDetails(mSelectedReceipt);
                ListReceiptsFragment listRcptFragment = (ListReceiptsFragment) mPagerAdapter
                        .instantiateItem(mViewPager, LIST_RECEIPTS_FRAGMENT);
                listRcptFragment.setList(receipts);
                ListDetailsFragment listDetFragment = (ListDetailsFragment) mPagerAdapter
                        .instantiateItem(mViewPager, LIST_DETAILS_FRAGMENT);
                listDetFragment.setList(details);
            }
        }

        showAcceptCancelActionBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Keys.KEY_CURRENT_FRAGMENT, mViewPager.getCurrentItem());
        outState.putParcelable(Keys.KEY_CURRENT_RECEIPT, mSelectedReceipt);
    }

    @Override
    public void onBackPressed() {
        mCurrentFragment = mCurrentFragment - 1;
        if (mCurrentFragment < 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mCurrentFragment);
        }

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
    public void onListDetailsAccepted(List<Detail> details) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onListDetailsCanceled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiptSelected(Receipt receipt) {
        mSelectedReceipt = receipt;
        List<Detail> details = mStateFragment.getDetails(receipt);
        if (details != null) {
            ListDetailsFragment fragment = (ListDetailsFragment) mPagerAdapter
                    .getFragment(LIST_DETAILS_FRAGMENT);
            mViewPager.setCurrentItem(LIST_DETAILS_FRAGMENT);
            mCurrentFragment = LIST_DETAILS_FRAGMENT;
            fragment.setList(details);
        } else {
            mDS.getDetailsBy(receipt);
        }
    }

    @Override
    public void onCancelReceiptSelection() {
        // TODO Auto-generated method stub

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

    private class ListReceiptsPagerAdapter extends FragmentPagerAdapter {
        private static final int                     FRAGMENTS            = 2;

        private SparseArray<WeakReference<Fragment>> mRegisteredFragments = new SparseArray<WeakReference<Fragment>>();

        public ListReceiptsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
            case LIST_RECEIPTS_FRAGMENT:
                fragment = new ListReceiptsFragment();
                break;
            case LIST_DETAILS_FRAGMENT:
                fragment = new ListDetailsFragment();
                break;
            default:
                fragment = new ListReceiptsFragment();
            }

            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(position);
            Fragment fragment = (fw != null) ? fw.get() : null;
            if (fragment == null) {
                fragment = (Fragment) super.instantiateItem(container, position);
                mRegisteredFragments.put(position, new WeakReference<Fragment>(fragment));
            }
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getFragment(int position) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(position);
            return (fw != null) ? fw.get() : null;
        }

        @Override
        public int getCount() {
            return FRAGMENTS;
        }

    }

}
