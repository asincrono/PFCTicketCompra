package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class EndingDatePickerFragment extends DialogFragment implements OnDateSetListener {
    private SetEndingDateCallbacks mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (SetEndingDateCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SetEndingDateCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar endingDate = mListener.getEndingDate();
        if (endingDate == null) endingDate = Calendar.getInstance();
        return new DatePickerDialog(getActivity(), this, endingDate.get(Calendar.YEAR),
                endingDate.get(Calendar.MONTH), endingDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mListener.onSetEndingDate(cal);
    }

    public interface SetEndingDateCallbacks {
        public Calendar getEndingDate();

        public void onSetEndingDate(Calendar endingDate);
    }
}
