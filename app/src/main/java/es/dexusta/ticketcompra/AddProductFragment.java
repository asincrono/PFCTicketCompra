package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public class AddProductFragment extends Fragment {
    private static final String TAG = "AddProductFragment";

    private EditText mEdtProductName;
    private EditText mEdtProductDescription;

    private TextView mTvCategory;
    private TextView mTvSubcategory;

    private Category mCategory;
    private Subcategory mSubcategory;

    private AddProductCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onAttach.");

        if (activity instanceof AddProductCallback) {
            mCallback = (AddProductCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement AddProductCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onDetach.");

        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_fragment, container, false);

        mEdtProductName = (EditText) view.findViewById(R.id.edt_product_name);
        mEdtProductName.requestFocus();

        mEdtProductDescription = (EditText) view.findViewById(R.id.edt_description);

        mTvCategory = (TextView) view.findViewById(R.id.tv_category);
        mTvSubcategory = (TextView) view.findViewById(R.id.tv_subcategory);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onResume.");

        mCategory = mCallback.getSelectedCategory();
        mSubcategory = mCallback.getSeletedSubcategory();

        mTvCategory.setText(mCategory.getName());
        mTvSubcategory.setText(mSubcategory.getName());

        mCallback.showAcceptCancelActionBar(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (validateForm()) {

                            Editable text = mEdtProductName.getText();

                            if (BuildConfig.DEBUG && text == null)
                                throw new AssertionError("product name text shouldn't be null");

                            String pName = text.toString();

                            text = mEdtProductDescription.getText();

                            if (BuildConfig.DEBUG && text == null)
                                throw new AssertionError("product name text shouldn't be null");

                            String pDescription = text.toString();

                            Product product = new Product();
                            product.setName(pName);
                            product.setDescription(pDescription);
                            product.setSubcategoryId(mSubcategory.getId());

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

    @Override
    public void onStop() {
        super.onStop();
        mCallback.hideSoftKeyboard(mEdtProductName);
    }
}
