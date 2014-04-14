package es.dexusta.ticketcompra;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.google.cloud.backend.android.CloudBackendActivity;

public class ProductSelectionActivityV2 extends CloudBackendActivity {
    private static final String  TAG                            = "ProductSelectionActivityV2";
    private static final boolean DEBUG                          = true;

    private static final String  KEY_CURRENT_FRAGMENT_TAG       = "current_fragment_tag";

    private static final String  SELECT_CATEGORY_FRAGMENT_TAG   = "select_category";
    private static final String  SELECT_SUBCATEGORY_FRAGEMT_TAG = "select_subcategory";
    private static final String  SELECT_PRODUCT_FRAGMENT_TAG    = "select_product";
    private static final String  ADD_PRODUCT_FRAGMENT_TAG       = "add_product";

    private String               mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentFragmentTag;
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAGMENT_TAG,
                    SELECT_CATEGORY_FRAGMENT_TAG);
        }

        FragmentManager manager = getFragmentManager();

        Fragment fragment;
        if (manager.findFragmentByTag(mCurrentFragmentTag) == null) {
            if (mCurrentFragmentTag == SELECT_CATEGORY_FRAGMENT_TAG) {
                fragment = new SelectCategoryFragment();
            } else if (mCurrentFragmentTag == SELECT_SUBCATEGORY_FRAGEMT_TAG) {
                fragment = new SelectSubcategoryFragment();
            } else if (mCurrentFragmentTag == SELECT_PRODUCT_FRAGMENT_TAG) {
                fragment = new SelectProductFragment();
            } else {
                mCurrentFragmentTag = ADD_PRODUCT_FRAGMENT_TAG;
                fragment = new AddProductFragment();
            }
            manager.beginTransaction().add(fragment, SELECT_CATEGORY_FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentFragmentTag = getFragmentManager().findFragmentById(android.R.id.content)
                .getTag();
        outState.putString(KEY_CURRENT_FRAGMENT_TAG, currentFragmentTag);
    }

    @Override
    protected void onPostCreate() {
        // TODO Auto-generated method stub
        super.onPostCreate();
    }
    // Debe permitir elegir categor�a, subcategoria y producto.
    // Si no se encuentra el producto debería de poder añadirlo.

}
