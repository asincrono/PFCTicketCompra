package es.dexusta.ticketcompra.control;

import android.view.View.OnClickListener;

/**
 * Created by asincrono on 16/04/14.
 */
public interface FragmentABCallback extends FragmentSoftKeyCallback {
    public void showAcceptCancelActionBar(OnClickListener onClickAccept, OnClickListener onClickCancel);
    public void hideAcceptCancelActionBar();
    public boolean isABAvaliable();
}
