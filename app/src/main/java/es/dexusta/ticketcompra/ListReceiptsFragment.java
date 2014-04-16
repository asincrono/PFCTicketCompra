package es.dexusta.ticketcompra;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import es.dexusta.ticketcompra.control.ReceiptAdapter;
import es.dexusta.ticketcompra.model.Receipt;

public class ListReceiptsFragment extends ListFragment {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "ListReceiptsFragment";

    private ListReceiptsCallback mListener;

    private List<Receipt>        mList;

    public static ListReceiptsFragment newInstance(List<Receipt> list) {
        if (DEBUG) Log.d(TAG, "newInstance");
        ListReceiptsFragment fragment = new ListReceiptsFragment();
        fragment.mList = list;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_receipts_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG) Log.d(TAG, "onAttach");
        if (activity instanceof ListReceiptsCallback) {
            mListener = (ListReceiptsCallback) activity;
            setList(mList);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ListReceiptsFragment.ListReceiptsCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setList(List<Receipt> list) {        

        ReceiptAdapter adapter = (ReceiptAdapter) getListAdapter();
        if (adapter != null) {
            adapter.swapList(list);
        } else {
            setListAdapter(new ReceiptAdapter(getActivity(), list));
        }
    }    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Receipt receipt = ((ReceiptAdapter) getListAdapter()).getItem(position);
        mListener.onReceiptSelected(receipt);
    }

    interface ListReceiptsCallback {
        public void onReceiptSelected(Receipt receipt);

        public void onCancelReceiptSelection();
    }

}
