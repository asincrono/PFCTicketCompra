package es.dexusta.ticketcompra.control;

import android.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import es.dexusta.ticketcompra.R;

/**
 * Created by asincrono on 20/04/14.
 */
public class ActionBarController {

    public static void setDisplayDefault(ActionBar actionBar) {
        int display = ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE;
        int mask = ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM;

        actionBar.setDisplayOptions(display, mask);
    }

    public static void showCustom(ActionBar actionBar, int abLayout) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(abLayout);
    }

    public static void showMenu(Menu menu, boolean show) {
        int numItems = menu.size();
        for (int pos = 0; pos < numItems; pos += 1) {
            menu.getItem(pos).setVisible(show);
        }
    }

    public static void setAcceptCancel(ActionBar actionBar, OnClickListener onClickAccept,
                                       OnClickListener onClickCancel) {
        ActionBarController.showCustom(actionBar, R.layout.actionbar_cancel_accept);

        View actionBarCustomView = actionBar.getCustomView();
        actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
        actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);
    }

    public interface ActionBarHolder {
        public void showAcceptCancelAB(OnClickListener onClickAccept,
                                       OnClickListener onClickCancel);

        public void showClassicAB();

        public boolean isABAvaliable();
    }
}
