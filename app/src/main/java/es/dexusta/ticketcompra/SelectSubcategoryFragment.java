package es.dexusta.ticketcompra;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SelectSubcategoryFragment extends Fragment {
    private Button mBttNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_subcategory_fragment, container, false);
        mBttNext = (Button) view.findViewById(R.id.btt_next);
        return view;
    }

}
