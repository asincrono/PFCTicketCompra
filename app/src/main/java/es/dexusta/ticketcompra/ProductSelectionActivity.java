package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendFragmentActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.control.CategorySelectionCallback;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.control.SubcategorySelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.view.NoSwipeViewPager;

public class ProductSelectionActivity extends CloudBackendFragmentActivity implements
        CategorySelectionCallback, SubcategorySelectionCallback, ProductSelectionCallback,
        AddProductCallback {
    private static final String              TAG                         = "ProductSelectionActivity";
    private static final boolean             DEBUG                       = true;

    private static final int                 SELECT_CATEGORY_FRAGMENT    = 0;
    private static final int                 SELECT_SUBCATEGORY_FRAGMENT = 1;
    private static final int                 SELECT_PRODUCT_FRAGMENT     = 2;

    private int                              mCurrentCategoryId;
    private int                              mCurrentSubacategoryId;
    private int                              mCurrentProductId;
    private int                              mCurrentFragment;

    private Category                         mCurrentCategory;
    private Subcategory                      mCurrentSubcategory;

    private DataSource                       mDS;
    private DataAccessCallbacks<Category>    mCategoryListener;
    private DataAccessCallbacks<Subcategory> mSubcategoryListener;
    private DataAccessCallbacks<Product>     mProductListener;

    private boolean                          mPaused;

    private NoSwipeViewPager                 mViewPager;
    private ProductSelectionPagerAdapter     mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TESTING HIDE/SHOW ACTION BAR.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt(Keys.KEY_CURRENT_FRAGMENT,
                    SELECT_CATEGORY_FRAGMENT);
            mCurrentCategoryId = savedInstanceState.getInt(Keys.KEY_CURRENT_CATEGORY);
            mCurrentSubacategoryId = savedInstanceState.getInt(Keys.KEY_CURRENT_SUBCATEGORY);
            mCurrentProductId = savedInstanceState.getInt(Keys.KEY_CURRENT_PRODUCT);
        }

        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.pager_activity);

        mViewPager = (NoSwipeViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ProductSelectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mCategoryListener = new DataAccessCallbacks<Category>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Category> results) {
                // setProgressBarIndeterminateVisibility(false);
                mViewPager.setCurrentItem(SELECT_CATEGORY_FRAGMENT);
                CategorySelectionFragment fragment = (CategorySelectionFragment) mPagerAdapter
                        .getItem(SELECT_CATEGORY_FRAGMENT);

                fragment.setList(results);
                // fragment.getListView().setSelectionFromTop(mCurrentCategory,
                // 0);

            }

            @Override
            public void onDataProcessed(int processed, List<Category> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mSubcategoryListener = new DataAccessCallbacks<Subcategory>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                // setProgressBarIndeterminateVisibility(false);
                mViewPager.setCurrentItem(SELECT_SUBCATEGORY_FRAGMENT);
                SubcategorySelectionFragment fragment = (SubcategorySelectionFragment) mPagerAdapter
                        .getItem(SELECT_SUBCATEGORY_FRAGMENT);

                fragment.setList(results);
                fragment.getListView().setSelectionFromTop(mCurrentSubacategoryId, 0);

            }

            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mProductListener = new DataAccessCallbacks<Product>() {
            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                setProgressBarIndeterminateVisibility(false);

                mViewPager.setCurrentItem(SELECT_PRODUCT_FRAGMENT);
                ProductSelectionFragment fragment = (ProductSelectionFragment) mPagerAdapter
                        .getItem(SELECT_PRODUCT_FRAGMENT);
                fragment.setList(results);
                fragment.getListView().setSelectionFromTop(mCurrentProductId, 0);

            }

            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                    boolean result) {
                // Try to insert in the datastore.
                if (result) {
                   if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                       BackendDataAccess.uploadProduct(dataList.get(0), getApplicationContext(), getCloudBackend());
                       if (DEBUG) Log.d(TAG, "Product inserted :" + dataList.get(0));
                       Toast.makeText(ProductSelectionActivity.this, "Product inserted", Toast.LENGTH_SHORT).show();
                       mDS.getProductsBy(mCurrentSubcategory);
                   }
                }

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());

        mDS.setCategoryCallback(mCategoryListener);
        mDS.setSubcategoryCallback(mSubcategoryListener);
        mDS.setProductCallback(mProductListener);

        setProgressBarIndeterminateVisibility(true);
        mDS.listCategories();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Keys.KEY_CURRENT_FRAGMENT, mCurrentFragment);
        outState.putInt(Keys.KEY_CURRENT_CATEGORY, mCurrentCategoryId);
        outState.putInt(Keys.KEY_CURRENT_SUBCATEGORY, mCurrentSubacategoryId);
        outState.putInt(Keys.KEY_CURRENT_PRODUCT, mCurrentProductId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            mPaused = false;
            mDS.setCategoryCallback(mCategoryListener);
            mDS.setSubcategoryCallback(mSubcategoryListener);
            mDS.setProductCallback(mProductListener);
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        mDS.setCategoryCallback(null);
        mDS.setSubcategoryCallback(null);
        mDS.setProductCallback(null);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        int currentFragment = mViewPager.getCurrentItem();
        if (currentFragment > 0) {
            mViewPager.setCurrentItem(currentFragment - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCategorySelected(Category category, int position) {
        setProgressBarIndeterminateVisibility(true);
        mCurrentCategoryId = position;
        mCurrentCategory = category;
        mDS.getSubcategoriesBy(category);
    }

    @Override
    public void onCancelCategorySelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSubcategorySelected(Subcategory subcategory, int position) {
        setProgressBarIndeterminateVisibility(true);
        mCurrentSubacategoryId = position;
        mCurrentSubcategory = subcategory;
        mDS.getProductsBy(subcategory);
    }

    @Override
    public void onCancelSubcategorySelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProductSelected(Product product, int position) {
        mCurrentProductId = position;
        // Siguiente actividad.
        Intent intent = new Intent();
        intent.putExtra(Keys.KEY_PRODUCT, product);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCancelProductSelection() {
        // TODO Auto-generated method stub

    }

    public void onClickNewProduct(View v) {

    }

    @Override
    public void onAddProduct(Product product) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCancelAddProduct() {
        // TODO Auto-generated method stub

    }

    @Override
    public Category getCategory() {
        return mCurrentCategory;
    }

    @Override
    public Subcategory getSucategory() {
        return mCurrentSubcategory;
    }

    private class ProductSelectionPagerAdapter extends FragmentPagerAdapter {
        private static final int                     FRAGMENTS            = 3;

        private SparseArray<WeakReference<Fragment>> mRegisteredFragments = new SparseArray<WeakReference<Fragment>>();

        public ProductSelectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WeakReference<Fragment> fw = mRegisteredFragments.get(position);

            Fragment fragment = (fw != null) ? fw.get() : null;

            if (fragment == null) {
                switch (position) {
                case SELECT_CATEGORY_FRAGMENT:
                    fragment = CategorySelectionFragment.newInstance(ProductSelectionActivity.this);
                    break;
                case SELECT_SUBCATEGORY_FRAGMENT:
                    fragment = SubcategorySelectionFragment
                            .newInstance(ProductSelectionActivity.this);
                    break;
                case SELECT_PRODUCT_FRAGMENT:
                    fragment = ProductSelectionFragment.newInstance(ProductSelectionActivity.this);
                    break;
                }
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

        // public Fragment getFragment(int position) {
        // WeakReference<Fragment> fw = mRegisteredFragments.get(position);
        // return (fw != null) ? fw.get() : null;
        // }

        @Override
        public int getCount() {
            return FRAGMENTS;
        }

    }
}
