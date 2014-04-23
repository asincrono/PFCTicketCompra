package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.control.CategoryAdapter;
import es.dexusta.ticketcompra.control.CategorySelectionCallback;
import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.control.SubcategoryAdapter;
import es.dexusta.ticketcompra.control.SubcategorySelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public class ProductSelectionActivity extends CloudBackendActivity implements
        CategorySelectionCallback, SubcategorySelectionCallback, ProductSelectionCallback,
        AddProductCallback {
    private static final String  TAG   = "ProductSelectionActivity";
    private static final boolean DEBUG = true;

    private static final String TAG_STATE_FRAGMENT              = "state_fragment";
    private static final String TAG_SELECT_CATEGORY_FRAGMENT    = "select_category_fragment";
    private static final String TAG_SELECT_SUBCATEGORY_FRAGMENT = "select_subcategory_fragment";
    private static final String TAG_SELECT_PRODUCT_FRAGMENT     = "select_product_fragment";

    private int mSelectedCategoryPosition;
    private int mSelectedSubcategoryPosition;
    private int mSelectedProductPosition;
    private int mCurrentFragment;

    private StateFragment mStateFragment;

    private List<Category>    mCategories;
    private List<Subcategory> mSubcategories;
    private List<Product>     mProducts;

    private CategoryAdapter    mCategoryAdapter;
    private SubcategoryAdapter mSubcategoryAdapter;
    private ProductAdapter     mProductAdapter;

    private Category    mSelectedCategory;
    private Subcategory mSelectedSubcategory;

    private DataSource mDS;

    private boolean mPaused;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TESTING HIDE/SHOW ACTION BAR.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        FragmentManager manager = getFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = manager.beginTransaction();

            Fragment fragment = new StateFragment();
            transaction.add(fragment, TAG_STATE_FRAGMENT);
            transaction.commit();

            fragment = new SelectCategoryFragment();
            transaction = manager.beginTransaction();
            transaction.add(android.R.id.content, fragment, TAG_SELECT_CATEGORY_FRAGMENT);

            transaction.commit();
        } else {
            mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);
            mSelectedCategory = (Category) mStateFragment.get(Keys.KEY_CATEGORY);
            mSelectedSubcategory = (Subcategory) mStateFragment.get(Keys.KEY_SUBCATEGORY);

            mCategories = (List<Category>) mStateFragment.get(Keys.KEY_CATEGORY_LIST);
            mSubcategories = (List<Subcategory>) mStateFragment.get(Keys.KEY_SUBCATEGORY_LIST);
            mProducts = (List<Product>) mStateFragment.get(Keys.KEY_PRODUCT_LIST);

            mSelectedCategoryPosition = (Integer) mStateFragment.get(Keys.KEY_CURRENT_CATEGORY);
            mSelectedSubcategoryPosition = (Integer) mStateFragment.get(Keys.KEY_CURRENT_SUBCATEGORY);
            mSelectedProductPosition  = (Integer) mStateFragment.get(Keys.KEY_CURRENT_PRODUCT);
        }

        mCategoryAdapter = new CategoryAdapter(this, mCategories);
        mSubcategoryAdapter = new SubcategoryAdapter(this, mSubcategories);
        mProductAdapter = new ProductAdapter(this, mProducts);

        mDS = DataSource.getInstance(getApplicationContext());

        mDS.setCategoryCallback(new DataAccessCallbacks<Category>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Category> results) {
                // setProgressBarIndeterminateVisibility(false);
                mCategories = results;
                mStateFragment.put(Keys.KEY_CATEGORY_LIST, mCategories);
                mCategoryAdapter.swapList(mCategories);
            }

            @Override
            public void onDataProcessed(int processed, List<Category> dataList,
                                        Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setSubcategoryCallback(new DataAccessCallbacks<Subcategory>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                // setProgressBarIndeterminateVisibility(false);
                mSubcategories = results;
                mStateFragment.put(Keys.KEY_SUBCATEGORY_LIST, mSubcategories);
                mSubcategoryAdapter.swapList(mSubcategories);
            }

            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList,
                                        Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                setProgressBarIndeterminateVisibility(false);

                mProducts = results;
                mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);
                mProductAdapter.swapList(mProducts);
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
                        mDS.getProductsBy(mSelectedSubcategory);
                    }
                }
            }
        });

        setProgressBarIndeterminateVisibility(true);
        mDS.listCategories();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStateFragment.put(Keys.KEY_CURRENT_CATEGORY, mSelectedCategoryPosition);
        mStateFragment.put(Keys.KEY_CURRENT_SUBCATEGORY, mSelectedSubcategoryPosition);
        mStateFragment.put(Keys.KEY_CURRENT_PRODUCT, mSelectedProductPosition);
    }

    private void showSubcategorySelection() {

    }

    private void showProductSelection() {

    }

    private void showAddProduct() {

    }

    @Override
    public void onCategorySelected(Category category, int position) {
        setProgressBarIndeterminateVisibility(true);
        mSelectedCategoryPosition = position;
        mSelectedCategory = category;
        mDS.getSubcategoriesBy(category);
    }

    @Override
    public CategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }

    @Override
    public int getSelectedCategoryPosition() {
        return mSelectedCategoryPosition;
    }

    @Override
    public void onCancelCategorySelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSubcategorySelected(Subcategory subcategory, int position) {
        setProgressBarIndeterminateVisibility(true);
        mSelectedSubcategoryPosition = position;
        mSelectedSubcategory = subcategory;
        mDS.getProductsBy(subcategory);
    }

    @Override
    public SubcategoryAdapter getSubcategoryAdapter() {
        return mSubcategoryAdapter;
    }

    @Override
    public int getSelectedSubcategoryPostion() {
        return mSelectedSubcategoryPosition;
    }

    @Override
    public void onCancelSubcategorySelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProductSelected(Product product, int position) {
        mSelectedProductPosition = position;

        // Go to next activity.
        Intent intent = new Intent();
        intent.putExtra(Keys.KEY_PRODUCT, product);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public ProductAdapter getProductAdapter() {
        return mProductAdapter;
    }

    @Override
    public int getSelectedProductPosition() {
        return mSelectedProductPosition;
    }

    @Override
    public void onClickAddProduct() {

    }

    @Override
    public void onCancelProductSelection() {
        onBackPressed();

    }

    public void onClickNewProduct(View v) {

    }

    @Override
    public void showAcceptCancelActionBar(View.OnClickListener onClickAccept, View.OnClickListener onClickCancel) {

    }

    @Override
    public void hideAcceptCancelActionBar() {

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
    public Category getSelectedCategory() {
        return mSelectedCategory;
    }

    @Override
    public Subcategory getSeletedSubcategory() {
        return mSelectedSubcategory;
    }
}
