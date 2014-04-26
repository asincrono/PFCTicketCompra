package es.dexusta.ticketcompra;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.control.CategoryAdapter;
import es.dexusta.ticketcompra.control.CategorySelectionCallback;
import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.control.SubcategoryAdapter;
import es.dexusta.ticketcompra.control.SubcategorySelectionCallback;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public class ProductSelectionActivityV2 extends CloudBackendActivity implements
        CategorySelectionCallback, SubcategorySelectionCallback, ProductSelectionCallback,
        AddProductCallback {
    private static final String  TAG   = "ProductSelectionActivityV2";
    private static final boolean DEBUG = true;

    private static final String TAG_SELECT_CATEGORY_FRAGMENT   = "select_category";
    private static final String TAG_SELECT_SUBCATEGORY_FRAGEMT = "select_subcategory";
    private static final String TAG_SELECT_PRODUCT_FRAGMENT    = "select_product";
    private static final String TAG_ADD_PRODUCT_FRAGMENT       = "add_product";
    private static final String TAG_STATE_FRAGMENT             = "state_fragment";

    private StateFragment mStateFragment;

    private Category mSelectedCategory;
    private int mSelectedCategoryPosition;

    private Subcategory mSelectedSubcategory;
    private int mSelectedSubcategoryPosition;

    private Product mSelectedProduct;
    private int mSelectedProductPosition;

    private List<Category>    mCategories;
    private List<Subcategory> mSubcategories;
    private List<Product>     mProducts;

    private CategoryAdapter    mCategoryAdapter;
    private SubcategoryAdapter mSubcategoryAdapter;
    private ProductAdapter     mProductAdapter;

    private DataSource mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategoryAdapter = new CategoryAdapter(this);
        mSubcategoryAdapter = new SubcategoryAdapter(this);
        mProductAdapter = new ProductAdapter(this);

        FragmentManager manager = getFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            mStateFragment = new StateFragment();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT);

            transaction.add(android.R.id.content, new CategorySelectionFragment(),
                    TAG_SELECT_CATEGORY_FRAGMENT);

            transaction.commit();
        } else {
            mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

            mCategories = (List<Category>) mStateFragment.get(Keys.KEY_CATEGORY_LIST);
            mSubcategories = (List<Subcategory>) mStateFragment.get(Keys.KEY_SUBCATEGORY_LIST);
            mProducts = (List<Product>) mStateFragment.get(Keys.KEY_PRODUCT_LIST);

            mCategoryAdapter.swapList(mCategories);
            mSubcategoryAdapter.swapList(mSubcategories);
            mProductAdapter.swapList(mProducts);
        }



        mDS = DataSource.getInstance(getApplicationContext());

        mDS.setCategoryCallback(new DataAccessCallbacks<Category>() {
            @Override
            public void onDataProcessed(int processed, List<Category> dataList,
                                        Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Category> results) {
                if (BuildConfig.DEBUG && (results == null))
                    throw new AssertionError("Categories list shouldn't be empty");
                mCategories = results;
                mStateFragment.put(Keys.KEY_CATEGORY_LIST, mCategories);
                mCategoryAdapter.swapList(mCategories);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });
        mDS.setSubcategoryCallback(new DataAccessCallbacks<Subcategory>() {
            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList,
                                        Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                if (BuildConfig.DEBUG && (results == null))
                    throw new AssertionError("Subcategories list shouldn't be empty");
                mSubcategories = results;
                mStateFragment.put(Keys.KEY_SUBCATEGORY_LIST, mSubcategories);
                mSubcategoryAdapter.swapList(mSubcategories);
                showSubcategorySelection();
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });
        mDS.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList,
                                        Types.Operation operation, boolean result) {
                if (result) {
                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
                        BackendDataAccess.uploadProduct(dataList.get(0),
                                getApplicationContext(), getCloudBackend());
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Tried to upload new product.");
                    }
                    showProductSelection();
                }
            }

            @Override
            public void onDataReceived(List<Product> results) {
                mProducts = results;
                mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);
                mProductAdapter.swapList(mProducts);
                showProductSelection();
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        mDS.listCategories();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStateFragment.put(Keys.KEY_CATEGORY_LIST, mCategories);
        mStateFragment.put(Keys.KEY_SUBCATEGORY_LIST, mSubcategories);
        mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);
    }

    private void showSubcategorySelection() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        applyTransactionAnimator(transaction);

        Fragment fragment = manager.findFragmentByTag(TAG_SELECT_SUBCATEGORY_FRAGEMT);

        if (fragment == null) {
            fragment = new SubcategorySelectionFragment();
            transaction.replace(android.R.id.content, fragment, TAG_SELECT_SUBCATEGORY_FRAGEMT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showProductSelection() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        applyTransactionAnimator(transaction);

        Fragment fragment = manager.findFragmentByTag(TAG_SELECT_PRODUCT_FRAGMENT);

        if (fragment == null) {
            fragment = new ProductSelectionFragment();
            transaction.replace(android.R.id.content, fragment, TAG_SELECT_PRODUCT_FRAGMENT);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showAddProduct() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        applyTransactionAnimator(transaction);

        Fragment fragment = manager.findFragmentByTag(TAG_ADD_PRODUCT_FRAGMENT);

        if (fragment == null) {
            fragment = new AddProductFragment();
            transaction.replace(android.R.id.content, fragment, TAG_ADD_PRODUCT_FRAGMENT);
        } else {
            transaction.add(android.R.id.content, fragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void applyTransactionAnimator(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right);
    }

    @Override
    protected void onPostCreate() {
        // TODO Auto-generated method stub
        super.onPostCreate();
    }

    @Override
    public void onAddProduct(Product product) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onAddProduct.");
        List<Product> list = new ArrayList<Product>();
        list.add(product);
        mDS.insertProducts(list);
    }

    @Override
    public void onCancelAddProduct() {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onCancelAddProduct.");
        onBackPressed();
    }

    @Override
    public Category getSelectedCategory() {
        return mSelectedCategory;
    }

    @Override
    public Subcategory getSeletedSubcategory() {
        return mSelectedSubcategory;
    }

    @Override
    public void onCategorySelected(Category category, int position) {
        mSelectedCategory = category;
        mSelectedCategoryPosition = position;
        mDS.getSubcategoriesBy(mSelectedCategory);
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
        onBackPressed();
    }

    @Override
    public void onSubcategorySelected(Subcategory subcategory, int position) {
        mSelectedSubcategory = subcategory;
        mSelectedSubcategoryPosition = position;
        mDS.getProductsBy(mSelectedSubcategory);
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
        onBackPressed();
    }

    @Override
    public void onProductSelected(Product product, int position) {
        mSelectedProduct = product;
        mSelectedProductPosition = position;
        // TODO RETURN SELECTED PRODUCT TO THE ORIGIN ACTIVITY.
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
        showAddProduct();
    }

    @Override
    public void onCancelProductSelection() {
        onBackPressed();
    }

    @Override
    public void showAcceptCancelActionBar(OnClickListener onClickAccept,
                                          OnClickListener onClickCancel) {

    }

    @Override
    public void hideAcceptCancelActionBar() {

    }

    @Override
    public boolean isABAvaliable() {
        return getActionBar() != null;
    }
}
