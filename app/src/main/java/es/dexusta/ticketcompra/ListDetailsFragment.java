package es.dexusta.ticketcompra;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import es.dexusta.ticketcompra.control.ReceiptDetailListAdapter;
import es.dexusta.ticketcompra.model.Detail;

public class ListDetailsFragment extends ListFragment implements OnClickListener {
    private static final String  TAG   = "ListDetailFragment";
    private static final boolean DEBUG = true;

    private ListDetailsCallback  mListener;
    private List<Detail>         mDetails;
    private boolean              mPendingUpdate;

    private Button               mBttAddDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_details_fragment, container, false);
        
        mBttAddDetail = (Button) view.findViewById(R.id.btt_add_detail);
        if (mListener.isInsertionActive()) {           
            mBttAddDetail.setOnClickListener(this);
        } else {
            mBttAddDetail.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (DEBUG) Log.d(TAG, " onActivityCreated");

        View v = getActivity().getActionBar().getCustomView();
        FrameLayout flAccept = (FrameLayout) v.findViewById(R.id.actionbar_accept);
        FrameLayout flCancel = (FrameLayout) v.findViewById(R.id.actionbar_cancel);

        flAccept.setOnClickListener(new OnClickListener() { // Accept.

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), TAG + " accept pressed", Toast.LENGTH_SHORT).show();
                mListener.onListDetailsAccepted(mDetails);
            }

        });

        flCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), TAG + " cancel pressed", Toast.LENGTH_SHORT).show();
                mListener.onListDetailsCanceled();
            }
        });

    }

    public void setList(List<Detail> list) {
        mDetails = list;
        Activity activity = getActivity();
        if (activity != null) {
            ReceiptDetailListAdapter adapter = (ReceiptDetailListAdapter) getListAdapter();
            if (adapter != null) {
                adapter.sawpList(list);
            } else {
                setListAdapter(new ReceiptDetailListAdapter(activity, list));
            }
            mPendingUpdate = false;
        } else {
            mPendingUpdate = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ListDetailsCallback) {
            mListener = (ListDetailsCallback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ListDetailsFragment.ListDetailsCallback");
        }
        if (mPendingUpdate) {            
            setList(mDetails);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        mListener.onAddDetail();
    }

//    private void showAcceptCancelActionBar(OnClickListener onClickAccept,
//            OnClickListener onClickCancel) {
//        final ActionBar actionBar = getActivity().getActionBar();
//
//        LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());
//
//        final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null);
//
//        actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
//        actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);
//
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
//                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//        // Previous line is equivalent to:
//        // actionBar.setDisplayShowTitleEnabled(false);
//        // actionBar.setDisplayShowHomeEnabled(false);
//        // actionBar.setDisplayUseLogoEnabled(false);
//        // actionBar.setDisplayShowCustomEnabled(true);
//
//        actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//    }

    interface ListDetailsCallback {
        public boolean isInsertionActive();

        public void onAddDetail();

        public void onListDetailsAccepted(List<Detail> details);

        public void onListDetailsCanceled();
    }
}
