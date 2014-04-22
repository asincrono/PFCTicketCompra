package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        if (DEBUG) Log.d(TAG, "onCreate");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CategorySelectionCallback) {
            mCallback = (CategorySelectionCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement CategorySelectionCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(mCallback.getCategoryAdapter());
    }

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSelection(mCallback.getSelectedCategoryPosition());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Category category = (Category)getListAdapter().getItem(position);
        mCallback.onCategorySelected(category, position);
    }

}
