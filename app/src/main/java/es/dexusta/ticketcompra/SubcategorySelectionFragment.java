package es.dexusta.ticketcompra;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import es.dexusta.ticketcompra.control.SubcategoryAdapter;
import es.dexusta.ticketcompra.control.SubcategorySelectionCallback;
import es.dexusta.ticketcompra.model.Subcategory;

public class SubcategorySelectionFragment extends ListFragment {
    private SubcategorySelectionCallback mCallback;

    // public static SubcategorySelectionFragment newInstance(SubcategoryAdapter
    // adapter) {
    // SubcategorySelectionFragment fragment = new
    // SubcategorySelectionFragment();
    // if (adapter != null) fragment.setListAdapter(adapter);
    // return fragment;
    // }

    public static SubcategorySelectionFragment newInstance(SubcategorySelectionCallback callback) {
        SubcategorySelectionFragment fragment = new SubcategorySelectionFragment();
        
        fragment.mCallback = callback;
        
        return fragment;
    }
    
    public static SubcategorySelectionFragment newInstance(List<Subcategory> list, SubcategorySelectionCallback callback) {
        SubcategorySelectionFragment fragment = new SubcategorySelectionFragment();
        
        fragment.setList(list);
        fragment.mCallback = callback;
        
        return fragment;
    }
    
    public void setList(List<Subcategory> list) {
        SubcategoryAdapter adapter = (SubcategoryAdapter) getListAdapter();
        
        if (adapter != null) {
            adapter.swapList(list);
        }
        else {
            setListAdapter(new SubcategoryAdapter(getActivity(), list));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subcategory_selec_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Subcategory subcategory = ((SubcategoryAdapter) getListAdapter()).getItem(position);
        mCallback.onSubcategorySelected(subcategory, position);
    }
    
    
}
