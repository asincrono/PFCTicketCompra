package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.cloud.backend.android.CloudBackendActivity;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public class AddProductActivity extends CloudBackendActivity {
    private static final String  TAG   = "AddProductActivity";
    private static final boolean DEBUG = true;

    private static final String KEY_CAT_POSITION    = "category_position";
    private static final String KEY_SUBCAT_POSITION = "subcategory_position";

    private EditText mEdtName;
    private EditText mEdtDescription;
    private Spinner  mSpnCategory;
    private Spinner  mSpnSubcategory;
    private int mSpnCatPosition    = Spinner.INVALID_POSITION;
    private int mSpnSubcatPosition = Spinner.INVALID_POSITION;

    private boolean mPaused;

    private DataSource                       mDS;
    private DataAccessCallbacks<Category>    mCategoryListener;
    private DataAccessCallbacks<Subcategory> mSubcategoryListener;
    private DataAccessCallbacks<Product>     mProductListener;

    private Subcategory mSubcategory;
    private Product     mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSpnCatPosition = savedInstanceState.getInt(KEY_CAT_POSITION, Spinner.INVALID_POSITION);
            mSpnSubcatPosition = savedInstanceState.getInt(KEY_SUBCAT_POSITION,
                    Spinner.INVALID_POSITION);
        }

        setContentView(R.layout.add_product_activity);

        // final ActionBar actionBar = getActionBar();
        //
        // LayoutInflater.from(actionBar.getThemedContext());
        //
        // final View actionBarCustomView =
        // inflater.inflate(R.layout.actionbar_cancel_accept, null);
        //
        // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
        // ActionBar.DISPLAY_SHOW_CUSTOM
        // | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //
        // actionBar.setCustomView(actionBarCustomView, new
        // ActionBar.LayoutParams(
        // ViewGroup.LayoutParams.MATCH_PARENT,
        // ViewGroup.LayoutParams.MATCH_PARENT));

        showAcceptCancelActionBar(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveData();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSpnCategory = (Spinner) findViewById(R.id.spn_category);
        mSpnSubcategory = (Spinner) findViewById(R.id.spn_subcategory);

        mSpnCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDS.getSubcategoriesBy((Category) mSpnCategory.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        mSpnSubcategory.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubcategory = (Subcategory) mSpnSubcategory.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        mEdtName = (EditText) findViewById(R.id.edt_product_name);
        mEdtDescription = (EditText) findViewById(R.id.edt_description);

        mCategoryListener = new DataAccessCallbacks<Category>() {

            @Override
            public void onDataProcessed(int processed, List<Category> dataList,
                                        Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Category> results) {

                ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(
                        AddProductActivity.this, android.R.layout.simple_spinner_item, results);
                categoryAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpnCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
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
                ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<Subcategory>(
                        AddProductActivity.this, android.R.layout.simple_spinner_item, results);
                subcategoryAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpnSubcategory.setAdapter(subcategoryAdapter);
                if (mSpnCatPosition != Spinner.INVALID_POSITION) {
                    mSpnCategory.setSelection(mSpnCatPosition);
                }
                if (mSpnSubcatPosition != Spinner.INVALID_POSITION) {
                    mSpnSubcategory.setSelection(mSpnSubcatPosition);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList,
                                        Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mProductListener = new DataAccessCallbacks<Product>() {

            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                                        boolean result) {
                if (result) {
//                    BackendDataAccess.uploadProducts(dataList, getApplicationContext(),
//                            getCloudBackend());
                    BackendDataAccessV2.uploadProducts(dataList, getApplicationContext(), getCloudBackend());

                    mDS.addToProductSubcategoryIdMap(dataList.get(0));
                } else {
                    if (DEBUG) Log.wtf(TAG, "La jodimos que no insertó el producto.");
                }

            }

            @Override
            public void onDataReceived(List<Product> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setCategoryCallback(mCategoryListener);
        mDS.setSubcategoryCallback(mSubcategoryListener);
        mDS.setProductCallback(mProductListener);

        mDS.listCategories();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int catPosition = mSpnCategory.getSelectedItemPosition();
        int subcatPosition = mSpnSubcategory.getSelectedItemPosition();
        outState.putInt(KEY_CAT_POSITION, catPosition);
        outState.putInt(KEY_SUBCAT_POSITION, subcatPosition);
    }

    private void showAcceptCancelActionBar(OnClickListener onClickAccept,
                                           OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();

        LayoutInflater inflater = LayoutInflater.from(getActionBar().getThemedContext());

        final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null);

        actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
        actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);

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

    private boolean validateForm() {
        return (mEdtName.getText().length() > 0);
    }

    private Product readForm() {
        Product product = null;
        if (validateForm()) {
            product = new Product();
            product.setName(mEdtName.getText().toString());
            product.setSubcategoryId(mSpnSubcategory.getSelectedItemId());
            String description = null;

        }
        return product;
    }

    private void saveData() {
        if (validateForm()) {
            Product product = new Product();

            long subcategoryId = ((Subcategory) mSpnSubcategory.getSelectedItem()).getId();
            product.setSubcategoryId(subcategoryId);

            product.setName(mEdtName.getText().toString());
            product.setDescription(mEdtDescription.getText().toString());

            List<Product> listProduct = new ArrayList<Product>();
            listProduct.add(product);

            mDS.insertProducts(listProduct);
            finish();
        }
    }
}
