package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.model.Product;

public class AddProductFragment extends Fragment {

    private EditText mEdtProductName;
    private EditText mEdtProductDescription;

    private long mSubcategoryId;

    private AddProductCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_fragment, container, false);

        mEdtProductName = (EditText) view.findViewById(R.id.edt_product_name);
        mEdtProductDescription = (EditText) view.findViewById(R.id.edt_description);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar ab = getActivity().getActionBar();

        if (BuildConfig.DEBUG && ab == null)
            throw new AssertionError("ActionBar shouldn't be null");

        ActionBarController.showAcceptCancelActionBar(ab,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

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
                        product.setSubcategoryId(mSubcategoryId);

                        mCallback.onAddProduct(product);
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
        // TODO: Check if the form is properly completed.
        return false;
    }
}
