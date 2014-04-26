package es.dexusta.ticketcompra.view;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link SimpleAddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SimpleAddProductFragment extends Fragment {

    private Category    mSelectedCategory;
    private Subcategory mSelectedSubcategory;

    private AddProductCallback mCallback;

    private EditText mEdtProductName;
    private EditText mEdtProductDescription;


    public SimpleAddProductFragment() {
        // Required empty public constructor
    }

    public static SimpleAddProductFragment newInstance(Subcategory selectedSubcategory) {
        SimpleAddProductFragment fragment = new SimpleAddProductFragment();
        fragment.mSelectedSubcategory = selectedSubcategory;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_add_product, container, false);

        mEdtProductName = (EditText) view.findViewById(R.id.edt_product_name);
        mEdtProductDescription = (EditText) view.findViewById(R.id.edt_description);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSelectedCategory = mCallback.getSelectedCategory();
        mSelectedSubcategory = mCallback.getSeletedSubcategory();

        ActionBar ab = getActivity().getActionBar();

        if (BuildConfig.DEBUG && ab == null)
            throw new AssertionError("ActionBar shouldn't be null");

        ActionBarController.setAcceptCancel(ab,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (validateForm()) {
                            Editable text = mEdtProductName.getText();
                            if (BuildConfig.DEBUG && text == null)
                                throw new AssertionError("Product name text shouldn't be null");
                            String pName = text.toString();

                            text = mEdtProductDescription.getText();

                            if (BuildConfig.DEBUG && text == null)
                                throw new AssertionError("Product description text shouldn't be null");
                            String pDescription = text.toString();

                            Product product = new Product();
                            product.setName(pName);
                            product.setDescription(pDescription);
                            product.setSubcategoryId(mSelectedSubcategory.getId());

                            mCallback.onAddProduct(product);
                        }
                    }
                },
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onCancelAddProduct();
                    }
                }
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AddProductCallback) {
            mCallback = (AddProductCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement AddProductCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private boolean validateForm() {
        Editable text = mEdtProductName.getText();
        Context context = getActivity();
        if (text.length() == 0) {
            mEdtProductName.requestFocus();

            if (context != null) {
                Toast.makeText(context,
                        R.string.msg_add_product_name, Toast.LENGTH_SHORT).show();
            }
            return false;
        } else {
            text = mEdtProductDescription.getText();
            if (text.length() == 0) {
                mEdtProductDescription.requestFocus();
                if (context != null) {
                    Toast.makeText(context,
                            R.string.msg_add_product_description, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }
        return true;
    }
}
