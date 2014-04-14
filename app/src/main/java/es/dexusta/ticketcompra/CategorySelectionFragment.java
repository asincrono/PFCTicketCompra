package es.dexusta.ticketcompra;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import es.dexusta.ticketcompra.control.CategoryAdapter;
import es.dexusta.ticketcompra.control.CategorySelectionCallback;
import es.dexusta.ticketcompra.model.Category;

public class CategorySelectionFragment extends ListFragment {
    private static final String TAG = "CategorySelectinFragment";
    private static final boolean DEBUG = true;
   
    private CategorySelectionCallback mCallback;
    
//    public static CategorySelectionFragment newInstance(CategoryAdapter adapter) {
//        CategorySelectionFragment fragment = new CategorySelectionFragment();
//        if (adapter != null) {
//            fragment.setListAdapter(adapter);
//        }
//        return fragment;
//    }

    public static CategorySelectionFragment newInstance(CategorySelectionCallback callback) {        
        CategorySelectionFragment fragment = new CategorySelectionFragment();
        fragment.mCallback = callback;
        return fragment;
    }
    public static CategorySelectionFragment newInstance(List<Category> list, CategorySelectionCallback callback) {        
        CategorySelectionFragment fragment = new CategorySelectionFragment(); 
        fragment.setList(list);
        fragment.mCallback = callback;
        return fragment;
    }
    
    public void setList(List<Category> list) {    
        CategoryAdapter adapter = (CategoryAdapter) getListAdapter();
        if (adapter != null) {
            adapter.swapList(list);
        }
        else {
            FragmentActivity fragAct = getActivity();
            if (fragAct != null) {
                setListAdapter(new CategoryAdapter(getActivity(), list));
            }
        }
    }
    
    public void setPostion(int position) {
        getListView().setSelectionFromTop(position, 0);
//        getListView().onSaveInstanceState();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        if (DEBUG) Log.d(TAG, "onCreate");
    }
    
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
////        ((ShopSelectionActivity)getActivity()).retrieveChainList();
//        setListAdapter(((SelectProductActivity)getActivity()).getCategoryAdapter());
//    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_selec_fragment, container, false);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Category category = (Category)getListAdapter().getItem(position);
        mCallback.onCategorySelected(category, position);
    }

}
