package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.control.FragmentABCallback;
import es.dexusta.ticketcompra.control.ReceiptAdapter;
import es.dexusta.ticketcompra.model.Receipt;

public class ListReceiptsFragment extends ListFragment {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "ListReceiptsFragment";

    private ListReceiptsCallback mCalback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_receipts_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG) Log.d(TAG, "onAttach");
        if (activity instanceof ListReceiptsCallback) {
            mCalback = (ListReceiptsCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ListReceiptsFragment.ListReceiptsCallback");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mCalback.getReceiptAdapter());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCalback = null;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Receipt receipt = ((ReceiptAdapter) getListAdapter()).getItem(position);
        mCalback.onReceiptSelected(receipt);
    }

    interface ListReceiptsCallback extends FragmentABCallback {
        public void onReceiptSelected(Receipt receipt);

        public void onCancelReceiptSelection();

        public ReceiptAdapter getReceiptAdapter();
    }

}
