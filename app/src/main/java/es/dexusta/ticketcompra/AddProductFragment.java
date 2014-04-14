package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import es.dexusta.ticketcompra.control.AddProductCallback;
import es.dexusta.ticketcompra.model.Product;

public class AddProductFragment extends Fragment {
    private TextView mTvCategoryLbl;
    private TextView mTvSubcategoryLbl;
    private EditText mEdtProductName;
    private EditText mEdtProductDescription;
    
    private long mSubcategoryId;

    private AddProductCallback mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AddProductCallback) {
            mCallbacks = (AddProductCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement AddProductCallbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_fragment, container, false);

        mTvCategoryLbl = (TextView) view.findViewById(R.id.tv_category);
        mTvSubcategoryLbl = (TextView) view.findViewById(R.id.tv_subcategory);
        
        mEdtProductName = (EditText) view.findViewById(R.id.edt_product_name);
        
        return view;
    }
    
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {        
        super.onActivityCreated(savedInstanceState);
        
        ActionBar ab = getActivity().getActionBar();
        
        View v = ab.getCustomView();
        
        FrameLayout abFLAccept = (FrameLayout) v.findViewById(R.id.actionbar_accept);
        FrameLayout abFLCancel = (FrameLayout) v.findViewById(R.id.actionbar_cancel);
        
        abFLAccept.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String pName = mEdtProductName.getText().toString();
                String pDescription = mEdtProductDescription.getText().toString();
                
                Product product = new Product();
                product.setName(pName);
                product.setDescription(pDescription);
                product.setSubcategoryId(mSubcategoryId);
                
            }
        });
        
        abFLCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}
