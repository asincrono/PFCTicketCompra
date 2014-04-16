package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import es.dexusta.ticketcompra.control.ChainAdapter;
import es.dexusta.ticketcompra.control.ChainSelectionCallback;
import es.dexusta.ticketcompra.model.Chain;

/**
 * Created by asincrono on 16/05/13.
 */
public class ChainSelectionFragment extends ListFragment {
    private static final String    TAG   = "ChainSelectionFragment";
    private static final boolean   DEBUG = true;

    private ChainSelectionCallback mCallback;

    public static ChainSelectionFragment newInstance() {
        if (DEBUG)
            Log.d(TAG, "newInstance");

        return new ChainSelectionFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach.");
        if (activity instanceof ChainSelectionCallback) {
            mCallback = (ChainSelectionCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet ChainSelectionCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chain_selec_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chain_selection_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), "Number of chains = " + getListAdapter().getCount(),
                Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Chain chain = ((ChainAdapter) getListAdapter()).getItem(position);
        mCallback.onChainSelected(chain);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (DEBUG)
            Log.d(TAG, "onViewCreated.");
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            Log.d(TAG, "onStart.");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume.");
        setListAdapter(mCallback.getChainAdapter());
    }
    
    
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.");
    }
    

}