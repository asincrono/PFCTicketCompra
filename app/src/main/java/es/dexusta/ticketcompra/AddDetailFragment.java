package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;

public class AddDetailFragment extends Fragment {
    private static final String  TAG   = "AddDetailFragment";
    private static final boolean DEBUG = true;

    private AddDetailCallback    mCallbacks;

    private TextView             mTvLblProductName;
    private EditText             mEdtPrice;
    private EditText             mEdtUnits;
    // second unit is the edit text for weight/volume (optional).
    private EditText             mEdtSecondUnit;
    private Spinner              mSpnSecondUnit;

    private Product              mProduct;

    public void setProduct(Product product) {
        mProduct = product;
        if (mTvLblProductName != null) {
            mTvLblProductName.setText(product.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_detail_fragment, container, false);

        mTvLblProductName = (TextView) view.findViewById(R.id.tv_lbl_product_name);
        mEdtPrice = (EditText) view.findViewById(R.id.edt_price);
        mEdtPrice.setFilters(new InputFilter[] { new InputFilter.LengthFilter(7),
                new DecimalFilter(2) });

        mEdtUnits = (EditText) view.findViewById(R.id.edt_units);
        // mEdtUnits.setFilters(new InputFilter[] {new
        // InputFilter.LengthFilter(3)});
        mEdtSecondUnit = (EditText) view.findViewById(R.id.edt_second_unit);
        mSpnSecondUnit = (Spinner) view.findViewById(R.id.spn_second_unit);

        mSpnSecondUnit.setAdapter(new UnitsAdapter(getActivity()));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar ab = getActivity().getActionBar();
        
        if (DEBUG) Log.d(TAG, " onActivityCreated");
        
        View v = ab.getCustomView();
        FrameLayout  abFlAccept = (FrameLayout) v.findViewById(R.id.actionbar_accept);
        FrameLayout  abFlCancel = (FrameLayout) v.findViewById(R.id.actionbar_cancel);

        abFlAccept.setOnClickListener(new View.OnClickListener() {

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
                detail.setProductName(mProduct.getName());

                clear();

                mCallbacks.onDetailAdded(detail);
            }
        });
        
        abFlCancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mCallbacks.onAddDetailCanceled();
            }
        });
        //        showAcceptCancelActionBar(new OnClickListener() { // Accept.
        //
        //            @Override
        //            public void onClick(View v) {
        //                Detail detail = new Detail();
        //                detail.setPrice(eurosToCents(mEdtPrice.getText().toString()));
        //                String secondUnitStr = mEdtSecondUnit.getText().toString();
        //                if (secondUnitStr.length() > 0) {
        //                    if (mSpnSecondUnit.getSelectedItemId() == 0) {
        //                        detail.setWeight(Integer.parseInt(secondUnitStr));
        //                    } else {
        //                        detail.setVolume(Integer.parseInt(secondUnitStr));
        //                    }
        //                }
        //                detail.setProductId(mProduct.getId());
        //                detail.setProductName(mProduct.getName());
        //                
        //                clear();
        //                
        //                mListener.onDetailAdded(detail);               
        //            }
        //        }, new OnClickListener() { // Cancel.
        //
        //            @Override
        //            public void onClick(View v) {
        //                clear();
        //            }
        //        }); 
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

    // private void showAcceptCancelActionBar(OnClickListener onClickAccept,
    // OnClickListener onClickCancel) {
    // final ActionBar actionBar = getActivity().getActionBar();
    //
    // LayoutInflater inflater =
    // LayoutInflater.from(actionBar.getThemedContext());
    //
    // final View actionBarCustomView =
    // inflater.inflate(R.layout.actionbar_cancel_accept, null);
    //
    // actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
    // actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);
    //
    // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
    // ActionBar.DISPLAY_SHOW_CUSTOM
    // | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    // // Previous line is equivalent to:
    // // actionBar.setDisplayShowTitleEnabled(false);
    // // actionBar.setDisplayShowHomeEnabled(false);
    // // actionBar.setDisplayUseLogoEnabled(false);
    // // actionBar.setDisplayShowCustomEnabled(true);
    //
    // actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
    // ViewGroup.LayoutParams.MATCH_PARENT,
    // ViewGroup.LayoutParams.MATCH_PARENT));
    // }

    interface AddDetailCallback {
        public void onDetailAdded(Detail detail);
        public void onAddDetailCanceled();
    }

    class UnitsAdapter extends BaseAdapter implements SpinnerAdapter {
        private String[]       mArray;

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
