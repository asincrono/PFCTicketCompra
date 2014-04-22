package es.dexusta.ticketcompra;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.model.Product;

public class ProductSelectionFragment extends ListFragment {    
    private ProductSelectionCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ProductSelectionCallback) {
            mCallback = (ProductSelectionCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ProductSelectionCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);        
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(mCallback.getProductAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        
        return inflater.inflate(R.layout.product_selec_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Product product = ((ProductAdapter)getListAdapter()).getItem(position);
        mCallback.onProductSelected(product, position);
    }
}
