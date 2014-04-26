package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SubcategorySelectionCallback) {
            mCallback = (SubcategorySelectionCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement SubcategorySelectionCallback");
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
        setListAdapter(mCallback.getSubcategoryAdapter());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subcategory_selec_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
//        if (lv != null) {
//            lv.setSelection(mCallback.getSelectedSubcategoryPostion());
//        }
        setSelection(mCallback.getSelectedSubcategoryPostion());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Subcategory subcategory = ((SubcategoryAdapter) getListAdapter()).getItem(position);
        mCallback.onSubcategorySelected(subcategory, position);
    }
    
    
}
