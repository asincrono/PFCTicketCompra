package es.dexusta.ticketcompra;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import es.dexusta.ticketcompra.control.ProductAdapter;
import es.dexusta.ticketcompra.control.ProductSelectionCallback;
import es.dexusta.ticketcompra.model.Product;

public class ProductSelectionFragment extends ListFragment {    
    private ProductSelectionCallback mCallback;
    
    public static ProductSelectionFragment newInstance(ProductSelectionCallback callback) {
        ProductSelectionFragment fragment = new ProductSelectionFragment();
        
        fragment.mCallback = callback;
        
        return fragment;
    }
    
    public static ProductSelectionFragment newInstance(List<Product> list, ProductSelectionCallback callback) {
        ProductSelectionFragment fragment = new ProductSelectionFragment();
        
        fragment.setList(list);
        fragment.mCallback = callback;
        
        return fragment;
    }
    
    public void setList(List<Product> list) {
        ProductAdapter adapter = (ProductAdapter) getListAdapter();
        
        if (adapter != null) {
            adapter.swapList(list);
        }
        else {
            setListAdapter(new ProductAdapter(getActivity(), list));
        }
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
