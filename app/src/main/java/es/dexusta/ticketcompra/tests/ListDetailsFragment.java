package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.control.FragmentABCallback;
import es.dexusta.ticketcompra.control.ReceiptDetailAdapter;

public class ListDetailsFragment extends ListFragment implements OnClickListener {
    private static final String  TAG   = "ListDetailFragment";
    private static final boolean DEBUG = true;

    private ListDetailsCallback mCallbacks;

    private Button mBttAddDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_details_fragment, container, false);

        mBttAddDetail = (Button) view.findViewById(R.id.btt_add_detail);
        if (mCallbacks.isInsertionActive()) {
            mBttAddDetail.setOnClickListener(this);
        } else {
            mBttAddDetail.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) Log.d(TAG, " onStart");

        mCallbacks.showAcceptCancelActionBar(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), TAG + " accept pressed", Toast.LENGTH_SHORT).show();
                        mCallbacks.onListDetailsAccepted();
                    }
                },
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), TAG + " cancel pressed", Toast.LENGTH_SHORT).show();
                        mCallbacks.onListDetailsCanceled();
                    }
                }
        );

//        ActionBar actionBar = getActivity().getActionBar();
//
//        OnClickListener onClickAccept = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), TAG + " accept pressed", Toast.LENGTH_SHORT).show();
//                mCallbacks.onListDetailsAccepted();
//            }
//        };
//
//        OnClickListener onClickCancel = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), TAG + " cancel pressed", Toast.LENGTH_SHORT).show();
//                mCallbacks.onListDetailsCanceled();
//            }
//        };
//
//        ActionBarController.setAcceptCancel(actionBar, onClickAccept, onClickCancel);

        setListAdapter(mCallbacks.getReceiptDetailListAdapter());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ListDetailsCallback) {
            mCallbacks = (ListDetailsCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ListDetailsFragment.ListDetailsCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onStop");
        mCallbacks.hideAcceptCancelActionBar();
    }

    @Override
    public void onClick(View v) {
        mCallbacks.onAddDetail();
    }

    public interface ListDetailsCallback extends FragmentABCallback {
        public boolean isInsertionActive();

        public void onAddDetail();

        public ReceiptDetailAdapter getReceiptDetailListAdapter();

        public void onListDetailsAccepted();

        public void onListDetailsCanceled();
    }
}
