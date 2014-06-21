package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.control.CategoryAdapter;
import es.dexusta.ticketcompra.control.CategorySelectionCallback;
import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.control.SubcategoryAdapter;
import es.dexusta.ticketcompra.control.SubcategorySelectionCallback;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.localdataaccess.DataAccessCallback;
import es.dexusta.ticketcompra.localdataaccess.LocalDataSource;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public class ProductSelectionActivityV2 extends CloudBackendActivity implements
        CategorySelectionCallback, SubcategorySelectionCallback, ProductSelectionCallback,
        AddProductCallback {
    private static final String TAG = "ProductSelectionActivityV2";

    private static final String TAG_SELECT_CATEGORY_FRAGMENT   = "select_category";
    private static final String TAG_SELECT_SUBCATEGORY_FRAGMET = "select_subcategory";
    private static final String TAG_SELECT_PRODUCT_FRAGMENT    = "select_product";
    private static final String TAG_ADD_PRODUCT_FRAGMENT       = "add_product";
    private static final String TAG_STATE_FRAGMENT             = "state_fragment";

    private StateFragment mStateFragment;

    private Category mSelectedCategory;
    private int      mSelectedCategoryPosition;

    private Subcategory mSelectedSubcategory;
    private int         mSelectedSubcategoryPosition;

    private Product mSelectedProduct;
    private int     mSelectedProductPosition;

    private List<Category>    mCategories;
    private List<Subcategory> mSubcategories;
    private List<Product>     mProducts;

//    private DataAccessCallbacks<Category>    mCategoryDACallbacks;
//    private DataAccessCallbacks<Subcategory> mSubcategoryDACallbacks;
//    private DataAccessCallbacks<Product>     mProductDACallbacks;

    private DataAccessCallback<Category>    mReadCategoryCallback;
    private DataAccessCallback<Subcategory> mReadSubcategoryCallback;
    private DataAccessCallback<Product>     mReadProductCallback;
    private DataAccessCallback<Product>     mInsertProductCallback;

    private CategoryAdapter    mCategoryAdapter;
    private SubcategoryAdapter mSubcategoryAdapter;
    private ProductAdapter     mProductAdapter;

//    private DataSource      mDS;
    private LocalDataSource mLDS;


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

            mProducts = new ArrayList<Product>();

//            mCategoryDACallbacks = new DataAccessCallbacks<Category>() {
//                @Override
//                public void onDataProcessed(int processed, List<Category> dataList,
//                                            Types.Operation operation, boolean result) {
//
//                }
//
//                @Override
//                public void onDataReceived(List<Category> results) {
//                    if (BuildConfig.DEBUG && (results == null))
//                        throw new AssertionError("Categories list shouldn't be empty");
//                    mCategories = results;
//                    mStateFragment.put(Keys.KEY_CATEGORY_LIST, mCategories);
//                    mCategoryAdapter.swapList(mCategories);
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            };

            mReadCategoryCallback = new DataAccessCallback<Category>() {
                @Override
                public void onComplete(List<Category> results, boolean result) {
                    if (BuildConfig.DEBUG && (results == null))
                        throw new AssertionError("Categories list shouldn't be empty");
                    mCategories = results;
                    mStateFragment.put(Keys.KEY_CATEGORY_LIST, mCategories);
                    mCategoryAdapter.swapList(mCategories);
                }
            };

//            mSubcategoryDACallbacks = new DataAccessCallbacks<Subcategory>() {
//                @Override
//                public void onDataProcessed(int processed, List<Subcategory> dataList,
//                                            Types.Operation operation, boolean result) {
//
//                }
//
//                @Override
//                public void onDataReceived(List<Subcategory> results) {
//                    if (BuildConfig.DEBUG && (results == null))
//                        throw new AssertionError("Subcategories list shouldn't be empty");
//                    mSubcategories = results;
//                    mStateFragment.put(Keys.KEY_SUBCATEGORY_LIST, mSubcategories);
//                    mSubcategoryAdapter.swapList(mSubcategories);
//                    // showSubcategorySelection();
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            };

            mReadSubcategoryCallback = new DataAccessCallback<Subcategory>() {
                @Override
                public void onComplete(List<Subcategory> results, boolean result) {
                    if (BuildConfig.DEBUG && (results == null))
                        throw new AssertionError("Subcategories list shouldn't be empty");
                    mSubcategories = results;
                    mStateFragment.put(Keys.KEY_SUBCATEGORY_LIST, mSubcategories);
                    mSubcategoryAdapter.swapList(mSubcategories);
                    // showSubcategorySelection();
                }
            };

//            mProductDACallbacks = new DataAccessCallbacks<Product>() {
//                @Override
//                public void onDataProcessed(int processed, List<Product> dataList,
//                                            Types.Operation operation, boolean result) {
//                    if (result) {
////                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
////                        BackendDataAccess.uploadProduct(dataList.get(0),
////                                getApplicationContext(), getCloudBackend(), null);
////                        if (BuildConfig.DEBUG)
////                            Log.d(TAG, "Tried to upload new product.");
////                    }
//
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Tried to upload new product.");
//
//                        mProducts.add(dataList.get(0));
//
//                        BackendDataAccessV2.uploadProducts(dataList,
//                                getApplicationContext(), getCloudBackend());
//
//
//                        // TODO: Comprobar los siguiente:
//                        // Aquí no se debería indicar que se lanza nada. Una vez insertado el producto
//                        // se retira AddProductFragment de la pila onBackPressed();
//                        refreshProductList();
//                        onBackPressed();
//
//                        //showProductSelection();
//                    }
//                }
//
//                @Override
//                public void onDataReceived(List<Product> results) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "Loaded " + results.size() + " products.");
//                    mProducts = results;
//                    mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);
//                    mProductAdapter.swapList(mProducts);
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            };

            mInsertProductCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (result) {
//                    if (BackendDataAccess.hasConnectivity(getApplicationContext())) {
//                        BackendDataAccess.uploadProduct(dataList.get(0),
//                                getApplicationContext(), getCloudBackend(), null);
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Tried to upload new product.");
//                    }

                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Tried to upload new product.");

                        if (mProducts == null) {
                            mProducts = new ArrayList<Product>(1);
                        }

                        mProducts.add(results.get(0));

                        BackendDataAccessV2.uploadProducts(results,
                                getApplicationContext(), getCloudBackend());


                        // TODO: Comprobar los siguiente:
                        // Aquí no se debería indicar que se lanza nada. Una vez insertado el producto
                        // se retira AddProductFragment de la pila onBackPressed();
                        refreshProductList();
                        onBackPressed();

                        //showProductSelection();
                    }
                }
            };

            mReadProductCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (results != null) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Loaded " + results.size() + " products.");
                    }
                    mProducts = results;
                    mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);
                    mProductAdapter.swapList(mProducts);
                }
            };

            mStateFragment.put(Keys.KEY_PRODUCT_LIST, mProducts);

        } else {
            mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

            mCategories = (List<Category>) mStateFragment.get(Keys.KEY_CATEGORY_LIST);
            mSubcategories = (List<Subcategory>) mStateFragment.get(Keys.KEY_SUBCATEGORY_LIST);
            mProducts = (List<Product>) mStateFragment.get(Keys.KEY_PRODUCT_LIST);

            mSelectedCategory = (Category) mStateFragment
                    .get(Keys.KEY_SELECTED_CATEGORY);
            mSelectedCategoryPosition = (Integer) mStateFragment
                    .get(Keys.KEY_SELECTED_CATEGORY_POSITION);
            mSelectedSubcategory = (Subcategory) mStateFragment
                    .get(Keys.KEY_SELECTED_SUBCATEGORY);
            mSelectedSubcategoryPosition = (Integer) mStateFragment
                    .get(Keys.KEY_SELECTED_SUBCATEGORY_POSITION);


//            mCategoryDACallbacks = (DataAccessCallbacks<Category>) mStateFragment
//                    .get(Keys.KEY_CATEGORY_DA_CALLBACKS);
//            mSubcategoryDACallbacks = (DataAccessCallbacks<Subcategory>) mStateFragment
//                    .get(Keys.KEY_SUBCATEGORY_DA_CALLBACKS);
//            mProductDACallbacks = (DataAccessCallbacks<Product>) mStateFragment
//                    .get(Keys.KEY_PRODUCT_DA_CALLBACKS);

            mReadCategoryCallback = (DataAccessCallback<Category>) mStateFragment.get(Keys.KEY_CATEGORY_DA_CALLBACKS);
            mReadSubcategoryCallback = (DataAccessCallback<Subcategory>) mStateFragment.get(Keys.KEY_SUBCATEGORY_DA_CALLBACKS);
            mReadProductCallback = (DataAccessCallback<Product>) mStateFragment.get(Keys.KEY_PRODUCT_DA_CALLBACKS);

            mCategoryAdapter.swapList(mCategories);
            mSubcategoryAdapter.swapList(mSubcategories);
            mProductAdapter.swapList(mProducts);
        }


//        mDS = DataSource.getInstance(getApplicationContext());
//
//        mDS.setCategoryCallback(mCategoryDACallbacks);
//        mDS.listCategories();

        mLDS = LocalDataSource.getInstance(getApplicationContext());
        mLDS.listCategories(mReadCategoryCallback);
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

        Fragment fragment = manager.findFragmentByTag(TAG_SELECT_SUBCATEGORY_FRAGMET);

        if (fragment == null) {
            fragment = new SubcategorySelectionFragment();
            transaction.replace(android.R.id.content, fragment, TAG_SELECT_SUBCATEGORY_FRAGMET);
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

//        mDS.setProductCallback(mProductDACallbacks);
//        mDS.insertProducts(list);

        mLDS.insertProducts(list, mInsertProductCallback);
    }

    private void refreshProductList() {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Refreshing the product list (selected subcategory is: \"" +
                    mSelectedSubcategory + "\".");
        if (mSelectedSubcategory != null) {
//            mDS.setProductCallback(mProductDACallbacks);
//            mDS.getProductsBy(mSelectedSubcategory);

            mLDS.getProductsBy(mSelectedSubcategory, mReadProductCallback);
        }
    }

    @Override
    public void onCancelAddProduct() {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onCancelAddProduct.");
        // if user cancel add product we assume he don't want to add any product, as in that case
        // she will just hit back.
        finish();
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

        mStateFragment.put(Keys.KEY_SELECTED_CATEGORY, mSelectedCategory);
        mStateFragment.put(Keys.KEY_SELECTED_CATEGORY_POSITION, mSelectedCategoryPosition);

//        mDS.setSubcategoryCallback(mSubcategoryDACallbacks);
//        mDS.getSubcategoriesBy(mSelectedCategory);

        mLDS.getSubcategoriesBy(mSelectedCategory, mReadSubcategoryCallback);
        showSubcategorySelection();
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
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Subcategory selected.");
        mSelectedSubcategory = subcategory;
        mSelectedSubcategoryPosition = position;

        mStateFragment.put(Keys.KEY_SELECTED_SUBCATEGORY, mSelectedSubcategory);
        mStateFragment.put(Keys.KEY_SELECTED_SUBCATEGORY_POSITION, mSelectedCategoryPosition);

//        mDS.setProductCallback(mProductDACallbacks);
//        mDS.getProductsBy(mSelectedSubcategory);

        mLDS.getProductsBy(mSelectedSubcategory, mReadProductCallback);
        showProductSelection();
    }

    @Override
    public SubcategoryAdapter getSubcategoryAdapter() {
        return mSubcategoryAdapter;
    }

    @Override
    public ProductAdapter getProductAdapter() {
        return mProductAdapter;
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Keys.KEY_SELECTED_PRODUCT, mSelectedProduct);
        setResult(RESULT_OK, resultIntent);
        // TODO RETURN SELECTED PRODUCT TO THE ORIGIN ACTIVITY.
        finish();
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
        ActionBarController.setAcceptCancel(getActionBar(), onClickAccept, onClickCancel);
    }

    @Override
    public void hideAcceptCancelActionBar() {
        ActionBarController.setDisplayDefault(getActionBar());
    }

    @Override
    public boolean isABAvaliable() {
        return getActionBar() != null;
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


    @Override
    public void setActionBarTitle(String title) {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }
}
