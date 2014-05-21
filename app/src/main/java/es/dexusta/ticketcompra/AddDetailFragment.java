package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import es.dexusta.ticketcompra.control.ActionBarController;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;

public class AddDetailFragment extends Fragment {
    private static final String  TAG   = "AddDetailFragment";
    private static final boolean DEBUG = true;

    private AddDetailCallback mCallbacks;

    private TextView mTvLblProductName;
    private EditText mEdtPrice;
    private EditText mEdtUnits;
    // second unit is the edit text for weight/volume (optional).
    private EditText mEdtSecondUnit;
    private Spinner  mSpnSecondUnit;

    private Product mProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_detail_fragment, container, false);

        mTvLblProductName = (TextView) view.findViewById(R.id.tv_lbl_product_name);
        mEdtPrice = (EditText) view.findViewById(R.id.edt_price);
        mEdtPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7),
                new DecimalFilter(2)});

        mEdtUnits = (EditText) view.findViewById(R.id.edt_units);
        // mEdtUnits.setFilters(new InputFilter[] {new
        // InputFilter.LengthFilter(3)});
        mEdtSecondUnit = (EditText) view.findViewById(R.id.edt_second_unit);
        mSpnSecondUnit = (Spinner) view.findViewById(R.id.spn_second_unit);

        mSpnSecondUnit.setAdapter(new UnitsAdapter(getActivity()));

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AddDetailCallback) {
            mCallbacks = (AddDetailCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement AddDetailFragment.AddDetailCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProduct = mCallbacks.getSelectedProduct();
        mTvLblProductName.setText(mProduct.getName());

        if (DEBUG) Log.d(TAG, " onActivityCreated");


        Activity activity = getActivity();

        if (BuildConfig.DEBUG && activity == null)
            throw new AssertionError("Activity can't be null at this point");

        ActionBar actionBar = activity.getActionBar();

        if (BuildConfig.DEBUG && actionBar == null) {
            throw new AssertionError("Action bar shouldn't be null");
        }

        View.OnClickListener onClickAccept = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Detail detail = new Detail();
                detail.setPrice(eurosToCents(mEdtPrice.getText().toString()));
                String secondUnitStr = mEdtSecondUnit.getText().toString();
                if (secondUnitStr.length() > 0) {
                    if (mSpnSecondUnit.getSelectedItemId() == 0) {
                        detail.setWeight(Integer.parseInt(secondUnitStr));
                    } else {
                        detail.setVolume(Integer.parseInt(secondUnitStr));
                    }
                }
                detail.setProductId(mProduct.getId());
                detail.setProductUnivId(mProduct.getUniversalId());
                detail.setProductName(mProduct.getName());


                clear();

                mCallbacks.onDetailAdded(detail);
            }
        };

        View.OnClickListener onClickCancel = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallbacks.onAddDetailCanceled();
            }
        };

        ActionBarController.setAcceptCancel(actionBar, onClickAccept, onClickCancel);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void clear() {
        mEdtPrice.setText("");
        mEdtUnits.setText("");
        mEdtSecondUnit.setText("");
        mSpnSecondUnit.setSelection(0);

    }

    private int eurosToCents(String priceStr) {
        int result = 0;
        if (priceStr.length() > 0) {
            int decimalPointPos = priceStr.indexOf('.');
            if (decimalPointPos >= 0) {
                String strEuros = priceStr.substring(0, decimalPointPos);
                String strCents = priceStr.substring(decimalPointPos + 1, priceStr.length());

                if (decimalPointPos > 0 && strEuros.length() > 0) {
                    // number does not start by "."
                    result = Integer.parseInt(strEuros) * 100;
                }

                if (strCents.length() > 0) {
                    // there are decimal ciphers
                    result += (strCents.length() == 1) ? Integer.parseInt(strCents) * 10 : Integer
                            .parseInt(strCents);
                }
            } else { // No decimal poing (and str. lenght > 0.
                result = Integer.parseInt(priceStr) * 100;
            }
        }
        return result;
    }

    interface AddDetailCallback {
        public void onDetailAdded(Detail detail);

        public void onAddDetailCanceled();

        public Product getSelectedProduct();
    }

    class UnitsAdapter extends BaseAdapter implements SpinnerAdapter {
        private String[] mArray;

        private LayoutInflater mInflater;

        public UnitsAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mArray = context.getResources().getStringArray(R.array.chose_weight_volume);
        }

        @Override
        public int getCount() {
            return mArray.length;
        }

        @Override
        public String getItem(int position) {
            if (position >= 0 && position < mArray.length) {
                return mArray[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent, false);
        }

        private View getView(int position, View convertView, ViewGroup parent, boolean isDrowpDown) {

            int resourceId = (isDrowpDown) ? android.R.layout.simple_spinner_dropdown_item
                    : android.R.layout.simple_spinner_item;

            View view = convertView;

            if (view == null) {
                view = mInflater.inflate(resourceId, parent, false);
            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent, true);
        }

    }

    class DecimalFilter implements InputFilter {
        private int    mDecimals;
        private String mRegex;

        public DecimalFilter(int decimals) {
            mDecimals = decimals;
        }

        /*
         * Regex for "x...x[.]xx" being x=0,1,...9: "\d*(\.\d?\d?)?"
         */

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {

            String regex = String.format("\\d*(\\.\\d{0,%d})?", mDecimals);

            // if the CharSequene to be inserter does not fit the regex we need
            // to reject it.
            if (source.toString().matches(regex)) {
                // now we check what would be the result.
                StringBuilder result = new StringBuilder(dest);
                result.replace(dstart, dend, source.subSequence(start, end).toString());
                if (result.toString().matches(regex))
                    return null;
            }

            return "";
        }
    }
}
